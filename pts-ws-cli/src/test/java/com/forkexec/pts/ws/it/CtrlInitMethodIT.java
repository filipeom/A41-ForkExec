package com.forkexec.pts.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.After;

import com.forkexec.pts.ws.BadInitFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;

/**
 * Class that tests CtrlInit operation
 */
public class CtrlInitMethodIT extends BaseIT {
  
  private final String USER = "test@gmail.com";

  @After
  public void cleanUp() {
    client.ctrlClear();
  }

  @Test
  public void success() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception, BadInitFault_Exception {
    client.ctrlInit(1337);

    client.activateUser(USER);

    assertEquals(1337, client.pointsBalance(USER));
  }

  @Test
  public void successOne() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception, BadInitFault_Exception {
    client.ctrlInit(1);

    client.activateUser(USER);

    assertEquals(1, client.pointsBalance(USER));
  }

  @Test
  public void successMax() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception, BadInitFault_Exception {
    client.ctrlInit(Integer.MAX_VALUE);

    client.activateUser(USER);

    assertEquals(Integer.MAX_VALUE, client.pointsBalance(USER));
  }

  @Test
  public void zeroPoints() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception, BadInitFault_Exception {
    client.ctrlInit(0);

    client.activateUser(USER);

    assertEquals(0, client.pointsBalance(USER));
  }

@Test(expected = BadInitFault_Exception.class)
  public void minusOnePoints() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception, BadInitFault_Exception {
    client.ctrlInit(-1);
  }

@Test(expected = BadInitFault_Exception.class)
  public void allNegativePoints() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception, BadInitFault_Exception {
    client.ctrlInit(Integer.MIN_VALUE);
  }
}
