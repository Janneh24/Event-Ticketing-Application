package com.ticketreservation.controller;

import com.ticketreservation.model.User;
import com.ticketreservation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userRepository);
    }

    @Test
    void testGetAllUsers() {
        User u1 = new User(1L, "alice", "pass", "CUSTOMER", true);
        when(userRepository.findAll()).thenReturn(List.of(u1));

        List<User> users = userController.getAllUsers();
        assertThat(users).containsExactly(u1);
    }

    @Test
    void testGetUserByIdSuccessAndNotFound() {
        User u1 = new User(1L, "alice", "pass", "CUSTOMER", true);
        when(userRepository.findById(1L)).thenReturn(u1);

        assertThat(userController.getUserById(1L)).isEqualTo(u1);

        when(userRepository.findById(2L)).thenReturn(null);
        assertThatThrownBy(() -> userController.getUserById(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found with id: 2");
    }

    @Test
    void testGetUserByUsernameSuccessAndValidation() {
        User u1 = new User(1L, "alice", "pass", "CUSTOMER", true);
        when(userRepository.findByUsername("alice")).thenReturn(u1);

        assertThat(userController.getUserByUsername("alice")).isEqualTo(u1);

        assertThatThrownBy(() -> userController.getUserByUsername(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username cannot be empty");

        assertThatThrownBy(() -> userController.getUserByUsername(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username cannot be empty");

        when(userRepository.findByUsername("unknown")).thenReturn(null);
        assertThatThrownBy(() -> userController.getUserByUsername("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found with username: unknown");
    }

    @Test
    void testCreateUserSuccessCustomerRole() {
        User user = new User(0L, "alice", "pass", "CUSTOMER", true);
        when(userRepository.save(user)).thenReturn(new User(1L, "alice", "pass", "CUSTOMER", true));

        User created = userController.createUser(user);
        assertThat(created.getId()).isEqualTo(1L);
    }

    @Test
    void testCreateUserSuccessOrganizerRole() {
        User user = new User(0L, "admin", "pass", "ORGANIZER", true);
        when(userRepository.save(user)).thenReturn(new User(2L, "admin", "pass", "ORGANIZER", true));

        User created = userController.createUser(user);
        assertThat(created.getId()).isEqualTo(2L);
    }

    @Test
    void testCreateUserValidationFailures() {
        assertThatThrownBy(() -> userController.createUser(null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userController.createUser(new User(0L, null, "pass", "CUSTOMER", true)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userController.createUser(new User(0L, "", "pass", "CUSTOMER", true)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userController.createUser(new User(0L, "alice", null, "CUSTOMER", true)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userController.createUser(new User(0L, "alice", "", "CUSTOMER", true)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userController.createUser(new User(0L, "alice", "pass", null, true)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userController.createUser(new User(0L, "alice", "pass", "", true)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userController.createUser(new User(0L, "alice", "pass", "INVALID_ROLE", true)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role must be ORGANIZER or CUSTOMER");
    }

    @Test
    void testUpdateUserSuccessAndValidation() {
        User user = new User(1L, "alice", "pass", "CUSTOMER", true);
        when(userRepository.update(user)).thenReturn(user);

        assertThat(userController.updateUser(user)).isEqualTo(user);

        User invalidId = new User(0L, "alice", "pass", "CUSTOMER", true);
        assertThatThrownBy(() -> userController.updateUser(invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID must be positive for update");
    }

    @Test
    void testDeleteUserSuccessAndValidation() {
        userController.deleteUser(1L);
        verify(userRepository).delete(1L);

        assertThatThrownBy(() -> userController.deleteUser(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID must be positive for deletion");
    }

    @Test
    void testLoginSuccessAndFailures() {
        User user = new User(1L, "alice", "pass", "CUSTOMER", true);
        User disabledUser = new User(2L, "bob", "pass", "CUSTOMER", false);

        when(userRepository.findByUsername("alice")).thenReturn(user);
        when(userRepository.findByUsername("bob")).thenReturn(disabledUser);

        assertThat(userController.login("alice", "pass")).isEqualTo(user);

        assertThatThrownBy(() -> userController.login("", "pass"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userController.login(null, "pass"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userController.login("alice", ""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userController.login("alice", null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userController.login("alice", "wrongpass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid username or password");

        when(userRepository.findByUsername("unknown")).thenReturn(null);
        assertThatThrownBy(() -> userController.login("unknown", "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid username or password");

        assertThatThrownBy(() -> userController.login("bob", "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User account is disabled");
    }
}
