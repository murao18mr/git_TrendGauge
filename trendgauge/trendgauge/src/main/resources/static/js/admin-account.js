const accountForm = document.getElementById("account-register-form");
const clear = document.getElementById("clear");

if (accountForm && clear) {
    clear.addEventListener("click", () => {
        accountForm.querySelectorAll("input:not([type='hidden'])").forEach(input => {
            input.value = "";
        });
    });
}
// トップに戻る
document.getElementById("back-to-top").addEventListener("click", function () {
    window.scrollTo({top: 0, behavior: "smooth"});
});

// 編集モーダル
const editButtons = document.querySelectorAll("button[data-bs-target='#edit-modal']");
const editModal = document.getElementById("edit-modal");

editModal.addEventListener("hidden.bs.modal", function () {
    document.getElementById("edit-store-code-error").textContent = "";
    document.getElementById("edit-store-name-error").textContent = "";
    document.getElementById("edit-email-error").textContent = "";
});

editButtons.forEach(function (button) {
    button.addEventListener("click", function () {
        const row = button.closest("tr");

        document.getElementById("edit-store-code").value = row.dataset.storeCode;
        document.getElementById("edit-store-name").value = row.dataset.storeName;
        document.getElementById("edit-email").value = row.dataset.email;
        document.getElementById("edit-opening-time").value = row.dataset.openingTime || "";
        document.getElementById("edit-closing-time").value = row.dataset.closingTime || "";

        document.getElementById("edit-status-active").checked = row.dataset.status === "active";
        document.getElementById("edit-status-closed").checked = row.dataset.status === "closed";

        editModal.dataset.storeId = row.dataset.storeId;
    });
});

const saveEditButton = document.getElementById("save-edit-button");

saveEditButton.addEventListener("click", function () {

    const storeId = editModal.dataset.storeId;
    const storeCode = document.getElementById("edit-store-code").value.trim();
    const storeName = document.getElementById("edit-store-name").value.trim();
    const email = document.getElementById("edit-email").value.trim();
    const status = document.querySelector("input[name='edit-status']:checked").value;
    const openingTime = document.getElementById("edit-opening-time").value || null;
    const closingTime = document.getElementById("edit-closing-time").value || null;

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const storeCodeError = document.getElementById("edit-store-code-error");
    const storeNameError = document.getElementById("edit-store-name-error");
    const emailError = document.getElementById("edit-email-error");

    storeCodeError.textContent = "";
    storeNameError.textContent = "";
    emailError.textContent = "";

    let hasError = false;

    if (storeCode === "") {
        storeCodeError.textContent = "店舗コードは必須入力です";
        hasError = true;
    } else if (storeCode.length > 20) {
        storeCodeError.textContent = "店舗コードは20文字以内で入力してください";
        hasError = true;
    } else if (!/^[a-zA-Z0-9]+$/.test(storeCode)) {
        storeCodeError.textContent = "店舗コードは半角英数字で入力してください";
        hasError = true;
    }

    if (storeName === "") {
        storeNameError.textContent = "店舗名は必須入力です";
        hasError = true;
    }

    if (email === "") {
        emailError.textContent = "メールアドレスは必須入力です";
        hasError = true;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        emailError.textContent = "メールアドレスの形式で入力してください";
        hasError = true;
    }

    if (hasError) {
        return;
    }

    const data = {
        storeId: storeId,
        storeCode: storeCode,
        storeName: storeName,
        email: email,
        status: status,
        openingTime: openingTime,
        closingTime: closingTime
    };

    fetch("/admin/account/edit", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(data)
    })
        .then(function (response) {
            if (!response.ok) {
                return response.text().then(function (message) {
                    throw new Error(message);
                });
            }

            bootstrap.Modal.getInstance(editModal).hide();
            location.reload();
        })
        .catch(function (error) {
            console.error(error);

            if (error.message === "メールアドレスはすでに使用されています") {
                emailError.textContent = error.message;
            } else if (error.message === "店舗コードはすでに使用されています") {
                storeCodeError.textContent = error.message;
            } else {
                alert(error.message);
            }
        });
});

//パスワード再設定モーダル
const passwordButtons = document.querySelectorAll("button[data-bs-target='#password-modal']");
const passwordModal = document.getElementById("password-modal");

passwordButtons.forEach(function (button) {
    button.addEventListener("click", function () {
        const row = button.closest("tr");

        document.getElementById("password-store-name").textContent = row.dataset.storeName;
        document.getElementById("password-store-id").value = row.dataset.storeId;
    });
});

const passwordStoreId = document.getElementById("password-store-id");
const resetPassword = document.getElementById("reset-password");
const resetConfirmPassword = document.getElementById("reset-confirm-password");

const resetPasswordError = document.getElementById("reset-password-error");
const resetConfirmPasswordError = document.getElementById("reset-confirm-password-error");
const resetPasswordMatchingError = document.getElementById("reset-password-matching-error");

passwordModal.addEventListener("hidden.bs.modal", function () {
    passwordStoreId.value = "";
    resetPassword.value = "";
    resetConfirmPassword.value = "";

    resetPasswordError.textContent = "";
    resetConfirmPasswordError.textContent = "";
    resetPasswordMatchingError.textContent = "";

    document.getElementById("password-store-name").textContent = "店舗名";
});

const savePasswordButton = document.getElementById("save-password-button");

savePasswordButton.addEventListener("click", function () {
    const password = resetPassword.value;
    const confirmPassword = resetConfirmPassword.value;

    resetPasswordError.textContent = "";
    resetConfirmPasswordError.textContent = "";
    resetPasswordMatchingError.textContent = "";

    let hasError = false;

    if (password === "") {
        resetPasswordError.textContent = "パスワードは必須入力です";
        hasError = true;
    } else if (password.length < 8) {
        resetPasswordError.textContent = "パスワードは8文字以上で入力してください";
        hasError = true;
    }

    if (confirmPassword === "") {
        resetConfirmPasswordError.textContent = "確認用パスワードは必須です";
        hasError = true;
    }

    if (password !== "" && confirmPassword !== "" && password !== confirmPassword) {
        resetPasswordMatchingError.textContent = "パスワードと確認用パスワードが一致しません";
        hasError = true;
    }

    if (hasError) {
        return;
    }

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const data = {
        storeId: passwordStoreId.value,
        password: password,
        confirmPassword: confirmPassword
    };

    fetch("/admin/account/password-reset", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(data)
    })
        .then(function (response) {
            if (!response.ok) {
                return response.text().then(function (message) {
                    throw new Error(message);
                });
            }

            const toast = document.getElementById("success-toast");
            const toastBody = document.getElementById("success-toast-body");
            toastBody.textContent = "パスワードを再設定しました。";
            new bootstrap.Toast(toast).show();

            bootstrap.Modal.getInstance(passwordModal).hide();
        })
        .catch(function (error) {
            console.error(error);
            alert(error.message);
        });
});