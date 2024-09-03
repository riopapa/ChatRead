package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.getStockGroup;
import static biz.riopapa.chatread.MainActivity.hourMin;
import static biz.riopapa.chatread.MainActivity.kvSMS;
import static biz.riopapa.chatread.MainActivity.lastChar;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.smsNoNumbers;
import static biz.riopapa.chatread.MainActivity.smsStrRepl;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockLine;
import static biz.riopapa.chatread.MainActivity.stockSGroupIdx;
import static biz.riopapa.chatread.MainActivity.stockSGroupTbl;
import static biz.riopapa.chatread.MainActivity.strReplace;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.toDay;
import static biz.riopapa.chatread.MainActivity.utils;
import static biz.riopapa.chatread.NotificationListener.isWorking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import biz.riopapa.chatread.common.IgnoreNumber;

public class CaseSMS {

    public void sms() {

        /*
            no Number : smsNoNumber.txt
            str Repl : smsRepl.txt
         */

        // remove prefix, suffix special chars from sbnWho
        sbnWho = sbnWho.replaceAll("[\\u200C-\\u206F]", "");
        // if last char is # then ignore
        if (sbnWho.charAt(sbnWho.length() - 1) == '#')
            return;
        // if numbers only then ignore
        if (sbnWho.replaceAll("[0-9]","").length() < 3) {
            return;
//            if (!sbnText.contains("스마트폰 배우고"))
//                return;
        }
        // ignore if duplicated
        if (kvSMS.isDup(sbnWho, sbnText))
            return;

        sbnText = strUtil.text2OneLine(sbnText);
        if (sbnWho.contains("NH투자") && sbnText.contains("체결")) {
            saySMSTrade();
            return;
        }
        if (sbnWho.startsWith("찌라")) {
            int g = getStockGroup.getIdx(sbnGroup, stockSGroupTbl, stockSGroupIdx);
            if (g < 0) {
                saySMSNormal();
            } else {
                for (int w = 0; w < sGroups.get(g).whos.size(); w++) {
                    if (sbnWho.startsWith(sGroups.get(g).whos.get(w).whoM)) {
                        // if stock Group then check skip keywords and then continue;
                        sbnWho = sGroups.get(g).whos.get(w).who;        // replace with short who
                        utils.logB(sbnGroup, sbnWho + ">> " + sbnText);
                        stockLine.sayIfMatched(g, w, sGroups.get(g).whos.get(w).stocks, sbnText);
                        break;
                    }
                }
            }
            return;
        }
        saySMSNormal();
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
                    logUpdate.addStock("sms>NH투자", sayMsg);
                    notificationBar.update(samPam +":"+stockName, sayMsg, true);
                    String talkMsg = stockName + samPam;
                    if (isWorking())
                        talkMsg = strUtil.makeEtc(talkMsg, 20);
                    sounds.speakAfterBeep(strUtil.removeDigit(talkMsg));
                    gSheet.add2Stock(sGroup, toDay + new SimpleDateFormat(hourMin, Locale.KOREA).format(new Date()),sbnWho, samPam, stockName,
                            sbnText.replace(stockName, new StringBuffer(stockName).insert(1, ".").toString()), samPam
                    );
                }
            } catch (Exception e) {
                logUpdate.addStock("NH투자", "Exception " + sbnText + e);
            }
        } else
            saySMSNormal();
    }

    private void saySMSNormal() {
        String head = "[sms."+ sbnWho + "] ";

        sbnText = strReplace.repl(smsStrRepl, sbnWho, sbnText);
        logUpdate.addLog(head, sbnText);
        notificationBar.update(head, sbnText, true);
        if (IgnoreNumber.in(smsNoNumbers, sbnWho))
            sbnText = strUtil.removeDigit(sbnText);
        sounds.speakAfterBeep(head + strUtil.makeEtc(sbnText, isWorking()? 20: 120));
    }
}
