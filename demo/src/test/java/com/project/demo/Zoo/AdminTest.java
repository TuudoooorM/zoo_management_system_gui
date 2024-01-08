package com.project.demo.Zoo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {
    @Test
    void testAdminConstructor() {
        Admin admin = new Admin("test", Sex.male, "password");
        assertEquals("test", admin.getName());
        assertEquals(Sex.male, admin.sex);
        assertEquals("password", admin.getPassword());
    }
}