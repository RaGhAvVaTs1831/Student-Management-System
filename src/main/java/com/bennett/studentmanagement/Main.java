package com.bennett.studentmanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/bennett/studentmanagement/views/MainView.fxml"));
        Scene scene = new Scene(root);
        
        // Apply modern styling
        scene.getStylesheets().add(getClass().getResource("/com/bennett/studentmanagement/styles/main.css").toExternalForm());
        
        primaryStage.setTitle("Bennett University - Student Management System");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 