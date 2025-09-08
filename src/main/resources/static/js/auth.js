// --- AUTH.JS ---

// Wait for the DOM to be fully loaded before running scripts
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const logoutButton = document.getElementById('logout-button'); // Might be on multiple pages

    // --- Helper Functions ---

    /**
     * Stores user data (minimal for now: just ID and name) in localStorage.
     * In a real app, you'd store a JWT token here.
     * @param {object} userData - The user data object from the API (needs at least 'id' and 'name').
     */
    function storeUserData(userData) {
        if (!userData || typeof userData.id === 'undefined') {
            console.error('Cannot store invalid user data:', userData);
            return;
        }
        localStorage.setItem('userId', userData.id);
        if (userData.name) {
            localStorage.setItem('userName', userData.name);
        }
         if (userData.preferredCurrency) {
            localStorage.setItem('userCurrency', userData.preferredCurrency);
        }
    }

    /**
     * Retrieves the stored user ID.
     * @returns {string | null} The user ID or null if not logged in.
     */
    function getUserId() {
        return localStorage.getItem('userId');
    }

     /**
     * Retrieves the stored user name.
     * @returns {string | null} The user name or null.
     */
    function getUserName() {
        return localStorage.getItem('userName');
    }

    /**
     * Clears user data from localStorage (logout).
     */
    function clearUserData() {
        localStorage.removeItem('userId');
        localStorage.removeItem('userName');
        localStorage.removeItem('userCurrency'); // Also clear currency
    }

    /**
     * Checks if the user is currently logged in (based on stored userId).
     * @returns {boolean} True if logged in, false otherwise.
     */
    function isLoggedIn() {
        return !!getUserId(); // Converts the userId (or null) to a boolean
    }

    /**
     * Redirects to the login page if the user is not authenticated.
     * Call this at the beginning of scripts for protected pages.
     */
    function enforceLogin() {
        if (!isLoggedIn()) {
            console.log('User not logged in. Redirecting to login page.');
            window.location.href = 'index.html'; // Redirect to login
        }
    }

     /**
     * Displays an error message on a form.
     * @param {HTMLElement} errorElement - The <p> element to display the error in.
     * @param {string} message - The error message.
     */
    function showFormError(errorElement, message) {
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    }

    /**
     * Hides an error message element.
     * @param {HTMLElement} errorElement - The <p> element to hide.
     */
    function hideFormError(errorElement) {
         if (errorElement) {
            errorElement.textContent = '';
            errorElement.style.display = 'none';
        }
    }

     /**
     * Displays a success message on a form.
     * @param {HTMLElement} successElement - The <p> element to display the message in.
     * @param {string} message - The success message.
     */
    function showFormSuccess(successElement, message) {
        if (successElement) {
            successElement.textContent = message;
            successElement.style.display = 'block';
        }
    }
     /**
     * Hides a success message element.
     * @param {HTMLElement} successElement - The <p> element to hide.
     */
    function hideFormSuccess(successElement) {
         if (successElement) {
            successElement.textContent = '';
            successElement.style.display = 'none';
        }
    }


    // --- Event Listeners ---

    // Handle Login Form Submission
    if (loginForm) {
        const emailInput = document.getElementById('login-email');
        const passwordInput = document.getElementById('login-password');
        const errorElement = document.getElementById('login-error');

        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault(); // Prevent default browser submission
            hideFormError(errorElement); // Clear previous errors

            const email = emailInput.value.trim();
            const password = passwordInput.value.trim();

            if (!email || !password) {
                showFormError(errorElement, 'Please enter both email and password.');
                return;
            }

            const loginData = { email, password };

            try {
                // Assume api.js provides fetchAPI(url, options)
                const result = await fetchAPI('/api/auth/login', {
                    method: 'POST',
                    body: loginData // api.js should handle JSON stringify
                });

                console.log('Login successful:', result);
                storeUserData(result); // Store user ID and name
                window.location.href = 'dashboard.html'; // Redirect to dashboard

            } catch (error) {
                console.error('Login failed:', error);
                // Customize error message based on potential status codes if needed
                showFormError(errorElement, error.message || 'Login failed. Please check your credentials.');
            }
        });
    }

    // Handle Registration Form Submission
    if (registerForm) {
        const nameInput = document.getElementById('register-name');
        const emailInput = document.getElementById('register-email');
        const passwordInput = document.getElementById('register-password');
        const currencyInput = document.getElementById('register-currency');
        const errorElement = document.getElementById('register-error');
        const successElement = document.getElementById('register-success');


        registerForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            hideFormError(errorElement);
            hideFormSuccess(successElement);

            const name = nameInput.value.trim();
            const email = emailInput.value.trim();
            const password = passwordInput.value.trim();
            const currency = currencyInput.value.trim().toUpperCase();


            if (!name || !email || !password || !currency) {
                showFormError(errorElement, 'Please fill in all required fields.');
                return;
            }
            if (currency.length !== 3) {
                 showFormError(errorElement, 'Currency code must be 3 letters (e.g., USD).');
                 return;
            }

            const registrationData = {
                name: name,
                email: email,
                password: password,
                preferredCurrency: currency
            };

            try {
                const result = await fetchAPI('/api/auth/register', {
                    method: 'POST',
                    body: registrationData
                });

                console.log('Registration successful:', result);
                showFormSuccess(successElement, 'Registration successful! You can now log in.');
                registerForm.reset(); // Clear the form
                // Optional: Redirect to login after a short delay
                // setTimeout(() => { window.location.href = 'index.html'; }, 2000);

            } catch (error) {
                console.error('Registration failed:', error);
                showFormError(errorElement, error.message || 'Registration failed. Please try again.');
            }
        });
    }

    // Handle Logout Button Click
    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            console.log('Logging out...');
            clearUserData();
            window.location.href = 'index.html'; // Redirect to login page
        });
    }

    // --- Page Initialization ---

    // If on dashboard or profile page, enforce login
    const currentPage = window.location.pathname.split('/').pop();
    if (currentPage === 'dashboard.html' || currentPage === 'profile.html') {
        enforceLogin();

        // Update greeting message on dashboard if element exists
        const userGreeting = document.getElementById('user-greeting');
         if (userGreeting && getUserName()) {
             userGreeting.textContent = `Welcome, ${getUserName()}!`;
         }
    }

    // Expose necessary functions globally if needed by other scripts (or use modules)
    // Example: window.auth = { isLoggedIn, getUserId, enforceLogin };
    // For simplicity here, we rely on script execution order and localStorage.

}); // End DOMContentLoaded