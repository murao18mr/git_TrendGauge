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

//トップに戻る
const backToTopButton = document.getElementById("back-to-top");

if (backToTopButton) {
    backToTopButton.addEventListener("click", function () {
        window.scrollTo({top: 0, behavior: "smooth"});
    });
}

const goToBottomButton = document.getElementById("go-to-bottom");

if (goToBottomButton) {
    goToBottomButton.addEventListener("click", function () {
        window.scrollTo({top: document.documentElement.scrollHeight, behavior: "smooth"});
    });
}