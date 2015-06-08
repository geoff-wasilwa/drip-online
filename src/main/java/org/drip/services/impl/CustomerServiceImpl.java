package org.drip.services.impl;

import org.drip.model.Customer;
import org.drip.repository.CustomerRepository;
import org.drip.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {
	
	@Autowired
	private CustomerRepository customerRepository;

	@Override
    public Customer getCustomer(String firstName, String lastName, String accountNumber, String areaCode, String phoneNumber, String zipCode) {
		return customerRepository.findCustomer(firstName, lastName, accountNumber, areaCode, phoneNumber, zipCode);
	}
	
	@Override
    public Customer getCustomer(String businessName, String accountNumber,String areaCode, String phoneNumber, String zipCode) {
		return customerRepository.findCustomer(businessName, accountNumber, areaCode, phoneNumber, zipCode);
	}
	
	@Override
    public Customer getCustomer(String email) {
		return customerRepository.findByEmail(email);
	}

	@Override
    public Customer saveCustomer(Customer customer) {
	    return customerRepository.save(customer);
    }	
}
