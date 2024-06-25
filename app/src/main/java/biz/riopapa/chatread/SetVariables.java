package biz.riopapa.chatread;

import static android.content.Context.MODE_PRIVATE;
import static biz.riopapa.chatread.MainActivity.downloadFolder;
import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.kvKakao;
import static biz.riopapa.chatread.MainActivity.kvSMS;
import static biz.riopapa.chatread.MainActivity.kvStock;
import static biz.riopapa.chatread.MainActivity.kvTelegram;
import static biz.riopapa.chatread.MainActivity.logQue;
import static biz.riopapa.chatread.MainActivity.logSave;
import static biz.riopapa.chatread.MainActivity.logStock;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.logWork;
import static biz.riopapa.chatread.MainActivity.mAudioManager;
import static biz.riopapa.chatread.MainActivity.mBackgroundServiceIntent;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.msgNamoo;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.notificationService;
import static biz.riopapa.chatread.MainActivity.packageDirectory;
import static biz.riopapa.chatread.MainActivity.phoneVibrate;
import static biz.riopapa.chatread.MainActivity.sharePref;
import static biz.riopapa.chatread.MainActivity.sharedEditor;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockCheck;
import static biz.riopapa.chatread.MainActivity.stockName;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.tableListFile;
import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.toDay;
import static biz.riopapa.chatread.MainActivity.todayFolder;
import static biz.riopapa.chatread.MainActivity.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import biz.riopapa.chatread.stocks.StockCheck;
import biz.riopapa.chatread.stocks.StockName;
import biz.riopapa.chatread.stocks.StockGetPut;
import biz.riopapa.chatread.common.PhoneVibrate;
import biz.riopapa.chatread.common.Sounds;
import biz.riopapa.chatread.common.Utils;
import biz.riopapa.chatread.func.AppsTable;
import biz.riopapa.chatread.func.FileIO;
import biz.riopapa.chatread.func.LogUpdate;
import biz.riopapa.chatread.func.MsgNamoo;
import biz.riopapa.chatread.func.OptionTables;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.func.StrUtil;
import biz.riopapa.chatread.func.TableListFile;
import biz.riopapa.chatread.func.GSheet;
import biz.riopapa.chatread.models.KeyVal;
import biz.riopapa.chatread.notification.NotificationBar;
import biz.riopapa.chatread.notification.NotificationService;

public class SetVariables {
    public SetVariables(Context context, String msg) {
        mContext = context;
        Log.e("SetVariables", "started " + msg);
        if (sharePref ==  null) {
            sharePref = context.getSharedPreferences("sayText", MODE_PRIVATE);
            sharedEditor = sharePref.edit();
        }
        if (logQue.isEmpty()) {
            logQue = sharePref.getString("logQue", "");
            logStock = sharePref.getString("logStock", "");
            logSave = sharePref.getString("logSave", "");
            logWork = sharePref.getString("logWork", "");
        }

        if (packageDirectory == null)
            packageDirectory = new File(Environment.getExternalStorageDirectory(), "_ChatTalkLog");
        if (tableFolder ==  null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }
        if (todayFolder == null) {
            todayFolder = new File(packageDirectory, toDay);
        }

        if (fileIO == null)
            fileIO = new FileIO();
        fileIO.readyFolders();

        kvKakao = new KeyVal();
        kvTelegram = new KeyVal();
        kvCommon = new KeyVal();
        kvSMS = new KeyVal();
        kvStock = new KeyVal();
        if (utils == null) {
            utils = new Utils();
            sounds = new Sounds();
            utils = new Utils();
            strUtil = new StrUtil();
            logUpdate = new LogUpdate();
            stockName = new StockName();
            stockGetPut = new StockGetPut();
            stockCheck = new StockCheck();
            gSheet = new GSheet();

            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            phoneVibrate = new PhoneVibrate();
            msgNamoo = new MsgNamoo();
            notificationService = new NotificationService();
            notificationBar = new NotificationBar();
            if (!isServiceRunning(mContext, notificationService.getClass())) {
                mBackgroundServiceIntent = new Intent(mContext, notificationService.getClass());
//                mContext.startForegroundService(mBackgroundServiceIntent);
                mContext.startService(mBackgroundServiceIntent);
            }
        }
        new ReadyToday();
        new OptionTables();
        new AppsTable().get();
        tableListFile = new TableListFile();

    }

    boolean isServiceRunning(Context context, Class serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

