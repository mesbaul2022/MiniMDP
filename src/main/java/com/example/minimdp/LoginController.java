package com.example.minimdp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML private RadioButton adminRadio;
    @FXML private RadioButton userRadio;

    private ToggleGroup roleGroup;

    @FXML
    public void initialize() {
        roleGroup = new ToggleGroup();
        adminRadio.setToggleGroup(roleGroup);
        userRadio.setToggleGroup(roleGroup);
        userRadio.setSelected(true);
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        boolean wantsAdmin = adminRadio.isSelected();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter email and password.");
            return;
        }

        try {
            User user = UserDAO.findByEmailAndPassword(email, password);
            if (user == null) {
                errorLabel.setText("Invalid email or password.");
                return;
            }

            if (wantsAdmin && !user.isAdmin()) {
                errorLabel.setText("This account is not an admin.");
                return;
            }
            if (!wantsAdmin && user.isAdmin()) {
                errorLabel.setText("Select Admin to login as admin.");
                return;
            }

            Session.setCurrentUser(user);

            Stage stage = (Stage) emailField.getScene().getWindow();
            if (user.isAdmin()) {
                FXMLLoader loader = new FXMLLoader(
                        HelloApplication.class.getResource("/com/example/minimdp/admin-dashboard.fxml")
                );
                stage.setTitle("MiniMDB - Admin Panel");
                Scene scene = new Scene(loader.load(), 900, 520);
                scene.getStylesheets().add(
                        HelloApplication.class.getResource("/com/example/minimdp/theme.css").toExternalForm()
                );
                stage.setScene(scene);
            } else {
                FXMLLoader loader = new FXMLLoader(
                        HelloApplication.class.getResource("/com/example/minimdp/user-dashboard.fxml")
                );
                stage.setTitle("MiniMDB - User Panel");
                Scene scene = new Scene(loader.load(), 900, 520);
                scene.getStylesheets().add(
                        HelloApplication.class.getResource("/com/example/minimdp/theme.css").toExternalForm()
                );
                stage.setScene(scene);
            }
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleOpenRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("register.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setTitle("MiniMDB - Register");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
