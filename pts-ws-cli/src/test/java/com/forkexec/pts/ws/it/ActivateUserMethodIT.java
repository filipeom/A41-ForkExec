package com.forkexec.pts.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;

/**
 * Class that tests ActivateUser operation
 */
public class ActivateUserMethodIT extends BaseIT {

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
  public void nullUserIdentifier() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser(null);
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void emptyUserIdentifier() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithWhiteSpace() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("test @email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithPercent() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("test%s@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithAsterisk() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("test*@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithPlus() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("test+@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithWierdA() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("testª@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithTilde() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("test~@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithArrow() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("test<@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void userIdentifierWithNewline() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("test\n@email.com");
  }

  @Test(expected = InvalidEmailFault_Exception.class)
  public void invalidUserIdentifier() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("notAnEmail");
  }

  @Test(expected = EmailAlreadyExistsFault_Exception.class)
  public void duplicateUserIdentifier() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("test@email.com");
    client.activateUser("test@email.com");
  }

  @Test(expected = EmailAlreadyExistsFault_Exception.class)
  public void duplicateUserIdentifierWithAlotOfEmails() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("test@email.ml");
    client.activateUser("test@email.pt");
    client.activateUser("test@email.zl");
    client.activateUser("test@email.mb");
    client.activateUser("test@email.mm");
    client.activateUser("test@email.se");
    client.activateUser("test@email.ok");
    client.activateUser("test@email.nk");
    client.activateUser("test@email.ts");
    client.activateUser("test@email.ml");
  }


  @Test
  public void successWithSameEmailAfterClear() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    client.activateUser("test@email.com");
    client.ctrlClear();
    client.activateUser("test@email.com");

    assertEquals(100, client.pointsBalance("test@email.com"));
  }
}
