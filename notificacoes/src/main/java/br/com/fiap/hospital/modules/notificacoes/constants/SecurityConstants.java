package br.com.fiap.hospital.modules.notificacoes.constants;

import java.nio.charset.StandardCharsets;

public class SecurityConstants {
    public static final String SECRET = "hospital-scheduler-secret-key-2026-very-long-value";
    public static final String HMAC_ALGORITHM = "HmacSHA256";
    public static final byte[] SECRET_BYTES = SECRET.getBytes(StandardCharsets.UTF_8);
}
