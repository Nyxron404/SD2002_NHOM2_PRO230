<%--
    Document   : quanLyTrangTrai
    Mô tả      : UC-2.1 Thông tin trang trại | UC-2.2 Phân chia khu vực | UC-2.3 Quản lý khu vực chi tiết
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Trang trại - Trang trại Sầu Riêng</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
        <style>
            /* BASE CSS - dùng chung style hệ thống */
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { font-family: 'Roboto', sans-serif; background: #f5f7fa; color: #333; display: flex; height: 100vh; overflow: hidden; }
            .main-content { flex: 1; display: flex; flex-direction: column; height: 100vh; overflow: hidden; }
            .content { flex: 1; padding: 28px 32px; overflow-y: auto; background: #f5f7fa; }
            .overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.3); z-index: 999; }
            .overlay.active { display: block; }

            /* DASHBOARD CARDS */
            .dashboard-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; margin-bottom: 24px; }
            .stat-card { background: #fff; border-radius: 12px; padding: 20px; display: flex; align-items: center; box-shadow: 0 4px 12px rgba(0,0,0,0.03); border: 1px solid #e9edf4; transition: transform 0.2s; }
            .stat-card:hover { transform: translateY(-5px); box-shadow: 0 8px 16px rgba(0,0,0,0.06); }
            .stat-icon { width: 56px; height: 56px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; margin-right: 16px; flex-shrink: 0; }
            .stat-icon.primary { background: #e6f0ff; color: #4d90fe; }
            .stat-icon.warning { background: #fef5e6; color: #f39c12; }
            .stat-icon.success { background: #e8f8f0; color: #27ae60; }
            .stat-icon.danger { background: #fceceb; color: #e74c3c; }
            .stat-details h3 { font-size: 13px; color: #8aa3c0; font-weight: 500; margin-bottom: 4px; text-transform: uppercase; letter-spacing: 0.5px; }
            .stat-details p { font-size: 26px; font-weight: 700; color: #1e2a3a; margin: 0; }
            .stat-details small { font-size: 12px; color: #8aa3c0; }

            /* TABS */
            .tabs { display: flex; gap: 4px; margin-bottom: 24px; background: #fff; border-radius: 12px; padding: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); border: 1px solid #e9edf4; width: fit-content; }
            .tab-btn { padding: 10px 24px; border: none; background: transparent; border-radius: 8px; font-weight: 500; font-size: 14px; color: #6f8fb0; cursor: pointer; transition: all 0.2s; display: flex; align-items: center; gap: 8px; }
            .tab-btn:hover { background: #f0f2f7; color: #1e2a3a; }
            .tab-btn.active { background: #4d90fe; color: #fff; box-shadow: 0 2px 6px rgba(77,144,254,0.3); }
            .tab-content { display: none; }
            .tab-content.active { display: block; animation: fadeIn 0.3s ease; }
            @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

            /* TOOLBAR & BUTTONS */
            .toolbar { display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; margin-bottom: 20px; gap: 16px; }
            .toolbar .search-box { display: flex; align-items: center; background: #fff; border-radius: 8px; padding: 0 16px; border: 1px solid #e9edf4; flex: 1 1 300px; box-shadow: inset 0 1px 3px rgba(0,0,0,0.02); }
            .toolbar .search-box i { color: #8aa3c0; margin-right: 10px; }
            .toolbar .search-box input { border: none; padding: 12px 0; font-size: 14px; width: 100%; outline: none; background: transparent; }
            .toolbar .actions { display: flex; gap: 12px; flex-wrap: wrap; }

            .btn { padding: 8px 16px; border: none; border-radius: 8px; font-weight: 500; font-size: 13px; cursor: pointer; display: inline-flex; align-items: center; justify-content: center; gap: 8px; transition: 0.2s; text-decoration: none; }
            .btn-primary { background: #4d90fe; color: #fff; }
            .btn-primary:hover { background: #3a7bd5; }
            .btn-success { background: #6fcf97; color: #fff; }
            .btn-success:hover { background: #52b381; }
            .btn-warning { background: #f39c12; color: #fff; }
            .btn-warning:hover { background: #d68910; }
            .btn-danger { background: #e74c3c; color: #fff; }
            .btn-danger:hover { background: #c0392b; }
            .btn-outline { background: transparent; color: #4a5b6e; border: 1px solid #d0d8e3; }
            .btn-outline:hover { background: #e9edf4; }
            .btn-outline-danger { background: transparent; color: #e74c3c; border: 1px solid #e74c3c; }
            .btn-outline-danger:hover { background: #fceceb; }
            .btn-sm { padding: 6px 12px; font-size: 12px; }

            /* TABLE */
            .table-container { background: #fff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.03); overflow-x: auto; padding: 0; border: 1px solid #e9edf4; }
            .data-table { width: 100%; border-collapse: collapse; font-size: 14px; min-width: 600px; }
            .data-table th { text-align: left; padding: 16px; color: #4a5b6e; font-weight: 600; border-bottom: 2px solid #e9edf4; background: #fcfdfe; white-space: nowrap; font-size: 13px; text-transform: uppercase; letter-spacing: 0.5px; }
            .data-table td { padding: 14px 16px; border-bottom: 1px solid #f0f2f7; vertical-align: middle; }
            .data-table tr:last-child td { border-bottom: none; }
            .data-table tr:hover td { background: #f8faff; }
            .badge-status { display: inline-block; padding: 5px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; white-space: nowrap; }
            .badge-status.available { background: #d5f5e3; color: #1e8449; }
            .badge-status.inuse { background: #fdebd0; color: #a04000; }
            .badge-status.maintenance { background: #fadbd8; color: #922b21; }
            .badge-status.neutral { background: #e6f0ff; color: #2f6fd6; }

            /* MODALS */
            .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.4); z-index: 9999; justify-content: center; align-items: center; backdrop-filter: blur(3px); }
            .modal-overlay.active { display: flex; }
            .modal { background: #fff; border-radius: 16px; width: 95%; max-width: 700px; max-height: 90vh; overflow-y: auto; padding: 32px; box-shadow: 0 20px 60px rgba(0,0,0,0.2); animation: fadeInUp 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
            .modal.large { max-width: 1000px; }
            @keyframes fadeInUp { from { opacity: 0; transform: translateY(30px) scale(0.95); } to { opacity: 1; transform: translateY(0) scale(1); } }
            .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid #e9edf4; }
            .modal-header h2 { font-size: 20px; font-weight: 600; color: #1e2a3a; margin: 0; display: flex; align-items: center; gap: 10px; }
            .modal-header .close-modal { background: none; border: none; font-size: 28px; cursor: pointer; color: #8aa3c0; transition: color 0.2s; padding: 0; line-height: 1; }
            .modal-header .close-modal:hover { color: #e74c3c; }
            .form-group { margin-bottom: 18px; }
            .form-group label { display: block; font-weight: 500; margin-bottom: 8px; color: #4a5b6e; font-size: 13px; }
            .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 10px 14px; border: 1px solid #d0d8e3; border-radius: 8px; font-size: 14px; transition: all 0.2s; background: #fff; font-family: inherit; }
            .form-group input:focus, .form-group select:focus, .form-group textarea:focus { border-color: #4d90fe; outline: none; box-shadow: 0 0 0 3px rgba(77,144,254,0.1); }
            .form-group input[readonly] { background: #f0f2f7; color: #6f8fb0; cursor: not-allowed; }
            .form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px; border-top: 1px solid #e9edf4; padding-top: 20px; }
            .form-row { display: flex; flex-wrap: wrap; gap: 16px; }
            .form-row .form-group { flex: 1 1 calc(50% - 8px); min-width: 200px; }
            .form-hint { font-size: 12px; color: #8aa3c0; margin-top: 6px; }
            .form-error { display: none; background: #fceceb; color: #c0392b; padding: 10px 14px; border-radius: 8px; font-size: 13px; margin-bottom: 16px; border: 1px solid #f5c6c1; }
            .form-error.active { display: block; }
            .form-section-title { font-size: 13px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; color: #4d90fe; margin: 22px 0 12px; padding-top: 14px; border-top: 1px dashed #e9edf4; }
            .form-section-title:first-of-type { margin-top: 0; padding-top: 0; border-top: none; }

            /* THÔNG TIN TRANG TRẠI - CARD */
            .info-card { background: #fff; border-radius: 12px; border: 1px solid #e9edf4; box-shadow: 0 4px 12px rgba(0,0,0,0.03); padding: 28px 32px; }
            .info-card-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; gap: 16px; flex-wrap: wrap; }
            .info-card-header h2 { font-size: 20px; color: #1e2a3a; display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
            .info-card-header p { font-size: 13px; color: #8aa3c0; margin-top: 4px; }
            .info-logo { width: 64px; height: 64px; border-radius: 12px; object-fit: cover; border: 1px solid #e9edf4; flex-shrink: 0; }
            .info-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 20px 28px; }
            .info-row .info-label { font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; color: #8aa3c0; font-weight: 600; margin-bottom: 6px; }
            .info-row .info-value { font-size: 15px; color: #1e2a3a; font-weight: 500; }
            .info-row.full { grid-column: 1 / -1; }
            .info-row .info-value.muted { color: #a9b8c9; font-weight: 400; font-style: italic; }
            .map-image { width: 100%; max-height: 320px; object-fit: cover; border-radius: 10px; border: 1px solid #e9edf4; margin-top: 8px; }
            .gps-map-mini { height: 200px; border-radius: 10px; margin-top: 8px; }

            /* KHU VỰC - GRID CARD */
            .zone-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; }
            .zone-card { background: #fff; border-radius: 14px; border: 1px solid #e9edf4; box-shadow: 0 4px 12px rgba(0,0,0,0.03); padding: 22px; transition: transform 0.2s, box-shadow 0.2s; display: flex; flex-direction: column; gap: 14px; }
            .zone-card:hover { transform: translateY(-4px); box-shadow: 0 10px 24px rgba(0,0,0,0.07); }
            .zone-card-top { display: flex; align-items: flex-start; justify-content: space-between; gap: 10px; }
            .zone-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0; }
            .zone-icon.trong { background: #e8f8f0; color: #27ae60; }
            .zone-icon.kho { background: #fef5e6; color: #f39c12; }
            .zone-icon.thietbi { background: #e6f0ff; color: #4d90fe; }
            .zone-icon.thuhoach { background: #fdf0e6; color: #d0672a; }
            .zone-icon.khac { background: #f0f0f5; color: #7a7a9d; }
            .zone-name { font-size: 16px; font-weight: 600; color: #1e2a3a; line-height: 1.3; }
            .zone-type-badge { display: inline-block; margin-top: 4px; font-size: 11px; font-weight: 600; padding: 3px 10px; border-radius: 20px; background: #f0f2f7; color: #4a5b6e; text-transform: uppercase; letter-spacing: 0.4px; }
            .zone-area-list { display: flex; justify-content: space-between; font-size: 12px; color: #6f8fb0; }
            .zone-area-list strong { color: #1e2a3a; font-size: 13px; }
            .area-bar { height: 8px; border-radius: 20px; background: #eef1f6; overflow: hidden; }
            .area-bar-fill { height: 100%; border-radius: 20px; background: linear-gradient(90deg, #4d90fe, #6fcf97); transition: width 0.4s ease; }
            .area-bar-fill.warn { background: linear-gradient(90deg, #f39c12, #e74c3c); }
            .area-bar-label { display: flex; justify-content: space-between; font-size: 11px; color: #8aa3c0; margin-top: 4px; }
            .zone-card-actions { display: flex; gap: 8px; margin-top: 4px; }
            .zone-card-actions .btn { flex: 1; }
            .zone-card-desc { font-size: 13px; color: #6f8fb0; line-height: 1.4; }

            .empty-state { text-align: center; padding: 60px 20px; color: #8aa3c0; background: #fff; border-radius: 12px; border: 1px dashed #d0d8e3; }
            .empty-state i { font-size: 42px; color: #c3d1e0; margin-bottom: 14px; display: block; }
            .empty-state p { font-size: 14px; margin-bottom: 18px; }

            /* Modal chi tiết khu vực */
            .kv-summary { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; background: #f8faff; border: 1px solid #e9edf4; border-radius: 10px; padding: 16px 18px; margin-bottom: 20px; }
            .kv-summary div h4 { font-size: 11px; text-transform: uppercase; color: #8aa3c0; margin-bottom: 4px; letter-spacing: 0.4px; }
            .kv-summary div p { font-size: 16px; font-weight: 700; color: #1e2a3a; }
            .donvi-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
            .donvi-toolbar h3 { font-size: 15px; color: #1e2a3a; }
            .actions-cell { display: flex; gap: 6px; justify-content: center; }
            .btn-icon { width: 32px; height: 32px; border-radius: 8px; border: 1px solid #d0d8e3; background: #fff; color: #4a5b6e; cursor: pointer; display: inline-flex; align-items: center; justify-content: center; transition: 0.2s; }
            .btn-icon:hover { background: #e9edf4; }
            .btn-icon.danger:hover { background: #fceceb; color: #e74c3c; border-color: #e74c3c; }

            /* UPLOAD ẢNH */
            .upload-box { border: 2px dashed #d0d8e3; border-radius: 10px; padding: 14px; text-align: center; cursor: pointer; transition: 0.2s; background: #fbfcfe; }
            .upload-box:hover { border-color: #4d90fe; background: #f4f8ff; }
            .upload-box input[type="file"] { display: none; }
            .upload-box i { font-size: 22px; color: #8aa3c0; margin-bottom: 6px; display: block; }
            .upload-preview { margin-top: 10px; display: none; }
            .upload-preview img { width: 100%; max-height: 180px; object-fit: cover; border-radius: 8px; border: 1px solid #e9edf4; }
            .upload-preview.active { display: block; }

            @media (max-width: 900px) {
                .info-grid { grid-template-columns: 1fr; }
            }
            @media (max-width: 768px) {
                .kv-summary { grid-template-columns: 1fr; }
            }
        </style>
    </head>
    <body>

        <%@ include file="/views/commons/sidebar.jsp" %>
        <div class="overlay" id="overlay"></div>

        <div class="main-content">
            <%@ include file="/views/commons/header.jsp" %>
            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    var pageTitle = document.getElementById('pageHeaderTitle');
                    if (pageTitle) pageTitle.innerHTML = 'Quản lý Trang trại <span>| Khu vực & Phân chia</span>';
                });
            </script>

            <section class="content">

                <!-- ===== DASHBOARD TỔNG QUAN DIỆN TÍCH ===== -->
                <div class="dashboard-cards">
                    <div class="stat-card">
                        <div class="stat-icon primary"><i class="fas fa-map"></i></div>
                        <div class="stat-details">
                            <h3>Tổng diện tích trang trại</h3>
                            <p id="dashTongDienTich">0 m²</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon warning"><i class="fas fa-object-group"></i></div>
                        <div class="stat-details">
                            <h3>Đã phân chia khu vực</h3>
                            <p id="dashDaPhanChia">0 m²</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon success"><i class="fas fa-border-none"></i></div>
                        <div class="stat-details">
                            <h3>Diện tích còn trống</h3>
                            <p id="dashConTrong">0 m²</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon danger"><i class="fas fa-layer-group"></i></div>
                        <div class="stat-details">
                            <h3>Số khu vực</h3>
                            <p id="dashSoKhuVuc">0</p>
                        </div>
                    </div>
                </div>

                <div class="tabs">
                    <button class="tab-btn active" data-tab="tabThongTin"><i class="fas fa-info-circle"></i> Thông tin trang trại</button>
                    <button class="tab-btn" data-tab="tabKhuVuc"><i class="fas fa-th-large"></i> Phân chia khu vực</button>
                </div>

                <!-- ===== TAB 1: THÔNG TIN TRANG TRẠI (UC-2.1) ===== -->
                <div id="tabThongTin" class="tab-content active">
                    <c:choose>
                        <c:when test="${not empty trangTrai}">
                            <div class="info-card" id="viewTrangTrai">
                                <div class="info-card-header">
                                    <div style="display:flex; gap:16px; align-items:center;">
                                        <c:if test="${not empty trangTrai.getLogo()}">
                                            <img class="info-logo" src="${pageContext.request.contextPath}/${trangTrai.getLogo()}" alt="Logo">
                                        </c:if>
                                        <div>
                                            <h2><i class="fas fa-seedling" style="color:#27ae60;"></i> ${fn:escapeXml(trangTrai.getTen_trang_trai())}
                                                <span class="badge-status ${trangTrai.getTrang_thai() eq 'Đang hoạt động' ? 'available' : 'inuse'}">${fn:escapeXml(trangTrai.getTrang_thai())}</span>
                                            </h2>
                                            <p>Hệ thống chỉ quản lý một trang trại duy nhất</p>
                                        </div>
                                    </div>
                                    <button class="btn btn-primary" onclick="openTrangTraiModal()"><i class="fas fa-pen"></i> Chỉnh sửa</button>
                                </div>
                                <div class="info-grid">
                                    <div class="info-row"><div class="info-label">Mã số thuế</div><div class="info-value">${fn:escapeXml(trangTrai.getMa_so_thue())}</div></div>
                                    <div class="info-row"><div class="info-label">Mã số vùng trồng</div><div class="info-value">${fn:escapeXml(trangTrai.getMa_so_vung_trong())}</div></div>
                                    <div class="info-row"><div class="info-label">Năm thành lập</div><div class="info-value">${not empty trangTrai.getNam_thanh_lap() ? trangTrai.getNam_thanh_lap() : '-'}</div></div>

                                    <div class="info-row"><div class="info-label">Người đại diện</div><div class="info-value">${not empty trangTrai.getNguoi_dai_dien() ? fn:escapeXml(trangTrai.getNguoi_dai_dien()) : '-'}</div></div>
                                    <div class="info-row"><div class="info-label">Chức vụ</div><div class="info-value">${not empty trangTrai.getChuc_vu_dai_dien() ? fn:escapeXml(trangTrai.getChuc_vu_dai_dien()) : '-'}</div></div>
                                    <div class="info-row"><div class="info-label">Trạng thái</div><div class="info-value">${fn:escapeXml(trangTrai.getTrang_thai())}</div></div>

                                    <div class="info-row"><div class="info-label">Email</div><div class="info-value">${fn:escapeXml(trangTrai.getEmail())}</div></div>
                                    <div class="info-row"><div class="info-label">Số điện thoại</div><div class="info-value">${fn:escapeXml(trangTrai.getSo_dien_thoai())}</div></div>
                                    <div class="info-row"><div class="info-label">Tổng diện tích</div><div class="info-value">${trangTrai.getDien_tich_tong()} m²</div></div>

                                    <div class="info-row full"><div class="info-label">Địa chỉ</div><div class="info-value">${fn:escapeXml(trangTrai.getDia_chi())}</div></div>

                                    <div class="info-row"><div class="info-label">Nông sản chủ lực</div><div class="info-value">${fn:escapeXml(trangTrai.getNong_san_chu_luc())}</div></div>
                                    <div class="info-row"><div class="info-label">Lĩnh vực hoạt động</div><div class="info-value">${fn:escapeXml(trangTrai.getLinh_vuc_hoat_dong())}</div></div>
                                    <div class="info-row"><div class="info-label">Chứng nhận</div><div class="info-value">${not empty trangTrai.getChung_nhan() ? fn:escapeXml(trangTrai.getChung_nhan()) : '-'}</div></div>

                                    <div class="info-row full">
                                        <div class="info-label">Mô tả</div>
                                        <c:choose>
                                            <c:when test="${not empty trangTrai.getMo_ta()}">
                                                <div class="info-value" style="font-weight:400;">${fn:escapeXml(trangTrai.getMo_ta())}</div>
                                            </c:when>
                                            <c:otherwise><div class="info-value muted">Chưa có mô tả</div></c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="info-row full">
                                        <div class="info-label">Toạ độ GPS</div>
                                        <div class="info-value">${fn:escapeXml(trangTrai.getToa_do_gps())}</div>
                                        <c:if test="${not empty trangTrai.getLat() and not empty trangTrai.getLng()}">
                                            <div id="viewGpsMap" class="gps-map-mini"
                                                 data-lat="${trangTrai.getLat()}" data-lng="${trangTrai.getLng()}"></div>
                                        </c:if>
                                    </div>

                                    <c:if test="${not empty trangTrai.getAnh_ban_do()}">
                                        <div class="info-row full">
                                            <div class="info-label">Ảnh bản đồ / sơ đồ bố trí trang trại</div>
                                            <img class="map-image" src="${pageContext.request.contextPath}/${trangTrai.getAnh_ban_do()}" alt="Bản đồ trang trại">
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <i class="fas fa-map-marked-alt"></i>
                                <p>Chưa có thông tin trang trại. Vui lòng khai báo thông tin trang trại để bắt đầu quản lý khu vực.</p>
                                <button class="btn btn-primary" onclick="openTrangTraiModal()"><i class="fas fa-plus"></i> Khai báo trang trại</button>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- ===== TAB 2: PHÂN CHIA KHU VỰC (UC-2.2 / UC-2.3) ===== -->
                <div id="tabKhuVuc" class="tab-content">
                    <div class="toolbar">
                        <div class="search-box">
                            <i class="fas fa-search"></i>
                            <input type="text" placeholder="Tìm kiếm khu vực..." id="searchKhuVuc" oninput="renderZoneGrid()">
                        </div>
                        <div class="actions">
                            <button class="btn btn-primary" onclick="openKhuVucModal()"><i class="fas fa-plus"></i> Thêm khu vực</button>
                        </div>
                    </div>
                    <div id="zoneGridWrapper"></div>
                </div>

            </section>
        </div>

        <!-- ==================== MODAL THÔNG TIN TRANG TRẠI (UC-2.1) ====================
             Form submit trực tiếp (multipart, không AJAX) vì có upload ảnh.
             Toàn bộ kiểm tra dữ liệu (bắt buộc, định dạng, diện tích...) do TrangTraiService
             xử lý ở server; nếu có lỗi, server forward lại trang này kèm thông báo lỗi. -->
        <div class="modal-overlay${not empty loiTrangTrai ? ' active' : ''}" id="modalTrangTrai">
            <div class="modal large">
                <div class="modal-header">
                    <h2><i class="fas fa-seedling" style="color:#27ae60;"></i> Thông Tin Trang Trại</h2>
                    <button type="button" class="close-modal" onclick="closeModal('modalTrangTrai')">&times;</button>
                </div>
                <c:if test="${not empty loiTrangTrai}">
                    <div class="form-error active">${fn:escapeXml(loiTrangTrai)}</div>
                </c:if>
                <form id="formTrangTrai" method="post" enctype="multipart/form-data"
                      action="${pageContext.request.contextPath}/trangtrai">
                    <input type="hidden" name="action" value="capNhatTrangTrai">

                    <div class="form-section-title">Thông tin chung</div>
                    <div class="form-row">
                        <div class="form-group" style="flex:2;">
                            <label>Tên trang trại <span style="color:#e74c3c;">*</span></label>
                            <input type="text" name="ten_trang_trai" required maxlength="255"
                                   value="${fn:escapeXml(trangTrai.getTen_trang_trai())}" placeholder="VD: Trang trại Sầu Riêng Đắk Lắk">
                        </div>
                        <div class="form-group">
                            <label>Trạng thái</label>
                            <select name="trang_thai">
                                <option value="Đang hoạt động" ${empty trangTrai or trangTrai.getTrang_thai() eq 'Đang hoạt động' ? 'selected' : ''}>Đang hoạt động</option>
                                <option value="Tạm ngừng hoạt động" ${trangTrai.getTrang_thai() eq 'Tạm ngừng hoạt động' ? 'selected' : ''}>Tạm ngừng hoạt động</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Mã số thuế <span style="color:#e74c3c;">*</span></label>
                            <input type="text" name="ma_so_thue" required maxlength="20" value="${fn:escapeXml(trangTrai.getMa_so_thue())}" placeholder="VD: 0601234567">
                        </div>
                        <div class="form-group">
                            <label>Mã số vùng trồng <span style="color:#e74c3c;">*</span></label>
                            <input type="text" name="ma_so_vung_trong" required maxlength="50" value="${fn:escapeXml(trangTrai.getMa_so_vung_trong())}" placeholder="VD: VN-DL-0001">
                        </div>
                        <div class="form-group">
                            <label>Năm thành lập</label>
                            <input type="number" name="nam_thanh_lap" min="1900" max="2100" value="${trangTrai.getNam_thanh_lap()}">
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Người đại diện</label>
                            <input type="text" name="nguoi_dai_dien" maxlength="150" value="${fn:escapeXml(trangTrai.getNguoi_dai_dien())}" placeholder="VD: Nguyễn Văn A">
                        </div>
                        <div class="form-group">
                            <label>Chức vụ đại diện</label>
                            <input type="text" name="chuc_vu_dai_dien" maxlength="150" value="${fn:escapeXml(trangTrai.getChuc_vu_dai_dien())}" placeholder="VD: Chủ trang trại">
                        </div>
                    </div>

                    <div class="form-section-title">Thông tin liên hệ</div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Email <span style="color:#e74c3c;">*</span></label>
                            <input type="email" name="email" required maxlength="150" value="${fn:escapeXml(trangTrai.getEmail())}" placeholder="VD: contact@trangtrai.vn">
                        </div>
                        <div class="form-group">
                            <label>Số điện thoại <span style="color:#e74c3c;">*</span></label>
                            <input type="text" name="so_dien_thoai" required maxlength="10" pattern="0[0-9]{9}"
                                   value="${fn:escapeXml(trangTrai.getSo_dien_thoai())}" placeholder="VD: 0912345678">
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Địa chỉ <span style="color:#e74c3c;">*</span></label>
                        <input type="text" name="dia_chi" required maxlength="255" value="${fn:escapeXml(trangTrai.getDia_chi())}" placeholder="VD: Xã Ea Kiết, Huyện Cư M'gar, Đắk Lắk">
                    </div>

                    <div class="form-group">
                        <label>Toạ độ GPS <span style="color:#e74c3c;">*</span> — bấm vào bản đồ để chọn vị trí</label>
                        <input type="text" id="ttToaDoGps" name="toa_do_gps" required readonly
                               value="${fn:escapeXml(trangTrai.getToa_do_gps())}" placeholder="Bấm vào bản đồ bên dưới để chọn">
                        <div id="ttMap" class="gps-map-mini" style="height:260px;"
                             data-lat="${empty trangTrai.getLat() ? '14.0583' : trangTrai.getLat()}"
                             data-lng="${empty trangTrai.getLng() ? '108.2772' : trangTrai.getLng()}"></div>
                    </div>

                    <div class="form-section-title">Hoạt động sản xuất</div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Nông sản chủ lực <span style="color:#e74c3c;">*</span></label>
                            <input type="text" name="nong_san_chu_luc" required maxlength="255" value="${fn:escapeXml(trangTrai.getNong_san_chu_luc())}" placeholder="VD: Sầu riêng">
                        </div>
                        <div class="form-group">
                            <label>Lĩnh vực hoạt động <span style="color:#e74c3c;">*</span></label>
                            <input type="text" name="linh_vuc_hoat_dong" required maxlength="255" value="${fn:escapeXml(trangTrai.getLinh_vuc_hoat_dong())}" placeholder="VD: Trồng trọt, sơ chế nông sản">
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Tổng diện tích (m²) <span style="color:#e74c3c;">*</span></label>
                            <input type="number" name="dien_tich_tong" step="0.01" min="0.01" required value="${trangTrai.getDien_tich_tong()}" placeholder="VD: 50000">
                            <div class="form-hint">Diện tích không được nhỏ hơn tổng diện tích các khu vực đã phân chia.</div>
                        </div>
                        <div class="form-group">
                            <label>Chứng nhận</label>
                            <input type="text" name="chung_nhan" maxlength="255" value="${fn:escapeXml(trangTrai.getChung_nhan())}" placeholder="VD: VietGAP, GlobalGAP">
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Mô tả</label>
                        <textarea name="mo_ta" rows="3" placeholder="Ghi chú thêm về trang trại (không bắt buộc)">${fn:escapeXml(trangTrai.getMo_ta())}</textarea>
                    </div>

                    <div class="form-section-title">Hình ảnh</div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Logo trang trại</label>
                            <label class="upload-box" for="fileLogo">
                                <i class="fas fa-image"></i>
                                <span>Bấm để chọn ảnh logo (không bắt buộc)</span>
                                <input type="file" id="fileLogo" name="logo" accept="image/*" onchange="xemTruocAnh(this, 'previewLogo')">
                            </label>
                            <div class="upload-preview${not empty trangTrai.getLogo() ? ' active' : ''}" id="previewLogo">
                                <c:choose>
                                    <c:when test="${not empty trangTrai.getLogo()}">
                                        <img src="${pageContext.request.contextPath}/${trangTrai.getLogo()}" alt="Xem trước logo">
                                    </c:when>
                                    <c:otherwise><img src="" alt="Xem trước logo"></c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="form-group">
                            <label>Ảnh bản đồ / sơ đồ bố trí trang trại</label>
                            <label class="upload-box" for="fileAnhBanDo">
                                <i class="fas fa-map-marked-alt"></i>
                                <span>Bấm để chọn ảnh bản đồ (không bắt buộc)</span>
                                <input type="file" id="fileAnhBanDo" name="anh_ban_do" accept="image/*" onchange="xemTruocAnh(this, 'previewAnhBanDo')">
                            </label>
                            <div class="upload-preview${not empty trangTrai.getAnh_ban_do() ? ' active' : ''}" id="previewAnhBanDo">
                                <c:choose>
                                    <c:when test="${not empty trangTrai.getAnh_ban_do()}">
                                        <img src="${pageContext.request.contextPath}/${trangTrai.getAnh_ban_do()}" alt="Xem trước ảnh bản đồ">
                                    </c:when>
                                    <c:otherwise><img src="" alt="Xem trước ảnh bản đồ"></c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="button" class="btn btn-outline" onclick="closeModal('modalTrangTrai')">Hủy</button>
                        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Lưu thông tin</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- ==================== MODAL THÊM/SỬA KHU VỰC (UC-2.2) ==================== -->
        <div class="modal-overlay" id="modalKhuVuc">
            <div class="modal">
                <div class="modal-header">
                    <h2 id="modalKhuVucTitle"><i class="fas fa-th-large" style="color:#4d90fe;"></i> Thêm Khu Vực Mới</h2>
                    <button class="close-modal" onclick="closeModal('modalKhuVuc')">&times;</button>
                </div>
                <div class="form-error" id="errKhuVuc"></div>
                <form id="formKhuVuc">
                    <input type="hidden" id="kvId" value="">
                    <div class="form-row">
                        <div class="form-group" style="flex: 2;">
                            <label>Tên khu vực <span style="color:#e74c3c;">*</span></label>
                            <input type="text" id="kvTen" required placeholder="VD: Khu trồng số 1">
                        </div>
                        <div class="form-group" style="flex: 1;">
                            <label>Loại khu vực <span style="color:#e74c3c;">*</span></label>
                            <select id="kvLoai" required>
                                <option value="Khu trồng">Khu trồng</option>
                                <option value="Khu kho">Khu kho</option>
                                <option value="Khu thiết bị">Khu thiết bị</option>
                                <option value="Khu thu hoạch">Khu thu hoạch</option>
                                <option value="Khác">Khác</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Diện tích khai thác (m²) <span style="color:#e74c3c;">*</span></label>
                            <input type="number" id="kvDienTichKhaiThac" step="0.01" min="0" required oninput="capNhatTongDienTichKhuVuc()">
                            <div class="form-hint">Phần diện tích dùng để phân chia lô/kệ/vị trí bên trong khu vực.</div>
                        </div>
                        <div class="form-group">
                            <label>Diện tích chiếm dụng (m²) <span style="color:#e74c3c;">*</span></label>
                            <input type="number" id="kvDienTichChiemDung" step="0.01" min="0" required oninput="capNhatTongDienTichKhuVuc()">
                            <div class="form-hint">Đường đi, hạ tầng, không dùng để khai thác.</div>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Tổng diện tích khu vực</label>
                        <input type="text" id="kvTongDienTich" readonly value="0 m²">
                        <div class="form-hint" id="kvConLaiHint"></div>
                    </div>
                    <div class="form-group">
                        <label>Mô tả</label>
                        <textarea id="kvMoTa" rows="2" placeholder="Ghi chú thêm về khu vực (không bắt buộc)"></textarea>
                    </div>
                    <div class="form-actions">
                        <button type="button" class="btn btn-outline" onclick="closeModal('modalKhuVuc')">Hủy</button>
                        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Lưu khu vực</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- ==================== MODAL CHI TIẾT KHU VỰC (UC-2.3) ==================== -->
        <div class="modal-overlay" id="modalChiTietKhuVuc">
            <div class="modal large">
                <div class="modal-header">
                    <h2 id="ctKhuVucTitle"><i class="fas fa-th-large" style="color:#4d90fe;"></i> Chi Tiết Khu Vực</h2>
                    <button class="close-modal" onclick="closeModal('modalChiTietKhuVuc')">&times;</button>
                </div>

                <div class="kv-summary">
                    <div><h4>Diện tích khai thác</h4><p id="ctKhaiThac">0 m²</p></div>
                    <div><h4>Đã phân chia đơn vị</h4><p id="ctDaPhanChia">0 m²</p></div>
                    <div><h4>Còn trống</h4><p id="ctConTrong">0 m²</p></div>
                </div>

                <div class="donvi-toolbar">
                    <h3 id="ctDonViHeading"><i class="fas fa-list"></i> Danh sách đơn vị quản lý</h3>
                    <div style="display:flex; gap:10px;">
                        <button class="btn btn-outline btn-sm" onclick="editKhuVucFromDetail()"><i class="fas fa-pen"></i> Sửa khu vực</button>
                        <button class="btn btn-outline-danger btn-sm" onclick="xoaKhuVucHienTai()"><i class="fas fa-trash"></i> Xoá khu vực</button>
                        <button class="btn btn-success btn-sm" onclick="openDonViModal()"><i class="fas fa-plus"></i> Thêm đơn vị</button>
                    </div>
                </div>

                <div class="table-container">
                    <table class="data-table" id="tableDonVi">
                        <thead>
                            <tr>
                                <th>Mã đơn vị</th>
                                <th>Tên đơn vị</th>
                                <th>Loại</th>
                                <th>Diện tích (m²)</th>
                                <th>Sức chứa</th>
                                <th>Trạng thái</th>
                                <th style="text-align:center;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyDonVi"></tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- ==================== MODAL THÊM/SỬA ĐƠN VỊ QUẢN LÝ (UC-2.3) ==================== -->
        <div class="modal-overlay" id="modalDonVi">
            <div class="modal">
                <div class="modal-header">
                    <h2 id="modalDonViTitle"><i class="fas fa-box" style="color:#6fcf97;"></i> Thêm Đơn Vị Quản Lý</h2>
                    <button class="close-modal" onclick="closeModal('modalDonVi')">&times;</button>
                </div>
                <div class="form-error" id="errDonVi"></div>
                <form id="formDonVi">
                    <input type="hidden" id="dvId" value="">
                    <input type="hidden" id="dvKhuVucId" value="">
                    <div class="form-row">
                        <div class="form-group">
                            <label>Mã đơn vị</label>
                            <input type="text" id="dvMa" placeholder="VD: LO-01">
                        </div>
                        <div class="form-group">
                            <label>Loại đơn vị <span style="color:#e74c3c;">*</span></label>
                            <select id="dvLoai" required>
                                <option value="Lô đất">Lô đất</option>
                                <option value="Kệ">Kệ</option>
                                <option value="Vị trí lưu trữ">Vị trí lưu trữ</option>
                                <option value="Khác">Khác</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Tên đơn vị <span style="color:#e74c3c;">*</span></label>
                        <input type="text" id="dvTen" required placeholder="VD: Lô A1">
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Diện tích (m²) <span style="color:#e74c3c;">*</span></label>
                            <input type="number" id="dvDienTich" step="0.01" min="0.01" required>
                            <div class="form-hint" id="dvDienTichHint">Không được vượt diện tích khai thác còn lại của khu vực.</div>
                        </div>
                        <div class="form-group">
                            <label>Sức chứa</label>
                            <input type="number" id="dvSucChua" step="0.01" min="0" placeholder="Áp dụng cho kệ / vị trí lưu trữ">
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Trạng thái</label>
                        <select id="dvTrangThai">
                            <option value="Đang hoạt động">Đang hoạt động</option>
                            <option value="Ngừng sử dụng">Ngừng sử dụng</option>
                            <option value="Bảo trì">Bảo trì</option>
                        </select>
                    </div>
                    <div class="form-actions">
                        <button type="button" class="btn btn-outline" onclick="closeModal('modalDonVi')">Hủy</button>
                        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Lưu đơn vị</button>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
        <script>
            const contextPath = '${pageContext.request.contextPath}';

            // ===== DỮ LIỆU TỪ SERVER (JSTL) - chỉ dùng để hiển thị dashboard/khu vực, KHÔNG chứa logic nghiệp vụ =====
            const trangTrai = <c:choose><c:when test="${not empty trangTrai}">{
                id: ${trangTrai.getId()},
                dienTichTong: ${trangTrai.getDien_tich_tong()}
            }</c:when><c:otherwise>null</c:otherwise></c:choose>;

            let khuVucList = [
            <c:forEach items="${danhSachKhuVuc}" var="kv" varStatus="loop">
                {
                    id: ${kv.getId()},
                    tenKhuVuc: "${fn:escapeXml(kv.getTen_khu_vuc())}",
                    loaiKhuVuc: "${fn:escapeXml(kv.getLoai_khu_vuc())}",
                    dienTichKhaiThac: ${kv.getDien_tich_khai_thac()},
                    dienTichChiemDung: ${kv.getDien_tich_chiem_dung()},
                    dienTichKhuVuc: ${kv.getDien_tich_khu_vuc()},
                    moTa: "${fn:escapeXml(kv.getMo_ta())}"
                }${!loop.last ? ',' : ''}
            </c:forEach>
            ];

            let donViList = [
            <c:forEach items="${danhSachDonVi}" var="dv" varStatus="loop">
                {
                    id: ${dv.getId()},
                    khuVucId: ${dv.getKhu_vuc_id()},
                    loaiDonVi: "${fn:escapeXml(dv.getLoai_don_vi())}",
                    maDonVi: "${fn:escapeXml(dv.getMa_don_vi())}",
                    tenDonVi: "${fn:escapeXml(dv.getTen_don_vi())}",
                    dienTich: ${dv.getDien_tich()},
                    sucChua: ${dv.getSuc_chua()},
                    trangThai: "${fn:escapeXml(dv.getTrang_thai())}"
                }${!loop.last ? ',' : ''}
            </c:forEach>
            ];

            let currentKhuVucId = null;

            // ===== ICON / MÀU THEO LOẠI KHU VỰC =====
            function zoneIconInfo(loai) {
                switch (loai) {
                    case 'Khu trồng': return {cls: 'trong', icon: 'fa-seedling'};
                    case 'Khu kho': return {cls: 'kho', icon: 'fa-warehouse'};
                    case 'Khu thiết bị': return {cls: 'thietbi', icon: 'fa-tractor'};
                    case 'Khu thu hoạch': return {cls: 'thuhoach', icon: 'fa-shopping-basket'};
                    default: return {cls: 'khac', icon: 'fa-layer-group'};
                }
            }

            function dienTichDaPhanChiaKhuVuc(khuVucId) {
                return donViList.filter(d => d.khuVucId === khuVucId).reduce((s, d) => s + d.dienTich, 0);
            }

            // ===== DASHBOARD TỔNG QUAN (chỉ hiển thị, không phải nghiệp vụ) =====
            function renderDashboard() {
                const tongDienTich = trangTrai ? trangTrai.dienTichTong : 0;
                const daPhanChia = khuVucList.reduce((s, k) => s + k.dienTichKhuVuc, 0);
                const conTrong = Math.max(0, tongDienTich - daPhanChia);
                document.getElementById('dashTongDienTich').textContent = tongDienTich.toLocaleString() + ' m²';
                document.getElementById('dashDaPhanChia').textContent = daPhanChia.toLocaleString() + ' m²';
                document.getElementById('dashConTrong').textContent = conTrong.toLocaleString() + ' m²';
                document.getElementById('dashSoKhuVuc').textContent = khuVucList.length;
            }

            // ===== TAB SWITCH =====
            document.querySelectorAll('.tab-btn').forEach(function (btn) {
                btn.addEventListener('click', function () {
                    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
                    this.classList.add('active');
                    document.querySelectorAll('.tab-content').forEach(tc => tc.classList.remove('active'));
                    document.getElementById(this.getAttribute('data-tab')).classList.add('active');
                });
            });

            function closeModal(id) {
                document.getElementById(id).classList.remove('active');
            }
            function showError(id, msg) {
                const el = document.getElementById(id);
                el.textContent = msg;
                el.classList.add('active');
            }
            function hideError(id) {
                const el = document.getElementById(id);
                el.textContent = '';
                el.classList.remove('active');
            }

            // ===================== UC-2.1: THÔNG TIN TRANG TRẠI =====================
            // Form gửi thẳng lên servlet (không AJAX). JS ở đây chỉ để hiển thị modal
            // và bản đồ chọn GPS / xem trước ảnh - không xử lý logic nghiệp vụ nào.
            let ttMapInstance = null, ttMapMarker = null;

            function khoiTaoBanDoChonGPS() {
                if (ttMapInstance) { setTimeout(() => ttMapInstance.invalidateSize(), 150); return; }
                const el = document.getElementById('ttMap');
                if (!el || typeof L === 'undefined') return;
                const lat = parseFloat(el.dataset.lat), lng = parseFloat(el.dataset.lng);
                ttMapInstance = L.map('ttMap').setView([lat, lng], 15);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '&copy; OpenStreetMap'
                }).addTo(ttMapInstance);
                const gpsInput = document.getElementById('ttToaDoGps');
                if (gpsInput.value) {
                    ttMapMarker = L.marker([lat, lng]).addTo(ttMapInstance);
                }
                ttMapInstance.on('click', function (e) {
                    const la = e.latlng.lat.toFixed(6), ln = e.latlng.lng.toFixed(6);
                    gpsInput.value = la + ',' + ln;
                    if (ttMapMarker) ttMapMarker.setLatLng(e.latlng);
                    else ttMapMarker = L.marker(e.latlng).addTo(ttMapInstance);
                });
                setTimeout(() => ttMapInstance.invalidateSize(), 150);
            }

            function xemTruocAnh(input, previewId) {
                const wrap = document.getElementById(previewId);
                const img = wrap.querySelector('img');
                if (input.files && input.files[0]) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        img.src = e.target.result;
                        wrap.classList.add('active');
                    };
                    reader.readAsDataURL(input.files[0]);
                }
            }

            function openTrangTraiModal() {
                document.getElementById('modalTrangTrai').classList.add('active');
                khoiTaoBanDoChonGPS();
            }

            // Ảnh xem trước nhỏ (thông tin xem, chỉ hiển thị) cho toạ độ đã lưu
            function khoiTaoBanDoXemToaDo() {
                const el = document.getElementById('viewGpsMap');
                if (!el || typeof L === 'undefined') return;
                const lat = parseFloat(el.dataset.lat), lng = parseFloat(el.dataset.lng);
                const map = L.map('viewGpsMap', {zoomControl: false, dragging: false, scrollWheelZoom: false}).setView([lat, lng], 15);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {attribution: '&copy; OpenStreetMap'}).addTo(map);
                L.marker([lat, lng]).addTo(map);
            }

            // ===================== UC-2.2: PHÂN CHIA KHU VỰC =====================
            function capNhatTongDienTichKhuVuc() {
                const kt = parseFloat(document.getElementById('kvDienTichKhaiThac').value) || 0;
                const cd = parseFloat(document.getElementById('kvDienTichChiemDung').value) || 0;
                const tong = kt + cd;
                document.getElementById('kvTongDienTich').value = tong.toLocaleString() + ' m²';

                if (trangTrai) {
                    const editId = parseInt(document.getElementById('kvId').value) || -1;
                    const daPhanChiaKhac = khuVucList.filter(k => k.id !== editId).reduce((s, k) => s + k.dienTichKhuVuc, 0);
                    const conLai = trangTrai.dienTichTong - daPhanChiaKhac;
                    document.getElementById('kvConLaiHint').textContent = 'Diện tích còn lại của trang trại: ' + conLai.toLocaleString() + ' m²';
                }
            }

            function openKhuVucModal(id) {
                hideError('errKhuVuc');
                document.getElementById('formKhuVuc').reset();
                if (id) {
                    const kv = khuVucList.find(k => k.id === id);
                    document.getElementById('modalKhuVucTitle').innerHTML = '<i class="fas fa-pen" style="color:#4d90fe;"></i> Sửa Khu Vực';
                    document.getElementById('kvId').value = kv.id;
                    document.getElementById('kvTen').value = kv.tenKhuVuc;
                    document.getElementById('kvLoai').value = kv.loaiKhuVuc;
                    document.getElementById('kvDienTichKhaiThac').value = kv.dienTichKhaiThac;
                    document.getElementById('kvDienTichChiemDung').value = kv.dienTichChiemDung;
                    document.getElementById('kvMoTa').value = kv.moTa || '';
                } else {
                    document.getElementById('modalKhuVucTitle').innerHTML = '<i class="fas fa-th-large" style="color:#4d90fe;"></i> Thêm Khu Vực Mới';
                    document.getElementById('kvId').value = '';
                }
                capNhatTongDienTichKhuVuc();
                document.getElementById('modalKhuVuc').classList.add('active');
            }

            document.getElementById('formKhuVuc').addEventListener('submit', function (e) {
                e.preventDefault();
                hideError('errKhuVuc');
                const id = document.getElementById('kvId').value;
                const isUpdate = !!id;

                const params = new URLSearchParams();
                params.append('action', isUpdate ? 'suaKhuVuc' : 'themKhuVuc');
                if (isUpdate) params.append('id', id);
                params.append('ten_khu_vuc', document.getElementById('kvTen').value.trim());
                params.append('loai_khu_vuc', document.getElementById('kvLoai').value);
                params.append('dien_tich_khai_thac', document.getElementById('kvDienTichKhaiThac').value);
                params.append('dien_tich_chiem_dung', document.getElementById('kvDienTichChiemDung').value);
                params.append('mo_ta', document.getElementById('kvMoTa').value.trim());

                fetch(contextPath + '/trangtrai', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    body: params.toString()
                }).then(res => res.json()).then(data => {
                    if (data.success) {
                        alert('Lưu khu vực thành công!');
                        location.reload();
                    } else {
                        showError('errKhuVuc', data.error || 'Có lỗi xảy ra.');
                    }
                }).catch(err => showError('errKhuVuc', 'Không thể kết nối máy chủ: ' + err.message));
            });

            function xoaKhuVuc(id) {
                if (!confirm('Bạn có chắc chắn muốn xoá khu vực này?')) return;
                const params = new URLSearchParams();
                params.append('action', 'xoaKhuVuc');
                params.append('id', id);
                fetch(contextPath + '/trangtrai', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    body: params.toString()
                }).then(res => res.json()).then(data => {
                    if (data.success) {
                        alert('Đã xoá khu vực!');
                        location.reload();
                    } else {
                        alert('Lỗi: ' + (data.error || 'Không thể xoá.'));
                    }
                }).catch(err => alert('Không thể kết nối máy chủ: ' + err.message));
            }
            function xoaKhuVucHienTai() {
                if (currentKhuVucId != null) xoaKhuVuc(currentKhuVucId);
            }
            function editKhuVucFromDetail() {
                closeModal('modalChiTietKhuVuc');
                openKhuVucModal(currentKhuVucId);
            }

            // ===== RENDER LƯỚI KHU VỰC =====
            function renderZoneGrid() {
                const wrapper = document.getElementById('zoneGridWrapper');
                const keyword = (document.getElementById('searchKhuVuc').value || '').toLowerCase();
                const filtered = khuVucList.filter(k => k.tenKhuVuc.toLowerCase().includes(keyword) || k.loaiKhuVuc.toLowerCase().includes(keyword));

                if (khuVucList.length === 0) {
                    wrapper.innerHTML = '<div class="empty-state"><i class="fas fa-th-large"></i><p>Chưa có khu vực nào được phân chia. Hãy thêm khu vực đầu tiên cho trang trại.</p>' +
                        '<button class="btn btn-primary" onclick="openKhuVucModal()"><i class="fas fa-plus"></i> Thêm khu vực</button></div>';
                    return;
                }
                if (filtered.length === 0) {
                    wrapper.innerHTML = '<div class="empty-state"><i class="fas fa-search"></i><p>Không tìm thấy khu vực phù hợp.</p></div>';
                    return;
                }

                let html = '<div class="zone-grid">';
                filtered.forEach(function (kv) {
                    const icon = zoneIconInfo(kv.loaiKhuVuc);
                    const daPhanChia = dienTichDaPhanChiaKhuVuc(kv.id);
                    const percent = kv.dienTichKhaiThac > 0 ? Math.min(100, (daPhanChia / kv.dienTichKhaiThac) * 100) : 0;
                    const warnClass = percent >= 90 ? ' warn' : '';
                    const soDonVi = donViList.filter(d => d.khuVucId === kv.id).length;

                    html += '<div class="zone-card">';
                    html += '<div class="zone-card-top">';
                    html += '<div class="zone-icon ' + icon.cls + '"><i class="fas ' + icon.icon + '"></i></div>';
                    html += '<div style="flex:1;"><div class="zone-name">' + kv.tenKhuVuc + '</div><span class="zone-type-badge">' + kv.loaiKhuVuc + '</span></div>';
                    html += '</div>';

                    if (kv.moTa) html += '<div class="zone-card-desc">' + kv.moTa + '</div>';

                    html += '<div class="zone-area-list"><span>Khai thác: <strong>' + kv.dienTichKhaiThac.toLocaleString() + ' m²</strong></span><span>Chiếm dụng: <strong>' + kv.dienTichChiemDung.toLocaleString() + ' m²</strong></span></div>';

                    html += '<div><div class="area-bar"><div class="area-bar-fill' + warnClass + '" style="width:' + percent.toFixed(0) + '%;"></div></div>';
                    html += '<div class="area-bar-label"><span>Đã dùng ' + daPhanChia.toLocaleString() + ' / ' + kv.dienTichKhaiThac.toLocaleString() + ' m²</span><span>' + percent.toFixed(0) + '%</span></div></div>';

                    html += '<div class="zone-area-list"><span><i class="fas fa-cubes"></i> ' + soDonVi + ' đơn vị quản lý</span><span>Tổng: <strong>' + kv.dienTichKhuVuc.toLocaleString() + ' m²</strong></span></div>';

                    html += '<div class="zone-card-actions">';
                    html += '<button class="btn btn-primary btn-sm" onclick="openChiTietKhuVuc(' + kv.id + ')"><i class="fas fa-list"></i> Chi tiết</button>';
                    html += '<button class="btn btn-outline btn-sm" onclick="openKhuVucModal(' + kv.id + ')"><i class="fas fa-pen"></i></button>';
                    html += '<button class="btn btn-outline-danger btn-sm" onclick="xoaKhuVuc(' + kv.id + ')"><i class="fas fa-trash"></i></button>';
                    html += '</div>';

                    html += '</div>';
                });
                html += '</div>';
                wrapper.innerHTML = html;
            }

            // ===================== UC-2.3: QUẢN LÝ KHU VỰC CHI TIẾT =====================
            function openChiTietKhuVuc(khuVucId) {
                currentKhuVucId = khuVucId;
                const kv = khuVucList.find(k => k.id === khuVucId);
                if (!kv) return;

                const icon = zoneIconInfo(kv.loaiKhuVuc);
                document.getElementById('ctKhuVucTitle').innerHTML = '<i class="fas ' + icon.icon + '" style="color:#4d90fe;"></i> ' + kv.tenKhuVuc + ' <span style="font-size:13px; color:#8aa3c0; font-weight:400;">(' + kv.loaiKhuVuc + ')</span>';
                document.getElementById('ctDonViHeading').innerHTML = '<i class="fas fa-list"></i> Danh sách đơn vị quản lý' + (kv.loaiKhuVuc === 'Khu trồng' ? ' (Lô đất)' : ' (Kệ / Vị trí lưu trữ)');

                renderChiTietKhuVuc();
                document.getElementById('modalChiTietKhuVuc').classList.add('active');
            }

            function renderChiTietKhuVuc() {
                const kv = khuVucList.find(k => k.id === currentKhuVucId);
                if (!kv) return;
                const daPhanChia = dienTichDaPhanChiaKhuVuc(kv.id);
                const conTrong = Math.max(0, kv.dienTichKhaiThac - daPhanChia);

                document.getElementById('ctKhaiThac').textContent = kv.dienTichKhaiThac.toLocaleString() + ' m²';
                document.getElementById('ctDaPhanChia').textContent = daPhanChia.toLocaleString() + ' m²';
                document.getElementById('ctConTrong').textContent = conTrong.toLocaleString() + ' m²';

                const tbody = document.getElementById('tbodyDonVi');
                const list = donViList.filter(d => d.khuVucId === kv.id);

                if (list.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; color:#8aa3c0; padding: 30px;">Chưa có đơn vị nào trong khu vực này.</td></tr>';
                    return;
                }

                let html = '';
                list.forEach(function (dv) {
                    let statusClass = 'available';
                    if (dv.trangThai === 'Bảo trì') statusClass = 'maintenance';
                    else if (dv.trangThai === 'Ngừng sử dụng') statusClass = 'inuse';

                    html += '<tr>';
                    html += '<td><span style="color:#6f8fb0; font-size:13px;">' + (dv.maDonVi || '-') + '</span></td>';
                    html += '<td><strong style="color:#2c3e50;">' + dv.tenDonVi + '</strong></td>';
                    html += '<td><span class="badge-status neutral">' + dv.loaiDonVi + '</span></td>';
                    html += '<td>' + dv.dienTich.toLocaleString() + '</td>';
                    html += '<td>' + (dv.sucChua ? dv.sucChua.toLocaleString() : '-') + '</td>';
                    html += '<td><span class="badge-status ' + statusClass + '">' + dv.trangThai + '</span></td>';
                    html += '<td><div class="actions-cell">';
                    html += '<button class="btn-icon" title="Sửa" onclick="openDonViModal(' + dv.id + ')"><i class="fas fa-pen"></i></button>';
                    html += '<button class="btn-icon danger" title="Xoá" onclick="xoaDonVi(' + dv.id + ')"><i class="fas fa-trash"></i></button>';
                    html += '</div></td>';
                    html += '</tr>';
                });
                tbody.innerHTML = html;
            }

            function openDonViModal(id) {
                hideError('errDonVi');
                document.getElementById('formDonVi').reset();
                document.getElementById('dvKhuVucId').value = currentKhuVucId;

                const kv = khuVucList.find(k => k.id === currentKhuVucId);
                const daPhanChiaKhac = donViList.filter(d => d.khuVucId === currentKhuVucId && d.id !== id).reduce((s, d) => s + d.dienTich, 0);
                const conLai = kv ? (kv.dienTichKhaiThac - daPhanChiaKhac) : 0;
                document.getElementById('dvDienTichHint').textContent = 'Diện tích khai thác còn trống trong khu vực: ' + conLai.toLocaleString() + ' m²';

                if (id) {
                    const dv = donViList.find(d => d.id === id);
                    document.getElementById('modalDonViTitle').innerHTML = '<i class="fas fa-pen" style="color:#6fcf97;"></i> Sửa Đơn Vị Quản Lý';
                    document.getElementById('dvId').value = dv.id;
                    document.getElementById('dvMa').value = dv.maDonVi;
                    document.getElementById('dvLoai').value = dv.loaiDonVi;
                    document.getElementById('dvTen').value = dv.tenDonVi;
                    document.getElementById('dvDienTich').value = dv.dienTich;
                    document.getElementById('dvSucChua').value = dv.sucChua;
                    document.getElementById('dvTrangThai').value = dv.trangThai;
                } else {
                    document.getElementById('modalDonViTitle').innerHTML = '<i class="fas fa-box" style="color:#6fcf97;"></i> Thêm Đơn Vị Quản Lý';
                    document.getElementById('dvId').value = '';
                    if (kv && kv.loaiKhuVuc !== 'Khu trồng') document.getElementById('dvLoai').value = 'Kệ';
                    else document.getElementById('dvLoai').value = 'Lô đất';
                }
                document.getElementById('modalDonVi').classList.add('active');
            }

            document.getElementById('formDonVi').addEventListener('submit', function (e) {
                e.preventDefault();
                hideError('errDonVi');
                const id = document.getElementById('dvId').value;
                const isUpdate = !!id;

                const params = new URLSearchParams();
                params.append('action', isUpdate ? 'suaDonVi' : 'themDonVi');
                if (isUpdate) params.append('id', id);
                params.append('khu_vuc_id', document.getElementById('dvKhuVucId').value);
                params.append('loai_don_vi', document.getElementById('dvLoai').value);
                params.append('ma_don_vi', document.getElementById('dvMa').value.trim());
                params.append('ten_don_vi', document.getElementById('dvTen').value.trim());
                params.append('dien_tich', document.getElementById('dvDienTich').value);
                params.append('suc_chua', document.getElementById('dvSucChua').value || '0');
                params.append('trang_thai', document.getElementById('dvTrangThai').value);

                fetch(contextPath + '/trangtrai', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    body: params.toString()
                }).then(res => res.json()).then(data => {
                    if (data.success) {
                        alert('Lưu đơn vị quản lý thành công!');
                        location.reload();
                    } else {
                        showError('errDonVi', data.error || 'Có lỗi xảy ra.');
                    }
                }).catch(err => showError('errDonVi', 'Không thể kết nối máy chủ: ' + err.message));
            });

            function xoaDonVi(id) {
                if (!confirm('Bạn có chắc chắn muốn xoá đơn vị này?')) return;
                const params = new URLSearchParams();
                params.append('action', 'xoaDonVi');
                params.append('id', id);
                fetch(contextPath + '/trangtrai', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    body: params.toString()
                }).then(res => res.json()).then(data => {
                    if (data.success) {
                        alert('Đã xoá đơn vị!');
                        location.reload();
                    } else {
                        alert('Lỗi: ' + (data.error || 'Không thể xoá.'));
                    }
                }).catch(err => alert('Không thể kết nối máy chủ: ' + err.message));
            }

            // Sidebar mobile
            const menuToggle = document.getElementById('menuToggle');
            const sidebar = document.getElementById('sidebar');
            const overlay = document.getElementById('overlay');
            function toggleSidebar() {
                sidebar.classList.toggle('open');
                overlay.classList.toggle('active');
            }
            if (menuToggle) menuToggle.addEventListener('click', toggleSidebar);
            overlay.addEventListener('click', toggleSidebar);

            // INIT
            renderDashboard();
            renderZoneGrid();
            khoiTaoBanDoXemToaDo();
            <c:if test="${not empty loiTrangTrai}">khoiTaoBanDoChonGPS();</c:if>
        </script>
    </body>
</html>
