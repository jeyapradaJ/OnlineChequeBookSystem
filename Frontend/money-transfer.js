const API_URL = "http://localhost:8081/api";

const customerId = localStorage.getItem("customerId");

if (!customerId) {
    window.location.href = "index.html";
}


// Load customer account
async function loadAccount() {

    try {

        const response = await fetch(
            `${API_URL}/customers/${customerId}/dashboard`
        );

        if (!response.ok) {
            throw new Error("Unable to load account");
        }

        const data = await response.json();

        if (!data.accounts || data.accounts.length === 0) {
            alert("No bank account found.");
            return;
        }

        const account = data.accounts[0];

        localStorage.setItem(
            "selectedAccountId",
            account.id
        );

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

    } catch (error) {

        console.error(error);

        alert("Unable to load account details.");
    }
}


// Money transfer
document.getElementById(
    "transferForm"
).addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const accountId =
            localStorage.getItem(
                "selectedAccountId"
            );

        const beneficiaryAccount =
            document.getElementById(
                "beneficiaryAccount"
            ).value.trim();

        const amount =
            Number(
                document.getElementById(
                    "transferAmount"
                ).value
            );

        const message =
            document.getElementById(
                "transferMessage"
            );


        if (!accountId) {
            message.textContent =
                "Account not found.";
            return;
        }


        if (!beneficiaryAccount) {
            message.textContent =
                "Please enter beneficiary account number.";
            return;
        }


        if (amount <= 0) {
            message.textContent =
                "Please enter a valid amount.";
            return;
        }


        try {

            const response = await fetch(
                `${API_URL}/transactions` +
                `?accountId=${accountId}` +
                `&transactionType=MONEY_TRANSFER` +
                `&amount=${amount}`,
                {
                    method: "POST"
                }
            );


            const result =
                await response.text();


            if (!response.ok) {

                message.textContent =
                    result;

                return;
            }


            message.textContent =
                "Money transferred successfully!";


            document.getElementById(
                "transferForm"
            ).reset();


            // Reload updated account balance

            await loadAccount();


        } catch (error) {

            console.error(error);

            message.textContent =
                "Unable to complete the transfer.";
        }

    }
);


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


// Load account

loadAccount();