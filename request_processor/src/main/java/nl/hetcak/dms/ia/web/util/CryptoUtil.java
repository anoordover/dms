package nl.hetcak.dms.ia.web.util;

import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.CryptoFailureException;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.managers.ConfigurationManager;
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
import java.util.Base64;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 * <p>
 * For reading files.
 * </p>
 * @author Joury.Zimmermann@AMPLEXOR.com
 * @author Jeroen.Pelt@AMPLEXOR.com (modified for this application)
 */
public class CryptoUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoUtil.class);
    private static final String CIPHER_INSTANCE = "AES/CBC/NoPadding";
    private static final int IV_SIZEB = 16;

    private CryptoUtil() {
        //No constructor.
    }

    /**
     * Decrypts the data.
     *
     * @param cData The data that must be decrypted.
     * @param cKey  The key used for decrypting the data.
     * @return decrypted data.
     * @throws CryptoFailureException Exception during decrypting the data.
     */
    public static byte[] decrypt(byte[] cData, byte[] cKey) throws CryptoFailureException {
        byte[] cReturn = new byte[0];
        if (cData.length < 17) {
            return cReturn;
        }

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
            SecretKeySpec objKeySpec = new SecretKeySpec(cKey, "AES");
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
            CryptoFailureException cfExc = new CryptoFailureException("Failed to decrypt Object.", ex);
            LOGGER.error("Crypto Util was unable to decrypt data.", cfExc);
            throw cfExc;
        }

        return cReturn;
    }

    /**
     * Decrypts a value.
     * @param stringData The {@link String} that needs to be decrypted.
     * @param configuration The current configuration.
     * @return A decrypted String.
     * @throws CryptoFailureException Error during decryption.
     */
    public static String decryptValue(String stringData, Configuration configuration) throws RequestResponseException {
        LOGGER.info("Decrypting encrypted values.");
        byte[] data = stringData.getBytes();
        byte[] decryptionKey = retrieveKey(configuration.getDecryptionKey());
        byte[] dataPartiallyDecrypted = Base64.getDecoder().decode(data);
        byte[] dataDecrypted = decrypt(dataPartiallyDecrypted,decryptionKey);
        LOGGER.info("Decryption complete.");
        return new String(dataDecrypted);
    }

    private static byte[] retrieveKey(byte[] cData) throws RequestResponseException {
        byte[] cBase64Key = Arrays.copyOfRange(cData, 0, 16);
        byte[] cBase64Data = Arrays.copyOfRange(cData, 16, cData.length);
        byte[] cDataEnc = Base64.getDecoder().decode(cBase64Data);
        byte[] cPlainData = decrypt(cDataEnc, cBase64Key);
        return Base64.getDecoder().decode(cPlainData);
    }
}
