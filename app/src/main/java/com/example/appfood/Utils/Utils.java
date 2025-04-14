package com.example.appfood.Utils;

import android.util.Patterns;
import android.widget.EditText;

public class Utils {

    public static boolean inputValidation(EditText input) {
        String username = input.getText().toString().trim();
        return username.length() != 0;
    }

    public static boolean isValidEmail(EditText input) {
        String email = input.getText().toString().trim();
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(EditText input) {
        String phone = input.getText().toString().trim();
        return phone.matches("\\d{10}"); // chỉ nhận đúng 10 chữ số
    }

    public static boolean isValidPassword(EditText input) {
        String password = input.getText().toString().trim();
        return password.length() >= 6; // tối thiểu 6 ký tự
    }
}
