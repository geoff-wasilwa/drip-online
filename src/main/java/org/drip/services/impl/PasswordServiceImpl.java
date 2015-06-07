package org.drip.services.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.drip.model.Customer;
import org.drip.model.ResetHash;
import org.drip.model.User;
import org.drip.repository.HashRepository;
import org.drip.services.PasswordService;
import org.drip.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class PasswordServiceImpl implements PasswordService {
	
	@Autowired
	HashRepository resetHashRepository;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Override
	public void sendResetLink(String email, String resetUrl) throws MessagingException, MailException {
		Customer customer = customerService.getCustomer(email);
		ResetHash resetHash = saveHash(customer);		
		final Context ctx = new Context();
		ctx.setVariable("resetUrl", resetUrl + "/password/reset?hash=" + resetHash.getHash());
		ctx.setVariable("user", customer);
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
		message.setSubject("Reset Password");
		message.setTo(email);
		final String htmlContent = this.templateEngine.process("email-template", ctx);
		message.setText(htmlContent, true);		
		this.mailSender.send(mimeMessage);
	}
	
	private String generateHash() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(260, random).toString(32);
	}
	
	private ResetHash saveHash(Customer customer) {
		ResetHash resetHash = new ResetHash();
		resetHash.setUser(customer.getUser());
		resetHash.setHash(generateHash());
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 1);
		resetHash.setExpireDate(calendar.getTime());
		return resetHashRepository.save(resetHash);
	}

	@Override
    public User getUserAssociatedWithHash(String hash) {
		ResetHash resetHash = resetHashRepository.findByHash(hash);
	    if (new Date().before(resetHash.getExpireDate())) {
	    	return resetHash.getUser();
	    }
	    return null;
    }

	@Override
    public Boolean updatePassword(String username, String newPassword) {
		Customer customer = customerService.getCustomer(username);
	    User user = customer.getUser();
	    user.setPassword(newPassword);
	    user.getResetHash().clear();
	    return customerService.saveCustomer(customer) != null;
    }
	
}
