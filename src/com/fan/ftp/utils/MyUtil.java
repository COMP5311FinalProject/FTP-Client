package com.fan.ftp.utils;

public class MyUtil {
    /**
     * if the size is large, convert it to KB or MB
     */
    public static String formatSize(Long size){
        if (size < 1024){
            return size + "B ";
        } else if ((size < 1024 * 1024)) {
            return String.format("%.2f",(double)size / 1024) + "KB";
        } else {
            return String.format("%.2f", (double)size / (1024 * 1024)) + "MB";
        }
    }

}
