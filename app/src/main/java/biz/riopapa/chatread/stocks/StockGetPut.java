package biz.riopapa.chatread.stocks;

import static android.content.Context.MODE_PRIVATE;
import static biz.riopapa.chatread.MainActivity.alerts;
import static biz.riopapa.chatread.MainActivity.downloadFolder;
import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.stockCounts;
import static biz.riopapa.chatread.MainActivity.stockGroups;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.todayFolder;

import android.content.SharedPreferences;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import biz.riopapa.chatread.common.SnackBar;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.models.Alert;
import biz.riopapa.chatread.models.StockGroup;
import biz.riopapa.chatread.models.Stock;
import biz.riopapa.chatread.models.Who;

public class StockGetPut {

    final static String STOCK_TABLE = "StockTable";
    final static String COUNTS_ONLY = "GroupCounts";

//   group^ group name  ^     skip1  ^  skip2    ^ skip3 ^  -1  ^ skip4    ^ sayMore
//    고선 ^ 리딩방 CA ^     !!     ^     해외  ^  BTC  ^       ^    0%    ^ 개장전

//   group^  who        ^  keyword1 ^ keyword2 ^ talk ^ count ^ skip     ^ more ^ prev ^ next
//    고선 ^ 고선생       ^    매수    ^   목표가  ^      ^  101  ^          ^ 중지

//    static ArrayList<String> gSkip1 = new ArrayList<>(), gSkip2 = new ArrayList<>(),
//            gSkip3 = new ArrayList<>(), gSkip4 = new ArrayList<>();
//
//    static List<String> chkKey1 = new ArrayList<>();
//    static List<String> chkKey2 = new ArrayList<>();
//    static List<String> chkSkip = new ArrayList<>();
//    static List<String> prvKey = new ArrayList<>();
//    static List<String> nxtKey = new ArrayList<>();
//    static int gIdx, gwIdx, svIdx;

    public void get() {
        SharedPreferences shareGroup = mContext.getSharedPreferences(STOCK_TABLE, MODE_PRIVATE);
        String json = shareGroup.getString(STOCK_TABLE, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<StockGroup>>() {
        }.getType();
        if (json.isEmpty())
            getFromFile();
        else
            stockGroups = gson.fromJson(json, type);
        setStockCounts();
    }

    public void getFromFile() {
        if (tableFolder ==  null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }
        ArrayList<StockGroup> list;
        Gson gson = new Gson();
        String json = fileIO.readFile(tableFolder, STOCK_TABLE +".json");
        if (json.isEmpty()) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<StockGroup>>() {
            }.getType();
            list = gson.fromJson(json, type);
        }
        stockGroups = list;
    }

    void setStockCounts() {
        int idx = 0;
        stockCounts = new int[100];
        for (int g = 0; g < stockGroups.size(); g++) {
            StockGroup grp = stockGroups.get(g);
            for (int w = 0; w < grp.whos.size(); w++) {
                Who who = grp.whos.get(w);
                for (int s = 0; s < who.stocks.size(); s++) {
                    Stock stock = who.stocks.get(s);
                    stock.idx = idx;
                    stockCounts[idx] = stock.count;
                    who.stocks.set(s, stock);
                    idx++;
                }
                grp.whos.set(w, who);
            }
            stockGroups.set(g, grp);
        }
    }

    public void put(String msg) {
        new SnackBar().show(STOCK_TABLE +".json", msg);
        applyStockCounts();
        SharedPreferences shareGroup = mContext.getSharedPreferences(STOCK_TABLE, MODE_PRIVATE);
        SharedPreferences.Editor sg = shareGroup.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stockGroups);
        sg.putString(STOCK_TABLE, json);
        sg.apply();
        json = json.replace("{\"grp\"","\n\n{\"grp\"")
                .replace("{\"count\"","\n\n{\"count\"")
                .replace(",", ", ")
                .replace("\"next\":", "\n\"next\":")
                .replace("[{", "[\n{")
                .replace("}]}", "}\n]\n}")
                .replace("}],", "}\n],\n")
                .replace(",", ", ")
        ;
        if (todayFolder == null)
            new ReadyToday();
        fileIO.writeFile(tableFolder, STOCK_TABLE +".json", json);
    }

    void applyStockCounts() {
        for (int g = 0; g < stockGroups.size(); g++) {
            StockGroup grp = stockGroups.get(g);
            for (int w = 0; w < grp.whos.size(); w++) {
                Who who = grp.whos.get(w);
                for (int s = 0; s < who.stocks.size(); s++) {
                    Stock stock = who.stocks.get(s);
                    stock.count = stockCounts[stock.idx];
                    who.stocks.set(s, stock);
                }
                grp.whos.set(w, who);
            }
            stockGroups.set(g, grp);
        }
    }

    void save_counts() {
        SharedPreferences shareCount = mContext.getSharedPreferences(COUNTS_ONLY, MODE_PRIVATE);
        SharedPreferences.Editor sc = shareCount.edit();
        sc.clear();
        sc.apply();
        for (int g = 0; g < stockGroups.size(); g++) {
            StockGroup grp = stockGroups.get(g);
            for (int w = 0; w < grp.whos.size(); w++) {
                Who who = grp.whos.get(w);
                for (int s = 0; s < who.stocks.size(); s++) {
                    Stock sto = who.stocks.get(s);
                    String[] joins = new String[]{"m", grp.grp, who.who, sto.key1, sto.key2};
                    String keyVal = String.join("~", joins);
                    sc.putInt(keyVal, sto.count);
                }
            }
        }
        sc.apply();
    }

    public void convert() {
        stockGroups = new ArrayList<>();
        StockGroup grp = new StockGroup();
        Stock stock = new Stock();
        grp.whos = new ArrayList<>();
        Who who = new Who();
        who.stocks = new ArrayList<>();
        String svG = "group", svWho = "w";
        for (int a = 0; a < alerts.size(); a++) {
            Alert al = alerts.get(a);
            if (svG.equals(al.group)) {
                if (svWho.equals(al.who)) {
                    stock = new Stock();
                    stock.key1 = al.key1;
                    stock.key2 = al.key2;
                    stock.count = al.matched;
                    stock.talk = "";
                    stock.prv = al.prev;
                    stock.nxt = al.next;
                    stock.skip1 = al.skip;
                    who.stocks.add(stock);
                } else {
                    if (!svWho.equals("w")) {
                        grp.whos.add(who);
                    }
                    svWho = al.who;
                    who = new Who();
                    who.who = al.who;
                    who.whoF = al.who;
                    who.stocks = new ArrayList<>();
                    stock = new Stock();
                    stock.key1 = al.key1;
                    stock.key2 = al.key2;
                    stock.count = al.matched;
                    stock.talk = "";
                    stock.prv = al.prev;
                    stock.nxt = al.next;
                    stock.skip1 = al.skip;
                    who.stocks.add(stock);
                }
            } else {
                if (!svG.equals("group")) {
                    grp.whos.add(who);
                    stockGroups.add(grp);
                    who = new Who();
                    who.stocks = new ArrayList<>();
                    grp = new StockGroup();
                    grp.whos = new ArrayList<>();
                }
                svG = al.group;
                svWho = "w";
                grp = new StockGroup();
                grp.whos = new ArrayList<>();
                grp.grp = al.group;
                grp.grpF = al.who;
                grp.skip1 = al.key1;
                grp.skip2 = al.key2;
                grp.skip3 = al.skip;
                grp.telGrp = al.group.charAt(0) == '텔';
                grp.ignore = false;
                who = new Who();
            }
        }
        stockGroups.add(grp);
        put("convert");
    }

    public void makeStockTable() {


    }
//    public static void makeArrays() {
//
//        String svGroup = "x", svWho = "x";
//        int alertSize = alerts.size();
//        aGroups = new ArrayList<>();
//        aGroupQuiets = new ArrayList<>();
//
//        for (Alert al: alerts) {
//            if (!svGroup.equals(al.group)) {
//                aGroups.add(al.group);
//                aGroupQuiets.add(al.quiet);
//                String key;
//                key = al.key1; if (key.isEmpty()) key = "NotFnd"; gSkip1.add(key);
//                key = al.key2; if (key.isEmpty()) key = "NotFnd"; gSkip2.add(key);
//                key = al.talk; if (key.isEmpty()) key = "NotFnd"; gSkip3.add(key);
//                svGroup = al.group;
//            }
//        }
//        int groupCnt = aGroups.size();
//        aGSkip1 = gSkip1.toArray(new String[groupCnt]);
//        aGSkip2 = gSkip2.toArray(new String[groupCnt]);
//        aGSkip3 = gSkip3.toArray(new String[groupCnt]);
//        aGroupWhos = new String[groupCnt][];
//        aGroupWhoKey1 = new String[groupCnt][][];
//        aGroupWhoKey2 = new String[groupCnt][][];
//        aGroupWhoSkip = new String[groupCnt][][];
//        aGroupWhoPrev = new String[groupCnt][][];
//        aGroupWhoNext = new String[groupCnt][][];
//        aAlertLineIdx = new int[groupCnt][][];
//        aGroupSaid = new String[groupCnt];
//        for (int i = 0; i < groupCnt; i++)
//            aGroupSaid[i] = "x";
//
//        gIdx = 0; gwIdx = 0;
//        ArrayList<String> whoList = new ArrayList<>();
//        for (Alert al: alerts) {
//            if (al.matched == -1) {    // this means group
//                int sz = whoList.size();
//                if (sz > 0) {    // save Prev Group
//                    aGroupWhos[gIdx] = whoList.toArray(new String[0]);
//                    aGroupWhoKey1[gIdx] = new String[sz][];
//                    aGroupWhoKey2[gIdx] = new String[sz][];
//                    aGroupWhoSkip[gIdx] = new String[sz][];
//                    aGroupWhoPrev[gIdx] = new String[sz][];
//                    aGroupWhoNext[gIdx] = new String[sz][];
//                    aAlertLineIdx[gIdx] = new int[sz][];
//                    whoList = new ArrayList<>();
//                    gIdx++;
//                }
//                svWho = "x";
//            } else {
//                if (!svWho.equals(al.who))
//                    whoList.add(al.who);
//                svWho = al.who;
//            }
//        }
//
//        clearArrays();
//        gIdx = 0; gwIdx = 0;
//        svIdx = 2;
//        svWho = "x";
//        for (int i = 0; i < alertSize; i++) {
//            Alert al = alerts.get(i);
//            if (al.matched == -1) {    // this means group
//                if (!chkKey1.isEmpty()) {
//                    makeAGroupWho();
//                    svIdx = i;
//                    gIdx++;
//                }
//                svWho = "x";
//                clearArrays();
//                gwIdx = 0;
//            } else {
//                if (!svWho.equals(al.who)) {
//                    if (!chkKey1.isEmpty()) {
//                        makeAGroupWho();
//                        gwIdx++;
//                    }
//                    svIdx = i;
//                    svWho = al.who;
//                    clearArrays();
//                }
//                chkKey1.add(al.key1);
//                chkKey2.add(al.key2);
//                chkSkip.add(al.skip.isEmpty() ? "N0sKi" : al.skip);
//                prvKey.add(al.prev);
//                nxtKey.add(al.next);
//            }
//        }
//    }

//    static void makeAGroupWho() {
//        int sz = chkKey1.size();
//        aGroupWhoKey1[gIdx][gwIdx] = chkKey1.toArray(new String[sz]);
//        aGroupWhoKey2[gIdx][gwIdx] = chkKey2.toArray(new String[sz]);
//        aGroupWhoSkip[gIdx][gwIdx] = chkSkip.toArray(new String[sz]);
//        aGroupWhoPrev[gIdx][gwIdx] = prvKey.toArray(new String[sz]);
//        aGroupWhoNext[gIdx][gwIdx] = nxtKey.toArray(new String[sz]);
//        aAlertLineIdx[gIdx][gwIdx] = new int [sz];
//        for (int j = 0; j < sz; j++)
//            aAlertLineIdx[gIdx][gwIdx][j] = svIdx+j;
//    }
//
//    static void clearArrays() {
//        chkKey1 = new ArrayList<>();
//        chkKey2 = new ArrayList<>();
//        chkSkip = new ArrayList<>();
//        prvKey = new ArrayList<>();
//        nxtKey = new ArrayList<>();
//    }
    public void sort() {
        // group asc, who asc, matched desc
        stockGroups.sort(Comparator.comparing(obj -> (obj.grp)));
        for (int g = 0; g < stockGroups.size(); g++) {
            StockGroup grp = stockGroups.get(g);
            for (int w = 0; w < grp.whos.size(); w++) {
                Who who = grp.whos.get(w);
                who.stocks.sort(Comparator.comparing(obj -> (-obj.count)));
                grp.whos.set(w, who);
            }
            stockGroups.set(g, grp);
        }

    }

//    static final String del = String.copyValueOf(new char[]{(char) Byte.parseByte("7F", 16)});


}