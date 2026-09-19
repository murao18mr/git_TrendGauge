const memoDateError = document.getElementById("memo-date-error");
const memoCommentError = document.getElementById("memo-comment-error");

document.getElementById("memo-button").addEventListener("click", function () {

    const today = new Date();

    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");

    document.getElementById("memo-date").value = year + "-" + month + "-" + day;
});

document.getElementById("memo-register").addEventListener("click", function () {

    memoDateError.textContent = "";
    memoCommentError.textContent = "";

    const saleDate = document.getElementById("memo-date").value;
    const comment = document.getElementById("memo-comment").value;

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    fetch("/main/memo", {
        method: "POST", headers: {
            "Content-Type": "application/x-www-form-urlencoded", [csrfHeader]: csrfToken
        }, body: `saleDate=${saleDate}&comment=${comment}`
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errors => {
                    throw errors;
                });
            }
            return fetch("/main/memo");
        })
        .then(response => response.json())
        .then(data => {

            const memoList = document.querySelector(".memo-list");
            memoList.innerHTML = "";

            data.forEach(function (memo) {

                const card = document.createElement("div");
                card.className = "card mb-2";

                const cardBody = document.createElement("div");
                cardBody.className = "card-body py-2";

                const date = document.createElement("div");
                const createdAt = new Date(memo.createdAt);

                const year = createdAt.getFullYear();
                const month = String(createdAt.getMonth() + 1).padStart(2, "0");
                const day = String(createdAt.getDate()).padStart(2, "0");
                const hours = String(createdAt.getHours()).padStart(2, "0");
                const minutes = String(createdAt.getMinutes()).padStart(2, "0");

                date.textContent = year + "/" + month + "/" + day + " " + hours + ":" + minutes;

                const comment = document.createElement("div");
                comment.textContent = memo.comment;

                cardBody.appendChild(date);
                cardBody.appendChild(comment);
                card.appendChild(cardBody);
                memoList.appendChild(card);
            });
            const modal = bootstrap.Modal.getInstance(document.getElementById("memo-modal"));
            modal.hide();
        })
        .catch(errors => {

            if (errors.saleDate) {
                memoDateError.textContent = errors.saleDate;
            }

            if (errors.comment) {
                memoCommentError.textContent = errors.comment;
            }
        });
});

const memoModal = document.getElementById("memo-modal");

memoModal.addEventListener("hidden.bs.modal", function () {
    memoDateError.textContent = "";
    memoCommentError.textContent = "";
    document.getElementById("memo-comment").value = "";
});