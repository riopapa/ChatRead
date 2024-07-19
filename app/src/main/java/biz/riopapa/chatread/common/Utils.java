package biz.riopapa.chatread.common;

import static biz.riopapa.chatread.MainActivity.deBug;
import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.packageDirectory;
import static biz.riopapa.chatread.MainActivity.strUtil;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Locale;

import biz.riopapa.chatread.func.ReadyToday;

public class Utils {

    public Utils() {
    }
    /*
        logW writes log on that day, and removed after a few days
        logE writes to download folder, removing by manual operation
     */
    public void logW(String tag, String text) {
        Log.w(tag, text);
        new ReadyToday();
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        String logText  =(traces.length>6) ? excludeName(traces[6].getMethodName()):"";
        logText += excludeName(traces[5].getMethodName()) + excludeName(traces[4].getMethodName()) +
                excludeName(traceClassName(traces[3].getClassName()))+"> "+traces[3].getMethodName() +
                "#" + traces[3].getLineNumber() + " {"+ tag + "} " + text;
        fileIO.append2Today("zLog "+tag+".txt", logText);
    }

    public void logB(String tag, String text) {
        if (deBug) {
            String str = strUtil.text2OneLine(text);
            logW(tag,  str.length()> 80? str.substring(0, 80) : str + "\n");
        }
    }

    public void logE(String tag, String text) {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        String logText  =(traces.length>6) ? excludeName(traces[6].getMethodName()):"";
        logText += excludeName(traces[5].getMethodName()) + excludeName(traces[4].getMethodName()) +
                excludeName(traceClassName(traces[3].getClassName()))+"> "+traces[3].getMethodName() +
                "#" + traces[3].getLineNumber() + " {"+ tag + "} " + text;
        Log.e("<" + tag + ">" , logText);
        fileIO.append2Today("errLog_"+tag+".txt", logText);
//        sounds.beepOnce(MainActivity.soundType.ERR.ordinal());   // error sound
    }

    private String excludeName(String s) {
        String [] omits = { "performResume", "performCreate", "callActivityOnResume", "access$",
                "onNotificationPosted", "NotificationListener", "performCreate", "log",
                "handleReceiver", "handleMessage", "dispatchKeyEvent", "onBindViewHolder"};
        for (String o : omits) {
            if (s.contains(o)) return ". ";
        }
        return s + "> ";
    }
    private String traceClassName(String s) {
        return s.substring(s.lastIndexOf(".")+1);
    }

    /* delete old packageDirectory / files if storage is less than x days */
    public void deleteOldFiles() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd", Locale.KOREA);
        String weekAgo = dateFormat.format(System.currentTimeMillis() - 3*24*60*60*1000L);
        File[] files = packageDirectory.listFiles();
        if (files == null)
            return;
        Collator myCollator = Collator.getInstance();
        for (File file : files) {
            String shortFileName = file.getName();
            if (myCollator.compare(shortFileName, weekAgo) < 0) {
                deleteFolder(file);
            }
        }
    }

    public void deleteFolder(File file) {
        String deleteCmd = "rm -r " + file.toString();
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(deleteCmd);
        } catch (IOException e) {
            //
        }
    }
}