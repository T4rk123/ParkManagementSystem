package com.amusementpark;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        primaryStage.setTitle("Информационно-справочная система парка развлечений");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}