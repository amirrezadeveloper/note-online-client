package fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.noxnotes.LoginActivity;
import com.example.noxnotes.R;
import app.*;

public class ProfileFragment extends Fragment {

    AppCompatEditText email , fName , lName ;
    AppCompatImageView avatar ;
    AppCompatTextView logout ;

    public static int nightMode = 0 ;
    RelativeLayout parent;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        return init(view);
    }

    private View init(View view) {

        email  = view.findViewById(R.id.email);
        fName  = view.findViewById(R.id.fName);
        lName  = view.findViewById(R.id.lName);
        avatar = view.findViewById(R.id.avatar);
        logout = view.findViewById(R.id.logout);
        parent = view.findViewById(R.id.parent);

        email.setText(spref.getOurInstance().getString(ROUTER.INPUT_EMAIL , ""));
        fName.setText(spref.getOurInstance().getString(ROUTER.INPUT_FNAME , ""));
        lName.setText(spref.getOurInstance().getString(ROUTER.INPUT_LNAME , ""));

        nightMode = spref.getOurInstance().getInt(ROUTER.NIGHT_MODE , 0);
        if (nightMode == 1){
            parent.setBackgroundColor(getResources().getColor(R.color.darkBackground));
            email.setTextColor(getResources().getColor(R.color.ColorIcons));
            fName.setTextColor(getResources().getColor(R.color.ColorIcons));
            lName.setTextColor(getResources().getColor(R.color.ColorIcons));

        }

        Glide.with(Application.getContext())
                .load(app.main.URL + spref.getOurInstance().getString("image" , ""))

                .apply( new RequestOptions()
                        .placeholder(R.drawable.ic_person)
                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(avatar);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Log Out");
                alert.setMessage("Do you really want to log Out ?");
                alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        spref.getOurInstance().edit().clear().apply();
                        startActivity(new Intent(getActivity() , LoginActivity.class));
                        getActivity().finish();

                    }
                });
                alert.setPositiveButton("No" , null);
                alert.show();
            }
        });

        return view;
    }
}