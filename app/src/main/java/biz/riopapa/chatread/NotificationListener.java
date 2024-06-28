package biz.riopapa.chatread;

import static biz.riopapa.chatread.MainActivity.appFullNames;
import static biz.riopapa.chatread.MainActivity.appIgnores;
import static biz.riopapa.chatread.MainActivity.appNameIdx;
import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.kaApp;
import static biz.riopapa.chatread.MainActivity.ktGroupIgnores;
import static biz.riopapa.chatread.MainActivity.ktNoNumbers;
import static biz.riopapa.chatread.MainActivity.ktStrRepl;
import static biz.riopapa.chatread.MainActivity.ktTxtIgnores;
import static biz.riopapa.chatread.MainActivity.ktWhoIgnores;
import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.kvKakao;
import static biz.riopapa.chatread.MainActivity.kvSMS;
import static biz.riopapa.chatread.MainActivity.kvTelegram;
import static biz.riopapa.chatread.MainActivity.lastChar;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.mAudioManager;
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
import static biz.riopapa.chatread.MainActivity.smsNoNumbers;
import static biz.riopapa.chatread.MainActivity.smsReplFrom;
import static biz.riopapa.chatread.MainActivity.smsReplTo;
import static biz.riopapa.chatread.MainActivity.smsTxtIgnores;
import static biz.riopapa.chatread.MainActivity.smsWho;
import static biz.riopapa.chatread.MainActivity.smsWhoIgnores;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockCheck;
import static biz.riopapa.chatread.MainActivity.stockKaGroupMatchIdx;
import static biz.riopapa.chatread.MainActivity.stockKaGroupMatchTbl;
import static biz.riopapa.chatread.MainActivity.stockSMSGroupMatchIdx;
import static biz.riopapa.chatread.MainActivity.stockSMSGroupMatchTbl;
import static biz.riopapa.chatread.MainActivity.stockTelGroupMatchIdx;
import static biz.riopapa.chatread.MainActivity.stockTelGroupMatchTbl;
import static biz.riopapa.chatread.MainActivity.strReplSet;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.teleApp;
import static biz.riopapa.chatread.MainActivity.timeBegin;
import static biz.riopapa.chatread.MainActivity.timeEnd;
import static biz.riopapa.chatread.MainActivity.utils;

import android.app.Notification;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

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

                if (hasIgnoreStr(kaApp))
                    return;
                if (sbnGroup.isEmpty()) {  // no groupNames
                    if (sbnWho.isEmpty())  // nothing
                        return;
                    if (sbnWho.charAt(sbnWho.length() - 1) == '#' ||
                        IgnoreThis.contains(sbnWho, ktWhoIgnores))
                        return;
                    sbnText = strUtil.text2OneLine(sbnText);
                    if (kvKakao.isDup(sbnWho, sbnText))
                        return;

                    sbnText = strReplSet.repl(ktStrRepl, "grp", sbnText);
                    notificationBar.update("카톡!" + sbnWho, sbnText, true);
                    head = "{카톡!" + sbnWho + "} ";
                    logUpdate.addLog(head, sbnText);
                    if (IgnoreNumber.in(ktNoNumbers, sbnWho))
                        sbnText = strUtil.removeDigit(sbnText);
                    sounds.speakKakao(" 카톡 왔음 " + sbnWho + " 님이 " +
                            strUtil.replaceKKHH(strUtil.makeEtc(sbnText, isWorking()? 20 :150)));
                } else {    // with group name
                    if (sbnGroup.charAt(sbnGroup.length() - 1) == '#' ||
                        IgnoreThis.contains(sbnGroup, ktGroupIgnores))
                        return;
                    else if (sbnWho.isEmpty() ||
                        sbnWho.charAt(sbnWho.length() - 1) == '#' ||
                        IgnoreThis.contains(sbnWho, ktWhoIgnores))
                        return;
                    sbnText = strUtil.text2OneLine(sbnText);
                    if (kvKakao.isDup(sbnGroup, sbnText))
                        return;
                    int g = isStockKaGroup(sbnGroup);
                    if (g < 0) {
                        sbnText = strReplSet.repl(ktStrRepl, sbnGroup, sbnText);
                        notificationBar.update("카톡!" + sbnGroup + "." + sbnWho, sbnText, true);
                        head = "{카톡!" + sbnGroup + "." + sbnWho + "} ";
                        logUpdate.addLog(head, sbnText);
                        if (IgnoreNumber.in(ktNoNumbers, sbnGroup))
                            sbnText = strUtil.removeDigit(sbnText);
                        sounds.speakKakao(" 카톡 왔음 " + sbnGroup + " 의 " + sbnWho + " 님이 " +
                                strUtil.replaceKKHH(strUtil.makeEtc(sbnText, isWorking() ? 20 : 150)));
                        return;
                    }
                    sayKaStock(g);
                    return;
                }
                break;

            case ANDROID:

                if (kvCommon.isDup(ANDROID, sbnText))
                    return;
                if (hasIgnoreStr(sbnApp))
                    return;
                head = "< an > "+sbnWho;
                logUpdate.addLog(head, sbnWho+" / "+sbnText);
                break;

            case TG:

                if (sbnText.length() < 20)  // for better performance, with logically not true
                    return;

                if (hasIgnoreStr(teleApp))
                    return;

                int g = isStockTelGroup(sbnWho);
                if (g < 0) { // not in stock group
                    if (sbnGroup.contains("새로운 메시지"))
                        sbnGroup = "_새_";
                    head = "[텔레 <" + sbnGroup + "><" + sbnWho + ">]";
                    // todo : ktStrRepl 교체 필요
                    sbnText = strReplSet.repl(ktStrRepl, sbnGroup, strUtil.text2OneLine(sbnText));
                    logUpdate.addLog(head, sbnText);
                    notificationBar.update(sbnGroup + "|" + sbnWho, sbnText, true);
                    sbnText = head + ", " + sbnText;
                    sounds.speakAfterBeep(strUtil.makeEtc(sbnText, isWorking() ? 20 : 150));
                    return;
                }
                sayTelStock(g);

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
                if (sbnApp.replF != null) {
                    for (int i = 0; i < sbnApp.replF.length; i++) {
                        if ((sbnText).contains(sbnApp.replF[i])) {
                            sbnText = sbnText.replace(sbnApp.replF[i],sbnApp.replT[i]);
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

            case SMS:

                saySMS();
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
        if (sbnApp.replF != null) {
            for (int i = 0; i < sbnApp.replF.length; i++) {
                if ((sbnText).contains(sbnApp.replF[i]))
                    sbnText = sbnText.replace(sbnApp.replF[i], sbnApp.replT[i]);
                if ((sbnWho).contains(sbnApp.replF[i]))
                    sbnWho = sbnWho.replace(sbnApp.replF[i], sbnApp.replT[i]);
            }
        }

        head = sbnAppNick + "." + sbnWho;
        logUpdate.addWork(head, sbnText);
        notificationBar.update(head, sbnText, true);
        String say = head + ", " + sbnText;
        sounds.speakAfterBeep(strUtil.makeEtc(say, 50));

    }

    private void sayKaStock(int g) {

        // longWhoName, shortWhoName [],  <= teleGrp.txt
        // 텔데봇 ^ DailyBOT
        // 텔투봄 ^ 투자의 봄
        // 텔오늘 ^ 오늘의단타 공식채널

        if (sbnText.length() < 20)  // for better performance, with logically not true
            return;
       if (timeBegin == 0)
            new ReadyToday();
        long nowTime = System.currentTimeMillis();
        if (nowTime < timeBegin || nowTime > timeEnd)
            return;
        sbnGroup = sGroups.get(g).grp;  // replace with short group
        if (kvKakao.isDup(sbnGroup, sbnText))
            return;
        // 'Ai 데일리 봇' 은 group 12메시 있음 : who 형태임
        // '투자의 봄' 은 group 없이 who 만 존재
        // 어떤 경우는 이름이 text 맨 앞에

        // {텔천하} 제왕>> 윤 종묵 님이 🔹수익 天下 🔸단타의 제왕 (王) 그룹에 사진을 보냈습니다
        // {텔천하} [🔹수익 天下 🔸단타의 제왕 (王): 수익 영의정 (正)] #청산하세요
        // {텔리치} [👑 리치플러스 R (급등일보)👑: 리치플러스] ✅ LS에코에너지 英 사업 부지 협상 돌
        // {텔소나} [소나무 투자그룹 정보방] 오영석 전문가: 선물투자가  어렵고.복잡하다고
        // {텔소나} [소나무 투자그룹 정보방] 자연 윤: 🖼 수고하셨습니당!
        // {텔투봄} [🌸투자의 봄(春)🌸] 🌸투자의 봄(春)🌸: 참여하실 분들은

        sbnText = strUtil.text2OneLine(sbnText);
        nowSGroup = sGroups.get(g);
        if (sbnText.contains(nowSGroup.skip1) || sbnText.contains(nowSGroup.skip2))
            return;
        for (int w = 0; w < nowSGroup.whos.size(); w++) {
            if (sbnWho.contains(nowSGroup.whos.get(w).whoM)) {
                nowSWho = nowSGroup.whos.get(w);
                // if stock Group then check skip keywords and then continue;
                sbnWho = nowSWho.who;        // replace with short who
//        utils.logW(sbnGroup, "["+sbnWho + "] " + ((sbnText.length() > 100)? sbnText.substring(0, 100): sbnText));
                stockCheck.check(g, w, nowSWho.stocks);
                return;
            }
        }
//        utils.logW(sbnGroup, "{["+sbnWho + "} " + sbnText);
    }

    private void sayTelStock(int g) {

        if (timeBegin == 0)
            new ReadyToday();
        long nowTime = System.currentTimeMillis();
        if (nowTime < timeBegin || nowTime > timeEnd)
            return;
        sbnGroup = sGroups.get(g).grp;  // replace with short group
        // 'Ai 데일리 봇' 은 group 12메시 있음 : who 형태임
        // '투자의 봄' 은 group 없이 who 만 존재
        // 어떤 경우는 이름이 text 맨 앞에

        // {텔천하} 제왕>> 윤 종묵 님이 🔹수익 天下 🔸단타의 제왕 (王) 그룹에 사진을 보냈습니다
        // {텔천하} [🔹수익 天下 🔸단타의 제왕 (王): 수익 영의정 (正)] #청산하세요
        // {텔리치} [👑 리치플러스 R (급등일보)👑: 리치플러스] ✅ LS에코에너지 英 사업 부지 협상 돌
        // {텔소나} [소나무 투자그룹 정보방] 오영석 전문가: 선물투자가  어렵고.복잡하다고
        // {텔소나} [소나무 투자그룹 정보방] 자연 윤: 🖼 수고하셨습니당!
        // {텔투봄} [🌸투자의 봄(春)🌸] 🌸투자의 봄(春)🌸: 참여하실 분들은

        sbnText = strUtil.text2OneLine(sbnText);
//        utils.logW(sbnGroup, "["+sbnWho + "] " + ((sbnText.length() > 100)? sbnText.substring(0, 100): sbnText));
        int p = sbnWho.indexOf(":");
        if (p > 0 && p < 30) {  // 텔소나, 텔리치
            sbnWho = sbnWho.substring(p+1).trim();
        } else {
            p = sbnText.indexOf(":");
            if (p > 0 && p < 60) {  // 텔투봄, 텔천하
                sbnWho = sbnText.substring(0, p).trim();
                sbnText = sbnText.substring(p + 1).trim();
            } else {
//                utils.logW(sbnGroup, "??" + sbnWho + "?? " + sbnText);
                return;
            }
        }
        if (kvTelegram.isDup(sbnGroup, sbnText))
            return;
        nowSGroup = sGroups.get(g);
        if (sbnText.contains(nowSGroup.skip1) ||
                sbnText.contains(nowSGroup.skip2))
            return;
        for (int w = 0; w < nowSGroup.whos.size(); w++) {
            if (sbnWho.contains(nowSGroup.whos.get(w).whoM)) {
                sbnWho = nowSGroup.whos.get(w).who;        // replace with short who
//                utils.logW(sbnGroup, sbnWho + ">> " + sbnText);
                stockCheck.check(g, w, nowSGroup.whos.get(w).stocks);
                break;
            }
        }
    }

    void saySMS() {
        if (sbnWho.replaceAll(ctx.getString(R.string.regex_number_only), "").length() < 6 &&
                !sbnText.contains("스마트폰 배우고"))
            return;
        if (sbnWho.charAt(sbnWho.length() - 1) == '#' ||
            IgnoreThis.contains(sbnWho, smsWhoIgnores) || IgnoreThis.contains(sbnText, smsTxtIgnores))
            return;
        sbnText = strUtil.text2OneLine(sbnText);
        if (kvSMS.isDup(sbnWho, sbnText))
            return;
        if (sbnWho.contains("NH투자")) {
            if (sbnText.contains("체결"))
                saySMSTrade();
            else {
                saySMSNormal();
            }
        } else if (sbnWho.contains("찌라")) {

            int g = isStockSMSGroup(sbnWho);
            if (g < 0) {
                sbnWho = sbnWho.replaceAll("[\\u200C-\\u206F]", "");
                sbnText = sbnText.replace(ctx.getString(R.string.web_sent), "")
                        .replaceAll("[\\u200C-\\u206F]", "");
                saySMSNormal();
            } else {
                nowSGroup = sGroups.get(g);
                for (int w = 0; w < nowSGroup.whos.size(); w++) {
                    if (sbnWho.contains(nowSGroup.whos.get(w).whoM)) {
                        nowSWho = nowSGroup.whos.get(w);
                        // if stock Group then check skip keywords and then continue;
                        sbnWho = nowSWho.who;        // replace with short who
                        utils.logW(sbnGroup, sbnWho + ">> " + sbnText);
                        stockCheck.check(g, w, nowSWho.stocks);
                        break;
                    }
                }
            }

        } else {
            saySMSNormal();
        }
    }

    private void saySMSTrade() {
        int pos = sbnText.indexOf("주문");
        if (pos > 0) {
            sbnText = sbnText.substring(0, pos);
            try {
                String[] words = sbnText.split("\\|");
                // |[NH투자]|매수 전량체결|KMH    |10주|9,870원|주문 0001026052
                //   0       1          2       3    4       5
                if (words.length < 5) {
                    logUpdate.addStock("SMS NH 증권 에러 " + words.length, sbnText);
                    sounds.speakAfterBeep(sbnText);
                } else {
                    String stockName = words[2].trim();  // 종목명
                    boolean buySell = words[1].contains("매수");
                    String samPam = (buySell) ?  " 샀음": " 팔림";
                    String amount = words[3];
                    String uPrice = words[4];
                    String sGroup = lastChar + "체결";
                    String sayMsg = stockName + " " + amount + " " + uPrice + samPam;
                    notificationBar.update(samPam +":"+stockName, sayMsg, true);
                    logUpdate.addStock("sms>NH투자", sayMsg);
                    gSheet.add2Stock(sGroup, sbnWho, samPam, stockName,
                            sbnText.replace(stockName, new StringBuffer(stockName).insert(1, ".").toString()), samPam,
                            new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(new Date()));
                    sayMsg = stockName + samPam;
                    if (isWorking())
                        sayMsg = strUtil.makeEtc(sayMsg, 20);
                    sounds.speakAfterBeep(strUtil.removeDigit(sayMsg));
                }
            } catch (Exception e) {
                logUpdate.addStock("NH투자", "Exception " + sbnText + e);
//                sounds.speakAfterBeep(mText);
            }
        } else
            saySMSNormal();
    }

    private void saySMSNormal() {
        String head = "[sms."+ sbnWho + "] ";
        sbnText = strUtil.strShorten("sms", sbnText);
        notificationBar.update(head, sbnText, true);
        logUpdate.addLog(head, sbnText);
        if (utils == null)
            utils = new Utils();
        for (int i = 0; i < smsWho.length; i++) {
            if (sbnWho.equals(smsWho[i])) {
                sbnText = sbnText.replace(smsReplFrom[i], smsReplTo[i]);
            }
        }
        if (IgnoreNumber.in(smsNoNumbers, sbnWho))
            sbnText = strUtil.removeDigit(sbnText);
        sounds.speakAfterBeep(head + strUtil.makeEtc(sbnText, isWorking()? 20: 120));
    }

    private int isStockTelGroup(String sbnWho) {
        for (int i = 0; i < stockTelGroupMatchTbl.length; i++) {
            if (sbnWho.contains(stockTelGroupMatchTbl[i]))
                return stockTelGroupMatchIdx[i];
        }
        return -1;
    }

    private int isStockKaGroup(String sbnWho) {
        for (int i = 0; i < stockKaGroupMatchTbl.length; i++) {
            if (sbnWho.contains(stockKaGroupMatchTbl[i]))
                return stockKaGroupMatchIdx[i];
        }
        return -1;
    }

    private int isStockSMSGroup(String sbnWho) {
        for (int i = 0; i < stockSMSGroupMatchTbl.length; i++) {
            if (sbnWho.contains(stockSMSGroupMatchTbl[i]))
                return stockSMSGroupMatchIdx[i];
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

        if (kvCommon.isDup(TESRY, sbnText))
            return;
        if (sbnText.contains("연결됨")) {
            long nowTime = System.currentTimeMillis();
            if ((nowTime - tesla_time) > 70 * 60 * 1000)    // 70 min.
                sounds.beepOnce(MainActivity.soundType.HI_TESLA.ordinal());
            tesla_time = nowTime;
            return;
        }
        logUpdate.addLog("[ 테스리 ]", sbnText);
        notificationBar.update(sbnAppNick, sbnText, true);
        sounds.speakAfterBeep("테스리, " + sbnText);
    }

    boolean isSbnNothing(StatusBarNotification sbn) {

        sbnAppName = sbn.getPackageName();  // to LowCase
        if (sbnAppName.isEmpty() || Collections.binarySearch(appIgnores, sbnAppName) >= 0)
            return true;
        Bundle extras = sbn.getNotification().extras;
        // get eText //
        try {
            sbnText = "" + extras.getString(Notification.EXTRA_TEXT,"");
        } catch (Exception e) {
            utils.logW("sbnText", "sbnText Exception "+ sbnAppName +" "+sbnText);
            return true;
        }
        if (sbnText.isEmpty())
            return true;

        // get eWho //
        try {
            sbnWho = "" + extras.getString(Notification.EXTRA_TITLE,"");
        } catch (Exception e) {
            new Utils().logW("sbn WHO Error", "no SWho "+ sbnAppName +" "+sbnText);
            return true;
        }

        switch (sbnAppName) {

            case "android":
                sbnApp = apps.get(2);   // FIXED TO 2  0: header, 1: 1time mail
                sbnAppNick = ANDROID;
                sbnAppType = ANDROID;
                return false;

            case "com.kakao.talk":
                sbnAppNick = "카톡";
                sbnAppType = KK_TALK;
                break;

            case "org.telegram.messenger":
                sbnAppNick = TELEGRAM;
                sbnAppType = TG;
                break;

            case "com.samsung.android.messaging":
                sbnAppNick = "문자";
                sbnAppType = SMS;
                return false;

            default:
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