<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Canh tác - Trang trại Sầu Riêng</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <style>
        /* RESET & LAYOUT */
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Roboto', sans-serif; background: #f5f7fa; color: #333; display: flex; height: 100vh; overflow: hidden; }
        .main-content { flex: 1; display: flex; flex-direction: column; height: 100vh; overflow: hidden; }
        .content { flex: 1; padding: 28px 32px; overflow-y: auto; background: #f5f7fa; }
        .overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.3); z-index: 999; }
        .overlay.active { display: block; }

        /* TABS */
        .tabs { display: flex; flex-wrap: wrap; gap: 4px; margin-bottom: 24px; background: #fff; border-radius: 12px; padding: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); border: 1px solid #e9edf4; }
        .tab-btn { padding: 10px 18px; border: none; background: transparent; border-radius: 8px; font-weight: 500; font-size: 14px; color: #6f8fb0; cursor: pointer; transition: all 0.2s; flex: 0 1 auto; white-space: nowrap; }
        .tab-btn i { margin-right: 6px; }
        .tab-btn:hover { background: #f0f2f7; color: #1e2a3a; }
        .tab-btn.active { background: #4d90fe; color: #fff; box-shadow: 0 2px 6px rgba(77,144,254,0.3); }
        .tab-content { display: none; }
        .tab-content.active { display: block; }

        /* TOOLBAR */
        .toolbar { display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; margin-bottom: 20px; gap: 12px; }
        .toolbar .search-box { display: flex; align-items: center; background: #fff; border-radius: 8px; padding: 0 16px; border: 1px solid #e9edf4; flex: 1 1 300px; }
        .toolbar .search-box i { color: #8aa3c0; margin-right: 10px; }
        .toolbar .search-box input { border: none; padding: 10px 0; font-size: 14px; width: 100%; outline: none; background: transparent; }
        .toolbar .actions { display: flex; gap: 12px; flex-wrap: wrap; }

        /* BUTTONS */
        .btn { padding: 10px 20px; border: none; border-radius: 8px; font-weight: 500; font-size: 14px; cursor: pointer; display: inline-flex; align-items: center; gap: 8px; transition: 0.2s; text-decoration: none; }
        .btn-primary { background: #4d90fe; color: #fff; } .btn-primary:hover { background: #3a7bd5; }
        .btn-success { background: #6fcf97; color: #fff; } .btn-success:hover { background: #52b381; }
        .btn-warning { background: #f39c12; color: #fff; } .btn-warning:hover { background: #d68910; }
        .btn-danger { background: #e74c3c; color: #fff; } .btn-danger:hover { background: #c0392b; }
        .btn-outline { background: transparent; color: #4a5b6e; border: 1px solid #d0d8e3; } .btn-outline:hover { background: #e9edf4; }

        /* 4 NÚT THAO TÁC (chuẩn ảnh mẫu) */
        .actions-cell { display: flex; gap: 8px; justify-content: center; }
        .btn-action { width: 38px; height: 32px; border: none; border-radius: 8px; color: #fff; font-size: 14px; cursor: pointer; display: inline-flex; align-items: center; justify-content: center; transition: 0.2s; }
        .btn-action.edit { background-color: #4d90fe; }
        .btn-action.import { background-color: #6fcf97; }
        .btn-action.export { background-color: #f39c12; }
        .btn-action.delete { background-color: #e74c3c; }
        .btn-action.info { background-color: #8aa3c0; }
        .btn-action:hover { opacity: 0.8; transform: translateY(-2px); }

        /* TABLE */
        .table-container { background: #fff; border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.04); overflow-x: auto; padding: 0 0 8px 0; }
        .data-table { width: 100%; border-collapse: collapse; font-size: 14px; min-width: 800px; }
        .data-table th { text-align: left; padding: 16px 16px 12px 16px; color: #6f8fb0; font-weight: 500; border-bottom: 1px solid #e9edf4; background: #fafbfc; white-space: nowrap; }
        .data-table td { padding: 14px 16px; border-bottom: 1px solid #f0f2f7; vertical-align: middle; }
        .data-table tr:hover td { background: #f8faff; }
        .badge-status { display: inline-block; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }
        .badge-status.active { background: #d5f5e3; color: #1e8449; }
        .badge-status.pending { background: #fdebd0; color: #a04000; }
        .badge-status.done { background: #d5f5e3; color: #1e8449; }
        .badge-status.overdue { background: #fadbd8; color: #922b21; }
        .badge-status.warning { background: #fdebd0; color: #a04000; }
        .badge-status.danger { background: #fadbd8; color: #922b21; }

        /* MODALS */
        .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.4); z-index: 9999; justify-content: center; align-items: center; backdrop-filter: blur(2px); }
        .modal-overlay.active { display: flex; }
        .modal { background: #fff; border-radius: 20px; width: 95%; max-width: 800px; max-height: 90vh; overflow-y: auto; padding: 32px; box-shadow: 0 20px 60px rgba(0,0,0,0.2); animation: fadeInUp 0.25s ease; }
        .modal.large { max-width: 1100px; }
        @keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
        .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
        .modal-header h2 { font-size: 22px; font-weight: 500; color: #1e2a3a; }
        .modal-header .close-modal { background: none; border: none; font-size: 28px; cursor: pointer; color: #8aa3c0; transition: color 0.2s; }
        .modal-header .close-modal:hover { color: #e74c3c; }
        .form-group { margin-bottom: 18px; }
        .form-group label { display: block; font-weight: 500; margin-bottom: 6px; color: #2c3e50; font-size: 14px; }
        .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 10px 14px; border: 1px solid #d0d8e3; border-radius: 8px; font-size: 14px; transition: border 0.2s; background: #fafbfc; }
        .form-group input:focus, .form-group select:focus, .form-group textarea:focus { border-color: #4d90fe; outline: none; background: #fff; }
        .form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px; border-top: 1px solid #e9edf4; padding-top: 20px; }
        .form-row { display: flex; flex-wrap: wrap; gap: 16px; }
        .form-row .form-group { flex: 1 1 calc(50% - 8px); min-width: 200px; }
        .detail-table { width: 100%; border-collapse: collapse; font-size: 14px; margin-top: 6px; }
        .detail-table th { background: #f0f2f7; padding: 10px 8px; text-align: left; font-weight: 500; color: #2c3e50; border-bottom: 1px solid #d0d8e3; }
        .detail-table td { padding: 8px 6px; border-bottom: 1px solid #e9edf4; vertical-align: middle; }
        .detail-table input, .detail-table select { width: 100%; padding: 6px 8px; border: 1px solid #d0d8e3; border-radius: 6px; font-size: 13px; background: #fff; }
        .detail-table .btn-remove-row { background: #e74c3c; color: #fff; border: none; border-radius: 6px; padding: 4px 10px; cursor: pointer; font-size: 13px; }
        .total-summary { display: flex; justify-content: flex-end; gap: 30px; margin-top: 16px; padding: 12px 16px; background: #f8faff; border-radius: 8px; border: 1px solid #e9edf4; }
        .total-summary span { font-weight: 500; color: #1e2a3a; }
        .total-summary .amount { font-weight: 700; color: #2c3e50; }
    </style>
</head>
<body>

    <!-- NHÚNG SIDEBAR -->
    <%@ include file="/views/commons/sidebar.jsp" %>
    <div class="overlay" id="overlay"></div>

    <div class="main-content">
        <!-- NHÚNG HEADER -->
        <%@ include file="/views/commons/header.jsp" %>
        <script>
            document.addEventListener("DOMContentLoaded", function() {
                var pageTitle = document.getElementById('pageHeaderTitle');
                if (pageTitle) pageTitle.innerHTML = 'Quản lý Canh tác <span>| Giống, Vườn, Chăm sóc, Thu hoạch</span>';
            });
        </script>

        <section class="content">
            <!-- TABS CHỨC NĂNG CHÍNH CỦA UC-4 -->
            <div class="tabs">
                <button class="tab-btn active" data-tab="tabGiong"><i class="fas fa-seedling"></i> Giống cây</button>
                <button class="tab-btn" data-tab="tabVuon"><i class="fas fa-tree"></i> Thiết lập vườn</button>
                <button class="tab-btn" data-tab="tabLich"><i class="fas fa-calendar-alt"></i> Lịch chăm sóc</button>
                <button class="tab-btn" data-tab="tabNhatKy"><i class="fas fa-clipboard-list"></i> Nhật ký canh tác</button>
                <button class="tab-btn" data-tab="tabSinhTruong"><i class="fas fa-chart-line"></i> Sinh trưởng</button>
                <button class="tab-btn" data-tab="tabSauBenh"><i class="fas fa-bug"></i> Sâu bệnh</button>
                <button class="tab-btn" data-tab="tabThuHoach"><i class="fas fa-boxes"></i> Thu hoạch</button>
            </div>

            <!-- TAB 1: QUẢN LÝ GIỐNG CÂY (UC-4.1) -->
            <div id="tabGiong" class="tab-content active">
                <div class="toolbar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" placeholder="Tìm giống cây..." id="searchGiong" oninput="filterTable('giong')">
                    </div>
                    <div class="actions">
                        <button class="btn btn-primary" id="btnAddGiong"><i class="fas fa-plus"></i> Thêm giống mới</button>
                    </div>
                </div>
                <div class="table-container">
                    <table class="data-table" id="tableGiong">
                        <thead>
                            <tr>
                                <th>Mã</th>
                                <th>Tên giống</th>
                                <th>Đặc điểm</th>
                                <th>Thời gian thu hoạch (tháng)</th>
                                <th>Năng suất TK (kg/cây)</th>
                                <th style="text-align:center;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyGiong"></tbody>
                    </table>
                </div>
            </div>

            <!-- TAB 2: THIẾT LẬP VƯỜN (UC-4.2) -->
            <div id="tabVuon" class="tab-content">
                <div class="toolbar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" placeholder="Tìm lô đất..." id="searchVuon" oninput="filterTable('vuon')">
                    </div>
                    <div class="actions">
                        <button class="btn btn-primary" id="btnAddVuon"><i class="fas fa-plus"></i> Thiết lập vườn mới</button>
                    </div>
                </div>
                <div class="table-container">
                    <table class="data-table" id="tableVuon">
                        <thead>
                            <tr>
                                <th>ID Lô</th>
                                <th>Tên lô đất</th>
                                <th>Giống</th>
                                <th>Diện tích (ha)</th>
                                <th>Số cây</th>
                                <th>Mật độ (cây/ha)</th>
                                <th>Phân loại</th>
                                <th style="text-align:center;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyVuon"></tbody>
                    </table>
                </div>
            </div>

            <!-- TAB 3: LỊCH CHĂM SÓC (UC-4.3) -->
            <div id="tabLich" class="tab-content">
                <div class="toolbar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" placeholder="Tìm lịch..." id="searchLich" oninput="filterTable('lich')">
                    </div>
                    <div class="actions">
                        <button class="btn btn-primary" id="btnAddLich"><i class="fas fa-plus"></i> Tạo lịch mới</button>
                    </div>
                </div>
                <div class="table-container">
                    <table class="data-table" id="tableLich">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Lô đất</th>
                                <th>Công việc</th>
                                <th>Ngày thực hiện</th>
                                <th>Chu kỳ (ngày)</th>
                                <th>Trạng thái</th>
                                <th style="text-align:center;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyLich"></tbody>
                    </table>
                </div>
            </div>

            <!-- TAB 4: NHẬT KÝ CANH TÁC (UC-4.4) -->
            <div id="tabNhatKy" class="tab-content">
                <div class="toolbar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" placeholder="Tìm nhật ký..." id="searchNhatKy" oninput="filterTable('nhatky')">
                    </div>
                    <div class="actions">
                        <button class="btn btn-primary" id="btnAddNhatKy"><i class="fas fa-plus"></i> Ghi nhật ký mới</button>
                    </div>
                </div>
                <div class="table-container">
                    <table class="data-table" id="tableNhatKy">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Lô đất</th>
                                <th>Công việc</th>
                                <th>Ngày thực hiện</th>
                                <th>Vật tư (SL)</th>
                                <th>Ghi chú</th>
                                <th style="text-align:center;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyNhatKy"></tbody>
                    </table>
                </div>
            </div>

            <!-- TAB 5: THEO DÕI SINH TRƯỞNG (UC-4.5) -->
            <div id="tabSinhTruong" class="tab-content">
                <div class="toolbar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" placeholder="Tìm lô..." id="searchSinhTruong" oninput="filterTable('sinhtruong')">
                    </div>
                    <div class="actions">
                        <button class="btn btn-primary" id="btnAddSinhTruong"><i class="fas fa-plus"></i> Cập nhật trạng thái</button>
                    </div>
                </div>
                <div class="table-container">
                    <table class="data-table" id="tableSinhTruong">
                        <thead>
                            <tr>
                                <th>ID Lô</th>
                                <th>Tên lô</th>
                                <th>Giai đoạn</th>
                                <th>Tỷ lệ (%)</th>
                                <th>Ngày cập nhật</th>
                                <th>Số cây còn lại</th>
                                <th style="text-align:center;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="tbodySinhTruong"></tbody>
                    </table>
                </div>
            </div>

            <!-- TAB 6: SÂU BỆNH (UC-4.6) -->
            <div id="tabSauBenh" class="tab-content">
                <div class="toolbar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" placeholder="Tìm sâu bệnh..." id="searchSauBenh" oninput="filterTable('saubenh')">
                    </div>
                    <div class="actions">
                        <button class="btn btn-primary" id="btnAddSauBenh"><i class="fas fa-plus"></i> Ghi nhận sâu bệnh</button>
                    </div>
                </div>
                <div class="table-container">
                    <table class="data-table" id="tableSauBenh">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Lô đất</th>
                                <th>Loại sâu bệnh</th>
                                <th>Mức độ</th>
                                <th>Biện pháp xử lý</th>
                                <th>Trạng thái</th>
                                <th style="text-align:center;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="tbodySauBenh"></tbody>
                    </table>
                </div>
            </div>

            <!-- TAB 7: THU HOẠCH (UC-4.7) -->
            <div id="tabThuHoach" class="tab-content">
                <div class="toolbar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" placeholder="Tìm thu hoạch..." id="searchThuHoach" oninput="filterTable('thuhoach')">
                    </div>
                    <div class="actions">
                        <button class="btn btn-primary" id="btnAddThuHoach"><i class="fas fa-plus"></i> Ghi nhận thu hoạch</button>
                    </div>
                </div>
                <div class="table-container">
                    <table class="data-table" id="tableThuHoach">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Lô đất</th>
                                <th>Vụ mùa</th>
                                <th>Ngày thu</th>
                                <th>Sản lượng (kg)</th>
                                <th>Xếp loại</th>
                                <th style="text-align:center;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyThuHoach"></tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>

    <!-- ===== MODAL GIỐNG CÂY ===== -->
    <div class="modal-overlay" id="modalGiong">
        <div class="modal">
            <div class="modal-header">
                <h2 id="titleGiong">Thêm giống cây mới</h2>
                <button class="close-modal" onclick="closeModal('modalGiong')">&times;</button>
            </div>
            <form id="formGiong">
                <input type="hidden" id="editGiongId">
                <div class="form-row">
                    <div class="form-group"><label>Tên giống <span style="color:#e74c3c;">*</span></label><input type="text" id="tenGiong" required></div>
                    <div class="form-group"><label>Đặc điểm</label><input type="text" id="dacDiemGiong"></div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Thời gian thu hoạch (tháng)</label><input type="number" id="thoiGianThuHoach" step="0.5" value="8"></div>
                    <div class="form-group"><label>Năng suất tham khảo (kg/cây)</label><input type="number" id="nangSuatTK" step="0.1" value="50"></div>
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="closeModal('modalGiong')">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== MODAL THIẾT LẬP VƯỜN ===== -->
    <div class="modal-overlay" id="modalVuon">
        <div class="modal large">
            <div class="modal-header">
                <h2 id="titleVuon">Thiết lập vườn trồng</h2>
                <button class="close-modal" onclick="closeModal('modalVuon')">&times;</button>
            </div>
            <form id="formVuon">
                <input type="hidden" id="editVuonId">
                <div class="form-row">
                    <div class="form-group"><label>Lô đất (ID) <span style="color:#e74c3c;">*</span></label><input type="number" id="idKhuDatVuon" required placeholder="VD: 1"></div>
                    <div class="form-group"><label>Giống cây <span style="color:#e74c3c;">*</span></label>
                        <select id="idGiongVuon"><option value="">-- Chọn giống --</option></select>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Diện tích khai thác (ha)</label><input type="number" id="dienTichVuon" step="0.01" value="1.0"></div>
                    <div class="form-group"><label>Số lượng cây</label><input type="number" id="soCayVuon" step="1" value="400"></div>
                </div>
                <div class="form-group">
                    <label>Phân loại mật độ (tự động tính)</label>
                    <p id="matDoVuonDisplay" style="font-weight:bold; color:#4d90fe;">Chưa tính</p>
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="closeModal('modalVuon')">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== MODAL LỊCH CHĂM SÓC ===== -->
    <div class="modal-overlay" id="modalLich">
        <div class="modal">
            <div class="modal-header">
                <h2 id="titleLich">Tạo lịch chăm sóc</h2>
                <button class="close-modal" onclick="closeModal('modalLich')">&times;</button>
            </div>
            <form id="formLich">
                <input type="hidden" id="editLichId">
                <div class="form-group"><label>Lô đất (ID) <span style="color:#e74c3c;">*</span></label><input type="number" id="idKhuDatLich" required></div>
                <div class="form-group"><label>Công việc <span style="color:#e74c3c;">*</span></label><input type="text" id="congViecLich" required></div>
                <div class="form-row">
                    <div class="form-group"><label>Ngày thực hiện <span style="color:#e74c3c;">*</span></label><input type="datetime-local" id="ngayThucHienLich" required></div>
                    <div class="form-group"><label>Chu kỳ (ngày, 0 = không lặp)</label><input type="number" id="chuKyLich" value="0" min="0"></div>
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="closeModal('modalLich')">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== MODAL NHẬT KÝ CANH TÁC ===== -->
    <div class="modal-overlay" id="modalNhatKy">
        <div class="modal large">
            <div class="modal-header">
                <h2 id="titleNhatKy">Ghi nhật ký canh tác</h2>
                <button class="close-modal" onclick="closeModal('modalNhatKy')">&times;</button>
            </div>
            <form id="formNhatKy">
                <input type="hidden" id="editNhatKyId">
                <div class="form-row">
                    <div class="form-group"><label>Lô đất (ID) <span style="color:#e74c3c;">*</span></label><input type="number" id="idKhuDatNhatKy" required></div>
                    <div class="form-group"><label>Nhân viên (ID) <span style="color:#e74c3c;">*</span></label><input type="number" id="idNhanVienNhatKy" required></div>
                </div>
                <div class="form-group"><label>Công việc <span style="color:#e74c3c;">*</span></label><input type="text" id="tenCongViecNhatKy" required></div>
                <div class="form-group"><label>Ngày thực hiện <span style="color:#e74c3c;">*</span></label><input type="datetime-local" id="ngayThucHienNhatKy" required></div>
                <div class="form-group"><label>Ghi chú</label><textarea id="ghiChuNhatKy" rows="2"></textarea></div>

                <h4 style="margin: 16px 0 8px;"><i class="fas fa-box"></i> Vật tư sử dụng (nếu có)</h4>
                <div style="overflow-x:auto;">
                    <table class="detail-table" id="detailVatTuNhatKy">
                        <thead><tr><th>Vật tư</th><th>Số lượng</th><th>Đơn vị</th><th></th></tr></thead>
                        <tbody id="tbodyVatTuNhatKy">
                            <tr id="rowVatTuTemplate" style="display:none;">
                                <td><select class="vt-select"><option value="">-- Chọn --</option></select></td>
                                <td><input type="number" class="vt-qty" step="0.01" value="1"></td>
                                <td><span class="vt-donvi">-</span></td>
                                <td><button type="button" class="btn-remove-row" onclick="removeRow(this)"><i class="fas fa-times"></i></button></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div style="margin:8px 0 12px;"><button type="button" class="btn btn-outline btn-sm" onclick="addVatTuRow()"><i class="fas fa-plus"></i> Thêm vật tư</button></div>

                <h4 style="margin: 16px 0 8px;"><i class="fas fa-tractor"></i> Thiết bị sử dụng (nếu có)</h4>
                <div style="overflow-x:auto;">
                    <table class="detail-table" id="detailThietBiNhatKy">
                        <thead><tr><th>Thiết bị</th><th>Số lượng</th><th></th></tr></thead>
                        <tbody id="tbodyThietBiNhatKy">
                            <tr id="rowThietBiTemplate" style="display:none;">
                                <td><select class="tb-select"><option value="">-- Chọn --</option></select></td>
                                <td><input type="number" class="tb-qty" step="1" value="1"></td>
                                <td><button type="button" class="btn-remove-row" onclick="removeRow(this)"><i class="fas fa-times"></i></button></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div style="margin:8px 0 12px;"><button type="button" class="btn btn-outline btn-sm" onclick="addThietBiRow()"><i class="fas fa-plus"></i> Thêm thiết bị</button></div>

                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="closeModal('modalNhatKy')">Hủy</button>
                    <button type="submit" class="btn btn-success"><i class="fas fa-check"></i> Lưu nhật ký</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== MODAL SINH TRƯỞNG ===== -->
    <div class="modal-overlay" id="modalSinhTruong">
        <div class="modal">
            <div class="modal-header">
                <h2 id="titleSinhTruong">Cập nhật sinh trưởng</h2>
                <button class="close-modal" onclick="closeModal('modalSinhTruong')">&times;</button>
            </div>
            <form id="formSinhTruong">
                <input type="hidden" id="editSinhTruongId">
                <div class="form-group"><label>Lô đất (ID) <span style="color:#e74c3c;">*</span></label><input type="number" id="idKhuDatSinhTruong" required></div>
                <div class="form-row">
                    <div class="form-group"><label>Giai đoạn</label>
                        <select id="giaiDoanSinhTruong">
                            <option value="Cây con">Cây con</option>
                            <option value="Sinh trưởng">Sinh trưởng</option>
                            <option value="Ra hoa">Ra hoa</option>
                            <option value="Đậu quả">Đậu quả</option>
                            <option value="Thu hoạch">Thu hoạch</option>
                        </select>
                    </div>
                    <div class="form-group"><label>Tỷ lệ (%)</label><input type="number" id="tyLeSinhTruong" min="0" max="100" value="50"></div>
                </div>
                <div class="form-group"><label>Số cây còn lại</label><input type="number" id="soCayConLai" step="1"></div>
                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="closeModal('modalSinhTruong')">Hủy</button>
                    <button type="submit" class="btn btn-primary">Cập nhật</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== MODAL SÂU BỆNH ===== -->
    <div class="modal-overlay" id="modalSauBenh">
        <div class="modal">
            <div class="modal-header">
                <h2 id="titleSauBenh">Ghi nhận sâu bệnh</h2>
                <button class="close-modal" onclick="closeModal('modalSauBenh')">&times;</button>
            </div>
            <form id="formSauBenh">
                <input type="hidden" id="editSauBenhId">
                <div class="form-group"><label>Lô đất (ID) <span style="color:#e74c3c;">*</span></label><input type="number" id="idKhuDatSauBenh" required></div>
                <div class="form-group"><label>Loại sâu bệnh <span style="color:#e74c3c;">*</span></label><input type="text" id="loaiSauBenh" required></div>
                <div class="form-group"><label>Mức độ</label>
                    <select id="mucDoSauBenh">
                        <option value="Nhẹ">Nhẹ</option>
                        <option value="Trung bình">Trung bình</option>
                        <option value="Nặng">Nặng</option>
                    </select>
                </div>
                <div class="form-group"><label>Biện pháp xử lý</label><textarea id="bienPhapSauBenh" rows="2"></textarea></div>
                <div class="form-group"><label>Trạng thái</label>
                    <select id="trangThaiSauBenh">
                        <option value="Chưa xử lý">Chưa xử lý</option>
                        <option value="Đang xử lý">Đang xử lý</option>
                        <option value="Đã xử lý">Đã xử lý</option>
                    </select>
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="closeModal('modalSauBenh')">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== MODAL THU HOẠCH ===== -->
    <div class="modal-overlay" id="modalThuHoach">
        <div class="modal">
            <div class="modal-header">
                <h2 id="titleThuHoach">Ghi nhận thu hoạch</h2>
                <button class="close-modal" onclick="closeModal('modalThuHoach')">&times;</button>
            </div>
            <form id="formThuHoach">
                <input type="hidden" id="editThuHoachId">
                <div class="form-group"><label>Lô đất (ID) <span style="color:#e74c3c;">*</span></label><input type="number" id="idKhuDatThuHoach" required></div>
                <div class="form-group"><label>Vụ mùa <span style="color:#e74c3c;">*</span></label><input type="text" id="tenVuMua" required></div>
                <div class="form-group"><label>Ngày thu hoạch <span style="color:#e74c3c;">*</span></label><input type="datetime-local" id="ngayThuHoach" required></div>
                <div class="form-row">
                    <div class="form-group"><label>Tổng sản lượng (kg) <span style="color:#e74c3c;">*</span></label><input type="number" id="tongSanLuong" step="0.1" required></div>
                    <div class="form-group"><label>Xếp loại</label><input type="text" id="xepLoaiThuHoach" placeholder="VD: Loại 1"></div>
                </div>
                <div class="form-group"><label>Ghi chú</label><textarea id="ghiChuThuHoach" rows="2"></textarea></div>
                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="closeModal('modalThuHoach')">Hủy</button>
                    <button type="submit" class="btn btn-success"><i class="fas fa-check"></i> Lưu</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== JAVASCRIPT ĐÃ SỬA LỖI (CỘNG CHUỖI TRUYỀN THỐNG) ===== -->
    <script>
        // ========== DỮ LIỆU MẪU ==========

        let giongList = [
            { id: 1, ten: 'Sầu riêng Ri6', dacDiem: 'Cơm vàng, vỏ mỏng', thoiGian: 8, nangSuat: 60 },
            { id: 2, ten: 'Sầu riêng Monthong', dacDiem: 'Cơm dày, hạt lép', thoiGian: 9, nangSuat: 70 },
            { id: 3, ten: 'Sầu riêng Musang King', dacDiem: 'Vị đắng nhẹ, thơm', thoiGian: 7, nangSuat: 50 }
        ];
        let nextGiongId = 4;

        let vuonList = [
            { id: 1, idKhuDat: 1, idGiong: 1, dienTich: 1.2, soCay: 500, matDo: 416.67, phanLoai: 'Vừa' },
            { id: 2, idKhuDat: 2, idGiong: 2, dienTich: 0.8, soCay: 300, matDo: 375.0, phanLoai: 'Vừa' }
        ];
        let nextVuonId = 3;

        let lichList = [
            { id: 1, idKhuDat: 1, congViec: 'Bón phân lót', ngayThucHien: '2026-08-10T08:00', chuKy: 30, trangThai: 'Chờ xử lý' },
            { id: 2, idKhuDat: 2, congViec: 'Phun thuốc phòng bệnh', ngayThucHien: '2026-08-12T14:00', chuKy: 0, trangThai: 'Chờ xử lý' }
        ];
        let nextLichId = 3;

        let nhatKyList = [
            { id: 1, idKhuDat: 1, idNhanVien: 2, tenCongViec: 'Bón phân NPK', ngayThucHien: '2026-08-05T07:30', ghiChu: 'Đợt 1', vatTu: [{ id: 1, soLuong: 50 }], thietBi: [] },
            { id: 2, idKhuDat: 2, idNhanVien: 3, tenCongViec: 'Tưới nước', ngayThucHien: '2026-08-06T09:00', ghiChu: '', vatTu: [], thietBi: [{ id: 2, soLuong: 1 }] }
        ];
        let nextNhatKyId = 3;

        let sinhTruongList = [
            { id: 1, idKhuDat: 1, giaiDoan: 'Sinh trưởng', tyLe: 70, ngayCapNhat: '2026-08-07', soCayConLai: 480 },
            { id: 2, idKhuDat: 2, giaiDoan: 'Ra hoa', tyLe: 50, ngayCapNhat: '2026-08-07', soCayConLai: 290 }
        ];
        let nextSinhTruongId = 3;

        let sauBenhList = [
            { id: 1, idKhuDat: 1, loai: 'Nhện đỏ', mucDo: 'Trung bình', bienPhap: 'Phun thuốc Abamectin', trangThai: 'Đang xử lý' },
            { id: 2, idKhuDat: 2, loai: 'Rầy phấn trắng', mucDo: 'Nhẹ', bienPhap: 'Phun dầu khoáng', trangThai: 'Chưa xử lý' }
        ];
        let nextSauBenhId = 3;

        let thuHoachList = [
            { id: 1, idKhuDat: 1, tenVuMua: 'Vụ chính 2026', ngayThuHoach: '2026-07-20T08:00', tongSanLuong: 1200, xepLoai: 'Loại 1', ghiChu: '' },
            { id: 2, idKhuDat: 2, tenVuMua: 'Vụ chính 2026', ngayThuHoach: '2026-07-25T09:30', tongSanLuong: 800, xepLoai: 'Loại 2', ghiChu: '' }
        ];
        let nextThuHoachId = 3;

        let materials = [
            { id: 1, tenVatTu: 'Phân NPK', donViTinh: 'Kg' },
            { id: 2, tenVatTu: 'Thuốc Abamectin', donViTinh: 'Lít' },
            { id: 3, tenVatTu: 'Phân Kali', donViTinh: 'Bao' }
        ];
        let thietBiDropdownList = [
            { id: 1, tenThietBi: 'Máy kéo' },
            { id: 2, tenThietBi: 'Máy bơm' },
            { id: 3, tenThietBi: 'Máy cắt cỏ' }
        ];


        // ========== HÀM RENDER KHÔNG DÙNG  ==========

        function renderGiong() {
            const tbody = document.getElementById('tbodyGiong');
            tbody.innerHTML = '';
            giongList.forEach(function(g) {
                let trHtml = "<tr>";
                trHtml += "<td>" + g.id + "</td>";
                trHtml += "<td><strong>" + g.ten + "</strong></td>";
                trHtml += "<td>" + (g.dacDiem || '') + "</td>";
                trHtml += "<td>" + g.thoiGian + "</td>";
                trHtml += "<td>" + g.nangSuat + "</td>";
                trHtml += "<td><div class='actions-cell'>";
                trHtml += "<button class='btn-action edit' title='Sửa' onclick='editGiong(" + g.id + ")'><i class='fas fa-edit'></i></button>";
                trHtml += "<button class='btn-action delete' title='Xóa' onclick='deleteItem(\"giong\", " + g.id + ")'><i class='fas fa-trash'></i></button>";
                trHtml += "</div></td></tr>";

                let tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });
        }

        function renderVuon() {
            const tbody = document.getElementById('tbodyVuon');
            tbody.innerHTML = '';
            vuonList.forEach(function(v) {
                const g = giongList.find(function(x) { return x.id === v.idGiong; });
                const tenGiong = g ? g.ten : 'Chưa có';
                
                let trHtml = "<tr>";
                trHtml += "<td>" + v.idKhuDat + "</td>";
                trHtml += "<td>Lô " + v.idKhuDat + "</td>";
                trHtml += "<td>" + tenGiong + "</td>";
                trHtml += "<td>" + v.dienTich + "</td>";
                trHtml += "<td>" + v.soCay + "</td>";
                trHtml += "<td>" + v.matDo.toFixed(0) + "</td>";
                trHtml += "<td><span class='badge-status active'>" + v.phanLoai + "</span></td>";
                trHtml += "<td><div class='actions-cell'>";
                trHtml += "<button class='btn-action edit' title='Sửa' onclick='editVuon(" + v.id + ")'><i class='fas fa-edit'></i></button>";
                trHtml += "<button class='btn-action delete' title='Xóa' onclick='deleteItem(\"vuon\", " + v.id + ")'><i class='fas fa-trash'></i></button>";
                trHtml += "</div></td></tr>";

                let tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });
        }

        function renderLich() {
            const tbody = document.getElementById('tbodyLich');
            tbody.innerHTML = '';
            lichList.forEach(function(l) {
                const statusClass = l.trangThai === 'Chờ xử lý' ? 'pending' : (l.trangThai === 'Đã hoàn thành' ? 'done' : 'overdue');
                
                let trHtml = "<tr>";
                trHtml += "<td>" + l.id + "</td>";
                trHtml += "<td>Lô " + l.idKhuDat + "</td>";
                trHtml += "<td>" + l.congViec + "</td>";
                trHtml += "<td>" + l.ngayThucHien.replace('T', ' ') + "</td>";
                trHtml += "<td>" + l.chuKy + "</td>";
                trHtml += "<td><span class='badge-status " + statusClass + "'>" + l.trangThai + "</span></td>";
                trHtml += "<td><div class='actions-cell'>";
                trHtml += "<button class='btn-action edit' title='Sửa' onclick='editLich(" + l.id + ")'><i class='fas fa-edit'></i></button>";
                trHtml += "<button class='btn-action delete' title='Xóa' onclick='deleteItem(\"lich\", " + l.id + ")'><i class='fas fa-trash'></i></button>";
                trHtml += "</div></td></tr>";

                let tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });
        }

        function renderNhatKy() {
            const tbody = document.getElementById('tbodyNhatKy');
            tbody.innerHTML = '';
            nhatKyList.forEach(function(n) {
                let vatTuStr = n.vatTu.map(function(v) {
                    const vt = materials.find(function(m) { return m.id === v.id; });
                    return (vt ? vt.tenVatTu : 'VT' + v.id) + '(' + v.soLuong + ')';
                }).join(', ') || 'Không';
                
                let trHtml = "<tr>";
                trHtml += "<td>" + n.id + "</td>";
                trHtml += "<td>Lô " + n.idKhuDat + "</td>";
                trHtml += "<td>" + n.tenCongViec + "</td>";
                trHtml += "<td>" + n.ngayThucHien.replace('T', ' ') + "</td>";
                trHtml += "<td>" + vatTuStr + "</td>";
                trHtml += "<td>" + (n.ghiChu || '') + "</td>";
                trHtml += "<td><div class='actions-cell'>";
                trHtml += "<button class='btn-action info' title='Chi tiết' onclick='alert(\"Nhật ký ID " + n.id + "\")'><i class='fas fa-eye'></i></button>";
                trHtml += "<button class='btn-action delete' title='Xóa' onclick='deleteItem(\"nhatky\", " + n.id + ")'><i class='fas fa-trash'></i></button>";
                trHtml += "</div></td></tr>";

                let tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });
        }

        function renderSinhTruong() {
            const tbody = document.getElementById('tbodySinhTruong');
            tbody.innerHTML = '';
            sinhTruongList.forEach(function(s) {
                let trHtml = "<tr>";
                trHtml += "<td>" + s.idKhuDat + "</td>";
                trHtml += "<td>Lô " + s.idKhuDat + "</td>";
                trHtml += "<td><span class='badge-status active'>" + s.giaiDoan + "</span></td>";
                trHtml += "<td>" + s.tyLe + "%</td>";
                trHtml += "<td>" + s.ngayCapNhat + "</td>";
                trHtml += "<td>" + s.soCayConLai + "</td>";
                trHtml += "<td><div class='actions-cell'>";
                trHtml += "<button class='btn-action edit' title='Sửa' onclick='editSinhTruong(" + s.id + ")'><i class='fas fa-edit'></i></button>";
                trHtml += "<button class='btn-action delete' title='Xóa' onclick='deleteItem(\"sinhtruong\", " + s.id + ")'><i class='fas fa-trash'></i></button>";
                trHtml += "</div></td></tr>";

                let tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });
        }

        function renderSauBenh() {
            const tbody = document.getElementById('tbodySauBenh');
            tbody.innerHTML = '';
            sauBenhList.forEach(function(s) {
                const statusClass = s.trangThai === 'Chưa xử lý' ? 'overdue' : (s.trangThai === 'Đang xử lý' ? 'warning' : 'done');
                
                let trHtml = "<tr>";
                trHtml += "<td>" + s.id + "</td>";
                trHtml += "<td>Lô " + s.idKhuDat + "</td>";
                trHtml += "<td>" + s.loai + "</td>";
                trHtml += "<td>" + s.mucDo + "</td>";
                trHtml += "<td>" + (s.bienPhap || '') + "</td>";
                trHtml += "<td><span class='badge-status " + statusClass + "'>" + s.trangThai + "</span></td>";
                trHtml += "<td><div class='actions-cell'>";
                trHtml += "<button class='btn-action edit' title='Sửa' onclick='editSauBenh(" + s.id + ")'><i class='fas fa-edit'></i></button>";
                trHtml += "<button class='btn-action delete' title='Xóa' onclick='deleteItem(\"saubenh\", " + s.id + ")'><i class='fas fa-trash'></i></button>";
                trHtml += "</div></td></tr>";

                let tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });
        }

        function renderThuHoach() {
            const tbody = document.getElementById('tbodyThuHoach');
            tbody.innerHTML = '';
            thuHoachList.forEach(function(t) {
                let trHtml = "<tr>";
                trHtml += "<td>" + t.id + "</td>";
                trHtml += "<td>Lô " + t.idKhuDat + "</td>";
                trHtml += "<td>" + t.tenVuMua + "</td>";
                trHtml += "<td>" + t.ngayThuHoach.replace('T', ' ') + "</td>";
                trHtml += "<td><strong>" + t.tongSanLuong + "</strong></td>";
                trHtml += "<td>" + (t.xepLoai || '') + "</td>";
                trHtml += "<td><div class='actions-cell'>";
                trHtml += "<button class='btn-action edit' title='Sửa' onclick='editThuHoach(" + t.id + ")'><i class='fas fa-edit'></i></button>";
                trHtml += "<button class='btn-action delete' title='Xóa' onclick='deleteItem(\"thuhoach\", " + t.id + ")'><i class='fas fa-trash'></i></button>";
                trHtml += "</div></td></tr>";

                let tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });
        }

        // ========== HÀM XÓA CHUNG ==========
        function deleteItem(type, id) {
            if (!confirm('Bạn có chắc chắn muốn xóa mục này?')) return;
            const lists = {
                giong: giongList, vuon: vuonList, lich: lichList,
                nhatky: nhatKyList, sinhtruong: sinhTruongList, 
                saubenh: sauBenhList, thuhoach: thuHoachList
            };
            const list = lists[type];
            if (!list) return;
            const idx = list.findIndex(function(item) { return item.id === id; });
            if (idx !== -1) list.splice(idx, 1);
            
            const renderMap = {
                giong: renderGiong, vuon: renderVuon, lich: renderLich,
                nhatky: renderNhatKy, sinhtruong: renderSinhTruong,
                saubenh: renderSauBenh, thuhoach: renderThuHoach
            };
            if (renderMap[type]) renderMap[type]();
        }

        // ========== MODAL HELPERS ==========
        function closeModal(id) { document.getElementById(id).classList.remove('active'); }
        function openModal(id) { document.getElementById(id).classList.add('active'); }

        // ========== GIỐNG ==========
        function editGiong(id) {
            const g = giongList.find(function(item) { return item.id === id; });
            if (!g) return;
            document.getElementById('editGiongId').value = g.id;
            document.getElementById('tenGiong').value = g.ten;
            document.getElementById('dacDiemGiong').value = g.dacDiem || '';
            document.getElementById('thoiGianThuHoach').value = g.thoiGian;
            document.getElementById('nangSuatTK').value = g.nangSuat;
            document.getElementById('titleGiong').textContent = 'Sửa giống cây';
            openModal('modalGiong');
        }
        document.getElementById('btnAddGiong').addEventListener('click', function() {
            document.getElementById('editGiongId').value = '';
            document.getElementById('tenGiong').value = '';
            document.getElementById('dacDiemGiong').value = '';
            document.getElementById('thoiGianThuHoach').value = '8';
            document.getElementById('nangSuatTK').value = '50';
            document.getElementById('titleGiong').textContent = 'Thêm giống cây mới';
            openModal('modalGiong');
        });
        document.getElementById('formGiong').addEventListener('submit', function(e) {
            e.preventDefault();
            const id = document.getElementById('editGiongId').value;
            const data = {
                ten: document.getElementById('tenGiong').value.trim(),
                dacDiem: document.getElementById('dacDiemGiong').value.trim(),
                thoiGian: parseFloat(document.getElementById('thoiGianThuHoach').value) || 0,
                nangSuat: parseFloat(document.getElementById('nangSuatTK').value) || 0
            };
            if (!data.ten) { alert('Tên giống không được để trống'); return; }
            if (id) {
                const g = giongList.find(function(item) { return item.id === parseInt(id); });
                if (g) Object.assign(g, data);
            } else {
                data.id = nextGiongId++;
                giongList.push(data);
            }
            closeModal('modalGiong');
            renderGiong();
        });

        // ========== VƯỜN ==========
        function editVuon(id) {
            const v = vuonList.find(function(item) { return item.id === id; });
            if (!v) return;
            document.getElementById('editVuonId').value = v.id;
            document.getElementById('idKhuDatVuon').value = v.idKhuDat;
            document.getElementById('idGiongVuon').value = v.idGiong;
            document.getElementById('dienTichVuon').value = v.dienTich;
            document.getElementById('soCayVuon').value = v.soCay;
            document.getElementById('titleVuon').textContent = 'Sửa thiết lập vườn';
            updateMatDoDisplay();
            openModal('modalVuon');
        }
        document.getElementById('btnAddVuon').addEventListener('click', function() {
            document.getElementById('editVuonId').value = '';
            document.getElementById('idKhuDatVuon').value = '';
            document.getElementById('idGiongVuon').value = '';
            document.getElementById('dienTichVuon').value = '1.0';
            document.getElementById('soCayVuon').value = '400';
            document.getElementById('titleVuon').textContent = 'Thiết lập vườn mới';
            updateMatDoDisplay();
            openModal('modalVuon');
        });

        function updateMatDoDisplay() {
            const dt = parseFloat(document.getElementById('dienTichVuon').value) || 0;
            const sc = parseFloat(document.getElementById('soCayVuon').value) || 0;
            if (dt > 0) {
                const md = sc / dt;
                let loai = '';
                if (md < 200) loai = 'Thưa';
                else if (md < 350) loai = 'Vừa';
                else if (md < 500) loai = 'Dày';
                else loai = 'Rất dày';
                document.getElementById('matDoVuonDisplay').textContent = md.toFixed(0) + ' cây/ha - ' + loai;
            } else {
                document.getElementById('matDoVuonDisplay').textContent = 'Chưa tính';
            }
        }
        document.getElementById('dienTichVuon').addEventListener('input', updateMatDoDisplay);
        document.getElementById('soCayVuon').addEventListener('input', updateMatDoDisplay);

        function populateGiongSelect() {
            const sel = document.getElementById('idGiongVuon');
            sel.innerHTML = '<option value="">-- Chọn giống --</option>';
            giongList.forEach(function(g) {
                const opt = document.createElement('option');
                opt.value = g.id;
                opt.textContent = g.ten;
                sel.appendChild(opt);
            });
        }

        document.getElementById('formVuon').addEventListener('submit', function(e) {
            e.preventDefault();
            const id = document.getElementById('editVuonId').value;
            const idKhuDat = parseInt(document.getElementById('idKhuDatVuon').value);
            const idGiong = parseInt(document.getElementById('idGiongVuon').value);
            const dienTich = parseFloat(document.getElementById('dienTichVuon').value) || 0;
            const soCay = parseInt(document.getElementById('soCayVuon').value) || 0;
            if (!idKhuDat || !idGiong || dienTich <= 0 || soCay <= 0) {
                alert('Vui lòng nhập đầy đủ thông tin');
                return;
            }
            const matDo = soCay / dienTich;
            let phanLoai = '';
            if (matDo < 200) phanLoai = 'Thưa';
            else if (matDo < 350) phanLoai = 'Vừa';
            else if (matDo < 500) phanLoai = 'Dày';
            else phanLoai = 'Rất dày';
            
            const data = { idKhuDat: idKhuDat, idGiong: idGiong, dienTich: dienTich, soCay: soCay, matDo: matDo, phanLoai: phanLoai };
            if (id) {
                const v = vuonList.find(function(item) { return item.id === parseInt(id); });
                if (v) Object.assign(v, data);
            } else {
                data.id = nextVuonId++;
                vuonList.push(data);
            }
            closeModal('modalVuon');
            renderVuon();
        });

        // ========== LỊCH ==========
        function editLich(id) {
            const l = lichList.find(function(item) { return item.id === id; });
            if (!l) return;
            document.getElementById('editLichId').value = l.id;
            document.getElementById('idKhuDatLich').value = l.idKhuDat;
            document.getElementById('congViecLich').value = l.congViec;
            document.getElementById('ngayThucHienLich').value = l.ngayThucHien;
            document.getElementById('chuKyLich').value = l.chuKy;
            document.getElementById('titleLich').textContent = 'Sửa lịch chăm sóc';
            openModal('modalLich');
        }
        document.getElementById('btnAddLich').addEventListener('click', function() {
            document.getElementById('editLichId').value = '';
            document.getElementById('idKhuDatLich').value = '';
            document.getElementById('congViecLich').value = '';
            const now = new Date();
            document.getElementById('ngayThucHienLich').value = now.toISOString().slice(0, 16);
            document.getElementById('chuKyLich').value = '0';
            document.getElementById('titleLich').textContent = 'Tạo lịch chăm sóc mới';
            openModal('modalLich');
        });
        document.getElementById('formLich').addEventListener('submit', function(e) {
            e.preventDefault();
            const id = document.getElementById('editLichId').value;
            const data = {
                idKhuDat: parseInt(document.getElementById('idKhuDatLich').value),
                congViec: document.getElementById('congViecLich').value.trim(),
                ngayThucHien: document.getElementById('ngayThucHienLich').value,
                chuKy: parseInt(document.getElementById('chuKyLich').value) || 0,
                trangThai: 'Chờ xử lý'
            };
            if (!data.idKhuDat || !data.congViec) { alert('Vui lòng nhập đầy đủ'); return; }
            if (id) {
                const l = lichList.find(function(item) { return item.id === parseInt(id); });
                if (l) Object.assign(l, data);
            } else {
                data.id = nextLichId++;
                lichList.push(data);
            }
            closeModal('modalLich');
            renderLich();
        });

        // ========== NHẬT KÝ ==========
        function addVatTuRow() {
            const tbody = document.getElementById('tbodyVatTuNhatKy');
            const tr = document.getElementById('rowVatTuTemplate').cloneNode(true);
            tr.id = '';
            tr.style.display = '';
            const select = tr.querySelector('.vt-select');
            select.innerHTML = '<option value="">-- Chọn --</option>';
            materials.forEach(function(m) {
                const opt = document.createElement('option');
                opt.value = m.id;
                opt.textContent = m.tenVatTu;
                select.appendChild(opt);
            });
            select.addEventListener('change', function() {
                const id = parseInt(this.value);
                const vt = materials.find(function(m) { return m.id === id; });
                const span = this.closest('tr').querySelector('.vt-donvi');
                if (span) span.textContent = vt ? vt.donViTinh : '-';
            });
            tbody.appendChild(tr);
        }

        function addThietBiRow() {
            const tbody = document.getElementById('tbodyThietBiNhatKy');
            const tr = document.getElementById('rowThietBiTemplate').cloneNode(true);
            tr.id = '';
            tr.style.display = '';
            const select = tr.querySelector('.tb-select');
            select.innerHTML = '<option value="">-- Chọn --</option>';
            thietBiDropdownList.forEach(function(tb) {
                const opt = document.createElement('option');
                opt.value = tb.id;
                opt.textContent = tb.tenThietBi;
                select.appendChild(opt);
            });
            tbody.appendChild(tr);
        }

        function removeRow(btn) {
            const tr = btn.closest('tr');
            if (tr.id === 'rowVatTuTemplate' || tr.id === 'rowThietBiTemplate') return;
            tr.remove();
        }

        document.getElementById('btnAddNhatKy').addEventListener('click', function() {
            document.getElementById('editNhatKyId').value = '';
            document.getElementById('idKhuDatNhatKy').value = '';
            document.getElementById('idNhanVienNhatKy').value = '';
            document.getElementById('tenCongViecNhatKy').value = '';
            const now = new Date();
            document.getElementById('ngayThucHienNhatKy').value = now.toISOString().slice(0, 16);
            document.getElementById('ghiChuNhatKy').value = '';
            document.getElementById('tbodyVatTuNhatKy').innerHTML = '';
            document.getElementById('tbodyThietBiNhatKy').innerHTML = '';
            addVatTuRow();
            addThietBiRow();
            document.getElementById('titleNhatKy').textContent = 'Ghi nhật ký canh tác mới';
            openModal('modalNhatKy');
        });

        document.getElementById('formNhatKy').addEventListener('submit', function(e) {
            e.preventDefault();
            const id = document.getElementById('editNhatKyId').value;
            const data = {
                idKhuDat: parseInt(document.getElementById('idKhuDatNhatKy').value),
                idNhanVien: parseInt(document.getElementById('idNhanVienNhatKy').value),
                tenCongViec: document.getElementById('tenCongViecNhatKy').value.trim(),
                ngayThucHien: document.getElementById('ngayThucHienNhatKy').value,
                ghiChu: document.getElementById('ghiChuNhatKy').value.trim(),
                vatTu: [],
                thietBi: []
            };
            if (!data.idKhuDat || !data.idNhanVien || !data.tenCongViec) { alert('Vui lòng nhập đầy đủ thông tin bắt buộc'); return; }
            
            document.querySelectorAll('#tbodyVatTuNhatKy tr').forEach(function(tr) {
                const sel = tr.querySelector('.vt-select');
                const qty = tr.querySelector('.vt-qty');
                if (sel && qty && sel.value) {
                    data.vatTu.push({ id: parseInt(sel.value), soLuong: parseFloat(qty.value) || 0 });
                }
            });
            document.querySelectorAll('#tbodyThietBiNhatKy tr').forEach(function(tr) {
                const sel = tr.querySelector('.tb-select');
                const qty = tr.querySelector('.tb-qty');
                if (sel && qty && sel.value) {
                    data.thietBi.push({ id: parseInt(sel.value), soLuong: parseInt(qty.value) || 0 });
                }
            });

            if (id) {
                const n = nhatKyList.find(function(item) { return item.id === parseInt(id); });
                if (n) Object.assign(n, data);
            } else {
                data.id = nextNhatKyId++;
                nhatKyList.push(data);
            }
            closeModal('modalNhatKy');
            renderNhatKy();
        });

        // ========== SINH TRƯỞNG ==========
        function editSinhTruong(id) {
            const s = sinhTruongList.find(function(item) { return item.id === id; });
            if (!s) return;
            document.getElementById('editSinhTruongId').value = s.id;
            document.getElementById('idKhuDatSinhTruong').value = s.idKhuDat;
            document.getElementById('giaiDoanSinhTruong').value = s.giaiDoan;
            document.getElementById('tyLeSinhTruong').value = s.tyLe;
            document.getElementById('soCayConLai').value = s.soCayConLai;
            document.getElementById('titleSinhTruong').textContent = 'Cập nhật sinh trưởng';
            openModal('modalSinhTruong');
        }
        document.getElementById('btnAddSinhTruong').addEventListener('click', function() {
            document.getElementById('editSinhTruongId').value = '';
            document.getElementById('idKhuDatSinhTruong').value = '';
            document.getElementById('giaiDoanSinhTruong').value = 'Cây con';
            document.getElementById('tyLeSinhTruong').value = '50';
            document.getElementById('soCayConLai').value = '';
            document.getElementById('titleSinhTruong').textContent = 'Cập nhật sinh trưởng mới';
            openModal('modalSinhTruong');
        });
        document.getElementById('formSinhTruong').addEventListener('submit', function(e) {
            e.preventDefault();
            const id = document.getElementById('editSinhTruongId').value;
            const data = {
                idKhuDat: parseInt(document.getElementById('idKhuDatSinhTruong').value),
                giaiDoan: document.getElementById('giaiDoanSinhTruong').value,
                tyLe: parseInt(document.getElementById('tyLeSinhTruong').value) || 0,
                soCayConLai: parseInt(document.getElementById('soCayConLai').value) || 0,
                ngayCapNhat: new Date().toISOString().slice(0, 10)
            };
            if (!data.idKhuDat) { alert('Vui lòng nhập ID lô đất'); return; }
            if (id) {
                const s = sinhTruongList.find(function(item) { return item.id === parseInt(id); });
                if (s) Object.assign(s, data);
            } else {
                data.id = nextSinhTruongId++;
                sinhTruongList.push(data);
            }
            closeModal('modalSinhTruong');
            renderSinhTruong();
        });

        // ========== SÂU BỆNH ==========
        function editSauBenh(id) {
            const s = sauBenhList.find(function(item) { return item.id === id; });
            if (!s) return;
            document.getElementById('editSauBenhId').value = s.id;
            document.getElementById('idKhuDatSauBenh').value = s.idKhuDat;
            document.getElementById('loaiSauBenh').value = s.loai;
            document.getElementById('mucDoSauBenh').value = s.mucDo;
            document.getElementById('bienPhapSauBenh').value = s.bienPhap || '';
            document.getElementById('trangThaiSauBenh').value = s.trangThai;
            document.getElementById('titleSauBenh').textContent = 'Sửa sâu bệnh';
            openModal('modalSauBenh');
        }
        document.getElementById('btnAddSauBenh').addEventListener('click', function() {
            document.getElementById('editSauBenhId').value = '';
            document.getElementById('idKhuDatSauBenh').value = '';
            document.getElementById('loaiSauBenh').value = '';
            document.getElementById('mucDoSauBenh').value = 'Nhẹ';
            document.getElementById('bienPhapSauBenh').value = '';
            document.getElementById('trangThaiSauBenh').value = 'Chưa xử lý';
            document.getElementById('titleSauBenh').textContent = 'Ghi nhận sâu bệnh mới';
            openModal('modalSauBenh');
        });
        document.getElementById('formSauBenh').addEventListener('submit', function(e) {
            e.preventDefault();
            const id = document.getElementById('editSauBenhId').value;
            const data = {
                idKhuDat: parseInt(document.getElementById('idKhuDatSauBenh').value),
                loai: document.getElementById('loaiSauBenh').value.trim(),
                mucDo: document.getElementById('mucDoSauBenh').value,
                bienPhap: document.getElementById('bienPhapSauBenh').value.trim(),
                trangThai: document.getElementById('trangThaiSauBenh').value
            };
            if (!data.idKhuDat || !data.loai) { alert('Vui lòng nhập đầy đủ'); return; }
            if (id) {
                const s = sauBenhList.find(function(item) { return item.id === parseInt(id); });
                if (s) Object.assign(s, data);
            } else {
                data.id = nextSauBenhId++;
                sauBenhList.push(data);
            }
            closeModal('modalSauBenh');
            renderSauBenh();
        });

        // ========== THU HOẠCH ==========
        function editThuHoach(id) {
            const t = thuHoachList.find(function(item) { return item.id === id; });
            if (!t) return;
            document.getElementById('editThuHoachId').value = t.id;
            document.getElementById('idKhuDatThuHoach').value = t.idKhuDat;
            document.getElementById('tenVuMua').value = t.tenVuMua;
            document.getElementById('ngayThuHoach').value = t.ngayThuHoach;
            document.getElementById('tongSanLuong').value = t.tongSanLuong;
            document.getElementById('xepLoaiThuHoach').value = t.xepLoai || '';
            document.getElementById('ghiChuThuHoach').value = t.ghiChu || '';
            document.getElementById('titleThuHoach').textContent = 'Sửa thu hoạch';
            openModal('modalThuHoach');
        }
        document.getElementById('btnAddThuHoach').addEventListener('click', function() {
            document.getElementById('editThuHoachId').value = '';
            document.getElementById('idKhuDatThuHoach').value = '';
            document.getElementById('tenVuMua').value = '';
            const now = new Date();
            document.getElementById('ngayThuHoach').value = now.toISOString().slice(0, 16);
            document.getElementById('tongSanLuong').value = '';
            document.getElementById('xepLoaiThuHoach').value = '';
            document.getElementById('ghiChuThuHoach').value = '';
            document.getElementById('titleThuHoach').textContent = 'Ghi nhận thu hoạch mới';
            openModal('modalThuHoach');
        });
        document.getElementById('formThuHoach').addEventListener('submit', function(e) {
            e.preventDefault();
            const id = document.getElementById('editThuHoachId').value;
            const data = {
                idKhuDat: parseInt(document.getElementById('idKhuDatThuHoach').value),
                tenVuMua: document.getElementById('tenVuMua').value.trim(),
                ngayThuHoach: document.getElementById('ngayThuHoach').value,
                tongSanLuong: parseFloat(document.getElementById('tongSanLuong').value) || 0,
                xepLoai: document.getElementById('xepLoaiThuHoach').value.trim(),
                ghiChu: document.getElementById('ghiChuThuHoach').value.trim()
            };
            if (!data.idKhuDat || !data.tenVuMua || data.tongSanLuong <= 0) {
                alert('Vui lòng nhập đầy đủ thông tin');
                return;
            }
            if (id) {
                const t = thuHoachList.find(function(item) { return item.id === parseInt(id); });
                if (t) Object.assign(t, data);
            } else {
                data.id = nextThuHoachId++;
                thuHoachList.push(data);
            }
            closeModal('modalThuHoach');
            renderThuHoach();
        });

        // ========== FILTER ==========
        function filterTable(type) {
            const prefixMap = {
                giong: 'searchGiong', vuon: 'searchVuon', lich: 'searchLich',
                nhatky: 'searchNhatKy', sinhtruong: 'searchSinhTruong',
                saubenh: 'searchSauBenh', thuhoach: 'searchThuHoach'
            };
            const inputId = prefixMap[type];
            if (!inputId) return;
            const keyword = document.getElementById(inputId).value.toLowerCase();
            const tbodyId = 'tbody' + type.charAt(0).toUpperCase() + type.slice(1);
            const rows = document.querySelectorAll('#' + tbodyId + ' tr');
            rows.forEach(function(row) {
                row.style.display = row.textContent.toLowerCase().includes(keyword) ? '' : 'none';
            });
        }

        // ========== TAB SWITCHING ==========
        document.querySelectorAll('.tab-btn').forEach(function(btn) {
            btn.addEventListener('click', function() {
                document.querySelectorAll('.tab-btn').forEach(function(b) { b.classList.remove('active'); });
                this.classList.add('active');
                document.querySelectorAll('.tab-content').forEach(function(tc) { tc.classList.remove('active'); });
                document.getElementById(this.getAttribute('data-tab')).classList.add('active');
            });
        });

        // ========== SIDEBAR TOGGLE ==========
        const menuToggle = document.getElementById('menuToggle');
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('overlay');
        function toggleSidebar() { sidebar.classList.toggle('open'); overlay.classList.toggle('active'); }
        if (menuToggle) menuToggle.addEventListener('click', toggleSidebar);
        overlay.addEventListener('click', toggleSidebar);

        // ========== KHOI TAO ==========
        populateGiongSelect();
        const originalRenderGiong = renderGiong;
        renderGiong = function() {
            originalRenderGiong();
            populateGiongSelect();
        };

        renderGiong(); renderVuon(); renderLich();
        renderNhatKy(); renderSinhTruong(); renderSauBenh(); renderThuHoach();

        document.querySelectorAll('.modal-overlay').forEach(function(el) {
            el.addEventListener('click', function(e) {
                if (e.target === this) this.classList.remove('active');
            });
        });
    </script>
</body>
</html>