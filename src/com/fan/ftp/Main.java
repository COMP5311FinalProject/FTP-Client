package com.fan.ftp;

import com.fan.ftp.controller.ClientController;
import com.fan.ftp.controller.LoginController;
import com.fan.ftp.model.FileModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

public class Main extends Application {
    private FileModel[] files;
    Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        showLoginView();
//        showClientView();
//        Label label1 = new Label("This is Scene1");
//        Button button1 = new Button("Login");
//        button1.setOnAction(e -> {
//            window.setScene(scene2);
//        });
//        GridPane grid = new GridPane();
//        grid.setAlignment(Pos.BASELINE_CENTER);
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(10,25,25,25));
//
//        Text scenetitle = new Text("Welcome");
//        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
//        VBox titleVbox = new VBox(10);
////        titleVbox.setAlignment(Pos.TOP_CENTER);
//        titleVbox.getChildren().add(scenetitle);
//        titleVbox.setPadding(new Insets(30,25,40,0));
//        grid.add(titleVbox, 1, 1, 2, 1);
//
//        Label address = new Label("FTP IP Address:");
//        grid.add(address, 0, 2);
//
//        TextField addressTextField = new TextField();
//        grid.add(addressTextField, 1, 2);
//
//        Label userName = new Label("User Name:");
//        grid.add(userName, 0, 3);
//
//        TextField userTextField = new TextField();
//        grid.add(userTextField, 1, 3);
//
//        Label pw = new Label("Password:");
//        grid.add(pw, 0, 4);
//
//        PasswordField pwBox = new PasswordField();
//        grid.add(pwBox,1,4);
//
//
//        HBox hbBtn = new HBox(10);
//        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
//        hbBtn.getChildren().add(button1);
//        hbBtn.setPadding(new Insets(10,0,0,0));
//        grid.add(hbBtn, 1, 5);
//
//
//        scene1 = new Scene(grid,500,500);
//        Button button2 = new Button("Go back to Scene1");
//        button2.setOnAction(e -> {
//            window.setScene(scene1);
//        });
//        StackPane layout2 = new StackPane();
//        layout2.getChildren().addAll(button2);
//        scene2 = new Scene(layout2,500,500);
//        window.setScene(scene1);
//        window.setTitle("FTP Client");
//        window.show();
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
//        controller.setMain(this);
//        for (FTPFile file: files){
//            System.out.println(file.getName());
//        }
        controller.init(files);
        window.setTitle("FTP Client");
        window.setScene(new Scene(root,900,1000));
        window.show();
    }

    public FileModel[] getFiles() {
        return files;
    }

    public void setFiles(FileModel[] files) {
        this.files = files;
    }
}
