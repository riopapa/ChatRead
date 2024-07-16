package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.ktGroupIgnores;
import static biz.riopapa.chatread.MainActivity.ktNoNumbers;
import static biz.riopapa.chatread.MainActivity.ktStrRepl;
import static biz.riopapa.chatread.MainActivity.ktTxtIgnores;
import static biz.riopapa.chatread.MainActivity.ktWhoIgnores;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.smsNoNumbers;
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
        ktNoNumbers = tableListFile.read("ktNoNum");

        smsNoNumbers = tableListFile.read("smsNoNum");
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

//    void readStrReplFile() {
//        /*
//         * 0          1       2         3
//         * group ^ repl To ^ repl from
//         * 퍼플  ^ pp1   ^ $매수 하신분들 【 매수 】
//         */
//        class StrLong2Short {
//            final String grpName;
//            final ArrayList<String> grpLong;
//            final ArrayList<String> grpShort;
//
//            StrLong2Short(String grpName, ArrayList<String> grpLong, ArrayList<String> grpShort) {
//                this.grpName = grpName;
//                this.grpLong = grpLong;
//                this.grpShort = grpShort;
//            }
//        }
//        ArrayList<StrLong2Short> strLong2Shorts = new ArrayList<>();
//        String[] lines = tableListFile.read("strRepl");
//        String svGroup = "";
//        ArrayList<String> gLong = new ArrayList<>();
//        ArrayList<String> gShort = new ArrayList<>();
//        String prvLine = "";
//        for (String oneLine : lines) {
//            String[] ones = oneLine.split("\\^");
//            if (ones.length < 3) {
//                if (sounds == null)
//                    sounds = new Sounds();
//                sounds.beepOnce(MainActivity.soundType.ERR.ordinal());
//                String s = "StrRepl Caret missing : "+oneLine+"\nprv line : "+prvLine;
//                Toast.makeText(mContext,s,
//                        Toast.LENGTH_LONG).show();
//                utils.logW("strRead", s);
//                continue;
//            }
//            if (!svGroup.equals(ones[0])) {
//                if (!svGroup.isEmpty())
//                    strLong2Shorts.add(new StrLong2Short(svGroup, gLong, gShort));
//                svGroup = ones[0];
//                gShort = new ArrayList<>();
//                gLong = new ArrayList<>();
//            }
//            gShort.add(ones[1]);
//            gLong.add(ones[2]);
//            prvLine = oneLine;
//        }
//        if (!gLong.isEmpty())
//            strLong2Shorts.add(new StrLong2Short(svGroup, gLong, gShort));
//
//        replGroupCnt = strLong2Shorts.size();
//        replGroup = new String[replGroupCnt];
//        replLong = new String[replGroupCnt][];
//        replShort = new String[replGroupCnt][];
//
//        for (int i = 0; i < replGroupCnt; i++) {
//            StrLong2Short strLong2Short = strLong2Shorts.get(i);
//            replGroup[i] = strLong2Short.grpName;
//            String[] sLong = new String[strLong2Short.grpLong.size()];
//            String[] sShort = new String[strLong2Short.grpLong.size()];
//            for (int j = 0; j < strLong2Short.grpLong.size(); j++) {
//                sLong[j] = strLong2Short.grpLong.get(j);
//                sShort[j] = strLong2Short.grpShort.get(j);
//            }
//            replLong[i] = sLong;
//            replShort[i] = sShort;
//        }
//    }

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