package com.ticketreservation.view;

import com.ticketreservation.controller.UserController;
import com.ticketreservation.model.User;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class LoginView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final UserController userController;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;

    private User authenticatedUser;

    public LoginView(UserController userController) {
        this.userController = userController;
        initUI();
    }

    private void initUI() {
        setTitle("Event Ticket System - Login");
        setSize(400, 220);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        centerPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        usernameField.setName("usernameField");
        centerPanel.add(usernameField);

        centerPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        passwordField.setName("passwordField");
        centerPanel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setName("loginButton");
        loginButton.addActionListener(e -> loginAction());
        centerPanel.add(loginButton);

        errorLabel = new JLabel(" ");
        errorLabel.setName("errorLabel");
        errorLabel.setForeground(Color.RED);
        centerPanel.add(errorLabel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void loginAction() {
        errorLabel.setText(" ");
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            authenticatedUser = userController.login(username, password);
            dispose();
        } catch (RuntimeException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}
