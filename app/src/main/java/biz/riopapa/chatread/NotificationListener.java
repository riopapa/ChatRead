package biz.riopapa.chatread;

import static biz.riopapa.chatread.MainActivity.appFullNames;
import static biz.riopapa.chatread.MainActivity.appIgnores;
import static biz.riopapa.chatread.MainActivity.appNameIdx;
import static biz.riopapa.chatread.MainActivity.appTypes;
import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.caseAndroid;
import static biz.riopapa.chatread.MainActivity.caseApp;
import static biz.riopapa.chatread.MainActivity.caseKaTalk;
import static biz.riopapa.chatread.MainActivity.caseTelegram;
import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.mAudioManager;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.sbnApp;
import static biz.riopapa.chatread.MainActivity.sbnAppIdx;
import static biz.riopapa.chatread.MainActivity.sbnAppName;
import static biz.riopapa.chatread.MainActivity.sbnAppNick;
import static biz.riopapa.chatread.MainActivity.sbnAppType;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.utils;

import android.app.Notification;
import android.media.AudioManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Collections;

import biz.riopapa.chatread.common.Copy2Clipboard;
import biz.riopapa.chatread.common.Utils;

public class NotificationListener extends NotificationListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
        if (kvCommon == null)
            new AllVariables().set(this, "New NotificationListener");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (kvCommon == null)
            new AllVariables().set(this, "<< noty >>");

        if (ignoreSbn(sbn))
            return;

        switch (sbnAppType) {

            case "t":
                caseTelegram.tel();
                break;

            case "a":
                caseAndroid.roid();
                break;

            case "k":
                caseKaTalk.kaTalk();
                break;

            case "d":
                caseApp.app();
                break;

            default:

                if (kvCommon.isDup("none", sbnText))
                    return;
                sbnText = strUtil.text2OneLine(sbnText);
                sounds.speakAfterBeep("새 앱 설치됨 " + sbnText);
                sbnText = "새로운 앱이 설치됨,  group : [" + sbnGroup + "], who : [" + sbnWho +
                        "], text : " + sbnText;
                notificationBar.update(sbnAppName, sbnText, true);
                new Copy2Clipboard(sbnAppName);
                logUpdate.addLog("[ " + sbnAppName + " ]", sbnText);
                break;
        }
    }

    boolean ignoreSbn(StatusBarNotification sbn) {

        sbnAppName = sbn.getPackageName();  // to LowCase

        if (sbnAppName.isEmpty() || Collections.binarySearch(appIgnores, sbnAppName) >= 0)
            return true;
        Bundle extras = sbn.getNotification().extras;
        try {
            sbnText =extras.getCharSequence(Notification.EXTRA_TEXT, "").toString();
        } catch (Exception e) {
            utils.logW("sbnText", "sbnText Exception "+ sbnAppName +" "+sbnText);
            return true;
        }
        if (sbnText.isEmpty())
            return true;

        // get eWho //
        try {
            sbnWho = extras.getCharSequence(Notification.EXTRA_TITLE,"").toString();
        } catch (Exception e) {
            new Utils().logW("sbn WHO Error", "no SWho "+ sbnAppName +" "+sbnText);
            return true;
        }

        int idx = Collections.binarySearch(appFullNames, sbnAppName);
        if (idx >= 0) {
            sbnAppType = appTypes.get(idx);
            sbnAppIdx = appNameIdx.get(idx);
            sbnApp = apps.get(sbnAppIdx);
            sbnAppNick = sbnApp.nickName;
        } else {
            sbnAppNick = "N";
            sbnAppType = "N";
        }

        try {
            sbnGroup = extras.getString(Notification.EXTRA_SUB_TEXT);
            if (sbnGroup == null || sbnGroup.equals("null"))
                sbnGroup = "";
        } catch (Exception e) {
            sbnGroup = "";
        }
        return false;
    }

    public static boolean isWorking() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) < 6;
    }

}
