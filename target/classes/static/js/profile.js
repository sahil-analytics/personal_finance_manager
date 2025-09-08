// --- PROFILE.JS ---

document.addEventListener('DOMContentLoaded', () => {

    // --- DOM Elements ---
    const profileInfoDiv = document.getElementById('profile-info');
    const profileNameSpan = document.getElementById('profile-name');
    const profileEmailSpan = document.getElementById('profile-email');
    const profileCurrencySpan = document.getElementById('profile-currency');
    const profileLoadError = document.getElementById('profile-load-error');

    const updateProfileForm = document.getElementById('update-profile-form');
    const updateNameInput = document.getElementById('update-name');
    const updateCurrencyInput = document.getElementById('update-currency');
    const profileUpdateError = document.getElementById('profile-update-error');
    const profileUpdateSuccess = document.getElementById('profile-update-success');

    // --- Helper Functions (from auth.js or similar) ---
    // Assuming these are available (or include them again if not using modules/global scope)
    function getUserId() {
        return localStorage.getItem('userId');
    }

     function getUserName() {
        return localStorage.getItem('userName');
    }

    function getUserCurrency() {
        return localStorage.getItem('userCurrency');
    }

    /**
     * Updates user details in localStorage after successful profile update.
     * @param {object} updatedUserData - The user data returned from the update API.
     */
    function updateStoredUserData(updatedUserData) {
         if (updatedUserData?.name) {
            localStorage.setItem('userName', updatedUserData.name);
        }
         if (updatedUserData?.preferredCurrency) {
            localStorage.setItem('userCurrency', updatedUserData.preferredCurrency);
        }
    }


    /**
     * Displays an error message.
     * @param {HTMLElement} errorElement - The <p> element.
     * @param {string} message - The error message.
     */
    function showMessage(element, message, isError = true) {
        if (element) {
            element.textContent = message;
            element.style.display = 'block';
            // Optionally add/remove CSS classes for styling errors/success
             element.className = isError ? 'error-message' : 'success-message';
        }
    }

    /**
     * Hides a message element.
     * @param {HTMLElement} element - The <p> element to hide.
     */
    function hideMessage(element) {
         if (element) {
            element.textContent = '';
            element.style.display = 'none';
        }
    }

    // --- Core Profile Functions ---

    /**
     * Fetches the current user's profile data from the API.
     */
    async function fetchUserProfile() {
        const userId = getUserId();
        if (!userId) {
            console.error("User ID not found. Cannot fetch profile.");
            showMessage(profileLoadError, "You must be logged in to view your profile.");
            // Optional: Redirect to login via auth.js function if needed
            return;
        }

        hideMessage(profileLoadError);
        hideMessage(profileUpdateError);
        hideMessage(profileUpdateSuccess);

        try {
            const userData = await fetchAPI(`/api/users/${userId}`); // GET is default

            if (userData) {
                displayProfileData(userData);
                populateUpdateForm(userData);
            } else {
                 throw new Error("Received no data for profile.");
            }

        } catch (error) {
            console.error("Error fetching profile:", error);
            showMessage(profileLoadError, `Failed to load profile: ${error.message}`);
        }
    }

    /**
     * Displays the fetched user data in the profile info section.
     * @param {object} userData - The user data object from the API.
     */
    function displayProfileData(userData) {
        if (!userData) return;
        profileNameSpan.textContent = userData.name || 'N/A';
        profileEmailSpan.textContent = userData.email || 'N/A'; // Email shouldn't change via this form
        profileCurrencySpan.textContent = userData.preferredCurrency || 'N/A';
    }

    /**
     * Populates the update profile form with current data.
     * @param {object} userData - The user data object from the API.
     */
    function populateUpdateForm(userData) {
        if (!userData) return;
        updateNameInput.value = userData.name || '';
        updateCurrencyInput.value = userData.preferredCurrency || '';
        // Do NOT populate password fields here
    }

    /**
     * Handles the submission of the update profile form.
     */
    async function handleUpdateProfile(event) {
        event.preventDefault(); // Prevent default form submission

        const userId = getUserId();
        if (!userId) {
            showMessage(profileUpdateError, "Cannot update profile. User not identified.");
            return;
        }

        hideMessage(profileUpdateError);
        hideMessage(profileUpdateSuccess);

        const updatedName = updateNameInput.value.trim();
        const updatedCurrency = updateCurrencyInput.value.trim().toUpperCase();

        // Basic Validation
        if (!updatedName || !updatedCurrency) {
            showMessage(profileUpdateError, "Name and Currency cannot be empty.");
            return;
        }
        if (updatedCurrency.length !== 3) {
             showMessage(profileUpdateError, "Currency code must be 3 letters (e.g., USD).");
             return;
        }


        // Construct DTO - ONLY send fields that can be updated via this form
        const updateData = {
            id: parseInt(userId), // Include ID if needed by backend, but typically it's from path
            name: updatedName,
            preferredCurrency: updatedCurrency
            // DO NOT SEND email or password from this form
        };


        try {
            const result = await fetchAPI(`/api/users/${userId}`, {
                method: 'PUT',
                body: updateData
            });

            console.log('Profile update successful:', result);
            showMessage(profileUpdateSuccess, "Profile updated successfully!", false);

            // Update displayed info and stored data
            displayProfileData(result); // Update the static display section
            updateStoredUserData(result); // Update localStorage

            // Optional: Update header greeting if it exists and name changed
             const userGreeting = document.getElementById('user-greeting'); // Check if header element exists on this page
             if (userGreeting && result.name) {
                 userGreeting.textContent = `Welcome, ${result.name}!`;
             }


        } catch (error) {
            console.error("Error updating profile:", error);
            showMessage(profileUpdateError, `Failed to update profile: ${error.message}`);
        }
    }


    // --- Initialization ---

    // Add event listener to the update form
    if (updateProfileForm) {
        updateProfileForm.addEventListener('submit', handleUpdateProfile);
    } else {
         console.warn("Update profile form not found.");
    }

    // Fetch and display profile data when the page loads
    fetchUserProfile();

}); // End DOMContentLoaded