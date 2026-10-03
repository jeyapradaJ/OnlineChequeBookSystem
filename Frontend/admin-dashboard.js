const API_URL = "https://onlinechequebooksystem.onrender.com/api";

// =========================
// Check Admin Login
// =========================

const adminId = localStorage.getItem("adminId");
const adminName = localStorage.getItem("adminName");

if (!adminId) {
    window.location.href = "admin-login.html";
}


// Show admin name
document.getElementById("adminName").textContent =
    adminName || "Admin";


// =========================
// Load Admin Dashboard
// =========================

async function loadAdminDashboard() {

    try {

        // Get dashboard data
        const dashboardResponse =
            await fetch(
                `${API_URL}/admin/dashboard`
            );

        if (!dashboardResponse.ok) {
            throw new Error(
                "Unable to load dashboard"
            );
        }

        const dashboardData =
            await dashboardResponse.json();


        // Total customers
        document.getElementById(
            "totalCustomers"
        ).textContent =
            dashboardData.totalCustomers;


        // Total requests
        document.getElementById(
            "totalRequests"
        ).textContent =
            dashboardData.totalRequests;


        // Pending requests
        const pendingCount =
            dashboardData.requests.filter(
                request =>
                    request.status === "PENDING"
            ).length;


        document.getElementById(
            "pendingRequests"
        ).textContent =
            pendingCount;


        // Load customers
        await loadCustomers();


        // Load complete request details
        await loadRequests();


    } catch (error) {

        console.error(error);

        alert(
            "Unable to load admin dashboard."
        );
    }
}


// =========================
// Load Customers
// =========================

async function loadCustomers() {

    try {

        const response =
            await fetch(
                `${API_URL}/customers`
            );

        if (!response.ok) {
            throw new Error(
                "Unable to load customers"
            );
        }

        const customers =
            await response.json();


        const container =
            document.getElementById(
                "customerContainer"
            );


        if (customers.length === 0) {

            container.innerHTML =
                "<p>No customers registered.</p>";

            return;
        }


        let html = `
            <div class="admin-table-wrapper">

                <table class="admin-table">

                    <thead>

                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                        </tr>

                    </thead>

                    <tbody>
        `;


        customers.forEach(function (customer) {

            html += `
                <tr>

                    <td>${customer.id}</td>

                    <td>${customer.name}</td>

                    <td>${customer.email}</td>

                    <td>${customer.phone}</td>

                </tr>
            `;

        });


        html += `
                    </tbody>

                </table>

            </div>
        `;


        container.innerHTML = html;


    } catch (error) {

        console.error(error);

        document.getElementById(
            "customerContainer"
        ).innerHTML =
            "<p>Unable to load customers.</p>";
    }
}


// =========================
// Load Complete Cheque Book Requests
// =========================

async function loadRequests() {

    try {

        const response =
            await fetch(
                `${API_URL}/admin/cheque-requests/details`
            );

        if (!response.ok) {
            throw new Error(
                "Unable to load requests"
            );
        }

        const requests =
            await response.json();


        const container =
            document.getElementById(
                "requestContainer"
            );


        if (requests.length === 0) {

            container.innerHTML =
                "<p>No cheque book requests.</p>";

            return;
        }


        let html = `
            <div class="admin-table-wrapper">

                <table class="admin-table">

                    <thead>

                        <tr>
                            <th>Request ID</th>
                            <th>Customer Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Account Number</th>
                            <th>Account Type</th>
                            <th>Leaves</th>
                            <th>Status</th>
                            <th>Update Status</th>
                        </tr>

                    </thead>

                    <tbody>
        `;


        requests.forEach(function (request) {

            html += `
                <tr>

                    <td>
                        ${request.requestId}
                    </td>

                    <td>
                        ${request.customerName || "-"}
                    </td>

                    <td>
                        ${request.customerEmail || "-"}
                    </td>

                    <td>
                        ${request.customerPhone || "-"}
                    </td>

                    <td>
                        ${request.accountNumber || "-"}
                    </td>

                    <td>
                        ${request.accountType || "-"}
                    </td>

                    <td>
                        ${request.numberOfLeaves}
                    </td>

                    <td>
                        <span class="status-badge">
                            ${request.status}
                        </span>
                    </td>

                    <td>

                        <select
                            onchange="updateRequestStatus(
                                ${request.requestId},
                                this.value
                            )"
                        >

                            <option value="">
                                Select Status
                            </option>

                            <option value="PENDING">
                                PENDING
                            </option>

                            <option value="PROCESSING">
                                PROCESSING
                            </option>

                            <option value="APPROVED">
                                APPROVED
                            </option>

                            <option value="DISPATCHED">
                                DISPATCHED
                            </option>

                            <option value="COMPLETED">
                                COMPLETED
                            </option>

                            <option value="REJECTED">
                                REJECTED
                            </option>

                        </select>

                    </td>

                </tr>
            `;

        });


        html += `
                    </tbody>

                </table>

            </div>
        `;


        container.innerHTML = html;


    } catch (error) {

        console.error(error);

        document.getElementById(
            "requestContainer"
        ).innerHTML =
            "<p>Unable to load cheque book requests.</p>";
    }
}


// =========================
// Update Request Status
// =========================

async function updateRequestStatus(
    requestId,
    status
) {

    if (!status) {
        return;
    }


    try {

        const response =
            await fetch(
                `${API_URL}/admin/cheque-requests/${requestId}/status?status=${status}`,
                {
                    method: "PUT"
                }
            );


        if (!response.ok) {

            const errorMessage =
                await response.text();

            alert(errorMessage);

            return;
        }


        alert(
            "Request status updated successfully!"
        );


        // Reload dashboard
        await loadAdminDashboard();


    } catch (error) {

        console.error(error);

        alert(
            "Unable to update request status."
        );
    }
}


// =========================
// Admin Logout
// =========================

document.getElementById(
    "adminLogoutBtn"
).addEventListener(
    "click",
    function () {

        localStorage.removeItem("adminId");
        localStorage.removeItem("adminName");
        localStorage.removeItem("adminEmail");

        window.location.href =
            "admin-login.html";
    }
);


// =========================
// Load Dashboard
// =========================

loadAdminDashboard();