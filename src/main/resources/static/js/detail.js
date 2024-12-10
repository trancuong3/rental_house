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

    // Thêm sự kiện thay đổi cho ngày bắt đầu và ngày kết thúc
    startDateInput.addEventListener("change", calculateRental);
    endDateInput.addEventListener("change", calculateRental);
});
