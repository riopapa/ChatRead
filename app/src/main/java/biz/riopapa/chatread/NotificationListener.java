package biz.riopapa.chatread;

import static biz.riopapa.chatread.MainActivity.aGroups;
import static biz.riopapa.chatread.MainActivity.appFullNames;
import static biz.riopapa.chatread.MainActivity.appIgnores;
import static biz.riopapa.chatread.MainActivity.appNameIdx;
import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.gIdx;
import static biz.riopapa.chatread.MainActivity.ktGroupIgnores;
import static biz.riopapa.chatread.MainActivity.ktNoNumbers;
import static biz.riopapa.chatread.MainActivity.ktTxtIgnores;
import static biz.riopapa.chatread.MainActivity.ktWhoIgnores;
import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.kvKakao;
import static biz.riopapa.chatread.MainActivity.kvSMS;
import static biz.riopapa.chatread.MainActivity.kvTelegram;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.mAudioManager;
import static biz.riopapa.chatread.MainActivity.msgKeyword;
import static biz.riopapa.chatread.MainActivity.msgSMS;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.nowSWho;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sbnApp;
import static biz.riopapa.chatread.MainActivity.sbnAppIdx;
import static biz.riopapa.chatread.MainActivity.sbnAppName;
import static biz.riopapa.chatread.MainActivity.sbnAppNick;
import static biz.riopapa.chatread.MainActivity.sbnAppType;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.smsReplFrom;
import static biz.riopapa.chatread.MainActivity.smsReplTo;
import static biz.riopapa.chatread.MainActivity.smsTxtIgnores;
import static biz.riopapa.chatread.MainActivity.smsWhoIgnores;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockCheck;
import static biz.riopapa.chatread.MainActivity.stockKaGroupNameIdx;
import static biz.riopapa.chatread.MainActivity.stockKaGroupNameTbl;
import static biz.riopapa.chatread.MainActivity.stockTelGroupNameIdx;
import static biz.riopapa.chatread.MainActivity.stockTelGroupNameTbl;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.teleApp;
import static biz.riopapa.chatread.MainActivity.timeBegin;
import static biz.riopapa.chatread.MainActivity.timeEnd;
import static biz.riopapa.chatread.MainActivity.utils;
import static biz.riopapa.chatread.MainActivity.wIdx;
import static biz.riopapa.chatread.MainActivity.whoNameFrom;
import static biz.riopapa.chatread.MainActivity.whoNameTo;

import android.app.Notification;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.Collections;

import biz.riopapa.chatread.common.Copy2Clipboard;
import biz.riopapa.chatread.common.IgnoreNumber;
import biz.riopapa.chatread.common.IgnoreThis;
import biz.riopapa.chatread.common.Utils;
import biz.riopapa.chatread.func.MsgNamoo;
import biz.riopapa.chatread.func.ReadyToday;
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
//                sayTelegram();
                talkTelegram();
                break;

            case APP:

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

//        utils.logW("work", "grp="+sbnGroup+", who="+sbnWho+", txt="+sbnText);
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

    private void talkTelegram() {

        if (hasIgnoreStr(teleApp))
            return;
        // longWhoName, shortWhoName [],  <= teleGrp.txt
        // 텔데봇 ^ DailyBOT
        // 텔투봄 ^ 투자의 봄
        // 텔오늘 ^ 오늘의단타 공식채널

        if (sbnText.length() < 15)  // for better performance, with logically not true
            return;

        gIdx = isStockTelGroup(sbnWho);
        if (gIdx < 0) { // not in stock group
            if (sbnGroup.contains("새로운 메시지"))
                sbnGroup = "_새_";
            head = "[텔레 <" + sbnGroup + "><" + sbnWho + ">]";
            logUpdate.addLog(head, sbnText);
            notificationBar.update(sbnGroup + "|" + sbnWho, sbnText, true);
            sbnText = head + ", " + sbnText;
            sounds.speakAfterBeep(strUtil.makeEtc(sbnText, isWorking() ? 20 : 150));
            return;
        }
//        if (sbnText.length() < 15)
//            return;
        if (timeBegin == 0)
            new ReadyToday();
        long nowTime = System.currentTimeMillis();
        if (nowTime < timeBegin || nowTime > timeEnd)
            return;
        final String [] ignores = {"항셍","나스닥","리딩","출석"};
        for (String ignore : ignores) {
            if (sbnText.contains(ignore))
                return;
        }
        sbnGroup = sGroups.get(gIdx).grp;  // replace with short group
        if (kvTelegram.isDup(sbnGroup, sbnText))
            return;
        // 'Ai 데일리 봇' 은 group 12메시 있음 : who 형태임
        // '투자의 봄' 은 group 없이 who 만 존재
        // 어떤 경우는 이름이 text 맨 앞에

        int p = sbnWho.indexOf(":");
        if (p > 0 && p < 30) {
            sbnWho = sbnWho.substring(p+1).trim();
        } else {
            p = sbnText.indexOf(":");
            if (p > 0 && p < 30) {
                sbnWho = sbnWho.substring(0, p).trim();
                sbnText = sbnText.substring(p + 1).trim();
            }
        }

        sbnText = strUtil.text2OneLine(sbnText);
        nowSGroup = sGroups.get(gIdx);
        if (sbnText.contains(nowSGroup.skip1) ||
                sbnText.contains(nowSGroup.skip2))
            return;
        for (wIdx = 0; wIdx < nowSGroup.whos.size(); wIdx++) {
            if (sbnWho.contains(nowSGroup.whos.get(wIdx).whoF)) {
                nowSWho = nowSGroup.whos.get(wIdx);
                // if stock Group then check skip keywords and then continue;
                sbnWho = nowSWho.who;        // replace with short who
                sbnText = strUtil.strShorten(sbnWho, sbnText);
//                utils.logW(sbnGroup+"2", sbnWho + ">> " + sbnText);
                stockCheck.check(nowSWho.stocks);
                break;
            }
        }
    }

    private int isStockTelGroup(String sbnWho) {
        for (int i = 0; i < stockTelGroupNameTbl.length; i++) {
            if (sbnWho.contains(stockTelGroupNameTbl[i]))
                return stockTelGroupNameIdx[i];
        }
        return -1;
    }

    private int isStockKaGroup(String sbnWho) {
        for (int i = 0; i < stockKaGroupNameTbl.length; i++) {
            if (sbnWho.contains(stockKaGroupNameTbl[i]))
                return stockKaGroupNameIdx[i];
        }
        return -1;
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
            sbnText = extras.getString(Notification.EXTRA_TEXT);
            if (sbnText.isEmpty() || sbnText.equals("null"))
                return true;
        } catch (Exception e) {
            return true;
        }
        // get eWho //
        try {
            sbnWho = extras.getString(Notification.EXTRA_TITLE);
            if (sbnWho.equals("null"))
                sbnWho = "";
        } catch (Exception e) {
            new Utils().logW("sbn WHO Error", "no SWho "+ sbnAppName +" "+sbnText);
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