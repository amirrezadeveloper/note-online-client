package com.example.noxnotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import interfaces.NoteStateChangeListener;
import objects.LoginObject;
import objects.NoteObject;
import app.*;

public class ShowNoteActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatImageView back , icon , state , delete ;
    AppCompatTextView title , message ;
    FloatingActionButton edit ;
    SpinKitView progressBar ;

    NoteObject noteObject ;
    LinearLayout linear ;

    public static int nightMode = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);
        initViews();
        init();

    }



    private void initViews() {

        noteObject = (NoteObject) getIntent().getSerializableExtra("object");

        back        = findViewById(R.id.back);
        icon        = findViewById(R.id.icon);
        state       = findViewById(R.id.state);
        delete      = findViewById(R.id.delete);
        title       = findViewById(R.id.title);
        message     = findViewById(R.id.message);
        edit        = findViewById(R.id.edit);
        progressBar = findViewById(R.id.progressBar);
        linear      = findViewById(R.id.linear);

        title.setTypeface(MainActivity.font);
        message.setTypeface(MainActivity.font);
        title.setTextColor(spref.getOurInstance().getInt(ROUTER.TEXT_COLOR , 1));
        message.setTextColor(spref.getOurInstance().getInt(ROUTER.TEXT_COLOR , 1));
        title.setTextSize(spref.getOurInstance().getInt(ROUTER.FONT_SIZE , 12));
        message.setTextSize(spref.getOurInstance().getInt(ROUTER.FONT_SIZE , 12));

        nightMode = spref.getOurInstance().getInt(ROUTER.NIGHT_MODE , 0);

        if (nightMode == 1){
            linear.setBackgroundColor(getResources().getColor(R.color.darkBackground));
            //title.setTextColor(getResources().getColor(R.color.ColorIcons));
            //message.setTextColor(getResources().getColor(R.color.ColorIcons));
        }else {
            linear.setBackgroundColor(getResources().getColor(R.color.ColorIcons));
            //title.setTextColor(getResources().getColor(R.color.darkBackground));
           // message.setTextColor(getResources().getColor(R.color.darkBackground));
        }


        back.setOnClickListener(this);
        state.setOnClickListener(this);
        delete.setOnClickListener(this);
        edit.setOnClickListener(this);
    }

    private void init(){
        title.setText(noteObject.getTitle());
        message.setText(noteObject.getMessage());
        icon.setImageDrawable(app.getImage(noteObject));
        state.setImageResource(
                noteObject.getSeen()==1?R.drawable.ic_checked:R.drawable.ic_circle
        );
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.back:{
                finish();
                break;
            }
            case R.id.state:{
                changeState();
                break;
            }
            case R.id.delete:{
                startDelete();
                break;
            }
            case R.id.edit:{
                Intent intent = new Intent(this , EditNoteActivity.class);
                intent.putExtra("object" , noteObject);
                startActivity(intent);
                break;
            }

        }
    }

    private void changeState(){

      app.changeNoteState(0 , noteObject, new NoteStateChangeListener() {
          @Override
          public void onChange(int position, int noteID, int state , Boolean success) {

              if (success){

                  noteObject.setSeen(state);
                  NoteBroadCast.send(position , NoteBroadCast.ACTION_EDIT , noteObject);
                  ShowNoteActivity.this.state.setImageResource(
                          noteObject.getSeen()==1?R.drawable.ic_checked:R.drawable.ic_circle
                  );
              }

          }

          @Override
          public void onStart() {
              progressBar.setVisibility(View.VISIBLE);
          }

          @Override
          public void onFinish() {
              progressBar.setVisibility(View.INVISIBLE);
          }


      });
    }

    private void startDelete(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Note");
        alert.setMessage( "Are you sure you want to delete the note?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                delete();
            }
        });

        alert.setNegativeButton("No", null);
        alert.show();

    }

    private void delete(){

        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_NOTES);
        params.put(ROUTER.ACTION , ROUTER.ACTION_DELETE);
        params.put(ROUTER.INPUT_NOTE_ID , noteObject.getId());

        NoteClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);


                if (response.equals(ROUTER.SUCCESS)){

                    NoteBroadCast.send(NoteBroadCast.ACTION_DELETE , noteObject);
                    finish();
                }

                else
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("error")){
                        app.t(jsonObject.getString("error") , ToastType.ERROR);
                    }


                }catch (JSONException e){

                    app.t("Something Went Wrong" , ToastType.ERROR);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
                super.onStart();
            }

            @Override
            public void onFinish() {
                progressBar.setVisibility(View.INVISIBLE);
                super.onFinish();
            }
        });


    }
}