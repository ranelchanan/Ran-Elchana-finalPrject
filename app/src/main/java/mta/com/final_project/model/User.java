package mta.com.final_project.model;

import java.util.Dictionary;
import java.util.Enumeration;

/**
 * Created by rgerman on 4/27/2018.
 */

public class User {


    private String id;
    private String email;
    private String photoUrl;
    private String username;
    private String phoneNumber;
    private Dictionary<String , Boolean> notificationTokens;
    public User(){
        notificationTokens = new Dictionary<String, Boolean>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public Enumeration<String> keys() {
                return null;
            }

            @Override
            public Enumeration<Boolean> elements() {
                return null;
            }

            @Override
            public Boolean get(Object key) {
                return null;
            }

            @Override
            public Boolean put(String key, Boolean value) {
                return null;
            }

            @Override
            public Boolean remove(Object key) {
                return null;
            }
        };
    }

    public User(String id, String email, String photoUrl, String username) {
        this.username = username;
        this.email = email;
        this.photoUrl = photoUrl;
        this.id = id;
    }

    public Dictionary<String, Boolean> getNotificationTokens() {
        return notificationTokens;
    }

    public void setNotificationTokens(Dictionary<String, Boolean> notificationTokens) {
        this.notificationTokens = notificationTokens;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}


