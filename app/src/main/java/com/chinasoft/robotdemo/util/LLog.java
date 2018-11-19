package com.chinasoft.robotdemo.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class LLog {
    private static LLog lLog;
    private String PATH_LOGCAT = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append("robotdemo").toString();

    public static class MyDate {
        public static String getFileName() {
            return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        }

        public static String getDateEN() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        }
    }

    private LLog() {
    }

    public static LLog getLog() {
        if (lLog == null) {
            synchronized (LLog.class) {
                if (lLog == null) {
                    lLog = new LLog();
                }
            }
        }
        return lLog;
    }

    public void e(String title, String strcontent) {
        Log.e(title, strcontent);
        write(this.PATH_LOGCAT, "Log" + MyDate.getFileName() + ".txt", title, strcontent);
    }

    public void prru(String title, String strcontent) {
        Log.e(title, strcontent);
        write(this.PATH_LOGCAT, "Prru" + MyDate.getFileName() + ".txt", title, strcontent);
    }

    public void crash(String title, String strcontent) {
        write(this.PATH_LOGCAT, "crash" + MyDate.getFileName() + ".txt", title, strcontent);
    }

    private void write(String filePath, String fileName, String title, String strcontent) {
        Exception e;
        Throwable th;
        makeFilePath(filePath, fileName);
        String strFilePath = new StringBuilder(String.valueOf(filePath)).append("/").append(fileName).toString();
        String strContent = MyDate.getDateEN() + " "+title +" "+ strcontent + "\r\n";
        RandomAccessFile raf = null;
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                boolean mkdirs = file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf2 = new RandomAccessFile(file, "rwd");
            try {
                raf2.seek(file.length());
                raf2.write(strContent.getBytes());
                if (raf2 != null) {
                    try {
                        raf2.close();
                        raf = raf2;
                        return;
                    } catch (IOException e2) {
                        Log.e("e", e2.toString());
                    }
                }
                raf = raf2;
            } catch (Exception e3) {
                e = e3;
                raf = raf2;
                try {
                    Log.e("TestFile", "Error on write File:" + e);
                    if (raf != null) {
                        try {
                            raf.close();
                        } catch (IOException e22) {
                            Log.e("e", e22.toString());
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (raf != null) {
                        try {
                            raf.close();
                        } catch (IOException e222) {
                            Log.e("e", e222.toString());
                        }
                    }
//                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                raf = raf2;
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e2222) {
                        Log.e("e", e2222.toString());
                    }
                }
//                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            Log.e("TestFile", "Error on write File:" + e);
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e22222) {
                    Log.e("e", e22222.toString());
                }
            }
        }
    }

    private static void makeRootDirectory(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            Log.i("error:", e.toString());
        }
    }

    private File makeFilePath(String filePath, String fileName) {
        Exception e;
        File file = null;
        makeRootDirectory(filePath);
        try {
            File file2 = new File(new StringBuilder(String.valueOf(filePath)).append("/").append(fileName).toString());
            try {
                if (file2.exists()) {
                    return file2;
                }
                file2.createNewFile();
                return file2;
            } catch (Exception e2) {
                e = e2;
                file = file2;
                Log.e("error", e.toString());
                return file;
            }
        } catch (Exception e3) {
            e = e3;
            Log.e("error", e.toString());
            return file;
        }
    }
}
