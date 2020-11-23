package com.fan.ftp.controller;

import com.fan.ftp.MyFTP;
import com.fan.ftp.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private Main main;
    @FXML
    private TextField userNameText;
    @FXML
    private PasswordField passwordText;
    @FXML
    private Button resetButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField portText;
    @FXML
    private TextField serverAddressText;
    @FXML
    private ImageView imageView;

    private String[] loginData;

    public LoginController() {
    }

    @FXML
    public void loginButtonAction(ActionEvent event) throws IOException {
        this.loginData = new String[5];
        this.loginData[0] = this.serverAddressText.getText();
        this.loginData[1] = this.portText.getText();
        this.loginData[2] = this.userNameText.getText();
        this.loginData[3] = this.passwordText.getText();
        MyFTP ftp;
        try {
            ftp = new MyFTP(loginData[0],loginData[2],loginData[3],Integer.parseInt(loginData[1]));
        } catch (ConnectException e){
            // server not start or server has some problems
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FTP Client");
            alert.setHeaderText("There is something wrong with the server");
            alert.initOwner(main.getWindow());
            alert.showAndWait();
            return;
        }
        int code = 0;
        try {
            code = ftp.initFtp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // alert a window to tell user the user is not existed
        if (code == 1){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FTP Client");
            alert.setHeaderText("This User is not existed");
            alert.initOwner(main.getWindow());
            alert.showAndWait();
            return;
        }
        // alert a window to tell user the password is wrong
        if (code == 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FTP Client");
            alert.setHeaderText("Wrong Password!");
            alert.initOwner(main.getWindow());
            alert.showAndWait();
            return;
        }
        main.setFtp(ftp);
        // login successfully to get all files
        if (MyFTP.isLogin){
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    //Update UI here
                    try {
                        main.setFiles(ftp.getAllFile());
                        main.showClientView(ftp.getAllFile());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @FXML
    public void resetButtonAction(ActionEvent event) {
        this.userNameText.setText((String)null);
        this.passwordText.setText((String)null);
        this.serverAddressText.setText((String)null);
        this.portText.setText((String)null);
    }



    public void setMain(Main main) {
        this.main = main;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageView.setImage(new Image(Main.class.getResourceAsStream("ftp.png")));
    }
}
