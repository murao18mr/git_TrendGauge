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

const tabButtons = document.querySelectorAll(".tab-button");

function activeTab(button) {
    tabButtons.forEach(function (tab) {
        tab.classList.remove("active");
    });
    button.classList.add("active");
}

ratioButton.addEventListener("click", function () {
    activeTab(ratioButton);
    showArea(ratioArea);
    loadRanking("/main/ratio", "ratioRankingChart", ratioRankingChart);
});

budgetButton.addEventListener("click", function () {
    activeTab(budgetButton);
    showArea(budgetArea);
    loadRanking("/main/budget", "budgetRankingChart", budgetRankingChart);
});

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

if (view === "sales") {
    showArea(salesArea);
}

if (view === "category") {
    showArea(categoryArea);
}

if (view === "color") {
    showArea(colorArea);
}


let ratioRankingChart;
let budgetRankingChart;

function loadRanking(url, canvasId, chartInstance) {
    fetch(url)
        .then(response => response.json())
        .then(data => {
            const chartHeight = Math.max(data.length * 45, 430);
            const emptyRows = Math.max(Math.ceil(chartHeight / 45) - data.length, 0);

            const labels = data.map(function (store, index) {
                return (index + 1) + "位  " + store.storeName;
            });

            const values = data.map(function (store) {
                return store.ratio;
            });

            for (let i = 0; i < emptyRows; i++) {
                labels.push("");
                values.push(null);
            }

            const canvas = document.getElementById(canvasId);

            const chartContainer = canvas.parentElement;
            chartContainer.style.height = chartHeight + "px";

            if (chartInstance) {
                chartInstance.destroy();
            }
            const chart = new Chart(canvas, {
                type: "bar",
                plugins: [ChartDataLabels],
                data: {
                    labels: labels,
                    datasets: [
                        {
                            data: values,
                            backgroundColor: data.map(function (store, index) {
                                if (index === 0) {
                                    return "#D4AF37";
                                }

                                if (index === 1) {
                                    return "#B8B8B8";
                                }

                                if (index === 2) {
                                    return "#C58B4A";
                                }

                                return "#D9D9D9";
                            }),
                            borderWidth: 0,
                            barPercentage: 0.7,
                            categoryPercentage: 0.8
                        }
                    ]
                },
                options: {
                    indexAxis: "y",
                    responsive: true,
                    maintainAspectRatio: false,
                    animation: false,
                    plugins: {
                        legend: {
                            display: false
                        },
                        tooltip: {
                            callbacks: {
                                label: function (context) {
                                    return context.raw + "%";
                                }
                            }
                        },
                        annotation: {
                            annotations: {
                                line100: {
                                    type: "line",
                                    xMin: 100,
                                    xMax: 100,
                                    borderWidth: 2
                                }
                            }
                        },
                        datalabels: {
                            anchor: "end",
                            align: "right",
                            formatter: function (value) {
                                if (value === null) {
                                    return "-";
                                }
                                return value + "%";
                            }
                        }
                    },
                    scales: {
                        x: {
                            beginAtZero: true,
                            suggestedMax: 130,
                            ticks: {
                                callback: function (value) {
                                    return value + "%";
                                }
                            }
                        },
                        y: {
                            ticks: {
                                font: {
                                    weight: "bold",
                                    size: 14
                                }
                            }
                        }
                    }
                }
            });

            return chart;
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

loadRanking("/main/ratio", "ratioRankingChart", ratioRankingChart);
activeTab(ratioButton);

setupChart(salesArea, loadSalesChart);
setupChart(categoryArea, loadCategoryChart);
setupChart(colorArea, loadColorChart);

