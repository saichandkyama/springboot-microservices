package com.mylearning.accounts.service.impl;

import com.mylearning.accounts.constans.AccountsConstants;
import com.mylearning.accounts.dto.AccountsDto;
import com.mylearning.accounts.dto.CustomerDto;
import com.mylearning.accounts.entity.Accounts;
import com.mylearning.accounts.entity.Customer;
import com.mylearning.accounts.exception.CustomerAlreadyExistsException;
import com.mylearning.accounts.exception.ResourceNotFoundException;
import com.mylearning.accounts.mapper.AccountsMapper;
import com.mylearning.accounts.mapper.CustomerMapper;
import com.mylearning.accounts.repository.AccountsRepository;
import com.mylearning.accounts.repository.CustomerRepository;
import com.mylearning.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {
    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    /**
     * Creates a new account for the given customer.
     *
     * <p>This service will create a new account with default settings.
     */
    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobile number: " + customerDto.getMobileNumber());
        }
        Customer savedCustomer = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
    }

    /**
     * Fetches the account details for the given mobile number.
     *
     * @param mobileNumber the mobile number to search for
     * @return the account details for the given mobile number
     */
    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "Mobile Number", mobileNumber));
        Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "Customer Id", customer.getCustomerId().toString()));
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        AccountsDto accountsDto = AccountsMapper.mapToAccountsDto(account, new AccountsDto());
        customerDto.setAccountsDto(accountsDto);
        return customerDto;
    }

    /**
     * Updates the customer and its associated account details.
     *
     * <p>This service will update the customer and the associated account details if the
     * account number is found. If the account number is not found, a
     * {@link ResourceNotFoundException} is thrown.
     *
     * @param customerDto the customer details to be updated
     * @return true if the customer and its associated account is updated successfully,
     *         false otherwise
     */
    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        if(accountsDto !=null ){
            Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "AccountNumber", accountsDto.getAccountNumber().toString())
            );
            AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = accountsRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "CustomerID", customerId.toString())
            );
            CustomerMapper.mapToCustomer(customerDto,customer);
            customerRepository.save(customer);
            isUpdated = true;
        }
        return  isUpdated;
    }

    /**
     * Deletes the account and associated customer record for the given mobile number.
     *
     * <p>This service will first find the customer by the provided mobile number. If found,
     * it will delete the customer's account and then the customer record itself. If the customer
     * is not found, a {@link ResourceNotFoundException} is thrown.
     *
     * @param mobileNumber the mobile number of the customer whose account is to be deleted
     * @return true if the account and customer record are deleted successfully
     * @throws ResourceNotFoundException if no customer is found with the given mobile number
     */
    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "Mobile Number", mobileNumber));
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

    /**
     * @param customer - Customer Object
     * @return the new account details
     */
    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        return newAccount;
    }
}
