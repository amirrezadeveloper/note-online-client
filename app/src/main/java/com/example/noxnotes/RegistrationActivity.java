 package com.example.noxnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import app.*;
import cz.msebera.android.httpclient.Header;
import objects.LoginObject;

 public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatEditText  email , password , rePassword , fName , lName ;
    RadioGroup sex ;
    AppCompatTextView submit , login ;
    SpinKitView progressBar ;
    int sexInt = 1 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();
    }

    private void init() {

        email      = findViewById(R.id.email);
        password   = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);
        fName      = findViewById(R.id.fName);
        lName      = findViewById(R.id.lName);

        sex = findViewById(R.id.sex);

        submit = findViewById(R.id.submit);
        login  = findViewById(R.id.login);
        progressBar  = findViewById(R.id.progressBar);

        submit.setOnClickListener(this);
        login.setOnClickListener(this);
    }

     @Override
     public void onClick(View v) {

        if (v == submit){
            submit();
        }else {
            finish();
        }
     }

     private void submit() {


        if (email.getText().toString().length()<5 ){
           app.t("Please enter your correct email" , ToastType.ERROR);
           app.animateError(email);
           return;
        }

         if (password.getText().toString().length()<3){
             app.t("Your password is too short Enter a more secure password" , ToastType.ERROR);
             app.animateError(password);
             return;
         }
         if (!password.getText().toString().equals(rePassword.getText().toString())){
             app.t("The passwords entered do not match" , ToastType.ERROR);
             app.animateError(password);
             app.animateError(rePassword);
             return;
         }

        if (fName.getText().toString().length()<3){
            app.t("Please enter your correct first name" , ToastType.ERROR);
            app.animateError(fName);
            return;
        }
        if (lName.getText().toString().length()<3){
            app.t("Please enter your correct last name" , ToastType.ERROR);
            app.animateError(lName);
            return;
        }




        if (sex.getCheckedRadioButtonId() == R.id.female) sexInt = 0 ;
       else if (sex.getCheckedRadioButtonId() == R.id.other) sexInt = 2 ;

         RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_LOGIN);
        params.put(ROUTER.ACTION , ROUTER.ACTION_REGISTRATION);
        params.put(ROUTER.INPUT_EMAIL , email.getText().toString());
        params.put(ROUTER.INPUT_PASSWORD , password.getText().toString());
        params.put(ROUTER.INPUT_FNAME , fName.getText().toString());
        params.put(ROUTER.INPUT_LNAME , lName.getText().toString());
        params.put(ROUTER.INPUT_SEX , sexInt+"");

        NoteClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("error")){
                        app.t(jsonObject.getString("error") , ToastType.ERROR);
                    }
                    else {
                        LoginObject loginObject = new Gson().fromJson(response , LoginObject.class);

                        if (loginObject.getState().equals(ROUTER.SUCCESS)){

                            String name = fName.getText().toString() + " " +lName.getText().toString() ;
                            app.t( "Welcome " + name, ToastType.SUCCESS);

                            spref.getOurInstance().edit()
                                    .putString(ROUTER.SESSION     , loginObject.getSession())
                                    .putString(ROUTER.INPUT_EMAIL     , email.getText().toString())
                                    .putInt(ROUTER.USER_ID        , loginObject.getId())
                                    .putString(ROUTER.INPUT_FNAME , fName.getText().toString())
                                    .putString(ROUTER.INPUT_LNAME , lName.getText().toString())
                                    .putInt(ROUTER.INPUT_SEX      , sexInt).apply();

                            spref.getOurInstance().edit()
                                    .putInt("nightMode", loginObject.getSettingsObject().getNightMode())
                                    .putString("font", loginObject.getSettingsObject().getFont())
                                    .putInt("bgColor", loginObject.getSettingsObject().getBgColor())
                                    .putInt("textColor", loginObject.getSettingsObject().getTextColor())
                                    .putInt("fontSize", loginObject.getSettingsObject().getFontSize())
                                    .putInt("avatarID", loginObject.getSettingsObject().getAvatarObject().getId())
                                    .putString("name", loginObject.getSettingsObject().getAvatarObject().getName())
                                    .putString("image", loginObject.getSettingsObject().getAvatarObject().getImage())
                                    .apply();


                            Intent intent = new Intent(RegistrationActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        else {

                            app.t("Something Went Wrong" , ToastType.ERROR);
                        }
                    }

                }catch (JSONException e){

                    app.t("Something Went Wrong" , ToastType.ERROR);
                   // app.l(e.toString());
                }

                app.l("response : " + response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                app.l("onFailure");
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