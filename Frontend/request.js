const API_URL = "http://localhost:8081/api";

const customerId = localStorage.getItem("customerId");

// If customer is not logged in
if (!customerId) {
    window.location.href = "index.html";
}


// Load customer's bank accounts
async function loadAccounts() {

    try {

        const response = await fetch(
            `${API_URL}/accounts/customer/${customerId}`
        );

        if (!response.ok) {
            throw new Error("Unable to load accounts");
        }

        const accounts = await response.json();

        const accountSelect =
            document.getElementById("accountSelect");

        accountSelect.innerHTML =
            '<option value="">Select your account</option>';

        accounts.forEach(function (account) {

            const option = document.createElement("option");

            option.value = account.id;

            option.textContent =
                account.accountNumber +
                " - " +
                account.accountType;

            accountSelect.appendChild(option);
        });

        if (accounts.length === 0) {

            accountSelect.innerHTML =
                '<option value="">No bank account available</option>';
        }

    } catch (error) {

        console.error(error);

        document.getElementById("accountSelect").innerHTML =
            '<option value="">Unable to load account</option>';
    }
}


// Submit cheque book request
document.getElementById("requestForm").addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const accountId =
            document.getElementById("accountSelect").value;

        const numberOfLeaves =
            Number(document.getElementById("leavesSelect").value);

        const message =
            document.getElementById("requestMessage");


        const requestData = {

            accountId: Number(accountId),

            numberOfLeaves: numberOfLeaves
        };


        try {

            const response = await fetch(
                `${API_URL}/cheque-requests`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(requestData)
                }
            );


            const result = await response.json();


            if (response.ok) {

                message.textContent =
                    "Cheque book request submitted successfully!";

                message.style.color = "green";

                document.getElementById("requestForm").reset();

            } else {

                message.textContent =
                    result;

                message.style.color = "red";
            }

        } catch (error) {

            console.error(error);

            message.textContent =
                "Unable to connect to the server.";

            message.style.color = "red";
        }
    }
);


// Back to dashboard
document.getElementById("backBtn").addEventListener(
    "click",
    function () {

        window.location.href =
            "customer-dashboard.html";
    }
);


// Load accounts when page opens
loadAccounts();