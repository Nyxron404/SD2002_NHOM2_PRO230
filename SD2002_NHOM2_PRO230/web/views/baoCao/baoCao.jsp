<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Báo cáo & Thống kê - Trang trại Sầu Riêng</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Thêm thư viện vẽ biểu đồ -->
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

        <style>
            /* BASE & LAYOUT */
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { font-family: 'Roboto', sans-serif; background: #f5f7fa; color: #333; display: flex; height: 100vh; overflow: hidden; }
            .main-content { flex: 1; display: flex; flex-direction: column; height: 100vh; overflow: hidden; }
            .content { flex: 1; padding: 28px 32px; overflow-y: auto; background: #f5f7fa; }

            /* DASHBOARD CARDS (Tổng hợp số liệu) */
            .dashboard-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 20px; margin-bottom: 24px; }
            .stat-card { background: #fff; border-radius: 12px; padding: 20px; display: flex; align-items: center; box-shadow: 0 4px 12px rgba(0,0,0,0.03); border: 1px solid #e9edf4; transition: transform 0.2s; }
            .stat-card:hover { transform: translateY(-5px); box-shadow: 0 8px 16px rgba(0,0,0,0.06); }
            .stat-icon { width: 56px; height: 56px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; margin-right: 16px; flex-shrink: 0;}
            
            /* Các màu Icon */
            .stat-icon.primary { background: #e6f0ff; color: #4d90fe; }
            .stat-icon.warning { background: #fef5e6; color: #f39c12; }
            .stat-icon.success { background: #e8f8f0; color: #27ae60; }
            .stat-icon.danger { background: #fceceb; color: #e74c3c; }
            .stat-icon.gray { background: #e9edf4; color: #4a5b6e; }
            .stat-icon.purple { background: #f4e8fb; color: #8e44ad; }

            .stat-details { flex: 1; overflow: hidden; }
            .stat-details h3 { font-size: 13px; color: #8aa3c0; font-weight: 500; margin-bottom: 4px; text-transform: uppercase; letter-spacing: 0.5px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
            .stat-details p { font-size: 22px; font-weight: 700; color: #1e2a3a; margin: 0; }
            .stat-details .sub-text { font-size: 12px; color: #8aa3c0; margin-top: 5px; }

            /* TOOLBAR & FILTER */
            .toolbar { display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; margin-bottom: 24px; gap: 16px; background: #fff; padding: 16px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.03); border: 1px solid #e9edf4; }
            .filter-group { display: flex; align-items: center; gap: 10px; }
            .filter-group label { font-weight: 500; font-size: 14px; color: #4a5b6e; }
            .filter-group select, .filter-group input[type="date"] { padding: 8px 12px; border: 1px solid #d0d8e3; border-radius: 8px; outline: none; font-family: inherit; }

            /* BẢNG BIỂU */
            .table-container { background: #fff; border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.04); overflow-x: auto; padding: 0 0 8px 0; border: 1px solid #e9edf4; margin-bottom: 24px; }
            .material-table { width: 100%; border-collapse: collapse; font-size: 14px; }
            .material-table th { text-align: left; padding: 16px; color: #6f8fb0; font-weight: 500; border-bottom: 1px solid #e9edf4; background: #fafbfc; white-space: nowrap; text-transform: uppercase; font-size: 12px; }
            .material-table td { padding: 14px 16px; border-bottom: 1px solid #f0f2f7; vertical-align: middle; }
            .material-table tr:hover td { background: #f8faff; }

            /* TRẠNG THÁI */
            .badge-status { display: inline-block; padding: 5px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }
            .badge-status.danger { background: #fadbd8; color: #922b21; }
            .badge-status.warning { background: #fdebd0; color: #a04000; }
            .badge-status.ok { background: #d5f5e3; color: #1e8449; }

            /* BUTTONS */
            .btn { padding: 8px 16px; border: none; border-radius: 8px; font-weight: 500; font-size: 13px; cursor: pointer; display: inline-flex; align-items: center; gap: 8px; transition: 0.2s; }
            .btn-primary { background: #4d90fe; color: #fff; }
            .btn-primary:hover { background: #3a7bd5; }

            /* CHART CONTAINER */
            .chart-wrapper { background: #fff; padding: 24px; border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.04); border: 1px solid #e9edf4; display: flex; justify-content: center; align-items: center; min-height: 400px; }
            .section-title { margin-bottom: 16px; font-size: 16px; color: #1e2a3a; font-weight: 600; display: flex; align-items: center; gap: 8px; }
        </style>
    </head>
    <body>

        <%@ include file="/views/commons/sidebar.jsp" %>

        <div class="main-content">
            <%@ include file="/views/commons/header.jsp" %>
            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    var pageTitle = document.getElementById('pageHeaderTitle');
                    if (pageTitle)
                        pageTitle.innerHTML = 'Báo cáo & Thống kê <span>| Phân tích hiệu quả kinh doanh</span>';
                });
            </script>

            <section class="content">

                <!-- ===== DASHBOARD TỔNG HỢP (8 THẺ) ===== -->
                <div class="dashboard-cards">
                    <!-- Thẻ 1: Doanh thu & Sản lượng -->
                    <div class="stat-card">
                        <div class="stat-icon success"><i class="fas fa-hand-holding-usd"></i></div>
                        <div class="stat-details">
                            <h3>Tài chính & Thu hoạch</h3>
                            <p><fmt:formatNumber value="${uocTinhDoanhThu}" pattern="#,##0"/> đ</p>
                            <div class="sub-text">Sản lượng: <strong style="color:#27ae60;"><fmt:formatNumber value="${tongSanLuong}" pattern="#,##0.##"/> kg</strong></div>
                        </div>
                    </div>

                    <!-- Thẻ 2: Chi phí Canh tác -->
                    <div class="stat-card">
                        <div class="stat-icon danger"><i class="fas fa-money-bill-wave"></i></div>
                        <div class="stat-details">
                            <h3>Tổng chi phí tiêu hao</h3>
                            <p><fmt:formatNumber value="${tongChiPhiTrangTrai}" pattern="#,##0"/> đ</p>
                            <div class="sub-text">Phân bổ từ Vật tư, Dụng cụ & Khấu hao</div>
                        </div>
                    </div>

                    <!-- Thẻ 3: Quy mô Trang trại -->
                    <div class="stat-card">
                        <div class="stat-icon primary"><i class="fas fa-map-marked-alt"></i></div>
                        <div class="stat-details">
                            <h3>Quy mô Trang trại</h3>
                            <p>${soVuon != null ? soVuon : 0} Vườn trồng</p>
                            <div class="sub-text">${soKhuVuc != null ? soKhuVuc : 0} Khu vực | <strong><fmt:formatNumber value="${tongSoCay != null ? tongSoCay : 0}" pattern="#,##0"/> Cây sầu riêng</strong></div>
                        </div>
                    </div>
                    
                    <!-- Thẻ 4: Tổng Vật tư -->
                    <div class="stat-card">
                        <div class="stat-icon primary"><i class="fas fa-cubes"></i></div>
                        <div class="stat-details">
                            <h3>Danh mục Vật tư</h3>
                            <p style="color: #4d90fe;">${tongVatTu} Loại</p>
                            <div class="sub-text">Đang được quản lý trong hệ thống</div>
                        </div>
                    </div>

                    <!-- Thẻ 5: Kho Vật tư (Cảnh báo) -->
                    <div class="stat-card">
                        <div class="stat-icon warning"><i class="fas fa-boxes"></i></div>
                        <div class="stat-details">
                            <h3>Cảnh báo Kho Vật tư</h3>
                            <p style="color: #e74c3c; font-size: 20px;">${loHangDaHetHan != null ? loHangDaHetHan.size() : 0} lô đã quá hạn</p>
                            <div class="sub-text"><strong style="color: #f39c12;">${loHangSapHetHan != null ? loHangSapHetHan.size() : 0}</strong> lô sắp hết | <strong style="color: #c0392b;">${vatTuDuoiNguong != null ? vatTuDuoiNguong.size() : 0}</strong> VT cần nhập</div>
                        </div>
                    </div>

                    <!-- Thẻ 6: Thiết bị Máy móc -->
                    <div class="stat-card">
                        <div class="stat-icon gray"><i class="fas fa-tractor"></i></div>
                        <div class="stat-details">
                            <h3>Trạng thái Thiết bị</h3>
                            <p>${tbSanSang != null ? tbSanSang : 0} Sẵn sàng</p>
                            <div class="sub-text"><strong style="color: #f39c12;">${tbDangSuDung != null ? tbDangSuDung : 0}</strong> Đang dùng | <strong style="color: #e74c3c;">${tbBaoTri != null ? tbBaoTri : 0}</strong> Bảo trì/Hỏng</div>
                        </div>
                    </div>

                    <!-- Thẻ 7: Lịch Bảo trì -->
                    <div class="stat-card">
                        <div class="stat-icon purple"><i class="fas fa-tools"></i></div>
                        <div class="stat-details">
                            <h3>Lịch Bảo trì Máy móc</h3>
                            <p style="color: #8e44ad;">${tbQuaHanBaoTri != null ? tbQuaHanBaoTri : 0} Quá hạn</p>
                            <div class="sub-text"><strong style="color: #9b59b6;">${tbSapBaoTri != null ? tbSapBaoTri : 0}</strong> Thiết bị sắp đến hạn bảo trì</div>
                        </div>
                    </div>
                    
                    <!-- Thẻ 8: Nhắc việc -->
                    <div class="stat-card">
                        <div class="stat-icon" style="background:#eaf6ee; color:#27ae60;"><i class="fas fa-bell"></i></div>
                        <div class="stat-details">
                            <h3>Công việc canh tác</h3>
                            <p style="color: #27ae60;">${nhacViecCho != null ? nhacViecCho : 0} Chờ xử lý</p>
                            <div class="sub-text">Tự động phát sinh từ Lịch chăm sóc</div>
                        </div>
                    </div>
                </div>

                <!-- ===== TOOLBAR (BỘ LỌC) ===== -->
                <form action="baocao" method="GET">
                    <div class="toolbar">
                        <div class="filter-group">
                            <label for="reportType"><i class="fas fa-filter"></i> Loại báo cáo:</label>
                            <select id="reportType" name="type" onchange="this.form.submit()">
                                <option value="">-- Mời bạn chọn báo cáo cần hiển thị --</option>
                                <option value="chi-phi" ${param.type == 'chi-phi' ? 'selected' : ''}>Thống kê Chi phí Sản xuất</option>
                                <option value="ton-kho" ${param.type == 'ton-kho' ? 'selected' : ''}>Báo cáo Tồn kho Vật tư</option>
                                <option value="nang-suat" ${param.type == 'nang-suat' ? 'selected' : ''}>Thống kê Năng suất & Sản lượng</option>
                            </select>
                        </div>

                        <!-- Lọc ngày tháng cho Chi phí / Năng suất -->
                        <c:if test="${param.type == 'chi-phi' || param.type == 'nang-suat'}">
                            <div style="display: flex; gap: 16px;">
                                <div class="filter-group">
                                    <label>Từ ngày:</label>
                                    <input type="date" name="startDate" value="${startDate}">
                                </div>
                                <div class="filter-group">
                                    <label>Đến ngày:</label>
                                    <input type="date" name="endDate" value="${endDate}">
                                </div>
                                <button type="submit" class="btn btn-primary"><i class="fas fa-sync-alt"></i> Cập nhật dữ liệu</button>
                            </div>
                        </c:if>
                    </div>
                </form>

                <!-- ===== KHU VỰC HIỂN THỊ KẾT QUẢ BÁO CÁO CỤ THỂ ===== -->

                <!-- 1. BÁO CÁO CHI PHÍ -->
                <c:if test="${param.type == 'chi-phi'}">
                    <h3 class="section-title"><i class="fas fa-pie-chart" style="color: #4d90fe;"></i> Phân bổ Chi phí Vật tư theo Loại (Từ ${startDate} đến ${endDate})</h3>
                    <div class="chart-wrapper">
                        <c:choose>
                            <c:when test="${empty chiPhiDataMap}">
                                <div style="text-align: center; color: #8aa3c0; padding: 40px;">
                                    <i class="fas fa-folder-open" style="font-size: 48px; margin-bottom: 16px; color: #d0d8e3;"></i>
                                    <p>Không có dữ liệu chi phí tiêu hao vật tư trong khoảng thời gian này.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div style="position: relative; height:50vh; width:100%">
                                    <canvas id="chiPhiChart"></canvas>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>

                <!-- 2. BÁO CÁO TỒN KHO -->
                <c:if test="${param.type == 'ton-kho'}">
                    <h3 class="section-title"><i class="fas fa-list-ul" style="color: #27ae60;"></i> Danh sách Toàn bộ Vật tư trong hệ thống</h3>
                    <div class="table-container">
                        <table class="material-table">
                            <thead>
                                <tr>
                                    <th>Mã VT</th>
                                    <th>Tên vật tư</th>
                                    <th>Đơn vị tính</th>
                                    <th>Quy cách</th>
                                    <th>Tồn kho hiện tại</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${listAllVatTu}" var="vt">
                                    <tr>
                                        <td><strong>VT00${vt.id}</strong></td>
                                        <td>${vt.ten_vat_tu}</td>
                                        <td>${vt.don_vi_tinh}</td>
                                        <td>${vt.quy_cach_dong_goi}</td>
                                        <td><strong style="color: #27ae60;">${vt.ton_kho_hien_tai}</strong></td>
                                        <td><span class="badge-status ${vt.ton_kho_hien_tai <= vt.ton_kho_toi_thieu ? 'danger' : 'ok'}">${vt.trang_thai}</span></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty listAllVatTu}">
                                    <tr><td colspan="6" style="text-align: center; padding: 20px;">Kho chưa có dữ liệu vật tư.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>

                    <h3 class="section-title" style="margin-top: 32px;"><i class="fas fa-boxes" style="color: #e74c3c;"></i> Danh sách Vật tư cần nhập bổ sung (Dưới định mức)</h3>
                    <div class="table-container">
                        <table class="material-table">
                            <thead>
                                <tr>
                                    <th>Mã VT</th>
                                    <th>Tên vật tư</th>
                                    <th>Số lượng hiện tại</th>
                                    <th>Ngưỡng tối thiểu</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${vatTuDuoiNguong}" var="vt">
                                    <tr>
                                        <td><strong>VT00${vt.id}</strong></td>
                                        <td>${vt.ten_vat_tu}</td>
                                        <td><strong style="color: #e74c3c;">${vt.ton_kho_hien_tai} ${vt.don_vi_tinh}</strong></td>
                                        <td>${vt.ton_kho_toi_thieu} ${vt.don_vi_tinh}</td>
                                        <td><span class="badge-status danger">Cần nhập thêm</span></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty vatTuDuoiNguong}">
                                    <tr><td colspan="5" style="text-align: center; padding: 20px; color: #8aa3c0;">Kho bảo đảm an toàn. Không có vật tư nào dưới định mức.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>

                    <h3 class="section-title" style="margin-top: 32px;"><i class="fas fa-calendar-times" style="color: #f39c12;"></i> Danh sách Lô hàng sắp / đã hết hạn</h3>
                    <div class="table-container">
                        <table class="material-table">
                            <thead>
                                <tr>
                                    <th>Số Lô</th>
                                    <th>Thuộc Vật tư</th>
                                    <th>Hạn sử dụng</th>
                                    <th>Tồn dư trong lô</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${loHangDaHetHan}" var="lo">
                                    <tr style="background-color: #fdf5f4;">
                                        <td><strong>${lo.so_lo}</strong></td>
                                        <td>${lo.ten_vat_tu != null ? lo.ten_vat_tu : 'Đang cập nhật'}</td>
                                        <td><strong style="color: #c0392b;">${lo.han_su_dung}</strong></td>
                                        <td>${lo.so_luong_con_lai}</td>
                                        <td><span class="badge-status danger">Đã quá hạn</span></td>
                                    </tr>
                                </c:forEach>

                                <c:forEach items="${loHangSapHetHan}" var="lo">
                                    <tr>
                                        <td><strong>${lo.so_lo}</strong></td>
                                        <td>${lo.ten_vat_tu != null ? lo.ten_vat_tu : 'Đang cập nhật'}</td>
                                        <td><strong style="color: #f39c12;">${lo.han_su_dung}</strong></td>
                                        <td>${lo.so_luong_con_lai}</td>
                                        <td><span class="badge-status warning">Sắp hết hạn</span></td>
                                    </tr>
                                </c:forEach>
                                
                                <c:if test="${empty loHangSapHetHan && empty loHangDaHetHan}">
                                    <tr><td colspan="5" style="text-align: center; padding: 20px; color: #8aa3c0;">Kho bảo đảm an toàn. Không có lô hàng nào sắp/đã hết hạn.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </c:if>

                <!-- 3. BÁO CÁO NĂNG SUẤT -->
                <c:if test="${param.type == 'nang-suat'}">
                    <h3 class="section-title"><i class="fas fa-chart-bar" style="color: #27ae60;"></i> Báo cáo Năng suất thu hoạch theo Lô (kg)</h3>
                    <div class="chart-wrapper">
                        <c:choose>
                            <c:when test="${empty nangSuatDataMap}">
                                <div style="text-align: center; color: #8aa3c0; padding: 40px;">
                                    <i class="fas fa-seedling" style="font-size: 48px; margin-bottom: 16px; color: #d0d8e3;"></i>
                                    <p>Chưa có dữ liệu sản lượng thu hoạch sầu riêng nào được ghi nhận trong thời gian này.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div style="position: relative; height:50vh; width:100%">
                                    <canvas id="nangSuatChart"></canvas>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>

            </section>
        </div>

        <!-- SCRIPT VẼ BIỂU ĐỒ (DOUGHNUT VÀ BAR) BẰNG CHART.JS -->
        <script>
            <c:if test="${param.type == 'chi-phi' && not empty chiPhiDataMap}">
                const cpLabels = [];
                const cpDataValues = [];

                <c:forEach items="${chiPhiDataMap}" var="entry">
                    cpLabels.push("${entry.key}");
                    cpDataValues.push(${entry.value});
                </c:forEach>

                const ctxCP = document.getElementById('chiPhiChart').getContext('2d');
                new Chart(ctxCP, {
                    type: 'doughnut', 
                    data: {
                        labels: cpLabels,
                        datasets: [{
                            label: 'Tổng tiền (VNĐ)',
                            data: cpDataValues,
                            backgroundColor: ['#4d90fe', '#e74c3c', '#27ae60', '#f39c12', '#9b59b6', '#34495e'],
                            borderWidth: 0,
                            hoverOffset: 10
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { position: 'right', labels: { font: {family: "'Roboto', sans-serif", size: 14}, padding: 20 } },
                            tooltip: {
                                callbacks: {
                                    label: function (context) {
                                        let label = context.label || '';
                                        if (label) label += ': ';
                                        if (context.parsed !== null) {
                                            label += new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(context.parsed);
                                        }
                                        return label;
                                    }
                                }
                            }
                        },
                        cutout: '60%' 
                    }
                });
            </c:if>

            <c:if test="${param.type == 'nang-suat' && not empty nangSuatDataMap}">
                const nsLabels = [];
                const nsDataValues = [];

                <c:forEach items="${nangSuatDataMap}" var="entry">
                    nsLabels.push("${entry.key}");
                    nsDataValues.push(${entry.value});
                </c:forEach>

                const ctxNS = document.getElementById('nangSuatChart').getContext('2d');
                new Chart(ctxNS, {
                    type: 'bar', 
                    data: {
                        labels: nsLabels,
                        datasets: [{
                            label: 'Sản lượng thu hoạch (kg)',
                            data: nsDataValues,
                            backgroundColor: '#27ae60', 
                            borderRadius: 6
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {
                            y: { beginAtZero: true }
                        },
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return context.parsed.y.toLocaleString() + ' kg';
                                    }
                                }
                            }
                        }
                    }
                });
            </c:if>
        </script>
    </body>
</html>