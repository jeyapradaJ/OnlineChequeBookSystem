const API_URL = "http://localhost:8081/api";


// =========================
// Admin Login
// =========================

document.getElementById("adminLoginForm").addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        const email =
            document.getElementById("adminEmail").value;

        const password =
            document.getElementById("adminPassword").value;

        const message =
            document.getElementById("adminLoginMessage");


        const loginData = {

            email: email,

            password: password
        };


        try {

            const response = await fetch(
                `${API_URL}/admin/login`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(loginData)
                }
            );


            const result =
                await response.json();


            // =========================
            // Successful Login
            // =========================

            if (response.ok) {

                message.textContent =
                    "Admin login successful!";

                message.style.color = "green";


                // Store admin information
                localStorage.setItem(
                    "adminId",
                    result.id
                );

                localStorage.setItem(
                    "adminName",
                    result.name
                );

                localStorage.setItem(
                    "adminEmail",
                    result.email
                );


                // Open Admin Dashboard
                setTimeout(function () {

                    window.location.href =
                        "admin-dashboard.html";

                }, 500);


            }

            // =========================
            // Login Failed
            // =========================

            else {

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