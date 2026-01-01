package com.example.minimdp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    public void handleRegister(ActionEvent event) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        try {
            if (UserDAO.emailExists(email)) {
                messageLabel.setText("Email already registered.");
                messageLabel.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }

            User user = new User(name, email, password, "USER");
            UserDAO.createUser(user);

            messageLabel.setText("Account created. You can log in now.");
            messageLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBackToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setTitle("MiniMDB - Login");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
