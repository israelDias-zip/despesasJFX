package org.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.utils.HibernateUtil;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Carrega o arquivo FXML que você criou
        // O nome deve ser EXATAMENTE como você salvou em resources
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("despesa-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 800, 600); // Tamanho da janela
        stage.setTitle("Gestão de Despesas");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {

        HibernateUtil.shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}