// --- API.JS ---

// Define the base URL for your Spring Boot API.
// If frontend and backend are served from the exact same origin (host & port),
// you might only need paths starting with '/', e.g., '/api'.
// But using the full URL is more robust for development (e.g., using Live Server)
// and potential separate deployments.
const API_BASE_URL = 'http://localhost:8080'; // Adjust if your backend runs on a different port

/**
 * A centralized function for making API requests using fetch.
 *
 * @param {string} url - The API endpoint path (e.g., '/api/users', '/api/auth/login').
 * @param {object} [options={}] - Optional configuration object for fetch (method, body, headers, etc.).
 * @param {string} [options.method='GET'] - The HTTP method (GET, POST, PUT, DELETE, etc.).
 * @param {object|FormData|string} [options.body] - The request body. If an object, it's automatically stringified as JSON unless it's FormData.
 * @param {object} [options.headers] - Custom headers to merge with defaults.
 * @returns {Promise<any>} A promise that resolves with the parsed JSON response body, or null for 204 No Content.
 * @throws {Error} Throws an error for network issues or non-successful HTTP statuses (non-2xx),
 *                 potentially including error details from the response body.
 */
async function fetchAPI(url, options = {}) {
    const fullUrl = API_BASE_URL + url;

    // Default headers
    const defaultHeaders = {
        'Accept': 'application/json',
        // 'Content-Type' is set below based on body type
    };

    // Prepare fetch configuration
    const config = {
        method: options.method?.toUpperCase() || 'GET', // Default to GET
        headers: {
            ...defaultHeaders,
            ...options.headers, // Allow overriding/adding headers
        },
        // Add other fetch options if needed (e.g., credentials: 'include')
    };

    // Handle request body
    if (options.body) {
        if (options.body instanceof FormData) {
            // Don't set Content-Type for FormData; browser does it with boundary
            config.body = options.body;
        } else if (typeof options.body === 'object') {
            // Stringify plain objects as JSON
            config.body = JSON.stringify(options.body);
            // Set Content-Type for JSON
            config.headers['Content-Type'] = 'application/json';
        } else {
            // Assume body is already a string (or other primitive)
            config.body = options.body;
            // You might want to explicitly set Content-Type for other types if needed
            // e.g., config.headers['Content-Type'] = 'text/plain';
        }
    }

    // Ensure GET/HEAD requests don't have Content-Type or body set inappropriately
     if (config.method === 'GET' || config.method === 'HEAD') {
         delete config.body;
         delete config.headers['Content-Type']; // Content-Type not needed for GET/HEAD usually
     }


    console.log(`Making ${config.method} request to: ${fullUrl}`); // Optional: log requests

    try {
        const response = await fetch(fullUrl, config);

        // --- Handle Response ---
        if (response.ok) { // Status code 200-299
            if (response.status === 204) { // No Content
                console.log(`Request successful (204 No Content): ${config.method} ${fullUrl}`);
                return null; // Indicate success with no data to return
            }
            try {
                 const data = await response.json();
                 console.log(`Request successful (Status ${response.status}): ${config.method} ${fullUrl}`, data);
                 return data; // Return parsed JSON data
            } catch (jsonError) {
                 console.error(`Fetch API: Response OK (Status ${response.status}), but failed to parse JSON body.`, jsonError);
                 // Handle cases where backend sends non-JSON success response?
                 // Maybe return raw text: await response.text();
                 throw new Error('Received an invalid JSON response from the server.');
            }

        } else {
            // --- Handle Errors (Non-2xx status codes) ---
            let errorData = null;
            let errorMessage = `Request failed with status ${response.status}: ${response.statusText}`;

            // Try to parse error details from the response body (often JSON)
            try {
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    errorData = await response.json();
                    // Use message from backend if available (common pattern)
                    errorMessage = errorData.message || errorData.error || JSON.stringify(errorData);
                    console.error(`Server Error (Status ${response.status}):`, errorData);
                } else {
                    // If not JSON, maybe read as text
                    const textError = await response.text();
                    errorMessage = textError || errorMessage; // Use text error if available
                    console.error(`Server Error (Status ${response.status}): ${textError || '(No text body)'}`);
                }
            } catch (e) {
                console.warn('Fetch API: Could not parse error response body.', e);
                 // Stick with the initial status text message
            }

            const error = new Error(errorMessage);
            error.status = response.status; // Attach status code to the error object
            error.data = errorData;         // Attach parsed error data if available
            throw error; // Throw the error to be caught by the calling function's catch block
        }

    } catch (networkError) {
        // --- Handle Network Errors (fetch itself failed) ---
        console.error(`Network error during fetch to ${fullUrl}:`, networkError);
        throw new Error(`Network error: ${networkError.message}. Could not connect to the server.`);
    }
}

// --- Example Usage (Optional - just for demonstration) ---
/*
async function testGet() {
    try {
        const users = await fetchAPI('/api/users/1/transactions'); // Example endpoint
        console.log('Test GET successful:', users);
    } catch (error) {
        console.error('Test GET failed:', error.message, 'Status:', error.status);
    }
}

async function testPost() {
     try {
        const loginData = { email: 'test@example.com', password: 'password' };
        const user = await fetchAPI('/api/auth/login', {
             method: 'POST',
             body: loginData
         });
        console.log('Test POST successful:', user);
    } catch (error) {
        console.error('Test POST failed:', error.message, 'Status:', error.status, 'Data:', error.data);
    }
}

// Uncomment to run tests when script loads:
// testGet();
// testPost();
*/