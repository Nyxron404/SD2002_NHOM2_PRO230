package util;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Tiện ích gửi email qua SMTP (Gmail) bằng thư viện jakarta.mail.
 *
 * CÁCH CẤU HÌNH:
 *  1. Tạo "Mật khẩu ứng dụng" (App Password) cho tài khoản Gmail dùng để gửi.
 *  2. Điền SENDER_EMAIL và SENDER_APP_PASSWORD bên dưới.
 *
 * Nếu chưa cấu hình (hoặc gửi lỗi), hệ thống sẽ ghi nội dung email ra console
 * server để lập trình viên vẫn lấy được username/mật khẩu/mã xác thực khi phát triển.
 */
public class EmailUtil {

    // ==== CẤU HÌNH TÀI KHOẢN GỬI ====
    private static final String SENDER_EMAIL = "smartfarmmanage@gmail.com";
    private static final String SENDER_APP_PASSWORD = "grxhduafxftczxjm"; // App Password Gmail
    private static final String SENDER_NAME = "Smart Farm";

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    private static boolean isConfigured() {
        return SENDER_EMAIL != null && !SENDER_EMAIL.isEmpty()
                && SENDER_APP_PASSWORD != null && !SENDER_APP_PASSWORD.isEmpty();
    }

    /**
     * Gửi email dạng HTML. Trả về true nếu gửi thành công (hoặc đã ghi log fallback ở chế độ dev).
     */
    public static boolean send(String toEmail, String subject, String htmlBody) {
        if (!isConfigured()) {
            // Chế độ phát triển: chưa cấu hình SMTP -> in ra console để lấy thông tin.
            System.out.println("======================================================");
            System.out.println("[EMAIL - CHẾ ĐỘ DEV, CHƯA CẤU HÌNH SMTP]");
            System.out.println("Gửi tới : " + toEmail);
            System.out.println("Tiêu đề : " + subject);
            System.out.println("Nội dung:\n" + htmlBody.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim());
            System.out.println("======================================================");
            return true;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME, "UTF-8"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject, "UTF-8");
            message.setContent(htmlBody, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("[EMAIL] Đã gửi email tới " + toEmail);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // Gửi lỗi -> vẫn in ra console để không chặn luồng nghiệp vụ khi phát triển.
            System.out.println("[EMAIL - GỬI LỖI, IN RA CONSOLE] tới " + toEmail + " | " + subject);
            System.out.println(htmlBody.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim());
            return false;
        }
    }

    /** Email gửi thông tin đăng nhập khi tạo tài khoản mới (UC-1.1). */
    public static void guiThongTinDangNhap(String toEmail, String hoTen, String tenDangNhap, String matKhau) {
        String subject = "THÔNG TIN TÀI KHOẢN SMART FARM";
        String body = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                + "<h2 style='color: #2e7d32;'>Chào mừng bạn gia nhập Smart Farm!</h2>"
                + "<p>Chào <b>" + safe(hoTen) + "</b>, tài khoản làm việc của bạn trên hệ thống đã được thiết lập thành công.</p>"
                + "<div style='background-color: #f9f9f9; padding: 15px; border-left: 5px solid #2e7d32; border-radius: 5px;'>"
                + "<strong>Tên đăng nhập:</strong> <span style='color: #d35400; font-size: 18px;'>" + safe(tenDangNhap) + "</span><br>"
                + "<strong>Mật khẩu:</strong> <span style='color: #d35400; font-size: 18px;'>" + safe(matKhau) + "</span>"
                + "</div>"
                + "<p>Vui lòng đăng nhập và <b>đổi mật khẩu ngay</b> trong lần đầu tiên sử dụng.</p>"
                + "<p style='color: #c0392b;'><strong>⚠️ Lưu ý bảo mật:</strong> Tuyệt đối không chia sẻ mật khẩu này với bất kỳ ai để đảm bảo an toàn cho tài khoản cá nhân của bạn.</p>"
                + "<hr style='border: 0; border-top: 1px solid #eee;'>"
                + "<p>Nếu bạn cần hỗ trợ, vui lòng liên hệ bộ phận Kỹ thuật qua email: "
                + "<a href='mailto:longdazng@gmail.com'>longdazng@gmail.com</a> hoặc "
                + "<a href='mailto:linhhqth08598@gmail.com'>linhhqth08598@gmail.com</a>.</p>"
                + "<p style='font-size: 12px; color: #777;'><i>Lưu ý: Đây là email tự động, vui lòng không phản hồi thư này.</i></p>"
                + "<p>Trân trọng,<br><b>Ban Quản Trị Smart Farm</b></p>"
                + "</div>";
        send(toEmail, subject, body);
    }

    /** Email gửi lại tên đăng nhập mới khi cập nhật họ tên (UC-1.2). */
    public static void guiTenDangNhapMoi(String toEmail, String hoTen, String tenDangNhapMoi) {
        String subject = "[Smart Farm] Cập nhật tên đăng nhập";
        String body = "<div style='font-family:Arial,sans-serif;max-width:560px;margin:auto;border:1px solid #e9edf4;border-radius:12px;overflow:hidden'>"
                + "<div style='background:#2e7d32;color:#fff;padding:20px 24px'><h2 style='margin:0'>🌱 Smart Farm</h2></div>"
                + "<div style='padding:24px;color:#333'>"
                + "<p>Xin chào <b>" + safe(hoTen) + "</b>,</p>"
                + "<p>Tên đăng nhập của bạn đã được cập nhật lại. Tên đăng nhập mới là:</p>"
                + "<p style='font-size:20px;text-align:center;padding:12px;background:#f5f7fa;border-radius:8px'><b>" + safe(tenDangNhapMoi) + "</b></p>"
                + "<p>Mật khẩu của bạn giữ nguyên như trước.</p>"
                + "</div>"
                + "<div style='background:#f5f7fa;color:#8aa3c0;padding:14px 24px;font-size:12px'>Email tự động, vui lòng không trả lời.</div>"
                + "</div>";
        send(toEmail, subject, body);
    }

    /** Email gửi mã xác thực để đặt lại mật khẩu (UC-0.1 luồng 3a). */
    public static void guiMaXacThuc(String toEmail, String hoTen, String maXacThuc, int soPhutHieuLuc) {
        String subject = "[Smart Farm] Mã xác thực đặt lại mật khẩu";
        String body = "<div style='font-family:Arial,sans-serif;max-width:560px;margin:auto;border:1px solid #e9edf4;border-radius:12px;overflow:hidden'>"
                + "<div style='background:#2e7d32;color:#fff;padding:20px 24px'><h2 style='margin:0'>🌱 Smart Farm</h2></div>"
                + "<div style='padding:24px;color:#333'>"
                + "<p>Xin chào <b>" + safe(hoTen) + "</b>,</p>"
                + "<p>Bạn (hoặc ai đó) đã yêu cầu đặt lại mật khẩu. Mã xác thực của bạn là:</p>"
                + "<p style='font-size:32px;letter-spacing:8px;text-align:center;padding:16px;background:#f5f7fa;border-radius:8px;color:#1e2a3a'><b>" + safe(maXacThuc) + "</b></p>"
                + "<p>Mã có hiệu lực trong <b>" + soPhutHieuLuc + " phút</b>. Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>"
                + "</div>"
                + "<div style='background:#f5f7fa;color:#8aa3c0;padding:14px 24px;font-size:12px'>Email tự động, vui lòng không trả lời.</div>"
                + "</div>";
        send(toEmail, subject, body);
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace("<", "&lt;").replace(">", "&gt;");
    }
}
