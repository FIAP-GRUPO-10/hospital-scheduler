package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.security;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity.Usuario;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = new SecretKeySpec(Base64.getDecoder().decode(secret),"HmacSHA256");
    }

    public String generateToken(Usuario usuario) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("role", usuario.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }
}