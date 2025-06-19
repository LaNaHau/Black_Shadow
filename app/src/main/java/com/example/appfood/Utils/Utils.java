package com.example.appfood.Utils;

import android.util.Patterns;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;

import java.text.NumberFormat;
import java.util.Locale;

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

    public static double getDoubleValue(DataSnapshot snapshot, String key) {
        Double value = snapshot.child(key).getValue(Double.class);
        return value != null ? value : 0.0;
    }

    public static long getLongValue(DataSnapshot snapshot, String key) {
        Long value = snapshot.child(key).getValue(Long.class);
        return value != null ? value : 0L;
    }

    public static String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " VND";
    }

}
