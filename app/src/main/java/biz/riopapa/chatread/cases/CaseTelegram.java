package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.deBug;
import static biz.riopapa.chatread.MainActivity.getStockGroup;
import static biz.riopapa.chatread.MainActivity.ignoreString;
import static biz.riopapa.chatread.MainActivity.kvTelegram;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.readyToday;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sbnApp;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockLine;
import static biz.riopapa.chatread.MainActivity.stockTGroupIdx;
import static biz.riopapa.chatread.MainActivity.stockTGroupTbl;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.timeBegin;
import static biz.riopapa.chatread.MainActivity.timeEnd;
import static biz.riopapa.chatread.MainActivity.utils;
import static biz.riopapa.chatread.NotificationListener.isWorking;

import biz.riopapa.chatread.models.SGroup;

public class CaseTelegram {

    public void tel() {

        Runnable task = () -> {

            String stockText;
            String stockGrp ;
            String stockWho;
            if (sbnText.length() < 25)  // for better performance, with logically not true
                return;

            if (ignoreString.check(sbnApp))
                return;

            if (timeBegin == 0)
                readyToday.check();

            stockText = strUtil.text2OneLine(sbnText);
            stockGrp = sbnGroup.isEmpty() ? "NoGrp" : sbnGroup;
            stockWho = sbnWho;
            int g = getStockGroup.getIdx(stockWho, stockTGroupTbl, stockTGroupIdx);
//            if (deBug) {
//                utils.logB(stockGrp + " Dump", " ");
//                utils.logB(stockGrp + " Dump", "g="+g+" Dump start ----- " + stockGrp
//                        + " {" + stockWho + " t=" + stockText);
//            }
            if (g < 0) { // not in stock group
                String head;
                if (stockGrp.equals("NoGrp")) {
                    head = "[텔레 : " + stockWho + "]";
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

            long nowTime = System.currentTimeMillis();
            if (nowTime < timeBegin || nowTime > timeEnd)
                return;
            SGroup sGroup = sGroups.get(g);
            stockGrp = sGroup.grp;  // replace with short group

            if (stockText.contains(sGroup.skip1) ||
                    stockText.contains(sGroup.skip2))
                return;

//            if (deBug && sGroup.log) {
//                if (!kvTelegram.isDup(stockGrp + stockWho, stockText)) {
//                    String whoStr = stockWho.replaceAll("[^\\w\\s가-힣]", "");
//                    utils.logB(stockGrp + " Dump", "g="+stockGrp+" wstr="+whoStr);
//                }
//            }
            stockWho = getWho(stockGrp, stockWho, stockText);

            if (kvTelegram.isDup(stockGrp, stockText))
                return;

            if (deBug && sGroup.log) {
                utils.logB(stockGrp + " Dump", "g="+stockGrp+" w="+stockWho+" t="+stockText);
                utils.logB(stockGrp + " Dump", sGroup.grp + " / "+ sGroup.grpM);
            }
            utils.logB(stockGrp + " Dump", "whos size= "+sGroup.whos.size());
            for (int w = 0; w < sGroup.whos.size(); w++) {
                utils.logB(stockGrp + " Dump", "   "+w+") sbnw="+stockWho+" ^ "
                        + sGroup.whos.get(w).whoM);
                if (stockWho.startsWith(sGroup.whos.get(w).whoM)) {
                    stockWho = sGroup.whos.get(w).who;
                    stockLine.sayIfMatched(g, w, sGroup.whos.get(w).stocks, stockText);
                    break;
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    String getWho(String gName, String wName, String sText) {
        // revise sbnGroup, sbnWho by checking sbnWho

        String[] grpWho = wName.split(":");
        utils.logB(gName + " Dump", "cnt; "+grpWho.length+", "+grpWho[0]+", "+wName);
        if (grpWho.length == 2) {       // 0)그룹명 : 1)이름
            return grpWho[1].trim();
        } else if (grpWho.length == 3) {
            return grpWho[2].trim();
        } else {    // group name only
            int p = sText.indexOf(":");
            if (p > 0) {
                return sText.substring(0, p).trim();
            } else {
                return "$" + (sText.length() > 10 ? sText.substring(0, 10) : sText);
            }
        }
    }
}
