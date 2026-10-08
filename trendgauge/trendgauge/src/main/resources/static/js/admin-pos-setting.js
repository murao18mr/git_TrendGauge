const clientSecretInput = document.getElementById("client-secret");
const toggleClientSecretButton = document.getElementById("toggle-client-secret");

toggleClientSecretButton.addEventListener("click", function () {
    if (clientSecretInput.type === "password") {
        clientSecretInput.type = "text";
        toggleClientSecretButton.textContent = "非表示";
    } else {
        clientSecretInput.type = "password";
        toggleClientSecretButton.textContent = "表示";
    }
});

const autoSyncOn = document.getElementById("auto-sync-on");
const autoSyncOff = document.getElementById("auto-sync-off");
const syncTimeArea = document.getElementById("sync-time-area");
const syncTime = document.getElementById("sync-time");

function updateSyncTimeVisibility() {
    if (autoSyncOn.checked) {
        syncTimeArea.classList.remove("d-none");
    } else {
        syncTimeArea.classList.add("d-none");
        syncTime.value = "02:00";
    }
}

autoSyncOn.addEventListener("change", updateSyncTimeVisibility);
autoSyncOff.addEventListener("change", updateSyncTimeVisibility);

updateSyncTimeVisibility();

const targetStore = document.getElementById("target-store");
const posType = document.getElementById("pos-type");
const contractId = document.getElementById("contract-id");
const clientId = document.getElementById("client-id");
const smaregiStoreId = document.getElementById("smaregi-store-id");
const saveSetting = document.getElementById("save-setting");
const connectionTest = document.getElementById("connection-test");
const connectionResult = document.getElementById("connection-result");

function clearSetting() {
    posType.value = "smaregi";
    contractId.value = "";
    clientId.value = "";
    smaregiStoreId.value = "";
    clientSecretInput.value = "";
    clientSecretInput.type = "password";
    toggleClientSecretButton.textContent = "表示";
    autoSyncOff.checked = true;
    syncTime.value = "02:00";
    updateSyncTimeVisibility();
}

targetStore.addEventListener("change", async function () {
    connectionResult.textContent = "";
    connectionResult.className = "";
    const storeId = targetStore.value;

    document.querySelectorAll(".error-message, .secret-message").forEach(function (error) {
        error.remove();
    });

    clearSetting();
    saveSetting.disabled = false;

    if (!storeId) {
        return;
    }

    saveSetting.disabled = true;

    try {
        const response = await fetch(`/admin/setting/pos/${storeId}`);

        if (!response.ok) {
            throw new Error("設定の取得に失敗しました");
        }

        const data = await response.json();

        if (targetStore.value !== storeId) {
            return;
        }

        posType.value = data.posType || "smaregi";
        contractId.value = data.contractId || "";
        clientId.value = data.clientId || "";
        smaregiStoreId.value = data.smaregiStoreId || "";
        autoSyncOn.checked = data.autoSync;
        autoSyncOff.checked = !data.autoSync;
        syncTime.value = data.syncTime || "02:00";

        updateSyncTimeVisibility();
        saveSetting.disabled = false;

    } catch (error) {
        if (targetStore.value === storeId) {
            alert("POS連携設定の取得に失敗しました。店舗を選択し直してください。");
        }
    }
});

if (targetStore.value) {
    targetStore.dispatchEvent(new Event("change"));
}

connectionTest.addEventListener("click", async function () {
    connectionTest.disabled = true;
    connectionResult.className = "text-secondary";
    connectionResult.textContent = "接続確認中...";

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const data = {
        storeId: targetStore.value || null,
        posType: posType.value,
        contractId: contractId.value,
        clientId: clientId.value,
        clientSecret: clientSecretInput.value,
        autoSync: false
    };

    try {
        const response = await fetch("/admin/setting/pos/test", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error("通信に失敗しました");
        }

        const message = await response.text();
        connectionResult.textContent = message;

        if (message === "接続に成功しました") {
            connectionResult.className = "text-success";
        } else {
            connectionResult.className = "text-danger";
        }

    } catch (error) {
        connectionResult.className = "text-danger";
        connectionResult.textContent = "接続テスト中にエラーが発生しました";

    } finally {
        connectionTest.disabled = false;
    }
});

const syncButton = document.getElementById("sync-button");

syncButton.addEventListener("click", async function () {

    const storeId = targetStore.value;

    if (!storeId) {
        alert("同期する店舗を選択してください");
        return;
    }

    syncButton.disabled = true;
    connectionResult.className = "text-secondary";
    connectionResult.textContent = "売上データを同期しています...";

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    try {
        const response = await fetch(
            `/admin/setting/pos/sync?storeId=${encodeURIComponent(storeId)}`,
            {
                method: "POST",
                headers: {
                    [csrfHeader]: csrfToken
                }
            }
        );

        if (!response.ok) {
            throw new Error("同期に失敗しました");
        }

        const message = await response.text();

        connectionResult.className = "text-success";
        connectionResult.textContent = message;

    } catch (error) {

        connectionResult.className = "text-danger";
        connectionResult.textContent = "売上データの同期に失敗しました";

    } finally {
        syncButton.disabled = false;
    }
});

