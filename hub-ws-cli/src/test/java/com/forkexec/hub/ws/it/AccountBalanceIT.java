package com.forkexec.hub.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.InvalidCreditCardFault_Exception;
import com.forkexec.hub.ws.InvalidMoneyFault_Exception;

public class AccountBalanceIT extends BaseIT {

  String userId = "filipeom@hotmail.com";
  String invalidUserId = "hello@hotmail.com";
  int money = 30;
  String creditCardNumber = "4024007102923926";

  @Before
  public void setUp() throws InvalidUserIdFault_Exception {
    hubClient.activateAccount(userId);
  }

  //Success
  @Test
  public void accountBalanceSuccess() throws InvalidUserIdFault_Exception {
    assertEquals(hubClient.accountBalance(userId), 100);
  }

  @Test
  public void accountBalanceSuccessAfterLoadAccount() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
    hubClient.loadAccount(userId, money, creditCardNumber);
    assertEquals(hubClient.accountBalance(userId), 3400);
  }

  //Input tests
  @Test(expected = InvalidUserIdFault_Exception.class)
  public void accountBalanceNullId() throws InvalidUserIdFault_Exception {
    hubClient.accountBalance(null);
  }

  @Test(expected = InvalidUserIdFault_Exception.class)
  public void accountBalanceIdDoesNotExist() throws InvalidUserIdFault_Exception {
    hubClient.accountBalance(invalidUserId);
  }

  @After
  public void delete(){
    hubClient.ctrlClear();
  }

}
