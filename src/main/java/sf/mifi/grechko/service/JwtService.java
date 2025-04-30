package sf.mifi.grechko.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final byte[] secretKeyBytes;

    public JwtService(byte[] key) {
        secretKeyBytes = key;
    }

    public String generateToken(String username) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiredAt = new Date(issuedAt.getTime() + 3600000); // Срок действия токена: 1 час

        // Создаем набор претензий (claims)
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("your_app_name")
                .subject(username)
                .issueTime(issuedAt)
                .expirationTime(expiredAt)
                .build();

        // Создаем заголовок JWT
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();

        // Создаем объект SignedJWT
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);

        // Подписываем токен
        try {
            MACSigner macSigner = new MACSigner(secretKeyBytes);
            signedJWT.sign(macSigner);
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to sign JWT", e);
        }

        // Возвращаем сериализованный токен
        return signedJWT.serialize();
    }

    /*public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }*/

}
