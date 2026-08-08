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
:root{
  --xanh:#2f8f5b; --xanh-nhat:#eaf6ee; --xanh-dam:#1e6b41;
  --duong:#3b7ddd; --duong-nhat:#eef4ff;
  --do:#e05555; --do-nhat:#fdeceb;
  --cam:#e08a2b; --cam-nhat:#fff4e2;
  --xam:#6b7a90; --vien:#e6ebf2; --nen:#f4f6fa; --chu:#1f2a37;
  --bong:0 1px 2px rgba(16,24,40,.05), 0 4px 16px rgba(16,24,40,.06);
  --bo-goc:14px;
}
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:"Segoe UI",Roboto,Arial,sans-serif;background:var(--nen);color:var(--chu);display:flex;min-height:100vh;font-size:14px}
.main-content{flex:1;min-width:0}
.content{padding:24px 28px 60px}

/* ---------- Thanh tab ---------- */
.tabs{display:flex;flex-wrap:wrap;gap:2px;margin-bottom:22px;background:#fff;border-radius:var(--bo-goc);
      padding:6px;border:1px solid var(--vien);box-shadow:var(--bong);position:sticky;top:0;z-index:40}
.tabs a{display:flex;align-items:center;gap:7px;padding:10px 16px;text-decoration:none;color:var(--xam);
        border-radius:10px;font-size:13.5px;font-weight:600;transition:.15s;white-space:nowrap}
.tabs a:hover{background:var(--nen);color:var(--chu)}
.tabs a.active{background:var(--xanh);color:#fff;box-shadow:0 2px 8px rgba(47,143,91,.3)}
.tabs a i{font-size:13px;opacity:.9}

/* ---------- Khối nội dung ---------- */
.panel{background:#fff;border:1px solid var(--vien);border-radius:var(--bo-goc);box-shadow:var(--bong);
       padding:24px;margin-bottom:20px}
.panel-head{display:flex;justify-content:space-between;align-items:flex-start;gap:16px;
            padding-bottom:16px;margin-bottom:18px;border-bottom:1px solid var(--vien);flex-wrap:wrap}
.panel-head h2{font-size:19px;font-weight:700;display:flex;align-items:center;gap:10px}
.panel-head h2 i{color:var(--xanh)}
.panel-head p{color:var(--xam);font-size:13px;margin-top:5px;max-width:640px;line-height:1.55}

/* ---------- Nút ---------- */
.btn{display:inline-flex;align-items:center;justify-content:center;gap:7px;padding:9px 16px;border:1px solid transparent;
     border-radius:9px;font-size:13.5px;font-weight:600;cursor:pointer;text-decoration:none;transition:.15s;
     background:var(--duong);color:#fff;font-family:inherit;white-space:nowrap}
.btn:hover{filter:brightness(.94)}
.btn.green{background:var(--xanh)}
.btn.red{background:var(--do)}
.btn.ghost{background:#fff;color:var(--xam);border-color:var(--vien)}
.btn.ghost:hover{background:var(--nen);color:var(--chu)}
.btn.sm{padding:6px 11px;font-size:12.5px;border-radius:7px}
.btn.icon{padding:6px 9px}
.btn-row{display:flex;gap:8px;justify-content:flex-end;align-items:center;margin-top:20px;
         padding-top:18px;border-top:1px solid var(--vien);flex-wrap:wrap}
.cell-btns{display:flex;gap:6px}

/* ---------- Bảng ---------- */
.table-wrap{overflow:auto;border:1px solid var(--vien);border-radius:11px}
table.tbl{width:100%;border-collapse:collapse;min-width:760px;background:#fff}
table.tbl th{background:#fafbfd;color:var(--xam);font-size:12px;font-weight:700;text-transform:uppercase;
             letter-spacing:.3px;padding:11px 14px;text-align:left;border-bottom:1px solid var(--vien);white-space:nowrap}
table.tbl td{padding:12px 14px;border-bottom:1px solid #f1f4f8;vertical-align:middle}
table.tbl tbody tr:last-child td{border-bottom:0}
table.tbl tbody tr:hover{background:#fbfcfe}
table.tbl td.num,table.tbl th.num{text-align:right;font-variant-numeric:tabular-nums}
.trong{text-align:center;color:var(--xam);padding:34px 14px}

/* ---------- Form ---------- */
.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(240px,1fr));gap:16px}
.grid.two{grid-template-columns:1fr 1fr}
.fld{display:flex;flex-direction:column}
.fld.wide{grid-column:1/-1}
.fld label{font-weight:600;font-size:12.5px;margin-bottom:6px;color:#41506a}
.fld label .sao{color:var(--do)}
.fld input,.fld select,.fld textarea{width:100%;padding:10px 12px;border:1px solid #d4dce8;border-radius:9px;
     font-size:13.5px;font-family:inherit;background:#fff;transition:.15s;color:var(--chu)}
.fld input:focus,.fld select:focus,.fld textarea:focus{outline:0;border-color:var(--duong);
     box-shadow:0 0 0 3px rgba(59,125,221,.12)}
.fld input[readonly]{background:#f5f7fa;color:var(--xam);cursor:default}
.fld textarea{min-height:78px;resize:vertical}
.fld select[multiple]{min-height:132px;padding:6px}
.fld .hint{font-size:12px;color:var(--xam);margin-top:5px;line-height:1.5}
.fld .hint b{color:#41506a}

/* ---------- Hộp gấp ---------- */
details.box{border:1px solid var(--vien);border-radius:12px;margin-bottom:20px;overflow:hidden;background:#fcfdff}
details.box>summary{list-style:none;cursor:pointer;padding:14px 18px;font-weight:600;color:var(--xanh-dam);
     display:flex;align-items:center;gap:9px;background:var(--xanh-nhat);font-size:13.5px}
details.box>summary::-webkit-details-marker{display:none}
details.box>summary::after{content:"\f078";font-family:"Font Awesome 6 Free";font-weight:900;
     margin-left:auto;font-size:11px;transition:.2s}
details.box[open]>summary::after{transform:rotate(180deg)}
details.box .box-body{padding:20px}

/* ---------- Nhãn trạng thái ---------- */
.tag{display:inline-flex;align-items:center;gap:5px;padding:4px 10px;border-radius:20px;font-size:12px;
     font-weight:600;white-space:nowrap}
.tag.ok{background:var(--xanh-nhat);color:var(--xanh-dam)}
.tag.info{background:var(--duong-nhat);color:#2c5fa8}
.tag.warn{background:var(--cam-nhat);color:#9a5a06}
.tag.bad{background:var(--do-nhat);color:#a5322f}
.tag.mute{background:#eef1f6;color:var(--xam)}

/* ---------- Thông báo ---------- */
.alert{display:flex;gap:12px;padding:14px 16px;border-radius:11px;margin-bottom:20px;line-height:1.55;font-size:13.5px}
.alert i{font-size:16px;margin-top:1px}
.alert.ok{background:var(--xanh-nhat);border:1px solid #bfe3cd;color:var(--xanh-dam)}
.alert.bad{background:var(--do-nhat);border:1px solid #f3c4c1;color:#a5322f}
.alert.note{background:var(--duong-nhat);border:1px solid #cddffa;color:#2c5fa8}

/* ---------- Thẻ số liệu ---------- */
.stats{display:grid;grid-template-columns:repeat(auto-fit,minmax(190px,1fr));gap:14px;margin-bottom:20px}
.stat{background:#fff;border:1px solid var(--vien);border-radius:12px;padding:16px 18px}
.stat span{display:block;font-size:12px;color:var(--xam);font-weight:600;text-transform:uppercase;letter-spacing:.3px}
.stat b{display:block;font-size:22px;margin-top:7px;font-weight:700}
.stat.g b{color:var(--xanh)}.stat.b b{color:var(--duong)}.stat.o b{color:var(--cam)}.stat.r b{color:var(--do)}

/* ---------- Bảng chọn vật tư ---------- */
.pick{border:1px solid var(--vien);border-radius:11px;overflow:hidden;margin-bottom:18px}
.pick-head{background:#fafbfd;padding:11px 15px;font-weight:700;font-size:13px;display:flex;
           align-items:center;gap:9px;border-bottom:1px solid var(--vien)}
.pick-head i{color:var(--xanh)}
.pick-head .dem{margin-left:auto;font-weight:600;color:var(--xam);font-size:12.5px}
.pick table{width:100%;border-collapse:collapse;min-width:640px}
.pick th{background:#fdfdfe;font-size:11.5px;color:var(--xam);text-transform:uppercase;letter-spacing:.3px;
         padding:9px 14px;text-align:left;border-bottom:1px solid var(--vien)}
.pick td{padding:9px 14px;border-bottom:1px solid #f3f5f9}
.pick tr:last-child td{border-bottom:0}
.pick tr.chon{background:var(--xanh-nhat)}
.pick input[type=number]{width:100%;max-width:150px;padding:7px 10px;border:1px solid #d4dce8;border-radius:7px;font-size:13px}
.pick input[type=checkbox]{width:17px;height:17px;cursor:pointer;accent-color:var(--xanh)}
.pick .ton{font-variant-numeric:tabular-nums;color:var(--xam);font-size:12.5px}
.pick-scroll{max-height:330px;overflow:auto}

/* ---------- Hộp thoại ---------- */
.modal{position:fixed;inset:0;background:rgba(17,24,39,.55);display:none;align-items:flex-start;
       justify-content:center;z-index:100;padding:36px 18px;overflow:auto}
.modal.mo{display:flex}
.modal-box{background:#fff;border-radius:16px;width:100%;max-width:760px;box-shadow:0 20px 60px rgba(0,0,0,.25)}
.modal-box.rong{max-width:940px}
.modal-head{display:flex;align-items:center;gap:11px;padding:18px 22px;border-bottom:1px solid var(--vien)}
.modal-head h3{font-size:17px;font-weight:700;flex:1}
.modal-head i.tieu-de{color:var(--duong)}
.dong-modal{background:none;border:0;font-size:22px;color:var(--xam);cursor:pointer;line-height:1;padding:0 4px}
.modal-body{padding:22px}

.mini{font-size:12.5px;color:var(--xam)}
.ellipsis{max-width:290px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;display:inline-block;vertical-align:bottom}
@media(max-width:900px){
  .content{padding:14px}
  .grid,.grid.two{grid-template-columns:1fr}
  .tabs{position:static}
}
</style>
</head>
<body>
<%@include file="/views/commons/sidebar.jsp"%>
<div class="main-content">
<%@include file="/views/commons/header.jsp"%>
<section class="content">

<!-- ============ THANH TAB ============ -->
<nav class="tabs">
  <a class="${empty param.tab || param.tab=='giong'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=giong"><i class="fas fa-seedling"></i>Giống cây</a>
  <a class="${param.tab=='vuon'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=vuon"><i class="fas fa-map-location-dot"></i>Thiết lập vườn</a>
  <a class="${param.tab=='lich'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=lich"><i class="fas fa-calendar-days"></i>Lịch &amp; nhắc việc</a>
  <a class="${param.tab=='nhatky'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=nhatky"><i class="fas fa-book"></i>Nhật ký chăm sóc</a>
  <a class="${param.tab=='sinhtruong'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=sinhtruong"><i class="fas fa-chart-line"></i>Sinh trưởng</a>
  <a class="${param.tab=='saubenh'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=saubenh"><i class="fas fa-bug"></i>Sâu bệnh</a>
  <a class="${param.tab=='thuhoach'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=thuhoach"><i class="fas fa-box-open"></i>Thu hoạch</a>
  <a class="${param.tab=='chiphi'?'active':''}" href="${pageContext.request.contextPath}/canhtac?tab=chiphi"><i class="fas fa-coins"></i>Chi phí &amp; tiêu hao</a>
</nav>

<c:if test="${not empty sessionScope.canhTacMessage}">
  <div class="alert ${sessionScope.canhTacLoi ? 'bad' : 'ok'}">
    <i class="fas ${sessionScope.canhTacLoi ? 'fa-circle-exclamation' : 'fa-circle-check'}"></i>
    <div>${sessionScope.canhTacMessage}</div>
  </div>
  <c:remove var="canhTacMessage" scope="session"/>
  <c:remove var="canhTacLoi" scope="session"/>
</c:if>

<!-- ==================================================================== -->
<!-- UC-4.1  GIỐNG CÂY                                                     -->
<!-- ==================================================================== -->
<div class="panel" data-tab="giong">
  <div class="panel-head">
    <div>
      <h2><i class="fas fa-seedling"></i>Danh mục giống sầu riêng</h2>
      <p>Trạng thái chỉ nhận hai giá trị cố định để thống kê không bị lệch do gõ sai chính tả.</p>
    </div>
    <button class="btn green" onclick="moThemGiong()"><i class="fas fa-plus"></i>Thêm giống</button>
  </div>

  <div class="table-wrap">
    <table class="tbl">
      <thead><tr>
        <th style="width:60px">ID</th><th>Tên giống</th><th>Đặc điểm</th>
        <th class="num" style="width:130px">Thời gian (tháng)</th>
        <th class="num" style="width:140px">Năng suất (kg/cây)</th>
        <th style="width:140px">Trạng thái</th><th style="width:130px">Thao tác</th>
      </tr></thead>
      <tbody>
      <c:forEach var="g" items="${listGiong}">
        <tr data-id="${g.id}" data-ten="${g.ten_giong}" data-dd="${g.dac_diem}"
            data-tg="${g.thoi_gian_sinh_truong_thu_hoach}" data-ns="${g.nang_suat_tham_khao}"
            data-tt="${g.trang_thai}">
          <td>${g.id}</td>
          <td><b>${g.ten_giong}</b></td>
          <td><span class="ellipsis mini">${g.dac_diem}</span></td>
          <td class="num">${g.thoi_gian_sinh_truong_thu_hoach}</td>
          <td class="num">${g.nang_suat_tham_khao}</td>
          <td><span class="tag ${g.trang_thai=='Đang canh tác'?'ok':'mute'}">${g.trang_thai}</span></td>
          <td>
            <div class="cell-btns">
              <button class="btn sm icon ghost" title="Sửa" onclick="moSuaGiong(this)"><i class="fas fa-pen"></i></button>
              <form method="post" action="${pageContext.request.contextPath}/canhtac" onsubmit="return confirm('Xóa giống ${g.ten_giong}?')">
                <input type="hidden" name="action" value="giong_delete">
                <input type="hidden" name="tab" value="giong">
                <input type="hidden" name="id" value="${g.id}">
                <button class="btn sm icon red" title="Xóa"><i class="fas fa-trash"></i></button>
              </form>
            </div>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty listGiong}"><tr><td colspan="7" class="trong">Chưa có giống nào.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>

<!-- ==================================================================== -->
<!-- UC-4.2  THIẾT LẬP VƯỜN                                                -->
<!-- ==================================================================== -->
<div class="panel" data-tab="vuon">
  <div class="panel-head">
    <div>
      <h2><i class="fas fa-map-location-dot"></i>Thiết lập vườn trồng</h2>
      <p>Diện tích lấy trực tiếp từ lô đất đã phân chia ở module Quản lý khu vực. Mật độ và phân loại được tính ngay khi bạn chọn lô và nhập số cây.</p>
    </div>
    <button class="btn green" onclick="moThemVuon()"><i class="fas fa-plus"></i>Thiết lập vườn</button>
  </div>

  <div class="table-wrap">
    <table class="tbl">
      <thead><tr>
        <th>Lô đất</th><th>Giống</th><th class="num">Diện tích (m²)</th><th class="num">Số cây</th>
        <th class="num">Mật độ (cây/ha)</th><th>Phân loại</th><th>Giai đoạn</th><th>Sâu bệnh</th><th style="width:130px">Thao tác</th>
      </tr></thead>
      <tbody>
      <c:forEach var="v" items="${listVuon}">
        <tr data-id="${v.id}" data-lo="${v.lo_dat_id}" data-giong="${v.giong_id}"
            data-cay="${v.so_luong_cay}" data-gc="${v.ghi_chu}" data-gd="${v.trang_thai_sinh_truong}">
          <td><b>${v.ten_lo_dat}</b></td>
          <td>${v.ten_giong}</td>
          <td class="num">${v.dien_tich}</td>
          <td class="num">${v.so_luong_cay}</td>
          <td class="num"><b>${v.mat_do_trong}</b></td>
          <td><span class="tag ${v.mat_do_bat_thuong?'bad':'ok'}">${v.phan_loai_mat_do}</span></td>
          <td><span class="tag info">${v.trang_thai_sinh_truong}</span></td>
          <td><c:choose><c:when test="${v.co_sau_benh}"><span class="tag bad"><i class="fas fa-bug"></i>Có</span></c:when>
              <c:otherwise><span class="tag mute">Không</span></c:otherwise></c:choose></td>
          <td>
            <div class="cell-btns">
              <button class="btn sm icon ghost" title="Sửa" onclick="moSuaVuon(this)"><i class="fas fa-pen"></i></button>
              <form method="post" action="${pageContext.request.contextPath}/canhtac" onsubmit="return confirm('Xóa thiết lập vườn của lô ${v.ten_lo_dat}?')">
                <input type="hidden" name="action" value="vuon_delete">
                <input type="hidden" name="tab" value="vuon">
                <input type="hidden" name="id" value="${v.id}">
                <button class="btn sm icon red" title="Xóa"><i class="fas fa-trash"></i></button>
              </form>
            </div>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty listVuon}"><tr><td colspan="9" class="trong">Chưa thiết lập vườn nào.</td></tr></c:if>
      </tbody>
    </table>
  </div>

  <details class="box" style="margin-top:20px">
    <summary><i class="fas fa-table-list"></i>Bảng tham chiếu mật độ trồng</summary>
    <div class="box-body">
      <div class="table-wrap">
        <table class="tbl">
          <thead><tr><th>Phân loại</th><th class="num">Từ (cây/ha)</th><th class="num">Đến (cây/ha)</th><th>Đặc điểm &amp; rủi ro</th></tr></thead>
          <tbody>
          <c:forEach var="m" items="${listMatDo}">
            <tr><td><b>${m.phan_loai}</b></td><td class="num">${m.mat_do_tu}</td>
                <td class="num"><c:choose><c:when test="${m.mat_do_den != null}">${m.mat_do_den}</c:when><c:otherwise>trở lên</c:otherwise></c:choose></td>
                <td class="mini">${m.dac_diem_rui_ro}</td></tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </details>
</div>

<!-- ==================================================================== -->
<!-- UC-4.3  LỊCH CHĂM SÓC & NHẮC VIỆC                                     -->
<!-- ==================================================================== -->
<div class="panel" data-tab="lich">
  <div class="panel-head">
    <div>
      <h2><i class="fas fa-calendar-days"></i>Lịch chăm sóc định kỳ</h2>
      <p>Mỗi lịch sẽ tự sinh nhắc việc cho từng lô theo chu kỳ. Khi ghi nhật ký chăm sóc, bạn chọn nhắc việc tương ứng để hệ thống đóng nó lại.</p>
    </div>
    <button class="btn green" onclick="moThemLich()"><i class="fas fa-plus"></i>Tạo lịch</button>
  </div>

  <div class="table-wrap">
    <table class="tbl">
      <thead><tr>
        <th style="width:60px">ID</th><th>Công việc</th><th>Lô áp dụng</th><th>Bắt đầu</th>
        <th>Chu kỳ</th><th>Kết thúc</th><th>Trạng thái</th><th style="width:130px">Thao tác</th>
      </tr></thead>
      <tbody>
      <c:forEach var="l" items="${listLich}">
        <tr data-id="${l.id}" data-cv="${l.loai_cong_viec}" data-bd="${l.ngay_bat_dau}"
            data-ck="${l.chu_ky_ngay}" data-kt="${l.ngay_ket_thuc}" data-tt="${l.trang_thai}"
            data-mt="${l.mo_ta}" data-lo="${l.danh_sach_lo_id}">
          <td>${l.id}</td>
          <td><b>${l.loai_cong_viec}</b></td>
          <td><span class="ellipsis mini">${l.danh_sach_lo_ten}</span></td>
          <td>${l.ngay_bat_dau}</td>
          <td><c:choose><c:when test="${l.chu_ky_ngay == null || l.chu_ky_ngay <= 1}"><span class="tag info">Hằng ngày</span></c:when>
              <c:otherwise>${l.chu_ky_ngay} ngày/lần</c:otherwise></c:choose></td>
          <td><c:choose><c:when test="${empty l.ngay_ket_thuc}"><span class="tag mute">Vĩnh viễn</span></c:when>
              <c:otherwise>${l.ngay_ket_thuc}</c:otherwise></c:choose></td>
          <td><span class="tag ${l.trang_thai=='Đang áp dụng'?'ok':'mute'}">${l.trang_thai}</span></td>
          <td>
            <div class="cell-btns">
              <button class="btn sm icon ghost" title="Sửa" onclick="moSuaLich(this)"><i class="fas fa-pen"></i></button>
              <form method="post" action="${pageContext.request.contextPath}/canhtac" onsubmit="return confirm('Xóa lịch này? Các nhắc việc chưa thực hiện cũng bị xóa.')">
                <input type="hidden" name="action" value="lich_delete">
                <input type="hidden" name="tab" value="lich">
                <input type="hidden" name="id" value="${l.id}">
                <button class="btn sm icon red" title="Xóa"><i class="fas fa-trash"></i></button>
              </form>
            </div>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty listLich}"><tr><td colspan="8" class="trong">Chưa có lịch chăm sóc nào.</td></tr></c:if>
      </tbody>
    </table>
  </div>

  <div class="panel-head" style="margin-top:26px;border-bottom:1px solid var(--vien)">
    <div>
      <h2 style="font-size:16px"><i class="fas fa-bell"></i>Nhắc việc phát sinh</h2>
      <p>Nhắc việc quá hạn là những công việc đã tới ngày mà chưa có nhật ký chăm sóc tương ứng.</p>
    </div>
  </div>
  <div class="table-wrap">
    <table class="tbl">
      <thead><tr><th>Ngày nhắc</th><th>Công việc</th><th>Lô đất</th><th>Trạng thái</th><th>Nhật ký</th></tr></thead>
      <tbody>
      <c:forEach var="nv" items="${listNhacViec}" begin="0" end="49">
        <tr>
          <td>${nv.ngay_nhac}</td>
          <td><b>${nv.loai_cong_viec}</b></td>
          <td>${nv.ten_lo_dat}</td>
          <td>
            <c:choose>
              <c:when test="${nv.nhat_ky_cham_soc_id != null}"><span class="tag ok"><i class="fas fa-check"></i>Đã thực hiện</span></c:when>
              <c:when test="${nv.qua_han}"><span class="tag bad"><i class="fas fa-triangle-exclamation"></i>Quá hạn</span></c:when>
              <c:otherwise><span class="tag info">Chờ xử lý</span></c:otherwise>
            </c:choose>
          </td>
          <td class="mini"><c:choose><c:when test="${nv.nhat_ky_cham_soc_id != null}">#${nv.nhat_ky_cham_soc_id}</c:when><c:otherwise>-</c:otherwise></c:choose></td>
        </tr>
      </c:forEach>
      <c:if test="${empty listNhacViec}"><tr><td colspan="5" class="trong">Chưa có nhắc việc. Hãy tạo lịch chăm sóc trước.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>

<!-- ==================================================================== -->
<!-- UC-4.4  NHẬT KÝ CHĂM SÓC                                              -->
<!-- ==================================================================== -->
<div class="panel" data-tab="nhatky">
  <div class="panel-head">
    <div>
      <h2><i class="fas fa-book"></i>Nhật ký chăm sóc</h2>
      <p>Chi phí và diện tích tiêu hao do hệ thống tính: vật tư xuất kho FIFO/FEFO theo đơn giá phiếu nhập, dụng cụ theo giá bình quân, thiết bị theo khấu hao ngày.</p>
    </div>
  </div>

  <details class="box" open>
    <summary><i class="fas fa-pen-to-square"></i>Ghi nhật ký mới</summary>
    <div class="box-body">
      <form method="post" action="${pageContext.request.contextPath}/canhtac" id="formNhatKy">
        <input type="hidden" name="action" value="nhatky_insert">
        <input type="hidden" name="tab" value="nhatky">

        <div class="grid">
          <div class="fld">
            <label>Nhắc việc <span class="mini">(nếu công việc này nằm trong lịch)</span></label>
            <select name="nhac_viec_id" id="nvChon" onchange="apDungNhacViec()">
              <option value="0" data-lo="" data-cv="" data-ngay="">-- Công việc phát sinh ngoài lịch --</option>
              <c:forEach var="nv" items="${nhacViecChoXuLy}" begin="0" end="99">
                <option value="${nv.id}" data-lo="${nv.lo_dat_id}" data-cv="${nv.loai_cong_viec}" data-ngay="${nv.ngay_nhac}">
                  ${nv.ngay_nhac} &middot; ${nv.loai_cong_viec} &middot; ${nv.ten_lo_dat}<c:if test="${nv.qua_han}"> (quá hạn)</c:if>
                </option>
              </c:forEach>
            </select>
            <div class="hint">Chọn nhắc việc sẽ tự điền lô đất, công việc và ngày; nhắc việc đó được đóng lại sau khi lưu.</div>
          </div>
          <div class="fld">
            <label>Lô đất <span class="sao">*</span></label>
            <select name="lo_dat_id" id="nkLo" required>
              <option value="">-- Chọn lô --</option>
              <c:forEach var="lo" items="${listLoDat}"><option value="${lo.id}">${lo.ten_don_vi}</option></c:forEach>
            </select>
          </div>
          <div class="fld">
            <label>Loại công việc <span class="sao">*</span></label>
            <input name="loai_cong_viec" id="nkCV" required placeholder="Bón phân, tỉa cành, tưới nước...">
          </div>
          <div class="fld">
            <label>Ngày thực hiện <span class="sao">*</span></label>
            <input name="ngay_thuc_hien" id="nkNgay" type="date" required>
          </div>
          <div class="fld wide">
            <label>Mô tả công việc</label>
            <textarea name="mo_ta" placeholder="Ghi rõ nội dung đã làm, tình trạng quan sát được..."></textarea>
          </div>
        </div>

        <!-- Vật tư -->
        <div class="pick" style="margin-top:20px">
          <div class="pick-head"><i class="fas fa-flask"></i>Vật tư sử dụng
            <span class="dem">Chỉ hiện vật tư còn tồn khả dụng và còn hạn sử dụng</span></div>
          <div class="pick-scroll">
            <table>
              <thead><tr><th style="width:56px">Chọn</th><th>Vật tư</th><th>Loại</th>
                         <th class="num" style="width:150px">Tồn khả dụng</th>
                         <th style="width:170px">Số lượng dùng</th></tr></thead>
              <tbody>
              <c:forEach var="vt" items="${listVatTu}">
                <tr>
                  <td><input type="checkbox" name="vt_id" value="${vt.id}" class="ck" data-o="vt_qty_${vt.id}"></td>
                  <td><b>${vt.ten}</b><c:if test="${not empty vt.han_su_dung_gan_nhat}">
                      <div class="mini">HSD gần nhất: ${vt.han_su_dung_gan_nhat}</div></c:if></td>
                  <td><span class="tag mute">${vt.ten_loai}</span></td>
                  <td class="num ton">${vt.ton_kha_dung} ${vt.don_vi_tinh}</td>
                  <td><input name="vt_qty_${vt.id}" id="vt_qty_${vt.id}" type="number" step="0.01" min="0"
                             max="${vt.ton_kha_dung}" value="0" class="sl" data-ck="${vt.id}" data-nhom="vt"
                             data-max="${vt.ton_kha_dung}" data-ten="${vt.ten}"></td>
                </tr>
              </c:forEach>
              <c:if test="${empty listVatTu}"><tr><td colspan="5" class="trong">Kho không còn vật tư khả dụng.</td></tr></c:if>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Dụng cụ -->
        <div class="pick">
          <div class="pick-head"><i class="fas fa-screwdriver-wrench"></i>Dụng cụ tiêu hao</div>
          <div class="pick-scroll">
            <table>
              <thead><tr><th style="width:56px">Chọn</th><th>Dụng cụ</th><th class="num" style="width:150px">Tồn kho</th>
                         <th style="width:170px">Số lượng dùng</th></tr></thead>
              <tbody>
              <c:forEach var="dc" items="${listDungCu}">
                <tr>
                  <td><input type="checkbox" name="dc_id" value="${dc.id}" class="ck" data-o="dc_qty_${dc.id}"></td>
                  <td><b>${dc.ten}</b></td>
                  <td class="num ton">${dc.ton_kha_dung} ${dc.don_vi_tinh}</td>
                  <td><input name="dc_qty_${dc.id}" id="dc_qty_${dc.id}" type="number" step="0.01" min="0"
                             max="${dc.ton_kha_dung}" value="0" class="sl" data-ck="${dc.id}" data-nhom="dc"
                             data-max="${dc.ton_kha_dung}" data-ten="${dc.ten}"></td>
                </tr>
              </c:forEach>
              <c:if test="${empty listDungCu}"><tr><td colspan="4" class="trong">Kho không còn dụng cụ.</td></tr></c:if>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Thiết bị -->
        <div class="pick">
          <div class="pick-head"><i class="fas fa-tractor"></i>Thiết bị điều động
            <span class="dem">Thiết bị đang bảo trì hoặc hỏng không xuất hiện ở đây</span></div>
          <div class="pick-scroll">
            <table>
              <thead><tr><th style="width:56px">Chọn</th><th>Thiết bị</th><th>Trạng thái</th>
                         <th class="num" style="width:150px">Khấu hao/ngày</th>
                         <th style="width:170px">Số ngày sử dụng</th></tr></thead>
              <tbody>
              <c:forEach var="tb" items="${listThietBi}">
                <tr>
                  <td><input type="checkbox" name="tb_id" value="${tb.id}" class="ck" data-o="tb_ngay_${tb.id}"></td>
                  <td><b>${tb.ten}</b></td>
                  <td><span class="tag mute">${tb.ten_loai}</span></td>
                  <td class="num ton">${tb.don_gia_gan_nhat} đ</td>
                  <td><input name="tb_ngay_${tb.id}" id="tb_ngay_${tb.id}" type="number" min="0" max="365"
                             value="0" class="sl" data-ck="${tb.id}" data-nhom="tb" data-max="365" data-ten="${tb.ten}"></td>
                </tr>
              </c:forEach>
              <c:if test="${empty listThietBi}"><tr><td colspan="5" class="trong">Không có thiết bị khả dụng.</td></tr></c:if>
              </tbody>
            </table>
          </div>
        </div>

        <div class="btn-row">
          <span class="mini" style="margin-right:auto">Nhập số lượng lớn hơn 0 sẽ tự tích chọn dòng tương ứng.</span>
          <button type="reset" class="btn ghost">Nhập lại</button>
          <button class="btn green"><i class="fas fa-floppy-disk"></i>Lưu nhật ký</button>
        </div>
      </form>
    </div>
  </details>

  <div class="table-wrap">
    <table class="tbl">
      <thead><tr>
        <th style="width:60px">ID</th><th>Lô</th><th>Công việc</th><th>Ngày</th>
        <th class="num">CP vật tư</th><th class="num">CP dụng cụ</th><th class="num">CP thiết bị</th>
        <th class="num">Tổng chi phí</th><th style="width:80px">Xóa</th>
      </tr></thead>
      <tbody>
      <c:forEach var="n" items="${listNhatKy}">
        <tr>
          <td>${n.id}</td><td><b>${n.ten_lo_dat}</b></td><td>${n.loai_cong_viec}</td><td>${n.ngay_thuc_hien}</td>
          <td class="num">${n.tong_chi_phi_vat_tu}</td>
          <td class="num">${n.tong_chi_phi_dung_cu}</td>
          <td class="num">${n.tong_chi_phi_thiet_bi}</td>
          <td class="num"><b>${n.tong_chi_phi}</b></td>
          <td>
            <form method="post" action="${pageContext.request.contextPath}/canhtac" onsubmit="return confirm('Xóa nhật ký #${n.id}? Tồn kho, diện tích và thiết bị sẽ được hoàn tác.')">
              <input type="hidden" name="action" value="nhatky_delete">
              <input type="hidden" name="tab" value="nhatky">
              <input type="hidden" name="id" value="${n.id}">
              <button class="btn sm icon red" title="Xóa"><i class="fas fa-trash"></i></button>
            </form>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty listNhatKy}"><tr><td colspan="9" class="trong">Chưa có nhật ký nào.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>

<!-- ==================================================================== -->
<!-- UC-4.5  SINH TRƯỞNG                                                   -->
<!-- ==================================================================== -->
<div class="panel" data-tab="sinhtruong">
  <div class="panel-head">
    <div>
      <h2><i class="fas fa-chart-line"></i>Theo dõi sinh trưởng</h2>
      <p>Mỗi lô chỉ có một bản ghi theo dõi. Cập nhật lần sau sẽ sửa trực tiếp bản ghi đó thay vì tạo dòng mới.</p>
    </div>
    <button class="btn green" onclick="moSinhTruong(null)"><i class="fas fa-plus"></i>Cập nhật lô</button>
  </div>

  <div class="table-wrap">
    <table class="tbl">
      <thead><tr>
        <th>Lô đất</th><th>Giống</th><th>Giai đoạn hiện tại</th><th>Tỷ lệ giai đoạn</th>
        <th class="num">Số cây còn lại</th><th class="num">Đã giảm</th><th>Cập nhật lần cuối</th><th style="width:90px">Sửa</th>
      </tr></thead>
      <tbody>
      <c:forEach var="s" items="${listSinhTruong}">
        <tr data-vuon="${s.vuon_trong_id}" data-gd="${s.trang_thai_hien_tai}" data-tl="${s.ty_le_giai_doan}"
            data-lcn="${s.loai_cap_nhat}" data-gc="${s.ghi_chu}">
          <td><b>${s.ten_lo_dat}</b></td>
          <td>${s.ten_giong}</td>
          <td><span class="tag info">${s.trang_thai_hien_tai}</span></td>
          <td><c:choose><c:when test="${empty s.ty_le_giai_doan}"><span class="mini">Đồng đều</span></c:when>
              <c:otherwise><span class="mini">${s.ty_le_giai_doan}</span></c:otherwise></c:choose></td>
          <td class="num">${s.so_cay_con_lai}</td>
          <td class="num">${s.so_luong_cay_giam}</td>
          <td>${s.ngay_cap_nhat}</td>
          <td><button class="btn sm icon ghost" title="Sửa" onclick="moSinhTruong(this)"><i class="fas fa-pen"></i></button></td>
        </tr>
      </c:forEach>
      <c:if test="${empty listSinhTruong}"><tr><td colspan="8" class="trong">Chưa có lô nào được theo dõi.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>

<!-- ==================================================================== -->
<!-- UC-4.6  SÂU BỆNH                                                      -->
<!-- ==================================================================== -->
<div class="panel" data-tab="saubenh">
  <div class="panel-head">
    <div>
      <h2><i class="fas fa-bug"></i>Theo dõi sâu bệnh</h2>
      <p>Ghi nhận phát hiện chưa động vào kho. Khi bạn chuyển trạng thái sang <b>Đã xử lý</b> và khai báo thuốc, hệ thống mới trừ kho, tính tiền và diện tích giải phóng.</p>
    </div>
    <button class="btn green" onclick="moThemSauBenh()"><i class="fas fa-plus"></i>Ghi nhận mới</button>
  </div>

  <div class="table-wrap">
    <table class="tbl">
      <thead><tr>
        <th style="width:60px">ID</th><th>Lô</th><th>Sâu bệnh</th><th>Mức độ</th><th>Ngày phát hiện</th>
        <th>Biện pháp</th><th>Trạng thái</th><th style="width:130px">Thao tác</th>
      </tr></thead>
      <tbody>
      <c:forEach var="s" items="${listSauBenh}">
        <tr data-id="${s.id}" data-vuon="${s.vuon_trong_id}" data-ten="${s.ten_sau_benh}"
            data-md="${s.muc_do_nghiem_trong}" data-ngay="${s.ngay_phat_hien}"
            data-bp="${s.bien_phap_xu_ly}" data-tt="${s.trang_thai}"
            data-nk="${s.nhat_ky_cham_soc_id}">
          <td>${s.id}</td>
          <td><b>${s.ten_lo_dat}</b></td>
          <td>${s.ten_sau_benh}</td>
          <td><span class="tag ${s.muc_do_nghiem_trong=='Nặng'?'bad':(s.muc_do_nghiem_trong=='Trung bình'?'warn':'mute')}">${s.muc_do_nghiem_trong}</span></td>
          <td>${s.ngay_phat_hien}</td>
          <td><span class="ellipsis mini">${s.bien_phap_xu_ly}</span></td>
          <td><span class="tag ${s.trang_thai=='Đã xử lý'?'ok':(s.trang_thai=='Đang xử lý'?'warn':'bad')}">${s.trang_thai}</span></td>
          <td>
            <div class="cell-btns">
              <button class="btn sm icon ghost" title="Cập nhật" onclick="moSuaSauBenh(this)"><i class="fas fa-pen"></i></button>
              <form method="post" action="${pageContext.request.contextPath}/canhtac" onsubmit="return confirm('Xóa ghi nhận này? Thuốc đã dùng (nếu có) sẽ được hoàn về kho.')">
                <input type="hidden" name="action" value="saubenh_delete">
                <input type="hidden" name="tab" value="saubenh">
                <input type="hidden" name="id" value="${s.id}">
                <button class="btn sm icon red" title="Xóa"><i class="fas fa-trash"></i></button>
              </form>
            </div>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty listSauBenh}"><tr><td colspan="8" class="trong">Chưa có ghi nhận sâu bệnh nào.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>

<!-- ==================================================================== -->
<!-- UC-4.7  THU HOẠCH                                                     -->
<!-- ==================================================================== -->
<div class="panel" data-tab="thuhoach">
  <div class="panel-head">
    <div>
      <h2><i class="fas fa-box-open"></i>Ghi nhận thu hoạch</h2>
      <p>Nông sản nhập kho sẽ chiếm diện tích của ô chứa được chọn. Hệ thống kiểm tra ô chứa còn đủ chỗ trước khi lưu.</p>
    </div>
  </div>

  <details class="box" open>
    <summary><i class="fas fa-pen-to-square"></i>Phiếu thu hoạch mới</summary>
    <div class="box-body">
      <form method="post" action="${pageContext.request.contextPath}/canhtac">
        <input type="hidden" name="action" value="thuhoach_insert">
        <input type="hidden" name="tab" value="thuhoach">
        <div class="grid">
          <div class="fld"><label>Vườn/lô <span class="sao">*</span></label>
            <select name="vuon_trong_id" required><option value="">-- Chọn --</option>
              <c:forEach var="v" items="${listVuon}"><option value="${v.id}">${v.ten_lo_dat} - ${v.ten_giong}</option></c:forEach>
            </select></div>
          <div class="fld"><label>Vụ mùa <span class="sao">*</span></label><input name="ten_vu_mua" required placeholder="Vụ chính 2026"></div>
          <div class="fld"><label>Ngày thu hoạch <span class="sao">*</span></label><input name="ngay_thu_hoach" type="date" required></div>
          <div class="fld"><label>Tổng sản lượng (kg) <span class="sao">*</span></label><input name="tong_san_luong_kg" type="number" step="0.01" min="0" required></div>
          <div class="fld"><label>Vị trí lưu trữ <span class="sao">*</span></label>
            <select name="vi_tri_luu_tru_id" required><option value="">-- Chọn ô chứa --</option>
              <c:forEach var="o" items="${listOChua}"><option value="${o.id}">${o.ten_don_vi} - ${o.dien_tich} m² (${o.trang_thai})</option></c:forEach>
            </select>
            <div class="hint">Lấy từ các đơn vị loại <b>Ô chứa</b> trong Khu thu hoạch.</div></div>
          <div class="fld"><label>Diện tích chiếm dụng (m²)</label>
            <input name="tong_dien_tich_chiem_dung" type="number" step="0.01" min="0" value="0">
            <div class="hint">Để <b>0</b> nếu đã khai báo ở bảng phân loại bên dưới; hệ thống sẽ tự cộng.</div></div>
          <div class="fld"><label>Trạng thái kho</label>
            <select name="trang_thai_luu_kho"><option>Đã nhập kho</option><option>Chờ nhập kho</option></select></div>
          <div class="fld"><label>Hoàn tất vụ thu hoạch</label>
            <select name="hoan_tat"><option value="false">Chưa - còn thu tiếp</option><option value="true">Đã thu hoạch xong</option></select></div>
          <div class="fld wide"><label>Ghi chú</label><textarea name="ghi_chu"></textarea></div>
        </div>

        <div class="pick" style="margin-top:20px">
          <div class="pick-head"><i class="fas fa-layer-group"></i>Phân loại chất lượng
            <span class="dem"><button type="button" class="btn sm ghost" onclick="themDongPhanLoai()"><i class="fas fa-plus"></i>Thêm dòng</button></span></div>
          <table id="tblPhanLoai">
            <thead><tr><th>Xếp loại</th><th style="width:190px">Sản lượng (kg)</th>
                       <th style="width:190px">Diện tích chiếm (m²)</th><th style="width:80px"></th></tr></thead>
            <tbody>
              <tr>
                <td><input name="pl_loai" placeholder="Loại A" style="width:100%;padding:7px 10px;border:1px solid #d4dce8;border-radius:7px"></td>
                <td><input name="pl_kg" type="number" step="0.01" min="0" value="0"></td>
                <td><input name="pl_dt" type="number" step="0.01" min="0" value="0"></td>
                <td><button type="button" class="btn sm icon red" onclick="xoaDongPhanLoai(this)"><i class="fas fa-xmark"></i></button></td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="btn-row">
          <button type="reset" class="btn ghost">Nhập lại</button>
          <button class="btn green"><i class="fas fa-floppy-disk"></i>Lưu thu hoạch</button>
        </div>
      </form>
    </div>
  </details>

  <div class="table-wrap">
    <table class="tbl">
      <thead><tr><th style="width:60px">ID</th><th>Lô</th><th>Giống</th><th>Vụ mùa</th><th>Ngày</th>
                 <th class="num">Sản lượng (kg)</th><th class="num">DT chiếm kho (m²)</th><th>Trạng thái</th><th style="width:80px">Xóa</th></tr></thead>
      <tbody>
      <c:forEach var="t" items="${listThuHoach}">
        <tr>
          <td>${t.id}</td><td><b>${t.ten_lo_dat}</b></td><td>${t.ten_giong}</td><td>${t.ten_vu_mua}</td>
          <td>${t.ngay_thu_hoach}</td><td class="num">${t.tong_san_luong_kg}</td>
          <td class="num">${t.tong_dien_tich_chiem_dung}</td>
          <td><span class="tag ${t.trang_thai_luu_kho=='Đã nhập kho'?'ok':'warn'}">${t.trang_thai_luu_kho}</span></td>
          <td>
            <form method="post" action="${pageContext.request.contextPath}/canhtac" onsubmit="return confirm('Xóa phiếu thu hoạch? Diện tích kho sẽ được trả lại.')">
              <input type="hidden" name="action" value="thuhoach_delete">
              <input type="hidden" name="tab" value="thuhoach">
              <input type="hidden" name="id" value="${t.id}">
              <button class="btn sm icon red" title="Xóa"><i class="fas fa-trash"></i></button>
            </form>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty listThuHoach}"><tr><td colspan="9" class="trong">Chưa có phiếu thu hoạch nào.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>

<!-- ==================================================================== -->
<!-- CHI PHÍ & TIÊU HAO                                                    -->
<!-- ==================================================================== -->
<div class="panel" data-tab="chiphi">
  <div class="panel-head">
    <div>
      <h2><i class="fas fa-coins"></i>Chi phí &amp; tiêu hao theo lô</h2>
      <p>Số liệu do hệ thống tổng hợp từ nhật ký chăm sóc và phiếu thu hoạch. <b>DT kho giải phóng</b> là diện tích kho được trả lại khi vật tư/dụng cụ mang ra lô sử dụng.</p>
    </div>
  </div>

  <c:set var="demLo" value="0"/>
  <c:forEach var="c" items="${listChiPhi}"><c:set var="demLo" value="${demLo+1}"/></c:forEach>
  <div class="stats">
    <div class="stat g"><span>Tổng chi phí canh tác</span><b>${tongChiPhiCanhTac} đ</b></div>
    <div class="stat b"><span>Số lô đang theo dõi</span><b>${demLo}</b></div>
  </div>

  <div class="table-wrap">
    <table class="tbl" style="min-width:1400px">
      <thead><tr>
        <th>Lô đất</th><th>Giống</th><th class="num">DT lô (m²)</th><th class="num">Số lần CS</th>
        <th class="num">CP vật tư</th><th class="num">CP dụng cụ</th><th class="num">CP thiết bị</th>
        <th class="num">Tổng chi phí</th><th class="num">CP/m²</th>
        <th class="num">DT kho giải phóng</th><th>Thiết bị (lượt/ngày)</th>
        <th class="num">Sản lượng (kg)</th><th class="num">DT lưu kho (m²)</th><th class="num">Giá thành/kg</th>
      </tr></thead>
      <tbody>
      <c:forEach var="c" items="${listChiPhi}">
        <tr>
          <td><b>${c.ten_lo_dat}</b></td><td>${c.ten_giong}</td>
          <td class="num">${c.dien_tich_lo}</td><td class="num">${c.so_lan_cham_soc}</td>
          <td class="num">${c.chi_phi_vat_tu}</td><td class="num">${c.chi_phi_dung_cu}</td>
          <td class="num">${c.chi_phi_thiet_bi}</td>
          <td class="num"><b>${c.tong_chi_phi}</b></td><td class="num">${c.chi_phi_tren_m2}</td>
          <td class="num">${c.tong_dien_tich_giai_phong}</td>
          <td>${c.so_luot_thiet_bi} / ${c.so_ngay_thiet_bi}</td>
          <td class="num">${c.san_luong_kg}</td><td class="num">${c.dien_tich_luu_kho}</td>
          <td class="num">${c.gia_thanh_tren_kg}</td>
        </tr>
      </c:forEach>
      <c:if test="${empty listChiPhi}"><tr><td colspan="14" class="trong">Chưa có lô đất nào. Hãy phân chia lô đất ở module Quản lý trang trại trước.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>

</section></div>

<!-- ==================================================================== -->
<!-- CÁC HỘP THOẠI                                                         -->
<!-- ==================================================================== -->

<!-- Giống -->
<div class="modal" id="mdGiong">
  <div class="modal-box">
    <div class="modal-head"><i class="fas fa-seedling tieu-de"></i><h3 id="mdGiongTitle">Thêm giống</h3>
      <button class="dong-modal" onclick="dongModal('mdGiong')">&times;</button></div>
    <form method="post" action="${pageContext.request.contextPath}/canhtac">
      <div class="modal-body">
        <input type="hidden" name="action" id="gAction" value="giong_insert">
        <input type="hidden" name="tab" value="giong">
        <input type="hidden" name="id" id="gId">
        <div class="grid two">
          <div class="fld"><label>Tên giống <span class="sao">*</span></label><input name="ten_giong" id="gTen" required></div>
          <div class="fld"><label>Trạng thái <span class="sao">*</span></label>
            <select name="trang_thai" id="gTT" required>
              <c:forEach var="t" items="${trangThaiGiong}"><option value="${t}">${t}</option></c:forEach>
            </select></div>
          <div class="fld"><label>Thời gian sinh trưởng đến thu hoạch (tháng)</label>
            <input name="thoi_gian_sinh_truong_thu_hoach" id="gTG" type="number" min="0" value="0"></div>
          <div class="fld"><label>Năng suất tham khảo (kg/cây)</label>
            <input name="nang_suat_tham_khao" id="gNS" type="number" step="0.01" min="0" value="0"></div>
          <div class="fld wide"><label>Đặc điểm</label><textarea name="dac_diem" id="gDD"></textarea></div>
        </div>
        <div class="btn-row">
          <button type="button" class="btn ghost" onclick="dongModal('mdGiong')">Hủy</button>
          <button class="btn green"><i class="fas fa-floppy-disk"></i>Lưu</button>
        </div>
      </div>
    </form>
  </div>
</div>

<!-- Vườn -->
<div class="modal" id="mdVuon">
  <div class="modal-box">
    <div class="modal-head"><i class="fas fa-map-location-dot tieu-de"></i><h3 id="mdVuonTitle">Thiết lập vườn</h3>
      <button class="dong-modal" onclick="dongModal('mdVuon')">&times;</button></div>
    <form method="post" action="${pageContext.request.contextPath}/canhtac">
      <div class="modal-body">
        <input type="hidden" name="action" id="vAction" value="vuon_insert">
        <input type="hidden" name="tab" value="vuon">
        <input type="hidden" name="id" id="vId">
        <div class="grid two">
          <div class="fld"><label>Lô đất <span class="sao">*</span></label>
            <select name="lo_dat_id" id="vLo" required onchange="tinhMatDo()">
              <option value="" data-dt="0">-- Chọn lô đất --</option>
              <c:forEach var="lo" items="${listLoDat}">
                <option value="${lo.id}" data-dt="${lo.dien_tich}">${lo.ten_don_vi} - ${lo.dien_tich} m²</option>
              </c:forEach>
            </select></div>
          <div class="fld"><label>Giống <span class="sao">*</span></label>
            <select name="giong_id" id="vGiong" required><option value="">-- Chọn giống --</option>
              <c:forEach var="g" items="${listGiong}"><option value="${g.id}">${g.ten_giong}</option></c:forEach>
            </select></div>
          <div class="fld"><label>Số lượng cây <span class="sao">*</span></label>
            <input name="so_luong_cay" id="vCay" type="number" min="1" required oninput="tinhMatDo()"></div>
          <div class="fld"><label>Ngày trồng</label><input name="ngay_trong" id="vNgay" type="date"></div>
          <div class="fld"><label>Diện tích lô (m²)</label><input id="vDT" readonly value="0">
            <div class="hint">Lấy tự động từ lô đất, không nhập tại đây.</div></div>
          <div class="fld"><label>Mật độ trồng (cây/ha)</label><input id="vMD" readonly value="0">
            <div class="hint">Công thức: số cây ÷ (diện tích m² ÷ 10.000).</div></div>
          <div class="fld wide"><label>Phân loại mật độ</label>
            <div id="vPL" style="padding:11px 13px;border-radius:9px;background:#f5f7fa;font-size:13px">
              Chọn lô đất và nhập số cây để xem phân loại.</div></div>
          <div class="fld"><label>Giai đoạn sinh trưởng</label>
            <select name="trang_thai_sinh_truong" id="vGD">
              <c:forEach var="gd" items="${giaiDoan}"><option value="${gd}">${gd}</option></c:forEach>
            </select></div>
          <div class="fld wide"><label>Ghi chú</label><textarea name="ghi_chu" id="vGC"></textarea></div>
        </div>
        <div class="btn-row">
          <button type="button" class="btn ghost" onclick="dongModal('mdVuon')">Hủy</button>
          <button class="btn green"><i class="fas fa-floppy-disk"></i>Lưu</button>
        </div>
      </div>
    </form>
  </div>
</div>

<!-- Lịch -->
<div class="modal" id="mdLich">
  <div class="modal-box">
    <div class="modal-head"><i class="fas fa-calendar-days tieu-de"></i><h3 id="mdLichTitle">Tạo lịch chăm sóc</h3>
      <button class="dong-modal" onclick="dongModal('mdLich')">&times;</button></div>
    <form method="post" action="${pageContext.request.contextPath}/canhtac">
      <div class="modal-body">
        <input type="hidden" name="action" id="lAction" value="lich_insert">
        <input type="hidden" name="tab" value="lich">
        <input type="hidden" name="id" id="lId">
        <div class="grid two">
          <div class="fld"><label>Loại công việc <span class="sao">*</span></label>
            <input name="loai_cong_viec" id="lCV" required placeholder="Bón phân định kỳ"></div>
          <div class="fld"><label>Trạng thái</label>
            <select name="trang_thai" id="lTT"><option>Đang áp dụng</option><option>Tạm dừng</option><option>Hoàn thành</option></select></div>
          <div class="fld"><label>Ngày bắt đầu <span class="sao">*</span></label><input name="ngay_bat_dau" id="lBD" type="date" required></div>
          <div class="fld"><label>Chu kỳ lặp (ngày)</label>
            <input name="chu_ky_ngay" id="lCK" type="number" min="0" value="0">
            <div class="hint">Nhập <b>0</b> nghĩa là lặp <b>hằng ngày</b>. Nhập 7 nghĩa là 7 ngày một lần.</div></div>
          <div class="fld"><label>Ngày kết thúc</label><input name="ngay_ket_thuc" id="lKT" type="date">
            <div class="hint">Để <b>trống</b> nghĩa là lịch <b>vĩnh viễn</b>. Hệ thống sinh nhắc việc trước 90 ngày và tự sinh bù mỗi lần mở màn hình.</div></div>
          <div class="fld wide"><label>Áp dụng cho lô <span class="sao">*</span></label>
            <select name="lo_ids" id="lLo" multiple required>
              <c:forEach var="lo" items="${listLoDat}"><option value="${lo.id}">${lo.ten_don_vi}</option></c:forEach>
            </select>
            <div class="hint">Giữ <b>Ctrl</b> (hoặc <b>Cmd</b>) để chọn nhiều lô. Mỗi lô sẽ có bộ nhắc việc riêng.</div></div>
          <div class="fld wide"><label>Mô tả</label><textarea name="mo_ta" id="lMT"></textarea></div>
        </div>
        <div class="btn-row">
          <button type="button" class="btn ghost" onclick="dongModal('mdLich')">Hủy</button>
          <button class="btn green"><i class="fas fa-floppy-disk"></i>Lưu lịch</button>
        </div>
      </div>
    </form>
  </div>
</div>

<!-- Sinh trưởng -->
<div class="modal" id="mdST">
  <div class="modal-box">
    <div class="modal-head"><i class="fas fa-chart-line tieu-de"></i><h3 id="mdSTTitle">Cập nhật sinh trưởng</h3>
      <button class="dong-modal" onclick="dongModal('mdST')">&times;</button></div>
    <form method="post" action="${pageContext.request.contextPath}/canhtac">
      <div class="modal-body">
        <input type="hidden" name="action" value="sinhtruong_luu">
        <input type="hidden" name="tab" value="sinhtruong">
        <div class="grid two">
          <div class="fld"><label>Vườn/lô <span class="sao">*</span></label>
            <select name="vuon_trong_id" id="stVuon" required><option value="">-- Chọn --</option>
              <c:forEach var="v" items="${listVuon}"><option value="${v.id}">${v.ten_lo_dat} - ${v.ten_giong}</option></c:forEach>
            </select>
            <div class="hint">Mỗi lô chỉ có một bản ghi; chọn lại lô đã có sẽ cập nhật bản ghi đó.</div></div>
          <div class="fld"><label>Giai đoạn chính</label>
            <select name="giai_doan_moi" id="stGD"><option value="">-- Không đổi --</option>
              <c:forEach var="gd" items="${giaiDoan}"><option value="${gd}">${gd}</option></c:forEach>
            </select></div>
          <div class="fld"><label>Loại cập nhật</label>
            <select name="loai_cap_nhat" id="stLCN">
              <option>Chuyển giai đoạn</option><option>Tỷ lệ giai đoạn</option><option>Giảm số cây</option></select></div>
          <div class="fld"><label>Số cây giảm trong đợt này</label>
            <input name="so_luong_cay_giam" id="stGiam" type="number" min="0" value="0">
            <div class="hint">Cây chết hoặc bị loại bỏ. Mật độ sẽ được tính lại.</div></div>
          <div class="fld wide"><label>Ghi chú</label><textarea name="ghi_chu" id="stGC"></textarea></div>
        </div>

        <div class="pick" style="margin-top:18px">
          <div class="pick-head"><i class="fas fa-percent"></i>Tỷ lệ giai đoạn (lô không đồng đều)
            <span class="dem"><button type="button" class="btn sm ghost" onclick="themDongGiaiDoan()"><i class="fas fa-plus"></i>Thêm dòng</button></span></div>
          <table id="tblGiaiDoan">
            <thead><tr><th>Giai đoạn</th><th style="width:190px">Tỷ lệ (%)</th><th style="width:80px"></th></tr></thead>
            <tbody></tbody>
          </table>
        </div>
        <div class="mini" style="margin-top:8px">Tổng các tỷ lệ không được vượt 100%. Bỏ trống nếu lô phát triển đồng đều.</div>

        <div class="btn-row">
          <button type="button" class="btn ghost" onclick="dongModal('mdST')">Hủy</button>
          <button class="btn green"><i class="fas fa-floppy-disk"></i>Lưu theo dõi</button>
        </div>
      </div>
    </form>
  </div>
</div>

<!-- Sâu bệnh -->
<div class="modal" id="mdSB">
  <div class="modal-box rong">
    <div class="modal-head"><i class="fas fa-bug tieu-de"></i><h3 id="mdSBTitle">Ghi nhận sâu bệnh</h3>
      <button class="dong-modal" onclick="dongModal('mdSB')">&times;</button></div>
    <form method="post" action="${pageContext.request.contextPath}/canhtac">
      <div class="modal-body">
        <input type="hidden" name="action" id="sbAction" value="saubenh_insert">
        <input type="hidden" name="tab" value="saubenh">
        <input type="hidden" name="id" id="sbId">
        <div class="grid two">
          <div class="fld"><label>Lô có sâu bệnh <span class="sao">*</span></label>
            <select name="vuon_trong_id" id="sbVuon" required><option value="">-- Chọn --</option>
              <c:forEach var="v" items="${listVuon}"><option value="${v.id}">${v.ten_lo_dat} - ${v.ten_giong}</option></c:forEach>
            </select></div>
          <div class="fld"><label>Tên sâu bệnh <span class="sao">*</span></label>
            <input name="ten_sau_benh" id="sbTen" required placeholder="Nấm Phytophthora, rệp sáp..."></div>
          <div class="fld"><label>Mức độ nghiêm trọng</label>
            <select name="muc_do_nghiem_trong" id="sbMD">
              <c:forEach var="md" items="${mucDoSauBenh}"><option value="${md}">${md}</option></c:forEach>
            </select></div>
          <div class="fld"><label>Ngày phát hiện</label><input name="ngay_phat_hien" id="sbNgay" type="date"></div>
          <div class="fld"><label>Trạng thái xử lý <span class="sao">*</span></label>
            <select name="trang_thai" id="sbTT" onchange="doiTrangThaiSauBenh()">
              <c:forEach var="tt" items="${trangThaiSauBenh}"><option value="${tt}">${tt}</option></c:forEach>
            </select></div>
          <div class="fld"><label>&nbsp;</label>
            <div id="sbGoiY" class="mini" style="padding:11px 13px;border-radius:9px;background:#f5f7fa"></div></div>
          <div class="fld wide"><label>Biện pháp xử lý</label>
            <textarea name="bien_phap_xu_ly" id="sbBP" placeholder="Mô tả thuốc, liều lượng, cách phun..."></textarea></div>
        </div>

        <div class="pick" id="sbKhoiThuoc" style="margin-top:18px;display:none">
          <div class="pick-head"><i class="fas fa-spray-can"></i>Thuốc bảo vệ thực vật sử dụng
            <span class="dem" id="sbNhanThuoc"></span></div>
          <div class="pick-scroll">
            <table>
              <thead><tr><th style="width:56px">Chọn</th><th>Tên thuốc</th>
                         <th class="num" style="width:150px">Tồn khả dụng</th>
                         <th style="width:170px">Số lượng dùng</th></tr></thead>
              <tbody>
              <c:forEach var="th" items="${listThuoc}">
                <tr>
                  <td><input type="checkbox" name="thuoc_id" value="${th.id}" class="ck" data-o="thuoc_qty_${th.id}"></td>
                  <td><b>${th.ten}</b><c:if test="${not empty th.han_su_dung_gan_nhat}">
                      <div class="mini">HSD gần nhất: ${th.han_su_dung_gan_nhat}</div></c:if></td>
                  <td class="num ton">${th.ton_kha_dung} ${th.don_vi_tinh}</td>
                  <td><input name="thuoc_qty_${th.id}" id="thuoc_qty_${th.id}" type="number" step="0.01" min="0"
                             max="${th.ton_kha_dung}" value="0" class="sl" data-ck="${th.id}" data-nhom="thuoc"
                             data-max="${th.ton_kha_dung}" data-ten="${th.ten}"></td>
                </tr>
              </c:forEach>
              <c:if test="${empty listThuoc}">
                <tr><td colspan="4" class="trong">Kho chưa có thuốc bảo vệ thực vật khả dụng.<br>
                    <span class="mini">Khai báo vật tư với loại "Thuốc bảo vệ thực vật" và nhập kho trước.</span></td></tr>
              </c:if>
              </tbody>
            </table>
          </div>
        </div>

        <div class="alert note" id="sbCanhBao" style="display:none;margin-top:16px">
          <i class="fas fa-circle-info"></i>
          <div>Khi lưu với trạng thái <b>Đã xử lý</b>, số thuốc khai báo sẽ bị trừ khỏi kho theo FIFO/FEFO.
            Chi phí và diện tích kho giải phóng được ghi vào một nhật ký chăm sóc loại
            <b>Phòng trừ sâu bệnh</b> của lô tương ứng. Thao tác này không thể khai báo lại lần thứ hai.</div>
        </div>

        <div class="btn-row">
          <button type="button" class="btn ghost" onclick="dongModal('mdSB')">Hủy</button>
          <button class="btn green"><i class="fas fa-floppy-disk"></i>Lưu</button>
        </div>
      </div>
    </form>
  </div>
</div>

<!-- Dữ liệu tham chiếu mật độ cho JavaScript -->
<div id="dataMatDo" style="display:none">
  <c:forEach var="m" items="${listMatDo}">
    <span data-tu="${m.mat_do_tu}" data-den="${m.mat_do_den}" data-loai="${m.phan_loai}" data-rr="${m.dac_diem_rui_ro}"></span>
  </c:forEach>
</div>

<script>
/* =============== Chỉ hiện panel của tab đang chọn =============== */
(function () {
  var tab = new URLSearchParams(window.location.search).get('tab') || 'giong';
  document.querySelectorAll('.panel[data-tab]').forEach(function (p) {
    p.style.display = (p.getAttribute('data-tab') === tab) ? '' : 'none';
  });
})();

/* =============== Hộp thoại =============== */
function moModal(id){ document.getElementById(id).classList.add('mo'); document.body.style.overflow='hidden'; }
function dongModal(id){ document.getElementById(id).classList.remove('mo'); document.body.style.overflow=''; }
document.querySelectorAll('.modal').forEach(function(m){
  m.addEventListener('click', function(e){ if(e.target===m) dongModal(m.id); });
});
document.addEventListener('keydown', function(e){
  if(e.key==='Escape') document.querySelectorAll('.modal.mo').forEach(function(m){ dongModal(m.id); });
});
function d(el, ten){ var v = el.closest('tr').dataset[ten]; return (v==null||v==='null') ? '' : v; }

/* =============== UC-4.1 Giống =============== */
function moThemGiong(){
  document.getElementById('mdGiongTitle').textContent='Thêm giống mới';
  document.getElementById('gAction').value='giong_insert';
  document.getElementById('gId').value='';
  document.getElementById('gTen').value='';
  document.getElementById('gDD').value='';
  document.getElementById('gTG').value=0;
  document.getElementById('gNS').value=0;
  document.getElementById('gTT').selectedIndex=0;
  moModal('mdGiong');
}
function moSuaGiong(btn){
  document.getElementById('mdGiongTitle').textContent='Sửa thông tin giống';
  document.getElementById('gAction').value='giong_update';
  document.getElementById('gId').value=d(btn,'id');
  document.getElementById('gTen').value=d(btn,'ten');
  document.getElementById('gDD').value=d(btn,'dd');
  document.getElementById('gTG').value=d(btn,'tg');
  document.getElementById('gNS').value=d(btn,'ns');
  document.getElementById('gTT').value=d(btn,'tt');
  moModal('mdGiong');
}

/* =============== UC-4.2 Vườn + tính mật độ trực tiếp =============== */
var bangMatDo = [];
document.querySelectorAll('#dataMatDo span').forEach(function(s){
  bangMatDo.push({
    tu: parseFloat(s.dataset.tu)||0,
    den: (s.dataset.den==='' || s.dataset.den==null) ? null : parseFloat(s.dataset.den),
    loai: s.dataset.loai || '',
    rr: s.dataset.rr || ''
  });
});
var MAT_DO_MIN = 50, MAT_DO_MAX = 400;

function tinhMatDo(){
  var sel = document.getElementById('vLo');
  var dt = parseFloat(sel.options[sel.selectedIndex] ? sel.options[sel.selectedIndex].dataset.dt : 0) || 0;
  var cay = parseInt(document.getElementById('vCay').value, 10) || 0;
  document.getElementById('vDT').value = dt;

  var oPL = document.getElementById('vPL');
  if (dt <= 0 || cay <= 0){
    document.getElementById('vMD').value = 0;
    oPL.innerHTML = 'Chọn lô đất và nhập số cây để xem phân loại.';
    oPL.style.background = '#f5f7fa';
    return;
  }

  var md = Math.round(cay / (dt / 10000) * 100) / 100;
  document.getElementById('vMD').value = md;

  var khop = null;
  for (var i = 0; i < bangMatDo.length; i++){
    var m = bangMatDo[i];
    if (md >= m.tu && (m.den === null || md <= m.den)) { khop = m; break; }
  }
  var batThuong = (md < MAT_DO_MIN || md > MAT_DO_MAX);
  var ten = khop ? khop.loai : 'Không khớp bảng tham chiếu';
  var rr  = khop ? khop.rr : 'Mật độ nằm ngoài mọi khoảng đã khai báo.';

  oPL.innerHTML = '<span class="tag ' + (batThuong ? 'bad' : 'ok') + '">'
      + (batThuong ? '<i class="fas fa-triangle-exclamation"></i>' : '<i class="fas fa-check"></i>')
      + ten + '</span> <span class="mini" style="margin-left:8px">' + rr + '</span>'
      + (batThuong ? '<div class="mini" style="margin-top:7px;color:#a5322f">Mật độ hợp lý khoảng '
          + MAT_DO_MIN + ' - ' + MAT_DO_MAX + ' cây/ha. Vẫn lưu được nhưng lô sẽ bị đánh dấu bất thường.</div>' : '');
  oPL.style.background = batThuong ? '#fdeceb' : '#eaf6ee';
}

function moThemVuon(){
  document.getElementById('mdVuonTitle').textContent='Thiết lập vườn mới';
  document.getElementById('vAction').value='vuon_insert';
  document.getElementById('vId').value='';
  document.getElementById('vLo').selectedIndex=0;
  document.getElementById('vGiong').selectedIndex=0;
  document.getElementById('vCay').value='';
  document.getElementById('vNgay').value='';
  document.getElementById('vGC').value='';
  document.getElementById('vGD').selectedIndex=0;
  tinhMatDo();
  moModal('mdVuon');
}
function moSuaVuon(btn){
  document.getElementById('mdVuonTitle').textContent='Sửa thiết lập vườn';
  document.getElementById('vAction').value='vuon_update';
  document.getElementById('vId').value=d(btn,'id');
  document.getElementById('vLo').value=d(btn,'lo');
  document.getElementById('vGiong').value=d(btn,'giong');
  document.getElementById('vCay').value=d(btn,'cay');
  document.getElementById('vGC').value=d(btn,'gc');
  var gd=d(btn,'gd'); if(gd) document.getElementById('vGD').value=gd;
  tinhMatDo();
  moModal('mdVuon');
}

/* =============== UC-4.3 Lịch =============== */
function moThemLich(){
  document.getElementById('mdLichTitle').textContent='Tạo lịch chăm sóc';
  document.getElementById('lAction').value='lich_insert';
  document.getElementById('lId').value='';
  document.getElementById('lCV').value='';
  document.getElementById('lBD').value='';
  document.getElementById('lCK').value=0;
  document.getElementById('lKT').value='';
  document.getElementById('lMT').value='';
  document.getElementById('lTT').selectedIndex=0;
  Array.from(document.getElementById('lLo').options).forEach(function(o){ o.selected=false; });
  moModal('mdLich');
}
function moSuaLich(btn){
  document.getElementById('mdLichTitle').textContent='Sửa lịch chăm sóc';
  document.getElementById('lAction').value='lich_update';
  document.getElementById('lId').value=d(btn,'id');
  document.getElementById('lCV').value=d(btn,'cv');
  document.getElementById('lBD').value=d(btn,'bd');
  document.getElementById('lCK').value=d(btn,'ck') || 0;
  document.getElementById('lKT').value=d(btn,'kt');
  document.getElementById('lMT').value=d(btn,'mt');
  document.getElementById('lTT').value=d(btn,'tt');
  var chon=(d(btn,'lo')||'').split(',');
  Array.from(document.getElementById('lLo').options).forEach(function(o){
    o.selected = chon.indexOf(o.value) >= 0;
  });
  moModal('mdLich');
}

/* =============== UC-4.4 Nhật ký =============== */
function apDungNhacViec(){
  var sel=document.getElementById('nvChon');
  var o=sel.options[sel.selectedIndex];
  if(!o || !o.dataset.lo) return;
  document.getElementById('nkLo').value=o.dataset.lo;
  document.getElementById('nkCV').value=o.dataset.cv;
  document.getElementById('nkNgay').value=o.dataset.ngay;
}

/* Tích/bỏ tích ô chọn theo số lượng nhập, và chặn vượt tồn kho */
document.addEventListener('input', function(e){
  var el=e.target;
  if(!el.classList || !el.classList.contains('sl')) return;

  var max=parseFloat(el.dataset.max);
  var val=parseFloat(el.value);
  if(!isNaN(max) && !isNaN(val) && val>max){
    el.value=max;
    el.style.borderColor='#e05555';
    setTimeout(function(){ el.style.borderColor=''; }, 900);
    val=max;
  }
  var nhom=el.dataset.nhom;
  var ten = nhom==='vt' ? 'vt_id' : (nhom==='dc' ? 'dc_id' : (nhom==='tb' ? 'tb_id' : 'thuoc_id'));
  var box=document.querySelector('input[name="'+ten+'"][value="'+el.dataset.ck+'"]');
  if(box){
    box.checked = val>0;
    var tr=box.closest('tr');
    if(tr) tr.classList.toggle('chon', box.checked);
  }
});

/* Bỏ tích thì đưa số lượng về 0 */
document.addEventListener('change', function(e){
  var el=e.target;
  if(!el.classList || !el.classList.contains('ck')) return;
  var o=document.getElementById(el.dataset.o);
  if(o && !el.checked) o.value=0;
  var tr=el.closest('tr');
  if(tr) tr.classList.toggle('chon', el.checked);
});

/* =============== UC-4.5 Sinh trưởng =============== */
var DS_GIAI_DOAN = [];
<c:forEach var="gd" items="${giaiDoan}">DS_GIAI_DOAN.push('${gd}');</c:forEach>

function taoDongGiaiDoan(ten, tyLe){
  var tr=document.createElement('tr');
  var opts='<option value="">-- Chọn giai đoạn --</option>';
  DS_GIAI_DOAN.forEach(function(g){
    opts += '<option value="'+g+'"'+(g===ten?' selected':'')+'>'+g+'</option>';
  });
  tr.innerHTML =
    '<td><select name="gd_ten" style="width:100%;padding:7px 10px;border:1px solid #d4dce8;border-radius:7px">'+opts+'</select></td>'+
    '<td><input name="gd_ty_le" type="number" step="0.01" min="0" max="100" value="'+(tyLe||0)+'"></td>'+
    '<td><button type="button" class="btn sm icon red" onclick="this.closest(\'tr\').remove()"><i class="fas fa-xmark"></i></button></td>';
  return tr;
}
function themDongGiaiDoan(){
  document.querySelector('#tblGiaiDoan tbody').appendChild(taoDongGiaiDoan('', 0));
}
function moSinhTruong(btn){
  var tbody=document.querySelector('#tblGiaiDoan tbody');
  tbody.innerHTML='';
  if(btn){
    document.getElementById('mdSTTitle').textContent='Cập nhật sinh trưởng của lô';
    document.getElementById('stVuon').value=d(btn,'vuon');
    var gd=d(btn,'gd'); document.getElementById('stGD').value=gd||'';
    document.getElementById('stLCN').value=d(btn,'lcn')||'Chuyển giai đoạn';
    document.getElementById('stGC').value=d(btn,'gc');
    document.getElementById('stGiam').value=0;
    /* Nạp lại tỷ lệ đã lưu, định dạng "Cây con: 30%, Sinh trưởng: 70%" */
    var tl=d(btn,'tl');
    if(tl){
      tl.split(',').forEach(function(p){
        var kv=p.split(':');
        if(kv.length<2) return;
        tbody.appendChild(taoDongGiaiDoan(kv[0].trim(), parseFloat(kv[1].replace('%',''))||0));
      });
    }
  } else {
    document.getElementById('mdSTTitle').textContent='Cập nhật sinh trưởng';
    document.getElementById('stVuon').selectedIndex=0;
    document.getElementById('stGD').selectedIndex=0;
    document.getElementById('stLCN').selectedIndex=0;
    document.getElementById('stGC').value='';
    document.getElementById('stGiam').value=0;
  }
  if(tbody.children.length===0) themDongGiaiDoan();
  moModal('mdST');
}

/* =============== UC-4.6 Sâu bệnh =============== */
function doiTrangThaiSauBenh(){
  var tt=document.getElementById('sbTT').value;
  var khoi=document.getElementById('sbKhoiThuoc');
  var canhBao=document.getElementById('sbCanhBao');
  var goiY=document.getElementById('sbGoiY');
  var laSua=document.getElementById('sbAction').value==='saubenh_update';
  var daTru=document.getElementById('sbId').dataset.nk==='1';

  if(tt==='Chưa xử lý'){
    khoi.style.display='none'; canhBao.style.display='none';
    goiY.textContent='Mới phát hiện, chưa áp dụng biện pháp nào. Kho chưa bị trừ.';
  } else if(tt==='Đang xử lý'){
    khoi.style.display = (laSua && !daTru) ? '' : 'none';
    canhBao.style.display='none';
    goiY.textContent='Đang áp dụng biện pháp. Có thể chọn trước thuốc dự kiến, kho vẫn CHƯA bị trừ.';
  } else {
    khoi.style.display = (laSua && !daTru) ? '' : 'none';
    canhBao.style.display = (laSua && !daTru) ? '' : 'none';
    goiY.textContent = daTru
      ? 'Ghi nhận này đã trừ kho thuốc trước đó.'
      : 'Đóng ghi nhận. Thuốc khai báo sẽ bị trừ kho và tính chi phí vào lô.';
  }
  document.getElementById('sbNhanThuoc').textContent =
      tt==='Đã xử lý' ? 'Số lượng khai báo sẽ bị trừ kho khi lưu' : 'Dự trù - chưa trừ kho';
}
function moThemSauBenh(){
  document.getElementById('mdSBTitle').textContent='Ghi nhận sâu bệnh mới';
  document.getElementById('sbAction').value='saubenh_insert';
  document.getElementById('sbId').value='';
  document.getElementById('sbId').dataset.nk='0';
  document.getElementById('sbVuon').selectedIndex=0;
  document.getElementById('sbVuon').disabled=false;
  document.getElementById('sbTen').value='';
  document.getElementById('sbMD').selectedIndex=0;
  document.getElementById('sbNgay').value='';
  document.getElementById('sbBP').value='';
  document.getElementById('sbTT').selectedIndex=0;
  doiTrangThaiSauBenh();
  moModal('mdSB');
}
function moSuaSauBenh(btn){
  document.getElementById('mdSBTitle').textContent='Cập nhật xử lý sâu bệnh';
  document.getElementById('sbAction').value='saubenh_update';
  document.getElementById('sbId').value=d(btn,'id');
  document.getElementById('sbId').dataset.nk = d(btn,'nk') ? '1' : '0';
  document.getElementById('sbVuon').value=d(btn,'vuon');
  document.getElementById('sbVuon').disabled=false;
  document.getElementById('sbTen').value=d(btn,'ten');
  document.getElementById('sbMD').value=d(btn,'md');
  document.getElementById('sbNgay').value=d(btn,'ngay');
  document.getElementById('sbBP').value=d(btn,'bp');
  document.getElementById('sbTT').value=d(btn,'tt');
  doiTrangThaiSauBenh();
  moModal('mdSB');
}

/* =============== UC-4.7 Phân loại thu hoạch =============== */
function themDongPhanLoai(){
  var tbody=document.querySelector('#tblPhanLoai tbody');
  var tr=tbody.rows[0].cloneNode(true);
  tr.querySelectorAll('input').forEach(function(i){ i.value = (i.name==='pl_loai') ? '' : '0'; });
  tbody.appendChild(tr);
}
function xoaDongPhanLoai(btn){
  var tbody=document.querySelector('#tblPhanLoai tbody');
  if(tbody.rows.length>1) btn.closest('tr').remove();
  else tbody.rows[0].querySelectorAll('input').forEach(function(i){ i.value=(i.name==='pl_loai')?'':'0'; });
}
</script>
</body>
</html>
