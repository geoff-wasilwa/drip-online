package org.drip.repository;

import org.drip.model.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface CustomerRepository extends CrudRepository<Customer, Long> {
	
	
	@Query("SELECT dripUser FROM DripUser dripUser inner join dripUser.accountNumbers accNumbers WHERE dripUser.firstName =:firstName and dripUser.lastName =:lastName and dripUser.phoneNumber =:phoneNumber and dripUser.zipCode =:zipCode and dripUser.areaCode =:areaCode and accNumbers.accountNumber =:accountNumber")
	public Customer findCustomer(@Param("firstName")  String firstName, @Param("lastName") String lastName, @Param("accountNumber") String accountNumber, @Param("areaCode") String areaCode, @Param("phoneNumber") String phoneNumber, @Param("zipCode") String zipCode);
	
	@Query("SELECT dripUser FROM DripUser dripUser inner join dripUser.accountNumbers accNumbers WHERE dripUser.businessName =:businessName and dripUser.phoneNumber =:phoneNumber and dripUser.zipCode =:zipCode and dripUser.areaCode =:areaCode and accNumbers.accountNumber =:accountNumber")
	public Customer findCustomer(@Param("businessName") String businessName, @Param("accountNumber") String accountNumber, @Param("areaCode") String areaCode, @Param("phoneNumber") String phoneNumber, @Param("zipCode") String zipCode);
	
	Customer findByEmail(String email);
}
