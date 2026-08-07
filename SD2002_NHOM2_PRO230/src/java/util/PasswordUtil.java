package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Tiện ích xử lý mật khẩu và sinh tên đăng nhập.
 *  - Mật khẩu được băm bằng SHA-256 trước khi lưu vào CSDL (không lưu mật khẩu thô).
 *  - Sinh mật khẩu mặc định 8 chữ số ngẫu nhiên (theo đặc tả UC-1.1).
 *  - Sinh tên đăng nhập từ họ tên (bỏ dấu tiếng Việt).
 */
public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /** Băm mật khẩu bằng SHA-256, trả về chuỗi hex thường. */
    public static String hash(String matKhautho) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(matKhautho.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** So khớp mật khẩu thô người dùng nhập với chuỗi băm lưu trong DB. */
    public static boolean matches(String matKhautho, String hashLuuTru) {
        if (matKhautho == null || hashLuuTru == null) return false;
        String h = hash(matKhautho);
        return h != null && h.equalsIgnoreCase(hashLuuTru);
    }

    /** Sinh mật khẩu mặc định gồm 8 chữ số (UC-1.1 bước 8). */
    public static String generateDefaultPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /** Sinh mã xác thực đặt lại mật khẩu (6 chữ số) cho luồng quên mật khẩu. */
    public static String generateVerificationCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Sinh phần gốc của tên đăng nhập từ họ tên, theo quy tắc:
     *  "Đặng Thành Long"  -> "longdt" (tên chính + chữ cái đầu các từ còn lại)
     *  "Long"             -> "long"
     * Kết quả đã bỏ dấu, viết thường, chỉ còn chữ và số.
     */
    public static String generateBaseUsername(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            return "user";
        }
        String khongDau = removeAccents(hoTen.trim()).toLowerCase();
        // Chỉ giữ lại chữ, số và khoảng trắng
        khongDau = khongDau.replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
        if (khongDau.isEmpty()) {
            return "user";
        }
        String[] tu = khongDau.split(" ");
        if (tu.length == 1) {
            return tu[0];
        }
        // Từ cuối là tên chính, các từ trước lấy chữ cái đầu
        StringBuilder sb = new StringBuilder(tu[tu.length - 1]);
        for (int i = 0; i < tu.length - 1; i++) {
            if (!tu[i].isEmpty()) {
                sb.append(tu[i].charAt(0));
            }
        }
        return sb.toString();
    }

    /** Bỏ dấu tiếng Việt. */
    public static String removeAccents(String input) {
        if (input == null) return "";
        String temp = input.replace('đ', 'd').replace('Đ', 'D');
        temp = Normalizer.normalize(temp, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
}
