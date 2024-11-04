package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UserDetailsImplUnitTest {

    @Test
    public void testAdminField() {
        UserDetailsImpl adminUser = UserDetailsImpl.builder()
            .id(1L)
            .username("admin")
            .firstName("Admin")
            .lastName("User")
            .admin(true)
            .password("password")
            .build();

        UserDetailsImpl nonAdminUser = UserDetailsImpl.builder()
            .id(2L)
            .username("user")
            .firstName("Non-admin")
            .lastName("User")
            .admin(false)
            .password("password")
            .build();

        assertTrue(adminUser.getAdmin());
        assertFalse(nonAdminUser.getAdmin());
    }

    @Test
    public void testEquals() {
        UserDetailsImpl user1 = UserDetailsImpl.builder()
            .id(1L)
            .username("user1")
            .firstName("First")
            .lastName("User")
            .admin(true)
            .password("password")
            .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
            .id(1L)
            .username("user2")
            .firstName("Second")
            .lastName("User")
            .admin(false)
            .password("password")
            .build();

        UserDetailsImpl user3 = UserDetailsImpl.builder()
            .id(2L)
            .username("user3")
            .firstName("Third")
            .lastName("User")
            .admin(true)
            .password("password")
            .build();

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
    }

    @Test
    public void testEqualsWithSameInstance() {
        UserDetailsImpl user = UserDetailsImpl.builder()
            .id(1L)
            .username("user")
            .firstName("First")
            .lastName("User")
            .admin(true)
            .password("password")
            .build();

        assertEquals(user, user);
    }

    @Test
    public void testEqualsWithNullAndDifferentClass() {
        UserDetailsImpl user = UserDetailsImpl.builder()
            .id(1L)
            .username("user")
            .firstName("First")
            .lastName("User")
            .admin(true)
            .password("password")
            .build();

        assertNotEquals(user, null);

        assertNotEquals(user, new Object());
    }
}