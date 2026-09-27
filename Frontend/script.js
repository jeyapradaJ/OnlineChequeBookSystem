const API_URL = "http://localhost:8081/api";


// =========================
// Login and Register box switching
// =========================

const loginBox = document.getElementById("loginBox");
const registerBox = document.getElementById("registerBox");

const showRegister = document.getElementById("showRegister");
const showLogin = document.getElementById("showLogin");


showRegister.addEventListener("click", function (event) {

    event.preventDefault();

    loginBox.classList.add("hidden");
    registerBox.classList.remove("hidden");

});


showLogin.addEventListener("click", function (event) {

    event.preventDefault();

    registerBox.classList.add("hidden");
    loginBox.classList.remove("hidden");

});


// =========================
// Customer Registration
// =========================

document.getElementById("registerForm").addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const name =
            document.getElementById("registerName").value;

        const email =
            document.getElementById("registerEmail").value;

        const phone =
            document.getElementById("registerPhone").value;

        const password =
            document.getElementById("registerPassword").value;

        const message =
            document.getElementById("registerMessage");


        const customerData = {

            name: name,

            email: email,

            phone: phone,

            password: password
        };


        try {

            const response = await fetch(
                `${API_URL}/customers/register`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(customerData)
                }
            );


            const result =
                await response.text();


            if (response.ok) {

                message.textContent =
                    "Registration successful! You can now login.";

                message.style.color = "green";


                document
                    .getElementById("registerForm")
                    .reset();

            } else {

                message.textContent =
                    result;

                message.style.color = "red";
            }


        } catch (error) {

            message.textContent =
                "Unable to connect to the server.";

            message.style.color = "red";

            console.error(error);
        }

    }
);


// =========================
// Customer Login
// =========================

document.getElementById("loginForm").addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        const email =
            document.getElementById("loginEmail").value;

        const password =
            document.getElementById("loginPassword").value;

        const message =
            document.getElementById("loginMessage");


        const loginData = {

            email: email,

            password: password
        };


        try {

            const response = await fetch(
                `${API_URL}/customers/login`,
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
                    "Login successful!";

                message.style.color = "green";


                // Store customer information
                localStorage.setItem(
                    "customerId",
                    result.id
                );

                localStorage.setItem(
                    "customerName",
                    result.name
                );

                localStorage.setItem(
                    "customerEmail",
                    result.email
                );


                // Check whether customer already has
                // a bank account

                setTimeout(async function () {

                    try {

                        const accountResponse =
                            await fetch(
                                `${API_URL}/accounts/customer/${result.id}`
                            );


                        const accounts =
                            await accountResponse.json();


                        // Customer already has account
                        if (accounts.length > 0) {

                            window.location.href =
                                "customer-dashboard.html";

                        }

                        // Customer does not have account
                        else {

                            window.location.href =
                                "bank-account.html";
                        }


                    } catch (error) {

                        console.error(error);

                        window.location.href =
                            "bank-account.html";
                    }

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

            message.textContent =
                "Unable to connect to the server.";

            message.style.color = "red";

            console.error(error);
        }

    }
);