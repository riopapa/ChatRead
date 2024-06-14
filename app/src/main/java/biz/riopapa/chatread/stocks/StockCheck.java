package biz.riopapa.chatread.stocks;

import static biz.riopapa.chatread.MainActivity.gIdx;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.nowSStock;
import static biz.riopapa.chatread.MainActivity.nowSWho;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sIdx;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.stockInform;
import static biz.riopapa.chatread.MainActivity.wIdx;

import java.util.ArrayList;

import biz.riopapa.chatread.models.SStock;

public class StockCheck {

    public void check(ArrayList<SStock> stocks) {

        for (sIdx = 0; sIdx < stocks.size() ; sIdx++) {
            nowSStock = stocks.get(sIdx);
            if (sbnText.contains(nowSStock.key1) && sbnText.contains(nowSStock.key2)) {
                stockInform.talkNlog(nowSStock);
                nowSStock.count++;
                nowSWho.stocks.set(sIdx, nowSStock);
                nowSGroup.whos.set(wIdx, nowSWho);
                sGroups.set(gIdx, nowSGroup);
                if (stockInform == null)
                    stockInform = new StockInform();
                stockGetPut.put("stock Cnt");
                break;
            }
        }
    }
}