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
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.strUtil;

import android.app.Notification;
import android.media.AudioManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.Collections;

import biz.riopapa.chatread.common.Copy2Clipboard;
import biz.riopapa.chatread.models.SBar;

public class NotificationListener extends NotificationListenerService {
    SBar sb;

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

        switch (sb.type) {

            case "t":
                caseTelegram.telegram(sb);
                break;

            case "a":
                caseAndroid.droid(sb);
                break;

            case "k":
                caseKaTalk.kaTalk(sb);
                break;

            case "d":
                caseApp.app(sb);
                break;

            default:

                if (kvCommon.isDup("none", sb.text))
                    return;
                sb.text = strUtil.text2OneLine(sb.text);
                sounds.speakAfterBeep("새 앱 설치됨 " + sb.text);
                sb.text = "새로운 앱이 설치됨,  group : [" + sb.group + "], who : [" + sb.who +
                        "], text : " + sb.text;
                notificationBar.update(sb.appName, sb.text, true);
                new Copy2Clipboard(sb.appName);
                logUpdate.addLog("[ " + sb.appName + " ]", sb.text);
                break;
        }
    }

    boolean ignoreSbn(StatusBarNotification sbn) {

        String appName = sbn.getPackageName();  // to LowCase

        if (appName.isEmpty() || Collections.binarySearch(appIgnores, appName) >= 0)
            return true;
        Bundle extras = sbn.getNotification().extras;
        String text = extras.getCharSequence(Notification.EXTRA_TEXT, "")
                .toString();
        if (text.isEmpty())
            return true;

        sb = new SBar();

        sb.who = extras.getCharSequence(Notification.EXTRA_TITLE,"")
                .toString();
        sb.text = text;

        int idx = Collections.binarySearch(appFullNames, appName);
        if (idx >= 0) {
            sb.type = appTypes.get(idx);
            sb.app = apps.get(appNameIdx.get(idx));
        } else {
            sb.type = "N";
            sb.appName = appName;
        }

        try {
            sb.group = extras.getString(Notification.EXTRA_SUB_TEXT);
            if (sb.group == null || sb.group.equals("null"))
                sb.group = "";
        } catch (Exception e) {
            sb.group = "";
        }
        return false;
    }

    public static boolean isWorking() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) < 5;
    }

}
