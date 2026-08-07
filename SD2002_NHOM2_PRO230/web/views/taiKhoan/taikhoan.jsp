<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Tài khoản - Trang trại Sầu Riêng</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { font-family: 'Roboto', sans-serif; background: #f5f7fa; color: #333; display: flex; height: 100vh; overflow: hidden; }
            .main-content { flex: 1; display: flex; flex-direction: column; height: 100vh; overflow: hidden; }
            .content { flex: 1; padding: 28px 32px; overflow-y: auto; background: #f5f7fa; }
            .overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.3); z-index: 999; }
            .overlay.active { display: block; }

            .toolbar { display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; margin-bottom: 24px; gap: 16px; }
            .toolbar .search-box { display: flex; align-items: center; background: #fff; border-radius: 8px; padding: 0 16px; border: 1px solid #e9edf4; flex: 1 1 300px; }
            .toolbar .search-box i { color: #8aa3c0; margin-right: 10px; }
            .toolbar .search-box input { border: none; padding: 10px 0; font-size: 14px; width: 100%; outline: none; background: transparent; }
            .toolbar .actions { display: flex; gap: 12px; flex-wrap: wrap; }

            .btn { padding: 10px 20px; border: none; border-radius: 8px; font-weight: 500; font-size: 14px; cursor: pointer; display: inline-flex; align-items: center; gap: 8px; transition: 0.2s; text-decoration: none; }
            .btn-primary { background: #4d90fe; color: #fff; }
            .btn-primary:hover { background: #3a7bd5; }
            .btn-success { background: #6fcf97; color: #fff; }
            .btn-success:hover { background: #52b381; }
            .btn-outline { background: transparent; color: #4a5b6e; border: 1px solid #d0d8e3; }
            .btn-outline:hover { background: #e9edf4; }

            .actions-cell { display: flex; gap: 8px; justify-content: center; }
            .btn-action { width: 38px; height: 32px; border: none; border-radius: 8px; color: #fff; font-size: 14px; cursor: pointer; display: inline-flex; align-items: center; justify-content: center; transition: 0.2s; }
            .btn-action.edit { background-color: #4d90fe; }
            .btn-action.lock { background-color: #e67e22; }
            .btn-action.unlock { background-color: #6fcf97; }
            .btn-action:hover { opacity: 0.85; transform: translateY(-2px); }

            .table-container { background: #fff; border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.04); overflow-x: auto; padding: 0 0 8px 0; }
            .material-table { width: 100%; border-collapse: collapse; font-size: 14px; min-width: 900px; }
            .material-table th { text-align: left; padding: 16px; color: #6f8fb0; font-weight: 500; border-bottom: 1px solid #e9edf4; background: #fafbfc; white-space: nowrap; }
            .material-table td { padding: 14px 16px; border-bottom: 1px solid #f0f2f7; vertical-align: middle; }
            .material-table tr:hover td { background: #f8faff; }
            .user-cell { display: flex; align-items: center; gap: 12px; }
            .user-cell .avatar { width: 38px; height: 38px; border-radius: 50%; background: #6fcf97; display: flex; align-items: center; justify-content: center; color: #fff; font-weight: 700; font-size: 14px; flex-shrink: 0; }
            .user-cell .u-name { font-weight: 500; color: #1e2a3a; }
            .user-cell .u-sub { font-size: 12px; color: #8aa3c0; }
            .badge-status { display: inline-block; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }
            .badge-status.ok { background: #d5f5e3; color: #1e8449; }
            .badge-status.locked { background: #fadbd8; color: #922b21; }
            .role-badge { display:inline-block; padding: 3px 10px; border-radius: 6px; background: #eef2f7; color: #4a5b6e; font-size: 12px; font-weight: 500; }
            .empty-row td { text-align: center; color: #8aa3c0; padding: 40px 0; }

            .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.4); z-index: 9999; justify-content: center; align-items: center; backdrop-filter: blur(2px); }
            .modal-overlay.active { display: flex; }
            .modal { background: #fff; border-radius: 20px; width: 95%; max-width: 720px; max-height: 90vh; overflow-y: auto; padding: 32px; box-shadow: 0 20px 60px rgba(0,0,0,0.2); animation: fadeInUp 0.25s ease; }
            @keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
            .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
            .modal-header h2 { font-size: 22px; font-weight: 500; color: #1e2a3a; }
            .modal-header .close-modal { background: none; border: none; font-size: 28px; cursor: pointer; color: #8aa3c0; }
            .modal-header .close-modal:hover { color: #e74c3c; }
            .form-group { margin-bottom: 18px; }
            .form-group label { display: block; font-weight: 500; margin-bottom: 6px; color: #2c3e50; font-size: 14px; }
            .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 10px 14px; border: 1px solid #d0d8e3; border-radius: 8px; font-size: 14px; background: #fafbfc; font-family: inherit; }
            .form-group input:focus, .form-group select:focus, .form-group textarea:focus { border-color: #4d90fe; outline: none; background: #fff; }
            .form-group input[readonly] { background: #eef2f7; cursor: not-allowed; }
            .form-row { display: flex; flex-wrap: wrap; gap: 16px; }
            .form-row .form-group { flex: 1 1 calc(50% - 8px); min-width: 200px; }
            .form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px; border-top: 1px solid #e9edf4; padding-top: 20px; }
            .note-box { background: #eef6ff; border: 1px solid #cfe3fb; color: #24506f; padding: 12px 14px; border-radius: 10px; font-size: 13px; margin-bottom: 20px; display: flex; gap: 10px; }
            .req { color: #e74c3c; }
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
                    if (pageTitle) pageTitle.innerHTML = 'Quản lý Tài khoản <span>| Bảng điều khiển</span>';
                });
            </script>

            <section class="content">
                <div class="toolbar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" placeholder="Tìm theo họ tên, tên đăng nhập, email..." id="searchInput" oninput="filterTable()">
                    </div>
                    <div class="actions">
                        <button class="btn btn-primary" id="btnAdd"><i class="fas fa-user-plus"></i> Thêm tài khoản</button>
                    </div>
                </div>

                <div class="table-container">
                    <table class="material-table" id="accountTable">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Người dùng</th>
                                <th>Tên đăng nhập</th>
                                <th>Số điện thoại</th>
                                <th>Vai trò</th>
                                <th>Trạng thái</th>
                                <th style="text-align:center;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="accountTableBody">
                            <c:choose>
                                <c:when test="${empty listTaiKhoan}">
                                    <tr class="empty-row"><td colspan="7"><i class="fas fa-inbox"></i> Chưa có tài khoản nào.</td></tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${listTaiKhoan}" var="tk">
                                        <tr>
                                            <td><strong>TK<fmt:formatNumber value="${tk.id}" minIntegerDigits="3"/></strong></td>
                                            <td>
                                                <div class="user-cell">
                                                    <div class="avatar"><c:out value="${fn:toUpperCase(fn:substring(tk.nguoiDung.ho_ten, 0, 1))}"/></div>
                                                    <div>
                                                        <div class="u-name"><c:out value="${tk.nguoiDung.ho_ten}"/></div>
                                                        <div class="u-sub"><c:out value="${tk.nguoiDung.email}"/></div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td><c:out value="${tk.ten_dang_nhap}"/></td>
                                            <td><c:out value="${tk.nguoiDung.so_dien_thoai}"/></td>
                                            <td><span class="role-badge"><c:out value="${tk.ten_vai_tro}"/></span></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${tk.trang_thai}"><span class="badge-status ok">Hoạt động</span></c:when>
                                                    <c:otherwise><span class="badge-status locked">Đã khóa</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="actions-cell">
                                                    <button class="btn-action edit" title="Cập nhật"
                                                            data-id="${tk.id}"
                                                            data-hoten="<c:out value='${tk.nguoiDung.ho_ten}'/>"
                                                            data-email="<c:out value='${tk.nguoiDung.email}'/>"
                                                            data-sdt="<c:out value='${tk.nguoiDung.so_dien_thoai}'/>"
                                                            data-ngaysinh="<fmt:formatDate value='${tk.nguoiDung.ngay_sinh}' pattern='yyyy-MM-dd'/>"
                                                            data-gioitinh="${tk.nguoiDung.gioi_tinh ? '1' : '0'}"
                                                            data-diachi="<c:out value='${tk.nguoiDung.dia_chi}'/>"
                                                            data-username="<c:out value='${tk.ten_dang_nhap}'/>"
                                                            onclick="openEditFromBtn(this)">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <c:choose>
                                                        <c:when test="${tk.trang_thai}">
                                                            <button class="btn-action lock" title="Khóa tài khoản" onclick="doiTrangThai(${tk.id}, 'lock')"><i class="fas fa-lock"></i></button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button class="btn-action unlock" title="Mở khóa tài khoản" onclick="doiTrangThai(${tk.id}, 'unlock')"><i class="fas fa-lock-open"></i></button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>

        <!-- ===== MODAL THÊM / SỬA TÀI KHOẢN ===== -->
        <div class="modal-overlay" id="accountModal">
            <div class="modal">
                <div class="modal-header">
                    <h2 id="modalTitle">Thêm tài khoản</h2>
                    <button class="close-modal" onclick="closeModal()">&times;</button>
                </div>

                <div class="note-box" id="noteAdd">
                    <i class="fas fa-info-circle" style="margin-top:2px;"></i>
                    <span>Hệ thống sẽ <b>tự động sinh tên đăng nhập</b> từ họ tên và <b>mật khẩu mặc định</b>, sau đó gửi tới email người dùng. Người dùng phải đổi mật khẩu ở lần đăng nhập đầu tiên.</span>
                </div>
                <div class="note-box" id="noteEdit" style="display:none;">
                    <i class="fas fa-info-circle" style="margin-top:2px;"></i>
                    <span>Tên đăng nhập <b>không sửa thủ công</b>. Nếu bạn thay đổi <b>họ tên</b>, hệ thống sẽ tự sinh lại tên đăng nhập và gửi tên đăng nhập mới về email.</span>
                </div>

                <form id="accountForm" onsubmit="submitForm(event)">
                    <input type="hidden" id="editId" value="">

                    <div class="form-group" id="usernameGroup" style="display:none;">
                        <label>Tên đăng nhập hiện tại</label>
                        <input type="text" id="usernameView" readonly>
                    </div>

                    <div class="form-group">
                        <label>Họ và tên <span class="req">*</span></label>
                        <input type="text" id="hoTen" placeholder="VD: Nguyễn Văn An" required>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Email <span class="req">*</span></label>
                            <input type="email" id="email" placeholder="email@example.com" required>
                        </div>
                        <div class="form-group">
                            <label>Số điện thoại <span class="req">*</span></label>
                            <input type="text" id="soDienThoai" placeholder="0xxxxxxxxx" maxlength="10" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Ngày sinh <span class="req">*</span></label>
                            <input type="date" id="ngaySinh" required>
                        </div>
                        <div class="form-group">
                            <label>Giới tính <span class="req">*</span></label>
                            <select id="gioiTinh" required>
                                <option value="1">Nam</option>
                                <option value="0">Nữ</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Địa chỉ <span class="req">*</span></label>
                        <textarea id="diaChi" rows="2" placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành" required></textarea>
                    </div>

                    <div class="form-actions">
                        <button type="button" class="btn btn-outline" onclick="closeModal()">Hủy</button>
                        <button type="submit" class="btn btn-success" id="btnSubmit"><i class="fas fa-save"></i> Lưu</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            const ctx = '${pageContext.request.contextPath}';

            function filterTable() {
                const kw = document.getElementById('searchInput').value.toLowerCase();
                document.querySelectorAll('#accountTableBody tr').forEach(function (row) {
                    if (row.classList.contains('empty-row')) return;
                    row.style.display = row.textContent.toLowerCase().includes(kw) ? '' : 'none';
                });
            }

            function openModalAdd() {
                document.getElementById('accountForm').reset();
                document.getElementById('editId').value = '';
                document.getElementById('modalTitle').textContent = 'Thêm tài khoản';
                document.getElementById('usernameGroup').style.display = 'none';
                document.getElementById('noteAdd').style.display = 'flex';
                document.getElementById('noteEdit').style.display = 'none';
                document.getElementById('gioiTinh').value = '1';
                document.getElementById('accountModal').classList.add('active');
            }

            function openEditFromBtn(btn) {
                document.getElementById('accountForm').reset();
                document.getElementById('editId').value = btn.getAttribute('data-id');
                document.getElementById('modalTitle').textContent = 'Cập nhật tài khoản';
                document.getElementById('usernameGroup').style.display = 'block';
                document.getElementById('usernameView').value = btn.getAttribute('data-username');
                document.getElementById('noteAdd').style.display = 'none';
                document.getElementById('noteEdit').style.display = 'flex';

                document.getElementById('hoTen').value = btn.getAttribute('data-hoten');
                document.getElementById('email').value = btn.getAttribute('data-email');
                document.getElementById('soDienThoai').value = btn.getAttribute('data-sdt');
                document.getElementById('ngaySinh').value = btn.getAttribute('data-ngaysinh');
                document.getElementById('gioiTinh').value = btn.getAttribute('data-gioitinh');
                document.getElementById('diaChi').value = btn.getAttribute('data-diachi');
                document.getElementById('accountModal').classList.add('active');
            }

            function closeModal() {
                document.getElementById('accountModal').classList.remove('active');
            }

            function submitForm(e) {
                e.preventDefault();
                const editId = document.getElementById('editId').value;
                const sdt = document.getElementById('soDienThoai').value.trim();
                if (!/^0\d{9}$/.test(sdt)) {
                    alert('Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.');
                    return;
                }

                const p = new URLSearchParams();
                p.append('action', editId ? 'update' : 'insert');
                if (editId) p.append('id', editId);
                p.append('ho_ten', document.getElementById('hoTen').value.trim());
                p.append('email', document.getElementById('email').value.trim());
                p.append('so_dien_thoai', sdt);
                p.append('ngay_sinh', document.getElementById('ngaySinh').value);
                p.append('gioi_tinh', document.getElementById('gioiTinh').value);
                p.append('dia_chi', document.getElementById('diaChi').value.trim());

                const btn = document.getElementById('btnSubmit');
                btn.disabled = true;
                fetch(ctx + '/taikhoan', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    body: p.toString()
                }).then(r => r.json()).then(data => {
                    alert(data.message);
                    if (data.success) location.reload();
                }).catch(() => alert('Có lỗi xảy ra, vui lòng thử lại.'))
                  .finally(() => { btn.disabled = false; });
            }

            function doiTrangThai(id, action) {
                const msg = action === 'lock'
                        ? 'Bạn có chắc chắn muốn KHÓA tài khoản này? Người dùng sẽ không thể đăng nhập.'
                        : 'Bạn có chắc chắn muốn MỞ KHÓA tài khoản này?';
                if (!confirm(msg)) return;
                const p = new URLSearchParams();
                p.append('action', action);
                p.append('id', id);
                fetch(ctx + '/taikhoan', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    body: p.toString()
                }).then(r => r.json()).then(data => {
                    alert(data.message);
                    if (data.success) location.reload();
                }).catch(() => alert('Có lỗi xảy ra, vui lòng thử lại.'));
            }

            document.getElementById('btnAdd').addEventListener('click', openModalAdd);

            // Responsive Sidebar
            const menuToggle = document.getElementById('menuToggle');
            const sidebar = document.getElementById('sidebar');
            const overlay = document.getElementById('overlay');
            function toggleSidebar() { sidebar.classList.toggle('open'); overlay.classList.toggle('active'); }
            if (menuToggle) menuToggle.addEventListener('click', toggleSidebar);
            if (overlay) overlay.addEventListener('click', toggleSidebar);
        </script>
    </body>
</html>
