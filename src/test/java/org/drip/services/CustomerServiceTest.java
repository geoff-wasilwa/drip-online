package org.drip.services;

import junit.framework.Assert;

import org.drip.model.Customer;
import org.drip.model.User;
import org.drip.repository.CustomerRepository;
import org.drip.services.impl.CustomerServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CustomerServiceTest {
	
	@Configuration
	static class CustomerServiceTestContextConfiguration {
		
		@Bean
		public CustomerService customerService() {
			return new CustomerServiceImpl();
		}
		
		@Bean
		public CustomerRepository customerRepository() {
			return Mockito.mock(CustomerRepository.class);
		}
		
	}
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Before
	public void setup() {
		User user1 = new User("john@test.com", "xxxx");
		Customer customerWithFirstLastName = new Customer();
		customerWithFirstLastName.setFirstName("John");
		customerWithFirstLastName.setLastName("Doe");
		customerWithFirstLastName.setPhoneNumber("12345678");
		customerWithFirstLastName.setAreaCode("123");
		customerWithFirstLastName.setZipCode("12345");
		customerWithFirstLastName.setUser(user1);
		Mockito.when(customerRepository.findCustomer(Matchers.eq("John"), Matchers.eq("Doe"), Matchers.eq("12345678"), Matchers.eq("123452"), Matchers.eq("12345678"), Matchers.eq("123"))).thenReturn(customerWithFirstLastName);
		
		User user2 = new User("business@test.org", "yyyy");
		Customer customerWithBusinessName = new Customer();
		customerWithBusinessName.setBusinessName("business");
		customerWithBusinessName.setPhoneNumber("12345678");
		customerWithBusinessName.setAreaCode("123");
		customerWithBusinessName.setZipCode("12345");
		customerWithBusinessName.setUser(user2);				
		Mockito.when(customerRepository.findByEmail("business@test.org")).thenReturn(customerWithBusinessName);
		Mockito.when(customerRepository.findCustomer("business", "12345678", "123452", "12345678", "123")).thenReturn(customerWithBusinessName);
	}
	
	@Test
	public void testFindCustomerByEmail() {
		Customer customer = customerService.getCustomer("business@test.org");
		Assert.assertEquals("business", customer.getBusinessName());
		Assert.assertEquals("12345678", customer.getPhoneNumber());
		Assert.assertEquals("business@test.org", customer.getUser().getUsername());
		Mockito.verify(customerRepository, VerificationModeFactory.times(1)).findByEmail(Mockito.anyString());
	}
	
	@Test 
	public void testFindCustomerByFirstLastNameAndOtherDetails() {
		Customer customer = customerService.getCustomer("John", "Doe", "12345678", "123452", "12345678", "123");
		Assert.assertEquals("John", customer.getFirstName());
		Assert.assertEquals("Doe", customer.getLastName());
		Assert.assertEquals("12345678", customer.getPhoneNumber());
		Assert.assertEquals("john@test.com", customer.getUser().getUsername());
		Mockito.verify(customerRepository, VerificationModeFactory.times(1)).findCustomer(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	
	@Test
	public void testFindCustomerByBusinessNameAndOtherDetails() {
		Customer customer = customerService.getCustomer("business", "12345678", "123452", "12345678", "123");
		Assert.assertEquals("business", customer.getBusinessName());
		Assert.assertEquals("12345678", customer.getPhoneNumber());
		Assert.assertEquals("business@test.org", customer.getUser().getUsername());
		Mockito.verify(customerRepository, VerificationModeFactory.times(1)).findCustomer(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	
	@After
	public void reset() {		
		//Mockito.reset(customerRepository);
	}
}
