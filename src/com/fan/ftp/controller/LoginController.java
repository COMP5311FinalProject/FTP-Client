package com.fan.ftp.controller;

import com.fan.ftp.Ftp_by_me_active;
import com.fan.ftp.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
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
//        FtpClient.backEnd(this.loginData, this.main);
        Ftp_by_me_active ftp = new Ftp_by_me_active(loginData[0],loginData[2],loginData[3]);
        if (Ftp_by_me_active.isLogined){
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
    }

    public void setMain(Main main) {
        this.main = main;
    }
}
