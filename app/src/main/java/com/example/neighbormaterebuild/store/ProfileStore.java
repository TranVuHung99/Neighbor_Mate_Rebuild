package com.example.neighbormaterebuild.store;

import com.example.neighbormaterebuild.model.User;

public class ProfileStore {

    private static ProfileStore ourInstance = new ProfileStore();

    public static ProfileStore getInstance() {
        if (ourInstance == null) {
            synchronized (ProfileStore.class) {
                if (null == ourInstance) {
                    ourInstance = new ProfileStore();
                }
            }
        }
        return ourInstance;
    }

    private ProfileStore() {
    }

    private User userLogin;


    public static ProfileStore getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(ProfileStore ourInstance) {
        ProfileStore.ourInstance = ourInstance;
    }

    public User getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(User userLogin) {
        this.userLogin = userLogin;
    }
}
