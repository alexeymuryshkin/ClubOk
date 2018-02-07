package dc.clubok.data;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//https://stackoverflow.com/questions/2860943/how-can-i-hash-a-password-in-java

public final class Crypt {

    /**
     * Each token produced by this class uses this identifier as a prefix.
     */
    private static final String ID = "$31$";

    /**
     * The minimum recommended cost, used by default
     */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    private static final int SIZE = 128;

    private static final Pattern layout = Pattern.compile("\\$31\\$(\\d\\d?)\\$(.{43})");

    private static final int cost = 16;

    public static byte[] salt;

    private static int iterations(int cost) {
        return 1 << cost;
    }

    private static void generateSalt() {
        salt = new byte[SIZE / 8];
        new SecureRandom().nextBytes(salt);
    }

    public static String hash(char[] password) {
        generateSalt();
        byte[] dk = pbkdf2(password, salt, iterations(cost));

        byte[] hash = new byte[salt.length + dk.length];
        System.arraycopy(salt, 0, hash, 0, salt.length);
        System.arraycopy(dk, 0, hash, salt.length, dk.length);
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();

        return ID + cost + '$' + enc.encodeToString(hash);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
        KeySpec spec = new PBEKeySpec(password, salt, iterations, SIZE);
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
            return f.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
        } catch (InvalidKeySpecException ex) {
            throw new IllegalStateException("Invalid SecretKeyFactory", ex);
        }
    }

    public static boolean compare(char[] password, String hashedPassword) {
        Matcher m = layout.matcher(hashedPassword);

        if (!m.matches())
            throw new IllegalArgumentException("Invalid token format");

        int iterations = iterations(Integer.parseInt(m.group(1)));
        byte[] hash = Base64.getUrlDecoder().decode(m.group(2));
        byte[] salt = Arrays.copyOfRange(hash, 0, SIZE / 8);
        byte[] check = pbkdf2(password, salt, iterations);
        int zero = 0;

        for (int idx = 0; idx < check.length; ++idx)
            zero |= hash[salt.length + idx] ^ check[idx];
        return zero == 0;
    }
}
