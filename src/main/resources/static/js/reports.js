// --- REPORTS.JS ---

document.addEventListener('DOMContentLoaded', () => {
    // --- Check Login Status ---
    if (!getUserId()) {
        console.error("User not logged in. Cannot display reports.");
        // Auth.js should handle redirection
        return;
    }

    // --- DOM Elements ---
    // Summary
    const summaryMonthInput = document.getElementById('summary-month'); // Input type="month"
    const viewSummaryButton = document.getElementById('view-summary-button');
    const viewYearlySummaryButton = document.getElementById('view-yearly-summary-button');
    const summaryIncomeSpan = document.getElementById('summary-income');
    const summaryExpensesSpan = document.getElementById('summary-expenses');
    const summaryBalanceSpan = document.getElementById('summary-balance');
    const summaryError = document.getElementById('summary-error');

    // Chart
    const chartMonthInput = document.getElementById('chart-month'); // Input type="month"
    const viewChartButton = document.getElementById('view-chart-button');
    const categoryChartCanvas = document.getElementById('category-pie-chart');
    const chartError = document.getElementById('chart-error');
    const chartNoData = document.getElementById('chart-nodata'); // Info message element

    // --- Chart.js Instance ---
    let categoryPieChart = null; // Holds the Chart object

    // --- Helper Functions ---

    function getUserId() {
        return localStorage.getItem('userId');
    }

    function getUserCurrency() {
        return localStorage.getItem('userCurrency') || 'USD';
    }

    /**
     * Formats a number as currency using the user's preferred currency.
     * (Copied/Adapted from transactions.js for self-containment or ensure it's globally available)
     * @param {number} amount The amount to format.
     * @returns {string} Formatted currency string.
     */
    function formatCurrency(amount) {
        const currency = getUserCurrency();
        const formatter = new Intl.NumberFormat(undefined, {
            style: 'currency',
            currency: currency,
            minimumFractionDigits: 2
        });
        try {
            // Handle null or undefined safely
            return formatter.format(amount ?? 0);
        } catch (e) {
            console.error("Error formatting currency:", e, "Amount:", amount);
            return String(amount ?? '0.00');
        }
    }

    /**
     * Parses "YYYY-MM" string into year and month numbers.
     * @param {string} yearMonthString - The string from the month input.
     * @returns {object|null} An object { year: number, month: number } or null if invalid.
     */
    function parseYearMonth(yearMonthString) {
        if (!yearMonthString || !/^\d{4}-\d{2}$/.test(yearMonthString)) {
            console.error("Invalid year-month format:", yearMonthString);
            return null; // Or return current month/year as default?
        }
        const [year, month] = yearMonthString.split('-').map(Number);
        return { year, month };
    }


    function showMessage(element, message, isError = true) {
        if (!element) return;
        element.textContent = message;
        element.className = isError ? 'error-message' : 'info-message'; // Use info for no data
        element.style.display = 'block';
    }

    function hideMessage(element) {
        if (!element) return;
        element.textContent = '';
        element.style.display = 'none';
    }

    // --- Summary Functions ---

    /**
     * Fetches and displays the summary (monthly or yearly).
     * @param {number} year The year for the summary.
     * @param {number|null} month The month (1-12) for monthly summary, or null for yearly.
     */
    async function fetchAndDisplaySummary(year, month = null) {
        const userId = getUserId();
        hideMessage(summaryError);
        // Indicate loading state (optional)
        summaryIncomeSpan.textContent = 'Loading...';
        summaryExpensesSpan.textContent = 'Loading...';
        summaryBalanceSpan.textContent = 'Loading...';

        // Construct query parameters
        let queryParams = `?year=${year}`;
        if (month !== null) {
            queryParams += `&month=${month}`;
        }

        try {
            const summaryData = await fetchAPI(`/api/users/${userId}/reports/summary${queryParams}`);
            displaySummary(summaryData);
        } catch (error) {
            console.error("Error fetching summary:", error);
            showMessage(summaryError, `Failed to load summary: ${error.message}`);
            // Reset display on error
            displaySummary({ totalIncome: 0, totalExpenses: 0, balance: 0 });
        }
    }

    /**
     * Updates the summary section display with fetched data.
     * @param {object} summaryData - The summary data object { totalIncome, totalExpenses, balance }.
     */
    function displaySummary(summaryData) {
        summaryIncomeSpan.textContent = formatCurrency(summaryData?.totalIncome);
        summaryExpensesSpan.textContent = formatCurrency(summaryData?.totalExpenses);
        summaryBalanceSpan.textContent = formatCurrency(summaryData?.balance);
    }

    // --- Chart Functions ---

    /**
     * Fetches category spending data and renders the pie chart.
     * @param {number} year The year for the chart data.
     * @param {number} month The month (1-12) for the chart data.
     */
    async function fetchAndDisplayChart(year, month) {
        const userId = getUserId();
        hideMessage(chartError);
        hideMessage(chartNoData);
         // Optional: Show a loading indicator on the canvas area

        try {
            const chartData = await fetchAPI(`/api/users/${userId}/reports/category-chart?year=${year}&month=${month}`);
            renderPieChart(chartData);
        } catch (error) {
            console.error("Error fetching chart data:", error);
            showMessage(chartError, `Failed to load chart data: ${error.message}`);
            // Ensure chart is cleared or shows error state
            if (categoryPieChart) {
                categoryPieChart.destroy();
                categoryPieChart = null;
            }
        }
    }

    /**
     * Renders the pie chart using Chart.js.
     * @param {object} chartData - Data from the API { labels: [], values: [] }.
     */
    function renderPieChart(chartData) {
        const ctx = categoryChartCanvas.getContext('2d');

        // Destroy previous chart instance if it exists
        if (categoryPieChart) {
            categoryPieChart.destroy();
            categoryPieChart = null;
        }

        if (!chartData || !chartData.labels || !chartData.values || chartData.labels.length === 0) {
            console.log("No data available for pie chart.");
             showMessage(chartNoData, "No expense data available for this period.", false); // Show info message
            // Optionally draw a placeholder or leave canvas blank
            return;
        }


        // Generate colors dynamically or use a predefined palette
         const backgroundColors = generateColors(chartData.labels.length);
         const borderColors = backgroundColors.map(color => color.replace('0.6', '1')); // Make border opaque

        categoryPieChart = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: chartData.labels, // Category names
                datasets: [{
                    label: 'Spending by Category',
                    data: chartData.values, // Spending amounts
                    backgroundColor: backgroundColors,
                     borderColor: borderColors, // Optional: Add borders
                     borderWidth: 1 // Optional: Border width
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false, // Allow chart to fill container height
                plugins: {
                    legend: {
                        position: 'top', // Or 'bottom', 'left', 'right'
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                let label = context.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                if (context.parsed !== null) {
                                    // Format tooltip value as currency
                                    label += formatCurrency(context.parsed);
                                }
                                return label;
                            }
                        }
                    },
                    title: {
                         display: false, // Title is already in the card header
                         // text: 'Monthly Spending by Category'
                     }
                }
            }
        });
    }

     /**
      * Generates an array of distinct colors for the chart.
      * @param {number} count - Number of colors needed.
      * @returns {string[]} Array of RGBA color strings.
      */
     function generateColors(count) {
        // Simple color generation - more sophisticated libraries exist
        const colors = [];
        const baseHue = Math.random() * 360; // Start at a random hue
        for (let i = 0; i < count; i++) {
             // Cycle through hues, vary saturation/lightness slightly if needed
            const hue = (baseHue + (i * (360 / (count + 1)))) % 360;
            colors.push(`hsla(${hue}, 70%, 60%, 0.6)`); // Use HSLA for easy manipulation
        }
         // Common palette fallback for small numbers
         const predefined = [
             'rgba(54, 162, 235, 0.6)', // Blue
             'rgba(255, 99, 132, 0.6)',  // Red
             'rgba(255, 206, 86, 0.6)', // Yellow
             'rgba(75, 192, 192, 0.6)', // Green
             'rgba(153, 102, 255, 0.6)',// Purple
             'rgba(255, 159, 64, 0.6)', // Orange
             'rgba(101, 176, 109, 0.6)', // Darker Green
             'rgba(230, 126, 34, 0.6)', // Darker Orange
             'rgba(41, 128, 185, 0.6)'  // Darker Blue
         ];
         return count <= predefined.length ? predefined.slice(0, count) : colors;
    }


    // --- Event Listeners ---

    // View Monthly Summary Button
    if (viewSummaryButton) {
        viewSummaryButton.addEventListener('click', () => {
            const yearMonth = parseYearMonth(summaryMonthInput.value);
            if (yearMonth) {
                fetchAndDisplaySummary(yearMonth.year, yearMonth.month);
            } else {
                showMessage(summaryError, "Please select a valid month and year for the summary.");
            }
        });
    }

    // View Yearly Summary Button
    if (viewYearlySummaryButton) {
        viewYearlySummaryButton.addEventListener('click', () => {
            const yearMonth = parseYearMonth(summaryMonthInput.value); // Get year from the same input
            if (yearMonth) {
                fetchAndDisplaySummary(yearMonth.year, null); // Pass null for month
            } else {
                 // Fallback or try to parse year differently if needed
                 showMessage(summaryError, "Please select a valid month and year to determine the year for the summary.");
            }
        });
    }

    // View Chart Button
    if (viewChartButton) {
        viewChartButton.addEventListener('click', () => {
            const yearMonth = parseYearMonth(chartMonthInput.value);
            if (yearMonth) {
                fetchAndDisplayChart(yearMonth.year, yearMonth.month);
            } else {
                showMessage(chartError, "Please select a valid month and year for the chart.");
            }
        });
    }

    // --- Initial Load ---
    function initializeReports() {
        // Set default month input values to the current month
        const today = new Date();
        const currentYear = today.getFullYear();
        const currentMonth = (today.getMonth() + 1).toString().padStart(2, '0'); // Month is 0-indexed
        const currentYearMonth = `${currentYear}-${currentMonth}`;

        if (summaryMonthInput) summaryMonthInput.value = currentYearMonth;
        if (chartMonthInput) chartMonthInput.value = currentYearMonth;

        // Fetch initial data for the current month
        fetchAndDisplaySummary(currentYear, parseInt(currentMonth, 10));
        fetchAndDisplayChart(currentYear, parseInt(currentMonth, 10));
    }

    initializeReports();

}); // End DOMContentLoaded