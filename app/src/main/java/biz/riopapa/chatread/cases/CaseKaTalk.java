package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.getStockGroup;
import static biz.riopapa.chatread.MainActivity.ignoreString;
import static biz.riopapa.chatread.MainActivity.ktGroupIgnores;
import static biz.riopapa.chatread.MainActivity.ktStrRepl;
import static biz.riopapa.chatread.MainActivity.ktTxtIgnores;
import static biz.riopapa.chatread.MainActivity.ktWhoIgnores;
import static biz.riopapa.chatread.MainActivity.kvKakao;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.readyToday;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockCheck;
import static biz.riopapa.chatread.MainActivity.stockKGroupIdx;
import static biz.riopapa.chatread.MainActivity.stockKGroupTbl;
import static biz.riopapa.chatread.MainActivity.strReplace;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.timeBegin;
import static biz.riopapa.chatread.MainActivity.timeEnd;
import static biz.riopapa.chatread.NotificationListener.isWorking;
import biz.riopapa.chatread.common.IgnoreThis;
import biz.riopapa.chatread.models.SBar;

public class CaseKaTalk {
    public void kaTalk(SBar sb) {

        if (IgnoreThis.contains(sb.text, ktTxtIgnores))
            return;

        if (ignoreString.check(sb))
            return;
        if (sb.group.isEmpty()) {  // no groupNames
            if (sb.who.isEmpty())  // nothing
                return;
            if (sb.who.charAt(sb.who.length() - 1) == '#' ||
                    IgnoreThis.contains(sb.who, ktWhoIgnores))
                return;
            sayKaTalkIndividual(sb);
        } else {
            sayKaTalkGroup(sb);
        }
    }

    private void sayKaTalkGroup(SBar sb) {
        if (sb.group.charAt(sb.group.length() - 1) == '#' ||
                IgnoreThis.contains(sb.group, ktGroupIgnores))
            return;
        else if (sb.who.isEmpty() ||
                sb.who.charAt(sb.who.length() - 1) == '#' ||
                IgnoreThis.contains(sb.who, ktWhoIgnores))
            return;
        if (kvKakao.isDup(sb.group, sb.text))
            return;
        sb.text = strUtil.text2OneLine(sb.text);

        int g = getStockGroup.getIdx(sb.group, stockKGroupTbl, stockKGroupIdx);
        if (g < 0)
            sayNormalGroup(sb);
        else
            sayKaStock(sb, g);
    }

    private void sayNormalGroup(SBar sb) {
        sb.text = strReplace.repl(ktStrRepl, sb.group, sb.text);
        String head = "{카톡!" + sb.group + "." + sb.who + "} ";
        logUpdate.addLog(head, sb.text);
        notificationBar.update("카톡!" + sb.group + "." + sb.who, sb.text, true);
        sounds.speakKakao(" 카톡 왔음 " + sb.group + " " + sb.who + " 님이 " +
                strUtil.replaceKKHH(strUtil.makeEtc(sb.text, isWorking() ? 20 : 150)));
    }

    private void sayKaTalkIndividual(SBar sb) {
        if (kvKakao.isDup(sb.who, sb.text))
            return;
        sb.text = strReplace.repl(ktStrRepl, sb.who, strUtil.text2OneLine(sb.text));
        String head = "{카톡!" + sb.who + "} ";
        logUpdate.addLog(head, sb.text);
        notificationBar.update("카톡!" + sb.who, sb.text, true);
        sounds.speakKakao(" 카톡 왔음 " + sb.who + " 님이 " +
                strUtil.replaceKKHH(strUtil.makeEtc(sb.text, isWorking()? 20 :150)));
    }

    private void sayKaStock(SBar sb,int g) {

        if (sb.text.length() < 20)  // for better performance, with logically not true
            return;
        readyToday.check();
        long nowTime = System.currentTimeMillis();
        if (nowTime < timeBegin || nowTime > timeEnd)
            return;
        sb.group = sGroups.get(g).grp;  // replace with short group
        if (kvKakao.isDup(sb.group, sb.text))
            return;

        sb.text = strUtil.text2OneLine(sb.text);
        if (sb.text.contains(sGroups.get(g).skip1) || sb.text.contains(sGroups.get(g).skip2))
            return;
        for (int w = 0; w < sGroups.get(g).whos.size(); w++) {
            if (sb.who.contains(sGroups.get(g).whos.get(w).whoM)) {
                // if stock Group then check skip keywords and then continue;
                sb.who = sGroups.get(g).whos.get(w).who;        // replace with short who
                stockCheck.sayIfMatched(g, w, sGroups.get(g).whos.get(w).stocks,
                        sb.group, sb.who, sb.text);
                return;
            }
        }
    }
}
