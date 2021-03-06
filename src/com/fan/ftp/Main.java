package com.fan.ftp;

import com.fan.ftp.controller.ClientController;
import com.fan.ftp.controller.LoginController;
import com.fan.ftp.model.FileModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;

public class Main extends Application {
    private FileModel[] files;
    private Stage window;
    private MyFTP ftp;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        showLoginView();
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
    }

    /**
     * show login window
     */
    public void showLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("views/login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.setMain(this);
        window.setOnCloseRequest(null);
        window.setTitle("FTP Client-Please login");
        Scene scene = new Scene(root,400,400);
        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }

    /**
     * show the client window
     */
    public void showClientView(FileModel[] files) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("views/client.fxml"));
        Parent root = loader.load();
        ClientController controller = loader.getController();
        controller.setMain(this);
        controller.init(files);
        // close the window directly,then tell the server I want to disconnect with you
        window.setOnCloseRequest(e -> {
            try {
                ftp.logout();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        window.setTitle("FTP Client");
        Scene scene = new Scene(root,750,500);
        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }


    public FileModel[] getFiles() {
        return files;
    }

    public void setFiles(FileModel[] files) {
        this.files = files;
    }

    public Stage getWindow() {
        return window;
    }

    public void setWindow(Stage window) {
        this.window = window;
    }

    public MyFTP getFtp() {
        return ftp;
    }

    public void setFtp(MyFTP ftp) {
        this.ftp = ftp;
    }
}
