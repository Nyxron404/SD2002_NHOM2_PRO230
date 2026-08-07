<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Thiết bị & Dụng cụ - Trang trại Sầu Riêng</title>
        <!-- Font Awesome & Google Font -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <style>
            /* BASE CSS */
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            body {
                font-family: 'Roboto', sans-serif;
                background: #f5f7fa;
                color: #333;
                display: flex;
                height: 100vh;
                overflow: hidden;
            }
            .main-content {
                flex: 1;
                display: flex;
                flex-direction: column;
                height: 100vh;
                overflow: hidden;
            }
            .content {
                flex: 1;
                padding: 28px 32px;
                overflow-y: auto;
                background: #f5f7fa;
            }
            .overlay {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0,0,0,0.3);
                z-index: 999;
            }
            .overlay.active {
                display: block;
            }

            /* DASHBOARD CARDS */
            .dashboard-cards {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                gap: 20px;
                margin-bottom: 24px;
            }
            .stat-card {
                background: #fff;
                border-radius: 12px;
                padding: 20px;
                display: flex;
                align-items: center;
                box-shadow: 0 4px 12px rgba(0,0,0,0.03);
                border: 1px solid #e9edf4;
                transition: transform 0.2s;
            }
            .stat-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 8px 16px rgba(0,0,0,0.06);
            }
            .stat-icon {
                width: 56px;
                height: 56px;
                border-radius: 12px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 24px;
                margin-right: 16px;
            }
            .stat-icon.primary {
                background: #e6f0ff;
                color: #4d90fe;
            }
            .stat-icon.warning {
                background: #fef5e6;
                color: #f39c12;
            }
            .stat-icon.success {
                background: #e8f8f0;
                color: #27ae60;
            }
            .stat-icon.danger {
                background: #fceceb;
                color: #e74c3c;
            }
            .stat-details h3 {
                font-size: 13px;
                color: #8aa3c0;
                font-weight: 500;
                margin-bottom: 4px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }
            .stat-details p {
                font-size: 26px;
                font-weight: 700;
                color: #1e2a3a;
                margin: 0;
            }

            /* TABS */
            .tabs {
                display: flex;
                gap: 4px;
                margin-bottom: 24px;
                background: #fff;
                border-radius: 12px;
                padding: 4px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.04);
                border: 1px solid #e9edf4;
                width: fit-content;
            }
            .tab-btn {
                padding: 10px 24px;
                border: none;
                background: transparent;
                border-radius: 8px;
                font-weight: 500;
                font-size: 14px;
                color: #6f8fb0;
                cursor: pointer;
                transition: all 0.2s;
                display: flex;
                align-items: center;
                gap: 8px;
            }
            .tab-btn:hover {
                background: #f0f2f7;
                color: #1e2a3a;
            }
            .tab-btn.active {
                background: #4d90fe;
                color: #fff;
                box-shadow: 0 2px 6px rgba(77,144,254,0.3);
            }
            .tab-content {
                display: none;
            }
            .tab-content.active {
                display: block;
                animation: fadeIn 0.3s ease;
            }
            @keyframes fadeIn {
                from {
                    opacity: 0;
                    transform: translateY(10px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            /* TOOLBAR & BUTTONS */
            .toolbar {
                display: flex;
                flex-wrap: wrap;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 20px;
                gap: 16px;
            }
            .toolbar .search-box {
                display: flex;
                align-items: center;
                background: #fff;
                border-radius: 8px;
                padding: 0 16px;
                border: 1px solid #e9edf4;
                flex: 1 1 300px;
                box-shadow: inset 0 1px 3px rgba(0,0,0,0.02);
            }
            .toolbar .search-box i {
                color: #8aa3c0;
                margin-right: 10px;
            }
            .toolbar .search-box input {
                border: none;
                padding: 12px 0;
                font-size: 14px;
                width: 100%;
                outline: none;
                background: transparent;
            }
            .toolbar .actions {
                display: flex;
                gap: 12px;
                flex-wrap: wrap;
            }

            .btn {
                padding: 8px 16px;
                border: none;
                border-radius: 8px;
                font-weight: 500;
                font-size: 13px;
                cursor: pointer;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
                transition: 0.2s;
                text-decoration: none;
            }
            .btn-primary {
                background: #4d90fe;
                color: #fff;
            }
            .btn-primary:hover {
                background: #3a7bd5;
            }
            .btn-success {
                background: #6fcf97;
                color: #fff;
            }
            .btn-success:hover {
                background: #52b381;
            }
            .btn-warning {
                background: #f39c12;
                color: #fff;
            }
            .btn-warning:hover {
                background: #d68910;
            }
            .btn-danger {
                background: #e74c3c;
                color: #fff;
            }
            .btn-danger:hover {
                background: #c0392b;
            }
            .btn-outline {
                background: transparent;
                color: #4a5b6e;
                border: 1px solid #d0d8e3;
            }
            .btn-outline:hover {
                background: #e9edf4;
            }
            .btn-outline-danger {
                background: transparent;
                color: #e74c3c;
                border: 1px solid #e74c3c;
            }
            .btn-outline-danger:hover {
                background: #fceceb;
            }

            /* TABLE */
            .table-container {
                background: #fff;
                border-radius: 12px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.03);
                overflow-x: auto;
                padding: 0;
                border: 1px solid #e9edf4;
            }
            .data-table {
                width: 100%;
                border-collapse: collapse;
                font-size: 14px;
                min-width: 800px;
            }
            .data-table th {
                text-align: left;
                padding: 16px;
                color: #4a5b6e;
                font-weight: 600;
                border-bottom: 2px solid #e9edf4;
                background: #fcfdfe;
                white-space: nowrap;
                font-size: 13px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }
            .data-table td {
                padding: 14px 16px;
                border-bottom: 1px solid #f0f2f7;
                vertical-align: middle;
            }
            .data-table tr:last-child td {
                border-bottom: none;
            }
            .data-table tr:hover td {
                background: #f8faff;
            }
            .badge-status {
                display: inline-block;
                padding: 5px 12px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 500;
            }
            .badge-status.available {
                background: #d5f5e3;
                color: #1e8449;
            }
            .badge-status.inuse {
                background: #fdebd0;
                color: #a04000;
            }
            .badge-status.maintenance {
                background: #fadbd8;
                color: #922b21;
            }

            .details-row-dc td {
                background-color: #f8faff !important;
                border-bottom: 2px solid #e9edf4;
                padding: 0 !important;
            }

            /* MODALS */
            .modal-overlay {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0,0,0,0.4);
                z-index: 9999;
                justify-content: center;
                align-items: center;
                backdrop-filter: blur(3px);
            }
            .modal-overlay.active {
                display: flex;
            }
            .modal {
                background: #fff;
                border-radius: 16px;
                width: 95%;
                max-width: 700px;
                max-height: 90vh;
                overflow-y: auto;
                padding: 32px;
                box-shadow: 0 20px 60px rgba(0,0,0,0.2);
                animation: fadeInUp 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
            }
            .modal.large {
                max-width: 1100px;
            }
            @keyframes fadeInUp {
                from {
                    opacity: 0;
                    transform: translateY(30px) scale(0.95);
                }
                to {
                    opacity: 1;
                    transform: translateY(0) scale(1);
                }
            }
            .modal-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 24px;
                padding-bottom: 16px;
                border-bottom: 1px solid #e9edf4;
            }
            .modal-header h2 {
                font-size: 20px;
                font-weight: 600;
                color: #1e2a3a;
                margin: 0;
                display: flex;
                align-items: center;
                gap: 10px;
            }
            .modal-header .close-modal {
                background: none;
                border: none;
                font-size: 28px;
                cursor: pointer;
                color: #8aa3c0;
                transition: color 0.2s;
                padding: 0;
                line-height: 1;
            }
            .modal-header .close-modal:hover {
                color: #e74c3c;
            }
            .form-group {
                margin-bottom: 18px;
            }
            .form-group label {
                display: block;
                font-weight: 500;
                margin-bottom: 8px;
                color: #4a5b6e;
                font-size: 13px;
            }
            .form-group input, .form-group select {
                width: 100%;
                padding: 10px 14px;
                border: 1px solid #d0d8e3;
                border-radius: 8px;
                font-size: 14px;
                transition: all 0.2s;
                background: #fff;
            }
            .form-group input:focus, .form-group select:focus {
                border-color: #4d90fe;
                outline: none;
                box-shadow: 0 0 0 3px rgba(77,144,254,0.1);
            }
            .form-actions {
                display: flex;
                justify-content: flex-end;
                gap: 12px;
                margin-top: 24px;
                border-top: 1px solid #e9edf4;
                padding-top: 20px;
            }
            .form-row {
                display: flex;
                flex-wrap: wrap;
                gap: 16px;
            }
            .form-row .form-group {
                flex: 1 1 calc(50% - 8px);
                min-width: 200px;
            }
            .detail-table {
                width: 100%;
                border-collapse: collapse;
                font-size: 14px;
                margin-top: 6px;
            }
            .detail-table th {
                background: #f0f2f7;
                padding: 10px 8px;
                text-align: left;
                font-weight: 500;
                color: #2c3e50;
                border-bottom: 1px solid #d0d8e3;
            }
            .detail-table td {
                padding: 8px 6px;
                border-bottom: 1px solid #e9edf4;
                vertical-align: middle;
            }
            .detail-table input, .detail-table select {
                width: 100%;
                padding: 6px 8px;
                border: 1px solid #d0d8e3;
                border-radius: 6px;
                font-size: 13px;
                background: #fff;
            }
            .detail-table .btn-remove-row {
                background: #e74c3c;
                color: #fff;
                border: none;
                border-radius: 6px;
                padding: 4px 10px;
                cursor: pointer;
                font-size: 13px;
            }
            .total-summary {
                display: flex;
                justify-content: flex-end;
                gap: 30px;
                margin-top: 16px;
                padding: 12px 16px;
                background: #f8faff;
                border-radius: 8px;
                border: 1px solid #e9edf4;
            }
            .total-summary span {
                font-weight: 500;
                color: #1e2a3a;
            }
            .total-summary .amount {
                font-weight: 700;
                color: #2c3e50;
                font-size: 16px;
            }
        </style>
    </head>
    <body>

        <!-- NHÚNG THEO ĐƯỜNG DẪN TUYỆT ĐỐI -->
        <%@ include file="/views/commons/sidebar.jsp" %>
        <div class="overlay" id="overlay"></div>

        <div class="main-content">
            <%@ include file="/views/commons/header.jsp" %>
            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    var pageTitle = document.getElementById('pageHeaderTitle');
                    if (pageTitle)
                        pageTitle.innerHTML = 'Tổng quan Bãi đỗ & Khu lưu trữ <span>| Dashboard</span>';
                });
            </script>

            <section class="content">

                <div class="dashboard-cards" id="dashCardsDungCu">
                    <div class="stat-card">
                        <div class="stat-icon primary"><i class="fas fa-toolbox"></i></div>
                        <div class="stat-details">
                            <h3>Tổng loại Dụng Cụ</h3>
                            <p id="dashTotalDungCu">0</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon danger"><i class="fas fa-exclamation-triangle"></i></div>
                        <div class="stat-details">
                            <h3>Dụng cụ sắp hết (Báo động)</h3>
                            <p id="dashWarningDungCu">0</p>
                        </div>
                    </div>
                </div>

                <div class="dashboard-cards" id="dashCardsThietBi" style="display: none;">
                    <div class="stat-card">
                        <div class="stat-icon success"><i class="fas fa-tractor"></i></div>
                        <div class="stat-details">
                            <h3>Thiết bị sẵn sàng</h3>
                            <p id="dashReadyThietBi">0</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon warning"><i class="fas fa-cogs"></i></div>
                        <div class="stat-details">
                            <h3>Thiết bị đang bảo trì / Sử dụng</h3>
                            <p id="dashInuseThietBi">0</p>
                        </div>
                    </div>
                </div>

                <div class="tabs">
                    <button class="tab-btn active" data-tab="tabDungCu" onclick="switchDashboard('dungcu')"><i class="fas fa-tools"></i> Dụng cụ cầm tay</button>
                    <button class="tab-btn" data-tab="tabThietBi" onclick="switchDashboard('thietbi')"><i class="fas fa-tractor"></i> Thiết bị máy móc lớn</button>
                </div>

                <!-- ===== TAB DỤNG CỤ ===== -->
                <div id="tabDungCu" class="tab-content active">
                    <div class="toolbar">
                        <div class="search-box">
                            <i class="fas fa-search"></i>
                            <input type="text" placeholder="Tìm kiếm dụng cụ..." id="searchDungCu" oninput="filterTable('dungcu')">
                        </div>
                        <div class="actions">
                            <button class="btn btn-primary" onclick="openKhaiBaoDungCuModal()"><i class="fas fa-plus"></i> Khai báo danh mục</button>
                            <button class="btn btn-success" id="btnNhapDungCuToolbar"><i class="fas fa-file-invoice"></i> Lập phiếu nhập khu</button>
                        </div>
                    </div>
                    <div class="table-container">
                        <table class="data-table" id="tableDungCu">
                            <thead>
                                <tr>
                                    <th>Mã ID</th>
                                    <th>Tên dụng cụ</th>
                                    <th>Đơn vị tính</th>
                                    <th>Giá bình quân (VNĐ)</th>
                                    <th>Tồn khu</th>
                                    <th>Cảnh báo (Min)</th>
                                    <th>Trạng thái tồn</th>
                                    <th style="text-align:center;">Chi tiết</th>
                                </tr>
                            </thead>
                            <tbody id="tbodyDungCu"></tbody>
                        </table>
                    </div>
                </div>

                <!-- ===== TAB THIẾT BỊ ===== -->
                <div id="tabThietBi" class="tab-content">
                    <div class="toolbar">
                        <div class="search-box">
                            <i class="fas fa-search"></i>
                            <input type="text" placeholder="Tìm kiếm thiết bị..." id="searchThietBi" oninput="filterTable('thietbi')">
                        </div>
                        <div class="actions">
                            <button class="btn btn-primary" id="btnAddThietBi" onclick="alert('Bản demo: Form thêm mới thiết bị.')"><i class="fas fa-plus"></i> Khai báo thiết bị mới</button>
                            <button class="btn btn-success" id="btnNhapThietBiToolbar" onclick="alert('Chưa làm phần thiết bị nhé!')"><i class="fas fa-file-invoice"></i> Lập phiếu nhập khu</button>
                        </div>
                    </div>
                    <div class="table-container">
                        <table class="data-table" id="tableThietBi">
                            <thead>
                                <tr>
                                    <th>Mã TB</th>
                                    <th>Tên thiết bị</th>
                                    <th>Bãi đỗ quy định</th>
                                    <th>Diện tích chiếm (m²)</th>
                                    <th>Trạng thái vận hành</th>
                                    <th style="text-align:center;">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody id="tbodyThietBi"></tbody>
                        </table>
                    </div>
                </div>

            </section>
        </div>

        <!-- ==================== MODAL KHAI BÁO DỤNG CỤ ==================== -->
        <div class="modal-overlay" id="modalKhaiBaoDungCu">
            <div class="modal">
                <div class="modal-header">
                    <h2><i class="fas fa-toolbox" style="color: #4d90fe;"></i> Khai Báo Danh Mục Dụng Cụ</h2>
                    <button class="close-modal" onclick="document.getElementById('modalKhaiBaoDungCu').classList.remove('active')">&times;</button>
                </div>
                <form id="formKhaiBaoDungCu">
                    <div class="form-row">
                        <div class="form-group" style="flex: 2;">
                            <label>Tên dụng cụ <span style="color:#e74c3c;">*</span></label>
                            <input type="text" id="kbTenDungCu" required placeholder="VD: Kéo cắt tỉa cành trên cao">
                        </div>
                        <div class="form-group" style="flex: 1;">
                            <label>Đơn vị tính <span style="color:#e74c3c;">*</span></label>
                            <select id="kbDonViTinh" required>
                                <option value="Cái">Cái</option>
                                <option value="Bộ">Bộ</option>
                                <option value="Bình">Bình</option>
                                <option value="Chiếc">Chiếc</option>
                                <option value="Đôi">Đôi</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group" style="flex: 1;">
                            <label>Khu vực lưu trữ mặc định</label>
                            <select id="kbKhuVucLuuTru">
                                <option value="3">Khu Dụng cụ (Cất đồ nhỏ)</option>
                            </select>
                        </div>
                        <div class="form-group" style="flex: 1;">
                            <label>Mức tồn kho tối thiểu (Cảnh báo) <span style="color:#e74c3c;">*</span></label>
                            <input type="number" id="kbTonKhoToiThieu" step="0.01" min="0" required placeholder="VD: 5">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group" style="flex: 1;">
                            <label>Giá bình quân (Để ước tính chi phí hao hụt) <span style="color:#e74c3c;">*</span></label>
                            <input type="number" id="kbGiaBinhQuan" step="1000" min="0" required placeholder="VD: 55000">
                        </div>
                        <div class="form-group" style="flex: 1;">
                            <label>Ngày tạo</label>
                            <!-- Ô Ngày tạo tự động lấy giờ hiện tại và Readonly -->
                            <input type="date" id="kbNgayTao" readonly style="background:#e9edf4; cursor:not-allowed;">
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="button" class="btn btn-outline" onclick="document.getElementById('modalKhaiBaoDungCu').classList.remove('active')">Hủy</button>
                        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Lưu danh mục</button>
                    </div>
                </form>
            </div>
        </div>


        <!-- ==================== MODAL GHI NHẬN HAO HỤT / THANH LÝ ==================== -->
        <div class="modal-overlay" id="modalHaoHutDungCu">
            <div class="modal">
                <div class="modal-header">
                    <h2><i class="fas fa-minus-circle" style="color: #e74c3c;"></i> Ghi Nhận Hao Hụt</h2>
                    <button class="close-modal" onclick="document.getElementById('modalHaoHutDungCu').classList.remove('active')">&times;</button>
                </div>
                <form id="formHaoHutDungCu">
                    <input type="hidden" id="hhDcId" value="">

                    <div class="form-row" style="background: #f8faff; padding: 12px 16px; border-radius: 8px; border: 1px solid #e9edf4; margin-bottom: 16px;">
                        <div class="form-group" style="flex: 2; margin-bottom: 0;">
                            <label>Tên dụng cụ</label>
                            <p id="hhDcName" style="font-weight:600; margin-top:4px; font-size:16px; color:#1e2a3a; margin-bottom: 0;"></p>
                        </div>
                        <div class="form-group" style="flex: 1; margin-bottom: 0;">
                            <label>Tồn khu hiện tại</label>
                            <p id="hhDcStock" style="font-weight:700; margin-top:4px; color:#1e8449; font-size:16px; margin-bottom: 0;"></p>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group" style="flex: 1;">
                            <label>Ngày ghi nhận <span style="color:#e74c3c;">*</span></label>
                            <input type="date" id="hhDcDate" required>
                        </div>
                        <div class="form-group" style="flex: 1;">
                            <label>Số lượng hỏng/mất <span style="color:#e74c3c;">*</span></label>
                            <input type="number" id="hhDcQty" step="0.01" min="0.01" required oninput="calcThietHai()">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group" style="flex: 1;">
                            <label>Lý do hao hụt / Ghi chú <span style="color:#e74c3c;">*</span></label>
                            <input type="text" id="hhDcReason" required placeholder="VD: Hỏng do sử dụng lâu ngày, gãy cán, thất lạc ngoài vườn...">
                        </div>
                    </div>

                    <!-- Bổ sung phần hiện Chi phí thiệt hại -->
                    <div style="margin-top: 10px; text-align: right; font-size: 15px;">
                        Ước tính thiệt hại tài sản: <strong id="hhDcThietHai" style="color: #e74c3c; font-size: 18px;">0</strong> VNĐ
                    </div>

                    <div class="form-actions">
                        <button type="button" class="btn btn-outline" onclick="document.getElementById('modalHaoHutDungCu').classList.remove('active')">Hủy</button>
                        <button type="submit" class="btn btn-danger"><i class="fas fa-check"></i> Xác nhận hao hụt</button>
                    </div>
                </form>
            </div>
        </div>


        <!-- ==================== MODAL LẬP PHIẾU NHẬP DỤNG CỤ ==================== -->
        <div class="modal-overlay" id="modalImportDungCu">
            <div class="modal large">
                <div class="modal-header">
                    <h2><i class="fas fa-file-invoice" style="color: #4d90fe;"></i> Lập Phiếu Nhập Dụng Cụ</h2>
                    <button class="close-modal" onclick="document.getElementById('modalImportDungCu').classList.remove('active')">&times;</button>
                </div>
                <form id="formImportDungCu" enctype="multipart/form-data">
                    <div style="background: #f8faff; padding: 20px; border-radius: 12px; margin-bottom: 20px; border: 1px solid #e9edf4;">
                        <h4 style="margin-bottom: 16px; color: #1e2a3a; font-size: 15px;"><i class="fas fa-receipt" style="color: #8aa3c0; margin-right: 6px;"></i> Thông tin chứng từ</h4>

                        <div class="form-row">
                            <div class="form-group"><label>Mã phiếu nhập <span style="color:#e74c3c;">*</span></label><input type="text" id="dcMaHoaDon" required></div>
                            <div class="form-group"><label>Loại phiếu nhập</label><input type="text" id="dcLoaiPhieu" value="Nhập Dụng Cụ" readonly style="background:#e9edf4; cursor:not-allowed; color: #6f8fb0; font-weight: 500;"></div>
                            <div class="form-group"><label>Số hóa đơn <span style="color:#e74c3c;">*</span></label><input type="text" id="dcSoHoaDon" required></div>
                        </div>

                        <div class="form-row">
                            <div class="form-group"><label>Mẫu số</label><input type="text" id="dcMauSo"></div>
                            <div class="form-group"><label>Ký hiệu</label><input type="text" id="dcKyHieu"></div>
                            <div class="form-group"><label>Ngày hóa đơn <span style="color:#e74c3c;">*</span></label><input type="date" id="dcNgayHoaDon" required></div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label>Nhà cung cấp <span style="color:#e74c3c;">*</span></label>
                                <select id="dcNhaCungCapId" required onchange="tuDongDienMSTDC()">
                                    <option value="">-- Chọn Nhà cung cấp --</option>
                                    <c:forEach items="${listNCC}" var="ncc">
                                        <option value="${ncc.getId()}" data-mst="${ncc.getMa_so_thue()}">${ncc.getTen_ncc()}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group"><label>Mã số thuế NCC</label><input type="text" id="dcMST" readonly style="background:#fcfdfe;"></div>
                        </div>

                        <div class="form-row">
                            <div class="form-group"><label>Người mua hàng <span style="color:#e74c3c;">*</span></label><input type="text" id="dcNguoiMua" required></div>
                            <div class="form-group"><label>Người bán hàng</label><input type="text" id="dcNguoiBan"></div>
                        </div>

                        <div class="form-row">
                            <div class="form-group" style="flex:2;">
                                <label>Upload Bản gốc hóa đơn (Ảnh/PDF) <span style="color:#e74c3c;">*</span></label>
                                <input type="file" id="dcAnhHoaDon" accept=".jpg, .jpeg, .png, .pdf" required style="padding: 6px 10px;">
                            </div>
                            <div class="form-group" style="flex:1;"><label>Ghi chú</label><input type="text" id="dcGhiChu" placeholder="Nhập ghi chú phiếu nhập..."></div>
                        </div>
                    </div>

                    <h4 style="margin-bottom: 12px; font-size: 15px; color: #1e2a3a;"><i class="fas fa-list-ul" style="color: #8aa3c0; margin-right: 6px;"></i> Chi tiết Dụng cụ nhập</h4>
                    <div style="overflow-x: auto; border: 1px solid #e9edf4; border-radius: 8px; border-bottom: none;">
                        <table class="detail-table" id="importDungCuTable" style="margin-top: 0;">
                            <thead>
                                <tr>
                                    <th style="width:30%;">Tên Dụng cụ</th>
                                    <th style="width:15%;">Số lượng</th>
                                    <th style="width:20%;">Đơn giá (VNĐ)</th>
                                    <th style="width:25%;">Thành tiền</th>
                                    <th style="width:10%; text-align: center;">Xóa</th>
                                </tr>
                            </thead>
                            <tbody id="importDungCuBody"></tbody>
                        </table>
                    </div>
                    <div style="margin-top: 12px;">
                        <button type="button" class="btn btn-outline btn-sm" id="addDcRowBtn" style="padding: 6px 12px; font-size: 13px;"><i class="fas fa-plus"></i> Thêm dòng dụng cụ</button>
                    </div>

                    <div class="total-summary" style="flex-wrap: wrap;">
                        <span>Tổng tiền hàng: <strong class="amount" id="dcTongTienHang">0</strong> VNĐ</span>
                        <span style="display: flex; align-items: center;">Thuế GTGT: <input type="number" id="dcTienThueGTGT" value="0" style="width:120px; margin:0 10px; padding:6px; font-weight: 600; text-align: right;" oninput="calcDcTotal()"> VNĐ</span>
                        <span>Tổng thanh toán: <strong class="amount" style="color:#e74c3c; font-size: 18px;" id="dcTongThanhToan">0</strong> VNĐ</span>
                    </div>

                    <div class="form-actions">
                        <button type="button" class="btn btn-outline" onclick="document.getElementById('modalImportDungCu').classList.remove('active')">Hủy</button>
                        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Xác nhận lưu phiếu</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- ==================== MODAL CHI TIẾT DỤNG CỤ ==================== -->
        <div class="modal-overlay" id="detailModalDC">
            <div class="modal" style="max-width: 800px; padding: 0;">
                <div class="modal-header" style="background: #4d90fe; padding: 20px 24px; border-radius: 16px 16px 0 0; margin-bottom: 0;">
                    <h2 style="color: #fff; font-size: 20px;"><i class="fas fa-toolbox" style="margin-right: 10px;"></i> Chi Tiết Dụng Cụ: <span id="detDcTitleName"></span></h2>
                    <button class="close-modal" style="color: #fff;" onclick="document.getElementById('detailModalDC').classList.remove('active')">&times;</button>
                </div>

                <div style="padding: 24px;">
                    <div style="display: flex; gap: 20px;">

                        <!-- Cột trái: Thông tin tổng quan -->
                        <div style="flex: 2; background: #f8faff; border: 1px solid #e9edf4; border-radius: 12px; padding: 20px;">
                            <h4 style="margin-bottom: 16px; color: #4a5b6e; text-transform: uppercase; font-size: 13px; letter-spacing: 0.5px;"><i class="fas fa-info-circle"></i> Thông số chi tiết</h4>
                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; font-size: 14px;">
                                <div><span style="color:#8aa3c0;">Mã Dụng Cụ:</span> <strong id="detDcId"></strong></div>
                                <div><span style="color:#8aa3c0;">Đơn Vị Tính:</span> <strong id="detDcUnit"></strong></div>
                                <div><span style="color:#8aa3c0;">Diện Tích Chiếm:</span> <strong id="detDcArea"></strong> m²</div>
                                <div><span style="color:#8aa3c0;">Giá Bình Quân:</span> <strong id="detDcPrice"></strong> VNĐ</div>
                                <div><span style="color:#8aa3c0;">Tổng Tồn Hiện Tại:</span> <strong id="detDcStock" style="color:#1e8449; font-size: 16px;"></strong></div>
                                <div><span style="color:#8aa3c0;">Mức Cảnh Báo:</span> <strong id="detDcMin" style="color:#e74c3c;"></strong></div>
                            </div>
                        </div>

                        <!-- Cột phải: Cụm Thao tác -->
                        <div style="flex: 1; display: flex; flex-direction: column; gap: 10px; justify-content: center;">
                            <button id="btnDetDcEdit" class="btn btn-outline" style="width: 100%; border-color:#4d90fe; color:#4d90fe; padding: 12px;"><i class="fas fa-edit"></i> Sửa Thông Tin</button>
                            <button id="btnDetDcImport" class="btn btn-success" style="width: 100%; padding: 12px;"><i class="fas fa-arrow-down"></i> Nhập Thêm</button>
                            <button id="btnDetDcHaoHut" class="btn btn-warning" style="width: 100%; padding: 12px; color: #fff;"><i class="fas fa-minus-circle"></i> Ghi Nhận Hao Hụt</button>
                            <button id="btnDetDcDelete" class="btn btn-outline-danger" style="width: 100%; padding: 12px;"><i class="fas fa-trash"></i> Xóa Dụng Cụ</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <script>
            // SỬ DỤNG DỮ LIỆU ĐỔ TỪ SERVLET CHO DỤNG CỤ
            let dungCuList = [
            <c:forEach items="${listDungCu}" var="dc" varStatus="loop">
            {
            id: ${dc.getId()},
                    maDungCu: '${dc.getMa_dung_cu()}',
                    tenDungCu: '${dc.getTen_dung_cu()}',
                    donViTinh: '${dc.getDon_vi_tinh()}',
                    dienTichChiemDung: ${dc.getDien_tich_chiem_dung()},
                    giaBinhQuan: ${dc.getGia_binh_quan()},
                    tonKhuHienTai: ${dc.getTon_kho_hien_tai()},
                    tonKhuToiThieu: ${dc.getTon_kho_toi_thieu()},
                    trangThai: '${dc.getTrang_thai()}'
            }${!loop.last ? ',' : ''}
            </c:forEach>
            ];

            let thietBiList = [
                {id: 1, maThietBi: 'TB-MC01', tenThietBi: 'Máy cày Kubota 45HP', dienTichChiemDung: 5.5, trangThai: 1, idKhuVuc: 4},
                {id: 2, maThietBi: 'TB-MC02', tenThietBi: 'Máy bay phun thuốc DJI', dienTichChiemDung: 2.0, trangThai: 2, idKhuVuc: 4},
                {id: 3, maThietBi: 'TB-B01', tenThietBi: 'Máy bơm công suất lớn', dienTichChiemDung: 1.5, trangThai: 1, idKhuVuc: 4}
            ];

            // HÀM UPDATE DASHBOARD CARDS
            function updateDashboardStats() {
                document.getElementById('dashTotalDungCu').textContent = dungCuList.length;
                let warningDcCount = dungCuList.filter(dc => dc.tonKhuHienTai <= dc.tonKhuToiThieu).length;
                document.getElementById('dashWarningDungCu').textContent = warningDcCount;
                if (warningDcCount > 0)
                    document.getElementById('dashWarningDungCu').style.color = '#e74c3c';
                else
                    document.getElementById('dashWarningDungCu').style.color = '#1e2a3a';

                let readyTbCount = thietBiList.filter(tb => tb.trangThai === 1).length;
                let inuseTbCount = thietBiList.filter(tb => tb.trangThai !== 1).length;
                document.getElementById('dashReadyThietBi').textContent = readyTbCount;
                document.getElementById('dashInuseThietBi').textContent = inuseTbCount;
            }

            function switchDashboard(type) {
                if (type === 'dungcu') {
                    document.getElementById('dashCardsDungCu').style.display = 'grid';
                    document.getElementById('dashCardsThietBi').style.display = 'none';
                } else {
                    document.getElementById('dashCardsDungCu').style.display = 'none';
                    document.getElementById('dashCardsThietBi').style.display = 'grid';
                }
            }

            document.querySelectorAll('.tab-btn').forEach(function (btn) {
                btn.addEventListener('click', function () {
                    document.querySelectorAll('.tab-btn').forEach(function (b) {
                        b.classList.remove('active');
                    });
                    this.classList.add('active');
                    document.querySelectorAll('.tab-content').forEach(function (tc) {
                        tc.classList.remove('active');
                    });
                    document.getElementById(this.getAttribute('data-tab')).classList.add('active');
                });
            });

            // ==========================================
            // RENDER BẢNG DỤNG CỤ (CÓ MENU SỔ XUỐNG GIỐNG VẬT TƯ)
            // ==========================================
            function renderDungCu() {
                const tbody = document.getElementById('tbodyDungCu');
                tbody.innerHTML = '';
                dungCuList.forEach(function (dc) {
                    const isLow = dc.tonKhuHienTai <= dc.tonKhuToiThieu;
                    const statusClass = isLow ? 'maintenance' : 'available';
                    const statusText = isLow ? 'Dưới định mức' : 'Đảm bảo';
                    const textColor = isLow ? '#e74c3c' : '#1e2a3a';
                    const fw = isLow ? '700' : '600';

                    let trHtml = "<tr class='main-row-dc'>";
                    trHtml += "<td><span style='color:#6f8fb0; font-size:13px;'>" + dc.maDungCu + "</span></td>";
                    trHtml += "<td><strong style='color:#2c3e50;'>" + dc.tenDungCu + "</strong></td>";
                    trHtml += "<td>" + dc.donViTinh + "</td>";
                    trHtml += "<td>" + dc.giaBinhQuan.toLocaleString() + "</td>";
                    trHtml += "<td><span style='color: " + textColor + "; font-weight: " + fw + "; font-size: 15px;'>" + dc.tonKhuHienTai + "</span></td>";
                    trHtml += "<td>" + dc.tonKhuToiThieu + "</td>";
                    trHtml += "<td><span class='badge-status " + statusClass + "'>" + statusText + "</span></td>";

                    // Nút Xem chi tiết kích hoạt Modal
                    trHtml += "<td style='text-align:center;'>";
                    trHtml += "<button class='btn btn-primary' style='padding: 6px 12px; font-size: 13px;' onclick='openDetailModalDC(" + dc.id + ")'><i class='fas fa-list'></i> Xem chi tiết</button>";
                    trHtml += "</td></tr>";

                    const trMain = document.createElement('tr');
                    trMain.className = 'main-row-dc';
                    trMain.innerHTML = trHtml;
                    tbody.appendChild(trMain);
                });
                updateDashboardStats();
            }

            // ===== RENDER BẢNG THIẾT BỊ =====
            function renderThietBi() {
                const tbody = document.getElementById('tbodyThietBi');
                tbody.innerHTML = '';
                thietBiList.forEach(function (tb) {
                    let statusClass = tb.trangThai === 1 ? 'available' : 'inuse';
                    let trHtml = "<tr>";
                    trHtml += "<td><span style='color:#6f8fb0; font-size:13px;'>" + tb.maThietBi + "</span></td>";
                    trHtml += "<td><strong style='color:#2c3e50;'>" + tb.tenThietBi + "</strong></td>";
                    trHtml += "<td>Bãi Thiết bị</td>";
                    trHtml += "<td>" + tb.dienTichChiemDung + " m²</td>";
                    trHtml += "<td><span class='badge-status " + statusClass + "'>" + (tb.trangThai === 1 ? "Sẵn sàng" : "Đang dùng") + "</span></td>";

                    trHtml += "<td><div class='actions-cell'>";
                    trHtml += "<button class='btn-action import btn-nhap-tb' title='Nhập bãi' onclick='alert(\"Chưa làm phần Thiết bị\")'><i class='fas fa-arrow-down'></i></button>";
                    trHtml += "</div></td></tr>";

                    const tr = document.createElement('tr');
                    tr.innerHTML = trHtml;
                    tbody.appendChild(tr);
                });
                updateDashboardStats();
            }

            // HÀM MỞ MODAL XEM CHI TIẾT DỤNG CỤ
            function openDetailModalDC(id) {
                // 1. Tìm thông tin dụng cụ
                const dc = dungCuList.find(function (item) {
                    return item.id === id;
                });
                if (!dc)
                    return;

                // 2. Điền thông tin tổng quan vào modal
                document.getElementById('detDcTitleName').textContent = dc.tenDungCu;
                document.getElementById('detDcId').textContent = dc.maDungCu;
                document.getElementById('detDcUnit').textContent = dc.donViTinh;
                document.getElementById('detDcArea').textContent = dc.dienTichChiemDung;
                document.getElementById('detDcPrice').textContent = dc.giaBinhQuan.toLocaleString();
                document.getElementById('detDcStock').textContent = dc.tonKhuHienTai;
                document.getElementById('detDcMin').textContent = dc.tonKhuToiThieu;

                // 3. Gắn sự kiện cho các nút thao tác
                document.getElementById('btnDetDcEdit').onclick = function () {
                    document.getElementById('detailModalDC').classList.remove('active');
                    alert("Chức năng sửa thông tin đang phát triển!");
                };
                document.getElementById('btnDetDcImport').onclick = function () {
                    document.getElementById('detailModalDC').classList.remove('active');
                    openImportDungCuModal(dc.id);
                };
                document.getElementById('btnDetDcHaoHut').onclick = function () {
                    document.getElementById('detailModalDC').classList.remove('active');
                    openHaoHutDungCuModal(dc.id);
                };
                document.getElementById('btnDetDcDelete').onclick = function () {
                    document.getElementById('detailModalDC').classList.remove('active');
                    alert("Chức năng xóa đang phát triển!");
                };

                // 4. Hiển thị Modal
                document.getElementById('detailModalDC').classList.add('active');
            }

            // ===== LOGIC FORM KHAI BÁO DỤNG CỤ =====
            function openKhaiBaoDungCuModal() {
                document.getElementById('formKhaiBaoDungCu').reset();
                // Lấy ngày hiện tại gán vào ô Ngày Tạo
                document.getElementById('kbNgayTao').value = new Date().toISOString().slice(0, 10);
                document.getElementById('modalKhaiBaoDungCu').classList.add('active');
            }

            document.getElementById('formKhaiBaoDungCu').addEventListener('submit', function (e) {
                e.preventDefault();
                let tenDungCu = document.getElementById('kbTenDungCu').value;
                let donViTinh = document.getElementById('kbDonViTinh').value;
                let tonKhoToiThieu = parseFloat(document.getElementById('kbTonKhoToiThieu').value);
                let giaBinhQuan = parseFloat(document.getElementById('kbGiaBinhQuan').value);

                dungCuList.push({
                    id: Date.now(),
                    maDungCu: 'DC' + Date.now().toString().slice(-3),
                    tenDungCu: tenDungCu,
                    donViTinh: donViTinh,
                    dienTichChiemDung: 0,
                    giaBinhQuan: giaBinhQuan,
                    tonKhuHienTai: 0,
                    tonKhuToiThieu: tonKhoToiThieu,
                    trangThai: 'Đang sử dụng'
                });

                document.getElementById('modalKhaiBaoDungCu').classList.remove('active');
                renderDungCu();
                alert("Khai báo dụng cụ mới thành công! (Dữ liệu tạm)");
            });

            // ===== LOGIC FORM NHẬP KHO DỤNG CỤ VỚI FETCH API THỰC TẾ =====
            let importDcRows = [];

            function addImportDcRow(dungCuId, soLuong, donGia) {
                importDcRows.push({id: Date.now() + Math.random(), dungCuId: dungCuId, soLuong: soLuong, donGia: donGia});
                renderImportDcRows();
            }

            function removeImportDcRow(rowId) {
                importDcRows = importDcRows.filter(function (r) {
                    return r.id !== rowId;
                });
                renderImportDcRows();
            }

            function renderImportDcRows() {
                const tbody = document.getElementById('importDungCuBody');
                tbody.innerHTML = '';

                importDcRows.forEach(function (row) {
                    let opts = "";
                    dungCuList.forEach(function (dc) {
                        let sel = (dc.id === row.dungCuId) ? "selected" : "";
                        opts += "<option value='" + dc.id + "' " + sel + ">" + dc.tenDungCu + "</option>";
                    });

                    let trHtml = "<tr>";
                    trHtml += "<td><select class='import-dc-select' data-rowid='" + row.id + "'>" + opts + "</select></td>";
                    trHtml += "<td><input type='number' class='import-dc-qty' data-rowid='" + row.id + "' value='" + row.soLuong + "' step='0.01' min='0.01'></td>";
                    trHtml += "<td><input type='number' class='import-dc-price' data-rowid='" + row.id + "' value='" + row.donGia + "' step='1000' min='0'></td>";
                    trHtml += "<td><strong class='import-dc-amount' style='color:#2c3e50; font-size:14px;'>" + (row.soLuong * row.donGia).toLocaleString() + "</strong></td>";
                    trHtml += "<td style='text-align:center;'><button type='button' class='btn-remove-row' onclick='removeImportDcRow(" + row.id + ")'><i class='fas fa-trash-alt'></i></button></td>";
                    trHtml += "</tr>";

                    const tr = document.createElement('tr');
                    tr.innerHTML = trHtml;
                    tbody.appendChild(tr);
                });

                document.querySelectorAll('.import-dc-select, .import-dc-qty, .import-dc-price').forEach(function (inp) {
                    inp.addEventListener('input', function () {
                        const rowId = parseFloat(this.getAttribute('data-rowid'));
                        const r = importDcRows.find(function (x) {
                            return x.id === rowId;
                        });
                        if (r) {
                            if (this.classList.contains('import-dc-select'))
                                r.dungCuId = parseInt(this.value);
                            if (this.classList.contains('import-dc-qty'))
                                r.soLuong = parseFloat(this.value) || 0;
                            if (this.classList.contains('import-dc-price'))
                                r.donGia = parseFloat(this.value) || 0;
                            this.closest('tr').querySelector('.import-dc-amount').textContent = (r.soLuong * r.donGia).toLocaleString();
                        }
                        calcDcTotal();
                    });
                });
                calcDcTotal();
            }

            function calcDcTotal() {
                let tongHang = 0;
                importDcRows.forEach(function (row) {
                    tongHang += (row.soLuong * row.donGia);
                });
                const thue = parseFloat(document.getElementById('dcTienThueGTGT').value) || 0;

                document.getElementById('dcTongTienHang').textContent = tongHang.toLocaleString();
                document.getElementById('dcTongThanhToan').textContent = (tongHang + thue).toLocaleString();
            }

            document.getElementById('addDcRowBtn').addEventListener('click', function () {
                if (dungCuList.length === 0) {
                    alert('Chưa có dụng cụ trong danh mục!');
                    return;
                }
                addImportDcRow(dungCuList[0].id, 1, 0);
            });

            function openImportDungCuModal(id) {
                importDcRows = [];
                document.getElementById('formImportDungCu').reset();
                document.getElementById('dcMaHoaDon').value = 'HD-DC' + Date.now().toString().slice(-5);
                document.getElementById('dcNgayHoaDon').value = new Date().toISOString().slice(0, 10);

                if (id)
                    addImportDcRow(id, 1, 0);
                else if (dungCuList.length > 0)
                    addImportDcRow(dungCuList[0].id, 1, 0);

                document.getElementById('modalImportDungCu').classList.add('active');
            }

            document.getElementById('btnNhapDungCuToolbar').addEventListener('click', function () {
                openImportDungCuModal(null);
            });

            // GỬI DỮ LIỆU NHẬP DỤNG CỤ LÊN SERVLET
            document.getElementById('formImportDungCu').addEventListener('submit', function (e) {
                e.preventDefault();
                if (importDcRows.length === 0) {
                    alert("Vui lòng thêm ít nhất một dòng dụng cụ!");
                    return;
                }

                let formData = new FormData();
                formData.append("action", "insertPhieuNhapDungCu");

                formData.append("ma_phieu_nhap", document.getElementById('dcMaHoaDon').value);
                formData.append("so_hoa_don", document.getElementById('dcSoHoaDon').value);
                formData.append("mau_so", document.getElementById('dcMauSo').value);
                formData.append("ky_hieu", document.getElementById('dcKyHieu').value);
                formData.append("ngay_hoa_don", document.getElementById('dcNgayHoaDon').value);
                formData.append("nha_cung_cap_id", document.getElementById('dcNhaCungCapId').value);
                formData.append("ma_so_thue_ncc", document.getElementById('dcMST').value);
                formData.append("nguoi_mua_hang", document.getElementById('dcNguoiMua').value);
                formData.append("nguoi_ban_hang", document.getElementById('dcNguoiBan').value);
                formData.append("ghi_chu", document.getElementById('dcGhiChu').value);

                let fileAnh = document.getElementById('dcAnhHoaDon').files[0];
                if (fileAnh)
                    formData.append("anh_hoa_don", fileAnh);

                formData.append("tong_tien_hang", document.getElementById('dcTongTienHang').innerText.replace(/,/g, ''));
                formData.append("tien_thue_gtgt", document.getElementById('dcTienThueGTGT').value);
                formData.append("tong_thanh_toan", document.getElementById('dcTongThanhToan').innerText.replace(/,/g, ''));

                importDcRows.forEach(row => {
                    formData.append("dung_cu_id[]", row.dungCuId);
                    formData.append("so_luong[]", row.soLuong);
                    formData.append("don_gia[]", row.donGia);
                });

                fetch('tbdc', {
                    method: 'POST',
                    body: formData
                })
                        .then(res => res.json())
                        .then(data => {
                            if (data.success) {
                                alert('Lập phiếu nhập dụng cụ thành công!');
                                document.getElementById('modalImportDungCu').classList.remove('active');
                                location.reload();
                            } else {
                                alert('Lỗi: ' + data.error);
                            }
                        })
                        .catch(err => {
                            alert('Không gửi được dữ liệu lên máy chủ: ' + err.message);
                        });
            });

            // ===== GHI NHẬN HAO HỤT / THANH LÝ =====
            function openHaoHutDungCuModal(id) {
                const dc = dungCuList.find(function (x) {
                    return x.id === id;
                });
                document.getElementById('hhDcId').value = dc.id;
                document.getElementById('hhDcName').textContent = dc.tenDungCu;
                document.getElementById('hhDcStock').textContent = dc.tonKhuHienTai + " " + dc.donViTinh;

                document.getElementById('formHaoHutDungCu').reset();
                document.getElementById('hhDcDate').value = new Date().toISOString().slice(0, 10);
                document.getElementById('hhDcThietHai').textContent = '0';
                document.getElementById('modalHaoHutDungCu').classList.add('active');
            }

            function calcThietHai() {
                const id = parseInt(document.getElementById('hhDcId').value);
                const qty = parseFloat(document.getElementById('hhDcQty').value) || 0;
                const dc = dungCuList.find(function (x) {
                    return x.id === id;
                });
                if (dc) {
                    const total = qty * dc.giaBinhQuan;
                    document.getElementById('hhDcThietHai').textContent = total.toLocaleString();
                }
            }

            document.getElementById('formHaoHutDungCu').addEventListener('submit', function (e) {
                e.preventDefault();
                const id = parseInt(document.getElementById('hhDcId').value);
                const qty = parseFloat(document.getElementById('hhDcQty').value);
                const reason = document.getElementById('hhDcReason').value;
                const dc = dungCuList.find(function (x) {
                    return x.id === id;
                });
                const chiphi = qty * dc.giaBinhQuan;

                if (qty > dc.tonKhuHienTai) {
                    alert("Lỗi: Số lượng báo hỏng/mất lớn hơn tồn kho thực tế!");
                    return;
                }

                let params = new URLSearchParams();
                params.append("action", "haoHutDungCu");
                params.append("id", id);
                params.append("qty", qty);
                params.append("chiphi", chiphi);
                params.append("reason", reason);

                fetch('tbdc', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    body: params.toString()
                })
                        .then(res => res.json())
                        .then(data => {
                            if (data.success) {
                                document.getElementById('modalHaoHutDungCu').classList.remove('active');
                                if (dc.tonKhuHienTai - qty < dc.tonKhuToiThieu) {
                                    alert("Đã ghi nhận hao hụt! CẢNH BÁO: Tồn kho hiện tại đã tụt xuống dưới mức tối thiểu. Vui lòng lên kế hoạch nhập thêm.");
                                } else {
                                    alert("Ghi nhận hao hụt / thanh lý dụng cụ thành công!");
                                }
                                location.reload();
                            } else {
                                alert("Lỗi khi ghi nhận: " + data.error);
                            }
                        })
                        .catch(err => {
                            console.error(err);
                            alert("Lỗi kết nối máy chủ!");
                        });
            });

            // Hàm tự động điền Mã số thuế khi chọn Nhà cung cấp
            function tuDongDienMSTDC() {
                var selectNCC = document.getElementById("dcNhaCungCapId");
                var selectedOption = selectNCC.options[selectNCC.selectedIndex];
                var mst = selectedOption.getAttribute("data-mst");
                var inputMST = document.getElementById("dcMST");
                if (inputMST)
                    inputMST.value = (mst && mst !== 'null') ? mst : "";
            }

            function filterTable(type) {
                const keyword = document.getElementById(type === 'dungcu' ? 'searchDungCu' : 'searchThietBi').value.toLowerCase();
                const rows = document.querySelectorAll(type === 'dungcu' ? '#tbodyDungCu tr.main-row-dc' : '#tbodyThietBi tr');

                rows.forEach(function (row) {
                    const isMatch = row.textContent.toLowerCase().includes(keyword);
                    row.style.display = isMatch ? '' : 'none';
                });
            }

            // Sidebar mobile
            const menuToggle = document.getElementById('menuToggle');
            const sidebar = document.getElementById('sidebar');
            const overlay = document.getElementById('overlay');
            function toggleSidebar() {
                sidebar.classList.toggle('open');
                overlay.classList.toggle('active');
            }
            if (menuToggle)
                menuToggle.addEventListener('click', toggleSidebar);
            overlay.addEventListener('click', toggleSidebar);

            // INIT
            renderDungCu();
            renderThietBi();
        </script>
    </body>
</html>