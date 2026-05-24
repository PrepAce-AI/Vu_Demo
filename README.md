
Dự án LMS_Demo_Authenticate bằng **Spring Boot**, **Spring Security**, **OAuth2 (Google)** và **SQL Server**.

---
Hướng dẫn

## 💾 1. Cấu hình Database (SQL Server)

Mở SQL Server và chạy lệnh duy nhất sau để tạo Database trống (bảng sẽ tự động sinh sau khi chạy app):

```sql
CREATE DATABASE coursera_demo;

```

---

## 🔑 2. Cấu hình Biến môi trường trên IntelliJ

Vì file `application.properties` sử dụng biến ẩn để bảo mật, bạn bắt buộc phải nạp giá trị thật trong IntelliJ trước khi bấm Run:

1. Tại góc phải trên cùng IntelliJ (cạnh nút **Run** hình tam giác), chọn mục **VuApplication** -> Chọn **Edit Configurations...**
2. Tìm ô **Environment variables** -> Bấm vào biểu tượng **tờ giấy/chiếc hộp** ở cuối dòng để mở bảng nhập.
3. Nhập nhanh chuỗi cấu hình dưới đây vào ô (Nhớ thay các giá trị sau dấu `=` thành thông tin thật của bạn):

```text
DB_USERNAME=sa;DB_PASSWORD=mat_khau_sql;MAIL_USERNAME=email_phu_cua_ban;app_password=ma_16_so_google;GOOGLE_CLIENT_ID=id_google;GOOGLE_CLIENT_SECRET=secret_google

```

4. Bấm **OK** -> **Apply** -> **OK** để lưu lại.

---

## 🚀 3. Khởi chạy dự án

1. Tìm file `VuApplication.java` (`src/main/java/com/vu/`).
2. Chuột phải chọn **Run 'VuApplication.main()'**.
3. Khi Console hiện dòng `Tomcat started on port 8080`, mở trình duyệt truy cập: `http://localhost:8080/login`.
