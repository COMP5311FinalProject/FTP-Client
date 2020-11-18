package com.fan.ftp;

import com.fan.ftp.model.FileModel;
import com.fan.ftp.utils.MyUtil;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class Ftp_by_me_active {

    private BufferedReader controlReader;
    private PrintWriter controlOut;

    private String ftpusername;
    private String ftppassword;


    private static final int PORT = 21;

    public static boolean isLogined=false ;

    Socket socket;

    public Ftp_by_me_active(String url, String username, String password) throws IOException {
            socket = new Socket(url, PORT);//建立与服务器的socket连接
            setUsername(username);
            setPassword(password);

            controlReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            controlOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }


    public int initftp() throws Exception {
        String msg;
        do {
            msg = controlReader.readLine();
            System.out.println(msg);
        } while (!msg.startsWith("220 "));

        controlOut.println("USER " + ftpusername);

        String response = controlReader.readLine();
        System.out.println(response);
        // username is not existed
        if (!response.startsWith("331 ")) {
            return 1;
        }

        controlOut.println("PASS " + ftppassword);

        response = controlReader.readLine();
        System.out.println(response);
        // password is wrong
        if (!response.startsWith("230 ")) {
            return 2;
        }

        isLogined=true;//登录成功标志
        return 0;
    }

    private void setUsername(String username) {
        this.ftpusername = username;
    }

    private void setPassword(String password) {
        this.ftppassword = password;
    }

    //获取所有文件和文件夹的名字
    public FileModel[] getAllFile() throws Exception {
        String response;
        // Send LIST command
        controlOut.println("LIST");

        // Read command response
        response = controlReader.readLine();
        System.out.println(response);


        // Read data from server
        Vector<FileModel> tempfiles = new Vector<>();

        String line = null;
        while ((line = controlReader.readLine()) != null) {
            if(line.equals("end of files"))
                break;
            System.out.println(line);
            FileModel temp = new FileModel();
            setFtpFileInfo(temp, line);
            tempfiles.add(temp);
        }

        // Read command response
        response = controlReader.readLine();
        System.out.println(response);

        FileModel[] files = new FileModel[tempfiles.size()];
        tempfiles.copyInto(files);//将vector数据存到数组里

        return files;

    }

    //通过字符串解析构造一个FTPfile对象
    private void setFtpFileInfo(FileModel in, String info) {
        String infos[] = info.split(" ");
        Vector<String> vinfos = new Vector<>();
        for (int i = 0; i < infos.length; i++) {
            if (!infos[i].equals(""))
                vinfos.add(infos[i]);
        }
        // in case there are some space in the original file name
        String name = "";
        for (int i = 7;i < vinfos.size();i++){
            name = name + vinfos.get(i) + " ";
        }
        in.setName(name);
        in.setSize(MyUtil.formatSize(Long.valueOf(vinfos.get(4))));
        in.setDate(vinfos.get(5) + " " + vinfos.get(6));

        String type=info.substring(0,1);
        if(type.equals("d"))
        {
            in.setType(1);//设置为文件夹
        }else
        {
            in.setType(0);//设置为文件
        }

    }


    //生成InputStream用于上传本地文件
    public void upload(String File_path) throws Exception {
        //本地文件读取-----------------------------------
        System.out.print("File Path :" + File_path);
        File f = new File(File_path);
        if (!f.exists()) {
            System.out.println("File not Exists...");
            return;
        }
        FileInputStream is = new FileInputStream(f);
        BufferedInputStream input = new BufferedInputStream(is);
        //-----------------------------------------------

        // Send PORT command
        String url="127.0.0.1";
        int dataport=(int)(Math.random()*100000%9999)+1024;
        String portCommand="MYPORT "+ url+","+dataport;
        controlOut.println(portCommand);

        String response;
        response=controlReader.readLine();
        System.out.println(response);


        // Send command STOR
        controlOut.println("STOR " + f.getName());

        // Open data connection
        ServerSocket dataSocketServ = new ServerSocket(dataport);
        Socket dataSocket=dataSocketServ.accept();

        // Read command response
        response = controlReader.readLine();
        System.out.println(response);

        // Read data from server
        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

        output.flush();
        input.close();
        output.close();
        dataSocket.close();


        response = controlReader.readLine();
        System.out.println(response);

    }



    //下载 from_file_name是下载的文件名,to_path是下载到的路径地址
    public void download(String from_file_name, String to_path) throws Exception {
        // Send PORT command
        String url="127.0.0.1";
        int dataport=(int)(Math.random()*100000%9999)+1024;
        String portCommand="MYPORT "+ url+","+dataport;
        controlOut.println(portCommand);

        String response;
        response=controlReader.readLine();
        System.out.println(response);

        //send RETR command
        controlOut.println("RETR " + from_file_name);

        // Open data connection
        ServerSocket dataSocketServ = new ServerSocket(dataport);
        Socket dataSocket=dataSocketServ.accept();


        // Read data from server
        BufferedOutputStream output = new BufferedOutputStream(
                new FileOutputStream(new File(to_path, from_file_name)));
        BufferedInputStream input = new BufferedInputStream(
                dataSocket.getInputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

        output.flush();
        output.close();
        input.close();
        dataSocket.close();

        response = controlReader.readLine();
        System.out.println(response);

        response = controlReader.readLine();
        System.out.println(response);
    }

    public void logout() throws IOException {
        isLogined = false;
        controlOut.println("QUIT");
        String response = controlReader.readLine();
        System.out.println(response);
        socket.close();
    }

}