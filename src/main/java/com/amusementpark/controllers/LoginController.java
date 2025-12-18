package com.amusementpark.controllers;

import com.amusementpark.models.User;
import com.amusementpark.services.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private AuthService authService = new AuthService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите логин и пароль!");
            return;
        }

        User user = authService.login(username, password);
        if (user != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                Parent root = loader.load();
                MainController controller = loader.getController();
                controller.setUser(user);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));
                stage.setTitle("Главная - " + user.getRole());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить главное окно: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Неверный логин или пароль!");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Регистрация нового пользователя");
        dialog.setHeaderText("");


        dialog.getDialogPane().setStyle("-fx-background-color: linear-gradient(to bottom, #e3f2fd, #f5f5f5); -fx-background-radius: 10; -fx-padding: 15; .button-bar { -fx-alignment: center; }");


        VBox dialogContent = new VBox(12.0);
        dialogContent.setAlignment(Pos.CENTER);
        dialogContent.setStyle("-fx-padding: 15; -fx-spacing: 12; -fx-background-radius: 8; -fx-background-color: #f8f9fa;");


        Label headerLabel = new Label("Заполните данные для регистрации");
        headerLabel.setWrapText(false);
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #3498db, #2980b9); -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-padding: 0 0 10 0; -fx-alignment: center;");
        dialogContent.getChildren().add(headerLabel);

        TextField regUsername = new TextField();
        regUsername.setPromptText("Логин (минимум 6 символов)");
        regUsername.setPrefWidth(300);
        regUsername.setStyle("-fx-background-radius: 5; -fx-padding: 8;");

        PasswordField regPassword = new PasswordField();
        regPassword.setPromptText("Пароль (минимум 6 символов)");
        regPassword.setPrefWidth(300);
        regPassword.setStyle("-fx-background-radius: 5; -fx-padding: 8;");

        Label usernameLabel = new Label("Логин:");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0277bd; -fx-font-size: 15px;");

        Label passwordLabel = new Label("Пароль:");
        passwordLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0277bd; -fx-font-size: 15px;");

        dialogContent.getChildren().addAll(
                usernameLabel, regUsername,
                passwordLabel, regPassword
        );

        dialog.getDialogPane().setContent(dialogContent);


        ButtonType registerButtonType = new ButtonType("Регистрация", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, cancelButtonType);


        dialog.setOnCloseRequest((DialogEvent e) -> dialog.close());


        Button registerButton = (Button) dialog.getDialogPane().lookupButton(registerButtonType);
        if (registerButton != null) {
            registerButton.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 20;");
        }


        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        if (cancelButton != null) {
            cancelButton.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: #34495e; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 20;");
        }


        dialog.setResultConverter(new Callback<ButtonType, ButtonType>() {
            @Override
            public ButtonType call(ButtonType param) {
                if (param == registerButtonType) {
                    String username = regUsername.getText().trim();
                    String password = regPassword.getText();
                    String role = "USER";


                    if (username.isEmpty() || password.isEmpty() || username.length() < 6 || password.length() < 6) {
                        showAlert(Alert.AlertType.ERROR, "Ошибка", "Логин и пароль должны быть минимум 6 символов!");
                        return null;
                    }

                    try {
                        authService.register(username, password, role);
                        showAlert(Alert.AlertType.INFORMATION, "Успех", "Пользователь зарегистрирован! Теперь войдите.");
                        dialog.close();

                        usernameField.setText(username);
                        passwordField.setText(password);
                        handleLogin(event);
                    } catch (RuntimeException e) {
                        if ("Логин уже существует!".equals(e.getMessage())) {
                            showAlert(Alert.AlertType.ERROR, "Ошибка регистрации", "Логин уже существует! Выберите другой.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Ошибка регистрации", "Не удалось зарегистрироваться: " + e.getMessage());
                        }
                        return null;
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Ошибка регистрации", "Не удалось зарегистрироваться: " + e.getMessage());
                        return null;
                    }
                }
                return param;
            }
        });

        dialog.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}