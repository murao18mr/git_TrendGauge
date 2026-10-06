const apiKeyInput = document.getElementById("api-key");
const toggleApiKeyButton = document.getElementById("toggle-api-key");

toggleApiKeyButton.addEventListener("click", function () {
    if (apiKeyInput.type === "password") {
        apiKeyInput.type = "text";
        toggleApiKeyButton.textContent = "非表示";
    } else {
        apiKeyInput.type = "password";
        toggleApiKeyButton.textContent = "表示";
    }
});