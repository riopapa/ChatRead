package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.appFullNames;
import static biz.riopapa.chatread.MainActivity.appIgnores;
import static biz.riopapa.chatread.MainActivity.appNameIdx;
import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.downloadFolder;
import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.kaApp;
import static biz.riopapa.chatread.MainActivity.kakaoAppIdx;
import static biz.riopapa.chatread.MainActivity.smsApp;
import static biz.riopapa.chatread.MainActivity.smsAppIdx;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.teleApp;
import static biz.riopapa.chatread.MainActivity.telegramAppIdx;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import biz.riopapa.chatread.models.App;

public class AppsTable {

    /* App table is in sdcard/download/_ChatTalk/appTable.xml */

    public void get() {
        if (tableFolder ==  null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }
        Gson gson = new Gson();
        String json = fileIO.readFile(tableFolder ,"appTable.xml");
        Type type = new TypeToken<List<App>>() {
        }.getType();
        apps = gson.fromJson(json, type);
        makeTable();
    }

    public void put() {
        if (tableFolder ==  null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }

        apps.sort(Comparator.comparing(obj -> (obj.fullName)));
        makeTable();

        Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson2.toJson(apps);
        fileIO.writeFile(tableFolder, "appTable.xml", prettyJson);
    }

    public void makeTable() {
        appFullNames = new ArrayList<>();
        appNameIdx = new ArrayList<>();
        appIgnores = new ArrayList<>();

        for (int i = 0; i < apps.size(); i++) {
            App app = apps.get(i);
            if (app.nickName.equals("@")) {
                appIgnores.add(app.fullName);
            } else {
                appFullNames.add(app.fullName);
                appNameIdx.add(i);
                switch (app.fullName) {
                    case "org.telegram.messenger":
                        telegramAppIdx = i;
                        teleApp = app;
                        break;
                    case "com.kakao.talk":
                        kakaoAppIdx = i;
                        kaApp = app;
                        break;
                    case "com.samsung.android.messaging":
                        smsAppIdx = i;
                        smsApp = app;
                        break;
                }
            }
        }
    }

}