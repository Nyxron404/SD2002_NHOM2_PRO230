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
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Roboto', sans-serif; background: #f5f7fa; color: #333; display: flex; height: 100vh; overflow: hidden; }
        .main-content { flex: 1; display: flex; flex-direction: column; height: 100vh; overflow: hidden; }
        .content { flex: 1; padding: 28px 32px; overflow-y: auto; background: #f5f7fa; }
        .overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.3); z-index: 999; }
        .overlay.active { display: block; }
        
        /* TABS */
        .tabs { display: flex; gap: 4px; margin-bottom: 24px; background: #fff; border-radius: 12px; padding: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); border: 1px solid #e9edf4; }
        .tab-btn { padding: 12px 28px; border: none; background: transparent; border-radius: 8px; font-weight: 500; font-size: 15px; color: #6f8fb0; cursor: pointer; transition: all 0.2s; flex: 1; text-align: center; }
        .tab-btn:hover { background: #f0f2f7; color: #1e2a3a; }
        .tab-btn.active { background: #4d90fe; color: #fff; box-shadow: 0 2px 6px rgba(77,144,254,0.3); }
        .tab-content { display: none; } .tab-content.active { display: block; }
        
        /* TOOLBAR & BUTTONS */
        .toolbar { display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; margin-bottom: 24px; gap: 16px; }
        .toolbar .search-box { display: flex; align-items: center; background: #fff; border-radius: 8px; padding: 0 16px; border: 1px solid #e9edf4; flex: 1 1 300px; }
        .toolbar .search-box i { color: #8aa3c0; margin-right: 10px; }
        .toolbar .search-box input { border: none; padding: 10px 0; font-size: 14px; width: 100%; outline: none; background: transparent; }
        .toolbar .actions { display: flex; gap: 12px; flex-wrap: wrap; }
        
        .btn { padding: 10px 20px; border: none; border-radius: 8px; font-weight: 500; font-size: 14px; cursor: pointer; display: inline-flex; align-items: center; gap: 8px; transition: 0.2s; text-decoration: none; }
        .btn-primary { background: #4d90fe; color: #fff; } .btn-primary:hover { background: #3a7bd5; }
        .btn-success { background: #6fcf97; color: #fff; } .btn-success:hover { background: #52b381; }
        .btn-warning { background: #f39c12; color: #fff; } .btn-warning:hover { background: #d68910; }
        .btn-outline { background: transparent; color: #4a5b6e; border: 1px solid #d0d8e3; } .btn-outline:hover { background: #e9edf4; }
        
        /* ACTIONS */
        .actions-cell { display: flex; gap: 8px; justify-content: center; }
        .btn-action { width: 38px; height: 32px; border: none; border-radius: 8px; color: #fff; font-size: 14px; cursor: pointer; display: inline-flex; align-items: center; justify-content: center; transition: 0.2s; }
        .btn-action.edit { background-color: #4d90fe; }
        .btn-action.import { background-color: #6fcf97; }
        .btn-action.export { background-color: #f39c12; }
        .btn-action.delete { background-color: #e74c3c; }
        .btn-action.maintain { background-color: #8aa3c0; }
        .btn-action:hover { opacity: 0.8; transform: translateY(-2px); }

        /* TABLE */
        .table-container { background: #fff; border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.04); overflow-x: auto; padding: 0 0 8px 0; }
        .data-table { width: 100%; border-collapse: collapse; font-size: 14px; min-width: 800px; }
        .data-table th { text-align: left; padding: 16px 16px 12px 16px; color: #6f8fb0; font-weight: 500; border-bottom: 1px solid #e9edf4; background: #fafbfc; white-space: nowrap; }
        .data-table td { padding: 14px 16px; border-bottom: 1px solid #f0f2f7; vertical-align: middle; }
        .data-table tr:hover td { background: #f8faff; }
        .badge-status { display: inline-block; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }
        .badge-status.available { background: #d5f5e3; color: #1e8449; }
        .badge-status.inuse { background: #fdebd0; color: #a04000; }
        .badge-status.maintenance { background: #fadbd8; color: #922b21; }
        
        /* MODALS */
        .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.4); z-index: 9999; justify-content: center; align-items: center; backdrop-filter: blur(2px); }
        .modal-overlay.active { display: flex; }
        .modal { background: #fff; border-radius: 20px; width: 95%; max-width: 700px; max-height: 90vh; overflow-y: auto; padding: 32px; box-shadow: 0 20px 60px rgba(0,0,0,0.2); animation: fadeInUp 0.25s ease; }
        .modal.large { max-width: 1100px; }
        @keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
        .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
        .modal-header h2 { font-size: 22px; font-weight: 500; color: #1e2a3a; }
        .modal-header .close-modal { background: none; border: none; font-size: 28px; cursor: pointer; color: #8aa3c0; transition: color 0.2s; }
        .modal-header .close-modal:hover { color: #e74c3c; }
        .form-group { margin-bottom: 18px; }
        .form-group label { display: block; font-weight: 500; margin-bottom: 6px; color: #2c3e50; font-size: 14px; }
        .form-group input, .form-group select { width: 100%; padding: 10px 14px; border: 1px solid #d0d8e3; border-radius: 8px; font-size: 14px; transition: border 0.2s; background: #fafbfc; }
        .form-group input[type="file"] { padding: 7px 10px; background: #fff; line-height: 1.5; cursor: pointer; }
        .form-group input:focus, .form-group select:focus { border-color: #4d90fe; outline: none; background: #fff; }
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
        @media (max-width: 600px) { .tabs { flex-direction: column; } .tab-btn { flex: none; } .toolbar { flex-direction: column; align-items: stretch; } .form-row .form-group { flex: 1 1 100%; } }
    </style>
</head>
<body>

    <!-- NHÚNG THEO ĐƯỜNG DẪN TUYỆT ĐỐI -->
    <%@ include file="/views/commons/sidebar.jsp" %>
    <div class="overlay" id="overlay"></div>

    <div class="main-content">
        <%@ include file="/views/commons/header.jsp" %>
        <script>
            document.addEventListener("DOMContentLoaded", function() {
                var pageTitle = document.getElementById('pageHeaderTitle');
                if (pageTitle) pageTitle.innerHTML = 'Quản lý Thiết bị & Dụng cụ <span>| Bãi đỗ & Khu lưu trữ</span>';
            });
        </script>

        <section class="content">
            <div class="tabs">
                <button class="tab-btn active" data-tab="tabDungCu"><i class="fas fa-tools"></i> Dụng cụ cầm tay</button>
                <button class="tab-btn" data-tab="tabThietBi"><i class="fas fa-tractor"></i> Thiết bị máy móc lớn</button>
            </div>

            <!-- ===== TAB DỤNG CỤ ===== -->
            <div id="tabDungCu" class="tab-content active">
                <div class="toolbar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" placeholder="Tìm kiếm dụng cụ..." id="searchDungCu" oninput="filterTable('dungcu')">
                    </div>
                    <div class="actions">
                        <button class="btn btn-primary" id="btnAddDungCu"><i class="fas fa-plus"></i> Khai báo danh mục</button>
                        <button class="btn btn-success" id="btnNhapDungCuToolbar"><i class="fas fa-file-invoice"></i> Lập phiếu nhập khu</button>
                    </div>
                </div>
                <div class="table-container">
                    <table class="data-table" id="tableDungCu">
                        <thead>
                            <tr>
                                <th>Mã ID</th>
                                <th>Tên dụng cụ</th>
                                <th>Khu vực lưu trữ</th>
                                <th>Đơn vị tính</th>
                                <th>Tồn khu</th>
                                <th>Cảnh báo (Tối thiểu)</th>
                                <th>Trạng thái tồn</th>
                                <th style="text-align:center;">Kho / Cấp phát</th>
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
                        <button class="btn btn-primary" id="btnAddThietBi"><i class="fas fa-plus"></i> Khai báo thiết bị mới</button>
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

    <!-- ==================== CÁC MODAL THAO TÁC NGHIỆP VỤ ==================== -->

    <!-- 1. MODAL CẤP PHÁT DỤNG CỤ (ĐÃ XÓA NHÂN VIÊN NHẬN THEO NGHIỆP VỤ) -->
    <div class="modal-overlay" id="modalExportDungCu">
        <div class="modal">
            <div class="modal-header">
                <h2><i class="fas fa-arrow-up"></i> Cấp Phát Dụng Cụ</h2>
                <button class="close-modal" onclick="document.getElementById('modalExportDungCu').classList.remove('active')">&times;</button>
            </div>
            <form id="formExportDungCu">
                <input type="hidden" id="exportDcId" value="">
                
                <div class="form-row">
                    <div class="form-group" style="flex: 2;">
                        <label>Tên dụng cụ</label>
                        <p id="exportDcName" style="font-weight:500; margin-top:4px; font-size:16px; color:#4d90fe;"></p>
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label>Tồn khu hiện tại</label>
                        <p id="exportDcStock" style="font-weight:bold; margin-top:4px; color:#1e8449; font-size:16px;"></p>
                    </div>
                </div>
                
                <div class="form-row">
                    <!-- Điều động dụng cụ thẳng ra Khu đất / Lô đất -->
                    <div class="form-group" style="flex: 2;">
                        <label>Khu Đất / Lô Đất sử dụng (ID) <span style="color:#e74c3c;">*</span></label>
                        <input type="number" id="exportDcKhuDat" required placeholder="Nhập ID Khu Đất / Lô đất">
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label>Ngày xuất <span style="color:#e74c3c;">*</span></label>
                        <input type="date" id="exportDcDate" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group" style="flex: 1;">
                        <label>Số lượng cấp phát <span style="color:#e74c3c;">*</span></label>
                        <input type="number" id="exportDcQty" step="0.01" required>
                    </div>
                    <div class="form-group" style="flex: 2;">
                        <label>Mô tả / Hoạt động chăm sóc</label>
                        <input type="text" id="exportDcReason" placeholder="VD: Phục vụ thu hoạch, bón phân đợt 1...">
                    </div>
                </div>

                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="document.getElementById('modalExportDungCu').classList.remove('active')">Hủy</button>
                    <button type="submit" class="btn btn-warning"><i class="fas fa-check"></i> Xác nhận cấp phát</button>
                </div>
            </form>
        </div>
    </div>


    <!-- 2. MODAL LẬP PHIẾU NHẬP DỤNG CỤ -->
    <div class="modal-overlay" id="modalImportDungCu">
        <div class="modal" style="max-width: 1000px;">
            <div class="modal-header">
                <h2><i class="fas fa-file-invoice"></i> Lập Phiếu Nhập Dụng Cụ</h2>
                <button class="close-modal" onclick="document.getElementById('modalImportDungCu').classList.remove('active')">&times;</button>
            </div>
            <form id="formImportDungCu" enctype="multipart/form-data">
                <div style="background: #f8faff; padding: 16px; border-radius: 12px; margin-bottom: 20px; border: 1px solid #e9edf4;">
                    <h4 style="margin-bottom: 12px; color: #1e2a3a;"><i class="fas fa-receipt"></i> Thông tin chứng từ</h4>

                    <div class="form-row">
                        <div class="form-group"><label>Mã phiếu nhập <span style="color:#e74c3c;">*</span></label><input type="text" id="dcMaHoaDon" required></div>
                        <div class="form-group"><label>Loại phiếu nhập</label><input type="text" id="dcLoaiPhieu" value="Nhập Dụng Cụ" readonly style="background:#e9edf4; cursor:not-allowed;"></div>
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
                        <div class="form-group"><label>Mã số thuế NCC</label><input type="text" id="dcMST"></div>
                    </div>

                    <div class="form-row">
                        <div class="form-group"><label>Người mua hàng <span style="color:#e74c3c;">*</span></label><input type="text" id="dcNguoiMua" required></div>
                        <div class="form-group"><label>Người bán hàng</label><input type="text" id="dcNguoiBan"></div>
                    </div>

                    <div class="form-row">
                        <div class="form-group" style="flex:2;">
                            <label>Upload Bản gốc hóa đơn (Ảnh/PDF) <span style="color:#e74c3c;">*</span></label>
                            <input type="file" id="dcAnhHoaDon" accept=".jpg, .jpeg, .png, .pdf" required>
                        </div>
                        <div class="form-group" style="flex:1;"><label>Ghi chú</label><input type="text" id="dcGhiChu"></div>
                    </div>
                </div>

                <h4 style="margin-bottom: 12px;"><i class="fas fa-list-ul"></i> Chi tiết Dụng cụ nhập</h4>
                <div style="overflow-x: auto;">
                    <table class="detail-table" id="importDungCuTable">
                        <thead>
                            <tr>
                                <th style="width:30%;">Tên Dụng cụ</th>
                                <th style="width:15%;">Số lượng</th>
                                <th style="width:20%;">Đơn giá (VNĐ)</th>
                                <th style="width:25%;">Thành tiền</th>
                                <th style="width:10%;"></th>
                            </tr>
                        </thead>
                        <tbody id="importDungCuBody"></tbody>
                    </table>
                </div>
                <div style="margin-top: 12px;">
                    <button type="button" class="btn btn-outline btn-sm" id="addDcRowBtn"><i class="fas fa-plus"></i> Thêm dòng dụng cụ</button>
                </div>

                <div class="total-summary" style="flex-wrap: wrap;">
                    <span>Tổng tiền hàng: <strong class="amount" id="dcTongTienHang">0</strong> VNĐ</span>
                    <span>Thuế GTGT: <input type="number" id="dcTienThueGTGT" value="0" style="width:100px; margin-left:10px; border:1px solid #d0d8e3; padding:4px;" oninput="calcDcTotal()"> VNĐ</span>
                    <span>Tổng thanh toán: <strong class="amount" style="color:#e74c3c;" id="dcTongThanhToan">0</strong> VNĐ</span>
                </div>

                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="document.getElementById('modalImportDungCu').classList.remove('active')">Hủy</button>
                    <button type="submit" class="btn btn-success"><i class="fas fa-check"></i> Xác nhận nhập khu</button>
                </div>
            </form>
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
                tonKhuHienTai: ${dc.getTon_kho_hien_tai()},
                tonKhuToiThieu: ${dc.getTon_kho_toi_thieu()},
                trangThai: '${dc.getTrang_thai()}'
            }${!loop.last ? ',' : ''}
            </c:forEach>
        ];

        // MOCK DATA CHO THIẾT BỊ (Làm sau)
        let thietBiList = [
            { id: 1, maThietBi: 'TB-MC01', tenThietBi: 'Máy cày Kubota 45HP', dienTichChiemDung: 5.5, trangThai: 1, idKhuVuc: 4 }
        ];

        // ===== TAB SWITCHING =====
        document.querySelectorAll('.tab-btn').forEach(function(btn) {
            btn.addEventListener('click', function() {
                document.querySelectorAll('.tab-btn').forEach(function(b) { b.classList.remove('active'); });
                this.classList.add('active');
                document.querySelectorAll('.tab-content').forEach(function(tc) { tc.classList.remove('active'); });
                document.getElementById(this.getAttribute('data-tab')).classList.add('active');
            });
        });

        // ===== RENDER BẢNG DỤNG CỤ =====
        function renderDungCu() {
            const tbody = document.getElementById('tbodyDungCu');
            tbody.innerHTML = '';
            dungCuList.forEach(function(dc) {
                const isLow = dc.tonKhuHienTai < dc.tonKhuToiThieu;
                const statusClass = isLow ? 'maintenance' : 'available';
                const statusText = isLow ? 'Cần mua thêm' : 'Đảm bảo';
                const textColor = isLow ? '#e74c3c' : '#333';

                let trHtml = "<tr>";
                trHtml += "<td>" + dc.maDungCu + "</td>";
                trHtml += "<td><strong>" + dc.tenDungCu + "</strong></td>";
                trHtml += "<td>Khu Dụng cụ</td>";
                trHtml += "<td>" + dc.donViTinh + "</td>";
                trHtml += "<td><strong style='color: " + textColor + "'>" + dc.tonKhuHienTai + "</strong></td>";
                trHtml += "<td>" + dc.tonKhuToiThieu + "</td>";
                trHtml += "<td><span class='badge-status " + statusClass + "'>" + statusText + "</span></td>";
                
                trHtml += "<td><div class='actions-cell'>";
                trHtml += "<button class='btn-action import btn-nhap-dc' title='Nhập khu' data-id='" + dc.id + "'><i class='fas fa-arrow-down'></i></button>";
                trHtml += "<button class='btn-action export btn-xuat-dc' title='Cấp phát' data-id='" + dc.id + "'><i class='fas fa-arrow-up'></i></button>";
                trHtml += "</div></td></tr>";

                const tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });
            
            document.querySelectorAll('.btn-nhap-dc').forEach(function(b) { 
                b.addEventListener('click', function() { openImportDungCuModal(parseInt(this.getAttribute('data-id'))); }); 
            });
            document.querySelectorAll('.btn-xuat-dc').forEach(function(b) { 
                b.addEventListener('click', function() { openExportDungCuModal(parseInt(this.getAttribute('data-id'))); }); 
            });
        }

        // ===== RENDER BẢNG THIẾT BỊ =====
        function renderThietBi() {
            const tbody = document.getElementById('tbodyThietBi');
            tbody.innerHTML = '';
            thietBiList.forEach(function(tb) {
                let statusClass = tb.trangThai === 1 ? 'available' : 'inuse';
                let trHtml = "<tr>";
                trHtml += "<td><strong>" + tb.maThietBi + "</strong></td>";
                trHtml += "<td>" + tb.tenThietBi + "</td>";
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
        }


        // ===== LOGIC FORM NHẬP KHO DỤNG CỤ VỚI FETCH API THỰC TẾ =====
        let importDcRows = [];

        function addImportDcRow(dungCuId, soLuong, donGia) {
            importDcRows.push({ id: Date.now() + Math.random(), dungCuId: dungCuId, soLuong: soLuong, donGia: donGia });
            renderImportDcRows();
        }

        function removeImportDcRow(rowId) {
            importDcRows = importDcRows.filter(function(r) { return r.id !== rowId; });
            renderImportDcRows();
        }

        function renderImportDcRows() {
            const tbody = document.getElementById('importDungCuBody');
            tbody.innerHTML = '';

            importDcRows.forEach(function(row) {
                let opts = "";
                dungCuList.forEach(function(dc) {
                    let sel = (dc.id === row.dungCuId) ? "selected" : "";
                    opts += "<option value='" + dc.id + "' " + sel + ">" + dc.tenDungCu + "</option>";
                });

                let trHtml = "<tr>";
                trHtml += "<td><select class='import-dc-select' data-rowid='" + row.id + "'>" + opts + "</select></td>";
                trHtml += "<td><input type='number' class='import-dc-qty' data-rowid='" + row.id + "' value='" + row.soLuong + "' step='0.01' min='0.01'></td>";
                trHtml += "<td><input type='number' class='import-dc-price' data-rowid='" + row.id + "' value='" + row.donGia + "' step='1000' min='0'></td>";
                trHtml += "<td class='import-dc-amount'>" + (row.soLuong * row.donGia).toLocaleString() + "</td>";
                trHtml += "<td><button type='button' class='btn-remove-row' onclick='removeImportDcRow(" + row.id + ")'><i class='fas fa-times'></i></button></td>";
                trHtml += "</tr>";

                const tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });

            document.querySelectorAll('.import-dc-select, .import-dc-qty, .import-dc-price').forEach(function(inp) {
                inp.addEventListener('input', function() {
                    const rowId = parseFloat(this.getAttribute('data-rowid'));
                    const r = importDcRows.find(function(x) { return x.id === rowId; });
                    if (r) {
                        if (this.classList.contains('import-dc-select')) r.dungCuId = parseInt(this.value);
                        if (this.classList.contains('import-dc-qty')) r.soLuong = parseFloat(this.value) || 0;
                        if (this.classList.contains('import-dc-price')) r.donGia = parseFloat(this.value) || 0;
                        this.closest('tr').querySelector('.import-dc-amount').textContent = (r.soLuong * r.donGia).toLocaleString();
                    }
                    calcDcTotal();
                });
            });
            calcDcTotal();
        }

        function calcDcTotal() {
            let tongHang = 0;
            importDcRows.forEach(function(row) { tongHang += (row.soLuong * row.donGia); });
            const thue = parseFloat(document.getElementById('dcTienThueGTGT').value) || 0;

            document.getElementById('dcTongTienHang').textContent = tongHang.toLocaleString();
            document.getElementById('dcTongThanhToan').textContent = (tongHang + thue).toLocaleString();
        }

        document.getElementById('addDcRowBtn').addEventListener('click', function() {
            if(dungCuList.length === 0) { alert('Chưa có dụng cụ trong danh mục!'); return; }
            addImportDcRow(dungCuList[0].id, 1, 0);
        });

        function openImportDungCuModal(id) {
            importDcRows = [];
            document.getElementById('formImportDungCu').reset();
            document.getElementById('dcMaHoaDon').value = 'HD-DC' + Date.now().toString().slice(-5);
            document.getElementById('dcNgayHoaDon').value = new Date().toISOString().slice(0, 10);
            
            if(id) addImportDcRow(id, 1, 0);
            else if(dungCuList.length > 0) addImportDcRow(dungCuList[0].id, 1, 0);
            
            document.getElementById('modalImportDungCu').classList.add('active');
        }

        document.getElementById('btnNhapDungCuToolbar').addEventListener('click', function() {
            openImportDungCuModal(null);
        });
        
        // GỬI DỮ LIỆU NHẬP DỤNG CỤ LÊN SERVLET
        document.getElementById('formImportDungCu').addEventListener('submit', function(e) {
            e.preventDefault();
            if(importDcRows.length === 0) {
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
            if (fileAnh) formData.append("anh_hoa_don", fileAnh);
            
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
                    location.reload(); // Tải lại trang để lấy tồn kho mới
                } else {
                    alert('Lỗi: ' + data.error);
                }
            })
            .catch(err => {
                alert('Không gửi được dữ liệu lên máy chủ: ' + err.message);
            });
        });

        // ===== CẤP PHÁT DỤNG CỤ =====
        function openExportDungCuModal(id) {
            const dc = dungCuList.find(function(x) { return x.id === id; });
            document.getElementById('exportDcId').value = dc.id;
            document.getElementById('exportDcName').textContent = dc.tenDungCu;
            document.getElementById('exportDcStock').textContent = dc.tonKhuHienTai + " " + dc.donViTinh;
            document.getElementById('exportDcDate').value = new Date().toISOString().slice(0, 10);
            
            document.getElementById('formExportDungCu').reset();
            document.getElementById('exportDcDate').value = new Date().toISOString().slice(0, 10);
            document.getElementById('modalExportDungCu').classList.add('active');
        }

        document.getElementById('formExportDungCu').addEventListener('submit', function(e) {
            e.preventDefault();
            const id = parseInt(document.getElementById('exportDcId').value);
            const qty = parseFloat(document.getElementById('exportDcQty').value);
            const dc = dungCuList.find(function(x) { return x.id === id; });

            if(qty > dc.tonKhuHienTai) {
                alert("Số lượng cấp phát yêu cầu vượt quá tồn khu hiện tại!");
                return;
            }

            dc.tonKhuHienTai -= qty; 
            document.getElementById('modalExportDungCu').classList.remove('active');
            renderDungCu();
            alert("Đã cấp phát dụng cụ ra Khu đất thành công! (Dữ liệu tạm)");
        });

        // Hàm tự động điền Mã số thuế khi chọn Nhà cung cấp
        function tuDongDienMSTDC() {
            var selectNCC = document.getElementById("dcNhaCungCapId");
            var selectedOption = selectNCC.options[selectNCC.selectedIndex];
            var mst = selectedOption.getAttribute("data-mst");
            var inputMST = document.getElementById("dcMST");
            if (inputMST) inputMST.value = (mst && mst !== 'null') ? mst : "";
        }

        // TÌM KIẾM
        function filterTable(type) {
            const keyword = document.getElementById(type === 'dungcu' ? 'searchDungCu' : 'searchThietBi').value.toLowerCase();
            const rows = document.querySelectorAll(type === 'dungcu' ? '#tbodyDungCu tr' : '#tbodyThietBi tr');
            rows.forEach(function(row) { row.style.display = row.textContent.toLowerCase().includes(keyword) ? '' : 'none'; });
        }

        // Sidebar mobile
        const menuToggle = document.getElementById('menuToggle');
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('overlay');
        function toggleSidebar() { sidebar.classList.toggle('open'); overlay.classList.toggle('active'); }
        if(menuToggle) menuToggle.addEventListener('click', toggleSidebar);
        overlay.addEventListener('click', toggleSidebar);

        // INIT
        renderDungCu();
        renderThietBi();
    </script>
</body>
</html>