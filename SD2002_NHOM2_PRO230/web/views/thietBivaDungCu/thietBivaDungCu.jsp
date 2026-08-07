<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
        
        /* NÚT THAO TÁC (GIỐNG ẢNH MẪU) */
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
                        <button class="btn btn-success btn-nhap-chung" data-type="2"><i class="fas fa-file-invoice"></i> Lập phiếu nhập khu</button>
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
                        <button class="btn btn-success btn-nhap-chung" data-type="3"><i class="fas fa-file-invoice"></i> Lập phiếu nhập khu</button>
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

    <!-- MODAL KHAI BÁO THIẾT BỊ (Bảng ThietBi) -->
    <div class="modal-overlay" id="modalThietBi">
        <div class="modal">
            <div class="modal-header">
                <h2 id="titleThietBi">Khai báo thiết bị mới</h2>
                <button class="close-modal" onclick="document.getElementById('modalThietBi').classList.remove('active')">&times;</button>
            </div>
            <form id="formThietBi">
                <div class="form-group"><label>Mã thiết bị <span style="color:#e74c3c;">*</span></label><input type="text" id="maThietBi" placeholder="VD: TB001" required></div>
                <div class="form-group"><label>Tên thiết bị <span style="color:#e74c3c;">*</span></label><input type="text" id="tenThietBi" placeholder="VD: Máy kéo Kubota" required></div>
                <div class="form-group">
                    <label>Khu Vực Bãi đỗ quy định</label>
                    <select id="idKhuVucThietBi">
                        <option value="4">Bãi Thiết bị (Đỗ máy móc lớn)</option>
                    </select>
                </div>
                <div class="form-group"><label>Diện tích chiếm dụng bãi đỗ (m²)</label><input type="number" id="dienTichThietBi" step="0.01" value="2.5"></div>
                <div class="form-group">
                    <label>Trạng thái khởi tạo</label>
                    <select id="trangThaiThietBi">
                        <option value="1">1: Rảnh</option>
                        <option value="3">3: Bảo trì</option>
                    </select>
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="document.getElementById('modalThietBi').classList.remove('active')">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu hồ sơ</button>
                </div>
            </form>
        </div>
    </div>

    <!-- MODAL KHAI BÁO DỤNG CỤ (Bảng DungCu) -->
    <div class="modal-overlay" id="modalDungCu">
        <div class="modal">
            <div class="modal-header">
                <h2>Khai báo Dụng cụ cầm tay mới</h2>
                <button class="close-modal" onclick="document.getElementById('modalDungCu').classList.remove('active')">&times;</button>
            </div>
            <form id="formDungCu">
                <div class="form-group"><label>Tên dụng cụ <span style="color:#e74c3c;">*</span></label><input type="text" id="tenDungCu" required></div>
                <div class="form-group">
                    <label>Khu Vực Lưu trữ</label>
                    <select id="idKhuVucDungCu">
                        <option value="3">Khu Dụng cụ (Cất đồ nhỏ)</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Đơn vị tính</label>
                    <select id="donViDungCu">
                        <option value="Cái">Cái</option>
                        <option value="Bộ">Bộ</option>
                    </select>
                </div>
                <div class="form-group"><label>Tồn khu tối thiểu (Ngưỡng cảnh báo)</label><input type="number" id="tonToiThieuDungCu" step="0.01" value="5"></div>
                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="document.getElementById('modalDungCu').classList.remove('active')">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu danh mục</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ==================== CÁC MODAL THAO TÁC NGHIỆP VỤ ==================== -->

    <!-- 1. MODAL CẤP PHÁT DỤNG CỤ (MỚI THÊM) -->
    <div class="modal-overlay" id="modalExportDungCu">
        <div class="modal">
            <div class="modal-header">
                <h2><i class="fas fa-arrow-up"></i> Cấp Phát Dụng Cụ</h2>
                <button class="close-modal" onclick="document.getElementById('modalExportDungCu').classList.remove('active')">&times;</button>
            </div>
            <form id="formExportDungCu">
                <input type="hidden" id="exportDcId" value="">
                <div class="form-group">
                    <label>Tên dụng cụ</label>
                    <p id="exportDcName" style="font-weight:500; margin-top:4px; font-size:16px; color:#4d90fe;"></p>
                </div>
                <div class="form-group">
                    <label>Tồn khu hiện tại</label>
                    <p id="exportDcStock" style="font-weight:bold; margin-top:4px; color:#1e8449; font-size:16px;"></p>
                </div>
                
                <div class="form-row">
                    <div class="form-group"><label>Điều động đến Khu Đất (ID) <span style="color:#e74c3c;">*</span></label><input type="number" id="exportDcKhuDat" required placeholder="Nhập ID Khu Đất"></div>
                    <div class="form-group"><label>Nhân viên nhận (ID) <span style="color:#e74c3c;">*</span></label><input type="number" id="exportDcNhanVien" required placeholder="Nhập ID Nhân Viên"></div>
                </div>
                
                <div class="form-row">
                    <div class="form-group"><label>Số lượng cấp phát <span style="color:#e74c3c;">*</span></label><input type="number" id="exportDcQty" step="0.01" required></div>
                    <div class="form-group"><label>Lý do cấp phát</label><input type="text" id="exportDcReason" placeholder="VD: Phục vụ thu hoạch đợt 1"></div>
                </div>

                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="document.getElementById('modalExportDungCu').classList.remove('active')">Hủy</button>
                    <button type="submit" class="btn btn-warning"><i class="fas fa-check"></i> Xác nhận cấp phát</button>
                </div>
            </form>
        </div>
    </div>

    <!-- 2. MODAL ĐIỀU ĐỘNG THIẾT BỊ -->
    <div class="modal-overlay" id="modalDieuDong">
        <div class="modal">
            <div class="modal-header">
                <h2>Lập Phiếu Điều Động Thiết Bị</h2>
                <button class="close-modal" onclick="document.getElementById('modalDieuDong').classList.remove('active')">&times;</button>
            </div>
            <form id="formDieuDong">
                <input type="hidden" id="idThietBiAction" value="">
                <div class="form-group"><label>Tên thiết bị</label><p id="tenThietBiDisplay" style="font-weight:500; margin-top:4px; font-size:16px; color:#4d90fe;"></p></div>
                
                <div class="form-row">
                    <div class="form-group"><label>Điều động đến Khu Đất (ID) <span style="color:#e74c3c;">*</span></label><input type="number" id="loDatDieuDong" required placeholder="Nhập ID Khu Đất"></div>
                </div>
                
                <div class="form-row">
                    <div class="form-group"><label>Ngày Điều Động <span style="color:#e74c3c;">*</span></label><input type="datetime-local" id="ngayDieuDong" required></div>
                    <div class="form-group"><label>Ngày Trả Dự Kiến <span style="color:#e74c3c;">*</span></label><input type="datetime-local" id="ngayTraDuKien" required></div>
                </div>

                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="document.getElementById('modalDieuDong').classList.remove('active')">Hủy</button>
                    <button type="submit" class="btn btn-warning"><i class="fas fa-paper-plane"></i> Xuất bãi</button>
                </div>
            </form>
        </div>
    </div>

    <!-- 3. MODAL HÓA ĐƠN NHẬP KHU CHUẨN 15 TRƯỜNG -->
    <div class="modal-overlay" id="modalImportHoaDon">
        <div class="modal large">
            <div class="modal-header">
                <h2><i class="fas fa-file-invoice"></i> Lập Phiếu Nhập Khu (Thiết bị / Dụng cụ)</h2>
                <button class="close-modal" onclick="document.getElementById('modalImportHoaDon').classList.remove('active')">&times;</button>
            </div>
            <form id="formImportHoaDon">
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

                    <div class="form-row">
                        <div class="form-group"><label>URL Ảnh chữ ký Người mua</label><input type="text" id="hdAnhMua" placeholder="Link ảnh"></div>
                        <div class="form-group"><label>URL Ảnh chữ ký Người bán</label><input type="text" id="hdAnhBan" placeholder="Link ảnh"></div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group" style="flex:2;"><label>URL Ảnh bản gốc hóa đơn <span style="color:#e74c3c;">*</span></label><input type="text" id="hdAnhHoaDon" required></div>
                        <div class="form-group" style="flex:1;"><label>Ghi chú</label><input type="text" id="hdGhiChu"></div>
                    </div>
                </div>

                <h4 style="margin-bottom: 12px;"><i class="fas fa-list-ul"></i> Chi tiết Hàng hóa (Bảng ChiTietHoaDonNhap)</h4>
                <div style="overflow-x: auto;">
                    <table class="detail-table" id="importDetailTable">
                        <thead>
                            <tr>
                                <th style="width:15%;">Loại hàng hóa</th>
                                <th style="width:30%;">Tên Thiết bị/Dụng cụ</th>
                                <th style="width:15%;">Số lượng</th>
                                <th style="width:20%;">Đơn giá (VNĐ)</th>
                                <th style="width:20%;">Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody id="importDetailBody">
                            <tr>
                                <td>
                                    <select id="dtLoaiHang" onchange="changeType()">
                                        <option value="2">2: Dụng cụ</option>
                                        <option value="3">3: Thiết bị</option>
                                    </select>
                                </td>
                                <td><select id="dtIdHangHoa"></select></td>
                                <td><input type="number" id="dtSoLuong" value="1" step="1" min="1" oninput="updateSingle()"></td>
                                <td><input type="number" id="dtDonGia" value="0" step="1000" min="0" oninput="updateSingle()"></td>
                                <td><strong id="dtThanhTien">0</strong></td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <div class="total-summary" style="flex-wrap: wrap;">
                    <span>Tổng tiền hàng: <strong class="amount" id="tongTienHang">0</strong> VNĐ</span>
                    <span>Tiền thuế GTGT: <input type="number" id="tienThueGTGT" value="0" style="width:100px; margin-left:10px; border-radius:4px; border:1px solid #d0d8e3; padding:4px;" oninput="updateSingle()"> VNĐ</span>
                    <span>Tổng thanh toán: <strong class="amount" style="color:#e74c3c;" id="tongThanhToan">0</strong> VNĐ</span>
                </div>

                <div class="form-actions">
                    <button type="button" class="btn btn-outline" onclick="document.getElementById('modalImportHoaDon').classList.remove('active')">Hủy</button>
                    <button type="submit" class="btn btn-success"><i class="fas fa-check"></i> Xác nhận nhập khu</button>
                </div>
            </form>
        </div>
    </div>

    <!-- SCRIPT (DÙNG CỘNG CHUỖI ĐỂ TRÁNH LỖI JSP EL) -->
    <script>
        // MOCK DATA CSDL
        let dungCuList = [
            { id: 1, tenDungCu: 'Kéo cắt tỉa cành', donViTinh: 'Cái', tonKhuHienTai: 15, tonKhuToiThieu: 5, idKhuVuc: 3 },
            { id: 2, tenDungCu: 'Bình xịt điện', donViTinh: 'Cái', tonKhuHienTai: 2, tonKhuToiThieu: 3, idKhuVuc: 3 }
        ];
        let nextDCId = 3;
        
        let thietBiList = [
            { id: 1, maThietBi: 'TB-MC01', tenThietBi: 'Máy cày Kubota 45HP', dienTichChiemDung: 5.5, trangThai: 1, idKhuVuc: 4 },
            { id: 2, maThietBi: 'TB-B02', tenThietBi: 'Hệ thống bơm tăng áp', dienTichChiemDung: 1.5, trangThai: 2, idKhuVuc: 4 },
            { id: 3, maThietBi: 'TB-C03', tenThietBi: 'Máy cắt cỏ đeo vai', dienTichChiemDung: 0.5, trangThai: 3, idKhuVuc: 4 }
        ];
        let nextTBId = 4;

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
                trHtml += "<td>" + dc.id + "</td>";
                trHtml += "<td><strong>" + dc.tenDungCu + "</strong></td>";
                trHtml += "<td>Khu Dụng cụ</td>";
                trHtml += "<td>" + dc.donViTinh + "</td>";
                trHtml += "<td><strong style='color: " + textColor + "'>" + dc.tonKhuHienTai + "</strong></td>";
                trHtml += "<td>" + dc.tonKhuToiThieu + "</td>";
                trHtml += "<td><span class='badge-status " + statusClass + "'>" + statusText + "</span></td>";
                
                // CỤM NÚT THAO TÁC DỤNG CỤ
                trHtml += "<td><div class='actions-cell'>";
                trHtml += "<button class='btn-action import btn-nhap-dc' title='Nhập khu' data-id='" + dc.id + "'><i class='fas fa-arrow-down'></i></button>";
                
                // NÚT CẤP PHÁT (MÀU VÀNG)
                trHtml += "<button class='btn-action export btn-xuat-dc' title='Cấp phát' data-id='" + dc.id + "'><i class='fas fa-arrow-up'></i></button>";
                
                trHtml += "<button class='btn-action delete btn-xoa-dc' title='Xóa' data-id='" + dc.id + "'><i class='fas fa-trash'></i></button>";
                trHtml += "</div></td></tr>";

                const tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });
            
            // Lắng nghe click Nhập dụng cụ
            document.querySelectorAll('.btn-nhap-dc').forEach(function(b) { 
                b.addEventListener('click', function() { openImportModal('2', parseInt(this.getAttribute('data-id'))); }); 
            });
            
            // Lắng nghe click Cấp phát (Xuất) dụng cụ 
            document.querySelectorAll('.btn-xuat-dc').forEach(function(b) { 
                b.addEventListener('click', function() { openExportDungCuModal(parseInt(this.getAttribute('data-id'))); }); 
            });
            
            // Lắng nghe click Xóa dụng cụ
            document.querySelectorAll('.btn-xoa-dc').forEach(function(b) { 
                b.addEventListener('click', function() { deleteItem('2', parseInt(this.getAttribute('data-id'))); }); 
            });
        }

        // ===== RENDER BẢNG THIẾT BỊ =====
        function renderThietBi() {
            const tbody = document.getElementById('tbodyThietBi');
            tbody.innerHTML = '';
            thietBiList.forEach(function(tb) {
                let statusText = "Sẵn sàng / Rảnh";
                if(tb.trangThai === 2) statusText = "Đang dùng ngoài vườn";
                if(tb.trangThai === 3) statusText = "Đang bảo trì";

                let statusClass = "available";
                if(tb.trangThai === 2) statusClass = "inuse";
                if(tb.trangThai === 3) statusClass = "maintenance";

                let trHtml = "<tr>";
                trHtml += "<td><strong>" + tb.maThietBi + "</strong></td>";
                trHtml += "<td>" + tb.tenThietBi + "</td>";
                trHtml += "<td>Bãi Thiết bị</td>";
                trHtml += "<td>" + tb.dienTichChiemDung + " m²</td>";
                trHtml += "<td><span class='badge-status " + statusClass + "'>" + statusText + "</span></td>";
                
                // CỤM NÚT THAO TÁC THIẾT BỊ
                trHtml += "<td><div class='actions-cell'>";
                trHtml += "<button class='btn-action import btn-nhap-tb' title='Nhập bãi' data-id='" + tb.id + "'><i class='fas fa-arrow-down'></i></button>";
                if (tb.trangThai === 1) {
                    trHtml += "<button class='btn-action export btn-dieudong' title='Điều động ra vườn' data-id='" + tb.id + "'><i class='fas fa-paper-plane'></i></button>";
                } else if (tb.trangThai === 2) {
                    trHtml += "<button class='btn-action maintain btn-thuhoi' title='Thu hồi về bãi' data-id='" + tb.id + "'><i class='fas fa-undo'></i></button>";
                } else {
                    trHtml += "<button class='btn-action edit' title='Thông tin bảo trì'><i class='fas fa-wrench'></i></button>";
                }
                trHtml += "<button class='btn-action delete btn-xoa-tb' title='Xóa' data-id='" + tb.id + "'><i class='fas fa-trash'></i></button>";
                trHtml += "</div></td></tr>";

                const tr = document.createElement('tr');
                tr.innerHTML = trHtml;
                tbody.appendChild(tr);
            });
            
            document.querySelectorAll('.btn-dieudong').forEach(function(b) { b.addEventListener('click', function() { openDieuDongModal(parseInt(this.getAttribute('data-id'))); }); });
            document.querySelectorAll('.btn-thuhoi').forEach(function(b) { b.addEventListener('click', function() { 
                const tb = thietBiList.find(function(x) { return x.id === parseInt(this.getAttribute('data-id')); });
                tb.trangThai = 1; renderThietBi();
            }); });
            document.querySelectorAll('.btn-nhap-tb').forEach(function(b) { b.addEventListener('click', function() { openImportModal('3', parseInt(this.getAttribute('data-id'))); }); });
            document.querySelectorAll('.btn-xoa-tb').forEach(function(b) { b.addEventListener('click', function() { deleteItem('3', parseInt(this.getAttribute('data-id'))); }); });
        }

        // ===== XÓA =====
        function deleteItem(type, id) {
            if(confirm('Chắc chắn muốn xóa danh mục này?')) {
                if(type === '2') {
                    dungCuList = dungCuList.filter(function(x) { return x.id !== id; });
                    renderDungCu();
                } else {
                    thietBiList = thietBiList.filter(function(x) { return x.id !== id; });
                    renderThietBi();
                }
            }
        }

        // ===== KHAI BÁO MỚI =====
        document.getElementById('formDungCu').addEventListener('submit', function(e) {
            e.preventDefault();
            dungCuList.push({ id: nextDCId++, tenDungCu: document.getElementById('tenDungCu').value, donViTinh: document.getElementById('donViDungCu').value, tonKhuHienTai: 0, tonKhuToiThieu: parseFloat(document.getElementById('tonToiThieuDungCu').value), idKhuVuc: 3 });
            document.getElementById('modalDungCu').classList.remove('active');
            renderDungCu();
        });

        document.getElementById('formThietBi').addEventListener('submit', function(e) {
            e.preventDefault();
            thietBiList.push({ id: nextTBId++, maThietBi: document.getElementById('maThietBi').value, tenThietBi: document.getElementById('tenThietBi').value, dienTichChiemDung: parseFloat(document.getElementById('dienTichThietBi').value), trangThai: parseInt(document.getElementById('trangThaiThietBi').value), idKhuVuc: 4 });
            document.getElementById('modalThietBi').classList.remove('active');
            renderThietBi();
        });

        // ===== BẮT SỰ KIỆN NÚT LẬP PHIẾU NHẬP KHU TRÊN TOOLBAR =====
        document.querySelectorAll('.btn-nhap-chung').forEach(function(b) {
            b.addEventListener('click', function() {
                openImportModal(this.getAttribute('data-type'), null);
            });
        });

        // ===== HÓA ĐƠN NHẬP KHO =====
        let importRowSingle = {}; 

        function openImportModal(loaiHang, idHang) {
            document.getElementById('formImportHoaDon').reset();
            document.getElementById('hdMaHoaDon').value = 'HD-TBDC' + Date.now().toString().slice(-5);
            document.getElementById('hdNgayLap').value = new Date().toISOString().slice(0, 16);
            
            document.getElementById('dtLoaiHang').value = loaiHang;
            importRowSingle = { loaiHang: loaiHang, idHang: idHang, soLuong: 1, donGia: 0 };
            
            changeType(); 
            if (idHang) document.getElementById('dtIdHangHoa').value = idHang;
            updateSingle();

            document.getElementById('modalImportHoaDon').classList.add('active');
        }

        function changeType() {
            const loaiHang = document.getElementById('dtLoaiHang').value;
            const select = document.getElementById('dtIdHangHoa');
            select.innerHTML = '';
            
            const list = (loaiHang === '2') ? dungCuList : thietBiList;
            list.forEach(function(item) {
                const opt = document.createElement('option');
                opt.value = item.id;
                opt.text = item.maThietBi ? (item.maThietBi + " - " + item.tenThietBi) : item.tenDungCu;
                select.add(opt);
            });
            importRowSingle.loaiHang = loaiHang;
        }

        function updateSingle() {
            const sl = parseFloat(document.getElementById('dtSoLuong').value) || 0;
            const dg = parseFloat(document.getElementById('dtDonGia').value) || 0;
            const vat = parseFloat(document.getElementById('tienThueGTGT').value) || 0;
            
            const amount = sl * dg;
            document.getElementById('dtThanhTien').textContent = amount.toLocaleString();
            document.getElementById('tongTienHang').textContent = amount.toLocaleString();
            document.getElementById('tongThanhToan').textContent = (amount + vat).toLocaleString();
        }

        document.getElementById('formImportHoaDon').addEventListener('submit', function(e) {
            e.preventDefault();
            const idToUpdate = parseInt(document.getElementById('dtIdHangHoa').value);
            const loaiHang = document.getElementById('dtLoaiHang').value;
            const soLuong = parseInt(document.getElementById('dtSoLuong').value) || 0;
            
            if(loaiHang === '2') {
                const dc = dungCuList.find(function(x) { return x.id === idToUpdate; });
                if(dc) dc.tonKhuHienTai += soLuong;
                renderDungCu();
            } else {
                alert("Đã cập nhật hệ thống ghi nhận bổ sung thiết bị vào bãi đỗ!");
                renderThietBi();
            }
            document.getElementById('modalImportHoaDon').classList.remove('active');
            alert('Lưu Phiếu Nhập Khu thành công!');
        });

        // ===== CẤP PHÁT DỤNG CỤ (MỚI) =====
        function openExportDungCuModal(id) {
            const dc = dungCuList.find(function(x) { return x.id === id; });
            document.getElementById('exportDcId').value = dc.id;
            document.getElementById('exportDcName').textContent = dc.tenDungCu;
            document.getElementById('exportDcStock').textContent = dc.tonKhuHienTai + " " + dc.donViTinh;
            document.getElementById('formExportDungCu').reset();
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

            dc.tonKhuHienTai -= qty; // Trừ tồn khu
            document.getElementById('modalExportDungCu').classList.remove('active');
            renderDungCu();
            alert("Đã cấp phát dụng cụ thành công và ghi nhận vào hệ thống!");
        });


        // ===== ĐIỀU ĐỘNG THIẾT BỊ =====
        function openDieuDongModal(id) {
            const tb = thietBiList.find(function(x) { return x.id === id; });
            document.getElementById('idThietBiAction').value = tb.id;
            document.getElementById('tenThietBiDisplay').textContent = tb.maThietBi + ' - ' + tb.tenThietBi;
            document.getElementById('formDieuDong').reset();
            
            const now = new Date();
            document.getElementById('ngayDieuDong').value = now.toISOString().slice(0, 16);
            now.setDate(now.getDate() + 1);
            document.getElementById('ngayTraDuKien').value = now.toISOString().slice(0, 16);

            document.getElementById('modalDieuDong').classList.add('active');
        }

        document.getElementById('formDieuDong').addEventListener('submit', function(e) {
            e.preventDefault();
            const id = parseInt(document.getElementById('idThietBiAction').value);
            const tb = thietBiList.find(function(x) { return x.id === id; });
            tb.trangThai = 2; // Chuyển sang 2: Đang điều động
            document.getElementById('modalDieuDong').classList.remove('active');
            renderThietBi();
            alert("Lập Phiếu Điều Động Thiết Bị thành công!");
        });

        // ===== TÌM KIẾM =====
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