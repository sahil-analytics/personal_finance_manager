# 🚀 Personal Finance Manager 🚀

A web-based application designed to help individuals track and manage their personal finances by providing tools for income and expense tracking, transaction categorization, and financial data visualization.

## 📝 Project Overview

The **Personal Finance Manager** is a full-stack application that addresses the challenges of traditional, manual financial management by offering a centralized, intuitive platform for users to gain better control and insight into their financial health.  
The application was developed as a six-month industrial training report for a Bachelor of Technology degree.
![Dashboard Preview](https://github.com/sahil-analytics/personal_finance_manager/blob/main/screenshots/1_Dashboard.png)

### Key Features
* **User Authentication**: Secure user registration and login functionality.
* **Profile Management**: Users can view and update their profile information.
* **Transaction Management**: Comprehensive **CRUD** (Create, Read, Update, Delete) operations for managing both income and expense entries.
* **Transaction Categorization**: Users can create and manage custom categories for their financial entries.
* **Financial Summaries**: Provides clear monthly and yearly summaries of total income, expenses, and balance.
* **Data Visualization**: Includes an insightful pie chart to visualize spending by category for a selected month.

---

## 🛠️ Technology Stack

The project was built using a robust, full-stack approach, with a clear separation between the backend and frontend components.

### Backend
* **Java Spring Boot**: The core framework for building the RESTful API.
* **Spring Data JPA**: Used for simplified database interaction and object-relational mapping (ORM).
* **MySQL**: The relational database used for persistent data storage.

### Frontend
* **HTML/CSS/JavaScript**: Standard web technologies for structuring, styling, and adding interactivity.
* **Chart.js**: A JavaScript library used to create the data visualization pie chart.

---

## ⚙️ Development Methodology and Techniques

The project followed an **Agile-inspired iterative approach**.

### Key Techniques Used:
* **Layered Architecture**: The backend is structured into distinct layers: Controller, Service, and Repository, ensuring a separation of concerns.
* **RESTful API Design**: APIs were designed following REST principles to facilitate communication between the frontend and backend.
* **Object-Relational Mapping (ORM)**: JPA/Hibernate was used to map Java objects to database tables, simplifying data persistence.
* **Asynchronous Communication**: The Fetch API was used for asynchronous HTTP requests, allowing for dynamic UI updates without full page reloads.

---

## 🚀 Future Scope

The project has a solid foundation and can be expanded in several key areas to become a more comprehensive and secure application. Future enhancements could include:
* **Security**: Implement robust security features like password hashing and JSON Web Token (JWT) based authentication.
* **Advanced Features**: Add budgeting tools, support for recurring transactions, financial goals, and data import/export functionality.
* **UI/UX Improvements**: Transition to a modern frontend framework like React or Vue.js for a more dynamic and maintainable user interface.
* **Technical Improvements**: Implement automated testing, use Docker for containerization, and optimize for cloud deployment.
