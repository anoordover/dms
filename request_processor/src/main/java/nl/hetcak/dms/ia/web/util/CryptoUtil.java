package nl.hetcak.dms.ia.web.util;

import nl.hetcak.dms.ia.web.exceptions.CryptoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 * @author Joury.Zimmermann@AMPLEXOR.com
 */
public class CryptoUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoUtil.class);
    private static final String CIPHER_INSTANCE = "AES/CBC/NoPadding";
    private static final String KEY_SPEC = "AES";
    private static final int IV_SIZEB = 16;
    private static final int KEY_SIZEB = 16;
    
    /**
     * Decrypts the data.
     * @param cData The data that must be decrypted.
     * @param cKey The key used for decrypting the data.
     * @return decrypted data.
     * @throws CryptoFailureException Exception during decrypting the data.
     */
    public static byte[] decrypt(byte[] cData, byte[] cKey) throws CryptoFailureException {
        byte[] cReturn = new byte[0];
        if (cData.length > 16) {
            try {
                byte[] cIV = new byte[IV_SIZEB];
                byte[] cInput = new byte[cData.length - 16];
                for (int i = 0; i < cData.length; ++i) {
                    if (i < cIV.length) {
                        cIV[i] = cData[i];
                    } else {
                        cInput[i - cIV.length] = cData[i];
                    }
                }
                Cipher objCipher = Cipher.getInstance(CIPHER_INSTANCE);
                SecretKeySpec objKeySpec = new SecretKeySpec(cKey, KEY_SPEC);
                IvParameterSpec objIvSpec = new IvParameterSpec(cIV);
                objCipher.init(Cipher.DECRYPT_MODE, objKeySpec, objIvSpec);
                byte[] cOutput = objCipher.doFinal(cInput);
                int iLength = cOutput.length - cOutput[cOutput.length - 1];
                cReturn = Arrays.copyOf(cOutput, iLength);
            } catch (BadPaddingException
                | NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException
                | InvalidKeyException
                | IllegalBlockSizeException ex) {
                CryptoFailureException cfExc = new CryptoFailureException("Failed to decrypt Object.",ex);
                LOGGER.error("Crypto Util was unable to decrypt data.",cfExc);
                throw cfExc;
            }
        }
        return cReturn;
    }
    
    /**
     * Encrypts the data.
     * @param cData The data that should be encrypted.
     * @param cKey The key that must be used for encrypting this data.
     * @return The encrypted data.
     * @throws InvalidKeyException The key doesn't met the requirements.
     * @throws CryptoFailureException A exception during encryption of the data.
     */
    public static byte[] encrypt(byte[] cData, byte[] cKey) throws InvalidKeyException, CryptoFailureException {
        byte[] cReturn = new byte[0];
        if (cKey.length != KEY_SIZEB) {
            throw new InvalidKeyException("The key does not meet the size requirement");
        }
        
        try {
            byte bPadding = (byte) Math.abs((cData.length % 16) - 16);
            byte[] cInput = Arrays.copyOf(cData, cData.length + bPadding);
            byte[] cIV = generateIV();
            for (int i = cData.length; i < cInput.length; ++i) {
                cInput[i] = bPadding;
            }
            Cipher objCipher = Cipher.getInstance(CIPHER_INSTANCE);
            SecretKeySpec objKeySpec = new SecretKeySpec(cKey, KEY_SPEC);
            IvParameterSpec objIvSpec = new IvParameterSpec(cIV);
            objCipher.init(Cipher.ENCRYPT_MODE, objKeySpec, objIvSpec);
            byte[] cOutput = objCipher.doFinal(cInput);
            cReturn = Arrays.copyOf(cIV, cOutput.length + IV_SIZEB);
            for (int i = IV_SIZEB; i < cReturn.length; ++i) {
                cReturn[i] = cOutput[i - IV_SIZEB];
            }
        } catch (BadPaddingException
            | NoSuchAlgorithmException
            | NoSuchPaddingException
            | InvalidAlgorithmParameterException
            | InvalidKeyException
            | IllegalBlockSizeException ex) {
            CryptoFailureException cfExc = new CryptoFailureException("Failed to encrypt Object.",ex);
            LOGGER.error("Crypto Util was unable to encrypt data.",cfExc);
            throw cfExc;
        }
        
        return cReturn;
    }
    
    /**
     * Creates a random key.
     * @return a secure random key.
     */
    public static byte[] createRandomKey(){
        SecureRandom secureRandom = new SecureRandom();
        final byte[] key = new byte[KEY_SIZEB];
        secureRandom.nextBytes(key);
        return key;
    }
    
    private static byte[] generateIV() {
        byte[] cIV = new byte[IV_SIZEB];
        SecureRandom secureRandom = new SecureRandom();
        String sPhrase = "c0nv0l8t3ed";
        for (int i = 0; i < cIV.length; ++i) {
            cIV[i] = (byte) ((byte) (secureRandom.nextDouble() * Byte.MAX_VALUE) ^ sPhrase.getBytes()[(int)(secureRandom.nextDouble() * sPhrase.length() - 1)]);
        }
        
        return cIV;
    }
}
