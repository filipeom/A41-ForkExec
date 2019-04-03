package com.forkexec.cc.ws.it;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Class that tests Ping operation
 */
public class ValidateNumberIT extends BaseIT {

	// tests
	// assertEquals(expected, actual);

	// public String ping(String x)

	@Test
	public void success() {
		assertTrue(client.validateNumber("4024007102923926"));
	}

	@Test
 	public void insuccess() {
 	  assertFalse(client.validateNumber("1234567890123456"));
	}

}
