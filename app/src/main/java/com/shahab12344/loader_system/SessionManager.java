package com.shahab12344.loader_system;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_ROLE = "role";
    private static final String KEY_PROFILE_IMAGE_URI = "profile_image_uri";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createUserSession(String userId, String firstName, String lastName, String email, String phoneNumber, String role) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE_NUMBER, phoneNumber);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_USER_ID);
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    public String getFirstName() {
        return sharedPreferences.getString(KEY_FIRST_NAME, "");
    }

    public String getLastName() {
        return sharedPreferences.getString(KEY_LAST_NAME, "");
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString(KEY_PHONE_NUMBER, "");
    }

    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, "");
    }
    public void saveProfileImageUri(String uri) {
        editor.putString(KEY_PROFILE_IMAGE_URI, uri);
        editor.apply();
    }

    public String getProfileImageUri() {
        return sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, null);
    }

    public void updatePhoneNumber(String newPhoneNumber) {
        // Assuming you have a key like "PHONE_NUMBER" to store the phone number
        editor.putString("phone_number", newPhoneNumber);
        editor.apply();
    }
}
