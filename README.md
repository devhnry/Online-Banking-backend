# Online Banking System API

## Overview

Welcome to the **OnlineBankSystem API** repository. This backend service imitates the features of an OnlineBanking Application with a few tweaks .

### Some Features of the Application.

- Account Login and Signup
- CRUD Operations on the Account Created such as View Balance, Transfer e.t.c
- Authorisation and Authentication based on Roles
- Virtual Account System to create external Accounts


## Endpoints (Authentication)
- **POST /api/v1/auth/signup**: SignUp for an Account.
- **POST /api/v1/auth/login**: Login to the Account Created.
- **POST /api/v1/auth/refreshToken**: Refresh Access authToken that grants authorisation.

## Endpoints (User Accounts)
- **GET /api/v1/account/balance: Allows a user to view their balance**
- **POST /api/v1/account/deposit: Allows a user to make deposit into the account created**
- **POST /api/v1/account/withdraw: Allows a user to withdraw from the account created**
- **POST /api/v1/account/transfer: Allows a user to transfer to another user**
- **PUT /transactionLimitDto: Allows a user to update the transaction limit for the account**
- **GET /api/v1/account/generateOtp**: Generates an OTP to perform account functions.
- **PUT /api/v1/account/resetPassword**: Allows a User to Reset Account Password.
- **PATCH /api/v1/account/updateProfile**: Allows a User to update their profile details.

## Endpoints (Admin)
- **PUT /api/v1/admin/suspend/{id}**: Allows an Admin to Suspend User Account.

## Endpoints (Kora Virtual Accounts)
- **GET /api/v1/account/get-vba-details**: Get details related to your virtual account.
- **GET /api/v1/account/get-all-vba-details**: Get All Virtual Accounts related to that customer.
- **GET /api/v1/account/transactions**: View all transactions made with your account.
- **POST /api/v1/account/create-virtual-account**: Creates Virtual Account for a User.
- **POST /api/v1/account/fundAccount**: Credits the Virtual Account created.

## Contribution

Contributions are welcome!
If you have any suggestions, improvements, or bug fixes,
please feel free to open an issue or submit a pull request.

