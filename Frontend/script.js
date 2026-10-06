const API_URL = "https://onlinechequebooksystem.onrender.com/api";


// =========================
// Customer Login
// =========================

const customerLoginForm =
    document.getElementById("customerLoginForm");


if (customerLoginForm) {

    customerLoginForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();

            const email =
                document.getElementById("loginEmail").value.trim();

            const password =
                document.getElementById("loginPassword").value;

            const message =
                document.getElementById("loginMessage");

            const loginButton =
                customerLoginForm.querySelector(
                    "button[type='submit']"
                );


            // Prevent multiple clicks
            loginButton.disabled = true;
            loginButton.textContent = "Logging in...";

            message.textContent = "Connecting to server...";
            message.style.color = "#3E2723";


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
                    await response.text();


                if (response.ok) {

                    const customer =
                        JSON.parse(result);


                    message.textContent =
                        "Login successful!";

                    message.style.color = "green";


                    // Store customer information

                    localStorage.setItem(
                        "customerId",
                        customer.id
                    );

                    localStorage.setItem(
                        "customerName",
                        customer.name
                    );

                    localStorage.setItem(
                        "customerEmail",
                        customer.email
                    );


                    // Check bank account

                    try {

                        const accountResponse =
                            await fetch(
                                `${API_URL}/accounts/customer/${customer.id}`
                            );


                        const accounts =
                            await accountResponse.json();


                        if (
                            Array.isArray(accounts) &&
                            accounts.length > 0
                        ) {

                            window.location.href =
                                "customer-dashboard.html";

                        } else {

                            window.location.href =
                                "bank-account.html";
                        }


                    } catch (error) {

                        console.error(error);

                        window.location.href =
                            "bank-account.html";
                    }


                } else {

                    message.textContent =
                        result ||
                        "Invalid email or password.";

                    message.style.color = "red";


                    // Enable button again
                    loginButton.disabled = false;
                    loginButton.textContent = "Login";
                }


            } catch (error) {

                console.error(error);

                message.textContent =
                    "Unable to connect to the server.";

                message.style.color = "red";


                // Enable button again
                loginButton.disabled = false;
                loginButton.textContent = "Login";
            }

        }
    );

}