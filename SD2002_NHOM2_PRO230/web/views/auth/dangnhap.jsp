<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng nhập - Trang Trại Sầu Riêng</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body {
                font-family: 'Roboto', sans-serif;
                background: linear-gradient(135deg, #1e2a3a 0%, #2a3a4e 100%);
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 20px;
                color: #333;
            }
            .auth-card {
                width: 100%;
                max-width: 900px;
                background: #fff;
                border-radius: 20px;
                box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                display: flex;
                overflow: hidden;
                min-height: 520px;
            }
            /* Panel trái - thương hiệu */
            .auth-brand {
                flex: 1;
                background: linear-gradient(160deg, #1e2a3a, #24506f);
                color: #fff;
                padding: 48px 40px;
                display: flex;
                flex-direction: column;
                justify-content: center;
                position: relative;
            }
            .auth-brand .logo { display: flex; align-items: center; gap: 14px; margin-bottom: 28px; }
            .auth-brand .logo i { font-size: 40px; color: #6fcf97; }
            .auth-brand .logo h1 { font-size: 26px; font-weight: 700; }
            .auth-brand .logo small { display: block; font-weight: 300; color: #8aa3c0; font-size: 14px; }
            .auth-brand h2 { font-size: 22px; font-weight: 500; margin-bottom: 14px; line-height: 1.4; }
            .auth-brand p { color: #b0c4de; font-weight: 300; line-height: 1.7; }
            .auth-brand .features { margin-top: 28px; list-style: none; }
            .auth-brand .features li { display: flex; align-items: center; gap: 12px; margin-bottom: 14px; color: #d0dce8; font-size: 14px; }
            .auth-brand .features li i { color: #6fcf97; }

            /* Panel phải - form */
            .auth-form-wrap {
                flex: 1;
                padding: 48px 44px;
                display: flex;
                flex-direction: column;
                justify-content: center;
            }
            .auth-form-wrap h3 { font-size: 24px; color: #1e2a3a; font-weight: 500; margin-bottom: 6px; }
            .auth-form-wrap .subtitle { color: #6f8fb0; font-size: 14px; margin-bottom: 28px; }
            .form-group { margin-bottom: 20px; }
            .form-group label { display: block; font-weight: 500; margin-bottom: 8px; color: #2c3e50; font-size: 14px; }
            .input-icon { position: relative; }
            .input-icon i { position: absolute; left: 14px; top: 50%; transform: translateY(-50%); color: #8aa3c0; }
            .input-icon input {
                width: 100%;
                padding: 12px 42px 12px 42px;
                border: 1px solid #d0d8e3;
                border-radius: 10px;
                font-size: 15px;
                background: #fafbfc;
                transition: border 0.2s;
            }
            .input-icon input:focus { border-color: #4d90fe; outline: none; background: #fff; }
            /* Selector đủ đặc trưng để thắng ".input-icon i", tránh 2 icon đè lên nhau */
            .input-icon i.toggle-pass { left: auto; right: 14px; cursor: pointer; }
            .row-between { display: flex; justify-content: space-between; align-items: center; margin-bottom: 22px; font-size: 14px; }
            .row-between a { color: #4d90fe; text-decoration: none; }
            .row-between a:hover { text-decoration: underline; }
            .btn-login {
                width: 100%;
                padding: 13px;
                border: none;
                border-radius: 10px;
                background: #6fcf97;
                color: #fff;
                font-size: 16px;
                font-weight: 500;
                cursor: pointer;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 10px;
                transition: 0.2s;
            }
            .btn-login:hover { background: #52b381; }
            .alert-error {
                background: #fadbd8;
                color: #922b21;
                border: 1px solid #f5b7b1;
                padding: 12px 14px;
                border-radius: 10px;
                font-size: 14px;
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                gap: 10px;
            }
            @media (max-width: 768px) {
                .auth-brand { display: none; }
                .auth-card { max-width: 440px; }
            }
        </style>
    </head>
    <body>
        <div class="auth-card">
            <div class="auth-brand">
                <div class="logo">
                    <i class="fas fa-seedling"></i>
                    <div>
                        <h1>Trang Trại</h1>
                        <small>Hệ thống quản lý Sầu Riêng</small>
                    </div>
                </div>
                <h2>Quản lý nông trại thông minh, hiệu quả và bền vững.</h2>
                <p>Đăng nhập để quản lý tài khoản, vật tư, canh tác và theo dõi toàn bộ hoạt động của trang trại.</p>
                <ul class="features">
                    <li><i class="fas fa-check-circle"></i> Quản lý tài khoản người dùng</li>
                    <li><i class="fas fa-check-circle"></i> Theo dõi vật tư, dụng cụ, thiết bị</li>
                    <li><i class="fas fa-check-circle"></i> Báo cáo và thống kê trực quan</li>
                </ul>
            </div>

            <div class="auth-form-wrap">
                <h3>Chào mừng trở lại 👋</h3>
                <div class="subtitle">Vui lòng đăng nhập vào tài khoản của bạn</div>

                <c:if test="${not empty error}">
                    <div class="alert-error"><i class="fas fa-exclamation-circle"></i> ${error}</div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/auth">
                    <input type="hidden" name="action" value="login">
                    <div class="form-group">
                        <label>Tên đăng nhập</label>
                        <div class="input-icon">
                            <i class="fas fa-user"></i>
                            <input type="text" name="username" placeholder="Nhập tên đăng nhập"
                                   value="${username != null ? username : ''}" required autofocus>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Mật khẩu</label>
                        <div class="input-icon">
                            <i class="fas fa-lock"></i>
                            <input type="password" name="password" id="password" placeholder="Nhập mật khẩu" required>
                            <i class="fas fa-eye toggle-pass" onclick="togglePassword()"></i>
                        </div>
                    </div>
                    <div class="row-between">
                        <span></span>
                        <a href="${pageContext.request.contextPath}/views/auth/quenmatkhau.jsp">Quên mật khẩu?</a>
                    </div>
                    <button type="submit" class="btn-login"><i class="fas fa-sign-in-alt"></i> Đăng nhập</button>
                </form>
            </div>
        </div>

        <script>
            function togglePassword() {
                var input = document.getElementById('password');
                var icon = document.querySelector('.toggle-pass');
                if (input.type === 'password') {
                    input.type = 'text';
                    icon.classList.replace('fa-eye', 'fa-eye-slash');
                } else {
                    input.type = 'password';
                    icon.classList.replace('fa-eye-slash', 'fa-eye');
                }
            }
        </script>
    </body>
</html>
