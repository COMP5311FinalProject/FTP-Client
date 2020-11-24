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
    /**
     * convert size to long
     */
    public static long formatSizeToLong(String str) {
        Double size = Double.parseDouble(str.substring(0,str.length()-2));
        if (str.contains("KB")){
            size = size * 1024;
        }
        if (str.contains("MB")){
            size = size * 1024 * 1024;
        }
        if (str.contains("KB")){
            size = size * 1024;
        }
        return size.longValue();
    }

    /**
     * convert KB, MB to B and then compare them
     */
    public static int compareSize(String size1, String size2) {
        double a1 = Double.parseDouble(size1.substring(0,size1.length()-2));
        double a2 = Double.parseDouble(size2.substring(0,size2.length()-2));
        if (size1.contains("KB")){
            a1 = a1 * 1024;
        }
        if (size1.contains("MB")){
            a1 = a1 * 1024 * 1024;
        }
        if (size2.contains("KB")){
            a2 = a2 * 1024;
        }
        if (size2.contains("MB")){
            a2 = a2 * 1024 * 1024;
        }
        return (int)(a1 - a2);
    }

}
