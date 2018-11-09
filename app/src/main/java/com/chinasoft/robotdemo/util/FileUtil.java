package com.chinasoft.robotdemo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by chinasoft_gyr on 2018/11/8.
 */

public class FileUtil {

    public static void writeBytesToFile(InputStream is, File file) throws IOException {
        FileOutputStream fos = null;
        try {
            byte[] data = new byte[2048];
            int nbread = 0;
            fos = new FileOutputStream(file);
            while((nbread=is.read(data))>-1){
                fos.write(data,0,nbread);
            }
        }catch (Exception ex) {
        }
        finally{
            if (fos!=null){
                fos.close();
            }
        }
    }
}
