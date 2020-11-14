package com.fan.ftp.controller;

import com.fan.ftp.Main;
import com.fan.ftp.model.FileModel;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.net.URL;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
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
    @FXML
    private TableColumn<FileModel, Void> action;


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
        }
        name.setCellValueFactory(new PropertyValueFactory<FileModel,String>("name"));
        size.setCellValueFactory(new PropertyValueFactory<FileModel,String>("size"));
        date.setCellValueFactory(new PropertyValueFactory<FileModel,String>("date"));
        action.setSortable(false);
        addButtonToTable();

        table.setItems(fileModels);
        // rewrite size column sort policy
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

    private void addButtonToTable() {

        Callback<TableColumn<FileModel, Void>, TableCell<FileModel, Void>> cellFactory = new Callback<TableColumn<FileModel, Void>, TableCell<FileModel, Void>>() {
            @Override
            public TableCell<FileModel, Void> call(final TableColumn<FileModel, Void> param) {
                final TableCell<FileModel, Void> cell = new TableCell<FileModel, Void>() {

                    private final Button btn = new Button("download");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            DirectoryChooser file=new DirectoryChooser();
                            file.setTitle("Choose the local directory for FTP");
                            File newFolder = file.showDialog(main.getWindow());
                            FileModel data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data.getName());
                            try {
                                main.getFtp().download(data.getName(),newFolder.getPath());
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("FTP Client");
                                alert.setHeaderText("Down Successfully!");
                                alert.initOwner(main.getWindow());
                                alert.showAndWait();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                            setAlignment(Pos.CENTER);
                        }
                    }
                };
                return cell;
            }
        };
        action.setCellFactory(cellFactory);
    }

    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure to logout");
        // modify the icon of this alert
        alert.initOwner(main.getWindow());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            System.out.println("OK");
        } else {
            System.out.println("NO");
        }
    }
}
