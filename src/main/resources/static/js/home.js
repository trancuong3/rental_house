function showPopup() {
    const popup = document.getElementById('popup');
    popup.style.display = 'flex'; // Show the popup
}

function hidePopup() {
    const popup = document.getElementById('popup');
    popup.style.display = 'none'; // Hide the popup
}
function scrollLeft() {
    const container = document.querySelector('.filters');
    container.scrollBy({
        left: -200, // Adjust scroll distance
        behavior: 'smooth'
    });
}

function scrollRight() {
    const container = document.querySelector('.filters');
    container.scrollBy({
        left: 200, // Adjust scroll distance
        behavior: 'smooth'
    });
}
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

// Get the raw price from the `data-raw-price` attribute
const priceElement = document.getElementById('price');
const rawPrice = priceElement.getAttribute('data-raw-price');
priceElement.textContent = formatCurrency(rawPrice);