const generateReportButton = document.getElementById("generate-report-button");
const reportPreview = document.getElementById("report-preview");
const reportComment = document.getElementById("report-comment");
const applyCommentButton = document.getElementById("apply-comment-button");
const copyButton = document.getElementById("copy-button");
const lineButton = document.getElementById("line-button");
const slackButton = document.getElementById("slack-button");

copyButton.disabled = true;
lineButton.disabled = true;
slackButton.disabled = true;
applyCommentButton.disabled = true;

generateReportButton.addEventListener("click", async () => {
    const response = await fetch("/manager/reports/generate");
    const report = await response.json();

    const storeName = report.storeName;
    const startDate = report.startDate;
    const endDate = report.endDate;
    const weeklySales = report.weeklySales;
    const weeklyBudgetRatio = report.weeklyBudgetRatio;
    const weeklyRatio = report.weeklyRatio;
    const topCategory = report.topCategory;

    reportPreview.value =
        `【${storeName} 週報】\n` +
        `対象期間：${startDate.substring(5).replace("-", "/")} ～ ${endDate.substring(5).replace("-", "/")}\n\n` +
        `■ 売上実績\n` +
        `週間売上：${weeklySales.toLocaleString()}円\n` +
        `今週の予算達成率：${weeklyBudgetRatio != null ? weeklyBudgetRatio.toFixed(1) + "%" : "未設定"}\n` +
        `昨年同週比：${weeklyRatio != null ? weeklyRatio.toFixed(1) + "%" : "比較データなし"}\n\n` +
        `■ 売れ筋\n` +
        `${topCategory != null ? `今週は「${topCategory}」がよく売れています。` : "売れ筋データなし"}`;

    copyButton.disabled = false;
    lineButton.disabled = false;
    slackButton.disabled = false;
    applyCommentButton.disabled = false;
});

applyCommentButton.addEventListener("click", () => {
    const comment = reportComment.value.trim();

    if (!comment) return;

    const currentReport = reportPreview.value;

    if (currentReport.includes("■ 店長コメント")) {
        reportPreview.value =
            currentReport.replace(
                /■ 店長コメント\n[\s\S]*$/,
                `■ 店長コメント\n${comment}`
            );
    } else {
        reportPreview.value =
            `${currentReport}\n\n` +
            `■ 店長コメント\n` +
            `${comment}`;
    }
});

copyButton.addEventListener("click", async () => {
    const reportText = reportPreview.value;

    if (!reportText.trim()) return;

    try {
        await navigator.clipboard.writeText(reportText);

        const toast = document.getElementById("success-toast");
        const toastBody = document.getElementById("success-toast-body");

        if (toast && toastBody) {
            toastBody.textContent = "週報をコピーしました";
            new bootstrap.Toast(toast).show();
        }
    } catch (error) {
        console.error("コピーに失敗しました", error);
    }
});

// LINE共有関係
lineButton.addEventListener("click", () => {
    const reportText = reportPreview.value;

    if (!reportText.trim()) return;

    const lineUrl = `https://line.me/R/share?text=${encodeURIComponent(reportText)}`;
    window.open(lineUrl, "_blank");
});

//Slack関係
slackButton.addEventListener("click", async () => {
    const reportText = reportPreview.value;

    if (!reportText.trim()) return;

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    try {
        const response = await fetch("/manager/reports/slack", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({
                reportText: reportText
            })
        });

        if (!response.ok) {
            throw new Error("Slack送信に失敗しました");
        }

        const toast = document.getElementById("success-toast");
        const toastBody = document.getElementById("success-toast-body");

        if (toast && toastBody) {
            toastBody.textContent = "週報をSlackへ送信しました";
            new bootstrap.Toast(toast).show();
        }

    } catch (error) {
        console.error("Slack送信に失敗しました", error);
    }
});