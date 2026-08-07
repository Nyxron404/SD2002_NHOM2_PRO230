<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quên mật khẩu - Trang Trại Sầu Riêng</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body {
                font-family: 'Roboto', sans-serif;
                background: linear-gradient(135deg, #1e2a3a 0%, #2a3a4e 100%);
                min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 20px; color: #333;
            }
            .card { width: 100%; max-width: 480px; background: #fff; border-radius: 20px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); padding: 40px; }
            .head { text-align: center; margin-bottom: 20px; }
            .head i.main { font-size: 42px; color: #6fcf97; margin-bottom: 12px; }
            .head h3 { font-size: 22px; color: #1e2a3a; font-weight: 500; }
            .head p { color: #6f8fb0; font-size: 14px; margin-top: 6px; }

            /* Thanh bước */
            .steps { display: flex; justify-content: center; gap: 10px; margin: 20px 0 26px; }
            .steps .dot { width: 32px; height: 32px; border-radius: 50%; background: #e9edf4; color: #8aa3c0; display:flex; align-items:center; justify-content:center; font-weight:700; font-size:14px; }
            .steps .dot.active { background: #6fcf97; color: #fff; }
            .steps .line { width: 40px; height: 2px; background: #e9edf4; align-self: center; }
            .steps .line.active { background: #6fcf97; }

            .form-group { margin-bottom: 18px; }
            .form-group label { display: block; font-weight: 500; margin-bottom: 8px; color: #2c3e50; font-size: 14px; }
            .input-icon { position: relative; }
            .input-icon i { position: absolute; left: 14px; top: 50%; transform: translateY(-50%); color: #8aa3c0; }
            .input-icon input { width: 100%; padding: 12px 14px 12px 42px; border: 1px solid #d0d8e3; border-radius: 10px; font-size: 15px; background: #fafbfc; }
            .input-icon input:focus { border-color: #4d90fe; outline: none; background: #fff; }
            .code-input input { text-align: center; letter-spacing: 8px; font-size: 22px; padding-left: 14px; font-weight: 700; }
            .btn { width: 100%; padding: 13px; border: none; border-radius: 10px; background: #6fcf97; color: #fff; font-size: 16px; font-weight: 500; cursor: pointer; display:flex; align-items:center; justify-content:center; gap:10px; }
            .btn:hover { background: #52b381; }
            .btn:disabled { opacity: .6; cursor: not-allowed; }
            .alert { padding: 12px 14px; border-radius: 10px; font-size: 14px; margin-bottom: 18px; display:flex; align-items:center; gap:10px; }
            .alert-error { background: #fadbd8; color: #922b21; border: 1px solid #f5b7b1; }
            .alert-success { background: #d5f5e3; color: #1e8449; border: 1px solid #a9dfbf; }
            .back-link { text-align: center; margin-top: 20px; font-size: 14px; }
            .back-link a { color: #4d90fe; text-decoration: none; }
            .back-link a:hover { text-decoration: underline; }
            .step { display: none; }
            .step.active { display: block; }
        </style>
    </head>
    <body>
        <div class="card">
            <div class="head">
                <i class="fas fa-unlock-alt main"></i>
                <h3>Khôi phục mật khẩu</h3>
                <p id="stepDesc">Nhập email và tên đăng nhập để nhận mã xác thực</p>
            </div>

            <div class="steps">
                <div class="dot active" id="dot1">1</div>
                <div class="line" id="line1"></div>
                <div class="dot" id="dot2">2</div>
                <div class="line" id="line2"></div>
                <div class="dot" id="dot3">3</div>
            </div>

            <div id="msg"></div>

            <!-- Bước 1: nhập email + username -->
            <div class="step active" id="step1">
                <div class="form-group">
                    <label>Email đã đăng ký</label>
                    <div class="input-icon">
                        <i class="fas fa-envelope"></i>
                        <input type="email" id="email" placeholder="Nhập email" required>
                    </div>
                </div>
                <div class="form-group">
                    <label>Tên đăng nhập</label>
                    <div class="input-icon">
                        <i class="fas fa-user"></i>
                        <input type="text" id="username" placeholder="Nhập tên đăng nhập" required>
                    </div>
                </div>
                <button class="btn" id="btnSendCode"><i class="fas fa-paper-plane"></i> Gửi mã xác thực</button>
            </div>

            <!-- Bước 2: nhập mã -->
            <div class="step" id="step2">
                <div class="form-group code-input">
                    <label>Mã xác thực (gửi qua email)</label>
                    <div class="input-icon">
                        <input type="text" id="ma" maxlength="6" placeholder="------" required>
                    </div>
                </div>
                <button class="btn" id="btnVerify"><i class="fas fa-check"></i> Xác thực mã</button>
            </div>

            <!-- Bước 3: mật khẩu mới -->
            <div class="step" id="step3">
                <div class="form-group">
                    <label>Mật khẩu mới</label>
                    <div class="input-icon">
                        <i class="fas fa-lock"></i>
                        <input type="password" id="matKhauMoi" placeholder="Ít nhất 6 ký tự" minlength="6" required>
                    </div>
                </div>
                <div class="form-group">
                    <label>Nhập lại mật khẩu mới</label>
                    <div class="input-icon">
                        <i class="fas fa-check-double"></i>
                        <input type="password" id="nhapLai" placeholder="Nhập lại mật khẩu mới" minlength="6" required>
                    </div>
                </div>
                <button class="btn" id="btnReset"><i class="fas fa-save"></i> Đặt lại mật khẩu</button>
            </div>

            <div class="back-link">
                <a href="${pageContext.request.contextPath}/auth"><i class="fas fa-arrow-left"></i> Quay lại đăng nhập</a>
            </div>
        </div>

        <script>
            const ctx = '${pageContext.request.contextPath}';

            function showMsg(type, text) {
                const box = document.getElementById('msg');
                box.innerHTML = '<div class="alert alert-' + type + '"><i class="fas fa-' +
                        (type === 'success' ? 'check-circle' : 'exclamation-circle') + '"></i> ' + text + '</div>';
            }
            function clearMsg() { document.getElementById('msg').innerHTML = ''; }

            function goStep(n) {
                document.querySelectorAll('.step').forEach(s => s.classList.remove('active'));
                document.getElementById('step' + n).classList.add('active');
                for (let i = 1; i <= 3; i++) {
                    document.getElementById('dot' + i).classList.toggle('active', i <= n);
                }
                document.getElementById('line1').classList.toggle('active', n >= 2);
                document.getElementById('line2').classList.toggle('active', n >= 3);
                const descs = {
                    1: 'Nhập email và tên đăng nhập để nhận mã xác thực',
                    2: 'Nhập mã xác thực đã gửi tới email của bạn',
                    3: 'Tạo mật khẩu mới cho tài khoản'
                };
                document.getElementById('stepDesc').textContent = descs[n];
            }

            function post(action, params, btn) {
                params.append('action', action);
                btn.disabled = true;
                return fetch(ctx + '/auth', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    body: params.toString()
                }).then(r => r.json()).finally(() => { btn.disabled = false; });
            }

            document.getElementById('btnSendCode').addEventListener('click', function () {
                clearMsg();
                const email = document.getElementById('email').value.trim();
                const username = document.getElementById('username').value.trim();
                if (!email || !username) { showMsg('error', 'Vui lòng nhập đầy đủ email và tên đăng nhập.'); return; }
                const p = new URLSearchParams();
                p.append('email', email);
                p.append('username', username);
                post('forgot', p, this).then(data => {
                    if (data.success) { showMsg('success', data.message); goStep(2); }
                    else showMsg('error', data.message);
                }).catch(() => showMsg('error', 'Có lỗi xảy ra, vui lòng thử lại.'));
            });

            document.getElementById('btnVerify').addEventListener('click', function () {
                clearMsg();
                const ma = document.getElementById('ma').value.trim();
                if (!ma) { showMsg('error', 'Vui lòng nhập mã xác thực.'); return; }
                const p = new URLSearchParams();
                p.append('ma', ma);
                post('verify', p, this).then(data => {
                    if (data.success) { showMsg('success', data.message); goStep(3); }
                    else showMsg('error', data.message);
                }).catch(() => showMsg('error', 'Có lỗi xảy ra, vui lòng thử lại.'));
            });

            document.getElementById('btnReset').addEventListener('click', function () {
                clearMsg();
                const m1 = document.getElementById('matKhauMoi').value;
                const m2 = document.getElementById('nhapLai').value;
                if (m1.length < 6) { showMsg('error', 'Mật khẩu mới phải có ít nhất 6 ký tự.'); return; }
                if (m1 !== m2) { showMsg('error', 'Mật khẩu nhập lại không khớp.'); return; }
                const p = new URLSearchParams();
                p.append('matKhauMoi', m1);
                p.append('nhapLai', m2);
                post('reset', p, this).then(data => {
                    if (data.success) {
                        showMsg('success', data.message + ' Đang chuyển về trang đăng nhập...');
                        document.querySelectorAll('.step').forEach(s => s.classList.remove('active'));
                        setTimeout(() => { window.location.href = ctx + '/auth'; }, 2000);
                    } else showMsg('error', data.message);
                }).catch(() => showMsg('error', 'Có lỗi xảy ra, vui lòng thử lại.'));
            });
        </script>
    </body>
</html>
