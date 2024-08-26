package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.appFullNames;
import static biz.riopapa.chatread.MainActivity.appIgnores;
import static biz.riopapa.chatread.MainActivity.appNameIdx;
import static biz.riopapa.chatread.MainActivity.appTypes;
import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.downloadFolder;
import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.sbnAppType;
import static biz.riopapa.chatread.MainActivity.tableFolder;

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
        String json = fileIO.readFile(tableFolder ,"appTable.txt");
        Type type = new TypeToken<List<App>>() {}.getType();
        if (json.isEmpty())
            getFromSVFile();
        else
            apps = gson.fromJson(json, type);
        makeTable();
    }

    public void getFromSVFile() {
        ArrayList<App> list;
        Gson gson = new Gson();
        String json = fileIO.readFile(tableFolder,  "appTableSV.txt");
        if (json.isEmpty()) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<App>>() {
            }.getType();
            list = gson.fromJson(json, type);
        }
        apps = list;
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
        fileIO.writeFile(tableFolder, "appTable.txt", prettyJson);
        // manual copy to appTableSv.txt is required for backup
    }

    public void putSV() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(apps);
        fileIO.writeFile(tableFolder, "appTableSV.txt", json);
    }
    public void makeTable() {
        appFullNames = new ArrayList<>();
        appNameIdx = new ArrayList<>();
        appIgnores = new ArrayList<>();
        appTypes = new ArrayList<>();

        for (int i = 0; i < apps.size(); i++) {
            App app = apps.get(i);
            if (app.nickName.equals("@")) {
                appIgnores.add(app.fullName);
            } else {
                appFullNames.add(app.fullName);
                appNameIdx.add(i);
                String appType;
                switch (app.nickName) {
                    case "텔레":
                        appType = "t";
                        break;
                    case "카톡":
                        appType = "k";
                        break;
                    case "a":
                        appType = "a";
                        break;
                    default:
                        appType = "d";
                        break;
                }
                appTypes.add(appType);
            }
        }
    }

}