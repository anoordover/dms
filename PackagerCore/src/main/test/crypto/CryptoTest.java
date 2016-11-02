package crypto;

import com.amplexor.ia.crypto.Crypto;
import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.*;

/**
 * Created by admjzimmermann on 26-10-2016.
 */
public class CryptoTest {

    @Test
    public void testDecrypt() throws Exception {
        byte[] cKey = "SomeSomeKeyKey12".getBytes();
        byte[] cData = "password".getBytes();
        byte[] cOutput = Crypto.encrypt(cData, cKey);
        System.out.println(Base64.getEncoder().encodeToString(cOutput));
        assertNotSame(cOutput.length, 0);
    }

    @Test
    public void testEncrypt() throws Exception {
        byte[] cData = Base64.getDecoder().decode("IGIbWVQrLEVvQUh/eWJKXTY+QNUsDVNFswrOyZ/OxW0=".getBytes());
        byte[] cKey = "SomeSomeKeyKey12".getBytes();
        byte[] cOutput = Crypto.decrypt(cData, cKey);
        System.out.println(new String(cOutput));
        assertEquals(new String(cOutput), "password");
    }
}