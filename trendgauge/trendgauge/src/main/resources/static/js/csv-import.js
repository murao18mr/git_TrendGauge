const csvFile = document.getElementById("csv-file");
const importButton = document.getElementById("import-button");

csvFile.addEventListener("change", async function () {
    const file = csvFile.files[0];
    if (!file) {
        return;
    }
    importButton.disabled = true;

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

    const previewRows = await response.json();
    const hasError = previewRows.some(function (row) {
        return row.errorMessage !== null;
    });

    importButton.disabled = hasError;

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
});