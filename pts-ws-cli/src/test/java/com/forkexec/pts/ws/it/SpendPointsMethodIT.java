package com.forkexec.pts.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.NotEnoughBalanceFault_Exception;

/**
 * Class that tests SpendPoints operation
 */
public class SpendPointsMethodIT extends BaseIT {

  private final String USER = "test@email.com";

  @Before
  public void setUp() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception {
    client.activateUser(USER);
  }

  @After
  public void cleanUp() {
    client.ctrlClear();
  }

  @Test
  public void success() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
    client.spendPoints(USER, 10);

    assertEquals(90, client.pointsBalance(USER));
  }

  @Test
  public void spentAllPointsSuccess() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
    client.spendPoints(USER, 100);

    assertEquals(0, client.pointsBalance(USER));
  }


  @Test
  public void spendZeroPoints() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
    client.spendPoints(USER, 0);

    assertEquals(100, client.pointsBalance(USER));
  }

  @Test(expected = NotEnoughBalanceFault_Exception.class)
  public void spendMaxPlusOnePoints() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
    client.spendPoints(USER, 101);
  }

  @Test(expected = NotEnoughBalanceFault_Exception.class)
  public void spendMaxPoints() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
    client.spendPoints(USER, Integer.MIN_VALUE-100);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void nullUser() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception, NotEnoughBalanceFault_Exception {
    client.spendPoints(null, 100);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void emptyUser() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception, NotEnoughBalanceFault_Exception {
    client.spendPoints("", 100);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void whitespaceUser() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception, NotEnoughBalanceFault_Exception {
    client.spendPoints("       ", 100);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void notAnEmail() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception, NotEnoughBalanceFault_Exception {
    client.spendPoints("email@not", 100);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void notAnEmail2() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception, NotEnoughBalanceFault_Exception {
    client.spendPoints("email@not.2", 100);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void notAnUser() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception, NotEnoughBalanceFault_Exception {
    client.spendPoints("notauser@email.com", 100);
  }
}
