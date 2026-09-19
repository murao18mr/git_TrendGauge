const pinInput = document.getElementById("manager-pin");
const pinButtons = document.querySelectorAll("[data-pin]");
const managerLoginButton = document.getElementById("manager-login-button");

pinButtons.forEach(function (button) {
    button.addEventListener("click", function () {
        if (pinInput.value.length >= 4) {
            return;
        }

        pinInput.value += button.dataset.pin;
    });
});

managerLoginButton.addEventListener("click", async function () {
    const pin = pinInput.value;

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const formData = new FormData();
    formData.append("pin", pin);

    const response = await fetch("/manager/login", {
        method: "POST",
        headers: {
            [csrfHeader]: csrfToken
        },
        body: formData
    });

    const result = await response.json();

    if (result) {
        window.location.href = "/manager/menu";
    } else {
        document.getElementById("pin-error").textContent = "PINコードが正しくありません";
        pinInput.value = "";
    }
});

document.getElementById("pin-backspace").addEventListener("click", function () {
    pinInput.value = pinInput.value.slice(0, -1);
});

const managerLoginModal = document.getElementById("manager-login-modal");

managerLoginModal.addEventListener("hidden.bs.modal", function () {
    document.getElementById("pin-error").textContent = "";
    pinInput.value = "";
});