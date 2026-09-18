package com.ticketreservation.repository.mysql;

import com.ticketreservation.model.User;
import com.ticketreservation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MysqlUserRepositoryIT extends AbstractMysqlRepositoryIT {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new MysqlUserRepository(dataSource);
    }

    @Test
    void testSaveAndFindById() {
        User user = new User(0L, "alice", "password123", "CUSTOMER", true);
        User saved = userRepository.save(user);

        assertThat(saved.getId()).isGreaterThan(0L);

        User found = userRepository.findById(saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("alice");
        assertThat(found.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    void testFindByUsername() {
        User user = new User(0L, "bob", "secret", "ORGANIZER", true);
        userRepository.save(user);

        User found = userRepository.findByUsername("bob");
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("bob");

        assertThat(userRepository.findByUsername("nonexistent")).isNull();
    }

    @Test
    void testFindAllAndUpdateAndDelete() {
        User user = new User(0L, "charlie", "pass", "CUSTOMER", true);
        User saved = userRepository.save(user);

        List<User> users = userRepository.findAll();
        assertThat(users).extracting(User::getUsername).contains("charlie");

        saved.setRole("ORGANIZER");
        userRepository.update(saved);

        User updated = userRepository.findById(saved.getId());
        assertThat(updated.getRole()).isEqualTo("ORGANIZER");

        userRepository.delete(saved.getId());
        assertThat(userRepository.findById(saved.getId())).isNull();
    }
}
