const API_URL = "https://onlinechequebooksystem.onrender.com";

const registerForm = document.getElementById("customerRegisterForm");
const registerMessage = document.getElementById("registerMessage");
const backBtn = document.getElementById("backBtn");


// Back to Customer Login
backBtn.addEventListener("click", function () {
    window.location.href = "customer-login.html";
});


// Customer Registration
registerForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const name = document.getElementById("registerName").value.trim();
    const email = document.getElementById("registerEmail").value.trim();
    const phone = document.getElementById("registerPhone").value.trim();
    const password = document.getElementById("registerPassword").value;

    registerMessage.textContent = "Creating your account...";
    registerMessage.style.color = "#3E2723";

    try {

        const response = await fetch(
            `${API_URL}/api/customers/register`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    name: name,
                    email: email,
                    phone: phone,
                    password: password
                })
            }
        );


        const data = await response.text();


        if (response.ok) {

            registerMessage.textContent =
                "Account created successfully! Redirecting to login...";

            registerMessage.style.color = "green";


            setTimeout(function () {
                window.location.href = "customer-login.html";
            }, 1500);

        } else {

            registerMessage.textContent =
                data || "Unable to create account.";

            registerMessage.style.color = "red";
        }

    } catch (error) {

        console.error(error);

        registerMessage.textContent =
            "Unable to connect to the server.";

        registerMessage.style.color = "red";
    }

});