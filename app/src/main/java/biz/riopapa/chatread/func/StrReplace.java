package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.downloadFolder;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.tableListFile;

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
            strRepls.add(new StrRepl(arr[0].trim(), arr[1].trim(), arr[2].trim()));
        }
        return strRepls;
    }

    public String repl(ArrayList<StrRepl> replList, String who, String txt) {
        if (txt.length() < 20)
            return txt;
        for (int i  = 0; i < replList.size(); i ++) {
            if (replList.get(i).grp.equals(who))
                txt = txt.replace(replList.get(i).strL, replList.get(i).strS);
        }
        return txt;
    }
}


