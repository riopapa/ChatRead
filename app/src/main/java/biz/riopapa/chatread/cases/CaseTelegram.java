package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.deBug;
import static biz.riopapa.chatread.MainActivity.getStockGroup;
import static biz.riopapa.chatread.MainActivity.ignoreString;
import static biz.riopapa.chatread.MainActivity.kvTelegram;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sbnApp;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockCheck;
import static biz.riopapa.chatread.MainActivity.stockTGroupIdx;
import static biz.riopapa.chatread.MainActivity.stockTGroupTbl;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.timeBegin;
import static biz.riopapa.chatread.MainActivity.timeEnd;
import static biz.riopapa.chatread.MainActivity.utils;
import static biz.riopapa.chatread.NotificationListener.isWorking;

import biz.riopapa.chatread.func.ReadyToday;

public class CaseTelegram {
    public void check() {
        if (sbnText.length() < 20)  // for better performance, with logically not true
            return;

        if (ignoreString.check(sbnApp))
            return;

        int g = getStockGroup.getIdx(sbnWho, stockTGroupTbl, stockTGroupIdx);
        if (g < 0) { // not in stock group
            String head;
            sbnText = strUtil.text2OneLine(sbnText);
            if (deBug) {
                String whoStr = sbnWho.length() > 15 ? sbnWho.substring(0, 15) : sbnWho;
                whoStr = whoStr.replaceAll("[^\\w\\s가-힣]", "");
                utils.logB("who_" + whoStr, sbnWho + "\n" + sbnText);
            }
            if (sbnGroup.isEmpty()) {
                head = "[텔레 : " + sbnWho + "]";
                logUpdate.addLog(head, sbnText);
                notificationBar.update("탤레|" + sbnWho, sbnText, true);
            } else {
                if (sbnGroup.contains("새로운 메시지"))
                    sbnGroup = "_새_";
                head = "[텔레 <" + sbnGroup + "><" + sbnWho + ">]";
                logUpdate.addLog(head, sbnText);
                notificationBar.update(sbnGroup + " | " + sbnWho, sbnText, true);
            }
            sbnText = head + ", " + sbnText;
            sounds.speakAfterBeep(strUtil.makeEtc(sbnText, isWorking() ? 50 : 150));
            return;
        }

        if (timeBegin == 0)
            new ReadyToday();
        long nowTime = System.currentTimeMillis();
        if (nowTime < timeBegin || nowTime > timeEnd)
            return;
        sbnGroup = sGroups.get(g).grp;  // replace with short group
        sbnText = strUtil.text2OneLine(sbnText);

        if (sbnText.contains(sGroups.get(g).skip1) ||
                sbnText.contains(sGroups.get(g).skip2))
            return;

        String [] grpWho = sbnWho.split(":");
        if (grpWho.length == 2) {       // 그룹명 : 이름
            sbnWho = grpWho[1].trim();
        } else if (grpWho.length == 3) {  // 와룡 : 그룹 이름
            sbnWho = grpWho[2].trim();
        } else {    // group name only
            int p = sbnText.indexOf(":");
            if (p > 0) {
                sbnWho = sbnText.substring(0, p).trim();
                sbnText = sbnText.substring(p + 1).trim();
            } else
                sbnWho = sbnText.substring(0,15);
        }

        if (kvTelegram.isDup(sbnWho, sbnText))
            return;

        utils.logB(sbnGroup, sbnWho + "%% "+sbnText);

        for (int w = 0; w < sGroups.get(g).whos.size(); w++) {
            if (sbnWho.startsWith(sGroups.get(g).whos.get(w).whoM)) {
                sbnWho = sGroups.get(g).whos.get(w).who;
                stockCheck.check(g, w, sGroups.get(g).whos.get(w).stocks);
                break;
            }
        }
    }

}
