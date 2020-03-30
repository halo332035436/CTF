package com.bullb.ctf.Utils;

import android.content.Context;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.security.keystore.KeyProperties.KEY_ALGORITHM_EC;

/**
 * Created by oscarlaw on 28/7/16.
 */
public class KeyTools {
    private Context context;
    private final String serverPublicKey = "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE2BBDYUNOHnwzHe1WOow0PmNDHIlwSaW7wKsgUM7l2S1wg9izcyIfwDX7Zv8KtiQsMScQSqCVXnJ2R4A0pI2JT9ULoa5+pwOKfV960egZG68A4TPBgqE1CiR3vIW8o/sp";
    private static KeyTools keyTools;

    public static synchronized KeyTools getInstance(Context context) {
        if (keyTools == null) {
            keyTools = new KeyTools(context);
        }
        return keyTools;
    }


    private KeyTools(Context context) {
        this.context = context;
        if (SharedPreference.getPublicKey(context) == null || getSecretKey() == null) {
            File file = new File(context.getApplicationContext().getFilesDir(), "secretkey.keystore");
            //Reset keystore file
            if (file.exists()) {
                file.delete();
            }
            generateNewKeys();
        }

    }

    //Generate a new pair of keys (EC)
    public void generateNewKeys() {
        ECGenParameterSpec ecParamSpec = new ECGenParameterSpec("secp384r1");
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance(KEY_ALGORITHM_EC);
            kpg.initialize(ecParamSpec);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        KeyPair kpA = kpg.generateKeyPair();
        String b = Base64.encodeToString(kpA.getPublic().getEncoded(), Base64.DEFAULT);

        Log.d("public_key", b);

        SharedPreference.setPublicKey(context, b);

        //start key exchange process
        keyExchange(kpA);
    }


    public String getPublicKey() {
        return SharedPreference.getPublicKey(context);
    }

    public SecretKey getSecretKey() {
        SecretKey secretKey = null;

        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());


            // get user password and file input stream
            File file = new File(context.getApplicationContext().getFilesDir(), "secretkey.keystore");
            InputStream inputStream;

            if (!file.exists()) {
                file.createNewFile();
                file.mkdir();
                inputStream = null;
            } else {
                inputStream = new FileInputStream(file);
            }
            try {
                ks.load(inputStream, SharedPreference.getRandomPw(context).toCharArray());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(SharedPreference.getRandomPw(context).toCharArray());
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) ks.getEntry("secret_key", keyStorePP);
            if (secretKeyEntry == null)
                return null;
            secretKey = secretKeyEntry.getSecretKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        }

        return secretKey;
    }


    public String getServerPublicKey() {
        return serverPublicKey;
    }


    /**
     * Procress of creating the secret key from server public key and client private key
     */
    public void keyExchange(KeyPair kp) {
        KeyAgreement aKA = null;

        byte[] sharedKeyA;
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

            // get user password and file input stream

            File file = new File(context.getApplicationContext().getFilesDir(), "secretkey.keystore");
            InputStream inputStream;

            if (!file.exists()) {
                file.createNewFile();
                file.mkdir();
                inputStream = null;
            } else {
                inputStream = new FileInputStream(file);
            }
            //assets目录下放了一个pkcs12的证书文件
            try {
                ks.load(inputStream, SharedPreference.getRandomPw(context).toCharArray());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            PrivateKey privKeyA = kp.getPrivate();

            KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM_EC);
            X509EncodedKeySpec x509ks = new X509EncodedKeySpec(Base64.decode(serverPublicKey, Base64.DEFAULT));
            PublicKey pubKeyB = kf.generatePublic(x509ks);

            aKA = KeyAgreement.getInstance("ECDH");
            aKA.init(privKeyA);
            aKA.doPhase(pubKeyB, true);
            sharedKeyA = aKA.generateSecret();
            byte[] secretKey = getHash(sharedKeyA);

            //set password for keystore
            KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(SharedPreference.getRandomPw(context).toCharArray());
            ks.setEntry("secret_key", new KeyStore.SecretKeyEntry(new SecretKeySpec(secretKey, KeyProperties.KEY_ALGORITHM_AES)), keyStorePP);
            Log.d("secret", new String(secretKey));

            // store away the keystore
            OutputStream outputStream = new FileOutputStream(file);
            try {
                ks.store(outputStream, SharedPreference.getRandomPw(context).toCharArray());
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Decrypt server response (logout app if decryption failed)
     *
     * @param ivString      the iv from server response
     * @param encryptedData the data from server response
     * @return decrypted data
     */
    public String decryptData(String ivString, String encryptedData) {
        return new String(decryption(ivString, encryptedData));
    }

    public byte[] decryptImage(String ivString, String encryptedData) {
        return decryption(ivString, encryptedData);
    }

    //Decryption process
    private byte[] decryption(String ivString, String encryptedData) {
        byte[] decValue = null;
        try {
            byte[] iv = Base64.decode(ivString.getBytes(), Base64.DEFAULT);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

            // get user password and file input stream
            File file = new File(context.getApplicationContext().getFilesDir(), "secretkey.keystore");
            InputStream inputStream;

            if (!file.exists()) {
                file.createNewFile();
                file.mkdir();
                inputStream = null;
            } else {
                inputStream = new FileInputStream(file);
            }
            try {
                ks.load(inputStream, SharedPreference.getRandomPw(context).toCharArray());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(SharedPreference.getRandomPw(context).toCharArray());
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) ks.getEntry("secret_key", keyStorePP);
            SecretKey secretKey = secretKeyEntry.getSecretKey();

            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] decordedValue = Base64.decode(encryptedData.getBytes(), Base64.DEFAULT);
            decValue = c.doFinal(decordedValue);
            Log.d("decrypted_data", new String(decValue));

            return decValue;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Decrypt Failed", Toast.LENGTH_SHORT).show();
            SharedPreference.logout(context);
        }
        return decValue;
    }


    /**
     * @param data The data need to be encrypted
     * @return A Hashmap which contain {data, iv, hash}
     */
    public Map<String, String> encrypt(String data) {
        return encryption(data.getBytes());
    }

    public Map<String, String> encrypt(byte[] image) {
        return encryption(image);
    }

    private Map<String, String> encryption(byte[] data) {
        Map<String, String> encryptedMap = new HashMap<>();
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

            // get user password and file input stream
            File file = new File(context.getApplicationContext().getFilesDir(), "secretkey.keystore");
            InputStream inputStream;

            if (!file.exists()) {
                file.createNewFile();
                file.mkdir();
                inputStream = null;
            } else {
                inputStream = new FileInputStream(file);
            }
            try {
                ks.load(inputStream, SharedPreference.getRandomPw(context).toCharArray());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(SharedPreference.getRandomPw(context).toCharArray());
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) ks.getEntry("secret_key", keyStorePP);
            SecretKey secretKey = secretKeyEntry.getSecretKey();
            Log.d("secret", new String(secretKey.getEncoded()));

            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = c.doFinal(data);
            String dataString = Base64.encodeToString(encryptedData, Base64.NO_WRAP);
            encryptedMap.put("data", dataString);
            AlgorithmParameters params = c.getParameters();
            encryptedMap.put("iv", Base64.encodeToString(params.getParameterSpec(IvParameterSpec.class).getIV(), Base64.NO_WRAP));
            Log.d("hash", sha256(data));
            encryptedMap.put("hash", sha256(data));

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedMap;

    }


    public static String sha256(byte[] base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base);
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private byte[] getHash(byte[] secretKey) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.reset();
        return digest.digest(secretKey);
    }

    private static String attendanceKey = "IuFWKUut";

    public static String attendanceDesEncryptId(String plainTextPassword) {
//        plainTextPassword = "j01";
        String encrypted = "";
        String encodedURL = "";
        try {
            DESKeySpec keySpec = new DESKeySpec(attendanceKey.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);
            byte[] cleartext = plainTextPassword.getBytes();

            Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = Base64.encodeToString(cipher.doFinal(cleartext), Base64.DEFAULT);
            encrypted = encrypted.replace("\n", "").replace("\r", "");
            encodedURL = URLEncoder.encode(encrypted, "UTF-8");

            Log.d("des", "attendanceDesEncryptId '" + plainTextPassword + "' -- des: " + encrypted + " ;utf: " + encodedURL);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedURL;
    }


    private static String performanceKey = "rnfEKcintpeyoyct";//16


    public static String performanceDesEncryptId(String key, String id, String ivString) {
        try {
            IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

            byte[] encrypted = cipher.doFinal(id.getBytes());
            String encryptedText = Base64.encodeToString(encrypted, Base64.DEFAULT);
            encryptedText = encryptedText.replace("\n", "").replace("\r", "");

            Log.d("CBC", "encryted: " + encryptedText);
            return encryptedText;
//            return URLEncoder.encode(encryptedText, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;

    }

    public static String performanceDesEncryptId(String id, String ivString) {
        try {
            IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());
            SecretKeySpec secretKeySpec = new SecretKeySpec(performanceKey.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

            byte[] encrypted = cipher.doFinal(id.getBytes());
            String encryptedText = Base64.encodeToString(encrypted, Base64.DEFAULT);
            encryptedText = encryptedText.replace("\n", "").replace("\r", "");

            Log.d("CBC", "encryted: " + encryptedText);
            return encryptedText;
//            return URLEncoder.encode(encryptedText, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;

    }

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch(UnsupportedEncodingException ex){
        }
        return null;
    }

    public static String toUrlEncode(String param){
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


}
