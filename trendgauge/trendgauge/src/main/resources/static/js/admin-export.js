const dailyRadio = document.getElementById("daily");
const monthlyRadio = document.getElementById("monthly");

const dateRangeArea = document.getElementById("date-range-area");
const targetMonthArea = document.getElementById("target-month-area");

dailyRadio.addEventListener("change", function () {
    if (dailyRadio.checked) {
        dateRangeArea.style.display = "block";
        targetMonthArea.style.display = "none";
    }
});

monthlyRadio.addEventListener("change", function () {
    if (monthlyRadio.checked) {
        dateRangeArea.style.display = "none";
        targetMonthArea.style.display = "block";
    }
});