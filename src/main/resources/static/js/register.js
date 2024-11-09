function checkPasswordMatch() {
    const password = document.getElementById('password').value;
    const repass = document.getElementById('repass').value;
    if (password !== repass) {
        document.getElementById('repass').style.borderColor = 'red';
        document.getElementById('repass').nextElementSibling.style.display = 'block';
        document.getElementById('repass').nextElementSibling.textContent = 'Mật khẩu không khớp!';
        return false;
    }
    document.getElementById('repass').style.borderColor = '';
    document.getElementById('repass').nextElementSibling.style.display = 'none';
    return true;
}

document.getElementById('repass').addEventListener('input', function() {
    checkPasswordMatch();
});