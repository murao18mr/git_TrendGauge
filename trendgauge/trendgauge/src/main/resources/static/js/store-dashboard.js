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

ratioButton.addEventListener("click", function () {
    ratioArea.style.display = "block";
    budgetArea.style.display = "none";
    salesArea.style.display = "none";
    categoryArea.style.display = "none";
    colorArea.style.display = "none";
});

budgetButton.addEventListener("click", function () {
    ratioArea.style.display = "none";
    budgetArea.style.display = "block";
    salesArea.style.display = "none";
    categoryArea.style.display = "none";
    colorArea.style.display = "none";
});

salesButton.addEventListener("click", function () {
    ratioArea.style.display = "none";
    budgetArea.style.display = "none";
    salesArea.style.display = "block";
    categoryArea.style.display = "none";
    colorArea.style.display = "none";
});

categoryButton.addEventListener("click", function () {
    ratioArea.style.display = "none";
    budgetArea.style.display = "none";
    salesArea.style.display = "none";
    categoryArea.style.display = "block";
    colorArea.style.display = "none";
});

colorButton.addEventListener("click", function () {
    ratioArea.style.display = "none";
    budgetArea.style.display = "none";
    salesArea.style.display = "none";
    categoryArea.style.display = "none";
    colorArea.style.display = "block";
});


const chartLabels = weeklySales.map(sale => sale.date);
const chartData = weeklySales.map(sale => sale.amount);

const salesTrendChart = new Chart(
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

const categoryLabels = categorySales.map(category => category.categoryName);
const categoryData = categorySales.map(category => category.amount);

const categorySalesChart = new Chart(
    document.getElementById("categorySalesChart"),
    {
        type: "bar",
        data: {
            labels: categoryLabels,
            datasets: [
                {
                    label: "売上",
                    data: categoryData
                }
            ]
        },
        options: {
            responsive: true
        }
    }
);

const colorLabels = colorSales.map(color => color.colorName);
const colorData = colorSales.map(color => color.amount);

const colorSalesChart = new Chart(
    document.getElementById("colorSalesChart"),
    {
        type: "bar",
        data: {
            labels: colorLabels,
            datasets: [
                {
                    label: "売上",
                    data: colorData
                }
            ]
        },
        options: {
            responsive: true
        }
    }
);