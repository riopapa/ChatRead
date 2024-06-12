package biz.riopapa.chatread.stocks;

import static android.content.Context.MODE_PRIVATE;
import static biz.riopapa.chatread.MainActivity.alerts;
import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.downloadFolder;
import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.stockCounts;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.stockKaGroupNameIdx;
import static biz.riopapa.chatread.MainActivity.stockKaGroupNameTbl;
import static biz.riopapa.chatread.MainActivity.stockTelGroupNameIdx;
import static biz.riopapa.chatread.MainActivity.stockTelGroupNameTbl;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.todayFolder;

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
import biz.riopapa.chatread.models.Alert;
import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.SWho;
import biz.riopapa.chatread.models.SStock;

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
        Type type = new TypeToken<List<SGroup>>() {
        }.getType();
        if (json.isEmpty())
            getFromFile();
        else
            sGroups = gson.fromJson(json, type);
        setStockCounts();
    }

    public void getFromFile() {
        if (tableFolder ==  null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }
        ArrayList<SGroup> list;
        Gson gson = new Gson();
        String json = fileIO.readFile(tableFolder, STOCK_TABLE +".json");
        if (json.isEmpty()) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<SGroup>>() {
            }.getType();
            list = gson.fromJson(json, type);
        }
        sGroups = list;
    }

    public void setStockCounts() {
        int idx = 0;
        stockCounts = new int[100]; // max 100 groups

        int telCnt = 0, kaCnt = 0;
        for (int g = 0; g < sGroups.size(); g++) {
            SGroup grp = sGroups.get(g);
            telCnt += (grp.telKa == 't') ? 1 : 0;
            kaCnt += (grp.telKa == 'k') ? 1 : 0;
            for (int w = 0; w < grp.whos.size(); w++) {
                SWho SWho = grp.whos.get(w);
                for (int s = 0; s < SWho.stocks.size(); s++) {
                    SStock SStock = SWho.stocks.get(s);
                    SStock.idx = idx;
                    stockCounts[idx] = SStock.count;
                    SWho.stocks.set(s, SStock);
                    idx++;
                }
                grp.whos.set(w, SWho);
            }
            sGroups.set(g, grp);
        }
        stockTelGroupNameTbl = new String[telCnt];
        stockTelGroupNameIdx = new int[telCnt];
        stockKaGroupNameTbl = new String[kaCnt];
        stockKaGroupNameIdx = new int[kaCnt];
        int t = 0, k = 0;
        for (int g = 0; g < sGroups.size(); g++) {
            if (sGroups.get(g).telKa == 't') {
                stockTelGroupNameTbl[t] = sGroups.get(g).grpF;
                stockTelGroupNameIdx[t] = g;
                t++;
            }
            if (sGroups.get(g).telKa == 'k') {
                stockKaGroupNameTbl[k] = sGroups.get(g).grpF;
                stockKaGroupNameIdx[k] = g;
                k++;
            }
        }
    }

    public void put(String msg) {
        new SnackBar().show(STOCK_TABLE +".json", msg);
        applyStockCounts();
        SharedPreferences shareGroup = mContext.getSharedPreferences(STOCK_TABLE, MODE_PRIVATE);
        SharedPreferences.Editor sg = shareGroup.edit();
        Gson gson = new Gson();
        String json = gson.toJson(sGroups);
        sg.putString(STOCK_TABLE, json);
        sg.apply();
        if (todayFolder == null)
            new ReadyToday();
        Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson2.toJson(apps);
        fileIO.writeFile(tableFolder, STOCK_TABLE +".json", prettyJson);
    }

    public void applyStockCounts() {
        for (int g = 0; g < sGroups.size(); g++) {
            SGroup grp = sGroups.get(g);
            for (int w = 0; w < grp.whos.size(); w++) {
                SWho who = grp.whos.get(w);
                for (int s = 0; s < who.stocks.size(); s++) {
                    SStock stock = who.stocks.get(s);
                    stock.count = stockCounts[stock.idx];
                    who.stocks.set(s, stock);
                }
                grp.whos.set(w, who);
            }
            sGroups.set(g, grp);
        }
    }

    void save_counts() {
        SharedPreferences shareCount = mContext.getSharedPreferences(COUNTS_ONLY, MODE_PRIVATE);
        SharedPreferences.Editor sc = shareCount.edit();
        sc.clear();
        sc.apply();
        for (int g = 0; g < sGroups.size(); g++) {
            SGroup grp = sGroups.get(g);
            for (int w = 0; w < grp.whos.size(); w++) {
                SWho SWho = grp.whos.get(w);
                for (int s = 0; s < SWho.stocks.size(); s++) {
                    SStock sto = SWho.stocks.get(s);
                    String[] joins = new String[]{"m", grp.grp, SWho.who, sto.key1, sto.key2};
                    String keyVal = String.join("~", joins);
                    sc.putInt(keyVal, sto.count);
                }
            }
        }
        sc.apply();
    }

    public void convert() {
        sGroups = new ArrayList<>();
        SGroup grp = new SGroup();
        SStock SStock = new SStock();
        grp.whos = new ArrayList<>();
        SWho SWho = new SWho();
        SWho.stocks = new ArrayList<>();
        String svG = "group", svWho = "w";
        for (int a = 0; a < alerts.size(); a++) {
            Alert al = alerts.get(a);
            if (svG.equals(al.group)) {
                if (svWho.equals(al.who)) {
                    SStock = new SStock();
                    SStock.key1 = al.key1;
                    SStock.key2 = al.key2;
                    SStock.count = al.matched;
                    SStock.talk = "";
                    SStock.prv = al.prev;
                    SStock.nxt = al.next;
                    SStock.skip1 = al.skip;
                    SWho.stocks.add(SStock);
                } else {
                    if (!svWho.equals("w")) {
                        grp.whos.add(SWho);
                    }
                    svWho = al.who;
                    SWho = new SWho();
                    SWho.who = al.who;
                    SWho.whoF = al.who;
                    SWho.stocks = new ArrayList<>();
                    SStock = new SStock();
                    SStock.key1 = al.key1;
                    SStock.key2 = al.key2;
                    SStock.count = al.matched;
                    SStock.talk = "";
                    SStock.prv = al.prev;
                    SStock.nxt = al.next;
                    SStock.skip1 = al.skip;
                    SWho.stocks.add(SStock);
                }
            } else {
                if (!svG.equals("group")) {
                    grp.whos.add(SWho);
                    sGroups.add(grp);
                    SWho = new SWho();
                    SWho.stocks = new ArrayList<>();
                    grp = new SGroup();
                    grp.telKa = 'x';
                    grp.whos = new ArrayList<>();
                }
                svG = al.group;
                svWho = "w";
                grp = new SGroup();
                grp.whos = new ArrayList<>();
                grp.grp = al.group;
                grp.grpF = al.who;
                grp.skip1 = al.key1;
                grp.skip2 = al.key2;
                grp.skip3 = al.skip;
                grp.telKa = (al.group.charAt(0) == '텔') ? 't' : 'k';
                grp.ignore = false;
                SWho = new SWho();
            }
        }
        sGroups.add(grp);
        put("convert");
    }

    public void sort() {
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
        setStockCounts();
    }

//    static final String del = String.copyValueOf(new char[]{(char) Byte.parseByte("7F", 16)});


}