package com.example.appfood.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class CustomIdGeneratorUtils {
    public static String generateUserId(String string) {
        // Ngày hiện tại
        String datePart = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        // 6 ký tự ngẫu nhiên
        String randomPart = getRandomAlphaNumeric(6);

        // Kết hợp tạo ID
        return string + datePart + "_" + randomPart;
    }

    // Hàm tạo chuỗi ngẫu nhiên gồm chữ và số
    private static String getRandomAlphaNumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }

        return sb.toString();
    }
}
