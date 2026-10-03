const API_URL = "https://onlinechequebooksystem.onrender.com/api";

// Get logged-in customer ID
const customerId = localStorage.getItem("customerId");

// Check login
if (!customerId) {
    window.location.href = "index.html";
}


// Bank Account Form
document.getElementById("bankAccountForm").addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const accountNumber =
            document.getElementById("accountNumber").value;

        const accountType =
            document.getElementById("accountType").value;

        const balance =
            Number(document.getElementById("balance").value);

        const message =
            document.getElementById("accountMessage");


        // Data to send to backend
        const accountData = {

            accountNumber: accountNumber,

            accountType: accountType,

            balance: balance,

            customerId: Number(customerId)
        };


        try {

            const response = await fetch(
                `${API_URL}/accounts`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(accountData)
                }
            );


            const result = await response.json();


            if (response.ok) {

                message.textContent =
                    "Bank account added successfully!";

                message.style.color = "green";


                // Go to dashboard after saving
                setTimeout(function () {

                    window.location.href =
                        "customer-dashboard.html";

                }, 1000);

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


// Logout
document.getElementById("logoutBtn").addEventListener(
    "click",
    function () {

        localStorage.removeItem("customerId");
        localStorage.removeItem("customerName");
        localStorage.removeItem("customerEmail");

        window.location.href = "index.html";
    }
);