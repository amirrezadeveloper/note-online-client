package objects;

import com.google.gson.annotations.SerializedName;

public class UserObject {


    @SerializedName("USER_ID")
    private int id ;
    @SerializedName("EMAIL")
    private String email ;
    @SerializedName("FNAME")
    private String fname ;
    @SerializedName("LNAME")
    private String lname ;
    @SerializedName("SEX")
    private int sex ;
    @SerializedName("SESSION")
    private String session ;

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public int getSex() {
        return sex;
    }

    public String getSession() {
        return session;
    }
}
