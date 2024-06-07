package biz.riopapa.chatread.stocks;

import static android.content.Context.MODE_PRIVATE;
import static biz.riopapa.chatread.MainActivity.alerts;
import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.todayFolder;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import biz.riopapa.chatread.common.SnackBar;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.models.Alert;

public class GroupSave {
    public GroupSave(String msg) {
        new SnackBar().show("Group Table", msg);
//        StockGetPut.sort();
        if (todayFolder == null)
            new ReadyToday();
        SharedPreferences sharePref = mContext.getSharedPreferences("alertLine", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharePref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alerts);
        sharedEditor.putString("alertLine", json);
        json = json.replace("},{","},\n\n{")
                .replace("\"next\":","\n\"next\":");
        fileIO.writeFile( tableFolder,"alertTable.json",json);
        AlertTable.makeArrays();
        for (int i = 0; i < alerts.size(); i++) {
            Alert al = alerts.get(i);
            if (al.matched != -1) {
                String[] joins = new String[]{"matched", al.group, al.who, al.key1, al.key2};
                String keyVal = String.join("~~", joins);
                sharedEditor.putInt(keyVal, al.matched);
            }
        }
        sharedEditor.apply();
    }
}