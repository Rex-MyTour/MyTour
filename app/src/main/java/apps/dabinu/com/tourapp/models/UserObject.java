package apps.dabinu.com.tourapp.models;

public class UserObject{

    public String username, email, phonenumber;

    public UserObject(String username, String email, String phonenumber){
        this.username = username;
        this.email = email;
        this.phonenumber = phonenumber;
    }

    public UserObject(){

    }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

    public String getPhonenumber(){
        return phonenumber;
    }
}
