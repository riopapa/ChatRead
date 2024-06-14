package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.aAlertLineIdx;
import static biz.riopapa.chatread.MainActivity.aGSkip1;
import static biz.riopapa.chatread.MainActivity.aGSkip2;
import static biz.riopapa.chatread.MainActivity.aGSkip3;
import static biz.riopapa.chatread.MainActivity.aGroupSaid;
import static biz.riopapa.chatread.MainActivity.aGroupWhoKey1;
import static biz.riopapa.chatread.MainActivity.aGroupWhoKey2;
import static biz.riopapa.chatread.MainActivity.aGroupWhoSkip;
import static biz.riopapa.chatread.MainActivity.aGroupQuiets;
import static biz.riopapa.chatread.MainActivity.stockInform;
import static biz.riopapa.chatread.MainActivity.alertWhoIndex;
import static biz.riopapa.chatread.MainActivity.alertsAdapter;
import static biz.riopapa.chatread.MainActivity.timeBegin;
import static biz.riopapa.chatread.MainActivity.timeEnd;

import android.util.Log;

import biz.riopapa.chatread.adapters.AlertsAdapter;
import biz.riopapa.chatread.stocks.StockInform;
import biz.riopapa.chatread.models.SStock;

public class MsgKeyword {

    public MsgKeyword(String str) {
        Log.e("MsgKeyword", "new "+str);
    }

    public void say(String group, String who, String text, int grpIdx) {

        if (timeBegin == 0)
            new ReadyToday();
        long nowTime = System.currentTimeMillis();
        if (nowTime < timeBegin || nowTime > timeEnd) {
            return;
        }
        if (aGroupQuiets.get(grpIdx) || aGroupSaid[grpIdx].equals(text))
            return;
        aGroupSaid[grpIdx] = text;
        if (text.contains(aGSkip1[grpIdx]) || text.contains(aGSkip2[grpIdx]) ||
                text.contains(aGSkip3[grpIdx]))
            return;
        int gWhoIdx = alertWhoIndex.get(grpIdx, who, text);
        if (gWhoIdx == -1)
            return;

        for (int i = 0; i < aGroupWhoKey1[grpIdx][gWhoIdx].length; i++) {
            if (!text.contains(aGroupWhoKey1[grpIdx][gWhoIdx][i]))
                continue;
            if (!text.contains(aGroupWhoKey2[grpIdx][gWhoIdx][i]))
                continue;
            if (text.contains(aGroupWhoSkip[grpIdx][gWhoIdx][i]))
                continue;

            if (stockInform == null)
                stockInform = new StockInform();
            stockInform.sayNlog(group, text, aAlertLineIdx[grpIdx][gWhoIdx][i]);
            if (alertsAdapter == null)
                alertsAdapter = new AlertsAdapter();
            else {
                alertsAdapter.notifyItemChanged(aAlertLineIdx[grpIdx][gWhoIdx][i]);
            }
        }
    }
}