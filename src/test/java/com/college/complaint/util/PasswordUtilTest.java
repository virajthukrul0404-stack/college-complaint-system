package com.college.complaint.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testHashAndVerify() {
        String plain = "Admin@12345";
        String hash = PasswordUtil.hashPassword(plain);
        assertNotNull(hash);
        assertTrue(PasswordUtil.checkPassword(plain, hash));
        assertFalse(PasswordUtil.checkPassword("WrongPass", hash));
        System.out.println("HASH_FOR_SEED: " + hash);
    }
}
