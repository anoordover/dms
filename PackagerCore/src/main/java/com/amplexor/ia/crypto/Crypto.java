package com.amplexor.ia.crypto;

import com.amplexor.ia.exception.ExceptionHelper;
import org.apache.commons.io.Charsets;
import org.omg.CORBA.DynAnyPackage.Invalid;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by admjzimmermann on 26-10-2016.
 */
public class Crypto {
    private static final int IV_SIZEB = 16;
    private static final int KEY_SIZEB = 16;

    public static byte[] decrypt(byte[] cData, byte[] cKey) {
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
            System.err.println(ex);
        }

        return cReturn;
    }

    private static byte[] generateIV() {
        byte[] cIV = new byte[IV_SIZEB];
        String sPhrase = "c0nv0l8t3ed";
        for (int i = 0; i < cIV.length; ++i) {
            cIV[i] = (byte) ((byte) (Math.random() * Byte.MAX_VALUE) ^ sPhrase.getBytes()[(int)(Math.random() * sPhrase.length() - 1)]);
        }

        return cIV;
    }

    public static void main(String[] cArgs) {
        if (cArgs.length < 1) {
            System.err.println("ERROR: Unsupported usage, expected: java -cp com.amplexor.ia.crypto.Crypto Crypto [password]");
        } else {
            if(cArgs[0].equals("test")) {
                System.out.println(new String(decrypt(Base64.getDecoder().decode(cArgs[1].getBytes()), "SomeSomeKeyKey12".getBytes())));
                return;
            }

            try {
                String sPassword = cArgs[0];
                System.out.println("Encrypting " + sPassword);
                System.out.println(Base64.getEncoder().encodeToString(encrypt(sPassword.getBytes(), "SomeSomeKeyKey12".getBytes())));
            } catch (InvalidKeyException ex) {
                System.err.println(ex);
            }
        }
    }
}
