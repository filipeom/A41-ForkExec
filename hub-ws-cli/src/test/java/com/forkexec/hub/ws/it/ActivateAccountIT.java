package com.forkexec.hub.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;

public class ActivateAccountIT extends BaseIT {

  String userId = "filipeom@hotmail.com";

  @Before
  public void setUp() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    {
      pointsClient.activateUser(userId);
    }
  }

  //Success
  @Test
  public void activateAccountSuccess() throws InvalidUserIdFault_Exception, InvalidEmailFault_Exception {
    pointsClient.ctrlClear();
    hubClient.activateAccount(userId);
    assertEquals(pointsClient.pointsBalance(userId), 100); //Verifica se existe e se os pontos inicias sao 100
  }

  // Input Tests
  @Test(expected = InvalidUserIdFault_Exception.class)
  public void activateAccountNullId() throws InvalidUserIdFault_Exception {
    hubClient.activateAccount(null);
  }

  @Test(expected = InvalidUserIdFault_Exception.class)
  public void activateAccountInvalidEmail() throws InvalidUserIdFault_Exception {
    hubClient.activateAccount("ola");
  }

  @Test(expected = InvalidUserIdFault_Exception.class)
  public void activateAccountEmailAlreadyExits() throws InvalidUserIdFault_Exception {
    hubClient.activateAccount(userId);
  }

  @After
  public void delete(){
    hubClient.ctrlClear();
    pointsClient.ctrlClear();
  }

}
