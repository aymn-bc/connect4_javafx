package com.example;


import com.example.controller.Controlleur;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppPuissance extends Application {
	Controlleur control=new Controlleur();
	@Override
    public void start(Stage stage) {		
		control.gameControlleur();		
		Scene scene = new Scene(control.getFenetre() , 640, 480);
        stage.setScene(scene);
        stage.show();		
    }
    public static void main(String[] args) {
        launch();
    }    
}
