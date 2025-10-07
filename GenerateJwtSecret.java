import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class GenerateJwtSecret {
    public static void main(String[] args) {
        try {
            // Generate a 256-bit (32-byte) secret key
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            
            // Convert to Base64 string
            String base64Secret = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            
            System.out.println("Generated JWT Secret Key:");
            System.out.println(base64Secret);
            System.out.println("\nAdd this to your environment variables:");
            System.out.println("JWT_SECRET=" + base64Secret);
            
        } catch (Exception e) {
            System.err.println("Error generating secret: " + e.getMessage());
        }
    }
}
