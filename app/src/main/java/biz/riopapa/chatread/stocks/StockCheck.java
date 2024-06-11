package biz.riopapa.chatread.stocks;

import static biz.riopapa.chatread.MainActivity.msgKeyword;
import static biz.riopapa.chatread.MainActivity.sIdx;
import static biz.riopapa.chatread.MainActivity.sbnText;

import java.util.ArrayList;

import biz.riopapa.chatread.models.SStock;

public class StockCheck {
    // check keywords within stock arrays
    public void check(ArrayList<SStock> stocks) {

        for (sIdx = 0; sIdx < stocks.size() ; sIdx++) {
            if (sbnText.contains(stocks.get(sIdx).key1) && sbnText.contains(stocks.get(sIdx).key2)) {
//                if (sbnText.contains(stocks.get(sIdx).key1) && sbnText.contains(stocks.get(sIdx).key2) &&
//                        !sbnText.contains(stocks.get(sIdx).skip1)) {
                msgKeyword.talk(stocks.get(sIdx));
                break;
            }
        }
    }
}