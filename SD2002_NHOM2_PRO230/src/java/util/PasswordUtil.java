package util;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /** So khớp mật khẩu người dùng nhập với mật khẩu lưu trong DB (so sánh trực tiếp). */
    public static boolean matches(String matKhauNhap, String matKhauLuuTru) {
        if (matKhauNhap == null || matKhauLuuTru == null) return false;
        // ĐÃ SỬA: Thêm trim() để loại bỏ khoảng trắng thừa vô tình có trong Database
        return matKhauNhap.trim().equals(matKhauLuuTru.trim());
    }

    public static String generateDefaultPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public static String generateVerificationCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public static String generateBaseUsername(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            return "user";
        }
        String khongDau = removeAccents(hoTen.trim()).toLowerCase();
        khongDau = khongDau.replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
        if (khongDau.isEmpty()) {
            return "user";
        }
        String[] tu = khongDau.split(" ");
        if (tu.length == 1) {
            return tu[0];
        }
        StringBuilder sb = new StringBuilder(tu[tu.length - 1]);
        for (int i = 0; i < tu.length - 1; i++) {
            if (!tu[i].isEmpty()) {
                sb.append(tu[i].charAt(0));
            }
        }
        return sb.toString();
    }

    public static String removeAccents(String input) {
        if (input == null) return "";
        String temp = input.replace('đ', 'd').replace('Đ', 'D');
        temp = Normalizer.normalize(temp, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
}