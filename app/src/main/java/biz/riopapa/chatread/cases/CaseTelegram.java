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

public class CaseTelegram {
    public void tel() {

        if (sbnText.length() < 25)  // for better performance, with logically not true
            return;

        if (ignoreString.check(sbnApp))
            return;

        if (timeBegin == 0)
            readyToday.check();

        int g = getStockGroup.getIdx(sbnWho, stockTGroupTbl, stockTGroupIdx);
        if (g < 0) { // not in stock group
            String head;
            sbnText = strUtil.text2OneLine(sbnText);
            if (sbnGroup.isEmpty()) {
                head = "[텔레 : " + sbnWho + "]";
                notificationBar.update("탤레|" + sbnWho, sbnText, true);
                logUpdate.addLog(head, sbnText);
            } else {
                if (sbnGroup.contains("새로운 메시지"))
                    sbnGroup = "_새_";
                head = "[텔레 <" + sbnGroup + "><" + sbnWho + ">]";
                notificationBar.update(sbnGroup + " | " + sbnWho, sbnText, true);
                logUpdate.addLog(head, sbnText);
            }
            sbnText = head + ", " + sbnText;
            sounds.speakAfterBeep(strUtil.makeEtc(sbnText, isWorking() ? 50 : 150));
            return;
        }

        long nowTime = System.currentTimeMillis();
        if (nowTime < timeBegin || nowTime > timeEnd)
            return;
        sbnGroup = sGroups.get(g).grp;  // replace with short group
        sbnText = strUtil.text2OneLine(sbnText);

        if (sbnText.contains(sGroups.get(g).skip1) ||
                sbnText.contains(sGroups.get(g).skip2))
            return;

        if (deBug && sGroups.get(g).log) {
            if (!kvTelegram.isDup(sbnGroup+sbnWho, sbnText)) {
                String whoStr = sbnWho.replaceAll("[^\\w\\s가-힣]", "");
                utils.logB("w_" + sbnGroup + "_" + whoStr, sbnWho + "\n" + sbnText);
            }
        }
        String [] grpWho = sbnWho.split(":");
        if (grpWho.length == 2) {       // 0)그룹명 : 1)이름
            sbnWho = grpWho[1].trim();
        } else if (grpWho.length == 3) {
            sbnWho = grpWho[2].trim();
        } else {    // group name only
            int p = sbnText.indexOf(":");
            if (p > 0) {
                sbnWho = sbnText.substring(0, p).trim();
                sbnText = sbnText.substring(p + 1).trim();
            } else {
                sbnWho = "$" +sbnText.substring(0, 15);
            }
        }

        if (kvTelegram.isDup(sbnGroup, sbnText))
            return;

        if (deBug && sGroups.get(g).log)
            utils.logB(sbnGroup, "@"+sbnWho + "@ "+sbnText);
        for (int w = 0; w < sGroups.get(g).whos.size(); w++) {
            if (sbnWho.startsWith(sGroups.get(g).whos.get(w).whoM)) {
                sbnWho = sGroups.get(g).whos.get(w).who;
                stockLine.sayIfMatched(g, w, sGroups.get(g).whos.get(w).stocks);
                break;
            }
        }
    }

}
