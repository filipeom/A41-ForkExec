package com.forkexec.hub.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.InvalidCreditCardFault_Exception;
import com.forkexec.hub.ws.InvalidMoneyFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;

import com.forkexec.pts.ws.cli.exception.*;

/**
 * Class that tests LoadAccountIT operation
 */
public class LoadAccountIT extends BaseIT {

  String userId = "filipeom@hotmail.com";
  int moneyToAdd = 50;
  String creditCardNumber = "4024007102923926";

  @Before
  public void setUp() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    {
      pointsClient.activateUser(userId);
    }
  }

  //Success
  @Test
  public void loadAccountSuccess() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception, InvalidEmailFault_Exception {
    hubClient.loadAccount(userId, moneyToAdd, creditCardNumber);

    assertEquals(pointsClient.pointsBalance(userId), 5600);
  }

  // Input Tests
  @Test(expected = InvalidUserIdFault_Exception.class)
  public void loadAccountNullId() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
    hubClient.loadAccount(null, moneyToAdd, creditCardNumber);
  }

  @Test(expected = InvalidMoneyFault_Exception.class)
  public void loadAccountZeroMoney() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
    hubClient.loadAccount(userId, 0, creditCardNumber);
  }

  @Test(expected = InvalidCreditCardFault_Exception.class)
  public void loadAccountNullCreditCartNumber() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
    hubClient.loadAccount(userId, moneyToAdd, null);
  }

  @Test(expected = InvalidCreditCardFault_Exception.class)
  public void loadAccountInvalidCreditCartNumber() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
    hubClient.loadAccount(userId, moneyToAdd, "123456789");
  }

  @Test(expected = InvalidMoneyFault_Exception.class)
  public void loadAccountNotConvertibleValue() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
    hubClient.loadAccount(userId, 40, creditCardNumber);
  }

  @Test(expected = InvalidMoneyFault_Exception.class)
  public void loadAccountNegativeValue() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
    hubClient.loadAccount(userId, -40, creditCardNumber);
  }

  @After
  public void delete(){
    hubClient.ctrlClear();
  }

}
