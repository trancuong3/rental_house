<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Thuê phòng</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 20px;
    }
    .box {
      margin: 10px 0;
    }
    span {
      font-weight: bold;
    }
  </style>
</head>
<body>
  <h2>Đặt thuê phòng</h2>

  <div class="box">
    Giá thuê mỗi ngày: <span id="pricePerNight">500.000</span> VNĐ
  </div>

  <div class="box">
    Ngày bắt đầu: <input type="date" id="startDate">
  </div>

  <div class="box">
    Ngày kết thúc: <input type="date" id="endDate">
  </div>

  <div class="box">
    Số ngày thuê: <span id="numDays">0</span>
  </div>

  <div class="box">
    Tạm tính: <span id="subtotal">0 VNĐ</span>
  </div>

  <div class="box">
    Tổng tiền: <span id="totalPrice">0 VNĐ</span>
  </div>

  <script>
    document.addEventListener("DOMContentLoaded", () => {
      const startDateInput = document.getElementById("startDate");
      const endDateInput = document.getElementById("endDate");
      const numDaysSpan = document.getElementById("numDays");
      const subtotalSpan = document.getElementById("subtotal");
      const totalPriceSpan = document.getElementById("totalPrice");

      // Lấy giá thuê từ HTML (loại bỏ định dạng nếu có)
      const pricePerDay = parseInt(
        document.getElementById("pricePerNight").textContent.replace(/\./g, "")
      );

      // Lấy ngày hôm nay (yyyy-mm-dd)
      const today = new Date().toISOString().split("T")[0];

      // Giới hạn ngày bắt đầu không được chọn trước hôm nay
      startDateInput.min = today;
      endDateInput.min = today;

      // Hàm tính toán tổng giá trị
      function calculateRental() {
        const startDate = new Date(startDateInput.value);
        const endDate = new Date(endDateInput.value);

        if (startDate && endDate && startDate < endDate) {
          // Tính số ngày
          const timeDiff = Math.abs(endDate - startDate);
          const numDays = Math.ceil(timeDiff / (1000 * 60 * 60 * 24));

          // Tính tổng tiền
          const subtotal = numDays * pricePerDay;

          // Cập nhật giao diện
          numDaysSpan.textContent = numDays;
          subtotalSpan.textContent = `${subtotal.toLocaleString("vi-VN")} VNĐ`;
          totalPriceSpan.textContent = `${subtotal.toLocaleString("vi-VN")} VNĐ`;
        } else {
          // Đặt lại giá trị nếu dữ liệu không hợp lệ
          numDaysSpan.textContent = "0";
          subtotalSpan.textContent = "0 VNĐ";
          totalPriceSpan.textContent = "0 VNĐ";
        }
      }

      // Sự kiện thay đổi ngày
      startDateInput.addEventListener("change", () => {
        // Khi đổi startDate thì cập nhật min cho endDate = startDate
        endDateInput.min = startDateInput.value;
        calculateRental();
      });

      endDateInput.addEventListener("change", calculateRental);
    });
  </script>
</body>
</html>
