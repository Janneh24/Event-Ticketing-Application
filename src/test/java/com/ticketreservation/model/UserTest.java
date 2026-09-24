package com.ticketreservation.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testUserConstructorsAndGettersSetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("pass123");
        user.setRole("CUSTOMER");
        user.setEnabled(true);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("john");
        assertThat(user.getPassword()).isEqualTo("pass123");
        assertThat(user.getRole()).isEqualTo("CUSTOMER");
        assertThat(user.isEnabled()).isTrue();

        User user2 = new User(2L, "admin", "secret", "ORGANIZER", false);
        assertThat(user2.getId()).isEqualTo(2L);
        assertThat(user2.getUsername()).isEqualTo("admin");
        assertThat(user2.getPassword()).isEqualTo("secret");
        assertThat(user2.getRole()).isEqualTo("ORGANIZER");
        assertThat(user2.isEnabled()).isFalse();
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User(1L, "john", "pass", "CUSTOMER", true);
        User user2 = new User(1L, "john", "pass", "CUSTOMER", true);
        User user3 = new User(2L, "john", "pass", "CUSTOMER", true);

        // Self-equality test (covers this == o branch)
        assertThat(user1).isEqualTo(user1);

        // Equality with identical object
        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());

        // Inequality tests
        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1).isNotEqualTo(null);
        assertThat(user1).isNotEqualTo("some string");
        assertThat(user1).isNotEqualTo(new User(1L, "diff_user", "pass", "CUSTOMER", true));
        assertThat(user1).isNotEqualTo(new User(1L, "john", "diff_pass", "CUSTOMER", true));
        assertThat(user1).isNotEqualTo(new User(1L, "john", "pass", "ORGANIZER", true));
        assertThat(user1).isNotEqualTo(new User(1L, "john", "pass", "CUSTOMER", false));
        assertThat(user1).isNotEqualTo(new User(1L, null, "pass", "CUSTOMER", true));
    }

    @Test
    void testToString() {
        User user = new User(1L, "john", "pass", "CUSTOMER", true);
        assertThat(user.toString()).contains("id=1", "username='john'", "role='CUSTOMER'", "enabled=true");
    }
}
