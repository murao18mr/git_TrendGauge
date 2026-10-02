const accountForm = document.getElementById("account-register-form");
const clear = document.getElementById("clear");

if (accountForm && clear) {
    clear.addEventListener("click", () => {
        accountForm.querySelectorAll("input:not([type='hidden'])").forEach(input => {
            input.value = "";
        });
    });
}