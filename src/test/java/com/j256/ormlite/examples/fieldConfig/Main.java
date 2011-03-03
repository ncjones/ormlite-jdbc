package com.j256.ormlite.examples.fieldConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

/**
 * Main sample routine to show how to do basic operations with the package.
 * 
 * <p>
 * <b>NOTE:</b> We use asserts in a couple of places to verify the results but if this were actual production code, we
 * would have proper error handling.
 * </p>
 */
public class Main {

	// we are using the in-memory H2 database
	private final static String DATABASE_URL = "jdbc:h2:mem:account";

	private Dao<Account, Integer> accountDao;
	private Dao<Delivery, Integer> deliveryDao;

	public static void main(String[] args) throws Exception {
		// turn our static method into an instance of Main
		new Main().doMain(args);
	}

	private void doMain(String[] args) throws Exception {
		JdbcConnectionSource connectionSource = null;
		try {
			// create our data-source for the database
			connectionSource = new JdbcConnectionSource(DATABASE_URL);
			// setup our database and DAOs
			setupDatabase(connectionSource);
			// read and write some data
			readWriteData();
			System.out.println("\n\nIt seems to have worked\n\n");
		} finally {
			// destroy the data source which should close underlying connections
			if (connectionSource != null) {
				connectionSource.close();
			}
		}
	}

	/**
	 * Setup our database and DAOs
	 */
	private void setupDatabase(ConnectionSource connectionSource) throws Exception {

		DatabaseTableConfig<Account> accountTableConfig = buildAccountTableConfig();
		accountDao = new BaseDaoImpl<Account, Integer>(connectionSource, accountTableConfig) {
		};

		DatabaseTableConfig<Delivery> deliveryTableConfig = buildDeliveryTableConfig(accountTableConfig);
		deliveryDao = new BaseDaoImpl<Delivery, Integer>(connectionSource, deliveryTableConfig) {
		};

		// if you need to create the table
		TableUtils.createTable(connectionSource, accountTableConfig);
		TableUtils.createTable(connectionSource, deliveryTableConfig);
	}

	private DatabaseTableConfig<Account> buildAccountTableConfig() {
		ArrayList<DatabaseFieldConfig> fieldConfigs = new ArrayList<DatabaseFieldConfig>();
		fieldConfigs.add(new DatabaseFieldConfig("id", null, DataType.UNKNOWN, null, 0, false, false, true, null,
				false, null, false, null, false, null, false, null, null, false));
		fieldConfigs.add(new DatabaseFieldConfig("name", Account.NAME_FIELD_NAME, DataType.UNKNOWN, null, 0, false,
				false, false, null, false, null, false, null, false, null, false, null, null, false));
		fieldConfigs.add(new DatabaseFieldConfig("password", Account.PASSWORD_FIELD_NAME, DataType.UNKNOWN, null, 0,
				true, false, false, null, false, null, false, null, false, null, false, null, null, false));
		DatabaseTableConfig<Account> tableConfig = new DatabaseTableConfig<Account>(Account.class, fieldConfigs);
		return tableConfig;
	}

	private DatabaseTableConfig<Delivery> buildDeliveryTableConfig(DatabaseTableConfig<Account> accountTableConfig) {
		ArrayList<DatabaseFieldConfig> fieldConfigs = new ArrayList<DatabaseFieldConfig>();
		fieldConfigs.add(new DatabaseFieldConfig("id", null, DataType.UNKNOWN, null, 0, false, false, true, null,
				false, null, false, null, false, null, false, null, null, false));
		fieldConfigs.add(new DatabaseFieldConfig("when", null, DataType.UNKNOWN, null, 0, false, false, false, null,
				false, null, false, null, false, null, false, null, null, false));
		fieldConfigs.add(new DatabaseFieldConfig("signedBy", null, DataType.UNKNOWN, null, 0, false, false, false,
				null, false, null, false, null, false, null, false, null, null, false));
		fieldConfigs.add(new DatabaseFieldConfig("account", null, DataType.UNKNOWN, null, 0, false, false, false, null,
				true, accountTableConfig, false, null, false, null, false, null, null, false));
		DatabaseTableConfig<Delivery> tableConfig = new DatabaseTableConfig<Delivery>(Delivery.class, fieldConfigs);
		return tableConfig;
	}

	/**
	 * Read and write some example data.
	 */
	private void readWriteData() throws Exception {
		// create an instance of Account
		String name = "Jim Coakley";
		Account account = new Account(name);
		// persist the account object to the database, it should return 1
		if (accountDao.create(account) != 1) {
			throw new Exception("Could not create Account in database");
		}

		Delivery delivery = new Delivery(new Date(), "Mr. Ed", account);
		// persist the account object to the database, it should return 1
		if (deliveryDao.create(delivery) != 1) {
			throw new Exception("Could not create Delivery in database");
		}

		Delivery delivery2 = deliveryDao.queryForId(delivery.getId());
		assertNotNull(delivery2);
		assertEquals(delivery.getId(), delivery2.getId());
		assertEquals(account.getId(), delivery2.getAccount().getId());
	}
}
