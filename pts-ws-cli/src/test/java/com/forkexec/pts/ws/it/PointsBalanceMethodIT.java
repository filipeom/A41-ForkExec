package com.forkexec.pts.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;

/**
 * Class that tests PointsBalance operation
 */
public class PointsBalanceMethodIT extends BaseIT {

  @Before
  public void setUp() {
  }

  @After
  public void cleanUp() {
    client.ctrlClear();
  }

  @Test
  public void success() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("test@email.com");

    assertEquals(100, client.pointsBalance("test@email.com"));
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void nullUserIdentifier() throws InvalidEmailFault_Exception {
    client.pointsBalance(null);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void emptyUserIdentifier() throws InvalidEmailFault_Exception {
    client.pointsBalance("");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithWhiteSpace() throws InvalidEmailFault_Exception {
    client.pointsBalance("test @email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithPercent() throws InvalidEmailFault_Exception {
    client.pointsBalance("test%s@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithAsterisk() throws InvalidEmailFault_Exception {
    client.pointsBalance("test*@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithPlus() throws InvalidEmailFault_Exception {
    client.pointsBalance("test+@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithWierdA() throws InvalidEmailFault_Exception {
    client.pointsBalance("testª@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithTilde() throws InvalidEmailFault_Exception {
    client.pointsBalance("test~@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithArrow() throws InvalidEmailFault_Exception {
    client.pointsBalance("test<@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithNewline() throws InvalidEmailFault_Exception {
    client.pointsBalance("test\n@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void invalidUserIdentifier() throws InvalidEmailFault_Exception {
    client.pointsBalance("notAnEmail");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userDoesntExist() throws InvalidEmailFault_Exception {
    client.pointsBalance("test@email.com");
  }
}
