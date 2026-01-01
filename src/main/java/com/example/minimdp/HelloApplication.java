package com.example.minimdp;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        stage.setResizable(true);
        stage.setMinWidth(450);
        stage.setMinHeight(320);

        DatabaseConnection.initializeDatabase();


        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/minimdp/login.fxml"));

        Scene scene = new Scene(loader.load(), 600, 400);
        stage.setTitle("MiniMDB - Login");
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void stop() {
        DatabaseConnection.close();
    }


    public static void main(String[] args) {
        launch();
    }
}
