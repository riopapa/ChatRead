package biz.riopapa.chatread;

import static android.content.Context.MODE_PRIVATE;
import static biz.riopapa.chatread.MainActivity.caseAndroid;
import static biz.riopapa.chatread.MainActivity.caseApp;
import static biz.riopapa.chatread.MainActivity.caseKaTalk;
import static biz.riopapa.chatread.MainActivity.caseSMS;
import static biz.riopapa.chatread.MainActivity.caseTelegram;
import static biz.riopapa.chatread.MainActivity.caseTesla;
import static biz.riopapa.chatread.MainActivity.caseWork;
import static biz.riopapa.chatread.MainActivity.deBug;
import static biz.riopapa.chatread.MainActivity.downloadFolder;
import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.getStockGroup;
import static biz.riopapa.chatread.MainActivity.ignoreString;
import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.kvKakao;
import static biz.riopapa.chatread.MainActivity.kvSMS;
import static biz.riopapa.chatread.MainActivity.kvStock;
import static biz.riopapa.chatread.MainActivity.kvTelegram;
import static biz.riopapa.chatread.MainActivity.kvWork;
import static biz.riopapa.chatread.MainActivity.logQue;
import static biz.riopapa.chatread.MainActivity.logSave;
import static biz.riopapa.chatread.MainActivity.logStock;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.logWork;
import static biz.riopapa.chatread.MainActivity.log_Que;
import static biz.riopapa.chatread.MainActivity.log_Save;
import static biz.riopapa.chatread.MainActivity.log_Stock;
import static biz.riopapa.chatread.MainActivity.log_Work;
import static biz.riopapa.chatread.MainActivity.mAudioManager;
import static biz.riopapa.chatread.MainActivity.mBackgroundServiceIntent;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.msgNamoo;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.notificationService;
import static biz.riopapa.chatread.MainActivity.packageDirectory;
import static biz.riopapa.chatread.MainActivity.phoneVibrate;
import static biz.riopapa.chatread.MainActivity.prefLog;
import static biz.riopapa.chatread.MainActivity.prefLogEditor;
import static biz.riopapa.chatread.MainActivity.prefSave;
import static biz.riopapa.chatread.MainActivity.prefSaveEditor;
import static biz.riopapa.chatread.MainActivity.prefStock;
import static biz.riopapa.chatread.MainActivity.prefStockEditor;
import static biz.riopapa.chatread.MainActivity.prefWork;
import static biz.riopapa.chatread.MainActivity.prefWorkEditor;
import static biz.riopapa.chatread.MainActivity.readyToday;
import static biz.riopapa.chatread.MainActivity.sharePref;
import static biz.riopapa.chatread.MainActivity.sharedEditor;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockLine;
import static biz.riopapa.chatread.MainActivity.stockName;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.strReplace;
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

import biz.riopapa.chatread.cases.CaseAndroid;
import biz.riopapa.chatread.cases.CaseApp;
import biz.riopapa.chatread.cases.CaseKaTalk;
import biz.riopapa.chatread.cases.CaseSMS;
import biz.riopapa.chatread.cases.CaseTelegram;
import biz.riopapa.chatread.cases.CaseTesla;
import biz.riopapa.chatread.cases.CaseWork;
import biz.riopapa.chatread.func.GetStockGroup;
import biz.riopapa.chatread.func.IgnoreString;
import biz.riopapa.chatread.func.StrReplace;
import biz.riopapa.chatread.stocks.StockLine;
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

public class AllVariables {
    public void set(Context context, String msg) {
        mContext = context;
        Log.e("AllVariables", "started " + msg);

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
        kvWork = new KeyVal();
        if (utils == null) {
            utils = new Utils();
            sounds = new Sounds();
            getStockGroup = new GetStockGroup();
            gSheet = new GSheet();
            strUtil = new StrUtil();
            logUpdate = new LogUpdate();
            stockName = new StockName();
            msgNamoo = new MsgNamoo();
            ignoreString = new IgnoreString();
            readyToday  = new ReadyToday();
            utils = new Utils();

            // cases folder
            caseAndroid = new CaseAndroid();
            caseApp = new CaseApp();
            caseKaTalk = new CaseKaTalk();
            caseSMS = new CaseSMS();
            caseTelegram = new CaseTelegram();
            caseTesla = new CaseTesla();
            caseWork = new CaseWork();

            // stocks folder
            stockGetPut = new StockGetPut();
            stockLine = new StockLine();
            strReplace = new StrReplace();

            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            phoneVibrate = new PhoneVibrate();
            notificationService = new NotificationService();
            notificationBar = new NotificationBar();
            if (!isServiceRunning(mContext, notificationService.getClass().getName())) {
                mBackgroundServiceIntent = new Intent(mContext, notificationService.getClass());
//                mContext.startForegroundService(mBackgroundServiceIntent);
                mContext.startService(mBackgroundServiceIntent);
            }
        }
        readyToday.check();
        new OptionTables();
        new AppsTable().get();
        tableListFile = new TableListFile();

        if (sharePref ==  null) {
            sharePref = context.getSharedPreferences("sayText", MODE_PRIVATE);
            sharedEditor = sharePref.edit();
        }
        if (logQue.isEmpty()) {
            prefLog = context.getSharedPreferences(log_Que, MODE_PRIVATE);
            prefLogEditor = prefLog.edit();
            logQue = prefLog.getString(log_Que, fileIO.readFile(tableFolder, log_Que+".txt"));

            prefStock = context.getSharedPreferences(log_Stock, MODE_PRIVATE);
            prefStockEditor = prefStock.edit();
            logStock = prefStock.getString(log_Stock, fileIO.readFile(tableFolder, log_Stock+".txt"));

            prefSave = context.getSharedPreferences(log_Save, MODE_PRIVATE);
            prefSaveEditor = prefSave.edit();
            logSave = prefSave.getString(log_Save, fileIO.readFile(tableFolder, log_Save+".txt"));

            prefWork = context.getSharedPreferences(log_Work, MODE_PRIVATE);
            prefWorkEditor = prefWork.edit();
            logWork = prefWork.getString(log_Work, fileIO.readFile(tableFolder, log_Work+".txt"));
        }
        deBug = sharePref.getBoolean("deBug", false);
        if (deBug) {
            fileIO.writeFile(todayFolder, "zLogStock.txt", logStock);
            fileIO.writeFile(todayFolder, "zLogQue.txt", logQue);
            fileIO.writeFile(todayFolder, "zLogWork.txt", logWork);
        }
    }

    public static boolean isServiceRunning(Context context, String svcClassName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (svcClassName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

