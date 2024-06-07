package biz.riopapa.chatread.stocks;

import static biz.riopapa.chatread.MainActivity.aGSkip1;
import static biz.riopapa.chatread.MainActivity.aGSkip2;
import static biz.riopapa.chatread.MainActivity.aGSkip3;
import static biz.riopapa.chatread.MainActivity.aGroupWhos;

public class AlertWhoIndex {

    public int get(int gIdx, String iWho, String iText) {
        if (iText.contains(aGSkip1[gIdx]) || iText.contains(aGSkip2[gIdx])
           || iText.contains(aGSkip3[gIdx]))
            return -1;
        for (int i = 0; i < aGroupWhos[gIdx].length; i++) {
            if (aGroupWhos[gIdx][i].equals(iWho))
                return i;
        }
        return -1;
    }

}