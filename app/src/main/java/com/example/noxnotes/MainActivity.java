package com.example.noxnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import fragments.NoteFragment;
import fragments.ProfileFragment;
import fragments.SettingsFragment;
import app.*;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigation ;

    SettingsFragment settingsFragment = new SettingsFragment();
    NoteFragment noteFragment = new NoteFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    public static Typeface font ;

    public static int nightMode = 0 ;
    public static RelativeLayout parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }



    private void init() {

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);

        parent = findViewById(R.id.parent);


        setNightMode();
        setFont();


        bottomNavigation.setSelectedItemId(R.id.note);
        openFragment(noteFragment);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        setNightMode();
        setFont();
        switch (menuItem.getItemId()){
            case R.id.settings:{

                openFragment(settingsFragment);
                break;
            }
            case R.id.note:{

                openFragment(noteFragment);
                break;
            }
            case R.id.profile:{

                openFragment(profileFragment);
                break;
            }
        }

        return true ;

    }

    private void openFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container , fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigation.getSelectedItemId() != R.id.note){
            bottomNavigation.setSelectedItemId(R.id.note);
        }
        else super.onBackPressed();
    }


    public void setNightMode(){
        nightMode = spref.getOurInstance().getInt(ROUTER.NIGHT_MODE , 0);

        if (nightMode == 1){
            parent.setBackgroundColor(getResources().getColor(R.color.darkBackground));
        }else {
            parent.setBackgroundColor(getResources().getColor(R.color.ColorIcons));
        }
    }

    public void setFont(){

        try {

            font = Typeface.createFromAsset(getAssets(), "fonts/"+spref.getOurInstance().getString(ROUTER.FONT , "roboto.ttf"));

        }catch (Exception e){

        }
    }

}