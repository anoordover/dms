package com.amplexor.ia.crypto;

import com.amplexor.ia.exception.ExceptionHelper;
import org.apache.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admjzimmermann on 26-10-2016.
 */
public class Crypto {
    private static final String ARG_KEYFILE = "-keyfile";
    private static final String ARG_KEY = "-key";
    private static final String ARG_DATA = "-data";

    private static final int IV_SIZEB = 16;
    private static final int KEY_SIZEB = 16;

    private Crypto() { //Hide implicit public ctor

    }

    public static byte[] decrypt(byte[] cData, byte[] cKey) {
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
            Cipher objCipher = Cipher.getInstance("AES/CBC/NoPadding");
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
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }

        return cReturn;
    }

    public static byte[] encrypt(byte[] cData, byte[] cKey) throws InvalidKeyException {
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
            Cipher objCipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec objKeySpec = new SecretKeySpec(cKey, "AES");
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
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }

        return cReturn;
    }

    private static byte[] generateIV() {
        byte[] cIV = new byte[IV_SIZEB];
        String sPhrase = "c0nv0l8t3ed";
        for (int i = 0; i < cIV.length; ++i) {
            SecureRandom objRandom = new SecureRandom();
            cIV[i] = (byte) ((byte) (Math.random() * Byte.MAX_VALUE) ^ sPhrase.getBytes()[objRandom.nextInt(sPhrase.length() - 1)]);
        }

        return cIV;
    }

    private static byte[] generateKey(byte[] cKey) throws InvalidKeyException {
        byte[] cReturn = new byte[0];
        byte[] cKeyEnc = new byte[16];
        if (cKey.length < 16) {
            for (int i = 0; i < cKeyEnc.length; ++i) {
                cKeyEnc[i] = cKey[Math.abs(i % cKey.length)];
            }
        } else if (cKey.length > 16) {
            for (int i = 0; i < cKeyEnc.length; ++i) {
                cKeyEnc[i] = cKey[i];
            }
        }

        byte[] cBase64EncData = Base64.getEncoder().encode(cKeyEnc);
        if (cBase64EncData.length > 4) {
            byte[] cBase64EncKey = new byte[16];
            for (int i = 0; i < 16; ++i) {
                cBase64EncKey[i] = cBase64EncData[Math.abs(i % 3)];
            }
            byte[] cBase64Encrypted = encrypt(cBase64EncData, cBase64EncKey);
            byte[] cFinalKey = Base64.getEncoder().encode(cBase64Encrypted);
            cReturn = Arrays.copyOf(cBase64EncKey, cBase64EncKey.length + cFinalKey.length);
            for (int i = cBase64EncKey.length; i < cReturn.length; ++i) {
                cReturn[i] = cFinalKey[i - cBase64EncKey.length];
            }
        }
        return cReturn;
    }

    private static void generateKeyFile(byte[] cKey, Path objPath) throws IOException, InvalidKeyException {
        try (OutputStream objOutput = Files.newOutputStream(objPath)) {
            objOutput.write(generateKey(cKey));
        }
    }

    public static byte[] retrieveKey(Path objPath) throws IOException, InvalidKeyException {
        int iSize = (int) Files.size(objPath);
        byte[] cData = new byte[iSize];
        try (InputStream objInput = Files.newInputStream(objPath)) {
            int iRead;
            int iCursor = 0;
            while ((iRead = objInput.read()) != -1) {
                cData[iCursor++] = (byte) iRead;
            }
        }

        return retrieveKey(cData);
    }

    private static byte[] retrieveKey(byte[] cData) {
        byte[] cBase64Key = Arrays.copyOfRange(cData, 0, 16);
        byte[] cBase64Data = Arrays.copyOfRange(cData, 16, cData.length);
        byte[] cDataEnc = Base64.getDecoder().decode(cBase64Data);
        byte[] cPlainData = decrypt(cDataEnc, cBase64Key);
        return Base64.getDecoder().decode(cPlainData);
    }

    private static Map<String, String> getArguments(String[] cArgs) {
        Map<String, String> cReturn = new HashMap<>();
        for (int i = 0; i < cArgs.length; i += 2) {
            if (i % 2 == 0 && cArgs.length >= i + 1) {
                cReturn.put(cArgs[i], cArgs[i + 1]);
            }
        }
        return cReturn;
    }

    public static void main(String[] cArgs) throws Exception {
        Map<String, String> cArgsMap = getArguments(cArgs);

        if (cArgsMap.get(ARG_KEYFILE) == null || cArgsMap.get(ARG_DATA) == null) {
            Logger.getLogger(Crypto.class).info(
                    "Usage com.amplexor.ia.crypto.Crypto\n" +
                            "(REQUIRED)\t" + ARG_DATA + " [data to encode]\n" +
                            "(OPTIONAL)\t" + ARG_KEY + " [key to use]\n" +
                            "(REQUIRED)\t" + ARG_KEYFILE + " [path to keyfile]\n" +
                            "(NOTE: Using " + ARG_KEYFILE + " without " + ARG_KEY + " will cause the key to be read from " + ARG_KEYFILE);
            return;
        }
        byte[] sKey = null;
        String sKeyFile = cArgsMap.get(ARG_KEYFILE);
        byte[] sData = cArgsMap.get(ARG_DATA).getBytes(Charset.forName("UTF-8"));
        String sOutput;

        if (cArgsMap.get(ARG_KEY) != null) {
            sKey = cArgsMap.get(ARG_KEY).getBytes(Charset.forName("UTF-8"));
        }

        if (!Files.exists(Paths.get(sKeyFile)) && sKey == null) {
            throw new IllegalArgumentException("Neither -key or -keyfile were supplied, or the keyfile does not exist at the provided path");
        }

        if (sKey != null) {
            generateKeyFile(sKey, Paths.get(sKeyFile));
        }

        sKey = retrieveKey(Paths.get(sKeyFile));
        sOutput = Base64.getEncoder().encodeToString(encrypt(sData, sKey));
        Logger.getLogger(Crypto.class).info("Result: [ " + sOutput + " ]");
    }
}
