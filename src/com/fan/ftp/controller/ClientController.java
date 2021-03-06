package com.fan.ftp.controller;

import com.fan.ftp.Main;
import com.fan.ftp.model.FileModel;
import com.fan.ftp.utils.MyUtil;
import impl.jfxtras.styles.jmetro.ToggleSwitchSkin;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.controlsfx.control.ToggleSwitch;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    private Main main;
    public static ObservableList<FileModel> fileModels = FXCollections.observableArrayList();
    @FXML
    private TableView<FileModel> table;
    @FXML
    private TableColumn<FileModel, String> name;
    @FXML
    private TableColumn<FileModel, String> size;
    @FXML
    private TableColumn<FileModel, String> date;
    @FXML
    private TableColumn<FileModel, Void> action;
    @FXML
    private ImageView imageView;
    @FXML
    private ToggleSwitch pasvSwitch;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label tip;

    private boolean isPassive = false;

    public void init(FileModel[] files) {
        for (FileModel file : files) {
            fileModels.add(file);
        }
        // binding property
        name.setCellValueFactory(new PropertyValueFactory<FileModel, String>("name"));
        size.setCellValueFactory(new PropertyValueFactory<FileModel, String>("size"));
        date.setCellValueFactory(new PropertyValueFactory<FileModel, String>("date"));
        action.setSortable(false);
        addButtonToTable();

        table.setItems(fileModels);
        // set default sort policy according to the modified time
        date.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().addAll(date);
        // rewrite size column sort policy
        size.setComparator(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return MyUtil.compareSize(o1, o2);
            }
        });
    }


    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * add a download button to each row and set event for it
     */
    private void addButtonToTable() {
        Callback<TableColumn<FileModel, Void>, TableCell<FileModel, Void>> cellFactory = new Callback<TableColumn<FileModel, Void>, TableCell<FileModel, Void>>() {
            @Override
            public TableCell<FileModel, Void> call(final TableColumn<FileModel, Void> param) {
                final TableCell<FileModel, Void> cell = new TableCell<FileModel, Void>() {
                    private final Button btn = new Button("download");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            if (isBusyNow()) return;
                            DirectoryChooser file=new DirectoryChooser();
                            file.setTitle("Choose the local directory for FTP");
                            File newFolder = file.showDialog(main.getWindow());
                            if (newFolder == null) {
                                return; // close the chooser directly
                            }
                            FileModel data = getTableView().getItems().get(getIndex());
                            Task copyWorker = null;
                            try {
                                // show tip: 1 file is downing now
                                tip.setVisible(true);
                                progressBar.setVisible(true);
                                progressBar.progressProperty().unbind();
                                if (isPassive){
                                    main.getFtp().setPasvMode(true);
                                }
                                copyWorker = main.getFtp().download(data.getName(), newFolder.getPath(), MyUtil.formatSizeToLong(data.getSize()));
                                progressBar.progressProperty().bind(copyWorker.progressProperty());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            copyWorker.messageProperty().addListener(new ChangeListener<String>() {
                                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                    String newValue) {
                                    System.out.println(newValue);
                                    // download is finished, hide the tip and progress bar
                                    if (newValue.equals("finish")) {
                                        tip.setVisible(false);
                                        progressBar.setVisible(false);
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("FTP Client");
                                        alert.setHeaderText("Download Successfully!");
                                        alert.initOwner(main.getWindow());
                                        alert.showAndWait();
                                    }
                                }
                            });
                            new Thread(copyWorker).start();
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

    /**
     * upload file function
     */
    public void upload() {
        if (isBusyNow()) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(main.getWindow());
        if (file != null) {
            try {
                main.getFtp().upload(file.getPath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("FTP Client");
                alert.setHeaderText("Upload Successfully!");
                alert.initOwner(main.getWindow());
                alert.showAndWait();
                refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * judge whether there is a task is executing now
     * @return
     */
    private boolean isBusyNow() {
        if (tip.visibleProperty().getValue()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("FTP Client");
            alert.setHeaderText("Please wait current download finish!");
            alert.initOwner(main.getWindow());
            alert.showAndWait();
            return true;
        }
        return false;
    }


    /**
     * logout function
     */
    public void logout() throws IOException {
        if (isBusyNow()) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure to logout");
        // modify the icon of this alert
        alert.initOwner(main.getWindow());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //Update UI here
                    try {
                        main.getFtp().logout();
                        fileModels.clear();
                        main.showLoginView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * refresh function
     */
    public void refresh() {
        if (isBusyNow()) return;
        try {
            if (isPassive) {
                main.getFtp().setPasvMode(true);
            }
            FileModel[] files = main.getFtp().getAllFile();
            fileModels.clear();
            for (FileModel file : files) {
                fileModels.add(file);
            }
            table.setItems(fileModels);
            table.getSortOrder().clear();
            table.getSortOrder().addAll(date);
            table.scrollTo(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageView.setImage(new Image(Main.class.getResourceAsStream("ftp.png")));
        pasvSwitch.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            isPassive = newValue;
        }));
        progressBar.setVisible(false);
        tip.setVisible(false);
    }


}
