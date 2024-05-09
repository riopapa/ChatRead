package biz.riopapa.chatread;

import static biz.riopapa.chatread.MainActivity.aGroups;
import static biz.riopapa.chatread.MainActivity.appFullNames;
import static biz.riopapa.chatread.MainActivity.appIgnores;
import static biz.riopapa.chatread.MainActivity.appNameIdx;
import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.mAudioManager;
import static biz.riopapa.chatread.MainActivity.ktGroupIgnores;
import static biz.riopapa.chatread.MainActivity.ktNoNumbers;
import static biz.riopapa.chatread.MainActivity.ktTxtIgnores;
import static biz.riopapa.chatread.MainActivity.ktWhoIgnores;
import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.kvKakao;
import static biz.riopapa.chatread.MainActivity.kvSMS;
import static biz.riopapa.chatread.MainActivity.kvTelegram;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.longWhoNames;
import static biz.riopapa.chatread.MainActivity.msgKeyword;
import static biz.riopapa.chatread.MainActivity.msgSMS;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.sbnApp;
import static biz.riopapa.chatread.MainActivity.sbnAppIdx;
import static biz.riopapa.chatread.MainActivity.sbnAppName;
import static biz.riopapa.chatread.MainActivity.sbnAppNick;
import static biz.riopapa.chatread.MainActivity.sbnAppType;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.shortWhoNames;
import static biz.riopapa.chatread.MainActivity.smsReplFrom;
import static biz.riopapa.chatread.MainActivity.smsReplTo;
import static biz.riopapa.chatread.MainActivity.smsTxtIgnores;
import static biz.riopapa.chatread.MainActivity.smsWhoIgnores;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.teleApp;
import static biz.riopapa.chatread.MainActivity.teleAppIdx;
import static biz.riopapa.chatread.MainActivity.utils;
import static biz.riopapa.chatread.MainActivity.whoNameFrom;
import static biz.riopapa.chatread.MainActivity.whoNameTo;

import android.app.Notification;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Collections;

import biz.riopapa.chatread.common.Copy2Clipboard;
import biz.riopapa.chatread.common.IgnoreNumber;
import biz.riopapa.chatread.common.IgnoreThis;
import biz.riopapa.chatread.common.Utils;
import biz.riopapa.chatread.func.AppsTable;
import biz.riopapa.chatread.func.MsgKeyword;
import biz.riopapa.chatread.func.MsgNamoo;
import biz.riopapa.chatread.func.MsgSMS;
import biz.riopapa.chatread.models.App;

public class NotificationListener extends NotificationListenerService {
    final String SMS = "sms";
    final String KK_TALK = "kk";
    final String TESRY = "테스리";
    final String ANDROID = "an";
    final String TG = "tg";
    final String TELEGRAM = "텔레";
    final String APP = "app";   // general application

    Context ctx;
    String head;
    static long tesla_time = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        ctx = this.getApplicationContext();

        if (kvCommon == null)
            new SetVariables(this, "noty");

        if (isSbnNothing(sbn))
            return;

        switch (sbnAppType) {

            case KK_TALK:

                if (IgnoreThis.contains(sbnText, ktTxtIgnores))
                    return;
                if (sbnGroup.isEmpty()) {  // no groupNames
                    if (sbnWho.isEmpty())  // nothing
                        return;
                    if (IgnoreThis.contains(sbnWho, ktWhoIgnores))
                        return;
                    sbnText = strUtil.text2OneLine(sbnText);
                    if (kvKakao.isDup(sbnWho, sbnText))
                        return;
                    sbnText = strUtil.strShorten(sbnWho, sbnText);
                    notificationBar.update("카톡!" + sbnWho, sbnText, true);
                    head = "{카톡!" + sbnWho + "} ";
                    logUpdate.addLog(head, sbnText);
                    if (IgnoreNumber.in(ktNoNumbers, sbnWho))
                        sbnText = strUtil.removeDigit(sbnText);
                    sounds.speakKakao(" 카톡 왔음 " + sbnWho + " 님이 " +
                            strUtil.replaceKKHH(strUtil.makeEtc(sbnText, isWorking()? 20 :150)));
                } else {    // with group name
                    if (IgnoreThis.contains(sbnGroup, ktGroupIgnores))
                        return;
                    else if (!sbnWho.isEmpty() && IgnoreThis.contains(sbnWho, ktWhoIgnores))
                        return;
                    sbnText = strUtil.text2OneLine(sbnText);
                    if (kvKakao.isDup(sbnGroup, sbnText))
                        return;
//                    if (msgKeyword == null)
//                        msgKeyword = new MsgKeyword("by ka");

                    int grpIdx = Collections.binarySearch(aGroups, sbnGroup);
//                    Log.w("grpIdx check " + grpIdx, "grpIdx=" + grpIdx + " group=" + sbnGroup + " who=" + sbnWho);
                    if (grpIdx >= 0) {
                        if (sbnText.length() < 15)
                            return;
                        for (int w = 0; w < whoNameFrom.length; w++) {
                            if (sbnWho.contains(whoNameFrom[w])) {
                                sbnWho = whoNameTo[w];
                                break;
                            }
                        }
                        msgKeyword.say(sbnGroup, sbnWho, sbnText, grpIdx);
                        return;
                    }
                    sbnText = strUtil.strShorten(sbnGroup, sbnText);
                    notificationBar.update("카톡!" + sbnGroup + "." + sbnWho, sbnText, true);
                    head = "{카톡!" + sbnGroup + "." + sbnWho + "} ";
                    logUpdate.addLog(head, sbnText);
                    if (IgnoreNumber.in(ktNoNumbers, sbnGroup))
                        sbnText = strUtil.removeDigit(sbnText);
                    sounds.speakKakao(" 카톡 왔음 " + sbnGroup + " 의 " + sbnWho + " 님이 " +
                            strUtil.replaceKKHH(strUtil.makeEtc(sbnText, isWorking()? 20:150)));
                }
                break;

            case TG:
                sayTelegram();
                break;

            case APP:

//                if (kvCommon == null)
//                    Log.e("ivCommon"," is nothing");
//                Log.w("APP","nick "+sbnApp.nickName);
                if (kvCommon.isDup(sbnApp.nickName, sbnText))
                    return;
                if (sbnApp.igStr != null && hasIgnoreStr(sbnApp))
                    return;

                sbnText = strUtil.text2OneLine(sbnText);
                if (sbnApp.inform != null) {
                    for (int i = 0; i < sbnApp.inform.length; i++) {
                        if ((sbnWho).contains(sbnApp.inform[i])) {
                            sbnWho = sbnAppNick;
                            sbnText = sbnApp.talk[i];
                            break;
                        }
                        if (sbnText.contains(sbnApp.inform[i])) {
                            sbnWho = sbnAppNick;
                            sbnText = sbnApp.talk[i];
                            break;
                        }
                    }
                }
                if (sbnApp.replFrom != null) {
                    for (int i = 0; i < sbnApp.replFrom.length; i++) {
                        if ((sbnText).contains(sbnApp.replFrom[i])) {
                            sbnText = sbnText.replace(sbnApp.replFrom[i],sbnApp.replTo[i]);
                        }
                    }
                }

                if (sbnApp.nickName.equals("NH나무")) {
                    utils.logW(sbnApp.nickName,sbnText);
                    new MsgNamoo().say(strUtil.text2OneLine(sbnText));
                    break;
                }

                if (sbnAppNick.equals("팀즈") || sbnAppNick.equals("아룩")) {
                    sayWork();
                    return;
                }
                if (sbnAppNick.equals(TESRY)) {
                    sayTesla();
                    return;
                }

                if (sbnApp.say) {
                    String say = sbnAppNick + " ";
                    say += (sbnApp.grp) ? sbnGroup+" ": " ";
                    say += (sbnApp.who) ? sbnWho: "";
                    say = say + ", ";
                    say = say + ((sbnApp.num) ? sbnText : strUtil.removeDigit(sbnText));
                    sounds.speakAfterBeep(strUtil.makeEtc(say, isWorking()? 20: 200));
                }

                if (sbnApp.addWho)
                    sbnText = sbnWho + "※" + sbnText;

                head = sbnAppNick;
                head += (sbnApp.grp && !sbnGroup.isEmpty()) ? sbnGroup+".": "";
                head += (sbnApp.who)? "@" + sbnWho : "";
                if (sbnApp.log) {
                    logUpdate.addLog(head, sbnText);
                }
                notificationBar.update(head, sbnText, true);
                break;

            case ANDROID:

                if (kvCommon.isDup(ANDROID, sbnText))
                    return;
                if (hasIgnoreStr(sbnApp))
                    return;
                head = "< an > "+sbnWho;
                logUpdate.addLog(head, sbnWho+" / "+sbnText);
                break;

            case SMS:

                if (sbnWho.replaceAll(ctx.getString(R.string.regex_number_only), "").length() < 6 &&
                        !sbnText.contains("스마트폰 배우고"))
                    return;
                if (IgnoreThis.contains(sbnWho, smsWhoIgnores) || IgnoreThis.contains(sbnText, smsTxtIgnores))
                    return;
                sbnText = strUtil.text2OneLine(sbnText);
                if (kvSMS.isDup(sbnWho, sbnText))
                    return;
//                if (msgSMS == null)
//                    msgSMS = new MsgSMS();
                sbnWho = sbnWho.replaceAll("[\\u200C-\\u206F]", "");
                sbnText = sbnText.replace(ctx.getString(R.string.web_sent), "")
                        .replaceAll("[\\u200C-\\u206F]", "");
                if (smsReplFrom != null) {
                    for (int i = 0; i < smsReplFrom.length; i++)
                        sbnText = sbnText.replace(smsReplFrom[i], smsReplTo[i]);
                }
                msgSMS.say(sbnWho, strUtil.strShorten(sbnWho, sbnText));
                break;

            default:

                if (kvCommon.isDup("none", sbnText))
                    return;
                sbnText = strUtil.text2OneLine(sbnText);
                sounds.speakAfterBeep("새 앱 설치됨 " + sbnText);
                sbnText = "새로운 앱이 설치됨,  group : " + sbnGroup + ", who : " + sbnWho +
                        ", text : " + sbnText;
                notificationBar.update(sbnAppName, sbnText, true);
                new Copy2Clipboard(sbnAppName);
                logUpdate.addLog("[ " + sbnAppName + " ]", sbnText);
                break;
        }
    }

    private void sayWork() {

        utils.logW("work", "grp="+sbnGroup+", who="+sbnWho+", txt="+sbnText);
        if (sbnApp.replFrom != null) {
            for (int i = 0; i < sbnApp.replFrom.length; i++) {
                if ((sbnText).contains(sbnApp.replFrom[i]))
                    sbnText = sbnText.replace(sbnApp.replFrom[i], sbnApp.replTo[i]);
                if ((sbnWho).contains(sbnApp.replFrom[i]))
                    sbnWho = sbnWho.replace(sbnApp.replFrom[i], sbnApp.replTo[i]);
            }
        }
//        if (sbnApp.addWho) {
//            sbnText = sbnWho + "※" + sbnText;
//            sbnWho = "";
//        }

        head = sbnAppNick + "." + sbnWho;
        logUpdate.addWork(head, sbnText);
        notificationBar.update(head, sbnText, true);
        String say = head + ", " + sbnText;
        sounds.speakAfterBeep(strUtil.makeEtc(say, 100));

    }

    private void sayTelegram() {

        if (kvTelegram.isDup("tel", sbnText))
            return;
        if (hasIgnoreStr(teleApp))
            return;
        // longWhoName, shortWhoName [],  <= teleGrp.txt
        // 텔데봇 ^ DailyBOT
        // 텔투봄 ^ 투자의 봄
        // 텔오늘 ^ 오늘의단타 공식채널
        for (int i = 0; i < longWhoNames.length; i++) {
            if (sbnWho.contains(longWhoNames[i])) { // 정확한 이름 다 찾지 않으려 contains 씀
                if (sbnText.length() < 15)
                    return;
                sbnText = strUtil.text2OneLine(sbnText);
                sbnGroup = shortWhoNames[i];
                String []grpWho = sbnWho.split(":");
                // 'Ai 데일리 봇' 은 group 12메시 있음 : who 형태임
                // '투자의 봄' 은 group 없이 who 만 존재
                if (grpWho.length > 1) {
//                    sbnGroup = grpWho[0].trim();
                    sbnWho = grpWho[1].trim();
                }

                // whoNameFrom, to [] <= whoName
                // Ai 매매비서 ^ ai
                // 투바의 봄 ^ 투봄
                for (int w = 0; w < whoNameFrom.length; w++) {
                    if (sbnWho.contains(whoNameFrom[w])) {
                        sbnWho = whoNameTo[w];          // 투봄 + 투봄
                        break;
                    }
                }

                if (sbnText.contains("종목") && sbnText.contains("매수")) {
                    utils.logW(sbnGroup, "종목,매수 " + sbnWho + " > "
                                + sbnText);
                }
//                if (msgKeyword == null)
//                    msgKeyword = new MsgKeyword("by tele");
                sbnText = strUtil.strShorten(sbnWho, sbnText);
                int grpIdx = Collections.binarySearch(aGroups, sbnGroup);
                if (grpIdx < 0)
                    utils.logE("tele", "grpIdx " + grpIdx + " err " + sbnGroup + " > " + sbnWho
                            + " > " + sbnText);
                else {
//                    utils.logW("tel " + sbnGroup, sbnWho + " : " + sbnText);
                    msgKeyword.say(sbnGroup, sbnWho, sbnText, grpIdx);
                }
                return;
            }
        }
        if (sbnGroup.contains("새로운 메시지"))
            sbnGroup = "_새_";
        head = "[텔레 " + sbnGroup + "|" + sbnWho + "]";
        logUpdate.addLog(head, sbnText);
        notificationBar.update(sbnGroup + "|" + sbnWho, sbnText, true);
        sbnText = head + ", " + sbnText;
        sounds.speakAfterBeep(strUtil.makeEtc(sbnText, isWorking()? 20:150));
    }

    private boolean hasIgnoreStr(App app) {
        for (String t: app.igStr) {
            if (sbnWho.contains(t))
                return true;
        }
        for (String t: app.igStr) {
            if (sbnText.contains(t))
                return true;
        }
        return false;
    }

    private void sayTesla() {

        if (sbnText.contains("연결됨")) {
            long nowTime = System.currentTimeMillis();
            if ((nowTime - tesla_time) > 50 * 60 * 1000)    // 50 min.
                sounds.beepOnce(MainActivity.soundType.HI_TESLA.ordinal());
            tesla_time = nowTime;
            return;
        }
        if (kvCommon.isDup(TESRY, sbnText))
            return;
        logUpdate.addLog("[ 테스리 ]", sbnText);
        notificationBar.update(sbnAppNick, sbnText, true);
        sounds.speakAfterBeep("테스리, " + sbnText);
    }

    boolean isSbnNothing(StatusBarNotification sbn) {

        sbnAppName = sbn.getPackageName();  // to LowCase
        if (sbnAppName.isEmpty())
            return true;
        Notification mNotification = sbn.getNotification();
        Bundle extras = mNotification.extras;
        // get eText //
        try {
            sbnText = ""+extras.get(Notification.EXTRA_TEXT);
            if (sbnText.isEmpty() || sbnText.equals("null"))
                return true;
        } catch (Exception e) {
            return true;
        }
        // get eWho //
        try {
            sbnWho = ""+extras.get(Notification.EXTRA_TITLE);
            if (sbnWho.equals("null"))
                sbnWho = "";
        } catch (Exception e) {
            new Utils().logW("sbn WHO Error", "no Who "+ sbnAppName +" "+sbnText);
            return true;
        }
//        if (apps == null || appIgnores == null) {
//            new AppsTable().get();
//            Log.e("reloading", "apps is null new size=" + apps.size());
//        }

        switch (sbnAppName) {

            case "android":
                sbnApp = apps.get(2);   // FIXED TO 2  0: header, 1: 1time mail
                sbnAppNick = ANDROID;
                sbnAppType = ANDROID;
                return false;

            case "com.kakao.talk":
                sbnAppNick = "카톡";
                sbnAppType = "kk";
                break;

            case "org.telegram.messenger":
                sbnAppNick = TELEGRAM;
                sbnAppType = TG;
                break;

            case "com.samsung.android.messaging":
                sbnAppNick = "문자";
                sbnAppType = "sms";
                return false;

            default:
                if (Collections.binarySearch(appIgnores, sbnAppName) >= 0)
                    return true;
                sbnAppIdx = Collections.binarySearch(appFullNames, sbnAppName);
                if (sbnAppIdx >= 0) {
                    sbnAppIdx = appNameIdx.get(sbnAppIdx);
                    sbnApp = apps.get(sbnAppIdx);
                    sbnAppNick = sbnApp.nickName;
                    sbnAppType = "app";
                } else {
                    sbnAppNick = "None";
                    sbnAppType = "None";
                    sbnAppIdx = -1;
                }
                break;
        }

        // get eGroup //
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