package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.getStockGroup;
import static biz.riopapa.chatread.MainActivity.hourMin;
import static biz.riopapa.chatread.MainActivity.kvSMS;
import static biz.riopapa.chatread.MainActivity.lastChar;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.smsNoNumbers;
import static biz.riopapa.chatread.MainActivity.smsStrRepl;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockCheck;
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
import biz.riopapa.chatread.models.SBar;

public class CaseSMS {

    public void sms(SBar sb) {

        /*
            no Number : smsNoNumber.txt
            str Repl : smsRepl.txt
         */

        // remove prefix, suffix special chars from sb.who
        sb.who = sb.who.replaceAll("[\\u200C-\\u206F]", "");
        // if last char is # then ignore
        if (sb.who.charAt(sb.who.length() - 1) == '#')
            return;
        // if numbers only then ignore
        if (sb.who.replaceAll("[0-9]","").length() < 3) {
            return;
//            if (!sb.text.contains("스마트폰 배우고"))
//                return;
        }
        // ignore if duplicated
        if (kvSMS.isDup(sb.who, sb.text))
            return;

        sb.text = strUtil.text2OneLine(sb.text);
        if (sb.who.contains("NH투자") && sb.text.contains("체결")) {
            saySMSTrade(sb);
            return;
        }
        if (sb.who.startsWith("찌라")) {
            int g = getStockGroup.getIdx(sb.group, stockSGroupTbl, stockSGroupIdx);
            if (g < 0) {
                saySMSNormal(sb);
            } else {
                for (int w = 0; w < sGroups.get(g).whos.size(); w++) {
                    if (sb.who.startsWith(sGroups.get(g).whos.get(w).whoM)) {
                        // if stock Group then check skip keywords and then continue;
                        sb.who = sGroups.get(g).whos.get(w).who;        // replace with short who
                        utils.logB(sb.group, sb.who + ">> " + sb.text);
                        stockCheck.sayIfMatched(g, w, sGroups.get(g).whos.get(w).stocks,
                                sb.group, sb.who, sb.text);
                        break;
                    }
                }
            }
            return;
        }
        saySMSNormal(sb);
    }
    private void saySMSTrade(SBar sb) {
        int pos = sb.text.indexOf("주문");
        if (pos > 0) {
            sb.text = sb.text.substring(0, pos);
            try {
                String[] words = sb.text.split("\\|");
                // |[NH투자]|매수 전량체결|KMH    |10주|9,870원|주문 0001026052
                //   0       1          2       3    4       5
                if (words.length < 5) {
                    logUpdate.addStock("SMS NH 증권 에러 " + words.length, sb.text);
                    sounds.speakAfterBeep(sb.text);
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
                    gSheet.add2Stock(sGroup, toDay + new SimpleDateFormat(hourMin, Locale.KOREA).format(new Date()),sb.who, samPam, stockName,
                            sb.text.replace(stockName, new StringBuffer(stockName).insert(1, ".").toString()), samPam
                    );
                }
            } catch (Exception e) {
                logUpdate.addStock("NH투자", "Exception " + sb.text + e);
            }
        } else
            saySMSNormal(sb);
    }

    private void saySMSNormal(SBar sb) {
        String head = "[sms."+ sb.who + "] ";

        sb.text = strReplace.repl(smsStrRepl, sb.who, sb.text);
        logUpdate.addLog(head, sb.text);
        notificationBar.update(head, sb.text, true);
        if (IgnoreNumber.in(smsNoNumbers, sb.who))
            sb.text = strUtil.removeDigit(sb.text);
        sounds.speakAfterBeep(head + strUtil.makeEtc(sb.text, isWorking()? 20: 120));
    }
}
