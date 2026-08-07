<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đổi mật khẩu - Trang Trại Sầu Riêng</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body {
                font-family: 'Roboto', sans-serif;
                background: linear-gradient(135deg, #1e2a3a 0%, #2a3a4e 100%);
                min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 20px; color: #333;
            }
            .card {
                width: 100%; max-width: 460px; background: #fff; border-radius: 20px;
                box-shadow: 0 20px 60px rgba(0,0,0,0.3); padding: 40px;
            }
            .card .head { text-align: center; margin-bottom: 24px; }
            .card .head i { font-size: 42px; color: #6fcf97; margin-bottom: 12px; }
            .card .head h3 { font-size: 22px; color: #1e2a3a; font-weight: 500; }
            .card .head p { color: #6f8fb0; font-size: 14px; margin-top: 6px; }
            .info-box { background: #eef6ff; border: 1px solid #cfe3fb; color: #24506f; padding: 12px 14px; border-radius: 10px; font-size: 13px; margin-bottom: 22px; display:flex; gap:10px; }
            .form-group { margin-bottom: 18px; }
            .form-group label { display: block; font-weight: 500; margin-bottom: 8px; color: #2c3e50; font-size: 14px; }
            .input-icon { position: relative; }
            .input-icon i { position: absolute; left: 14px; top: 50%; transform: translateY(-50%); color: #8aa3c0; }
            .input-icon input { width: 100%; padding: 12px 14px 12px 42px; border: 1px solid #d0d8e3; border-radius: 10px; font-size: 15px; background: #fafbfc; }
            .input-icon input:focus { border-color: #4d90fe; outline: none; background: #fff; }
            .btn { width: 100%; padding: 13px; border: none; border-radius: 10px; background: #6fcf97; color: #fff; font-size: 16px; font-weight: 500; cursor: pointer; display:flex; align-items:center; justify-content:center; gap:10px; }
            .btn:hover { background: #52b381; }
            .alert-error { background: #fadbd8; color: #922b21; border: 1px solid #f5b7b1; padding: 12px 14px; border-radius: 10px; font-size: 14px; margin-bottom: 18px; display:flex; align-items:center; gap:10px; }
            .hint { font-size: 12px; color: #8aa3c0; margin-top: 6px; }
        </style>
    </head>
    <body>
        <div class="card">
            <div class="head">
                <i class="fas fa-key"></i>
                <h3>Đổi mật khẩu lần đầu</h3>
                <p>Xin chào <b>${sessionScope.pendingHoTen}</b> (${sessionScope.pendingUsername})</p>
            </div>

            <div class="info-box">
                <i class="fas fa-info-circle" style="margin-top:2px;"></i>
                <span>Đây là lần đăng nhập đầu tiên. Vì lý do bảo mật, bạn cần đổi mật khẩu mặc định trước khi sử dụng hệ thống.</span>
            </div>

            <c:if test="${not empty error}">
                <div class="alert-error"><i class="fas fa-exclamation-circle"></i> ${error}</div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/auth">
                <input type="hidden" name="action" value="changepassword">
                <div class="form-group">
                    <label>Mật khẩu hiện tại (mật khẩu mặc định)</label>
                    <div class="input-icon">
                        <i class="fas fa-lock"></i>
                        <input type="password" name="matKhauCu" placeholder="Nhập mật khẩu mặc định" required autofocus>
                    </div>
                </div>
                <div class="form-group">
                    <label>Mật khẩu mới</label>
                    <div class="input-icon">
                        <i class="fas fa-lock-open"></i>
                        <input type="password" name="matKhauMoi" placeholder="Ít nhất 6 ký tự" minlength="6" required>
                    </div>
                    <div class="hint">Mật khẩu nên gồm chữ và số, tối thiểu 6 ký tự.</div>
                </div>
                <div class="form-group">
                    <label>Nhập lại mật khẩu mới</label>
                    <div class="input-icon">
                        <i class="fas fa-check-double"></i>
                        <input type="password" name="nhapLai" placeholder="Nhập lại mật khẩu mới" minlength="6" required>
                    </div>
                </div>
                <button type="submit" class="btn"><i class="fas fa-save"></i> Cập nhật mật khẩu</button>
            </form>
        </div>
    </body>
</html>
