package wifibs.dexian.secretdevltd.com.wifibikesecurity;

public class UserData {

    public String username, key, email, ip, pass, reg_date,mobile_number;
    public int port;

    public UserData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserData(String username, String email, String key, String ip, int port, String pass, String reg_date,String mobile_number) {
        this.username = username;
        this.email = email;
        this.key = key;
        this.ip = ip;
        this.pass = pass;
        this.reg_date = reg_date;
        this.port = port;
        this.mobile_number = mobile_number;
    }


    public String getUsername() {
        return username;
    }

    public String getKey() {
        return key;
    }

    public String getEmail() {
        return email;
    }

    public String getIp() {
        return ip;
    }

    public String getPass() {
        return pass;
    }

    public String getReg_date() {
        return reg_date;
    }

    public int getPort() {
        return port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }
}