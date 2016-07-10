package com.wenming.library.save;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.wenming.library.LogReport;
import com.wenming.library.encryption.IEncryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 提供通用的保存操作log的日志和设备信息的方法
 * Created by wenmingvs on 2016/7/9.
 */
public abstract class BaseSave implements ISave {

    private final static String TAG = "BaseSave";


    /**
     * 根据日期创建文件夹,文件夹的名称以日期命名,下面是日期的格式
     */
    public final static SimpleDateFormat CREATE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * 在每一条log前面增加一个时间戳
     */
    public final static SimpleDateFormat LOG_FOLDER_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS", Locale.getDefault());


    /**
     * 日志的保存的类型
     */
    public static final String SAVE_FILE_TYPE = ".txt";

    /**
     * 日志命名的其中一部分：时间戳
     */
    public final static String LOG_CREATE_TIME = CREATE_DATE_FORMAT.format(new Date(System.currentTimeMillis()));

    public static String LOG_DIR;

    /**
     * 操作日志全名拼接
     */
    public final static String LOG_FILE_NAME_MONITOR = "MonitorLog" + LOG_CREATE_TIME + SAVE_FILE_TYPE;

    public Context mContext;

    /**
     * 加密方式
     */
    public static IEncryption mEncryption;

    public BaseSave(Context context) {
        this.mContext = context;
    }

    /**
     * 写入设备的各种参数信息之前，请确保File文件以及他的父路径是存在的
     *
     * @param file
     */
    public File createFile(File file, Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Application Information").append('\n');
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = context.getApplicationInfo();
        sb.append("App Name : ").append(pm.getApplicationLabel(ai)).append('\n');
        try {
            PackageInfo pi = pm.getPackageInfo(ai.packageName, 0);
            sb.append("Version Code: ").append(pi.versionCode).append('\n');
            sb.append("Version Name: ").append(pi.versionName).append('\n');
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        sb.append('\n');
        sb.append("DEVICE INFORMATION").append('\n');
        sb.append("BOOTLOADER: ").append(Build.BOOTLOADER).append('\n');
        sb.append("BRAND: ").append(Build.BRAND).append('\n');
        sb.append("DEVICE: ").append(Build.DEVICE).append('\n');
        sb.append("HARDWARE: ").append(Build.HARDWARE).append('\n');

        //TODO 支持添加更多信息
        sb = new StringBuilder(encodeString(sb.toString()));

        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 用于在每条log前面，增加更多的文本信息，包括时间，线程名字等等
     */
    public static String formatLogMsg(String tag, String tips) {
        final String timeStr = LOG_FOLDER_TIME_FORMAT.format(Calendar.getInstance().getTime());
        final Thread currThread = Thread.currentThread();
        StringBuilder sb = new StringBuilder();
        sb.append("Trd: ").append(currThread.getId()).append(" ").append(currThread.getName()).append(" ").append(timeStr).append(" Class: ").append(tag).append(" > ").append(tips);
        sb = new StringBuilder(encodeString(sb.toString()));
        return sb.toString();
    }

    @Override
    public File writeLog(String tag, String content) {
        content = encodeString(content);
        LOG_DIR = LogReport.LOGDIR+"/Log/"+ CREATE_DATE_FORMAT.format(new Date(System.currentTimeMillis()));
        RandomAccessFile randomAccessFile = null;
        File logFile = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File logsDir = new File(LOG_DIR);
                if (!logsDir.exists()) {
                    logsDir.mkdirs();
                }
                logFile = new File(logsDir, LOG_FILE_NAME_MONITOR);
                if (!logFile.exists()) {
                    createFile(logFile, mContext);
                }
                randomAccessFile = new RandomAccessFile(logFile, "rw");
                randomAccessFile.seek(logFile.length());
                randomAccessFile.write(("\r\n" + formatLogMsg(tag, content)).getBytes());

            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
        return logFile;
    }

    @Override
    public void setEncodeType(IEncryption encodeType) {
        mEncryption = encodeType;
    }

    public static String encodeString(String content) {
        if (mEncryption != null) {
            try {
                return mEncryption.encrypt(content);
            } catch (Exception e) {
                e.printStackTrace();
                return content;
            }
        } else {
            return content;
        }
    }


}