package com.u2tzjtne.logrepoter.core.save.imp;

import com.u2tzjtne.logrepoter.core.LogReport;
import com.u2tzjtne.logrepoter.core.save.ISave;
import com.u2tzjtne.logrepoter.core.util.LogUtil;

/**
 * 用于写入Log到本地
 *
 * @author wenmingvs
 * @date 2016/7/9
 */
public class LogWriter {
    private static LogWriter mLogWriter;
    private static ISave mSave;

    private LogWriter() {
    }

    public static LogWriter getInstance() {
        if (mLogWriter == null) {
            synchronized (LogReport.class) {
                if (mLogWriter == null) {
                    mLogWriter = new LogWriter();
                }
            }
        }
        return mLogWriter;
    }


    public void init(ISave save) {
        mSave = save;
    }

    public static void writeLog(String tag, String content) {
        LogUtil.d(tag, content);
        mSave.writeLog(tag, content);
    }
}