<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Vật tư - Trang trại Sầu Riêng</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <style>
        /* BASE & LAYOUT */
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Roboto', sans-serif; background: #f5f7fa; color: #333; display: flex; height: 100vh; overflow: hidden; }
        .main-content { flex: 1; display: flex; flex-direction: column; height: 100vh; overflow: hidden; }
        .content { flex: 1; padding: 28px 32px; overflow-y: auto; background: #f5f7fa; }
        .overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.3); z-index: 999; }
        .overlay.active { display: block; }
        
        /* TOOLBAR */
        .toolbar { display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; margin-bottom: 24px; gap: 16px; }
        .toolbar .search-box { display: flex; align-items: center; background: #fff; border-radius: 8px; padding: 0 16px; border: 1px solid #e9edf4; flex: 1 1 300px; }
        .toolbar .search-box i { color: #8aa3c0; margin-right: 10px; }
        .toolbar .search-box input { border: none; padding: 10px 0; font-size: 14px; width: 100%; outline: none; background: transparent; }
        .toolbar .actions { display: flex; gap: 12px; flex-wrap: wrap; }
        
        /* GENERAL BUTTONS */
        .btn { padding: 10px 20px; border: none; border-radius: 8px; font-weight: 500; font-size: 14px; cursor: pointer; display: inline-flex; align-items: center; gap: 8px; transition: 0.2s; text-decoration: none; }
        .btn-primary { background: #4d90fe; color: #fff; } .btn-primary:hover { background: #3a7bd5; }
        .btn-success { background: #6fcf97; color: #fff; } .btn-success:hover { background: #52b381; }
        .btn-warning { background: #f39c12; color: #fff; } .btn-warning:hover { background: #d68910; }
        .btn-outline { background: transparent; color: #4a5b6e; border: 1px solid #d0d8e3; } .btn-outline:hover { background: #e9edf4; }
        
        /* CỤM 4 NÚT THAO TÁC THEO ẢNH MẪU */
        .actions-cell { display: flex; gap: 8px; justify-content: center; }
        .btn-action { width: 38px; height: 32px; border: none; border-radius: 8px; color: #fff; font-size: 14px; cursor: pointer; display: inline-flex; align-items: center; justify-content: center; transition: 0.2s; }
        .btn-action.edit { background-color: #4d90fe; }
        .btn-action.import { background-color: #6fcf97; }
        .btn-action.export { background-color: #f39c12; }
        .btn-action.delete { background-color: #e74c3c; }
        .btn-action:hover { opacity: 0.8; transform: translateY(-2px); }

        /* TABLE */
        .table-container { background: #fff; border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.04); overflow-x: auto; padding: 0 0 8px 0; }
        .material-table { width: 100%; border-collapse: collapse; font-size: 14px; min-width: 800px; }
        .material-table th { text-align: left; padding: 16px 16px 12px 16px; color: #6f8fb0; font-weight: 500; border-bottom: 1px solid #e9edf4; background: #fafbfc; white-space: nowrap; }
        .material-table td { padding: 14px 16px; border-bottom: 1px solid #f0f2f7; vertical-align: middle; }
        .material-table tr:hover td { background: #f8faff; }
        .badge-status { display: inline-block; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }
        .badge-status.under { background: #fdebd0; color: #a04000; }
        .badge-status.ok { background: #d5f5e3; color: #1e8449; }
        .badge-status.low { background: #fadbd8; color: #922b21; }

        /* MODALS */
        .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.4); z-index: 9999; justify-content: center; align-items: center; backdrop-filter: blur(2px); }
        .modal-overlay.active { display: flex; }
        .modal { background: #fff; border-radius: 20px; width: 95%; max-width: 1000px; max-height: 90vh; overflow-y: auto; padding: 32px; box-shadow: 0 20px 60px rgba(0,0,0,0.2); animation: fadeInUp 0.25s ease; }
        @keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
        .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
        .modal-header h2 { font-size: 22px; font-weight: 500; color: #1e2a3a; }
        .modal-header .close-modal { background: none; border: none; font-size: 28px; cursor: pointer; color: #8aa3c0; transition: color 0.2s; }
        .modal-header .close-modal:hover { color: #e74c3c; }
        .form-group { margin-bottom: 18px; }
        .form-group label { display: block; font-weight: 500; margin-bottom: 6px; color: #2c3e50; font-size: 14px; }
        .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 10px 14px; border: 1px solid #d0d8e3; border-radius: 8px; font-size: 14px; transition: border 0.2s; background: #fafbfc; }
        
        /* CSS cho nút upload file */
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
    </style>
</head>
<body>

    <!-- ĐƯỜNG DẪN TUYỆT ĐỐI -->
    <%@ include file="/views/commons/sidebar.jsp" %>
    <div class="overlay" id="overlay"></div>

    <div class="main-content">
        <%@ include file="/views/commons/header.jsp" %>
        <script>
            document.addEventListener("DOMContentLoaded", function() {
                var pageTitle = document.getElementById('pageHeaderTitle');
                if (pageTitle) pageTitle.innerHTML = 'Quản lý Vật tư <span>| Danh mục & Khu lưu trữ</span>';
            });
        </script>

        <section class="content">
            <div class="toolbar">
                <div class="search-box">
                    <i class="fas fa-search"></i>
                    <input type="text" placeholder="Tìm kiếm mã vật tư, tên vật tư..." id="searchInput" oninput="filterTable()">
                </div>
                <div class="actions">
                    <button class="btn btn-primary" id="btnAddMaterial"><i class="fas fa-plus"></i> Khai báo danh mục</button>
                    <button class="btn btn-success" id="btnImportInvoice"><i class="fas fa-file-invoice"></i> Lập phiếu nhập khu</button>
                </div>
            </div>

            <div class="table-container">
                <table class="material-table" id="materialTable">
                    <thead>
                        <tr>
                            <th>Mã VT</th>
                            <th>Tên vật tư</th>
                            <th>Khu vực (Lưu trữ)</th>
                            <th>Đơn vị</th>
                            <th>Quy cách (m²)</th>
                            <th>Tồn khu hiện tại</th>
                            <th>Sức chứa (Tối đa)</th>
                            <th>Cảnh báo (Tối thiểu)</th>
                            <th>Trạng thái tồn</th>
                            <th style="text-align:center;">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody id="materialTableBody">
                        <!-- Render JS -->
                    </tbody>
                </table>
            </div>
        </section>
    </div>

    <!-- ===== MODAL KHAI BÁO VẬT TƯ CHUẨN CSDL ===== -->
    <div class="modal-overlay" id="materialModal">
        <div class="modal" style="max-width: 700px;">
            <div class="modal-header">
                <h2 id="modalTitle">Khai báo Vật tư mới</h2>
                <button class="close-modal" id="closeMaterialModal">&times;</button>
            </div>
            <form id="materialForm">
                <input type="hidden" id="editId" value="">
                <div class="form-row">
                    <div class="form-group"><label>Mã vật tư <span style="color:#e74c3c;">*</span></label><input type="text" id="maVatTu" placeholder="VD: VT001" required></div>
                    <div class="form-group"><label>Tên vật tư <span style="color:#e74c3c;">*</span></label><input type="text" id="tenVatTu" placeholder="VD: Phân NPK 16-16-8" required></div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>Khu vực lưu trữ (Chuẩn VietGAP)</label>
                        <select id="idKhuVuc">
                            <option value="1">Khu Phân bón</option>
                            <option value="2">Khu Thuốc Bảo vệ thực vật</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Đơn vị tính</label>
                        <select id="donViTinh">
                            <option value="Kg">Kg</option>
                            <option value="Lít">Lít</option>
                            <option value="Gói">Gói</option>
                            <option value="Bao">Bao</option>
                        </select>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Quy cách đóng gói (m² chiếm chỗ/đơn vị)</label><input type="number" id="quyCachDongGoi" step="0.01" value="0.5"></div>
                    <div class="form-group"><label>Tồn khu hiện tại</label><input type="number" id="tonKhuHienTai" step="0.01" value="0" readonly style="background:#f0f2f7; cursor:not-allowed;"></div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Sức chứa tối đa của khu</label><input type="number" id="tonKhuToiDa" step="0.01" value="100"></div>
                    <div class="form-group"><label>Ngưỡng cảnh báo an toàn</label><input type="number" id="mucCanhBaoAnToan" step="0.01" value="10"></div>
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-outline" id="cancelMaterial">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu danh mục</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== MODAL HÓA ĐƠN NHẬP KHU ===== -->
    <div class="modal-overlay" id="importModal">
        <div class="modal" style="max-width: 1000px;">
            <div class="modal-header">
                <h2><i class="fas fa-file-invoice"></i> Lập Phiếu Nhập Khu (Hóa Đơn)</h2>
                <button class="close-modal" id="closeImportModal">&times;</button>
            </div>
            <form id="formImportHoaDon" enctype="multipart/form-data">
                <div style="background: #f8faff; padding: 16px; border-radius: 12px; margin-bottom: 20px; border: 1px solid #e9edf4;">
                    <h4 style="margin-bottom: 12px; color: #1e2a3a;"><i class="fas fa-receipt"></i> Thông tin chứng từ / Hóa đơn</h4>
                    <div class="form-row">
                        <div class="form-group"><label>Mã hóa đơn <span style="color:#e74c3c;">*</span></label><input type="text" id="hdMaHoaDon" required></div>
                        <div class="form-group"><label>Mẫu số</label><input type="text" id="hdMauSo"></div>
                        <div class="form-group"><label>Ký hiệu</label><input type="text" id="hdKyHieu"></div>
                    </div>
                    <div class="form-row">
                        <div class="form-group"><label>Ngày lập <span style="color:#e74c3c;">*</span></label><input type="datetime-local" id="hdNgayLap" required></div>
                        <div class="form-group"><label>Tên nhà cung cấp <span style="color:#e74c3c;">*</span></label><input type="text" id="hdTenNCC" required></div>
                        <div class="form-group"><label>Mã số thuế NCC</label><input type="text" id="hdMST"></div>
                    </div>
                    <div class="form-row">
                        <div class="form-group"><label>Người mua hàng <span style="color:#e74c3c;">*</span></label><input type="text" id="hdNguoiMua" required></div>
                        <div class="form-group"><label>Người bán hàng</label><input type="text" id="hdNguoiBan"></div>
                    </div>
                    
                    <!-- DÒNG ẢNH HÓA ĐƠN VÀ GHI CHÚ -->
                    <div class="form-row">
                        <div class="form-group" style="flex:2;">
                            <label>Upload Bản gốc hóa đơn (Ảnh/PDF) <span style="color:#e74c3c;">*</span></label>
                            <input type="file" id="hdAnhHoaDon" accept=".jpg, .jpeg, .png, .pdf" required>
                        </div>
                        <div class="form-group" style="flex:1;"><label>Ghi chú</label><input type="text" id="hdGhiChu"></div>
                    </div>

                    <!-- CHỮ KÝ ĐƯỢC CHUYỂN LÊN ĐÂY, NGAY DƯỚI ẢNH HÓA ĐƠN -->
                    <div class="form-row" style="margin-top: 10px; border-top: 1px dashed #d0d8e3; padding-top: 16px;">
                        <div class="form-group">
                            <label>Upload Chữ ký Người mua (Ảnh/PDF)</label>
                            <input type="file" id="hdAnhMua" accept=".jpg, .jpeg, .png, .pdf">
                        </div>
                        <div class="form-group">
                            <label>Upload Chữ ký Người bán (Ảnh/PDF)</label>
                            <input type="file" id="hdAnhBan" accept=".jpg, .jpeg, .png, .pdf">
                        </div>
                    </div>
                </div>

                <h4 style="margin-bottom: 12px;"><i class="fas fa-list-ul"></i> Chi tiết Vật tư nhập</h4>
                <div style="overflow-x: auto;">
                    <table class="detail-table" id="importDetailTable">
                        <thead>
                            <tr>
                                <th style="width:35%;">Tên Vật tư</th>
                                <th style="width:15%;">Số lượng</th>
                                <th style="width:20%;">Đơn giá (VNĐ)</th>
                                <th style="width:20%;">Thành tiền</th>
                                <th style="width:10%;"></th>
                            </tr>
                        </thead>
                        <tbody id="importDetailBody"></tbody>
                    </table>
                </div>
                <div style="margin-top: 12px;">
                    <button type="button" class="btn btn-outline btn-sm" id="addDetailRow"><i class="fas fa-plus"></i> Thêm dòng vật tư</button>
                </div>

                <div class="total-summary" style="flex-wrap: wrap;">
                    <span>Tổng tiền hàng: <strong class="amount" id="tongTienHang">0</strong> VNĐ</span>
                    <span>Thuế GTGT: <input type="number" id="tienThueGTGT" value="0" style="width:100px; margin-left:10px; border:1px solid #d0d8e3; padding:4px;" oninput="calcTotal()"> VNĐ</span>
                    <span>Tổng thanh toán: <strong class="amount" style="color:#e74c3c;" id="tongThanhToan">0</strong> VNĐ</span>
                </div>

                <div class="form-actions">
                    <button type="button" class="btn btn-outline" id="cancelImport">Hủy</button>
                    <button type="submit" class="btn btn-success"><i class="fas fa-check"></i> Xác nhận nhập khu</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== MODAL XUẤT KHU ===== -->
    <div class="modal-overlay" id="exportModal">
        <div class="modal" style="max-width: 700px;">
            <div class="modal-header">
                <h2><i class="fas fa-arrow-up"></i> Xuất khu / Cấp phát vật tư</h2>
                <button class="close-modal" id="closeExportModal">&times;</button>
            </div>
            <form id="exportForm">
                <input type="hidden" id="exportId" value="">
                <div class="form-group"><label>Tên vật tư xuất</label><p id="exportName" style="font-weight:500; margin-top:4px; font-size:16px;">-</p></div>
                <div class="form-group"><label>Tồn khu hiện tại (Khả dụng)</label><p id="exportCurrentStock" style="font-weight:bold; margin-top:4px; color:#1e8449; font-size:16px;">-</p></div>
                
                <div class="form-row">
                    <div class="form-group"><label>Điều động đến Khu Đất (ID) <span style="color:#e74c3c;">*</span></label><input type="number" id="exportKhuDat" placeholder="ID Bảng KhuDat" required></div>
                    <div class="form-group"><label>Nhân viên nhận (ID) <span style="color:#e74c3c;">*</span></label><input type="number" id="exportNhanVien" placeholder="ID Bảng NguoiDung" required></div>
                </div>
                <div class="form-group"><label>Tên công việc (Lý do xuất)</label><input type="text" id="exportCongViec" placeholder="VD: Bón thúc đợt 1 cho lô sầu riêng A"></div>
                <div class="form-group"><label>Số lượng xuất <span style="color:#e74c3c;">*</span></label><input type="number" id="exportQuantity" step="0.01" required></div>
                
                <div class="form-actions">
                    <button type="button" class="btn btn-outline" id="cancelExport">Hủy</button>
                    <button type="submit" class="btn btn-warning"><i class="fas fa-check"></i> Xác nhận xuất</button>
                </div>
            </form>
        </div>
    </div>

    <!-- SCRIPT XỬ LÝ -->
    <script>
        // DỮ LIỆU ĐÚNG CHUẨN CSDL
        let materials = [
            { id: 1, maVatTu: 'VT-NPK01', tenVatTu: 'Phân NPK 16-16-8', idKhuVuc: 1, tenKhuVuc: 'Khu Phân bón', donViTinh: 'Kg', quyCachDongGoi: 0.5, tonKhuHienTai: 120, tonKhuToiDa: 200, mucCanhBaoAnToan: 20 },
            { id: 2, maVatTu: 'VT-ABA02', tenVatTu: 'Thuốc trừ sâu Abamectin', idKhuVuc: 2, tenKhuVuc: 'Khu Thuốc BVTV', donViTinh: 'Lít', quyCachDongGoi: 0.3, tonKhuHienTai: 15, tonKhuToiDa: 80, mucCanhBaoAnToan: 20 },
            { id: 3, maVatTu: 'VT-KALI03', tenVatTu: 'Phân Kali Clorua', idKhuVuc: 1, tenKhuVuc: 'Khu Phân bón', donViTinh: 'Bao', quyCachDongGoi: 0.8, tonKhuHienTai: 5, tonKhuToiDa: 50, mucCanhBaoAnToan: 10 }
        ];
        let nextId = 4;

        function renderTable() {
            const tbody = document.getElementById('materialTableBody');
            tbody.innerHTML = '';
            
            materials.forEach(function(m) {
                const isLow = m.tonKhuHienTai <= m.mucCanhBaoAnToan;
                const isUnder = m.tonKhuHienTai < (m.mucCanhBaoAnToan * 1.5) && !isLow;
                const statusClass = isLow ? 'low' : (isUnder ? 'under' : 'ok');
                const statusText = isLow ? 'Dưới định mức' : (isUnder ? 'Sắp chạm ngưỡng' : 'Đảm bảo');
                const textColor = isLow ? '#e74c3c' : '#333';

                let trHtml = "<tr>";
                trHtml += "<td><strong>" + m.maVatTu + "</strong></td>";
                trHtml += "<td>" + m.tenVatTu + "</td>";
                trHtml += "<td>" + m.tenKhuVuc + "</td>";
                trHtml += "<td>" + m.donViTinh + "</td>";
                trHtml += "<td>" + m.quyCachDongGoi + "</td>";
                trHtml += "<td><strong style='color: " + textColor + "'>" + m.tonKhuHienTai + "</strong></td>";
                trHtml += "<td>" + m.tonKhuToiDa + "</td>";
                trHtml += "<td>" + m.mucCanhBaoAnToan + "</td>";
                trHtml += "<td><span class='badge-status " + statusClass + "'>" + statusText + "</span></td>";
                
                trHtml += "<td><div class='actions-cell'>";
                trHtml += "<button class='btn-action edit edit-btn' title='Sửa' data-id='" + m.id + "'><i class='fas fa-edit'></i></button>";
                trHtml += "<button class='btn-action import import-btn' title='Nhập khu' data-id='" + m.id + "'><i class='fas fa-arrow-down'></i></button>";
                trHtml += "<button class='btn-action export export-btn' title='Xuất khu' data-id='" + m.id + "'><i class='fas fa-arrow-up'></i></button>";
                trHtml += "<button class='btn-action delete delete-btn' title='Xóa' data-id='" + m.id + "'><i class='fas fa-trash'></i></button>";
                trHtml += "</div></td>";
                trHtml += "</tr>";

                const tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });

            document.querySelectorAll('.edit-btn').forEach(function(b) { b.addEventListener('click', function() { openEditModal(parseInt(this.getAttribute('data-id'))); }); });
            document.querySelectorAll('.import-btn').forEach(function(b) { b.addEventListener('click', function() { openImportModal(parseInt(this.getAttribute('data-id'))); }); });
            document.querySelectorAll('.export-btn').forEach(function(b) { b.addEventListener('click', function() { openExportModal(parseInt(this.getAttribute('data-id'))); }); });
            document.querySelectorAll('.delete-btn').forEach(function(b) { b.addEventListener('click', function() { deleteMaterial(parseInt(this.getAttribute('data-id'))); }); });
        }

        function deleteMaterial(id) {
            if(confirm('Bạn có chắc chắn muốn xóa vật tư này khỏi danh mục không?')) {
                materials = materials.filter(function(m) { return m.id !== id; });
                renderTable();
            }
        }

        function filterTable() {
            const keyword = document.getElementById('searchInput').value.toLowerCase();
            const rows = document.querySelectorAll('#materialTableBody tr');
            rows.forEach(function(row) {
                row.style.display = row.textContent.toLowerCase().includes(keyword) ? '' : 'none';
            });
        }

        function openEditModal(id) {
            document.getElementById('materialForm').reset();
            const modal = document.getElementById('materialModal');
            if (id) {
                const m = materials.find(function(item) { return item.id === id; });
                document.getElementById('modalTitle').textContent = 'Cập nhật danh mục vật tư';
                document.getElementById('editId').value = m.id;
                document.getElementById('maVatTu').value = m.maVatTu;
                document.getElementById('tenVatTu').value = m.tenVatTu;
                document.getElementById('idKhuVuc').value = m.idKhuVuc;
                document.getElementById('donViTinh').value = m.donViTinh;
                document.getElementById('quyCachDongGoi').value = m.quyCachDongGoi;
                document.getElementById('tonKhuHienTai').value = m.tonKhuHienTai;
                document.getElementById('tonKhuToiDa').value = m.tonKhuToiDa;
                document.getElementById('mucCanhBaoAnToan').value = m.mucCanhBaoAnToan;
            } else {
                document.getElementById('modalTitle').textContent = 'Khai báo Vật tư mới';
                document.getElementById('editId').value = '';
                document.getElementById('maVatTu').value = 'VT00' + nextId;
                document.getElementById('tonKhuHienTai').value = 0; 
            }
            modal.classList.add('active');
        }

        document.getElementById('btnAddMaterial').addEventListener('click', function() { openEditModal(null); });
        document.getElementById('closeMaterialModal').addEventListener('click', function() { document.getElementById('materialModal').classList.remove('active'); });
        document.getElementById('cancelMaterial').addEventListener('click', function() { document.getElementById('materialModal').classList.remove('active'); });

        document.getElementById('materialForm').addEventListener('submit', function(e) {
            e.preventDefault();
            const editId = document.getElementById('editId').value;
            const idKhuVuc = parseInt(document.getElementById('idKhuVuc').value);
            const data = {
                maVatTu: document.getElementById('maVatTu').value,
                tenVatTu: document.getElementById('tenVatTu').value,
                idKhuVuc: idKhuVuc,
                tenKhuVuc: idKhuVuc === 1 ? 'Khu Phân bón' : 'Khu Thuốc BVTV',
                donViTinh: document.getElementById('donViTinh').value,
                quyCachDongGoi: parseFloat(document.getElementById('quyCachDongGoi').value),
                tonKhuHienTai: parseFloat(document.getElementById('tonKhuHienTai').value),
                tonKhuToiDa: parseFloat(document.getElementById('tonKhuToiDa').value),
                mucCanhBaoAnToan: parseFloat(document.getElementById('mucCanhBaoAnToan').value)
            };
            if(data.tonKhuToiDa <= data.mucCanhBaoAnToan) { alert("Sức chứa tối đa phải lớn hơn ngưỡng cảnh báo!"); return; }

            if (editId) {
                const idx = materials.findIndex(function(m) { return m.id === parseInt(editId); });
                materials[idx] = Object.assign({}, materials[idx], data);
            } else {
                data.id = nextId++;
                materials.push(data);
            }
            document.getElementById('materialModal').classList.remove('active');
            renderTable();
        });

        // ===== HÓA ĐƠN NHẬP (CHI TIẾT VẬT TƯ) =====
        let importRows = [];
        
        function addImportRow(vatTuId, soLuong, donGia) {
            importRows.push({ id: Date.now() + Math.random(), vatTuId: vatTuId, soLuong: soLuong, donGia: donGia });
            renderImportRows();
        }

        function removeImportRow(rowId) {
            importRows = importRows.filter(function(r) { return r.id !== rowId; });
            renderImportRows();
        }

        function renderImportRows() {
            const tbody = document.getElementById('importDetailBody');
            tbody.innerHTML = '';
            
            importRows.forEach(function(row) {
                let opts = "";
                materials.forEach(function(m) {
                    let sel = (m.id === row.vatTuId) ? "selected" : "";
                    opts += "<option value='" + m.id + "' " + sel + ">" + m.maVatTu + " - " + m.tenVatTu + "</option>";
                });

                let trHtml = "<tr>";
                trHtml += "<td><select class='import-vattu-select' data-rowid='" + row.id + "'>" + opts + "</select></td>";
                trHtml += "<td><input type='number' class='import-qty' data-rowid='" + row.id + "' value='" + row.soLuong + "' step='0.01' min='0.01'></td>";
                trHtml += "<td><input type='number' class='import-price' data-rowid='" + row.id + "' value='" + row.donGia + "' step='1000' min='0'></td>";
                trHtml += "<td class='import-amount'>" + (row.soLuong * row.donGia).toLocaleString() + "</td>";
                trHtml += "<td><button type='button' class='btn-remove-row' onclick='removeImportRow(" + row.id + ")'><i class='fas fa-times'></i></button></td>";
                trHtml += "</tr>";

                const tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });
            
            document.querySelectorAll('.import-vattu-select').forEach(function(sel) {
                sel.addEventListener('change', function() {
                    const rowId = parseFloat(this.getAttribute('data-rowid'));
                    const r = importRows.find(function(x) { return x.id === rowId; });
                    if(r) r.vatTuId = parseInt(this.value);
                });
            });
            
            // ĐÃ SỬA: Lỗi nhập 1 số bị khựng là do chỗ này
            document.querySelectorAll('.import-qty').forEach(function(inp) {
                inp.addEventListener('input', function() {
                    const rowId = parseFloat(this.getAttribute('data-rowid'));
                    const r = importRows.find(function(x) { return x.id === rowId; });
                    if(r) {
                        r.soLuong = parseFloat(this.value) || 0;
                        // Chỉ cập nhật hiển thị tiền của dòng hiện tại thay vì vẽ lại toàn bộ bảng
                        this.closest('tr').querySelector('.import-amount').textContent = (r.soLuong * r.donGia).toLocaleString();
                    }
                    calcTotal();
                });
            });
            
            // ĐÃ SỬA: Giống hệt với ô Đơn giá
            document.querySelectorAll('.import-price').forEach(function(inp) {
                inp.addEventListener('input', function() {
                    const rowId = parseFloat(this.getAttribute('data-rowid'));
                    const r = importRows.find(function(x) { return x.id === rowId; });
                    if(r) {
                        r.donGia = parseFloat(this.value) || 0;
                        this.closest('tr').querySelector('.import-amount').textContent = (r.soLuong * r.donGia).toLocaleString();
                    }
                    calcTotal();
                });
            });
            calcTotal();
        }

        function calcTotal() {
            let tongHang = 0;
            importRows.forEach(function(row) { tongHang += (row.soLuong * row.donGia); });
            const thue = parseFloat(document.getElementById('tienThueGTGT').value) || 0;
            
            document.getElementById('tongTienHang').textContent = tongHang.toLocaleString();
            document.getElementById('tongThanhToan').textContent = (tongHang + thue).toLocaleString();
        }

        document.getElementById('addDetailRow').addEventListener('click', function() {
            if (materials.length === 0) { alert('Chưa có vật tư trong danh mục!'); return; }
            addImportRow(materials[0].id, 1, 0);
        });

        function openImportModal(id) {
            importRows = [];
            document.getElementById('formImportHoaDon').reset();
            document.getElementById('hdMaHoaDon').value = 'HD-VT' + Date.now().toString().slice(-5);
            document.getElementById('hdNgayLap').value = new Date().toISOString().slice(0, 16);
            
            if (id) {
                addImportRow(id, 1, 0);
            } else if (materials.length > 0) {
                addImportRow(materials[0].id, 1, 0); 
            }
            document.getElementById('importModal').classList.add('active');
        }

        document.getElementById('btnImportInvoice').addEventListener('click', function() { openImportModal(null); });
        document.getElementById('closeImportModal').addEventListener('click', function() { document.getElementById('importModal').classList.remove('active'); });
        document.getElementById('cancelImport').addEventListener('click', function() { document.getElementById('importModal').classList.remove('active'); });

        document.getElementById('formImportHoaDon').addEventListener('submit', function(e) {
            e.preventDefault();
            if (importRows.length === 0) { alert('Vui lòng thêm ít nhất một dòng vật tư.'); return; }
            
            importRows.forEach(function(row) {
                const vatTu = materials.find(function(m) { return m.id === row.vatTuId; });
                if (vatTu) vatTu.tonKhuHienTai += row.soLuong;
            });
            alert('Lưu Hóa đơn nhập khu thành công!');
            document.getElementById('importModal').classList.remove('active');
            renderTable();
        });

        // ===== MODAL XUẤT KHU =====
        function openExportModal(id) {
            const m = materials.find(function(item) { return item.id === id; });
            document.getElementById('exportId').value = id;
            document.getElementById('exportName').textContent = m.tenVatTu;
            document.getElementById('exportCurrentStock').textContent = m.tonKhuHienTai + ' ' + m.donViTinh;
            document.getElementById('exportForm').reset();
            document.getElementById('exportModal').classList.add('active');
        }
        document.getElementById('closeExportModal').addEventListener('click', function() { document.getElementById('exportModal').classList.remove('active'); });
        document.getElementById('cancelExport').addEventListener('click', function() { document.getElementById('exportModal').classList.remove('active'); });
        
        document.getElementById('exportForm').addEventListener('submit', function(e) {
            e.preventDefault();
            const id = parseInt(document.getElementById('exportId').value);
            const qty = parseFloat(document.getElementById('exportQuantity').value);
            const m = materials.find(function(item) { return item.id === id; });
            if (qty > m.tonKhuHienTai) { alert('Số lượng yêu cầu xuất lớn hơn tồn khu thực tế!'); return; }
            m.tonKhuHienTai -= qty;
            alert('Đã xuất khu và ghi nhận vào Nhật Ký Canh Tác!');
            document.getElementById('exportModal').classList.remove('active');
            renderTable();
        });

        // Responsive Sidebar
        const menuToggle = document.getElementById('menuToggle');
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('overlay');
        function toggleSidebar() { sidebar.classList.toggle('open'); overlay.classList.toggle('active'); }
        if(menuToggle) menuToggle.addEventListener('click', toggleSidebar);
        overlay.addEventListener('click', toggleSidebar);

        renderTable();
    </script>
</body>
</html>