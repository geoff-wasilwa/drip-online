package org.drip.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.apache.commons.lang.time.DateUtils;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.drip.config.TestConfig;
import org.drip.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
@ContextConfiguration(classes = { TestConfig.class })
@DatabaseSetup("classpath:testData.xml")
public class PasswordServiceTest {
	
	@Autowired
	PasswordService passwordService;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	DataSource dataSource;
	
	@Before
	public void setup() throws DatabaseUnitException, DataSetException, FileNotFoundException, SQLException {
	    Connection connection = DataSourceUtils.getConnection(dataSource);
		IDatabaseConnection databaseConnection = new DatabaseConnection(connection);
		String dataSetFile = "src/test/resources/hashData.xml";
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(dataSetFile));
		ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet);
		rDataSet.addReplacementObject("[expired]", DateUtils.addDays(new Date(), -1));
		rDataSet.addReplacementObject("[not_expired]", DateUtils.addDays(new Date(), 1));
		DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, rDataSet);
    }
	
	@After
	public void tearDown() throws DatabaseUnitException, DataSetException, FileNotFoundException, SQLException {
	    Connection connection = DataSourceUtils.getConnection(dataSource);
		IDatabaseConnection databaseConnection = new DatabaseConnection(connection);
		String dataSetFile = "src/test/resources/hashData.xml";
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(dataSetFile));
		ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet);
		rDataSet.addReplacementObject("[expired]", DateUtils.addDays(new Date(), -1));
		rDataSet.addReplacementObject("[not_expired]", DateUtils.addDays(new Date(), 1));
		DatabaseOperation.DELETE.execute(databaseConnection, rDataSet);
    }
	
	@Test
	@Transactional
	public void testUpdatePassword() {
		User userBeforePasswordChange = customerService.getCustomer("business@test.com").getUser();				
		String oldPassword = userBeforePasswordChange.getPassword();
		passwordService.updatePassword("business@test.com", "secret2");
		User userAfterPasswordChange = customerService.getCustomer("business@test.com").getUser();
		String newPassword = userAfterPasswordChange.getPassword();
		BCryptPasswordEncoder passwordEncorder = new BCryptPasswordEncoder();
		assertTrue(passwordEncorder.matches("secret", oldPassword));
		assertTrue(passwordEncorder.matches("secret2", newPassword));
	}
	
	@Test	
	public void testGetUserAssociatedWithHash() throws Exception {		
		User user = passwordService.getUserAssociatedWithHash("d41d8cd98f00b204e9800998ecf8427e");
		assertNotNull(user);
	}	
	
	@Test
	public void testGetUserAssociatedWithExpiredHash() {
		User user = passwordService.getUserAssociatedWithHash("d41d8cd98f00b204e9800998ecf84277");
		assertNull(user);
	}
	
}
