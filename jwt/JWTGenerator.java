import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import spark.Spark;
import com.google.gson.Gson;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTGenerator {
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    public static void main(String[] args) {
        Spark.port(8080);
        
        // Token oluşturma endpointi
        Spark.post("/generate-token", (req, res) -> {
            Gson gson = new Gson();
            Map<String, String> requestData = gson.fromJson(req.body(), Map.class);
            String username = requestData.get("username");

            if (username == null || username.isEmpty()) {
                res.status(400);
                return gson.toJson(Map.of("error", "Kullanıcı adı eksik"));
            }

            String token = createJWT(username);
            res.status(200);
            return gson.toJson(Map.of("token", token));
        });
    }

    private static String createJWT(String username) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillis = nowMillis + 3600000; // 1 saat geçerli
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey)
                .compact();
    }
}
