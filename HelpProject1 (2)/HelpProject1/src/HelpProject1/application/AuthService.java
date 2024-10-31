package HelpProject1.application;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class AuthService {
    private HelpSystem helpSystem;

    public AuthService(HelpSystem helpSystem) {
        this.helpSystem = helpSystem;
    }

    public boolean authenticate(String username, String password) {
        User user = helpSystem.getUser(username);
        byte[] hashedPassword = hashPassword(password);

        if (user != null) {
            if (user.isOneTimePassword() && user.getOtpExpiry().isAfter(LocalDateTime.now())) {
                user.setOneTimePassword(false);
                return true;
            }
            return Arrays.equals(user.getPasswordHash(), hashedPassword);
        }
        return false;
    }

    public byte[] hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Added static method for password hashing
    public static byte[] hashStaticPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
