# Bank Credit Project

**Bank Credit Service** is a single microservice RESTful application that allows employees to manage customer loans —
including creating customers, creating loans, listing loans, listing installments, and processing payments.

## Microservice

bank-credit-service – provides REST APIs to manage loans and customers.

## Features

- Create customers
- Create loans
- List loans
- List installments
- Pay loan installments

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- MapStruct
- Lombok
- H2 Database
- Maven

## Project Structure

<pre>  
    bank-credit-service/
    │
    ├── src/
    │ └── main/
    │ ├── java/
    │ └── resources/
    │ ├── application.yml
    │
    ├── pom.xml
    └── README.md
</pre>

### Prerequisites

- Java 17+
- Maven

### Run the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/sengulMor/bank-credit-service.git
   cd bank-credit-service
   ```

2. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```

3. The service will be available at:
   👉 http://localhost:8080
   You’ll be asked to log in.
4. The H2 database will be available at:
   👉 http://localhost:8080/h2-console
   You’ll be asked to log in.

### 🔐 Authentication

All endpoints and the web interface are protected using Spring Security.

### Default Credentials:

<pre> 
    | Username | Password |
    |----------|----------|
    | admin    | admin    |
</pre>

You will need to enter these credentials:

1. When accessing the API via Postman, browser, or Swagger
2. When opening the H2 database console

### 🔐 Authorization via Postman

1. Go to the **Authorization** tab.
2. Select **asic Auth** as the type.
3. Enter your credentials

### 📪 Example REST Endpoints

| Method | Endpoint (examples)                                                      | Description           |
|--------|--------------------------------------------------------------------------|-----------------------|
| POST   | `/customers`                                                             | Create a new customer |
| POST   | `/credits`                                                               | Create a loan         |
| GET    | `/credits?customerId=1&isPaid=false&page=0&size=10&sort=loanAmount,desc` | List all loans        |
| GET    | `/installments?loanId=1`                                                 | List installments     |
| POST   | `/installments`                                                          | Pay an installment    |

### 🧾 Examples with body for POST Endpoints

##### POST

###### /customers

<summary>📥{
    "name": "Max",
    "surname": "Surname",
    "creditLimit": 3000,
    "usedCreditLimit": 0
}
</summary>

##### POST

###### /credits

<summary>📥{
    "customerId": 3,
    "loanAmount": 5000,
    "numberOfInstallment": 12,
    "interestRate": 0.5
}
</summary>

##### POST

###### /installments

<summary>📥{
    "loanId": 3,
    "amount": 2500
}
</summary>





