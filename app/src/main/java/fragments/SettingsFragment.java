package fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.noxnotes.MainActivity;
import com.example.noxnotes.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;
import org.json.JSONException;
import org.json.JSONObject;
import app.ROUTER;
import app.*;
import cz.msebera.android.httpclient.Header;
import objects.LoginObject;
import objects.SettingsObject;

public class SettingsFragment extends Fragment implements  SeekBar.OnSeekBarChangeListener , View.OnClickListener , AdapterView.OnItemSelectedListener {


    AppCompatSpinner fonts ;
    AppCompatCheckBox nightMode ;
    AppCompatSeekBar fontSize ;
    TextView sampleText;
    HSLColorPicker colorPicker;

    public static int colorValue;
    public static int nightValue ;
    public static String fontValue;

    RelativeLayout parent;

    TextView fontSizeText ;
    public static Typeface font ;



    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return init(view);
    }

    private View init(View view){


        fonts = view.findViewById(R.id.fonts);
        nightMode = view.findViewById(R.id.nightMode);
        fontSize = view.findViewById(R.id.fontSize);
        sampleText = view.findViewById(R.id.sampleText);
        colorPicker = view.findViewById(R.id.colorPicker);
        parent = view.findViewById(R.id.parent);
        fontSizeText = view.findViewById(R.id.fontSizeText);


        fonts.setOnItemSelectedListener(this);
        nightMode.setOnClickListener(this);
        fontSize.setOnSeekBarChangeListener(this);



        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {

                spref.getOurInstance().edit().putInt(ROUTER.TEXT_COLOR, color).apply();
                sampleText.setTextColor(color);

            }
        });

        restoreData();
        getData();

        return view ;
    }

    private void restoreData() {


        fontValue = spref.getOurInstance().getString(ROUTER.FONT,"roboto.ttf");
        for(int i=0;i<7;i++)
            if(fontValue.equals(fonts.getItemAtPosition(i).toString())){
                fonts.setSelection(i);
                break;
            }



        nightValue = spref.getOurInstance().getInt(ROUTER.NIGHT_MODE ,0) ;
        if (nightValue == 0){
            nightMode.setChecked(false);
            parent.setBackgroundColor(getResources().getColor(R.color.ColorIcons));
            nightMode.setTextColor(getResources().getColor(R.color.darkBackground));
            fontSizeText.setTextColor(getResources().getColor(R.color.darkBackground));
        }else{
            nightMode.setChecked(true);
            parent.setBackgroundColor(getResources().getColor(R.color.darkBackground));
            nightMode.setTextColor(getResources().getColor(R.color.ColorIcons));
            fontSizeText.setTextColor(getResources().getColor(R.color.ColorIcons));
        }

        fontSize.setProgress(spref.getOurInstance().getInt(ROUTER.FONT_SIZE , 14));
        sampleText.setTextSize(spref.getOurInstance().getInt(ROUTER.FONT_SIZE , 14));

        colorValue = spref.getOurInstance().getInt(ROUTER.TEXT_COLOR , Color.BLACK);
        colorPicker.setColor(colorValue);
        sampleText.setTextColor(colorValue);


    }

    private void getData() {

        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_SETTINGS);
        params.put(ROUTER.ACTION , ROUTER.ACTION_SET);
        params.put(ROUTER.NIGHT_MODE , nightValue);
        params.put(ROUTER.FONT , fontValue);
        params.put(ROUTER.TEXT_COLOR ,colorValue);
        params.put(ROUTER.FONT_SIZE , fontSize.getProgress());

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

                        }
                        else {

                            app.t("Something Went Wrong" , ToastType.ERROR);
                        }


                    }

                }catch (JSONException e){

                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {

        if (v == nightMode){
            nightValue = nightMode.isChecked()?1:0;

            spref.getOurInstance().edit().putInt(ROUTER.NIGHT_MODE , nightValue).apply();

            if (nightValue == 1){
                parent.setBackgroundColor(getResources().getColor(R.color.darkBackground));
                MainActivity.parent.setBackgroundColor(getResources().getColor(R.color.darkBackground));
                nightMode.setTextColor(getResources().getColor(R.color.ColorIcons));
                View view = fonts.getSelectedView();
                ((TextView)view).setTextColor(getResources().getColor(R.color.ColorIcons));
                fontSizeText.setTextColor(getResources().getColor(R.color.ColorIcons));
            }else {
                parent.setBackgroundColor(getResources().getColor(R.color.ColorIcons));
                MainActivity.parent.setBackgroundColor(getResources().getColor(R.color.ColorIcons));
                nightMode.setTextColor(getResources().getColor(R.color.darkBackground));
                View view = fonts.getSelectedView();
                ((TextView)view).setTextColor(getResources().getColor(R.color.darkBackground));
                fontSizeText.setTextColor(getResources().getColor(R.color.darkBackground));
            }

        }



    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        spref.getOurInstance().edit().putString(ROUTER.FONT,fonts.getSelectedItem().toString()).apply();

        try {
            if (nightValue == 1){
                ((TextView) view).setTextColor(getResources().getColor(R.color.ColorIcons));
            }

            font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/"+spref.getOurInstance().getString(ROUTER.FONT , "roboto.ttf"));
            sampleText.setTypeface(font);


        }catch (Exception e){
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        sampleText.setTextSize(progress);
        spref.getOurInstance().edit().putInt(ROUTER.FONT_SIZE , progress).apply();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {



    }
}
