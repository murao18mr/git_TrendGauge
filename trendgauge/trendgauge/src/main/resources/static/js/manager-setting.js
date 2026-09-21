const targetMonth = document.getElementById("target-month");
const dailyTargetBody = document.getElementById("daily-target-body");
const dailyTargetMonth = document.getElementById("daily-target-month");
const dailyTargetModal = document.getElementById("daily-target-modal");
const targetAmount = document.getElementById("target-amount");
const lastYearRatio = document.getElementById("last-year-ratio");

function loadTarget() {
    const month = targetMonth.value;

    if (!month) {
        targetAmount.value = "";
        lastYearRatio.value = "";
        return;
    }

    fetch("/manager/setting/target?targetMonth=" + encodeURIComponent(month))
        .then(function (response) {
            if (response.status === 204) {
                targetAmount.value = "";
                lastYearRatio.value = "";
                return null;
            }
            return response.json();
        })
        .then(function (target) {
            if (!target) {
                return;
            }

            targetAmount.value = target.targetAmount ?? "";
            lastYearRatio.value = target.targetRatio ?? "";
        });
}
targetMonth.addEventListener("change", loadTarget);

function calculateTargetRatio() {
    const month = targetMonth.value;
    const amount = targetAmount.value;

    if (!month || !amount) {
        lastYearRatio.value = "";
        return;
    }

    fetch("/manager/setting/target/ratio?targetMonth=" +
        encodeURIComponent(month) + "&targetAmount=" + encodeURIComponent(amount))
        .then(function (response) {
            if (response.status === 204) {
                lastYearRatio.value = "";
                return null;
            }
            return response.json();
        })
        .then(function (ratio) {
            if (ratio === null) {
                return;
            }

            lastYearRatio.value = ratio;
        });
}

targetAmount.addEventListener("change", calculateTargetRatio);

function displayDailyTargets() {
    const month = dailyTargetMonth.value;

    if (!month) {
        dailyTargetBody.innerHTML = "";
        return;
    }

    const [year, monthNumber] = month.split("-").map(Number);
    const lastDay = new Date(year, monthNumber, 0).getDate();

    dailyTargetBody.innerHTML = "";

    for (let day = 1; day <= lastDay; day++) {
        const date = new Date(year, monthNumber - 1, day);
        const weekday = ["日", "月", "火", "水", "木", "金", "土"][date.getDay()];
        const dayType = date.getDay() === 0 ? "日曜" : date.getDay() === 6 ? "土曜" : "平日";

        const row = document.createElement("tr");
        if (date.getDay() === 0) {
            row.classList.add("sunday");
        } else if (date.getDay() === 6) {
            row.classList.add("saturday");
        }
        row.innerHTML =
            "<td>" + monthNumber + "/" + day + "</td>" +
            "<td>" + weekday + "</td>" +
            "<td>" + dayType + "</td>" +
            "<td><input type=\"number\" class=\"form-control daily-target-input\" min=\"0\"></td>";

        dailyTargetBody.appendChild(row);
    }
}

dailyTargetMonth.addEventListener("change", displayDailyTargets);

dailyTargetModal.addEventListener("show.bs.modal", function () {
    dailyTargetMonth.value = targetMonth.value;
    displayDailyTargets();
});

dailyTargetModal.addEventListener("hidden.bs.modal", function () {
    dailyTargetMonth.value = "";
    dailyTargetBody.innerHTML = "";
});

