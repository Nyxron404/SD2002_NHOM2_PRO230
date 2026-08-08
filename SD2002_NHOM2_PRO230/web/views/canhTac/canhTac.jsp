<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Quản lý Canh tác - Trang trại Sầu Riêng</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
<style>
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:Roboto,Arial,sans-serif;background:#f5f7fa;color:#333;display:flex;min-height:100vh}
.main-content{flex:1;min-width:0}.content{padding:28px 32px}
.tabs{display:flex;flex-wrap:wrap;gap:4px;margin-bottom:24px;background:#fff;border-radius:12px;padding:4px;border:1px solid #e9edf4}
.tabs a{padding:10px 18px;text-decoration:none;color:#6f8fb0;border-radius:8px;font-size:14px}.tabs a.active{background:#4d90fe;color:#fff}
.panel{background:#fff;border:1px solid #e9edf4;border-radius:14px;padding:22px;margin-bottom:20px}
.toolbar{display:flex;justify-content:space-between;gap:12px;align-items:center;margin-bottom:16px}.toolbar h2{font-size:20px;color:#1e2a3a}
.btn{display:inline-block;padding:9px 15px;border:0;border-radius:8px;text-decoration:none;cursor:pointer;color:#fff;background:#4d90fe}.btn.green{background:#6fcf97}.btn.red{background:#e74c3c}.btn.gray{background:#7d8da0}
.table-wrap{overflow:auto}.data-table{width:100%;border-collapse:collapse;min-width:800px}.data-table th,.data-table td{padding:12px;border-bottom:1px solid #edf0f4;text-align:left}.data-table th{background:#fafbfc;color:#6f8fb0}
.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:14px}.field label{display:block;font-weight:600;font-size:13px;margin-bottom:6px}.field input,.field select,.field textarea{width:100%;padding:9px;border:1px solid #d0d8e3;border-radius:7px}.full{grid-column:1/-1}
.area{background:#eef8f0!important;color:#237b43;font-weight:bold}.notice{padding:12px;background:#eef5ff;border:1px solid #d5e4fa;border-radius:8px;margin-bottom:16px;line-height:1.5}
.actions{display:flex;gap:8px;margin-top:16px}.badge{display:inline-block;padding:4px 9px;border-radius:15px;background:#eaf6ee;color:#237b43}.danger{background:#fde7e5;color:#a83227}.warn{background:#fff2dc;color:#a55b00}
details{margin-top:10px}summary{cursor:pointer;font-weight:600;color:#4d90fe}
@media(max-width:850px){.form-grid{grid-template-columns:1fr}.full{grid-column:auto}.content{padding:15px}}
</style>
</head>
<body>
<%@include file="/views/commons/sidebar.jsp"%>
<div class="main-content">
<%@include file="/views/commons/header.jsp"%>
<section class="content">

<div class="tabs">
<a class="${empty param.tab || param.tab=='giong'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=giong">Giống cây</a>
<a class="${param.tab=='vuon'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=vuon">Thiết lập vườn</a>
<a class="${param.tab=='lich'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=lich">Lịch chăm sóc</a>
<a class="${param.tab=='nhatky'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=nhatky">Nhật ký canh tác</a>
<a class="${param.tab=='sinhtruong'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=sinhtruong">Sinh trưởng</a>
<a class="${param.tab=='saubenh'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=saubenh">Sâu bệnh</a>
<a class="${param.tab=='thuhoach'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=thuhoach">Thu hoạch</a>
</div>

<c:if test="${not empty sessionScope.canhTacMessage}">
<div class="notice">${sessionScope.canhTacMessage}</div>
<c:remove var="canhTacMessage" scope="session"/>
</c:if>

<!-- GIỐNG -->
<div class="panel">
<div class="toolbar"><h2>Giống sầu riêng</h2></div>
<details open><summary>Thêm giống</summary>
<form method="post" action="${pageContext.request.contextPath}/canhtac">
<input type="hidden" name="action" value="giong_insert">
<input type="hidden" name="tab" value="giong">
<div class="form-grid" style="margin-top:14px">
<div class="field"><label>Tên giống *</label><input name="ten_giong" required></div>
<div class="field"><label>Thời gian sinh trưởng/thu hoạch (tháng)</label><input name="thoi_gian_sinh_truong_thu_hoach" type="number" min="0"></div>
<div class="field"><label>Năng suất tham khảo (kg/cây)</label><input name="nang_suat_tham_khao" type="number" step="0.01" min="0"></div>
<div class="field"><label>Trạng thái</label><input name="trang_thai" value="Đang canh tác"></div>
<div class="field full"><label>Đặc điểm</label><textarea name="dac_diem"></textarea></div>
</div><div class="actions"><button class="btn">Thêm giống</button></div>
</form></details>
<div class="table-wrap"><table class="data-table"><thead><tr><th>ID</th><th>Tên giống</th><th>Đặc điểm</th><th>Thời gian</th><th>Năng suất</th><th>Trạng thái</th><th>Xóa</th></tr></thead><tbody>
<c:forEach var="g" items="${listGiong}">
<tr><td>${g.id}</td><td>${g.ten_giong}</td><td>${g.dac_diem}</td><td>${g.thoi_gian_sinh_truong_thu_hoach}</td><td>${g.nang_suat_tham_khao}</td><td>${g.trang_thai}</td>
<td><form method="post" action="${pageContext.request.contextPath}/canhtac"><input type="hidden" name="action" value="giong_delete"><input type="hidden" name="id" value="${g.id}"><input type="hidden" name="tab" value="giong"><button class="btn red">Xóa</button></form></td></tr>
</c:forEach></tbody></table></div>
</div>

<!-- VƯỜN -->
<div class="panel">
<div class="toolbar"><h2>Thiết lập vườn trồng</h2></div>
<div class="notice"><b>Diện tích không nhập tại Canh tác.</b> Diện tích được lấy trực tiếp từ lô đất đã được bạn phân chia trong <b>Quản lý trang trại/khu vực → DonViQuanLy.dien_tich</b>. Canh tác chỉ quản lý giống, số cây và trạng thái của lô.</div>
<details open><summary>Thiết lập vườn mới</summary>
<form method="post" action="${pageContext.request.contextPath}/canhtac">
<input type="hidden" name="action" value="vuon_insert"><input type="hidden" name="tab" value="vuon">
<div class="form-grid" style="margin-top:14px">
<div class="field"><label>Lô đất *</label><select name="lo_dat_id" required><option value="">-- Chọn lô đất --</option>
<c:forEach var="lo" items="${listLoDat}"><option value="${lo.id}">${lo.ten_don_vi} - ${lo.dien_tich} ha</option></c:forEach></select></div>
<div class="field"><label>Giống *</label><select name="giong_id" required><option value="">-- Chọn giống --</option>
<c:forEach var="g" items="${listGiong}"><option value="${g.id}">${g.ten_giong}</option></c:forEach></select></div>
<div class="field"><label>Số lượng cây *</label><input name="so_luong_cay" type="number" min="1" required></div>
<div class="field"><label>Ngày trồng</label><input name="ngay_trong" type="date"></div>
<div class="field full"><label>Ghi chú</label><textarea name="ghi_chu"></textarea></div>
</div><div class="actions"><button class="btn">Lưu thiết lập vườn</button></div>
</form></details>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Lô</th><th>Giống</th><th>Diện tích DB</th><th>Số cây</th><th>Mật độ</th><th>Phân loại</th><th>Xóa</th></tr></thead><tbody>
<c:forEach var="v" items="${listVuon}">
<tr><td>${v.ten_lo_dat}</td><td>${v.ten_giong}</td><td><b>${v.dien_tich} ha</b></td><td>${v.so_luong_cay}</td><td>${v.mat_do_trong} cây/ha</td>
<td><span class="badge ${v.mat_do_bat_thuong?'danger':(v.phan_loai_mat_do=='Thưa'?'warn':'')}">${v.phan_loai_mat_do}</span></td>
<td><form method="post" action="${pageContext.request.contextPath}/canhtac"><input type="hidden" name="action" value="vuon_delete"><input type="hidden" name="id" value="${v.id}"><input type="hidden" name="tab" value="vuon"><button class="btn red">Xóa</button></form></td></tr>
</c:forEach></tbody></table></div>
</div>

<!-- LỊCH -->
<div class="panel">
<div class="toolbar"><h2>Lịch chăm sóc</h2></div>
<details><summary>Tạo lịch chăm sóc</summary>
<form method="post" action="${pageContext.request.contextPath}/canhtac" style="margin-top:14px">
<input type="hidden" name="action" value="lich_insert"><input type="hidden" name="tab" value="lich">
<div class="form-grid">
<div class="field"><label>Công việc *</label><input name="loai_cong_viec" required></div>
<div class="field"><label>Ngày bắt đầu *</label><input name="ngay_bat_dau" type="date" required></div>
<div class="field"><label>Chu kỳ (ngày)</label><input name="chu_ky_ngay" type="number" min="0"></div>
<div class="field"><label>Ngày kết thúc</label><input name="ngay_ket_thuc" type="date"></div>
<div class="field"><label>Trạng thái</label><select name="trang_thai"><option>Đang áp dụng</option><option>Tạm dừng</option><option>Hoàn thành</option></select></div>
<div class="field full"><label>Áp dụng cho lô</label><select name="lo_ids" multiple size="5"><c:forEach var="lo" items="${listLoDat}"><option value="${lo.id}">${lo.ten_don_vi}</option></c:forEach></select></div>
<div class="field full"><label>Mô tả</label><textarea name="mo_ta"></textarea></div>
</div><div class="actions"><button class="btn">Tạo lịch</button></div>
</form></details>
<div class="table-wrap"><table class="data-table"><thead><tr><th>ID</th><th>Công việc</th><th>Bắt đầu</th><th>Chu kỳ</th><th>Trạng thái</th><th>Xóa</th></tr></thead><tbody>
<c:forEach var="l" items="${listLich}"><tr><td>${l.id}</td><td>${l.loai_cong_viec}</td><td>${l.ngay_bat_dau}</td><td>${l.chu_ky_ngay}</td><td>${l.trang_thai}</td><td><form method="post" action="${pageContext.request.contextPath}/canhtac"><input type="hidden" name="action" value="lich_delete"><input type="hidden" name="id" value="${l.id}"><input type="hidden" name="tab" value="lich"><button class="btn red">Xóa</button></form></td></tr></c:forEach>
</tbody></table></div>
</div>

<!-- NHẬT KÝ -->
<div class="panel">
<div class="toolbar"><h2>Nhật ký canh tác</h2></div>
<details><summary>Ghi nhật ký chăm sóc</summary>
<form method="post" action="${pageContext.request.contextPath}/canhtac" style="margin-top:14px">
<input type="hidden" name="action" value="nhatky_insert"><input type="hidden" name="tab" value="nhatky">
<div class="form-grid">
<div class="field"><label>Lô đất *</label><select name="lo_dat_id" required><option value="">-- Chọn lô --</option><c:forEach var="lo" items="${listLoDat}"><option value="${lo.id}">${lo.ten_don_vi}</option></c:forEach></select></div>
<div class="field"><label>Công việc *</label><input name="loai_cong_viec" required></div>
<div class="field"><label>Ngày thực hiện *</label><input name="ngay_thuc_hien" type="date" required></div>
<div class="field full"><label>Mô tả</label><textarea name="mo_ta"></textarea></div>
</div>
<h3 style="margin:20px 0 10px">Vật tư</h3>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Chọn</th><th>Vật tư</th><th>Số lượng</th></tr></thead><tbody>
<c:forEach var="vt" items="${listVatTu}"><tr><td><input type="checkbox" name="vt_id" value="${vt.id}"></td><td>${vt.ten_vat_tu}</td><td><input name="vt_qty" type="number" step="0.01" min="0" value="0"></td></tr></c:forEach>
</tbody></table></div>
<h3 style="margin:20px 0 10px">Dụng cụ</h3>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Chọn</th><th>Dụng cụ</th><th>Số lượng</th></tr></thead><tbody>
<c:forEach var="dc" items="${listDungCu}"><tr><td><input type="checkbox" name="dc_id" value="${dc.id}"></td><td>${dc.ten_dung_cu}</td><td><input name="dc_qty" type="number" step="0.01" min="0" value="0"></td></tr></c:forEach>
</tbody></table></div>
<h3 style="margin:20px 0 10px">Thiết bị</h3>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Chọn</th><th>Thiết bị</th><th>Số ngày sử dụng</th></tr></thead><tbody>
<c:forEach var="tb" items="${listThietBi}"><tr><td><input type="checkbox" name="tb_id" value="${tb.id}"></td><td>${tb.ten_thiet_bi}</td><td><input name="tb_ngay" type="number" min="1" value="1"></td></tr></c:forEach>
</tbody></table></div>
<div class="notice" style="margin-top:14px">Chi phí thiết bị được backend tính từ dữ liệu thiết bị/khấu hao; không nhập chi phí thủ công.</div>
<div class="actions"><button class="btn green">Lưu nhật ký</button></div>
</form></details>
<div class="table-wrap"><table class="data-table"><thead><tr><th>ID</th><th>Lô</th><th>Công việc</th><th>Ngày</th><th>Vật tư</th><th>Dụng cụ</th><th>Thiết bị</th><th>Tổng</th><th>Xóa</th></tr></thead><tbody>
<c:forEach var="n" items="${listNhatKy}"><tr><td>${n.id}</td><td>${n.ten_lo_dat}</td><td>${n.loai_cong_viec}</td><td>${n.ngay_thuc_hien}</td><td>${n.tong_chi_phi_vat_tu}</td><td>${n.tong_chi_phi_dung_cu}</td><td>${n.tong_chi_phi_thiet_bi}</td><td><b>${n.tong_chi_phi}</b></td><td><form method="post" action="${pageContext.request.contextPath}/canhtac"><input type="hidden" name="action" value="nhatky_delete"><input type="hidden" name="id" value="${n.id}"><input type="hidden" name="tab" value="nhatky"><button class="btn red">Xóa</button></form></td></tr></c:forEach>
</tbody></table></div>
</div>

<!-- SINH TRƯỞNG -->
<div class="panel">
<div class="toolbar"><h2>Theo dõi sinh trưởng</h2></div>
<details><summary>Cập nhật sinh trưởng</summary>
<form method="post" action="${pageContext.request.contextPath}/canhtac" style="margin-top:14px">
<input type="hidden" name="action" value="sinhtruong_insert"><input type="hidden" name="tab" value="sinhtruong">
<div class="form-grid">
<div class="field"><label>Vườn/lô *</label><select name="vuon_trong_id" required><option value="">-- Chọn --</option><c:forEach var="v" items="${listVuon}"><option value="${v.id}">${v.ten_lo_dat}</option></c:forEach></select></div>
<div class="field"><label>Giai đoạn mới</label><input name="giai_doan_moi"></div>
<div class="field"><label>Tỷ lệ giai đoạn</label><input name="ty_le_giai_doan"></div>
<div class="field"><label>Số cây giảm</label><input name="so_luong_cay_giam" type="number" min="0" value="0"></div>
<div class="field"><label>Loại cập nhật</label><select name="loai_cap_nhat"><option>Chuyển giai đoạn</option><option>Giảm số cây</option></select></div>
<div class="field full"><label>Ghi chú</label><textarea name="ghi_chu"></textarea></div>
</div><div class="actions"><button class="btn">Cập nhật</button></div>
</form></details>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Lô</th><th>Giai đoạn</th><th>Tỷ lệ</th><th>Giảm cây</th><th>Ngày</th></tr></thead><tbody>
<c:forEach var="s" items="${listSinhTruong}"><tr><td>${s.ten_lo_dat}</td><td>${s.giai_doan_moi}</td><td>${s.ty_le_giai_doan}</td><td>${s.so_luong_cay_giam}</td><td>${s.ngay_cap_nhat}</td></tr></c:forEach>
</tbody></table></div>
</div>

<!-- SÂU BỆNH -->
<div class="panel">
<div class="toolbar"><h2>Theo dõi sâu bệnh</h2></div>
<details><summary>Ghi nhận sâu bệnh</summary>
<form method="post" action="${pageContext.request.contextPath}/canhtac" style="margin-top:14px">
<input type="hidden" name="action" value="saubenh_insert"><input type="hidden" name="tab" value="saubenh">
<div class="form-grid">
<div class="field"><label>Lô *</label><select name="vuon_trong_id" required><option value="">-- Chọn --</option><c:forEach var="v" items="${listVuon}"><option value="${v.id}">${v.ten_lo_dat}</option></c:forEach></select></div>
<div class="field"><label>Sâu bệnh *</label><input name="ten_sau_benh" required></div>
<div class="field"><label>Mức độ</label><select name="muc_do_nghiem_trong"><option>Nhẹ</option><option>Trung bình</option><option>Nặng</option></select></div>
<div class="field"><label>Ngày phát hiện</label><input name="ngay_phat_hien" type="date"></div>
<div class="field"><label>Trạng thái</label><select name="trang_thai"><option>Chưa xử lý</option><option>Đang xử lý</option><option>Đã xử lý</option></select></div>
<div class="field full"><label>Biện pháp xử lý</label><textarea name="bien_phap_xu_ly"></textarea></div>
</div><div class="actions"><button class="btn">Lưu</button></div>
</form></details>
<div class="table-wrap"><table class="data-table"><thead><tr><th>ID</th><th>Lô</th><th>Sâu bệnh</th><th>Mức độ</th><th>Ngày</th><th>Xử lý</th><th>Trạng thái</th></tr></thead><tbody>
<c:forEach var="s" items="${listSauBenh}"><tr><td>${s.id}</td><td>${s.ten_lo_dat}</td><td>${s.ten_sau_benh}</td><td>${s.muc_do_nghiem_trong}</td><td>${s.ngay_phat_hien}</td><td>${s.bien_phap_xu_ly}</td><td>${s.trang_thai}</td></tr></c:forEach>
</tbody></table></div>
</div>

<!-- THU HOẠCH -->
<div class="panel">
<div class="toolbar"><h2>Thu hoạch</h2></div>
<details><summary>Ghi nhận thu hoạch</summary>
<form method="post" action="${pageContext.request.contextPath}/canhtac" style="margin-top:14px">
<input type="hidden" name="action" value="thuhoach_insert"><input type="hidden" name="tab" value="thuhoach">
<div class="form-grid">
<div class="field"><label>Vườn/lô *</label><select name="vuon_trong_id" required><option value="">-- Chọn --</option><c:forEach var="v" items="${listVuon}"><option value="${v.id}">${v.ten_lo_dat}</option></c:forEach></select></div>
<div class="field"><label>Vụ mùa *</label><input name="ten_vu_mua" required></div>
<div class="field"><label>Ngày thu hoạch *</label><input name="ngay_thu_hoach" type="date" required></div>
<div class="field"><label>Sản lượng (kg) *</label><input name="tong_san_luong_kg" type="number" step="0.01" min="0" required></div>
<div class="field"><label>Vị trí lưu trữ</label><input name="vi_tri_luu_tru_id" type="number" min="0" value="0"></div>
<div class="field"><label>Diện tích chiếm dụng</label><input name="tong_dien_tich_chiem_dung" type="number" step="0.01" min="0" value="0"></div>
<div class="field"><label>Trạng thái kho</label><select name="trang_thai_luu_kho"><option>Đã nhập kho</option><option>Chờ nhập kho</option></select></div>
<div class="field full"><label>Ghi chú</label><textarea name="ghi_chu"></textarea></div>
</div><div class="actions"><button class="btn green">Lưu thu hoạch</button></div>
</form></details>
<div class="table-wrap"><table class="data-table"><thead><tr><th>ID</th><th>Lô</th><th>Vụ mùa</th><th>Ngày</th><th>Sản lượng</th><th>Kho</th></tr></thead><tbody>
<c:forEach var="t" items="${listThuHoach}"><tr><td>${t.id}</td><td>${t.ten_lo_dat}</td><td>${t.ten_vu_mua}</td><td>${t.ngay_thu_hoach}</td><td>${t.tong_san_luong_kg} kg</td><td>${t.trang_thai_luu_kho}</td></tr></c:forEach>
</tbody></table></div>
</div>

</section></div>
</body>
</html>
