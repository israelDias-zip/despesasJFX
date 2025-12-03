package org;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.utils.HibernateUtil;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hello-view.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Configurações da Janela
            stage.setTitle("Sistema de Gerenciamento");
            stage.setScene(scene);
            stage.centerOnScreen(); // Centraliza a janela ao abrir
            stage.show();

        } catch (IOException e) {
            System.err.println("ERRO CRÍTICO: Não foi possível carregar a interface gráfica.");
            System.err.println("Verifique se o arquivo .fxml está na pasta 'resources' correta.");
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Encerrando aplicação...");
        HibernateUtil.shutdown(); // Fecha o Pool de conexões do banco
        super.stop();
    }

    public static void main(String[] args) {
        launch(args); // Inicia o ciclo de vida do JavaFX
    }
}