package com.example.noxnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import app.ROUTER;
import app.spref;
import app.*;
import cz.msebera.android.httpclient.Header;
import objects.NoteObject;

public class AddNoteActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatImageView back , save ;
    AppCompatEditText title , message ;

    SpinKitView progressBar ;
    LinearLayout linear;
    public static int nightMode = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        init() ;
    }

    private void init() {

        back = findViewById(R.id.back);
        save = findViewById(R.id.save);
        title = findViewById(R.id.title);
        message = findViewById(R.id.message);
        progressBar = findViewById(R.id.progressBar);
        linear = findViewById(R.id.linear);

        nightMode = spref.getOurInstance().getInt(ROUTER.NIGHT_MODE , 0);

        if (nightMode == 1){
            linear.setBackgroundColor(getResources().getColor(R.color.darkBackground));
            title.setTextColor(getResources().getColor(R.color.ColorIcons));
            title.setHintTextColor(getResources().getColor(R.color.colorPrimaryLight));
            message.setTextColor(getResources().getColor(R.color.ColorIcons));
            message.setHintTextColor(getResources().getColor(R.color.colorPrimaryLight));
        }else {
            linear.setBackgroundColor(getResources().getColor(R.color.ColorIcons));
            title.setTextColor(getResources().getColor(R.color.darkBackground));
            title.setHintTextColor(getResources().getColor(R.color.ColorSecondaryText));
            message.setTextColor(getResources().getColor(R.color.darkBackground));
            message.setHintTextColor(getResources().getColor(R.color.ColorSecondaryText));
        }


        title.setText(spref.getOurInstance().getString(ROUTER.INPUT_TITLE , ""));
        message.setText(spref.getOurInstance().getString(ROUTER.INPUT_MESSAGE , ""));
        title.setTypeface(MainActivity.font);
        message.setTypeface(MainActivity.font);
        title.setTextColor(spref.getOurInstance().getInt(ROUTER.TEXT_COLOR , Color.BLACK));
        message.setTextColor(spref.getOurInstance().getInt(ROUTER.TEXT_COLOR , Color.BLACK));
        title.setTextSize(spref.getOurInstance().getInt(ROUTER.FONT_SIZE , 14));
        message.setTextSize(spref.getOurInstance().getInt(ROUTER.FONT_SIZE , 14));

        back.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v == back) {
            spref.getOurInstance().edit()
                    .putString(ROUTER.INPUT_TITLE    , title.getText().toString())
                    .putString(ROUTER.INPUT_MESSAGE , message.getText().toString())
                    .apply();
            finish();
        }
        else {
            saveNote();
        }
    }

    private void saveNote() {

        if (title.getText().toString().length() == 0){
            app.t("Please enter a title" , ToastType.ERROR);
            app.animateError(title);
            return;
        }
        if (message.getText().toString().length() == 0){
            app.t("Please enter a note" , ToastType.ERROR);
            app.animateError(message);
            return;
        }


        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE  , ROUTER.ROUTE_NOTES);
        params.put(ROUTER.ACTION , ROUTER.ACTION_ADD);
        params.put(ROUTER.INPUT_TITLE , title.getText().toString());
        params.put(ROUTER.INPUT_MESSAGE , message.getText().toString());

        NoteClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);

                try {
                    JSONObject object = new JSONObject(response);

                    if(object.has("error")) {
                        app.t(object.getString("error") , ToastType.ERROR);
                    }
                    else if(object.has("status") && object.getString("status").equals("SUCCESS")){
                        int id = object.getInt("id");
                        spref.getOurInstance().edit().remove(ROUTER.INPUT_TITLE).remove(ROUTER.INPUT_MESSAGE).apply();
                        NoteObject noteObject = new NoteObject(id ,
                                spref.getOurInstance().getInt(ROUTER.USER_ID , -1) ,
                                title.getText().toString() , message.getText().toString() , 0 , "");
                        NoteBroadCast.send(NoteBroadCast.ACTION_ADD , noteObject);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
