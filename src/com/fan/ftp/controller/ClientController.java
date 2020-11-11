package com.fan.ftp.controller;

import com.fan.ftp.Main;
import com.fan.ftp.model.FileModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.net.URL;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;

public class ClientController {
    private Main main;
    public static ObservableList<FileModel> fileModels = FXCollections.observableArrayList();
    @FXML
    private TableView<FileModel> table;

    @FXML
    private  TableColumn<FileModel, String> name;
    @FXML
    private  TableColumn<FileModel, String> size;
    @FXML
    private  TableColumn<FileModel, String> date;


//    public void showAllFileInfo(FTPFile[] ftpFiles){
//        System.out.println(ftpFiles.length);
//        for (FTPFile file: ftpFiles){
//            fileModels.add(new FileModel(file.getName(),file.getSize(),file.getTimestamp().getTime().toString()));
//        }
//        name.setCellValueFactory(new PropertyValueFactory<FileModel,String>("name"));
//        size.setCellValueFactory(new PropertyValueFactory<FileModel,Long>("size"));
//        date.setCellValueFactory(new PropertyValueFactory<FileModel,String>("date"));
//        table.setItems(fileModels);
//    }

    public void init(FileModel[] files) {
        for (FileModel file: files){
            fileModels.add(file);
//            fileModels.add(new FileModel(file.getName(),file.getSize(),simpleDateFormat.format(file.getTimestamp().getTime())));
//            System.out.println(file.getTimestamp());
        }
        name.setCellValueFactory(new PropertyValueFactory<FileModel,String>("name"));
        size.setCellValueFactory(new PropertyValueFactory<FileModel,String>("size"));
        date.setCellValueFactory(new PropertyValueFactory<FileModel,String>("date"));
        table.setItems(fileModels);
        size.setComparator(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                    double a1 = Double.parseDouble(o1.substring(0,o1.length()-2));
                    double a2 = Double.parseDouble(o2.substring(0,o2.length()-2));
                    if (o1.contains("KB")){
                        a1 = a1 * 1024;
                    }
                    if (o1.contains("MB")){
                        a1 = a1 * 1024 * 1024;
                    }
                    if (o2.contains("KB")){
                        a2 = a2 * 1024;
                    }
                    if (o2.contains("MB")){
                        a2 = a2 * 1024 * 1024;
                    }
                    return (int)(a1 - a2);
                }
        });
    }



    public void setMain(Main main) {
        this.main = main;
    }
}
