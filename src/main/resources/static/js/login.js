document.addEventListener("DOMContentLoaded", function() {
    const dropdown = document.querySelector(".dropdown");
    const dropdownBtn = document.querySelector(".dropdown-btn");
    const dropdownContent = document.querySelector(".dropdown-content");
    const loginAsInput = document.getElementById("loginAs");

    // Toggle dropdown content display on button click
    dropdownBtn.addEventListener("click", function(event) {
        event.preventDefault();
        dropdown.classList.toggle("active");
    });

    // Set the loginAs value when an option is selected
    dropdownContent.addEventListener("click", function(event) {
        if (event.target.tagName === "A") {
            event.preventDefault();
            const selectedRole = event.target.getAttribute("data-value");
            loginAsInput.value = selectedRole;
            dropdownBtn.textContent = event.target.textContent;
            dropdown.classList.remove("active");
        }
    });


    // Close dropdown if clicked outside
    document.addEventListener("click", function(event) {
        if (!dropdown.contains(event.target)) {
            dropdown.classList.remove("active");
        }
    });
});