package com.library.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    
    /**
     * Generate a strong password hash using BCrypt
     */
    public static String hashPassword(String password) {
        // BCrypt automatically generates a salt and includes it in the hash
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * Verify a password against a stored hash
     */
    public static boolean verifyPassword(String password, String storedHash) {
        // BCrypt verification
        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            return BCrypt.checkpw(password, storedHash);
        }
        
        // For backward compatibility with PBKDF2 hashes
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid stored hash format");
            }
            
            // This is a PBKDF2 hash, we'll handle it with the legacy method
            return verifyPBKDF2Password(password, storedHash);
        } catch (Exception e) {
            // If there's any error in parsing or verification, return false
            return false;
        }
    }
    
    /**
     * Legacy method for verifying PBKDF2 passwords
     */
    private static boolean verifyPBKDF2Password(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = java.util.Base64.getDecoder().decode(parts[1]);
            byte[] hash = java.util.Base64.getDecoder().decode(parts[2]);
            
            // Add global salt if it was used
            String globalSalt = "";  // Default empty global salt
            byte[] globalSaltBytes = globalSalt.getBytes();
            byte[] combinedSalt = new byte[salt.length + globalSaltBytes.length];
            System.arraycopy(salt, 0, combinedSalt, 0, salt.length);
            System.arraycopy(globalSaltBytes, 0, combinedSalt, salt.length, globalSaltBytes.length);
            
            // Generate hash with same parameters
            byte[] testHash = pbkdf2(password.toCharArray(), combinedSalt, iterations, hash.length);
            
            return java.util.Arrays.equals(hash, testHash);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * PBKDF2 implementation for legacy verification
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
            throws java.security.NoSuchAlgorithmException, java.security.spec.InvalidKeySpecException {
        javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(password, salt, iterations, bytes * 8);
        javax.crypto.SecretKeyFactory skf = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        return skf.generateSecret(spec).getEncoded();
    }
}