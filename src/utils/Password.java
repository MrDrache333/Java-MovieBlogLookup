package utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

import static utils.Debugger.writeerror;

class Password {

    private static SecretKeySpec secretKeySpec = null;

    static void initialize(){
    	try {
		    // Das Passwort bzw der Schluesseltext
		    String keyStr = "Everybody Hoerz";
		    // byte-Array erzeugen
		    byte[] key = (keyStr).getBytes("UTF-8");
		    // aus dem Array einen Hash-Wert erzeugen mit MD5 oder SHA
		    MessageDigest sha = MessageDigest.getInstance("SHA-256");
		    key = sha.digest(key);
		    // nur die ersten 128 bit nutzen
		    key = Arrays.copyOf(key, 16);
		    // der fertige Schluessel
		    secretKeySpec = new SecretKeySpec(key, "AES");
	    }catch(Exception e){
    		writeerror(e);
	    }
    }

    static String lock(String message) {
    	if (secretKeySpec == null)initialize();
        try {
            // Verschluesseln
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(message.getBytes());

            // bytes zu Base64-String konvertieren (dient der Lesbarkeit)
            BASE64Encoder myEncoder = new BASE64Encoder();
            return myEncoder.encode(encrypted);
        } catch (Exception e) {
            writeerror(e);
        }
        return null;
    }

    static String entlock(String input) {
	    if (secretKeySpec == null)initialize();
        try {

            // BASE64 String zu Byte-Array konvertieren
            BASE64Decoder myDecoder2 = new BASE64Decoder();
            byte[] crypted2 = myDecoder2.decodeBuffer(input);

            // Entschluesseln
            Cipher cipher2 = Cipher.getInstance("AES");
            cipher2.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] cipherData2 = cipher2.doFinal(crypted2);
            return new String(cipherData2);
        } catch (Exception ignored) {
        }
        return null;
    }
}