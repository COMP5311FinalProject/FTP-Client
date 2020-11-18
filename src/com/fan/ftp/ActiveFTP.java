package com.fan.ftp;

import com.fan.ftp.model.FileModel;
import com.fan.ftp.utils.MyUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ActiveFTP {

    private BufferedReader fromServer;
    private PrintWriter toServer;
    private String ftpUsername;
    private String ftpPassword;
    public static boolean isLogin =false ;

    Socket socket;

    public ActiveFTP(String url, String username, String password, int port) throws IOException {
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

        //Send PORT command
        int dataPort = sendPortCommand();
        String response;

        //Send command STOR
        toServer.println("STOR " + f.getName());

        // pen data connection
        ServerSocket dataSocketServ = new ServerSocket(dataPort);
        Socket dataSocket=dataSocketServ.accept();

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
    public void download(String from_file_name, String to_path) throws Exception {
        // send PORT command
        int dataPort = sendPortCommand();
        String response;

        // send RETR command
        toServer.println("RETR " + from_file_name);

        // Open data connection
        ServerSocket dataSocketServ = new ServerSocket(dataPort);
        Socket dataSocket=dataSocketServ.accept();

        // read data from server
        BufferedOutputStream output = new BufferedOutputStream(
                new FileOutputStream(new File(to_path, from_file_name)));
        BufferedInputStream input = new BufferedInputStream(
                dataSocket.getInputStream());
        readDataFromServer(input,output);
        dataSocket.close();

        response = fromServer.readLine();
        System.out.println(response);

        response = fromServer.readLine();
        System.out.println(response);
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

}