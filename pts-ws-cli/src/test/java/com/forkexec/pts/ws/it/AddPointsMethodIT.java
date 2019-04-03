package com.forkexec.pts.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;

/**
 * Class that tests AddPoints operation
 */
public class AddPointsMethodIT extends BaseIT {

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
  public void success() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
    client.addPoints(USER, 1000);

    assertEquals(1100, client.pointsBalance(USER));
  }

  @Test
  public void maxSuccess() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
    client.addPoints(USER, Integer.MAX_VALUE-100);

    assertEquals(Integer.MAX_VALUE, client.pointsBalance(USER));
  }

  @Test
  public void minSuccess() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
    client.addPoints(USER, 0);

    assertEquals(100, client.pointsBalance(USER));
  }

  @Test(expected = InvalidPointsFault_Exception.class)
  public void negativePoints() throws InvalidPointsFault_Exception , InvalidEmailFault_Exception{
    client.addPoints(USER, -1);
  }

  @Test(expected = InvalidPointsFault_Exception.class)
  public void negativeMaxPoints() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
    client.addPoints(USER, Integer.MIN_VALUE);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void nullUser() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
    client.addPoints(null, 100);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void emptyUser() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
    client.addPoints("", 100);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void whitespaceUser() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
    client.addPoints("       ", 100);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void notAnEmail() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
    client.addPoints("email@not", 100);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void notAnEmail2() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
    client.addPoints("email@not.2", 100);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void notAnUser() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
    client.addPoints("notauser@email.com", 100);
  }
}
