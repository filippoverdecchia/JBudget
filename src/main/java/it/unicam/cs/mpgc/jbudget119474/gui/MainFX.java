package it.unicam.cs.mpgc.jbudget119474.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {
    @Override
    public void start(Stage stage) {
        JBudgetView view = new JBudgetView();
        Scene scene = new Scene(view, 800, 600);
        stage.setTitle("JBudget - Gestione Bilancio");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
