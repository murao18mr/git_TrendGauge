document.getElementById("back-to-top").addEventListener("click", function () {
    window.scrollTo({top: 0, behavior: "smooth"});
});

const salesButton = document.getElementById("sales");
const keywordButton = document.getElementById("keyword");
const salesArea = document.getElementById("sales-area");
const keywordArea = document.getElementById("keyword-area")

keywordArea.style.display = "none";

const tabButtons = document.querySelectorAll(".tab-button");

function activeTab(button) {
    tabButtons.forEach(function (tab) {
        tab.classList.remove("active");
    });
    button.classList.add("active");
}

function showArea(area) {
    salesArea.style.display = "none";
    keywordArea.style.display = "none";

    area.style.display = "block";
}

salesButton.addEventListener("click", function () {
    activeTab(salesButton);
    showArea(salesArea);
});

keywordButton.addEventListener("click", function () {
    activeTab(keywordButton);
    showArea(keywordArea);
})

activeTab(salesButton);

// 絞り込み検索
const searchButton = document.getElementById("search-button");
const clearSearchButton = document.getElementById("clear-search-button");
const salesRows = document.querySelectorAll("#sales-area tbody tr");
const noDataMessage = document.getElementById("no-data-message");

searchButton.addEventListener("click", function () {
    const dateFrom = document.getElementById("search-date-from").value;
    const dateTo = document.getElementById("search-date-to").value;
    const amountFrom = document.getElementById("search-amount-from").value;
    const amountTo = document.getElementById("search-amount-to").value;
    const customerFrom = document.getElementById("search-customer-from").value;
    const customerTo = document.getElementById("search-customer-to").value;
    const unitPriceFrom = document.getElementById("search-unit-price-from").value;
    const unitPriceTo = document.getElementById("search-unit-price-to").value;
    const weather = document.getElementById("search-weather").value;

    let visibleCount = 0;

    salesRows.forEach(function (row) {
        const saleDate = row.dataset.saleDate;
        const amount = row.dataset.amount ? Number(row.dataset.amount) : null;
        const customerCount = row.dataset.customerCount ? Number(row.dataset.customerCount) : null;
        const unitPrice = row.dataset.unitPrice ? Number(row.dataset.unitPrice) : null;
        const rowWeather = row.dataset.weather;

        let isMatch = true;

        if (dateFrom && saleDate < dateFrom) isMatch = false;
        if (dateTo && saleDate > dateTo) isMatch = false;

        if (amountFrom && (amount === null || amount < Number(amountFrom))) isMatch = false;
        if (amountTo && (amount === null || amount > Number(amountTo))) isMatch = false;

        if (customerFrom && (customerCount === null || customerCount < Number(customerFrom))) {
            isMatch = false;
        }
        if (customerTo && (customerCount === null || customerCount > Number(customerTo))) {
            isMatch = false;
        }

        if (unitPriceFrom && (unitPrice === null || unitPrice < Number(unitPriceFrom))) {
            isMatch = false;
        }

        if (unitPriceTo && (unitPrice === null || unitPrice > Number(unitPriceTo))) {
            isMatch = false;
        }

        if (weather && rowWeather !== weather) isMatch = false;

        row.style.display = isMatch ? "" : "none";

        if (isMatch) {
            visibleCount++;
        }
    });

    noDataMessage.style.display = visibleCount === 0 ? "block" : "none";
});

clearSearchButton.addEventListener("click", function () {
    document.getElementById("search-date-from").value = "";
    document.getElementById("search-date-to").value = "";
    document.getElementById("search-amount-from").value = "";
    document.getElementById("search-amount-to").value = "";
    document.getElementById("search-customer-from").value = "";
    document.getElementById("search-customer-to").value = "";
    document.getElementById("search-unit-price-from").value = "";
    document.getElementById("search-unit-price-to").value = "";
    document.getElementById("search-weather").value = "";

    salesRows.forEach(function (row) {
        row.style.display = "";
    });

    noDataMessage.style.display = "none";
});

//修正ボタン
const editButtons = document.querySelectorAll("#sales-area button[data-sale-id]");
const editModal = new bootstrap.Modal(document.getElementById("sales-edit-modal"));

editButtons.forEach(function (button) {
    button.addEventListener("click", function () {
        const row = button.closest("tr");
        const saleId = button.dataset.saleId;
        editModal._element.dataset.saleId = saleId;

        document.getElementById("edit-sale-date").value = row.dataset.saleDate;
        document.getElementById("edit-amount").value = row.dataset.amount || "";
        document.getElementById("edit-customer-count").value = row.dataset.customerCount || "";
        document.getElementById("edit-weather").value = row.dataset.weather || "";

        const reportArea = document.getElementById("edit-report");
        const memoArea = document.getElementById("edit-memo");
        reportArea.value = "";
        reportArea.dataset.reportExists = "false";
        memoArea.innerHTML = "";

        Promise.all([
            fetch("/manager/sales/" + saleId + "/report")
                .then(function (response) {
                    if (response.status === 204) {
                        return null;
                    }
                    return response.json();
                }),
            fetch("/manager/sales/" + saleId + "/memos")
                .then(response => response.json())
        ])
            .then(function ([report, memos]) {
                if (report) {
                    reportArea.value = report.summary || "";
                    reportArea.dataset.reportExists = "true";
                } else {
                    reportArea.dataset.reportExists = "false";
                }

                memos.forEach(function (memo) {
                    const textarea = document.createElement("textarea");
                    textarea.className = "form-control mb-2";
                    textarea.rows = 3;
                    textarea.value = memo.comment;
                    textarea.dataset.memoId = memo.id;
                    memoArea.appendChild(textarea);
                });

                editModal.show();
            });
    });
});

// メモ追加
const addMemoButton = document.getElementById("add-memo");
const memoArea = document.getElementById("edit-memo");

addMemoButton.addEventListener("click", function () {
    const textarea = document.createElement("textarea");
    textarea.className = "form-control mb-2";
    textarea.rows = 3;
    memoArea.appendChild(textarea);
});


const saveButton = document.getElementById("edit");
saveButton.addEventListener("click", function () {
    const saleId = editModal._element.dataset.saleId;
    const amount = document.getElementById("edit-amount").value;
    const customerCount = document.getElementById("edit-customer-count").value;
    const weather = document.getElementById("edit-weather").value;
    const reportArea = document.getElementById("edit-report");
    const report = reportArea.value;

    const reportExists = reportArea.dataset.reportExists === "true";

    if (reportExists && !report.trim()) {
        const result = confirm("空欄にした日次報告は削除されます。よろしいですか？");

        if (!result) {
            return;
        }
    }

    const memoTextareas = document.querySelectorAll("#edit-memo textarea");
    const memos = [];
    let deleteCancelled = false;

    memoTextareas.forEach(function (textarea) {
        const memoId = textarea.dataset.memoId || null;
        const comment = textarea.value;

        if (memoId && !comment.trim()) {
            const result = confirm("空欄にしたメモは削除されます。よろしいですか？");

            if (!result) {
                deleteCancelled = true;
                return;
            }
        }

        memos.push({
            memoId: memoId,
            comment: comment
        });
    });

    if (deleteCancelled) {
        return;
    }

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const data = {
        saleId: saleId,
        amount: amount ? Number(amount) : null,
        customerCount: customerCount ? Number(customerCount) : null,
        weather: weather,
        report: report,
        memos: memos
    };

    fetch("/manager/sales/edit", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(data)
    })
        .then(function (response) {
            if (!response.ok) {
                throw new Error("保存に失敗しました。");
            }

            editModal.hide();
            location.reload();
        })
        .catch(function (error) {
            console.error(error);
        });
});