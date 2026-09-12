const ratioButton = document.getElementById("ratio");
const budgetButton = document.getElementById("budget");
const salesButton = document.getElementById("sales");
const categoryButton = document.getElementById("category");
const colorButton = document.getElementById("color");

const ratioArea = document.getElementById("ratio-ranking-area");
const budgetArea = document.getElementById("budget-ranking-area");
const salesArea = document.getElementById("sales-area");
const categoryArea = document.getElementById("category-area");
const colorArea = document.getElementById("color-area");

const ratioRankingList = document.getElementById("ratio-ranking-list");
const budgetRankingList = document.getElementById("budget-ranking-list");

budgetArea.style.display = "none";
salesArea.style.display = "none";
categoryArea.style.display = "none";
colorArea.style.display = "none";

function showArea(area) {
    ratioArea.style.display = "none";
    budgetArea.style.display = "none";
    salesArea.style.display = "none";
    categoryArea.style.display = "none";
    colorArea.style.display = "none";

    area.style.display = "block";
}

ratioButton.addEventListener("click", function () {
    showArea(ratioArea);
    loadRanking("/main/ratio", ratioRankingList);
});

budgetButton.addEventListener("click", function () {
    showArea(budgetArea);
    loadRanking("/main/budget", budgetRankingList);
});

salesButton.addEventListener("click", function () {
    showArea(salesArea);
    loadSalesChart(currentDate);
});

categoryButton.addEventListener("click", function () {
    showArea(categoryArea);
    loadCategoryChart(currentDate);
});

colorButton.addEventListener("click", function () {
    showArea(colorArea);
    loadColorChart(currentDate);
});

if (view === "sales") {
    showArea(salesArea);
}

if (view === "category") {
    showArea(categoryArea);
}

if (view === "color") {
    showArea(colorArea);
}

function loadRanking(url, listArea) {
    fetch(url)
        .then(response => response.json())
        .then(data => {
            listArea.innerHTML = "";

            data.forEach(function (store, index) {
                const listItem = document.createElement("div");
                listItem.className = "list-group-item d-flex justify-content-between align-items-center";

                const storeInfo = document.createElement("div");

                const rank = document.createElement("span");
                rank.className = "fw-bold me-2";
                rank.textContent = index + 1;

                const storeName = document.createElement("span");
                storeName.textContent = store.storeName;

                storeInfo.appendChild(rank);
                storeInfo.appendChild(storeName);

                const ratio = document.createElement("span");
                ratio.textContent =
                    store.ratio !== null ? store.ratio + "%" : "-";

                listItem.appendChild(storeInfo);
                listItem.appendChild(ratio);

                listArea.appendChild(listItem);
            });
        });
}


let salesTrendChart;
let categoryChart;
let colorChart;
let currentDate = baseDate;

function getTodayString() {
    const today = new Date();

    return today.getFullYear() + "-" +
        String(today.getMonth() + 1).padStart(2, "0") + "-" +
        String(today.getDate()).padStart(2, "0");
}

function updateWeekDisplay(dateString) {
    const date = new Date(dateString + "T00:00:00");

    const day = date.getDay();
    const diff = day === 0 ? -6 : 1 - day;

    date.setDate(date.getDate() + diff);
    const weekStart =
        date.getFullYear() + "-" +
        String(date.getMonth() + 1).padStart(2, "0") + "-" +
        String(date.getDate()).padStart(2, "0");

    date.setDate(date.getDate() + 6);
    const weekEnd =
        date.getFullYear() + "-" +
        String(date.getMonth() + 1).padStart(2, "0") + "-" +
        String(date.getDate()).padStart(2, "0");

    document.querySelectorAll(".week-start").forEach(function (element) {
        element.textContent = weekStart;
    });
    document.querySelectorAll(".week-end").forEach(function (element) {
        element.textContent = weekEnd;
    });
}

function weekNavigation(area, loadChart) {
    const previousWeek = area.querySelector(".previous-week");
    const nextWeek = area.querySelector(".next-week");

    previousWeek.addEventListener("click", function () {
        const date = new Date(currentDate + "T00:00:00");
        date.setDate(date.getDate() - 7);

        currentDate =
            date.getFullYear() + "-" +
            String(date.getMonth() + 1).padStart(2, "0") + "-" +
            String(date.getDate()).padStart(2, "0");

        nextWeek.disabled = false;
        loadChart(currentDate);
    });

    nextWeek.addEventListener("click", function () {
        const date = new Date(currentDate + "T00:00:00");
        date.setDate(date.getDate() + 7);

        const nextDate =
            date.getFullYear() + "-" +
            String(date.getMonth() + 1).padStart(2, "0") +
            "-" +
            String(date.getDate()).padStart(2, "0");

        const todayString = getTodayString();

        if (nextDate > todayString) {
            nextWeek.disabled = true;
            return;
        }
        nextWeek.disabled = false;
        currentDate = nextDate;
        loadChart(currentDate);
    });
}

function loadSalesChart(date) {
    fetch("/main/sales?date=" + date)
        .then(response => response.json())
        .then(data => {
            const chartLabels = data.map(sale => sale.date);
            const chartData = data.map(sale => sale.amount);
            updateWeekDisplay(date);

            if (salesTrendChart) {
                salesTrendChart.destroy();
            }
            salesTrendChart = new Chart(
                document.getElementById("salesTrendChart"),
                {
                    type: "line",
                    data: {
                        labels: chartLabels,
                        datasets: [
                            {
                                label: "売上",
                                data: chartData
                            }
                        ]
                    },
                    options: {
                        responsive: true
                    }
                }
            );
        });
}

function loadCategoryChart(date) {
    fetch("/main/category?date=" + date)
        .then(response => response.json())
        .then(data => {
            const chartLabels = data.map(category => category.categoryName);
            const chartData = data.map(category => category.amount);
            updateWeekDisplay(date);

            if (categoryChart) {
                categoryChart.destroy();
            }

            categoryChart = new Chart(
                document.getElementById("categorySalesChart"),
                {
                    type: "bar",
                    data: {
                        labels: chartLabels,
                        datasets: [
                            {
                                label: "部門別売上",
                                data: chartData
                            }
                        ]
                    },
                    options: {
                        responsive: true
                    }
                }
            );
        });
}

function loadColorChart(date) {
    fetch("/main/color?date=" + date)
        .then(response => response.json())
        .then(data => {
            const chartLabels = data.map(color => color.colorName);
            const chartData = data.map(color => color.amount);
            updateWeekDisplay(date);

            if (colorChart) {
                colorChart.destroy();
            }

            colorChart = new Chart(
                document.getElementById("colorSalesChart"),
                {
                    type: "bar",
                    data: {
                        labels: chartLabels,
                        datasets: [
                            {
                                label: "カラー別売上",
                                data: chartData
                            }
                        ]
                    },
                    options: {
                        responsive: true
                    }
                }
            );
        });
}

loadRanking("/main/ratio", ratioRankingList);

function setupChart(area, loadChart) {
    loadChart(currentDate);
    weekNavigation(area, loadChart);
}

setupChart(salesArea, loadSalesChart);
setupChart(categoryArea, loadCategoryChart);
setupChart(colorArea, loadColorChart);