public abstract class User {

    // instance variables
    private String userid;
    private String password;
    private String name;

    // methods
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void signup(String userid, String password, String name) {
        // use the setter methods to set the instance variables
        this.setUserid(userid);
        this.setPassword(password);
        this.setName(name);
        System.out.println("Signup successful!");
    }


    public void login(String userid, String password) {
        // use the getter methods to access the instance variables
        if (this.getUserid().equals(userid) && this.getPassword().equals(password)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid userid or password.");
        }
        
    }

    public void logout() {
        System.out.println("Logout successful!");
    }

    public void changePassword(String newPassword) {
        this.setPassword(newPassword);
        System.out.println("Password changed successfully!");
    }












}
