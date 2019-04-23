package com.forkexec.pts.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.forkexec.pts.ws.BadInitFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;

public class AddPointsIT extends BaseIT {
	@Before
	public void setUp() throws BadInitFault_Exception, EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.ctrlInit(USER_POINTS);
		client.activateUser(VALID_USER);
	}

	@After
	public void tearDown() {
		pointsTestClear();
	}

	@Test
	public void addPointsTest() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
		client.addPoints(VALID_USER, 1100);
		assertEquals(USER_POINTS + 1100, client.pointsBalance(VALID_USER));
	}

	@Test
	public void noPaymentTest() throws InvalidEmailFault_Exception {
		try {
			client.addPoints(VALID_USER, 0);
			fail();
		} catch (InvalidPointsFault_Exception e) {
			assertEquals(USER_POINTS, client.pointsBalance(VALID_USER));
		}
	}

	@Test
	public void negativeValueTest() throws InvalidEmailFault_Exception {
		try {
			client.addPoints(VALID_USER, -10);
			fail();
		} catch (InvalidPointsFault_Exception e) {
			assertEquals(USER_POINTS, client.pointsBalance(VALID_USER));
		}
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void unknownUserTest() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
		client.addPoints(UNKNOWN_USER, 10);
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void nullEmailTest() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
		client.addPoints(null, 10);
	}
}
