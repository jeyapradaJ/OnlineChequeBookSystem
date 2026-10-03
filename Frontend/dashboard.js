const API_URL = "https://onlinechequebooksystem.onrender.com/api";

const customerId = localStorage.getItem("customerId");

if (!customerId) {
   window.location.href = "customer-login.html";
}


// =========================
// Load Customer Dashboard
// =========================

async function loadDashboard() {

    try {

        const response =
            await fetch(
                `${API_URL}/customers/${customerId}/dashboard`
            );

        if (!response.ok) {
            throw new Error(
                "Unable to load dashboard"
            );
        }

        const data =
            await response.json();


        // =========================
        // Customer Information
        // =========================

        document.getElementById(
            "customerName"
        ).textContent =
            data.name;

        document.getElementById(
            "customerFullName"
        ).textContent =
            data.name;

        document.getElementById(
            "customerEmail"
        ).textContent =
            data.email;

        document.getElementById(
            "customerPhone"
        ).textContent =
            data.phone;


        // =========================
        // Bank Account
        // =========================

        if (
            data.accounts &&
            data.accounts.length > 0
        ) {

            const account =
                data.accounts[0];

            document.getElementById(
                "accountNumber"
            ).textContent =
                account.accountNumber;

            document.getElementById(
                "accountType"
            ).textContent =
                account.accountType;

            document.getElementById(
                "accountBalance"
            ).textContent =
                "₹" +
                Number(
                    account.balance
                ).toLocaleString("en-IN");
        }


        // =========================
        // Cheque Book Request History
        // =========================

        const requestHistoryBody =
            document.getElementById(
                "requestHistoryBody"
            );

        const requests =
            data.chequeBookRequests || [];


        if (requests.length === 0) {

            requestHistoryBody.innerHTML = `
                <tr>
                    <td colspan="4">
                        No cheque book requests yet.
                    </td>
                </tr>
            `;

        } else {

            let html = "";

            requests.forEach(
                function (request) {

                    html += `
                        <tr>

                            <td>
                                ${request.id}
                            </td>

                            <td>
                                ${
                                    request.account
                                    ? request.account.accountNumber
                                    : "-"
                                }
                            </td>

                            <td>
                                ${request.numberOfLeaves}
                            </td>

                            <td>
                                <span class="status-badge">
                                    ${request.status}
                                </span>
                            </td>

                        </tr>
                    `;

                }
            );

            requestHistoryBody.innerHTML =
                html;
        }


    } catch (error) {

        console.error(error);

        alert(
            "Unable to load customer dashboard."
        );
    }
}


// =========================
// Logout
// =========================

document.getElementById(
    "logoutBtn"
).addEventListener(
    "click",
    function () {

        localStorage.removeItem(
            "customerId"
        );

        localStorage.removeItem(
            "customerName"
        );

        localStorage.removeItem(
            "customerEmail"
        );

        window.location.href =
            "index.html";
    }
);


// =========================
// Request New Cheque Book
// =========================

document.getElementById(
    "requestChequeBtn"
).addEventListener(
    "click",
    function () {

        window.location.href =
            "request.html";
    }
);


// =========================
// Start Dashboard
// =========================

loadDashboard();
document.getElementById("transactionsBtn").addEventListener(
    "click",
    function () {
        window.location.href = "transactions.html";
    }
);
document.getElementById("moneyTransferBtn").addEventListener(
    "click",
    function () {
        window.location.href = "money-transfer.html";
    }
);