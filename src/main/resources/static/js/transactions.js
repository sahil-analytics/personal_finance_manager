// --- TRANSACTIONS.JS ---

document.addEventListener('DOMContentLoaded', () => {
    // --- Check Login Status (Should be handled by auth.js) ---
    if (!getUserId()) {
        console.error("User not logged in. Cannot manage transactions.");
        // auth.js should redirect, but double-check
        // window.location.href = 'index.html';
        return;
    }

    // --- DOM Elements ---
    // Transaction Form
    const transactionForm = document.getElementById('add-transaction-form');
    const editTransactionIdInput = document.getElementById('edit-transaction-id');
    const transactionTypeSelect = document.getElementById('transaction-type');
    const transactionAmountInput = document.getElementById('transaction-amount');
    const transactionCategorySelect = document.getElementById('transaction-category');
    const transactionDateInput = document.getElementById('transaction-date');
    const transactionDescriptionInput = document.getElementById('transaction-description');
    const addUpdateButton = document.getElementById('add-update-button');
    const cancelEditButton = document.getElementById('cancel-edit-button');
    const transactionError = document.getElementById('transaction-error');
    const transactionSuccess = document.getElementById('transaction-success');

    // Transactions Table
    const transactionsTableBody = document.getElementById('transactions-tbody');
    const transactionsError = document.getElementById('transactions-error');

    // Category Management
    const manageCategoriesButton = document.getElementById('manage-categories-button');
    const categoryModal = document.getElementById('category-modal');
    const closeCategoryModalButton = document.getElementById('close-category-modal');
    const addCategoryForm = document.getElementById('add-category-form');
    const newCategoryNameInput = document.getElementById('new-category-name');
    const categoryListUl = document.getElementById('category-list');
    const categoryAddError = document.getElementById('category-add-error');
    const categoryListError = document.getElementById('category-list-error');

    // --- State ---
    let currentCategories = []; // Cache categories to avoid redundant fetches

    // --- Helper Functions ---

    function getUserId() {
        return localStorage.getItem('userId');
    }
     function getUserCurrency() {
        // Fallback if not set during login/profile fetch
        return localStorage.getItem('userCurrency') || 'USD'; // Default to USD
    }

    function showMessage(element, message, isSuccess = false) {
        if (!element) return;
        element.textContent = message;
        element.className = isSuccess ? 'success-message' : 'error-message';
        element.style.display = 'block';
        // Auto-hide success messages after a few seconds
        if (isSuccess) {
            setTimeout(() => hideMessage(element), 3000);
        }
    }

    function hideMessage(element) {
        if (!element) return;
        element.textContent = '';
        element.style.display = 'none';
    }

    /**
     * Formats a number as currency using the user's preferred currency.
     * @param {number} amount The amount to format.
     * @returns {string} Formatted currency string.
     */
    function formatCurrency(amount) {
        const currency = getUserCurrency();
        const formatter = new Intl.NumberFormat(undefined, { // Use browser's locale
            style: 'currency',
            currency: currency,
            minimumFractionDigits: 2
        });
        // Handle potential errors if amount is not a number
        try {
            return formatter.format(amount);
        } catch (e) {
            console.error("Error formatting currency:", e, "Amount:", amount);
            return String(amount); // Fallback to string representation
        }
    }

     /**
     * Formats a date string (YYYY-MM-DD) into a more readable format.
     * @param {string} dateString The date string.
     * @returns {string} Formatted date string (e.g., Oct 27, 2023).
     */
    function formatDate(dateString) {
        if (!dateString) return 'N/A';
        try {
            const date = new Date(dateString + 'T00:00:00'); // Ensure correct parsing across timezones
            return date.toLocaleDateString(undefined, { // Use browser's locale
                year: 'numeric',
                month: 'short',
                day: 'numeric',
            });
        } catch(e) {
            console.error("Error formatting date:", e, "Date String:", dateString);
            return dateString; // Fallback
        }
    }


    /**
     * Clears the transaction form and resets buttons to "Add" mode.
     */
    function resetTransactionForm() {
        transactionForm.reset(); // Resets all form fields
        editTransactionIdInput.value = ''; // Clear hidden ID
        addUpdateButton.textContent = 'Add Transaction';
        cancelEditButton.style.display = 'none';
        // Set default date to today
        transactionDateInput.valueAsDate = new Date();
        hideMessage(transactionError);
        hideMessage(transactionSuccess);
    }

    // --- Category Functions ---

    /**
     * Fetches categories and populates the dropdown select in the transaction form.
     */
    async function loadCategoriesDropdown() {
        const userId = getUserId();
        try {
            currentCategories = await fetchAPI(`/api/users/${userId}/categories`);
            transactionCategorySelect.innerHTML = '<option value="">-- Select Category --</option>'; // Clear and add default

            if (currentCategories && currentCategories.length > 0) {
                currentCategories.forEach(category => {
                    const option = document.createElement('option');
                    option.value = category.id;
                    option.textContent = category.name;
                    transactionCategorySelect.appendChild(option);
                });
            } else {
                 transactionCategorySelect.innerHTML = '<option value="">-- No categories found --</option>';
                 // Optionally disable the select or prompt user to add categories
            }
        } catch (error) {
            console.error("Error loading categories for dropdown:", error);
            showMessage(transactionError, `Failed to load categories: ${error.message}`);
             transactionCategorySelect.innerHTML = '<option value="">-- Error loading --</option>';
        }
    }

    /**
     * Fetches categories and populates the list in the management modal.
     */
    async function loadCategoriesList() {
        const userId = getUserId();
        hideMessage(categoryListError);
        categoryListUl.innerHTML = '<li>Loading...</li>'; // Show loading indicator

        try {
            // Use cached categories if available and modal was just opened,
            // otherwise refetch for latest data after add/delete.
            // For simplicity, we refetch every time here.
            currentCategories = await fetchAPI(`/api/users/${userId}/categories`);
            categoryListUl.innerHTML = ''; // Clear list

            if (currentCategories && currentCategories.length > 0) {
                currentCategories.forEach(category => {
                    const li = document.createElement('li');
                    li.textContent = category.name;

                    const deleteBtn = document.createElement('button');
                    deleteBtn.textContent = 'Delete';
                    deleteBtn.classList.add('btn', 'btn-danger', 'btn-small', 'delete-category-btn');
                    deleteBtn.dataset.id = category.id; // Store category ID
                    li.appendChild(deleteBtn);

                    categoryListUl.appendChild(li);
                });
            } else {
                categoryListUl.innerHTML = '<li>No categories defined yet.</li>';
            }
        } catch (error) {
            console.error("Error loading categories list:", error);
             categoryListUl.innerHTML = ''; // Clear loading message
            showMessage(categoryListError, `Failed to load categories: ${error.message}`);
        }
    }

    /**
     * Handles adding a new category via the modal form.
     */
    async function handleAddCategory(event) {
        event.preventDefault();
        hideMessage(categoryAddError);
        const userId = getUserId();
        const categoryName = newCategoryNameInput.value.trim();

        if (!categoryName) {
            showMessage(categoryAddError, "Category name cannot be empty.");
            return;
        }

        const categoryData = { name: categoryName };

        try {
            await fetchAPI(`/api/users/${userId}/categories`, {
                method: 'POST',
                body: categoryData
            });

            newCategoryNameInput.value = ''; // Clear input
            await loadCategoriesList(); // Refresh list in modal
            await loadCategoriesDropdown(); // Refresh dropdown in main form
            // showMessage(... success ...) // Optional success message

        } catch (error) {
            console.error("Error adding category:", error);
            showMessage(categoryAddError, `Failed to add category: ${error.message}`);
        }
    }

    /**
     * Handles deleting a category from the management modal list.
     */
    async function handleDeleteCategory(categoryId) {
        const userId = getUserId();

        if (!window.confirm(`Are you sure you want to delete this category? Transactions using it might be affected.`)) {
            return;
        }

        hideMessage(categoryListError); // Clear previous errors

        try {
            await fetchAPI(`/api/users/${userId}/categories/${categoryId}`, {
                method: 'DELETE'
            });

            await loadCategoriesList(); // Refresh list in modal
            await loadCategoriesDropdown(); // Refresh dropdown in main form
             // showMessage(... success ...) // Optional success message

        } catch (error) {
            console.error("Error deleting category:", error);
            showMessage(categoryListError, `Failed to delete category: ${error.message}. It might be in use.`);
        }
    }

    // --- Transaction Functions ---

    /**
     * Fetches transactions and populates the table.
     */
    async function loadTransactions() {
        const userId = getUserId();
        hideMessage(transactionsError);
        transactionsTableBody.innerHTML = '<tr><td colspan="6">Loading transactions...</td></tr>';

        try {
            const transactions = await fetchAPI(`/api/users/${userId}/transactions`);
            transactionsTableBody.innerHTML = ''; // Clear loading message

            if (transactions && transactions.length > 0) {
                transactions.forEach(tx => {
                    const row = transactionsTableBody.insertRow();
                    row.insertCell(0).textContent = formatDate(tx.date);
                    row.insertCell(1).textContent = tx.type; // INCOME or EXPENSE
                    row.insertCell(2).textContent = tx.categoryName || 'N/A'; // Display category name
                    row.insertCell(3).textContent = formatCurrency(tx.amount);
                    row.cells[3].style.color = tx.type === 'INCOME' ? 'var(--success-color)' : 'var(--danger-color)';
                    row.insertCell(4).textContent = tx.description || '-';

                    // Actions Cell
                    const actionsCell = row.insertCell(5);
                    const editBtn = document.createElement('button');
                    editBtn.textContent = 'Edit';
                    editBtn.classList.add('btn', 'btn-secondary', 'btn-small', 'edit-transaction-btn');
                    editBtn.dataset.id = tx.id;
                    actionsCell.appendChild(editBtn);

                    const deleteBtn = document.createElement('button');
                    deleteBtn.textContent = 'Delete';
                    deleteBtn.classList.add('btn', 'btn-danger', 'btn-small', 'delete-transaction-btn');
                    deleteBtn.dataset.id = tx.id;
                    actionsCell.appendChild(deleteBtn);
                });
            } else {
                transactionsTableBody.innerHTML = '<tr><td colspan="6">No transactions found. Add one above!</td></tr>';
            }
        } catch (error) {
            console.error("Error loading transactions:", error);
             transactionsTableBody.innerHTML = ''; // Clear loading message
            showMessage(transactionsError, `Failed to load transactions: ${error.message}`);
        }
    }

    /**
     * Handles submission of the transaction form (Add or Update).
     */
    async function handleAddOrUpdateTransaction(event) {
        event.preventDefault();
        hideMessage(transactionError);
        hideMessage(transactionSuccess);

        const userId = getUserId();
        const transactionId = editTransactionIdInput.value; // Will be empty for Add, populated for Update

        // Basic Validation
        const type = transactionTypeSelect.value;
        const amount = transactionAmountInput.value;
        const categoryId = transactionCategorySelect.value;
        const date = transactionDateInput.value;

        if (!type || !amount || !categoryId || !date) {
            showMessage(transactionError, "Please fill in Type, Amount, Category, and Date.");
            return;
        }
        if (isNaN(parseFloat(amount)) || parseFloat(amount) <= 0) {
             showMessage(transactionError, "Amount must be a positive number.");
            return;
        }


        const transactionData = {
            type: type,
            amount: parseFloat(amount),
            categoryId: parseInt(categoryId),
            date: date,
            description: transactionDescriptionInput.value.trim() || null // Send null if empty
        };

        const isUpdate = !!transactionId;
        const url = isUpdate ? `/api/users/${userId}/transactions/${transactionId}` : `/api/users/${userId}/transactions`;
        const method = isUpdate ? 'PUT' : 'POST';

        try {
            const result = await fetchAPI(url, {
                method: method,
                body: transactionData
            });

            showMessage(transactionSuccess, `Transaction ${isUpdate ? 'updated' : 'added'} successfully!`, true);
            resetTransactionForm();
            await loadTransactions(); // Refresh the list
             // Optionally update summary/chart if reports.js is loaded and handles it

        } catch (error) {
            console.error(`Error ${isUpdate ? 'updating' : 'adding'} transaction:`, error);
            showMessage(transactionError, `Failed to ${isUpdate ? 'update' : 'add'} transaction: ${error.message}`);
        }
    }

    /**
     * Handles clicking the 'Edit' button on a transaction row.
     */
    async function handleEditTransactionClick(transactionId) {
        const userId = getUserId();
        hideMessage(transactionError);
        hideMessage(transactionSuccess);
        resetTransactionForm(); // Clear form first

        try {
            const tx = await fetchAPI(`/api/users/${userId}/transactions/${transactionId}`);

            // Populate the form
            editTransactionIdInput.value = tx.id;
            transactionTypeSelect.value = tx.type;
            transactionAmountInput.value = tx.amount;
            transactionCategorySelect.value = tx.categoryId;
            transactionDateInput.value = tx.date; // Format should be YYYY-MM-DD
            transactionDescriptionInput.value = tx.description || '';

            // Update button text and show cancel button
            addUpdateButton.textContent = 'Update Transaction';
            cancelEditButton.style.display = 'inline-block'; // Show cancel button

             // Scroll to the form for better UX
            transactionForm.scrollIntoView({ behavior: 'smooth' });

        } catch (error) {
            console.error("Error fetching transaction for edit:", error);
            showMessage(transactionError, `Failed to load transaction details: ${error.message}`);
        }
    }

     /**
     * Handles clicking the 'Delete' button on a transaction row.
     */
    async function handleDeleteTransactionClick(transactionId) {
        const userId = getUserId();

         if (!window.confirm('Are you sure you want to delete this transaction?')) {
            return;
        }

        hideMessage(transactionsError); // Clear table error

         try {
            await fetchAPI(`/api/users/${userId}/transactions/${transactionId}`, {
                method: 'DELETE'
            });

            // showMessage(... success ...) // Optional success message near table?
            await loadTransactions(); // Refresh the list
             // Optionally update summary/chart

        } catch (error) {
            console.error("Error deleting transaction:", error);
            showMessage(transactionsError, `Failed to delete transaction: ${error.message}`);
        }
    }


    // --- Event Listeners ---

    // Transaction Form Submit (Add/Update)
    if (transactionForm) {
        transactionForm.addEventListener('submit', handleAddOrUpdateTransaction);
    }

    // Cancel Edit Button
    if (cancelEditButton) {
        cancelEditButton.addEventListener('click', resetTransactionForm);
    }

    // Transaction Table Event Delegation (Edit/Delete Buttons)
    if (transactionsTableBody) {
        transactionsTableBody.addEventListener('click', (event) => {
            const editButton = event.target.closest('.edit-transaction-btn');
            const deleteButton = event.target.closest('.delete-transaction-btn');

            if (editButton) {
                handleEditTransactionClick(editButton.dataset.id);
            } else if (deleteButton) {
                handleDeleteTransactionClick(deleteButton.dataset.id);
            }
        });
    }

    // Category Modal Buttons
    if (manageCategoriesButton) {
        manageCategoriesButton.addEventListener('click', () => {
            categoryModal.style.display = 'flex'; // Show modal
            loadCategoriesList(); // Load/refresh list when opening
        });
    }
    if (closeCategoryModalButton) {
        closeCategoryModalButton.addEventListener('click', () => {
            categoryModal.style.display = 'none'; // Hide modal
             hideMessage(categoryAddError); // Clear errors on close
             hideMessage(categoryListError);
        });
    }
     // Close modal if clicking outside the content
     if (categoryModal) {
         categoryModal.addEventListener('click', (event) => {
             if (event.target === categoryModal) { // Check if the click was on the background overlay
                 categoryModal.style.display = 'none';
                 hideMessage(categoryAddError);
                 hideMessage(categoryListError);
             }
         });
     }


    // Add Category Form Submit
    if (addCategoryForm) {
        addCategoryForm.addEventListener('submit', handleAddCategory);
    }

    // Category List Event Delegation (Delete Button)
     if (categoryListUl) {
         categoryListUl.addEventListener('click', (event) => {
             const deleteButton = event.target.closest('.delete-category-btn');
             if (deleteButton) {
                 handleDeleteCategory(deleteButton.dataset.id);
             }
         });
     }

    // --- Initial Load ---
    loadCategoriesDropdown();
    loadTransactions();
    resetTransactionForm(); // Ensure form is in initial state (sets today's date)

}); // End DOMContentLoaded