package biz.riopapa.chatread;

import static biz.riopapa.chatread.MainActivity.alertStock;
import static biz.riopapa.chatread.MainActivity.alertWhoIndex;
import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.kvKakao;
import static biz.riopapa.chatread.MainActivity.kvSMS;
import static biz.riopapa.chatread.MainActivity.kvStock;
import static biz.riopapa.chatread.MainActivity.kvTelegram;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.mAudioManager;
import static biz.riopapa.chatread.MainActivity.mBackgroundServiceIntent;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.msgKeyword;
import static biz.riopapa.chatread.MainActivity.msgNamoo;
import static biz.riopapa.chatread.MainActivity.msgSMS;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.notificationService;
import static biz.riopapa.chatread.MainActivity.phoneVibrate;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockName;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.tableListFile;
import static biz.riopapa.chatread.MainActivity.gSheetUpload;
import static biz.riopapa.chatread.MainActivity.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import biz.riopapa.chatread.alerts.AlertStock;
import biz.riopapa.chatread.alerts.AlertTable;
import biz.riopapa.chatread.alerts.AlertWhoIndex;
import biz.riopapa.chatread.alerts.StockName;
import biz.riopapa.chatread.common.PhoneVibrate;
import biz.riopapa.chatread.common.Sounds;
import biz.riopapa.chatread.common.Utils;
import biz.riopapa.chatread.func.AppsTable;
import biz.riopapa.chatread.func.FileIO;
import biz.riopapa.chatread.func.LogUpdate;
import biz.riopapa.chatread.func.MsgKeyword;
import biz.riopapa.chatread.func.MsgNamoo;
import biz.riopapa.chatread.func.MsgSMS;
import biz.riopapa.chatread.func.OptionTables;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.func.StrUtil;
import biz.riopapa.chatread.func.TableListFile;
import biz.riopapa.chatread.func.GSheetUpload;
import biz.riopapa.chatread.models.KeyVal;
import biz.riopapa.chatread.notification.NotificationBar;
import biz.riopapa.chatread.notification.NotificationService;

public class SetVariables {
    public SetVariables(Context context, String msg) {
        mContext = context;
        Log.e("SetVariables", "started " + msg);

        if (fileIO == null)
            fileIO = new FileIO();
        fileIO.readyFolders();

        new ReadyToday();
        new OptionTables().readAll();
        new AppsTable().get();
        new AlertTable().get();
        alertWhoIndex = new AlertWhoIndex();
        tableListFile = new TableListFile();

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
            logUpdate = new LogUpdate(mContext);
            alertStock = new AlertStock();
            stockName = new StockName();
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            phoneVibrate = new PhoneVibrate();
            msgKeyword = new MsgKeyword(msg);
            msgSMS = new MsgSMS();
            msgNamoo = new MsgNamoo();
            notificationService = new NotificationService();
            notificationBar = new NotificationBar();
            if (!isServiceRunning(mContext, notificationService.getClass())) {
                mBackgroundServiceIntent = new Intent(mContext, notificationService.getClass());
//                mContext.startForegroundService(mBackgroundServiceIntent);
                mContext.startService(mBackgroundServiceIntent);
            }
            gSheetUpload = new GSheetUpload();
//            gSheetUpload.initSheetQue();
        }
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

