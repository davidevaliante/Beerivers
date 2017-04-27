package lite.organizer.mtg.harlequin.com.hedronalignment;

/**
 * Created by akain on 09/01/2017.
 */



public class User {

    public String userGender;

    public String userName,userSurname,userMail,userPass,userPhone;

    public User(){

    }

    public User(String userName,String userSurname,String userMail, String userPass, String userGender, String userPhone){
        this.userName = userName;
        this.userSurname = userSurname;
        this.userMail = userMail;
        this.userPass = userPass;
        this.userGender = userGender;
        this.userPhone = userPhone;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }
}
