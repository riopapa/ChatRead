package biz.riopapa.chatread.stocks;

import static biz.riopapa.chatread.MainActivity.gIdx;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.nowSStock;
import static biz.riopapa.chatread.MainActivity.nowSWho;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.stockInform;
import static biz.riopapa.chatread.MainActivity.utils;
import static biz.riopapa.chatread.MainActivity.wIdx;

import java.util.ArrayList;

import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.SStock;
import biz.riopapa.chatread.models.SWho;

public class StockCheck {

    public void check(ArrayList<SStock> stocks) {

        for (int s = 0; s < stocks.size() ; s++) {
            nowSStock = stocks.get(s);
            if (sbnText.contains(nowSStock.key1) && sbnText.contains(nowSStock.key2)) {
                if (stockInform == null)
                    stockInform = new StockInform();
                stockInform.talkNlog(nowSStock);
                nowSStock.count++;
                try {
                    nowSWho.stocks.set(s, (SStock) nowSStock.clone());
                    nowSGroup.whos.set(wIdx, (SWho) nowSWho.clone());
                    sGroups.set(gIdx, (SGroup) nowSGroup.clone());
                } catch (CloneNotSupportedException e) {
                    utils.logE("StockCheck", "CloneNotSupportedException");
                }
                break;
            }
        }
    }
}