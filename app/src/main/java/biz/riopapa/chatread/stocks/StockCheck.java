package biz.riopapa.chatread.stocks;

import static biz.riopapa.chatread.MainActivity.msgKeyword;
import static biz.riopapa.chatread.MainActivity.sIdx;
import static biz.riopapa.chatread.MainActivity.sbnText;

import java.util.ArrayList;

import biz.riopapa.chatread.models.SStock;

public class StockCheck {
    // check keywords within stock arrays
    public void check(ArrayList<SStock> SStocks) {

        for (sIdx = 0; sIdx < SStocks.size() ; sIdx++) {
            if (sbnText.contains(SStocks.get(sIdx).key1) && sbnText.contains(SStocks.get(sIdx).key2) &&
                    !sbnText.contains(SStocks.get(sIdx).skip1))
                msgKeyword.talk(SStocks.get(sIdx));
        }
    }
}