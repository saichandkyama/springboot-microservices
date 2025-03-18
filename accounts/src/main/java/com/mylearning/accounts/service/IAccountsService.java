package com.mylearning.accounts.service;

import com.mylearning.accounts.dto.CustomerDto;

public interface IAccountsService {
    /**
     * Create a new account with the given customer details.
     */
    void createAccount(CustomerDto customerDto);
    /**
     * Fetches the account details for the given mobile number.
     *
     * @param mobileNumber the mobile number to search for
     * @return the account details for the given mobile number
     */
    CustomerDto fetchAccount(String mobileNumber);

    /**
     * Updates the account details for the given customer.
     *
     * @param customerDto the customer details to update
     * @return true if the update was successful, false otherwise
     */
    boolean updateAccount(CustomerDto customerDto);

    /**
     * Deletes the account for the given mobile number.
     *
     * @param mobileNumber the mobile number to search for
     * @return true if the account was successfully deleted, false otherwise
     */
    boolean deleteAccount(String mobileNumber);
}
