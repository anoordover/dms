package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.exceptions.*;
import org.junit.Test;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * This test will test the throw ability of the custom exceptions.
 * They should be handled as normal exceptions.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ExceptionsTest {
    
    @Test(expected = LoginFailureException.class)
    public void throwLoginException() throws  LoginFailureException {
        throw new LoginFailureException();
    }
    
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
    public void throwMisconfigurationException() throws  MisconfigurationException {
        throw new MisconfigurationException();
    }
    
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
    public void throwMissingconfigurationException() throws  MissingConfigurationException {
        throw new MissingConfigurationException();
    }
    
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
    public void throwServerConnectionFailureException() throws ServerConnectionFailureException {
        throw new ServerConnectionFailureException();
    }
    
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
    
    //UnexpectedResultException
    //deprecated
    @Test(expected = UnexpectedResultException.class)
    public void throwUnexpectedResultException() throws UnexpectedResultException {
        throw new UnexpectedResultException();
    }
    
    @Test(expected = UnexpectedResultException.class)
    public void throwUnexpectedResultException1() throws UnexpectedResultException {
        throw new UnexpectedResultException("Test");
    }
    
    @Test(expected = UnexpectedResultException.class)
    public void throwUnexpectedResultException2() throws  UnexpectedResultException {
        Exception error = new Exception();
        throw new UnexpectedResultException("Test", error);
    }
    
    @Test(expected = UnexpectedResultException.class)
    public void throwUnexpectedResultException3() throws  UnexpectedResultException {
        Exception error = new Exception();
        throw new UnexpectedResultException(error);
    }
    
    @Test(expected = UnexpectedResultException.class)
    public void throwUnexpectedResultException4() throws  UnexpectedResultException {
        Exception error = new Exception();
        throw new UnexpectedResultException("Test", error, true, true);
    }
    
    //content grabbing exception
    
    @Test(expected = ContentGrabbingException.class)
    public void throwContentGrabbingException() throws ContentGrabbingException {
        throw new ContentGrabbingException();
    }
    
    @Test(expected = ContentGrabbingException.class)
    public void throwContentGrabbingException1() throws ContentGrabbingException {
        throw new ContentGrabbingException("Test");
    }
    
    @Test(expected = ContentGrabbingException.class)
    public void throwContentGrabbingException2() throws  ContentGrabbingException {
        Exception error = new Exception();
        throw new ContentGrabbingException("Test", error);
    }
    
    @Test(expected = ContentGrabbingException.class)
    public void throwContentGrabbingException3() throws  ContentGrabbingException {
        Exception error = new Exception();
        throw new ContentGrabbingException(error);
    }
    
    @Test(expected = ContentGrabbingException.class)
    public void throwContentGrabbingException4() throws  ContentGrabbingException {
        Exception error = new Exception();
        throw new ContentGrabbingException("Test", error, true, true);
    }

    //ToManyResults
    @Test(expected = TooManyResultsException.class)
    public void throwToManyResultsException() throws TooManyResultsException {
        throw new TooManyResultsException();
    }

    @Test(expected = TooManyResultsException.class)
    public void throwToManyResultsException1() throws TooManyResultsException {
        throw new TooManyResultsException("Test");
    }

    @Test(expected = TooManyResultsException.class)
    public void throwToManyResultsException2() throws TooManyResultsException {
        Exception error = new Exception();
        throw new TooManyResultsException("Test", error);
    }

    @Test(expected = TooManyResultsException.class)
    public void throwToManyResultsException3() throws TooManyResultsException {
        Exception error = new Exception();
        throw new TooManyResultsException(error);
    }

    @Test(expected = TooManyResultsException.class)
    public void throwToManyResultsException4() throws TooManyResultsException {
        Exception error = new Exception();
        throw new TooManyResultsException("Test", error, true, true);
    }

    //MultipleDocumentsException
    @Test(expected = MultipleDocumentsException.class)
    public void throwMultipleDocumentsException() throws MultipleDocumentsException {
        throw new MultipleDocumentsException();
    }

    @Test(expected = MultipleDocumentsException.class)
    public void throwMultipleDocumentsException1() throws MultipleDocumentsException {
        throw new MultipleDocumentsException("Test");
    }

    @Test(expected = MultipleDocumentsException.class)
    public void throwMultipleDocumentsException2() throws MultipleDocumentsException {
        Exception error = new Exception();
        throw new MultipleDocumentsException("Test", error);
    }

    @Test(expected = MultipleDocumentsException.class)
    public void throwMultipleDocumentsException3() throws MultipleDocumentsException {
        Exception error = new Exception();
        throw new MultipleDocumentsException(error);
    }

    @Test(expected = MultipleDocumentsException.class)
    public void throwMultipleDocumentsException4() throws MultipleDocumentsException {
        Exception error = new Exception();
        throw new MultipleDocumentsException("Test", error, true, true);
    }

    //NoContentAvailableException
    @Test(expected = NoContentAvailableException.class)
    public void throwNoContentAvailableException() throws NoContentAvailableException {
        throw new NoContentAvailableException();
    }

    @Test(expected = NoContentAvailableException.class)
    public void throwNoContentAvailableException1() throws NoContentAvailableException {
        throw new NoContentAvailableException("Test");
    }

    @Test(expected = NoContentAvailableException.class)
    public void throwNoContentAvailableException2() throws NoContentAvailableException {
        Exception error = new Exception();
        throw new NoContentAvailableException("Test", error);
    }

    @Test(expected = NoContentAvailableException.class)
    public void throwNoContentAvailableException3() throws NoContentAvailableException {
        Exception error = new Exception();
        throw new NoContentAvailableException(error);
    }

    @Test(expected = NoContentAvailableException.class)
    public void throwNoContentAvailableException4() throws NoContentAvailableException {
        Exception error = new Exception();
        throw new NoContentAvailableException("Test", error, true, true);
    }
    
    
    //NoContentAvailableException
    @Test(expected = InfoArchiveResponseException.class)
    public void throwInfoArchiveResponseException() throws InfoArchiveResponseException {
        throw new InfoArchiveResponseException();
    }
    
    @Test(expected = InfoArchiveResponseException.class)
    public void throwInfoArchiveResponseException1() throws InfoArchiveResponseException {
        throw new InfoArchiveResponseException("Test");
    }
    
    @Test(expected = InfoArchiveResponseException.class)
    public void throwInfoArchiveResponseException2() throws InfoArchiveResponseException {
        Exception error = new Exception();
        throw new InfoArchiveResponseException("Test", error);
    }
    
    @Test(expected = InfoArchiveResponseException.class)
    public void throwInfoArchiveResponseException3() throws InfoArchiveResponseException {
        Exception error = new Exception();
        throw new InfoArchiveResponseException(error);
    }
    
    @Test(expected = InfoArchiveResponseException.class)
    public void throwInfoArchiveResponseException4() throws InfoArchiveResponseException {
        Exception error = new Exception();
        throw new InfoArchiveResponseException("Test", error, true, true);
    }
    
    
    //CryptoFailureException
    @Test(expected = CryptoFailureException.class)
    public void throwCryptoFailureException() throws CryptoFailureException {
        throw new CryptoFailureException();
    }
    
    @Test(expected = CryptoFailureException.class)
    public void throwCryptoFailureException1() throws CryptoFailureException {
        throw new CryptoFailureException("Test");
    }
    
    @Test(expected = CryptoFailureException.class)
    public void throwCryptoFailureException2() throws CryptoFailureException {
        Exception error = new Exception();
        throw new CryptoFailureException("Test", error);
    }
    
    @Test(expected = CryptoFailureException.class)
    public void throwCryptoFailureException3() throws CryptoFailureException {
        Exception error = new Exception();
        throw new CryptoFailureException(error);
    }
    
    @Test(expected = CryptoFailureException.class)
    public void throwCryptoFailureException4() throws CryptoFailureException {
        Exception error = new Exception();
        throw new CryptoFailureException("Test", error, true, true);
    }
}
