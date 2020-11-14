package com.fan.ftp;

import com.fan.ftp.controller.ClientController;
import com.fan.ftp.controller.LoginController;
import com.fan.ftp.model.FileModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private FileModel[] files;
    private Stage window;
    private Ftp_by_me_active ftp;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        showLoginView();
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("ftp.png")));
//        showClientView(files);
    }
    public void showLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("views/login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.setMain(this);
        window.setTitle("FTP Client-Please login");
        window.setScene(new Scene(root,400,400));
        window.show();
    }

    public void showClientView(FileModel[] files) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("views/client.fxml"));
        Parent root = loader.load();
        ClientController controller = loader.getController();
        controller.setMain(this);
        controller.init(files);
        window.setTitle("FTP Client");
        window.setScene(new Scene(root,800,700));
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

    public Ftp_by_me_active getFtp() {
        return ftp;
    }

    public void setFtp(Ftp_by_me_active ftp) {
        this.ftp = ftp;
    }
}
