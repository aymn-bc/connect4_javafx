package com.example;


import com.example.controller.Controlleur;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppPuissance extends Application {
	Controlleur control=new Controlleur();
	@Override
    public void start(Stage stage) {		
		Scene scene = new Scene(control.getFenetre() , 640, 480);
        stage.setScene(scene);
        stage.show();				
        control.gameControlleur();
    }
    public static void main(String[] args) {
        launch();
    }    
}
