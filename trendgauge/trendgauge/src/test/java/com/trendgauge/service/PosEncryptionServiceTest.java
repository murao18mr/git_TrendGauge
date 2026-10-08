package com.trendgauge.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PosEncryptionServiceTest {
    @Test
    @DisplayName("暗号化した文字列を復号すると元の値に戻ること")
    void test_encryptAndDecrypt() throws Exception {
        byte[] keyBytes = new byte[32];
        String key = Base64.getEncoder().encodeToString(keyBytes);

        PosEncryptionService service = new PosEncryptionService(key);

        String original = "test-client-secret";
        String encrypted = service.encrypt(original);
        String decrypted = service.decrypt(encrypted);

        assertEquals(original, decrypted);
    }

    @Test
    @DisplayName("同じ文字列を2回暗号化しても異なる結果になること")
    void test_encryptDifferentResults() throws Exception {
        byte[] keyBytes = new byte[32];
        String key = Base64.getEncoder().encodeToString(keyBytes);

        PosEncryptionService service = new PosEncryptionService(key);

        String encrypted1 = service.encrypt("test-client-secret");
        String encrypted2 = service.encrypt("test-client-secret");

        assertNotEquals(encrypted1, encrypted2);
    }
}
