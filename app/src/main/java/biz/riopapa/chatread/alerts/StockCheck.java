package biz.riopapa.chatread.alerts;

import static android.content.Context.MODE_PRIVATE;
import static biz.riopapa.chatread.MainActivity.alerts;
import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.msgKeyword;
import static biz.riopapa.chatread.MainActivity.sIdx;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.stockGroups;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.todayFolder;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

import biz.riopapa.chatread.common.SnackBar;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.models.Alert;
import biz.riopapa.chatread.models.Stock;

public class StockCheck {
    // check keywords within stock arrays
    public void check(ArrayList<Stock> stocks) {

        for (sIdx = 0; sIdx < stocks.size() ; sIdx++) {
            if (sbnText.contains(stocks.get(sIdx).key1) && sbnText.contains(stocks.get(sIdx).key2) &&
                    !sbnText.contains(stocks.get(sIdx).skip1))
                msgKeyword.talk(stocks.get(sIdx));
        }
    }
}