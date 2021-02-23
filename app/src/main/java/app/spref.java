package app;

import android.content.Context;
import android.content.SharedPreferences;

public class spref {

    private static  SharedPreferences ourInstance = Application.getContext().getSharedPreferences(app.main.TAG , Context.MODE_PRIVATE);
    public static SharedPreferences getOurInstance(){

        if (ourInstance == null){
            ourInstance = Application.getContext().getSharedPreferences(app.main.TAG , Context.MODE_PRIVATE);
        }




        return ourInstance;
    }
}
