<%@page contentType="text/html" pageEncoding="UTF-8"%>
<style>
    .sidebar { width: 260px; background: #1e2a3a; color: #b0c4de; display: flex; flex-direction: column; flex-shrink: 0; height: 100vh; position: sticky; top: 0; overflow-y: auto; transition: width 0.3s; z-index: 100; }
    .sidebar-brand { display: flex; align-items: center; padding: 24px 20px 16px; border-bottom: 1px solid #2a3a4e; }
    .sidebar-brand i { font-size: 28px; color: #6fcf97; margin-right: 12px; }
    .sidebar-brand h2 { font-size: 18px; font-weight: 700; color: #fff; }
    .sidebar-brand small { font-size: 12px; font-weight: 300; color: #8aa3c0; display: block; }
    .sidebar-menu { flex: 1; padding: 20px 0; }
    .sidebar-menu .menu-label { font-size: 11px; text-transform: uppercase; letter-spacing: 1px; padding: 0 24px; color: #5f7a99; margin: 8px 0; }
    .sidebar-menu ul { list-style: none; padding: 0; margin: 0; }
    .sidebar-menu ul li { margin: 2px 12px; border-radius: 8px; }
    .sidebar-menu ul li a { display: flex; align-items: center; padding: 12px 16px; color: #b0c4de; text-decoration: none; font-size: 15px; border-radius: 8px; transition: all 0.2s; }
    .sidebar-menu ul li a i { width: 24px; font-size: 18px; margin-right: 14px; text-align: center; color: #6f8fb0; }
    .sidebar-menu ul li a:hover { background: #2a3a4e; color: #fff; }
    .sidebar-menu ul li a:hover i { color: #6fcf97; }
    .sidebar-menu ul li.active a { background: #2a3a4e; color: #fff; box-shadow: inset 3px 0 0 #6fcf97; }
    .sidebar-menu ul li.active a i { color: #6fcf97; }
    .sidebar-footer { padding: 16px 20px; border-top: 1px solid #2a3a4e; font-size: 13px; color: #5f7a99; display: flex; justify-content: space-between; }
    .sidebar-footer a { color: #6f8fb0; text-decoration: none; }
    .sidebar-footer a:hover { color: #6fcf97; }
</style>

<aside class="sidebar" id="sidebar">
    <div class="sidebar-brand">
        <i class="fas fa-seedling"></i>
        <div>
            <h2>Trang Trại</h2>
            <small>Sầu Riêng</small>
        </div>
    </div>
    
    <nav class="sidebar-menu">
        <div class="menu-label">DANH MỤC CHỨC NĂNG</div>
        <ul id="navLinks">
            <!-- UC-1 -->
            <li><a href="${pageContext.request.contextPath}/views/taiKhoan/quanLyTaiKhoan.jsp"><i class="fas fa-user-shield"></i> <span>Quản lý tài khoản</span></a></li>
            <!-- UC-2 -->
            <li><a href="${pageContext.request.contextPath}/views/trangTrai/quanLyTrangTrai.jsp"><i class="fas fa-map-marked-alt"></i> <span>Quản lý trang trại, khu vực</span></a></li>
            <!-- UC-3 -->
            <li><a href="${pageContext.request.contextPath}/vattu"><i class="fas fa-boxes"></i> <span>Quản lý vật tư nông nghiệp</span></a></li>
            <!-- UC-4 -->
            <li><a href="${pageContext.request.contextPath}/views/canhTac/canhTac.jsp"><i class="fas fa-seedling"></i> <span>Quản lý canh tác sầu riêng</span></a></li>
            <!-- UC-5 -->
            <li><a href="${pageContext.request.contextPath}/tbdc"><i class="fas fa-tractor"></i> <span>Quản lý dụng cụ, thiết bị</span></a></li>
            <!-- UC-6 -->
            <li><a href="${pageContext.request.contextPath}/views/baoCao/baoCao.jsp"><i class="fas fa-chart-pie"></i> <span>Báo cáo và thống kê</span></a></li>
        </ul>
        <div class="menu-label">HỆ THỐNG</div>
        <ul>
            <!-- UC-0 -->
            <li><a href="${pageContext.request.contextPath}/dangXuat.jsp"><i class="fas fa-sign-out-alt"></i> <span>Đăng xuất</span></a></li>
        </ul>
    </nav>
    <div class="sidebar-footer">
        <span><i class="far fa-copyright"></i> 2026</span>
        <a href="#"><i class="fas fa-question-circle"></i></a>
    </div>
</aside>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        var currentPath = window.location.pathname;
        var links = document.querySelectorAll('#sidebar a');
        links.forEach(function(link) {
            var href = link.getAttribute('href');
            if (currentPath === href || currentPath.indexOf(href) > -1) {
                link.parentElement.classList.add('active');
            } else {
                link.parentElement.classList.remove('active');
            }
        });
    });
</script>