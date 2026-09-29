const salesButton = document.getElementById("sales");
const categoryButton = document.getElementById("category");
const colorButton = document.getElementById("color");

const salesArea = document.getElementById("sales-area");
const categoryArea = document.getElementById("category-area");
const colorArea = document.getElementById("color-area");

categoryArea.style.display = "none";
colorArea.style.display = "none";

function showArea(area) {
    salesArea.style.display = "none";
    categoryArea.style.display = "none";
    colorArea.style.display = "none";

    area.style.display = "block";
}

const tabButtons = document.querySelectorAll(".tab-button");

function activeTab(button) {
    tabButtons.forEach(function (tab) {
        tab.classList.remove("active");
    });
    button.classList.add("active");
}

salesButton.addEventListener("click", function () {
    activeTab(salesButton);
    showArea(salesArea);
    loadSalesChart(currentDate);
});

categoryButton.addEventListener("click", function () {
    activeTab(categoryButton);
    showArea(categoryArea);
    loadCategoryChart(currentDate);
});

colorButton.addEventListener("click", function () {
    activeTab(colorButton);
    showArea(colorArea);
    loadColorChart(currentDate);
});


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

function updateNextWeekButton(nextWeek) {
    const date = new Date(currentDate + "T00:00:00");
    date.setDate(date.getDate() + 7);

    const nextDate =
        date.getFullYear() + "-" +
        String(date.getMonth() + 1).padStart(2, "0") + "-" +
        String(date.getDate()).padStart(2, "0");

    nextWeek.disabled = nextDate > getTodayString();
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

        updateNextWeekButton(nextWeek);
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

        currentDate = nextDate;
        updateNextWeekButton(nextWeek);
        loadChart(currentDate);
    });
}

function loadSalesChart(date) {
    fetch("/admin/store-detail/sales?storeId=" + storeId + "&date=" + date)
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
                        responsive: true,
                        animation: false,
                        plugins: {
                            legend: {
                                display: false
                            }
                        }
                    }
                }
            );
        });
}

function loadCategoryChart(date) {
    fetch("/admin/store-detail/category?storeId=" + storeId + "&date=" + date)
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
                                data: chartData,
                                maxBarThickness: 70
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        animation: false,
                        plugins: {
                            legend: {
                                display: false
                            },
                            datalabels: {
                                anchor: "end",
                                align: "top",
                                formatter: function (value) {
                                    return value.toLocaleString() + "円";
                                },
                                font: {
                                    weight: "bold"
                                }
                            }
                        }
                    }
                }
            );
        });
}

function loadColorChart(date) {
    fetch("/admin/store-detail/color?storeId=" + storeId + "&date=" + date)
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
                                data: chartData,
                                maxBarThickness: 70
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        animation: false,
                        plugins: {
                            legend: {
                                display: false
                            },
                            datalabels: {
                                anchor: "end",
                                align: "top",
                                formatter: function (value) {
                                    return value.toLocaleString() + "円";
                                },
                                font: {
                                    weight: "bold"
                                }
                            }
                        }
                    }
                }
            );
        });
}

function setupChart(area, loadChart) {
    loadChart(currentDate);
    weekNavigation(area, loadChart);
    updateNextWeekButton(area.querySelector(".next-week"));
}

activeTab(salesButton);
setupChart(salesArea, loadSalesChart);
setupChart(categoryArea, loadCategoryChart);
setupChart(colorArea, loadColorChart);