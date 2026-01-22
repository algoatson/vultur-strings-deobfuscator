import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class JadxAutoDecrypt {
    private static final Pattern OBFUSCATED_STRING_PATTERN = Pattern.compile(
        "(?:\\b[\\w$]+\\.)*a\\s*\\(\\s*new\\s+byte\\s*\\[\\s*]\\s*\\{([\\s\\S]*?)\\}\\s*\\)"
    );
    
    public static void main(String[] args) throws Exception {
        Path src = Path.of(args.length > 0 ? args[0] : "jadx-output");
        Files.walk(src)
            .filter(p -> p.toString().endsWith(".java"))
            .forEach(JadxAutoDecrypt::processFile);
    }
    
    static void processFile(Path p) {
        try {
            String code = Files.readString(p);
            Matcher m = OBFUSCATED_STRING_PATTERN.matcher(code);
            StringBuffer sb = new StringBuffer();

            while (m.find()) {
                byte[] blob = parseBytes(m.group(1));
                System.out.println("Foud Obfuscated String in file " + p + ": \n" + Arrays.toString(blob));
                String decrypted = decrypt(blob)
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"");
                m.appendReplacement(
                    sb, 
                    Matcher.quoteReplacement("\"" + decrypted + "\""));
            }
            
            m.appendTail(sb);
            Files.writeString(p, sb.toString());
        } catch (Exception e) {
            System.err.println("Failed processing file: " + p + " -> " + e.getMessage());
        }
    }
    
    static byte[] parseBytes(String s) {
        String[] parts = s.split(",");
        byte[] b = new byte[parts.length];
        for (int i = 0; i < parts.length; i++) {
            b[i] = (byte) Integer.parseInt(parts[i].trim());
        }
        return b;
    }
    
    static String decrypt(byte[] blob) throws Exception {
        // Last 16 bytes are the AES key
        byte[] c = Arrays.copyOfRange(blob, 0, blob.length - 16);
        byte[] k = Arrays.copyOfRange(blob, blob.length - 16, blob.length);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(k, "AES"));
        return new String(cipher.doFinal(c), StandardCharsets.UTF_8);
    }
}

