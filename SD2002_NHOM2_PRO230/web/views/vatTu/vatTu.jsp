<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

            /* TOOLBAR */
            .toolbar {
                display: flex;
                flex-wrap: wrap;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 24px;
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
            }
            .toolbar .search-box i {
                color: #8aa3c0;
                margin-right: 10px;
            }
            .toolbar .search-box input {
                border: none;
                padding: 10px 0;
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

            /* GENERAL BUTTONS */
            .btn {
                padding: 10px 20px;
                border: none;
                border-radius: 8px;
                font-weight: 500;
                font-size: 14px;
                cursor: pointer;
                display: inline-flex;
                align-items: center;
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
            .btn-outline {
                background: transparent;
                color: #4a5b6e;
                border: 1px solid #d0d8e3;
            }
            .btn-outline:hover {
                background: #e9edf4;
            }

            /* ACTIONS */
            .actions-cell {
                display: flex;
                gap: 8px;
                justify-content: center;
            }
            .btn-action {
                width: 38px;
                height: 32px;
                border: none;
                border-radius: 8px;
                color: #fff;
                font-size: 14px;
                cursor: pointer;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                transition: 0.2s;
            }
            .btn-action.edit {
                background-color: #4d90fe;
            }
            .btn-action.import {
                background-color: #6fcf97;
            }
            .btn-action.delete {
                background-color: #e74c3c;
            }
            .btn-action:hover {
                opacity: 0.8;
                transform: translateY(-2px);
            }

            /* TABLE */
            .table-container {
                background: #fff;
                border-radius: 16px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.04);
                overflow-x: auto;
                padding: 0 0 8px 0;
            }
            .material-table {
                width: 100%;
                border-collapse: collapse;
                font-size: 14px;
                min-width: 1000px;
            }
            .material-table th {
                text-align: left;
                padding: 16px 16px 12px 16px;
                color: #6f8fb0;
                font-weight: 500;
                border-bottom: 1px solid #e9edf4;
                background: #fafbfc;
                white-space: nowrap;
            }
            .material-table td {
                padding: 14px 16px;
                border-bottom: 1px solid #f0f2f7;
                vertical-align: middle;
            }
            .material-table tr:hover td {
                background: #f8faff;
            }
            .badge-status {
                display: inline-block;
                padding: 4px 12px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 500;
            }
            .badge-status.under {
                background: #fdebd0;
                color: #a04000;
            }
            .badge-status.ok {
                background: #d5f5e3;
                color: #1e8449;
            }
            .badge-status.low {
                background: #fadbd8;
                color: #922b21;
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
                backdrop-filter: blur(2px);
            }
            .modal-overlay.active {
                display: flex;
            }
            .modal {
                background: #fff;
                border-radius: 20px;
                width: 95%;
                max-width: 1000px;
                max-height: 90vh;
                overflow-y: auto;
                padding: 32px;
                box-shadow: 0 20px 60px rgba(0,0,0,0.2);
                animation: fadeInUp 0.25s ease;
            }
            @keyframes fadeInUp {
                from {
                    opacity: 0;
                    transform: translateY(30px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }
            .modal-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 24px;
            }
            .modal-header h2 {
                font-size: 22px;
                font-weight: 500;
                color: #1e2a3a;
            }
            .modal-header .close-modal {
                background: none;
                border: none;
                font-size: 28px;
                cursor: pointer;
                color: #8aa3c0;
                transition: color 0.2s;
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
                margin-bottom: 6px;
                color: #2c3e50;
                font-size: 14px;
            }
            .form-group input, .form-group select, .form-group textarea {
                width: 100%;
                padding: 10px 14px;
                border: 1px solid #d0d8e3;
                border-radius: 8px;
                font-size: 14px;
                transition: border 0.2s;
                background: #fafbfc;
            }
            .form-group input[type="file"] {
                padding: 7px 10px;
                background: #fff;
                line-height: 1.5;
                cursor: pointer;
            }
            .form-group input:focus, .form-group select:focus {
                border-color: #4d90fe;
                outline: none;
                background: #fff;
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
                    if (pageTitle)
                        pageTitle.innerHTML = 'Quản lý Vật tư <span>| Danh mục CSDL</span>';
                });
            </script>

            <section class="content">
                <div class="toolbar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" placeholder="Tìm kiếm tên vật tư, hoạt chất..." id="searchInput" oninput="filterTable()">
                    </div>
                    <div class="actions">
                        <button class="btn btn-primary" id="btnAddMaterial"><i class="fas fa-plus"></i> Khai báo vật tư mới</button>
                        <button class="btn btn-success" id="btnImportInvoice"><i class="fas fa-file-invoice"></i> Lập phiếu nhập kho</button>
                    </div>
                </div>

                <div class="table-container">
                    <table class="material-table" id="materialTable">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên vật tư</th>
                                <th>Đơn vị</th>
                                <th>Quy cách</th>
                                <th>DT Chiếm dụng (m²)</th>
                                <th>Tồn kho HT</th>
                                <th>Cảnh báo (Min)</th>
                                <th>Sức chứa (Max)</th>
                                <th>Tình trạng</th>
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

        <!-- ===== MODAL KHAI BÁO VẬT TƯ CHUẨN DB VAT_TU ===== -->
        <div class="modal-overlay" id="materialModal">
            <div class="modal" style="max-width: 800px;">
                <div class="modal-header">
                    <h2 id="modalTitle">Khai báo Vật tư</h2>
                    <button class="close-modal" id="closeMaterialModal">&times;</button>
                </div>
                <form id="materialForm">
                    <input type="hidden" id="editId" value="">

                    <div class="form-row">
                        <div class="form-group" style="flex:2;">
                            <label>Tên vật tư <span style="color:#e74c3c;">*</span></label>
                            <input type="text" id="tenVatTu" placeholder="VD: Phân NPK 16-16-8" required>
                        </div>
                        <div class="form-group" style="flex:1;">
                            <label>Loại vật tư (ID) <span style="color:#e74c3c;">*</span></label>
                            <select id="loaiVatTuId" required>
                                <option value="1">Phân bón</option>
                                <option value="2">Thuốc Bảo vệ thực vật</option>
                                <option value="3">Vật tư khác</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Hoạt chất (Tùy chọn)</label>
                            <input type="text" id="hoatChat" placeholder="VD: Đạm, Lân, Abamectin">
                        </div>
                        <div class="form-group">
                            <label>Đối tượng phòng trừ (Tùy chọn)</label>
                            <input type="text" id="doiTuongPhongTru" placeholder="VD: Sâu đục thân, rệp sáp">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Đơn vị tính <span style="color:#e74c3c;">*</span></label>
                            <select id="donViTinh" required onchange="toggleDonViTinhKhac()">
                                <option value="Kg">Kg</option>
                                <option value="Lít">Lít</option>
                                <option value="Gói">Gói</option>
                                <option value="Bao">Bao</option>
                                <option value="Chai">Chai</option>
                                <option value="Khác">Khác (Nhập tay...)</option>
                            </select>
                            <input type="text" id="donViTinhKhac" placeholder="Nhập đơn vị tính..." style="display: none; margin-top: 8px;">
                        </div>
                        <div class="form-group">
                            <label>Quy cách đóng gói <span style="color:#e74c3c;">*</span></label>
                            <input type="text" id="quyCachDongGoi" placeholder="VD: Bao 50kg, Chai 1L" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Diện tích chiếm dụng (m²) <span style="color:#e74c3c;">*</span></label>
                            <input type="number" id="dienTichChiemDung" step="0.01" required>
                        </div>
                        <div class="form-group">
                            <label>Thời gian cách ly (Ngày)</label>
                            <input type="number" id="thoiGianCachLy" min="0" placeholder="VD: 7">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Vị trí lưu trữ mặc định (Kho)</label>
                            <select id="viTriLuuTruMacDinhId">
                                <option value="0">-- Chưa phân bổ kho --</option>
                                <c:forEach items="${listDonVi}" var="dv">
                                    <option value="${dv.getId()}">${dv.getTen_don_vi()}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Tồn kho hiện tại</label>
                            <input type="number" id="tonKhoHienTai" step="0.01" value="0" readonly style="background:#f0f2f7; cursor:not-allowed;">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Tồn kho tối thiểu (Min) <span style="color:#e74c3c;">*</span></label>
                            <input type="number" id="tonKhoToiThieu" step="0.01" value="10" required>
                        </div>
                        <div class="form-group">
                            <label>Tồn kho tối đa (Max) <span style="color:#e74c3c;">*</span></label>
                            <input type="number" id="tonKhoToiDa" step="0.01" value="100" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Trạng thái kinh doanh <span style="color:#e74c3c;">*</span></label>
                            <select id="trangThai" required>
                                <option value="Đang sử dụng">Đang sử dụng</option>
                                <option value="Ngừng sử dụng">Ngừng sử dụng</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="button" class="btn btn-outline" id="cancelMaterial">Hủy</button>
                        <button type="submit" class="btn btn-primary">Lưu thông tin</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- ===== MODAL HÓA ĐƠN NHẬP KHO ===== -->
        <div class="modal-overlay" id="importModal">
            <div class="modal" style="max-width: 1000px;">
                <div class="modal-header">
                    <h2><i class="fas fa-file-invoice"></i> Lập Phiếu Nhập Kho</h2>
                    <button class="close-modal" id="closeImportModal">&times;</button>
                </div>
                <form id="formImportHoaDon" enctype="multipart/form-data">
                    <div style="background: #f8faff; padding: 16px; border-radius: 12px; margin-bottom: 20px; border: 1px solid #e9edf4;">
                        <h4 style="margin-bottom: 12px; color: #1e2a3a;"><i class="fas fa-receipt"></i> Thông tin chứng từ</h4>

                        <!-- Dòng 1: Mã phiếu, Loại phiếu (mặc định), Số hóa đơn -->
                        <div class="form-row">
                            <div class="form-group"><label>Mã phiếu nhập <span style="color:#e74c3c;">*</span></label><input type="text" id="hdMaHoaDon" required></div>
                            <div class="form-group"><label>Loại phiếu nhập</label><input type="text" id="hdLoaiPhieu" value="Nhập Vật Tư" readonly style="background:#e9edf4; cursor:not-allowed;"></div>
                            <div class="form-group"><label>Số hóa đơn <span style="color:#e74c3c;">*</span></label><input type="text" id="hdSoHoaDon" required></div>
                        </div>

                        <!-- Dòng 2: Mẫu số, Ký hiệu, Ngày hóa đơn -->
                        <div class="form-row">
                            <div class="form-group"><label>Mẫu số</label><input type="text" id="hdMauSo"></div>
                            <div class="form-group"><label>Ký hiệu</label><input type="text" id="hdKyHieu"></div>
                            <div class="form-group"><label>Ngày hóa đơn <span style="color:#e74c3c;">*</span></label><input type="date" id="hdNgayHoaDon" required></div>
                        </div>

                        <!-- Dòng 3: Nhà cung cấp ID, MST -->
                        <div class="form-row">
                            <div class="form-group">
                                <label>Nhà cung cấp <span style="color:#e74c3c;">*</span></label>
                                <select id="hdNhaCungCapId" required onchange="tuDongDienMST()">
                                    <option value="">-- Chọn Nhà cung cấp --</option>
                                    <c:forEach items="${listNCC}" var="ncc">
                                        <option value="${ncc.getId()}" data-mst="${ncc.getMa_so_thue()}">${ncc.getTen_ncc()}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group"><label>Mã số thuế NCC</label><input type="text" id="hdMST"></div>
                        </div>

                        <!-- Dòng 4: Người mua, Người bán -->
                        <div class="form-row">
                            <div class="form-group"><label>Người mua hàng <span style="color:#e74c3c;">*</span></label><input type="text" id="hdNguoiMua" required></div>
                            <div class="form-group"><label>Người bán hàng</label><input type="text" id="hdNguoiBan"></div>
                        </div>

                        <!-- Dòng 5: File Ảnh và Ghi chú (ĐÃ BỎ 2 CHỮ KÝ) -->
                        <div class="form-row">
                            <div class="form-group" style="flex:2;">
                                <label>Upload Bản gốc hóa đơn (Ảnh/PDF) <span style="color:#e74c3c;">*</span></label>
                                <input type="file" id="hdAnhHoaDon" accept=".jpg, .jpeg, .png, .pdf" required>
                            </div>
                            <div class="form-group" style="flex:1;"><label>Ghi chú</label><input type="text" id="hdGhiChu"></div>
                        </div>
                    </div>

                    <h4 style="margin-bottom: 12px;"><i class="fas fa-list-ul"></i> Chi tiết Vật tư nhập</h4>
                    <div style="overflow-x: auto;">
                        <table class="detail-table" id="importDetailTable">
                            <thead>
                                <tr>
                                    <th style="width:20%;">Tên Vật tư</th>
                                    <th style="width:15%;">Ngày SX</th>
                                    <th style="width:15%;">Hạn SD</th> 
                                    <th style="width:10%;">Số lượng</th>
                                    <th style="width:15%;">Đơn giá (VNĐ)</th>
                                    <th style="width:15%;">Thành tiền</th>
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
                        <button type="submit" class="btn btn-success"><i class="fas fa-check"></i> Xác nhận nhập kho</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- SCRIPT XỬ LÝ -->
        <script>
            // LẤY DỮ LIỆU TỪ DATABASE QUA SERVLET BẰNG JSTL VÀ GETTER
            let materials = [
            <c:forEach items="${listVatTu}" var="vt" varStatus="loop">
            {
            id: ${vt.getId()},
                    tenVatTu: '${vt.getTen_vat_tu()}',
                    loaiVatTuId: ${vt.getLoai_vat_tu_id()},
                    hoatChat: '${vt.getHoat_chat()}',
                    doiTuongPhongTru: '${vt.getDoi_tuong_phong_tru()}',
                    thoiGianCachLy: ${vt.getThoi_gian_cach_ly()},
                    donViTinh: '${vt.getDon_vi_tinh()}',
                    quyCachDongGoi: '${vt.getQuy_cach_dong_goi()}',
                    dienTichChiemDung: ${vt.getDien_tich_chiem_dung()},
                    viTriLuuTruMacDinhId: ${vt.getVi_tri_luu_tru_mac_dinh_id()},
                    tonKhoHienTai: ${vt.getTon_kho_hien_tai()},
                    tonKhoToiThieu: ${vt.getTon_kho_toi_thieu()},
                    tonKhoToiDa: ${vt.getTon_kho_toi_da()},
                    trangThai: '${vt.getTrang_thai()}'
            }${!loop.last ? ',' : ''}
            </c:forEach>
            ];

            function renderTable() {
                const tbody = document.getElementById('materialTableBody');
                tbody.innerHTML = '';

                materials.forEach(function (m) {
                    const isLow = m.tonKhoHienTai <= m.tonKhoToiThieu;
                    const isUnder = m.tonKhoHienTai < (m.tonKhoToiThieu * 1.5) && !isLow;
                    const statusClass = isLow ? 'low' : (isUnder ? 'under' : 'ok');
                    const statusText = isLow ? 'Dưới định mức' : (isUnder ? 'Sắp chạm ngưỡng' : 'Đảm bảo');
                    const textColor = isLow ? '#e74c3c' : '#333';

                    let trHtml = "<tr>";
                    trHtml += "<td><strong>VT00" + m.id + "</strong></td>";
                    trHtml += "<td>" + m.tenVatTu + "</td>";
                    trHtml += "<td>" + m.donViTinh + "</td>";
                    trHtml += "<td>" + m.quyCachDongGoi + "</td>";
                    trHtml += "<td>" + m.dienTichChiemDung + "</td>";
                    trHtml += "<td><strong style='color: " + textColor + "'>" + m.tonKhoHienTai + "</strong></td>";
                    trHtml += "<td>" + m.tonKhoToiThieu + "</td>";
                    trHtml += "<td>" + m.tonKhoToiDa + "</td>";
                    trHtml += "<td><span class='badge-status " + statusClass + "'>" + statusText + "</span></td>";

                    trHtml += "<td><div class='actions-cell'>";
                    trHtml += "<button class='btn-action edit edit-btn' title='Sửa' data-id='" + m.id + "'><i class='fas fa-edit'></i></button>";
                    trHtml += "<button class='btn-action import import-btn' title='Nhập kho' data-id='" + m.id + "'><i class='fas fa-arrow-down'></i></button>";
                    trHtml += "<button class='btn-action delete delete-btn' title='Xóa' data-id='" + m.id + "'><i class='fas fa-trash'></i></button>";
                    trHtml += "</div></td>";
                    trHtml += "</tr>";

                    const tr = document.createElement('tr');
                    tr.innerHTML = trHtml;
                    tbody.appendChild(tr);
                });

                document.querySelectorAll('.edit-btn').forEach(function (b) {
                    b.addEventListener('click', function () {
                        openEditModal(parseInt(this.getAttribute('data-id')));
                    });
                });
                document.querySelectorAll('.import-btn').forEach(function (b) {
                    b.addEventListener('click', function () {
                        openImportModal(parseInt(this.getAttribute('data-id')));
                    });
                });
                document.querySelectorAll('.delete-btn').forEach(function (b) {
                    b.addEventListener('click', function () {
                        deleteMaterial(parseInt(this.getAttribute('data-id')));
                    });
                });
            }

            function toggleDonViTinhKhac() {
                const select = document.getElementById('donViTinh');
                const input = document.getElementById('donViTinhKhac');
                if (select.value === 'Khác') {
                    input.style.display = 'block';
                    input.required = true;
                } else {
                    input.style.display = 'none';
                    input.required = false;
                    input.value = '';
                }
            }

            // --- THÊM & SỬA VẬT TƯ (KHÔNG DÙNG JSON) ---
            document.getElementById('materialForm').addEventListener('submit', function (e) {
                e.preventDefault();
                const editId = document.getElementById('editId').value;

                const tonKhoToiThieu = parseFloat(document.getElementById('tonKhoToiThieu').value);
                const tonKhoToiDa = parseFloat(document.getElementById('tonKhoToiDa').value);

                if (tonKhoToiDa <= tonKhoToiThieu) {
                    alert("Sức chứa tối đa (Max) phải lớn hơn cảnh báo tối thiểu (Min)!");
                    return;
                }

                let dvtFinal = document.getElementById('donViTinh').value;
                if (dvtFinal === 'Khác') {
                    dvtFinal = document.getElementById('donViTinhKhac').value;
                    if (!dvtFinal.trim()) {
                        alert("Vui lòng nhập đơn vị tính!");
                        return;
                    }
                }

                const params = new URLSearchParams();
                if (editId)
                    params.append('id', editId);
                params.append('ten_vat_tu', document.getElementById('tenVatTu').value);
                params.append('loai_vat_tu_id', document.getElementById('loaiVatTuId').value);
                params.append('hoat_chat', document.getElementById('hoatChat').value);
                params.append('doi_tuong_phong_tru', document.getElementById('doiTuongPhongTru').value);
                params.append('thoi_gian_cach_ly', document.getElementById('thoiGianCachLy').value || 0);
                params.append('don_vi_tinh', dvtFinal);
                params.append('quy_cach_dong_goi', document.getElementById('quyCachDongGoi').value);
                params.append('dien_tich_chiem_dung', document.getElementById('dienTichChiemDung').value);
                params.append('vi_tri_luu_tru_mac_dinh_id', document.getElementById('viTriLuuTruMacDinhId').value || 0);
                params.append('ton_kho_hien_tai', document.getElementById('tonKhoHienTai').value);
                params.append('ton_kho_toi_thieu', tonKhoToiThieu);
                params.append('ton_kho_toi_da', tonKhoToiDa);
                params.append('trang_thai', document.getElementById('trangThai').value);

                const actionUrl = editId ? 'vattu?action=update' : 'vattu?action=insert';

                fetch(actionUrl, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    body: params.toString()
                })
                        .then(res => res.json())
                        .then(data => {
                            if (data.success) {
                                alert(editId ? 'Cập nhật thành công!' : 'Thêm mới thành công!');
                                location.reload();
                            } else
                                alert('Lỗi xử lý lưu dữ liệu!');
                        })
                        .catch(error => console.error("Lỗi rùi:", error));
            });

            // --- XÓA VẬT TƯ (GỌI API) ---
            function deleteMaterial(id) {
                if (confirm('Bạn có chắc chắn muốn xóa vật tư này khỏi CSDL?')) {
                    fetch('vattu?action=delete&id=' + id, {method: 'POST'})
                            .then(res => res.json())
                            .then(data => {
                                if (data.success) {
                                    alert('Xóa thành công!');
                                    location.reload();
                                } else
                                    alert('Lỗi xóa dữ liệu!');
                            });
                }
            }

            function filterTable() {
                const keyword = document.getElementById('searchInput').value.toLowerCase();
                const rows = document.querySelectorAll('#materialTableBody tr');
                rows.forEach(function (row) {
                    row.style.display = row.textContent.toLowerCase().includes(keyword) ? '' : 'none';
                });
            }

            function openEditModal(id) {
                document.getElementById('materialForm').reset();
                const modal = document.getElementById('materialModal');

                document.getElementById('donViTinhKhac').style.display = 'none';
                document.getElementById('donViTinhKhac').required = false;

                if (id) {
                    const m = materials.find(function (item) {
                        return item.id === id;
                    });
                    document.getElementById('modalTitle').textContent = 'Cập nhật Vật tư';
                    document.getElementById('editId').value = m.id;
                    document.getElementById('tenVatTu').value = m.tenVatTu;
                    document.getElementById('loaiVatTuId').value = m.loaiVatTuId;
                    document.getElementById('hoatChat').value = m.hoatChat || '';
                    document.getElementById('doiTuongPhongTru').value = m.doiTuongPhongTru || '';
                    document.getElementById('thoiGianCachLy').value = m.thoiGianCachLy || '';

                    const standardUnits = ['Kg', 'Lít', 'Gói', 'Bao', 'Chai'];
                    if (standardUnits.includes(m.donViTinh)) {
                        document.getElementById('donViTinh').value = m.donViTinh;
                    } else {
                        document.getElementById('donViTinh').value = 'Khác';
                        document.getElementById('donViTinhKhac').style.display = 'block';
                        document.getElementById('donViTinhKhac').required = true;
                        document.getElementById('donViTinhKhac').value = m.donViTinh;
                    }

                    document.getElementById('quyCachDongGoi').value = m.quyCachDongGoi;
                    document.getElementById('dienTichChiemDung').value = m.dienTichChiemDung;
                    document.getElementById('viTriLuuTruMacDinhId').value = m.viTriLuuTruMacDinhId || '';
                    document.getElementById('tonKhoHienTai').value = m.tonKhoHienTai;
                    document.getElementById('tonKhoToiThieu').value = m.tonKhoToiThieu;
                    document.getElementById('tonKhoToiDa').value = m.tonKhoToiDa;
                    document.getElementById('trangThai').value = m.trangThai;
                } else {
                    document.getElementById('modalTitle').textContent = 'Khai báo Vật tư mới';
                    document.getElementById('editId').value = '';
                    document.getElementById('donViTinh').value = 'Kg';
                    document.getElementById('viTriLuuTruMacDinhId').value = '0';
                    document.getElementById('tonKhoHienTai').value = 0;
                    document.getElementById('trangThai').value = 'Đang sử dụng';
                }
                modal.classList.add('active');
            }

            document.getElementById('btnAddMaterial').addEventListener('click', function () {
                openEditModal(null);
            });
            document.getElementById('closeMaterialModal').addEventListener('click', function () {
                document.getElementById('materialModal').classList.remove('active');
            });
            document.getElementById('cancelMaterial').addEventListener('click', function () {
                document.getElementById('materialModal').classList.remove('active');
            });

            // ===== HÓA ĐƠN NHẬP KHO =====
            let importRows = [];

            function addImportRow(vatTuId, ngaySanXuat, hanSuDung, soLuong, donGia) {
                importRows.push({id: Date.now() + Math.random(), vatTuId: vatTuId, ngaySanXuat: ngaySanXuat, hanSuDung: hanSuDung, soLuong: soLuong, donGia: donGia});
                renderImportRows();
            }

            function removeImportRow(rowId) {
                importRows = importRows.filter(function (r) {
                    return r.id !== rowId;
                });
                renderImportRows();
            }

            function renderImportRows() {
                const tbody = document.getElementById('importDetailBody');
                tbody.innerHTML = '';

                importRows.forEach(function (row) {
                    let opts = "";
                    materials.forEach(function (m) {
                        let sel = (m.id === row.vatTuId) ? "selected" : "";
                        opts += "<option value='" + m.id + "' " + sel + ">" + m.tenVatTu + "</option>";
                    });

                    let trHtml = "<tr>";
                    trHtml += "<td><select class='import-vattu-select' data-rowid='" + row.id + "'>" + opts + "</select></td>";
                    // Input Ngày SX và Hạn SD
                    trHtml += "<td><input type='date' class='import-mfg' data-rowid='" + row.id + "' value='" + row.ngaySanXuat + "' style='width: 100%; border: 1px solid #d0d8e3; border-radius: 6px; padding: 6px 8px; font-size: 13px; background: #fff;'></td>";
                    trHtml += "<td><input type='date' class='import-exp' data-rowid='" + row.id + "' value='" + row.hanSuDung + "' style='width: 100%; border: 1px solid #d0d8e3; border-radius: 6px; padding: 6px 8px; font-size: 13px; background: #fff;'></td>";

                    trHtml += "<td><input type='number' class='import-qty' data-rowid='" + row.id + "' value='" + row.soLuong + "' step='0.01' min='0.01'></td>";
                    trHtml += "<td><input type='number' class='import-price' data-rowid='" + row.id + "' value='" + row.donGia + "' step='1000' min='0'></td>";
                    trHtml += "<td class='import-amount'>" + (row.soLuong * row.donGia).toLocaleString() + "</td>";
                    trHtml += "<td><button type='button' class='btn-remove-row' onclick='removeImportRow(" + row.id + ")'><i class='fas fa-times'></i></button></td>";
                    trHtml += "</tr>";

                    const tr = document.createElement('tr');
                    tr.innerHTML = trHtml;
                    tbody.appendChild(tr);
                });

                document.querySelectorAll('.import-vattu-select').forEach(function (sel) {
                    sel.addEventListener('change', function () {
                        const rowId = parseFloat(this.getAttribute('data-rowid'));
                        const r = importRows.find(function (x) {
                            return x.id === rowId;
                        });
                        if (r)
                            r.vatTuId = parseInt(this.value);
                    });
                });

                document.querySelectorAll('.import-mfg').forEach(function (inp) {
                    inp.addEventListener('change', function () {
                        const rowId = parseFloat(this.getAttribute('data-rowid'));
                        const r = importRows.find(function (x) {
                            return x.id === rowId;
                        });
                        if (r)
                            r.ngaySanXuat = this.value;
                    });
                });

                document.querySelectorAll('.import-exp').forEach(function (inp) {
                    inp.addEventListener('change', function () {
                        const rowId = parseFloat(this.getAttribute('data-rowid'));
                        const r = importRows.find(function (x) {
                            return x.id === rowId;
                        });
                        if (r)
                            r.hanSuDung = this.value;
                    });
                });

                document.querySelectorAll('.import-qty').forEach(function (inp) {
                    inp.addEventListener('input', function () {
                        const rowId = parseFloat(this.getAttribute('data-rowid'));
                        const r = importRows.find(function (x) {
                            return x.id === rowId;
                        });
                        if (r) {
                            r.soLuong = parseFloat(this.value) || 0;
                            this.closest('tr').querySelector('.import-amount').textContent = (r.soLuong * r.donGia).toLocaleString();
                        }
                        calcTotal();
                    });
                });

                document.querySelectorAll('.import-price').forEach(function (inp) {
                    inp.addEventListener('input', function () {
                        const rowId = parseFloat(this.getAttribute('data-rowid'));
                        const r = importRows.find(function (x) {
                            return x.id === rowId;
                        });
                        if (r) {
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
                importRows.forEach(function (row) {
                    tongHang += (row.soLuong * row.donGia);
                });
                const thue = parseFloat(document.getElementById('tienThueGTGT').value) || 0;

                document.getElementById('tongTienHang').textContent = tongHang.toLocaleString();
                document.getElementById('tongThanhToan').textContent = (tongHang + thue).toLocaleString();
            }

            document.getElementById('addDetailRow').addEventListener('click', function () {
                if (materials.length === 0) {
                    alert('Chưa có vật tư trong danh mục!');
                    return;
                }
                addImportRow(materials[0].id, '', '', 1, 0);
            });

            function openImportModal(id) {
                importRows = [];
                document.getElementById('formImportHoaDon').reset();
                document.getElementById('hdMaHoaDon').value = 'HD-VT' + Date.now().toString().slice(-5);
                document.getElementById('hdNgayHoaDon').value = new Date().toISOString().slice(0, 10);

                if (id) {
                    addImportRow(id, '', '', 1, 0);
                } else if (materials.length > 0) {
                    addImportRow(materials[0].id, '', '', 1, 0);
                }
                document.getElementById('importModal').classList.add('active');
            }

            document.getElementById('btnImportInvoice').addEventListener('click', function () {
                openImportModal(null);
            });
            document.getElementById('closeImportModal').addEventListener('click', function () {
                document.getElementById('importModal').classList.remove('active');
            });
            document.getElementById('cancelImport').addEventListener('click', function () {
                document.getElementById('importModal').classList.remove('active');
            });

            document.getElementById('formImportHoaDon').addEventListener('submit', function (e) {
                e.preventDefault();
                if (importRows.length === 0) {
                    alert('Vui lòng thêm ít nhất một dòng vật tư.');
                    return;
                }

                for (let i = 0; i < importRows.length; i++) {
                    if (!importRows[i].ngaySanXuat || !importRows[i].hanSuDung) {
                        alert("Vui lòng chọn đầy đủ Ngày sản xuất và Hạn sử dụng cho tất cả các vật tư!");
                        return;
                    }
                    if (new Date(importRows[i].ngaySanXuat) > new Date(importRows[i].hanSuDung)) {
                        alert("Ngày sản xuất không được lớn hơn Hạn sử dụng!");
                        return;
                    }
                }

                // Sử dụng FormData để hỗ trợ tự động gửi File lên server
                let formData = new FormData();
                formData.append("action", "insertPhieuNhap");

                // Các trường khớp với bảng database PhieuNhap
                formData.append("ma_phieu_nhap", document.getElementById('hdMaHoaDon').value);
                formData.append("loai_phieu_nhap", document.getElementById('hdLoaiPhieu').value); // Sẽ mặc định là "Nhập Vật Tư"
                formData.append("so_hoa_don", document.getElementById('hdSoHoaDon').value);
                formData.append("mau_so", document.getElementById('hdMauSo').value);
                formData.append("ky_hieu", document.getElementById('hdKyHieu').value);
                formData.append("ngay_hoa_don", document.getElementById('hdNgayHoaDon').value);
                formData.append("nha_cung_cap_id", document.getElementById('hdNhaCungCapId').value);
                formData.append("ma_so_thue_ncc", document.getElementById('hdMST').value);
                formData.append("nguoi_mua_hang", document.getElementById('hdNguoiMua').value);
                formData.append("nguoi_ban_hang", document.getElementById('hdNguoiBan').value);
                formData.append("ghi_chu", document.getElementById('hdGhiChu').value);

                // Đính kèm File Ảnh
                let fileAnh = document.getElementById('hdAnhHoaDon').files[0];
                if (fileAnh) {
                    formData.append("anh_hoa_don", fileAnh);
                }

                // Tiền tệ
                formData.append("tong_tien_hang", document.getElementById('tongTienHang').innerText.replace(/,/g, ''));
                formData.append("tien_thue_gtgt", document.getElementById('tienThueGTGT').value);
                formData.append("tong_thanh_toan", document.getElementById('tongThanhToan').innerText.replace(/,/g, ''));

                importRows.forEach(row => {
                    formData.append("vat_tu_id[]", row.vatTuId);
                    formData.append("so_luong[]", row.soLuong);
                    formData.append("don_gia[]", row.donGia);
                    formData.append("ngay_san_xuat[]", row.ngaySanXuat);
                    formData.append("han_su_dung[]", row.hanSuDung);
                });

                // FETCH API gửi multipart/form-data lên PhieuNhapServlet
                fetch('phieunhap', {
                    method: 'POST',
                    body: formData
                })
                        // Đọc dạng text trước rồi mới parse, để nếu server trả về trang lỗi HTML
                        // thì vẫn hiện được nội dung thật thay vì báo lỗi chung chung.
                        .then(function (res) {
                            return res.text().then(function (text) {
                                return {status: res.status, text: text};
                            });
                        })
                        .then(function (res) {
                            let data;
                            try {
                                data = JSON.parse(res.text);
                            } catch (e) {
                                console.error('Phản hồi không phải JSON (HTTP ' + res.status + '):', res.text);
                                alert('Máy chủ trả về lỗi (HTTP ' + res.status + '). Xem chi tiết trong Console trình duyệt hoặc log Tomcat.');
                                return;
                            }

                            if (data.success) {
                                alert('Lập phiếu nhập thành công! Ảnh hóa đơn đã được tạo Link lưu vào Database.');
                                document.getElementById('importModal').classList.remove('active');
                                location.reload();
                            } else {
                                alert('Lỗi khi nhập kho: ' + (data.error || 'Không rõ nguyên nhân, xem log Tomcat.'));
                            }
                        })
                        .catch(function (err) {
                            console.error("Lỗi:", err);
                            alert('Không gửi được dữ liệu lên máy chủ: ' + err.message);
                        });
            });

            // Responsive Sidebar
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

            renderTable();
            // Hàm tự động điền Mã số thuế khi chọn Nhà cung cấp
            function tuDongDienMST() {
                var selectNCC = document.getElementById("hdNhaCungCapId");
                var selectedOption = selectNCC.options[selectNCC.selectedIndex];
                var mst = selectedOption.getAttribute("data-mst");

                var inputMST = document.getElementById("hdMST");
                if (inputMST) {
                    inputMST.value = (mst && mst !== 'null') ? mst : "";
                }
            }
        </script>
    </body>
</html>