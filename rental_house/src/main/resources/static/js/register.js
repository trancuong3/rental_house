function checkConfirmPassword() {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    let confirmPasswordError = document.getElementById("confirmPassword_error");

    let isValid = true;

    // Kiểm tra mật khẩu và xác nhận mật khẩu có khớp không
    if (password !== confirmPassword) {
        document.getElementById('confirmPassword').style.borderColor = 'red';
        document.getElementById('confirmPassword').nextElementSibling.style.display = 'block';
        confirmPasswordError.textContent = "Mật khẩu không khớp";
        isValid = false;
    } else {
        document.getElementById('confirmPassword').style.borderColor = '';
        document.getElementById('confirmPassword').nextElementSibling.style.display = 'none';
        confirmPasswordError.textContent = "";
    }

    // Trả về kết quả nếu form hợp lệ
    return isValid;
}

function checkPassword(){
    const password = document.getElementById('password').value;
    let passwordError = document.getElementById("password_error");

    let isValid = true;

    // Kiểm tra mật khẩu độ dài
    if (password.length < 6 || password.length > 32) {
        document.getElementById('password').style.borderColor = 'red';
        document.getElementById('password').nextElementSibling.style.display = 'block';
        passwordError.textContent = "Mật khẩu phải có độ dài từ 6 đến 32 ký tự";
        isValid = false;
    } else {
        document.getElementById('password').style.borderColor = '';
        document.getElementById('password').nextElementSibling.style.display = 'none';
        passwordError.textContent = "";
    }

    // Trả về kết quả nếu form hợp lệ
    return isValid;
}

document.getElementById('registerForm').addEventListener('submit', function(event) {
    if (!checkPassword() || !checkConfirmPassword()) {
        event.preventDefault();  // Ngừng gửi form nếu có lỗi
    }
});

document.getElementById('confirmPassword').addEventListener('input', function(event) {
    if (!checkConfirmPassword()) {
        event.preventDefault();
    }
});
document.getElementById('password').addEventListener('input', function(event) {
    if (!checkPassword()) {
        event.preventDefault();
    }
});