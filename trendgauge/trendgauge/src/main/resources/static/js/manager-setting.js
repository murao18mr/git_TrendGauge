const targetMonth = document.getElementById("target-month");
const dailyTargetBody = document.getElementById("daily-target-body");
const dailyTargetMonth = document.getElementById("daily-target-month");
const dailyTargetModal = document.getElementById("daily-target-modal");
const targetAmount = document.getElementById("target-amount");
const lastYearRatio = document.getElementById("last-year-ratio");
const allocationPattern = document.getElementById("allocation-pattern");
const saveDailyTargetButton = document.getElementById("save-daily-target-button");
const dailyTargetMonthError = document.getElementById("daily-target-month-error");

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

let dailyTargetAmount = null;

function displayDailyTargets() {
    const month = dailyTargetMonth.value;
    const pattern = allocationPattern.value;

    dailyTargetBody.innerHTML = "";

    if (!month) {
        return;
    }

    if (!dailyTargetAmount) {
        dailyTargetBody.innerHTML =
            "<tr><td colspan=\"4\">月間予算が設定されていません</td></tr>";
        return;
    }

    if (!pattern) {
        dailyTargetBody.innerHTML =
            "<tr><td colspan=\"4\">配分パターンを選択してください</td></tr>";
        return;
    }

    const [year, monthNumber] = month.split("-").map(Number);
    const lastDay = new Date(year, monthNumber, 0).getDate();

    let totalWeight = 0;

    for (let day = 1; day <= lastDay; day++) {
        const date = new Date(year, monthNumber - 1, day);
        const dayOfWeek = date.getDay();

        if (pattern === "weekend" && (dayOfWeek === 0 || dayOfWeek === 6)) {
            totalWeight += 1.5;
        } else {
            totalWeight += 1;
        }
    }

    const baseAmount = dailyTargetAmount / totalWeight;

    for (let day = 1; day <= lastDay; day++) {
        const date = new Date(year, monthNumber - 1, day);
        const dayOfWeek = date.getDay();

        const weekday = ["日", "月", "火", "水", "木", "金", "土"][dayOfWeek];
        const dayType = dayOfWeek === 0 ? "日曜" : dayOfWeek === 6 ? "土曜" : "平日";

        let dailyAmount = baseAmount;

        if (pattern === "weekend" && (dayOfWeek === 0 || dayOfWeek === 6)) {
            dailyAmount = baseAmount * 1.5;
        }

        const saleDate = year + "-" +
            String(monthNumber).padStart(2, "0") + "-" +
            String(day).padStart(2, "0");

        const savedTarget = savedDailyTargets.find(function (target) {
            return target.saleDate === saleDate;
        });

        if (savedTarget) {
            dailyAmount = savedTarget.targetAmount;
        }

        const row = document.createElement("tr");

        if (dayOfWeek === 0) {
            row.classList.add("sunday");
        } else if (dayOfWeek === 6) {
            row.classList.add("saturday");
        }

        row.innerHTML =
            "<td>" + monthNumber + "/" + day + "</td>" +
            "<td>" + weekday + "</td>" +
            "<td>" + dayType + "</td>" +
            "<td><input type=\"number\" class=\"form-control daily-target-input\" min=\"0\" value=\"" +
            Math.round(dailyAmount) + "\"></td>";

        dailyTargetBody.appendChild(row);
    }
}

function loadDailyTarget() {
    const month = dailyTargetMonth.value;

    dailyTargetAmount = null;
    savedDailyTargets = [];
    dailyTargetBody.innerHTML = "";

    if (!month) {
        return;
    }

    fetch("/manager/setting/target?targetMonth=" + encodeURIComponent(month))
        .then(function (response) {
            if (response.status === 204) {
                return null;
            }
            return response.json();
        })
        .then(function (target) {
            dailyTargetAmount = target ? target.targetAmount : null;

            return fetch("/manager/setting/daily-target?targetMonth=" +
                encodeURIComponent(month));
        })
        .then(function (response) {
            return response.json();
        })
        .then(function (dailyTargets) {
            savedDailyTargets = dailyTargets;
            displayDailyTargets();
        });
}

dailyTargetMonth.addEventListener("change", loadDailyTarget);

allocationPattern.addEventListener("change", function () {
    displayDailyTargets();
});

dailyTargetModal.addEventListener("show.bs.modal", function () {
    dailyTargetMonth.value = targetMonth.value;
    allocationPattern.value = "";
    dailyTargetAmount = null;
    loadDailyTarget();
});

dailyTargetModal.addEventListener("hidden.bs.modal", function () {
    dailyTargetMonth.value = "";
    allocationPattern.value = "";
    dailyTargetBody.innerHTML = "";
    dailyTargetAmount = null;
});

saveDailyTargetButton.addEventListener("click", function () {
    const month = dailyTargetMonth.value;

    if (!month) {
        dailyTargetMonthError.textContent = "対象年月を選択してください";
        return;
    }

    dailyTargetMonthError.textContent = "";

    const inputs = [];
    const dailyInputs = document.querySelectorAll(".daily-target-input");

    const [year, monthNumber] = month.split("-").map(Number);

    dailyInputs.forEach(function (input, index) {
        const day = index + 1;
        const saleDate = year + "-" +
            String(monthNumber).padStart(2, "0") + "-" +
            String(day).padStart(2, "0");

        inputs.push({
            saleDate: saleDate,
            targetAmount: Number(input.value)
        });
    });

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    fetch("/manager/setting/daily-target", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(inputs)
    })
        .then(function (response) {
            if (!response.ok) {
                throw new Error("保存に失敗しました");
            }

            const toast = document.getElementById("success-toast");
            const toastBody = document.getElementById("success-toast-body");

            toastBody.textContent = "日割り予算を保存しました。";
            new bootstrap.Toast(toast).show();

            const modal = bootstrap.Modal.getInstance(dailyTargetModal);
            modal.hide();
        })
        .catch(function (error) {
            alert(error.message);
        });
});
