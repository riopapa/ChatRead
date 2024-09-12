package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.downloadFolder;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.tableListFile;
import static biz.riopapa.chatread.MainActivity.utils;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

import biz.riopapa.chatread.models.StrRepl;

public class StrReplace {

    public ArrayList<StrRepl> get(String str_replFile) {
        if (tableFolder == null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }
        ArrayList<StrRepl> strRepls = new ArrayList<>();
        File file = new File(tableFolder, str_replFile + ".txt");
        String[] lines = tableListFile.readRaw(file);
        for (String ln : lines) {
            String[] arr = ln.split("\\^");
            if (arr.length == 4)
                strRepls.add(new StrRepl(arr[0].trim(), arr[1].trim(),
                        arr[2].trim(), arr[3].trim()));
            else {
                sounds.speakAfterBeep("String Replace Carrot Error " + ln);
                utils.logW("StrRepl", " ^ missing.. [" + arr.length + "]" + ln);
            }
        }
        return strRepls;
    }

    public String repl(ArrayList<StrRepl> replList, String who, String txt) {
        // String [0] for Log
        // String [1] for speak (digit gone?)
        if (txt.length() < 20)
            return txt;
        for (int i  = 0; i < replList.size(); i ++) {
            int compared = who.compareTo(replList.get(i).grp);
            if (compared < 0)
                return txt;
            if (compared == 0) {
                if (replList.get(i).nine.equals("0")) // "0" means remove digits
                    txt = strUtil.removeDigit(txt);
                txt = txt.replace(replList.get(i).strL, replList.get(i).strS);
            }
        }
        return txt;
    }
}


