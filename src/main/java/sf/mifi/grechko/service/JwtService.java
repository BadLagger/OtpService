package sf.mifi.grechko.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final byte[] secretKeyBytes;

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

    public void parseToken(String token) throws ParseException, JOSEException {
        // Проверяем токен на валидность
        SignedJWT signedJWT = SignedJWT.parse(token);

        // Проверяем подпись токена
        JWSVerifier verifier = new MACVerifier(secretKeyBytes);
        if (!signedJWT.verify(verifier)) {
            throw new JOSEException("Invalid signature");
        }

        // Проверяем претензии (claims)
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        // Проверяем срок действия токена
        if (claimsSet.getExpirationTime().before(new Date())) {
            throw new JOSEException("Expired token");
        }

        // Проверяем субъекта (username)
        String subject = claimsSet.getSubject();
        if (subject == null || subject.isEmpty()) {
            throw new JOSEException("Missing subject claim");
        }

        // Токен успешно проверен
    }

}
