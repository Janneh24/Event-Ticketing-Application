package com.ticketreservation.view;

import com.ticketreservation.controller.UserController;
import com.ticketreservation.model.User;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginViewTest {

    @Mock
    private UserController userController;

    private FrameFixture window;
    private LoginView view;

    @BeforeEach
    void setUp() {
        view = GuiActionRunner.execute(() -> new LoginView(userController));
        window = new FrameFixture(view);
        window.show();
    }

    @AfterEach
    void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @Test
    void testInitialState() {
        window.textBox("usernameField").requireEmpty();
        window.textBox("passwordField").requireEmpty();
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testLoginSuccess() {
        User user = new User(1L, "alice", "pass", "CUSTOMER", true);
        when(userController.login("alice", "pass")).thenReturn(user);

        window.textBox("usernameField").setText("alice");
        window.textBox("passwordField").setText("pass");
        GuiActionRunner.execute(() -> window.button("loginButton").target().doClick());

        verify(userController).login("alice", "pass");
        assertThat(view.getAuthenticatedUser()).isEqualTo(user);
    }

    @Test
    void testLoginFailureShowsError() {
        when(userController.login("invalid", "wrong"))
                .thenThrow(new RuntimeException("Invalid credentials"));

        window.textBox("usernameField").setText("invalid");
        window.textBox("passwordField").setText("wrong");
        GuiActionRunner.execute(() -> window.button("loginButton").target().doClick());

        window.label("errorLabel").requireText("Invalid credentials");
        assertThat(view.getAuthenticatedUser()).isNull();
    }
}
