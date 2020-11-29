package com.fan.ftp;

import com.fan.ftp.model.FileModel;
import com.fan.ftp.utils.MyUtil;
import javafx.concurrent.Task;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

public class MyFTP {

    private BufferedReader fromServer;
    private PrintWriter toServer;
    private String ftpUsername;
    private String ftpPassword;
    public static boolean isLogin = false;
    private String passHost="127.0.0.1";
    private int passPort=21;
    private boolean isPasvMode = false;

    Socket socket;

    public MyFTP(String url, String username, String password, int port) throws IOException {
        //connect to the server
        socket = new Socket(url, port);
        setUsername(username);
        setPassword(password);

        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        toServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }


    public int initFtp() throws Exception {
        String msg;
        do {
            msg = fromServer.readLine();
            System.out.println(msg);
        } while (!msg.startsWith("220 "));

        toServer.println("USER " + ftpUsername);

        String response = fromServer.readLine();
        System.out.println(response);
        // username is not existed
        if (!response.startsWith("331 ")) {
            return 1;
        }

        toServer.println("PASS " + ftpPassword);

        response = fromServer.readLine();
        System.out.println(response);
        // password is wrong
        if (!response.startsWith("230 ")) {
            return 2;
        }

        isLogin =true;//login successfully
        return 0;
    }

    private void setUsername(String username) {
        this.ftpUsername = username;
    }

    private void setPassword(String password) {
        this.ftpPassword = password;
    }

    //get all files
    public FileModel[] getAllFile() throws Exception {
        String response;
        // Send LIST command
        toServer.println("LIST");

        // Read command response
        response = fromServer.readLine();
        System.out.println(response);

        // Read data from server
        Vector<FileModel> tempfiles = new Vector<>();

        String line = null;
        while ((line = fromServer.readLine()) != null) {
            if(line.equals("end of files"))
                break;
            FileModel temp = new FileModel();
            setFtpFileInfo(temp, line);
            tempfiles.add(temp);
        }

        // Read command response
        response = fromServer.readLine();
        System.out.println(response);

        FileModel[] files = new FileModel[tempfiles.size()];
        tempfiles.copyInto(files);

        return files;
    }

    //resolve a FileModel object by the string
    private void setFtpFileInfo(FileModel in, String info) {
        String infos[] = info.split(" ");
        Vector<String> vInfos = new Vector<>();
        for (int i = 0; i < infos.length; i++) {
            if (!infos[i].equals(""))
                vInfos.add(infos[i]);
        }
        // in case there are some space in the original file name
        String name = "";
        for (int i = 7;i < vInfos.size();i++){
            name = name + vInfos.get(i) + " ";
        }
        in.setName(name);
        in.setSize(MyUtil.formatSize(Long.valueOf(vInfos.get(4))));
        in.setDate(vInfos.get(5) + " " + vInfos.get(6));

        String type=info.substring(0,1);
        if(type.equals("d"))
        {
            in.setType(1);//set it to directory
        }else
        {
            in.setType(0);//set it to file
        }

    }


    //use InputStream to upload local file to the server
    public void upload(String File_path) throws Exception {
        System.out.print("File Path :" + File_path);
        File f = new File(File_path);
        if (!f.exists()) {
            System.out.println("File not Exists...");
            return;
        }
        FileInputStream is = new FileInputStream(f);
        BufferedInputStream input = new BufferedInputStream(is);

        String response;
        Socket dataSocket = null;

        if (isPasvMode) {
            //passive mode
            checkIsPassiveMode();
            //Send command STOR
            toServer.println("STOR " + f.getName());
            dataSocket = new Socket(passHost,passPort);
        } else {
            //主动模式:Send PORT command
            int dataPort = sendPortCommand();
            // Open data connection
            ServerSocket dataSocketServ = new ServerSocket(dataPort);
            //Send command STOR
            toServer.println("STOR " + f.getName());
            dataSocket=dataSocketServ.accept();
        }

        //Read command response
        response = fromServer.readLine();
        System.out.println(response);

        //Read data from server
        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
        readDataFromServer(input,output);
        dataSocket.close();

        response = fromServer.readLine();
        System.out.println(response);

    }

    //download file from server to the local path
    public Task download(String from_file_name, String to_path, Long size) throws Exception {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                String response;
                Socket dataSocket = null;
                if (isPasvMode){
                    checkIsPassiveMode();
                    toServer.println("RETR " + from_file_name);
                    dataSocket = new Socket(passHost,passPort);
                } else {
                    // 主动模式: send PORT command
                    int dataPort = sendPortCommand();
                    // Open data connection
                    ServerSocket dataSocketServ = new ServerSocket(dataPort);
                    toServer.println("RETR " + from_file_name);
                    dataSocket=dataSocketServ.accept();
                }
                // read data from server
                BufferedOutputStream output = new BufferedOutputStream(
                        new FileOutputStream(new File(to_path, from_file_name)));
                BufferedInputStream input = new BufferedInputStream(
                        dataSocket.getInputStream());

                byte[] buffer = new byte[4096];
                int bytesRead = 0;
                long currentRead = 0L;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    currentRead += bytesRead;
                    // update progress bar
                    updateProgress(currentRead,size);
                    try {
                        Thread.sleep(10);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                output.flush();
                updateMessage("finish");
                output.close();
                input.close();


                dataSocket.close();

                response = fromServer.readLine();
                System.out.println(response);

                response = fromServer.readLine();
                System.out.println(response);
                return true;
            }
        };
    }

    public void logout() throws IOException {
        isLogin = false;
        toServer.println("QUIT");
        String response = fromServer.readLine();
        System.out.println(response);
        socket.close();
    }

    private int sendPortCommand() throws IOException {
        String url="127.0.0.1";
        int dataPort=(int)(Math.random()*100000%9999)+1024;
        String portCommand="MYPORT "+ url+","+dataPort;
        toServer.println(portCommand);
        String response;
        response= fromServer.readLine();
        System.out.println(response);
        return dataPort;
    }

    /**
     * read data from server
     */
    private void readDataFromServer(BufferedInputStream input, BufferedOutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

        output.flush();
        output.close();
        input.close();
    }

    //tell server I want to use passive mode
    private void checkIsPassiveMode() throws Exception {
        String response;
        toServer.println("PASV");
        response = fromServer.readLine();
        System.out.println(response);
        if (!response.startsWith("2271 ")) {
            throw new IOException("FTPClient could not request passive mode: " + response);
        }

        int opening = response.indexOf('(');
        int closing = response.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = response.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                passHost = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                        + tokenizer.nextToken() + "." + tokenizer.nextToken();
                passPort = Integer.parseInt(tokenizer.nextToken()) * 256
                        + Integer.parseInt(tokenizer.nextToken());
                System.out.println(passHost);
                System.out.println(passPort);
            } catch (Exception e) {
                throw new IOException(
                        "FTPClient received bad data link information: "
                                + response);
            }
        }
    }

    public boolean isPasvMode() {
        return isPasvMode;
    }

    public void setPasvMode(boolean pasvMode) {
        isPasvMode = pasvMode;
    }
}