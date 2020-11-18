package com.fan.ftp.controller;

import com.fan.ftp.Ftp_by_me_active;
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
        Ftp_by_me_active ftp = new Ftp_by_me_active(loginData[0],loginData[2],loginData[3]);
        int code = 0;
        try {
            code = ftp.initftp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (code == 1){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FTP Client");
            alert.setHeaderText("This User is not existed");
            alert.initOwner(main.getWindow());
            alert.showAndWait();
            return;
        }
        if (code == 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FTP Client");
            alert.setHeaderText("Wrong Password!");
            alert.initOwner(main.getWindow());
            alert.showAndWait();
            return;
        }
        main.setFtp(ftp);
        if (Ftp_by_me_active.isLogined){
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    //Update UI here
                    try {
                        System.out.println(ftp.getAllFile().length);
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
