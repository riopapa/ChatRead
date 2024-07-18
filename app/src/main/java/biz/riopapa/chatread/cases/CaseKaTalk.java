package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.getStockGroup;
import static biz.riopapa.chatread.MainActivity.ignoreString;
import static biz.riopapa.chatread.MainActivity.ktGroupIgnores;
import static biz.riopapa.chatread.MainActivity.ktNoNumbers;
import static biz.riopapa.chatread.MainActivity.ktStrRepl;
import static biz.riopapa.chatread.MainActivity.ktTxtIgnores;
import static biz.riopapa.chatread.MainActivity.ktWhoIgnores;
import static biz.riopapa.chatread.MainActivity.kvKakao;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sbnApp;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockCheck;
import static biz.riopapa.chatread.MainActivity.stockKGroupIdx;
import static biz.riopapa.chatread.MainActivity.stockKGroupTbl;
import static biz.riopapa.chatread.MainActivity.strReplace;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.timeBegin;
import static biz.riopapa.chatread.MainActivity.timeEnd;
import static biz.riopapa.chatread.NotificationListener.isWorking;

import biz.riopapa.chatread.common.IgnoreNumber;
import biz.riopapa.chatread.common.IgnoreThis;
import biz.riopapa.chatread.func.ReadyToday;

public class CaseKaTalk {
    public void check() {

        if (IgnoreThis.contains(sbnText, ktTxtIgnores))
            return;

        if (ignoreString.check(sbnApp))
            return;
        if (sbnGroup.isEmpty()) {  // no groupNames
            if (sbnWho.isEmpty())  // nothing
                return;
            if (sbnWho.charAt(sbnWho.length() - 1) == '#' ||
                    IgnoreThis.contains(sbnWho, ktWhoIgnores))
                return;
            sayKaTalkNoGroup();
        } else {
            sayKaTalkGroup();
        }
    }

    private void sayKaTalkGroup() {
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
        int g = getStockGroup.getIdx(sbnGroup, stockKGroupTbl, stockKGroupIdx);
        if (g < 0) {
            sbnText = strReplace.repl(ktStrRepl, sbnGroup, sbnText);
            notificationBar.update("카톡!" + sbnGroup + "." + sbnWho, sbnText, true);
            String head = "{카톡!" + sbnGroup + "." + sbnWho + "} ";
            logUpdate.addLog(head, sbnText);
            if (IgnoreNumber.in(ktNoNumbers, sbnGroup))
                sbnText = strUtil.removeDigit(sbnText);
            sounds.speakKakao(" 카톡 왔음 " + sbnGroup + " 의 " + sbnWho + " 님이 " +
                    strUtil.replaceKKHH(strUtil.makeEtc(sbnText, isWorking() ? 20 : 150)));
            return;
        }
        sayKaStock(g);
    }

    private void sayKaTalkNoGroup() {
        sbnText = strUtil.text2OneLine(sbnText);
        if (kvKakao.isDup(sbnWho, sbnText))
            return;

        sbnText = strReplace.repl(ktStrRepl, sbnWho, sbnText);
        notificationBar.update("카톡!" + sbnWho, sbnText, true);
        String head = "{카톡!" + sbnWho + "} ";
        logUpdate.addLog(head, sbnText);
        if (IgnoreNumber.in(ktNoNumbers, sbnWho))
            sbnText = strUtil.removeDigit(sbnText);
        sounds.speakKakao(" 카톡 왔음 " + sbnWho + " 님이 " +
                strUtil.replaceKKHH(strUtil.makeEtc(sbnText, isWorking()? 20 :150)));
    }

    private void sayKaStock(int g) {

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

        sbnText = strUtil.text2OneLine(sbnText);
        if (sbnText.contains(sGroups.get(g).skip1) || sbnText.contains(sGroups.get(g).skip2))
            return;
        for (int w = 0; w < sGroups.get(g).whos.size(); w++) {
            if (sbnWho.contains(sGroups.get(g).whos.get(w).whoM)) {
                // if stock Group then check skip keywords and then continue;
                sbnWho = sGroups.get(g).whos.get(w).who;        // replace with short who
                stockCheck.check(g, w, sGroups.get(g).whos.get(w).stocks);
                return;
            }
        }
    }
}
