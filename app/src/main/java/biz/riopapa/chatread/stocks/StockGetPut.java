package biz.riopapa.chatread.stocks;

import static android.content.Context.MODE_PRIVATE;
import static biz.riopapa.chatread.MainActivity.downloadFolder;
import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockKatalkMatchIdx;
import static biz.riopapa.chatread.MainActivity.stockKatalkMatchTbl;
import static biz.riopapa.chatread.MainActivity.stockSMSMatchIdx;
import static biz.riopapa.chatread.MainActivity.stockSMSMatchTbl;
import static biz.riopapa.chatread.MainActivity.stockTeleMatchIdx;
import static biz.riopapa.chatread.MainActivity.stockTeleMatchTbl;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.todayFolder;
import static biz.riopapa.chatread.MainActivity.utils;

import android.content.SharedPreferences;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biz.riopapa.chatread.common.SnackBar;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.models.SGroup;

public class StockGetPut {

    final static String STOCK_TABLE = "StockTable";

    /* Stock table is in sdcard/download/_ChatTalk/StockTable.xml */

    public void get() {
        if (tableFolder == null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }
        Gson gson = new Gson();
        String json = fileIO.readFile(tableFolder, STOCK_TABLE + ".txt");
        Type type = new TypeToken<List<SGroup>>() {
        }.getType();
        if (json.isEmpty())
            getFromSVFile();
        else
            sGroups = gson.fromJson(json, type);
        setStockTelKaCount();
    }

    public void getFromSVFile() {
        if (tableFolder == null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }
        ArrayList<SGroup> list;
        Gson gson = new Gson();
        String json = fileIO.readFile(tableFolder, STOCK_TABLE + "SV.txt");
        if (json.isEmpty()) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<SGroup>>() {
            }.getType();
            list = gson.fromJson(json, type);
        }
        sGroups = list;
    }

    public void setStockTelKaCount() {

        Map<String, Integer> tMap = new HashMap<>();
        Map<String, Integer> kMap = new HashMap<>();
        Map<String, Integer> sMap = new HashMap<>();
        for (int g = 0; g < sGroups.size(); g++) {
            SGroup grp = sGroups.get(g);
            if (!grp.ignore) {
                switch (grp.telKa) {
                    case "t":
                        tMap.put(grp.grpM, g);
                        break;
                    case "k":
                        kMap.put(grp.grpM, g);
                        break;
                    case "s":
                        sMap.put(grp.grpM, g);
                        break;
                }
            }
        }

        List<Map.Entry<String, Integer>> entrys;

        entrys = new ArrayList<>(tMap.entrySet());
        entrys.sort(new StringKeyComparator());
        stockTeleMatchTbl = new String[entrys.size()];
        stockTeleMatchIdx = new int[entrys.size()];
        for (int i = 0; i < entrys.size(); i++) {
            stockTeleMatchTbl[i] = entrys.get(i).getKey();
            stockTeleMatchIdx[i] = entrys.get(i).getValue();
        }

        entrys = new ArrayList<>(kMap.entrySet());
        entrys.sort(new StringKeyComparator());
        stockKatalkMatchTbl = new String[entrys.size()];
        stockKatalkMatchIdx = new int[entrys.size()];
        for (int i = 0; i < entrys.size(); i++) {
            stockKatalkMatchTbl[i] = entrys.get(i).getKey();
            stockKatalkMatchIdx[i] = entrys.get(i).getValue();
        }
        entrys = new ArrayList<>(sMap.entrySet());
        entrys.sort(new StringKeyComparator());
        stockSMSMatchTbl = new String[entrys.size()];
        stockSMSMatchIdx = new int[entrys.size()];
        for (int i = 0; i < entrys.size(); i++) {
            stockSMSMatchTbl[i] = entrys.get(i).getKey();
            stockSMSMatchIdx[i] = entrys.get(i).getValue();
        }

//        int telCnt = 0, kaCnt = 0, smsCnt = 0;
//        for (int g = 0; g < sGroups.size(); g++) {
//            SGroup grp = sGroups.get(g);
//            if (!grp.ignore) {
//                telCnt += grp.telKa.equals("t") ? 1 : 0;
//                kaCnt += grp.telKa.equals("k") ? 1 : 0;
//                smsCnt += grp.telKa.equals("s") ? 1 : 0;
//            }
//        }
//        stockTelGroupMatchTbl = new String[telCnt];
//        stockTelGroupMatchIdx = new int[telCnt];
//        stockKaGroupMatchTbl = new String[kaCnt];
//        stockKaGroupMatchIdx = new int[kaCnt];
//        stockSMSGroupMatchTbl = new String[smsCnt];
//        stockSMSGroupMatchIdx = new int[smsCnt];
//        int t = 0, k = 0, s = 0;
//        for (int g = 0; g < sGroups.size(); g++) {
//            if (!sGroups.get(g).ignore) {
//                switch (sGroups.get(g).telKa) {
//                    case "t":
//                        stockTelGroupMatchTbl[t] = sGroups.get(g).grpM;
//                        stockTelGroupMatchIdx[t] = g;
//                        t++;
//                        break;
//                    case "k":
//                        stockKaGroupMatchTbl[k] = sGroups.get(g).grpM;
//                        stockKaGroupMatchIdx[k] = g;
//                        k++;
//                        break;
//                    case "s":
//                        stockSMSGroupMatchTbl[s] = sGroups.get(g).grpM;
//                        stockSMSGroupMatchIdx[s] = g;
//                        k++;
//                        break;
//                }
//            }
//        }
    }

    static class StringKeyComparator implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
            return entry1.getKey().compareTo(entry2.getKey());
        }
    }

    public void put(String msg) {
        if (todayFolder == null)
            new ReadyToday();
        new SnackBar().show(STOCK_TABLE + ".json", msg);
        utils.logW("StockPut", msg);
        sortByGroup();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(sGroups);
        fileIO.writeFile(tableFolder, STOCK_TABLE + ".txt", json);
        get();
    }

    public void save(String msg) {
        if (todayFolder == null)
            new ReadyToday();
        utils.logW("StockPut", msg);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(sGroups);
        fileIO.writeFile(tableFolder, STOCK_TABLE + ".txt", json);
    }

    public void putSV(String msg) {
        new SnackBar().show(STOCK_TABLE + ".json", msg);
        utils.logW("StockPut", msg);
        sortByGroup();
        SharedPreferences shareGroup = mContext.getSharedPreferences(STOCK_TABLE, MODE_PRIVATE);
        SharedPreferences.Editor sgEdit = shareGroup.edit();
        Gson gson = new Gson();
        String json = gson.toJson(sGroups);
        sgEdit.putString(STOCK_TABLE, json);
        sgEdit.apply();
        if (todayFolder == null)
            new ReadyToday();
        Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson2.toJson(sGroups);
        fileIO.writeFile(tableFolder, STOCK_TABLE + "SV.txt", prettyJson);
    }

    void sortByGroup() {
        // 'group' asc, 'who' asc, 'matched count' desc
        sGroups.sort(Comparator.comparing(obj -> (obj.grp)));
        for (int g = 0; g < sGroups.size(); g++) {
            SGroup grp = sGroups.get(g);
            for (int w = 0; w < grp.whos.size(); w++) {
                sGroups.get(g).whos.get(w).stocks.sort(Comparator.comparing(obj -> (-obj.count)));
            }
            sGroups.get(g).whos.sort(Comparator.comparing(obj -> (obj.who)));
            sGroups.set(g, grp);
        }
        setStockTelKaCount();
    }
}
