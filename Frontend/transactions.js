const API_URL = "http://localhost:8081/api";

// Get logged-in customer
const customerId = localStorage.getItem("customerId");

if (!customerId) {
    window.location.href = "index.html";
}


// Load customer account and transactions
async function loadTransactions() {

    try {

        // Get customer dashboard data
        const dashboardResponse =
            await fetch(
                `${API_URL}/customers/${customerId}/dashboard`
            );

        if (!dashboardResponse.ok) {
            throw new Error("Unable to load account");
        }

        const dashboardData =
            await dashboardResponse.json();


        // Check account
        if (!dashboardData.accounts ||
            dashboardData.accounts.length === 0) {

            alert("No bank account found.");
            return;
        }


        const account =
            dashboardData.accounts[0];

        const accountId =
            account.id;


        // Display account information

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
            Number(account.balance)
                .toLocaleString("en-IN");


        // Get transactions

        const transactionResponse =
            await fetch(
                `${API_URL}/transactions/account/${accountId}`
            );

        if (!transactionResponse.ok) {
            throw new Error(
                "Unable to load transactions"
            );
        }

        const transactions =
            await transactionResponse.json();


        const transactionBody =
            document.getElementById(
                "transactionBody"
            );


        // No transactions

        if (transactions.length === 0) {

            transactionBody.innerHTML = `
                <tr>
                    <td colspan="6">
                        No transactions yet.
                    </td>
                </tr>
            `;

            return;
        }


        // Display transactions

        let html = "";


        transactions.forEach(
            function (transaction) {

                const amount =
                    Number(transaction.amount)
                        .toLocaleString("en-IN");

                const balance =
                    Number(
                        transaction.balanceAfterTransaction
                    ).toLocaleString("en-IN");


                let date = "-";

                if (transaction.transactionDate) {

                    date =
                        new Date(
                            transaction.transactionDate
                        ).toLocaleString("en-IN");

                }


                html += `
                    <tr>

                        <td>
                            ${transaction.id}
                        </td>

                        <td>
                            ${transaction.transactionType}
                        </td>

                        <td>
                            ₹${amount}
                        </td>

                        <td>
                            ₹${balance}
                        </td>

                        <td>
                            ${transaction.status}
                        </td>

                        <td>
                            ${date}
                        </td>

                    </tr>
                `;

            }
        );


        transactionBody.innerHTML =
            html;


    } catch (error) {

        console.error(error);

        document.getElementById(
            "transactionBody"
        ).innerHTML = `
            <tr>
                <td colspan="6">
                    Unable to load transactions.
                </td>
            </tr>
        `;
    }
}


// Back to dashboard

document.getElementById(
    "backBtn"
).addEventListener(
    "click",
    function () {

        window.location.href =
            "customer-dashboard.html";

    }
);


// Load transactions

loadTransactions();