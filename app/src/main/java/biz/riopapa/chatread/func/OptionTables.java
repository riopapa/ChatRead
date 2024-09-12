package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.ktGroupIgnores;
import static biz.riopapa.chatread.MainActivity.ktStrRepl;
import static biz.riopapa.chatread.MainActivity.ktTxtIgnores;
import static biz.riopapa.chatread.MainActivity.ktWhoIgnores;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.smsStrRepl;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.strReplace;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.tableListFile;
import static biz.riopapa.chatread.MainActivity.utils;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import biz.riopapa.chatread.MainActivity;
import biz.riopapa.chatread.models.App;

public class OptionTables {

    public OptionTables() {

        tableListFile = new TableListFile();
        ktGroupIgnores = tableListFile.read("ktGrpIg");
        ktWhoIgnores = tableListFile.read("ktWhoIg");
        ktTxtIgnores = tableListFile.read("ktTxtIg");
        smsStrRepl = strReplace.get("smsRepl");
        ktStrRepl = strReplace.get("ktRepl");

        if (ktTxtIgnores == null) {
            sounds.beepOnce(MainActivity.soundType.ERR.ordinal());
            String s = "ktTxtIgnores is null";
            Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
            utils.logW("readAll",s);
        }
        new AppsTable().get();
        stockGetPut.get();

    }

    void readKtReplFile() {
        /*
         * 0       1         2
         * group ^ repl To ^ repl from
         * 퍼플  ^ pp1   ^ $매수 하신분들 【 매수 】
         */
        Gson gson = new Gson();
        String json = fileIO.readFile(tableFolder ,"ktRepl.xml");
        Type type = new TypeToken<List<App>>() {
        }.getType();
        ktStrRepl = gson.fromJson(json, type);
    }
}