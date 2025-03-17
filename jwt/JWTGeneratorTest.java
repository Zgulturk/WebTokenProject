import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JWTGeneratorTest {
    @Test
    public void testGenerateToken() {
        String token = Jwts.builder()
                .setSubject("testSubject")
                .signWith(Keys.hmacShaKeyFor("testKey".getBytes(StandardCharsets.UTF_8)))
                .compact();

        Claims claims = Jwts.parserBuilder()
                .verifyWith(Keys.hmacShaKeyFor("testKey".getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("testSubject", claims.getSubject());
    }
}