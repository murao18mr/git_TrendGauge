const clientSecretInput = document.getElementById("client-secret");
const toggleClientSecretButton = document.getElementById("toggle-client-secret");

toggleClientSecretButton.addEventListener("click", function () {
    if (clientSecretInput.type === "password") {
        clientSecretInput.type = "text";
        toggleClientSecretButton.textContent = "非表示";
    } else {
        clientSecretInput.type = "password";
        toggleClientSecretButton.textContent = "表示";
    }
});