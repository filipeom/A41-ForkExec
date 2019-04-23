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
import com.forkexec.pts.ws.NotEnoughBalanceFault_Exception;

public class SpendPointsIT extends BaseIT {

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
	public void spendPointsEnoughBalanceTest()
			throws InvalidEmailFault_Exception, NotEnoughBalanceFault_Exception, InvalidPointsFault_Exception {
		final int toSpend = USER_POINTS / 10;
		client.spendPoints(VALID_USER, toSpend);
		assertEquals(USER_POINTS - toSpend, client.pointsBalance(VALID_USER));
	}

	@Test
	public void spendEntireBalanceTest()
			throws InvalidEmailFault_Exception, NotEnoughBalanceFault_Exception, InvalidPointsFault_Exception {
		client.spendPoints(VALID_USER, USER_POINTS);
		assertEquals(0, client.pointsBalance(VALID_USER));
	}

	@Test(expected = InvalidPointsFault_Exception.class)
	public void spendNoPointsTest()
			throws InvalidEmailFault_Exception, NotEnoughBalanceFault_Exception, InvalidPointsFault_Exception {
		client.spendPoints(VALID_USER, 0);
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void unknownUserTest()
			throws InvalidEmailFault_Exception, NotEnoughBalanceFault_Exception, InvalidPointsFault_Exception {
		client.spendPoints(UNKNOWN_USER, 20);
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void nullEmailTest()
			throws InvalidEmailFault_Exception, NotEnoughBalanceFault_Exception, InvalidPointsFault_Exception {
		client.spendPoints(null, 20);
	}

	@Test
	public void notEnoughBalanceTest() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
		try {
			client.spendPoints(VALID_USER, USER_POINTS + 1);
			fail();
		} catch (NotEnoughBalanceFault_Exception e) {
			assertEquals(USER_POINTS, client.pointsBalance(VALID_USER));
		}
	}
}
