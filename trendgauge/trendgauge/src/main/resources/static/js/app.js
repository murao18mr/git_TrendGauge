// トースト
const toast = document.getElementById("success-toast");
const toastBody = document.getElementById("success-toast-body");

if (toast && toastBody.textContent.trim()) {
    new bootstrap.Toast(toast).show();
}

// エラー時スクロール
const errorScrollArea = document.getElementById("error-scroll-area");

if (errorScrollArea?.dataset.scrollToError === "true") {
    errorScrollArea.scrollIntoView({
        behavior: "smooth",
        block: "center"
    });
}