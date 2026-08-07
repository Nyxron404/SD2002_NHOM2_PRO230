<%@page contentType="text/html" pageEncoding="UTF-8"%>
<style>
    /* ===== HEADER ===== */
    .header { background: #fff; padding: 16px 32px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 2px 8px rgba(0,0,0,0.05); flex-shrink: 0; border-bottom: 1px solid #e9edf4; }
    .header-left { display: flex; align-items: center; gap: 16px; }
    .header-left .menu-toggle { background: none; border: none; font-size: 22px; color: #4a5b6e; cursor: pointer; display: none; }
    .header-left h1 { font-size: 20px; font-weight: 500; color: #1e2a3a; }
    .header-left h1 span { font-weight: 300; color: #6f8fb0; }
    .header-right { display: flex; align-items: center; gap: 20px; }
    .header-right .notification { position: relative; font-size: 20px; color: #4a5b6e; cursor: pointer; }
    .header-right .notification .badge { position: absolute; top: -6px; right: -8px; background: #e74c3c; color: #fff; font-size: 10px; font-weight: 700; padding: 2px 6px; border-radius: 20px; }
    .header-right .user-info { display: flex; align-items: center; gap: 12px; cursor: pointer; }
    .header-right .user-info .avatar { width: 38px; height: 38px; border-radius: 50%; background: #6fcf97; display: flex; align-items: center; justify-content: center; color: #fff; font-weight: 700; font-size: 16px; }
    .header-right .user-info .user-name { font-weight: 500; font-size: 14px; color: #1e2a3a; }
    .header-right .user-info .user-role { font-size: 12px; color: #6f8fb0; }
    @media (max-width: 992px) { .header-left .menu-toggle { display: block; } .header-left h1 span, .header-right .user-info .user-name, .header-right .user-info .user-role { display: none; } }
</style>

<header class="header">
    <div class="header-left">
        <button class="menu-toggle" id="menuToggle"><i class="fas fa-bars"></i></button>
        <!-- Thêm id="pageHeaderTitle" để JS ở trang con có thể thay đổi tên -->
        <h1 id="pageHeaderTitle">Tổng quan <span>| Bảng điều khiển</span></h1>
    </div>
    <div class="header-right">
        <div class="notification">
            <i class="fas fa-bell"></i>
            <span class="badge">3</span>
        </div>
        <div class="user-info" onclick="window.location.href='${pageContext.request.contextPath}/logout'" title="Đăng xuất">
            <div class="avatar" id="headerAvatar" data-name="${sessionScope.hoTen}">U</div>
            <div>
                <div class="user-name">${sessionScope.hoTen != null ? sessionScope.hoTen : 'Người dùng'}</div>
                <div class="user-role">${sessionScope.vaiTro != null ? sessionScope.vaiTro : ''}</div>
            </div>
        </div>
    </div>
</header>
<script>
    (function () {
        var a = document.getElementById('headerAvatar');
        if (a) {
            var name = (a.getAttribute('data-name') || '').trim();
            if (name) {
                var parts = name.split(/\s+/);
                a.textContent = parts[parts.length - 1].charAt(0).toUpperCase();
            }
        }
    })();
</script>