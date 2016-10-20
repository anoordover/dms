package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.exceptions.LoginFailureException;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.MissingConfigurationException;
import nl.hetcak.dms.ia.web.exceptions.ServerConnectionFailureException;
import org.junit.Test;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ExecptionsTest {
    //zinloze tests; dit test de werking van java.lang.Exception.
    
    @Test(expected = LoginFailureException.class)
    public void throwLoginException1() throws  LoginFailureException {
        throw new LoginFailureException("Test");
    }
    
    @Test(expected = LoginFailureException.class)
    public void throwLoginException2() throws  LoginFailureException {
        Exception error = new Exception();
        throw new LoginFailureException("Test", error);
    }
    
    @Test(expected = LoginFailureException.class)
    public void throwLoginException3() throws  LoginFailureException {
        Exception error = new Exception();
        throw new LoginFailureException(error);
    }
    
    @Test(expected = LoginFailureException.class)
    public void throwLoginException4() throws  LoginFailureException {
        Exception error = new Exception();
        throw new LoginFailureException("Test", error, true, true);
    }
    
    //Misconfiguration
    @Test(expected = MisconfigurationException.class)
    public void throwMisconfigurationException1() throws  MisconfigurationException {
        throw new MisconfigurationException("Test");
    }
    
    @Test(expected = MisconfigurationException.class)
    public void throwMisconfigurationException2() throws  MisconfigurationException {
        Exception error = new Exception();
        throw new MisconfigurationException("Test", error);
    }
    
    @Test(expected = MisconfigurationException.class)
    public void throwMisconfigurationException3() throws  MisconfigurationException {
        Exception error = new Exception();
        throw new MisconfigurationException(error);
    }
    
    @Test(expected = MisconfigurationException.class)
    public void throwMisconfigurationException4() throws  MisconfigurationException {
        Exception error = new Exception();
        throw new MisconfigurationException("Test", error, true, true);
    }
    
    //MissingConfigurationException
    @Test(expected = MissingConfigurationException.class)
    public void throwMissingconfigurationException1() throws  MissingConfigurationException {
        throw new MissingConfigurationException("Test");
    }
    
    @Test(expected = MissingConfigurationException.class)
    public void throwMissingconfigurationException2() throws  MissingConfigurationException {
        Exception error = new Exception();
        throw new MissingConfigurationException("Test", error);
    }
    
    @Test(expected = MissingConfigurationException.class)
    public void throwMissingconfigurationException3() throws  MissingConfigurationException {
        Exception error = new Exception();
        throw new MissingConfigurationException(error);
    }
    
    @Test(expected = MissingConfigurationException.class)
    public void throwMissingconfigurationException4() throws  MissingConfigurationException {
        Exception error = new Exception();
        throw new MissingConfigurationException("Test", error, true, true);
    }
    
    //ServerConnectionFailureException
    @Test(expected = ServerConnectionFailureException.class)
    public void throwServerConnectionFailureException1() throws ServerConnectionFailureException {
        throw new ServerConnectionFailureException("Test");
    }
    
    @Test(expected = ServerConnectionFailureException.class)
    public void throwServerConnectionFailureException2() throws  ServerConnectionFailureException {
        Exception error = new Exception();
        throw new ServerConnectionFailureException("Test", error);
    }
    
    @Test(expected = ServerConnectionFailureException.class)
    public void throwServerConnectionFailureException3() throws  ServerConnectionFailureException {
        Exception error = new Exception();
        throw new ServerConnectionFailureException(error);
    }
    
    @Test(expected = ServerConnectionFailureException.class)
    public void throwServerConnectionFailureException4() throws  ServerConnectionFailureException {
        Exception error = new Exception();
        throw new ServerConnectionFailureException("Test", error, true, true);
    }
}
