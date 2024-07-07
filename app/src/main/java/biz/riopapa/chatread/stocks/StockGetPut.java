package biz.riopapa.chatread.stocks;

import static android.content.Context.MODE_PRIVATE;
import static biz.riopapa.chatread.MainActivity.downloadFolder;
import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockKaGroupMatchIdx;
import static biz.riopapa.chatread.MainActivity.stockKaGroupMatchTbl;
import static biz.riopapa.chatread.MainActivity.stockSMSGroupMatchIdx;
import static biz.riopapa.chatread.MainActivity.stockSMSGroupMatchTbl;
import static biz.riopapa.chatread.MainActivity.stockTelGroupMatchIdx;
import static biz.riopapa.chatread.MainActivity.stockTelGroupMatchTbl;
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
import java.util.List;

import biz.riopapa.chatread.common.SnackBar;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.models.App;
import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.SWho;

public class StockGetPut {

    final static String STOCK_TABLE = "StockTable";

    /* Stock table is in sdcard/download/_ChatTalk/StockTable.xml */

    public void get() {
        if (tableFolder ==  null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }
        Gson gson = new Gson();
        String json = fileIO.readFile(tableFolder ,STOCK_TABLE + ".txt");
        Type type = new TypeToken<List<SGroup>>() {}.getType();
        if (json.isEmpty())
            getFromSVFile();
        else
            sGroups = gson.fromJson(json, type);
        setStockTelKaCount();
    }

    public void getFromSVFile() {
        if (tableFolder ==  null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }
        ArrayList<SGroup> list;
        Gson gson = new Gson();
        String json = fileIO.readFile(tableFolder, STOCK_TABLE +"SV.txt");
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
        int telCnt = 0, kaCnt = 0, smsCnt = 0;
        for (int g = 0; g < sGroups.size(); g++) {
            SGroup grp = sGroups.get(g);
            telCnt += grp.telKa.equals("t") ? 1 : 0;
            kaCnt += grp.telKa.equals("k") ? 1 : 0;
            smsCnt += grp.telKa.equals("s") ? 1 : 0;
        }
        stockTelGroupMatchTbl = new String[telCnt];
        stockTelGroupMatchIdx = new int[telCnt];
        stockKaGroupMatchTbl = new String[kaCnt];
        stockKaGroupMatchIdx = new int[kaCnt];
        stockSMSGroupMatchTbl = new String[smsCnt];
        stockSMSGroupMatchIdx = new int[smsCnt];
        int t = 0, k = 0, s = 0;
        for (int g = 0; g < sGroups.size(); g++) {
            switch (sGroups.get(g).telKa) {
                case "t":
                    stockTelGroupMatchTbl[t] = sGroups.get(g).grpM;
                    stockTelGroupMatchIdx[t] = g;
                    t++;
                    break;
                case "k":
                    stockKaGroupMatchTbl[k] = sGroups.get(g).grpM;
                    stockKaGroupMatchIdx[k] = g;
                    k++;
                    break;
                case "s":
                    stockSMSGroupMatchTbl[s] = sGroups.get(g).grpM;
                    stockSMSGroupMatchIdx[s] = g;
                    k++;
                    break;
            }
        }
    }

    public void put(String msg) {
        new SnackBar().show(STOCK_TABLE +".json", msg);
        utils.logW("StockPut", msg);
        sort();
        SharedPreferences shareGroup = mContext.getSharedPreferences(STOCK_TABLE, MODE_PRIVATE);
        SharedPreferences.Editor sgEdit = shareGroup.edit();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(sGroups);
        sgEdit.putString(STOCK_TABLE, json);
        sgEdit.apply();
        if (todayFolder == null)
            new ReadyToday();
        fileIO.writeFile(tableFolder, STOCK_TABLE +".txt", json);
    }

    public void putSV(String msg) {
        new SnackBar().show(STOCK_TABLE +".json", msg);
        utils.logW("StockPut", msg);
        sort();
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
        fileIO.writeFile(tableFolder, STOCK_TABLE +"SV.txt", prettyJson);
    }

    void sort() {
        // 'group' asc, 'who' asc, 'matched count' desc
        sGroups.sort(Comparator.comparing(obj -> (obj.grp)));
        for (int g = 0; g < sGroups.size(); g++) {
            SGroup grp = sGroups.get(g);
            for (int w = 0; w < grp.whos.size(); w++) {
                SWho whos = grp.whos.get(w);
                whos.stocks.sort(Comparator.comparing(obj -> (-obj.count)));
                grp.whos.set(w, whos);
            }
            sGroups.set(g, grp);
        }
        setStockTelKaCount();
    }

//    static final String del = String.copyValueOf(new char[]{(char) Byte.parseByte("7F", 16)});


}
//    public void convert() {
//        sGroups = new ArrayList<>();
//        SGroup grp = new SGroup();
//        SStock SStock = new SStock();
//        grp.whos = new ArrayList<>();
//        SWho SWho = new SWho();
//        SWho.stocks = new ArrayList<>();
//        String svG = "group", svWho = "w";
//        for (int a = 0; a < alerts.size(); a++) {
//            Alert al = alerts.get(a);
//            if (svG.equals(al.group)) {
//                if (svWho.equals(al.who)) {
//                    SStock = new SStock();
//                    SStock.key1 = al.key1;
//                    SStock.key2 = al.key2;
//                    SStock.count = al.matched;
//                    SStock.talk = "";
//                    SStock.prv = al.prev;
//                    SStock.nxt = al.next;
//                    SStock.skip1 = al.skip;
//                    SWho.stocks.add(SStock);
//                } else {
//                    if (!svWho.equals("w")) {
//                        grp.whos.add(SWho);
//                    }
//                    svWho = al.who;
//                    SWho = new SWho();
//                    SWho.who = al.who;
//                    SWho.whoF = al.who;
//                    SWho.stocks = new ArrayList<>();
//                    SStock = new SStock();
//                    SStock.key1 = al.key1;
//                    SStock.key2 = al.key2;
//                    SStock.count = al.matched;
//                    SStock.talk = "";
//                    SStock.prv = al.prev;
//                    SStock.nxt = al.next;
//                    SStock.skip1 = al.skip;
//                    SWho.stocks.add(SStock);
//                }
//            } else {
//                if (!svG.equals("group")) {
//                    grp.whos.add(SWho);
//                    sGroups.add(grp);
//                    SWho = new SWho();
//                    SWho.stocks = new ArrayList<>();
//                    grp = new SGroup();
//                    grp.telKa = 'x';
//                    grp.whos = new ArrayList<>();
//                }
//                svG = al.group;
//                svWho = "w";
//                grp = new SGroup();
//                grp.whos = new ArrayList<>();
//                grp.grp = al.group;
//                grp.grpF = al.who;
//                grp.skip1 = al.key1;
//                grp.skip2 = al.key2;
//                grp.skip3 = al.skip;
//                grp.telKa = (al.group.charAt(0) == '텔') ? 't' : 'k';
//                grp.ignore = false;
//                SWho = new SWho();
//            }
//        }
//        sGroups.add(grp);
//        put("convert");
//    }

//    void save_counts() {
//        SharedPreferences shareCount = mContext.getSharedPreferences(COUNTS_ONLY, MODE_PRIVATE);
//        SharedPreferences.Editor sc = shareCount.edit();
//        sc.clear();
//        sc.apply();
//        for (int g = 0; g < sGroups.size(); g++) {
//            SGroup grp = sGroups.get(g);
//            for (int w = 0; w < grp.whos.size(); w++) {
//                SWho SWho = grp.whos.get(w);
//                for (int s = 0; s < SWho.stocks.size(); s++) {
//                    SStock sto = SWho.stocks.get(s);
//                    String[] joins = new String[]{"m", grp.grp, SWho.who, sto.key1, sto.key2};
//                    String keyVal = String.join("~", joins);
//                    sc.putInt(keyVal, sto.count);
//                }
//            }
//        }
//        sc.apply();
//    }
//

//
//    public void applyStockCounts() {
//        for (int g = 0; g < sGroups.size(); g++) {
//            SGroup grp = sGroups.get(g);
//            for (int w = 0; w < grp.whos.size(); w++) {
//                SWho who = grp.whos.get(w);
//                for (int s = 0; s < who.stocks.size(); s++) {
//                    SStock stock = who.stocks.get(s);
//                    who.stocks.set(s, stock);
//                }
//                grp.whos.set(w, who);
//            }
//            sGroups.set(g, grp);
//        }
//    }
