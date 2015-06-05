package org.drip.services;

import org.drip.model.DripUser;
import org.drip.model.User;

public interface UserService {
	
	public abstract DripUser getUser(String firstName, String lastName, String accountNumber,String areaCode, String phoneNumber, String zipCode);
	
	public abstract DripUser getUser(String businessName, String accountNumber,String areaCode, String phoneNumber, String zipCode);
	
	public abstract User getUser(String email);
	
	User registerUser(User user);
	
	User saveUser(User user);
	
}
