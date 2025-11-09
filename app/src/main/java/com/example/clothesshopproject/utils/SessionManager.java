package com.example.clothesshopproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FULLNAME = "fullname";

    private static final String KEY_USER_ROLE = "admin";
    private static final String USER_ROLE_ADMIN = "ADMIN";
    // ------------------------------------

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String token, String email, String fullName) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_FULLNAME, fullName);
        editor.apply();
    }

    // Bổ sung phương thức lưu token (để AdminProductActivity dùng)
    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public void saveUserRole(String roleName) {
        editor.putString(KEY_USER_ROLE, roleName);
        editor.apply();
    }

    // BỔ SUNG CỦA BẠN: Phương thức lấy Role
    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, null);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getFullName() {
        return prefs.getString(KEY_FULLNAME, null);
    }

    public void clear() {
        editor.clear().apply();
    }

    public boolean isAdmin() {
        String currentRole = getUserRole();
        return USER_ROLE_ADMIN.equals(currentRole);
    }
}