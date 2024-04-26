package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.kvKakao;
import static biz.riopapa.chatread.MainActivity.kvSMS;
import static biz.riopapa.chatread.MainActivity.kvStock;
import static biz.riopapa.chatread.MainActivity.kvTelegram;
import static biz.riopapa.chatread.MainActivity.logQue;
import static biz.riopapa.chatread.MainActivity.logStock;
import static biz.riopapa.chatread.MainActivity.logWork;
import static biz.riopapa.chatread.MainActivity.packageDirectory;
import static biz.riopapa.chatread.MainActivity.sharedEditor;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.timeBegin;
import static biz.riopapa.chatread.MainActivity.timeEnd;
import static biz.riopapa.chatread.MainActivity.toDay;
import static biz.riopapa.chatread.MainActivity.todayFolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.riopapa.chatread.models.KeyVal;

public class ReadyToday {
    public ReadyToday() {
        String nowDay = new SimpleDateFormat("yy-MM-dd", Locale.KOREA).format(new Date());
        if (toDay.equals(nowDay))
            return;
        toDay = nowDay;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 8);
        c.set(Calendar.MINUTE, 30);
        timeBegin =c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 15);
        timeEnd = c.getTimeInMillis();

        todayFolder = new File(packageDirectory, toDay);
        if (!todayFolder.exists()) {
            if (todayFolder.mkdirs())
                new Utils().deleteOldFiles();
//            String new_day = "\n" + new SimpleDateFormat("MM-dd (EEE) HH:mm ", Locale.KOREA).format(new Date())
//                    + " NEW DAY " + " **/\nNew Day" + "\n";
//            logQue += new_day;
            sharedEditor.apply();
            FileIO.writeFile(tableFolder, "logStock.txt", logStock);
            FileIO.writeFile(tableFolder, "logQue.txt", logQue);
            FileIO.writeFile(tableFolder, "logWork.txt", logWork);
            StringBuilder sb = new StringBuilder();
            sb.append("\n\nkvCommon =\n").append(kvCommon.toString());
            sb.append("\n\nkvSMS =\n").append(kvSMS.toString());
            sb.append("\n\nkvTelegram =\n").append(kvTelegram.toString());
            sb.append("\n\nkvStock =\n").append(kvStock.toString());
            sb.append("\n\nkvKakao =\n").append(kvKakao.toString());
            FileIO.writeFile(todayFolder, "keyVal.txt", sb.toString());
            kvCommon = new KeyVal();
            kvStock = new KeyVal();
            kvSMS = new KeyVal();
            kvTelegram = new KeyVal();
            kvKakao = new KeyVal();
        }

    }
}
