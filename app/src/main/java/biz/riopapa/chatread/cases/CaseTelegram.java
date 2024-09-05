package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.deBug;
import static biz.riopapa.chatread.MainActivity.getStockGroup;
import static biz.riopapa.chatread.MainActivity.ignoreString;
import static biz.riopapa.chatread.MainActivity.kvTelegram;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.readyToday;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockCheck;
import static biz.riopapa.chatread.MainActivity.stockTGroupIdx;
import static biz.riopapa.chatread.MainActivity.stockTGroupTbl;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.timeBegin;
import static biz.riopapa.chatread.MainActivity.timeEnd;
import static biz.riopapa.chatread.MainActivity.utils;
import static biz.riopapa.chatread.NotificationListener.isWorking;

import biz.riopapa.chatread.models.SBar;
import biz.riopapa.chatread.models.SGroup;

public class CaseTelegram {

    public void telegram(SBar sb) {

        Runnable task = () -> {

            String stockText;
            String stockGrp ;
            String stockWho;

            if (sb.text.length() < 25)  // for better performance, with logically not true
                return;

            if (ignoreString.check(sb))
                return;

            if (timeBegin == 0)
                readyToday.check();

            stockText = strUtil.text2OneLine(sb.text);
            stockGrp = sb.group.isEmpty() ? "NoGrp" : sb.group;
            stockWho = sb.who;
            int g = getStockGroup.getIdx(stockWho, stockTGroupTbl, stockTGroupIdx);
            if (g < 0) {
                g = getStockGroup.getIdx(stockGrp, stockTGroupTbl, stockTGroupIdx);
                utils.logB("Dump g", "new g="+g+" Dump start ----- " + stockGrp
                        + " {" + stockWho + " t=" + stockText);
            }
//            if (deBug) {
//                utils.logB(stockGrp + " Dump", " ");
//                utils.logB(stockGrp + " Dump", "g="+g+" Dump start ----- " + stockGrp
//                        + " {" + stockWho + " t=" + stockText);
//            }
            if (g < 0) { // not in stock group
                utils.logB("Dump g", " ");
                utils.logB("Dump g", "g="+g+" Dump g < 0  ----- " + stockGrp
                        + " {" + stockWho + "} t=" + stockText);
                String head;
                if (stockGrp.equals("NoGrp")) {
                    head = "[텔레 . " + stockWho + "]";
                    notificationBar.update(head, stockText, true);
                    logUpdate.addLog(head, stockText);
                } else {
                    if (stockGrp.contains("새로운 메시지"))
                        stockGrp = "_새_";
                    head = "[텔레 <" + stockGrp + "><" + stockWho + ">]";
                    notificationBar.update(stockGrp + " | " + stockWho, stockText, true);
                    logUpdate.addLog(head, stockText);
                }
                String say = head + ", " + stockText;
                sounds.speakAfterBeep(strUtil.makeEtc(say, isWorking() ? 50 : 150));
                return;
            }

            if (stockText.length() < 35)  // for better performance, with logically not true
                return;
            long nowTime = System.currentTimeMillis();
            if (nowTime < timeBegin || nowTime > timeEnd)
                return;
            SGroup sGroup = sGroups.get(g);
            stockGrp = sGroup.grp;  // replace with short group

            if (kvTelegram.isDup(stockGrp, stockText))
                return;

            if (stockText.contains(sGroup.skip1) ||
                    stockText.contains(sGroup.skip2))
                return;

            String [] whoText = getActualWho(stockWho, stockText);
            stockWho = whoText[0];
            stockText = whoText[1];

            if (deBug && sGroup.log) {
                utils.logB(stockGrp + " Dump", "Group Case --- ");
                utils.logB(stockGrp + " Dump", "grp="+stockGrp+" who="+stockWho+" txt="+stockText);
            }
            for (int w = 0; w < sGroup.whos.size(); w++) {
//                utils.logB(stockGrp + " Dump", "   "+w+") sbnw="+stockWho+" ^ "
//                        + sGroup.whos.get(w).whoM);
                if (stockWho.startsWith(sGroup.whos.get(w).whoM)) {
                    stockWho = sGroup.whos.get(w).who;
                    stockCheck.sayIfMatched(g, w, sGroup.whos.get(w).stocks,
                            stockGrp, stockWho, stockText);
                    break;
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    String [] getActualWho(String wName, String sText) {
        // revise sb.group, sb.who by checking sb.who

        String[] grpWho = wName.split(":");
//        utils.logB(gName + " Dump", "cnt; "+grpWho.length+", "+grpWho[0]+" vs "+wName);
        if (grpWho.length == 2) {       // 0)그룹명 : 1)이름
            return new String [] {grpWho[1].trim(), sText};
        } else if (grpWho.length == 3) {
            return new String [] {grpWho[2].trim(), sText};
        } else {    // group name only
            int p = sText.indexOf(":");
            if (p > 0) {
                return new String [] {sText.substring(0, p).trim(), sText.substring(p+2)};
            } else {
                return new String[] {"$" + (sText.length() > 10 ? sText.substring(0, 10) : sText), sText};
            }
        }
    }
}
