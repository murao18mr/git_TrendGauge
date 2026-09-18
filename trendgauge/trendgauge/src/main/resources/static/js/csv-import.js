const csvFile = document.getElementById("csv-file");
const importButton = document.getElementById("import-button");
const mappingArea = document.getElementById("mapping-area");
const mappingFields = document.getElementById("mapping-fields");
const form = document.querySelector("form");

function checkMapping() {
    const selects = mappingFields.querySelectorAll("select");

    let dateCount = 0;
    let amountCount = 0;

    selects.forEach(function (select) {
        if (select.value === "date") {
            dateCount++;
        }

        if (select.value === "amount") {
            amountCount++;
        }
    });

    return dateCount === 1 && amountCount === 1;
}

async function previewWithMapping() {
    const file = csvFile.files[0];

    if (!file || !checkMapping()) {
        importButton.disabled = true;
        return;
    }

    const formData = new FormData();
    formData.append("csvFile", file);

    const selects = mappingFields.querySelectorAll("select");

    selects.forEach(function (select) {
        formData.append("mappingColumns", select.value);
    });

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch("/sales/preview", {
        method: "POST",
        headers: {
            [csrfHeader]: csrfToken
        },
        body: formData
    });

    const result = await response.json();

    displayPreview(result.rows);

    const hasError = result.rows.some(function (row) {
        return row.errorMessage !== null;
    });

    importButton.disabled = hasError;
}

csvFile.addEventListener("change", async function () {
    const file = csvFile.files[0];

    if (!file) {
        return;
    }

    importButton.disabled = true;
    mappingArea.classList.add("d-none");

    const formData = new FormData();
    formData.append("csvFile", file);

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch("/sales/preview", {
        method: "POST",
        headers: {
            [csrfHeader]: csrfToken
        },
        body: formData
    });

    const result = await response.json();
    const previewRows = result.rows;

    if (result.mappingRequired) {
        mappingArea.classList.remove("d-none");

        mappingFields.innerHTML = "";

        for (let i = 0; i < result.columnCount; i++) {
            const field = document.createElement("div");
            field.classList.add("mb-3");

            const label = document.createElement("label");
            label.classList.add("form-label");
            label.textContent = "CSV列" + (i + 1);

            const select = document.createElement("select");
            select.classList.add("form-select");

            const defaultOption = document.createElement("option");
            defaultOption.value = "";
            defaultOption.textContent = "選択してください";

            select.appendChild(defaultOption);

            const options = [
                ["date", "日付"],
                ["amount", "日次売上金額"],
                ["customerCount", "客数"],
                ["category", "カテゴリ"],
                ["color", "カラー"],
                ["quantity", "数量"],
                ["subtotal", "小計"],
                ["skip", "対象外（スキップ）"]
            ];

            options.forEach(function (optionData) {
                const option = document.createElement("option");
                option.value = optionData[0];
                option.textContent = optionData[1];
                select.appendChild(option);
            });

            select.addEventListener("change", function () {
                if (checkMapping()) {
                    previewWithMapping();
                } else {
                    importButton.disabled = true;
                    displayPreview([]);
                }
            });

            field.appendChild(label);
            field.appendChild(select);
            mappingFields.appendChild(field);
        }

        importButton.disabled = !checkMapping();
    } else {
        const hasError = previewRows.some(function (row) {
            return row.errorMessage !== null;
        });

        importButton.disabled = hasError;
    }

    displayPreview(previewRows);
});

function displayPreview(previewRows) {
    const previewBody = document.getElementById("preview-body");

    previewBody.innerHTML = "";

    previewRows.forEach(function (row) {
        const tr = document.createElement("tr");

        if (row.errorMessage !== null) {
            tr.classList.add("table-danger");
        }

        const rowNumber = document.createElement("td");
        rowNumber.textContent = row.rowNumber;

        const saleDate = document.createElement("td");
        saleDate.textContent = row.saleDate ?? "";

        const amount = document.createElement("td");
        amount.textContent = row.amount ?? "";

        const customerCount = document.createElement("td");
        customerCount.textContent = row.customerCount ?? "";

        const category = document.createElement("td");
        category.textContent = row.category ?? "";

        const color = document.createElement("td");
        color.textContent = row.color ?? "";

        const quantity = document.createElement("td");
        quantity.textContent = row.quantity ?? "";

        const subtotal = document.createElement("td");
        subtotal.textContent = row.subtotal ?? "";

        const errorMessage = document.createElement("td");
        errorMessage.textContent = row.errorMessage ?? "";

        tr.appendChild(rowNumber);
        tr.appendChild(saleDate);
        tr.appendChild(amount);
        tr.appendChild(customerCount);
        tr.appendChild(category);
        tr.appendChild(color);
        tr.appendChild(quantity);
        tr.appendChild(subtotal);
        tr.appendChild(errorMessage);

        previewBody.appendChild(tr);
    });
}

function setMappingHidden() {
    const hiddenFields = document.getElementById("mapping-hidden-fields");
    const selects = mappingFields.querySelectorAll("select");

    hiddenFields.innerHTML = "";

    selects.forEach(function (select) {
        const input = document.createElement("input");

        input.type = "hidden";
        input.name = "mappingColumns";
        input.value = select.value;

        hiddenFields.appendChild(input);
    });
}

form.addEventListener("submit", function () {
    if (mappingArea.classList.contains("d-none")) {
        return;
    }

    setMappingHidden();
});