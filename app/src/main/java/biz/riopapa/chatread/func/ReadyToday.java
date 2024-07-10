package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.kvKakao;
import static biz.riopapa.chatread.MainActivity.kvSMS;
import static biz.riopapa.chatread.MainActivity.kvStock;
import static biz.riopapa.chatread.MainActivity.kvTelegram;
import static biz.riopapa.chatread.MainActivity.logQue;
import static biz.riopapa.chatread.MainActivity.logStock;
import static biz.riopapa.chatread.MainActivity.logWork;
import static biz.riopapa.chatread.MainActivity.packageDirectory;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
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

import biz.riopapa.chatread.common.Utils;
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
        c.set(Calendar.MINUTE, 45);
        timeBegin =c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 15);
        c.set(Calendar.MINUTE, 35);
        timeEnd = c.getTimeInMillis();

        todayFolder = new File(packageDirectory, toDay);
        if (!todayFolder.exists()) {
            if (todayFolder.mkdirs())
                new Utils().deleteOldFiles();
//            sharedEditor.apply();
            fileIO.writeFile(tableFolder, "logStock.txt", logStock);
            fileIO.writeFile(tableFolder, "logQue.txt", logQue);
            fileIO.writeFile(tableFolder, "logWork.txt", logWork);
//            if (kvCommon != null) {
//                String sb = "\nkvCommon =\n" + kvCommon +
//                        "\nkvSMS =\n" + kvSMS.toString() +
//                        "\nkvTelegram =\n" + kvTelegram.toString() +
//                        "\nkvStock =\n" + kvStock.toString() +
//                        "\nkvKakao =\n" + kvKakao.toString();
//                fileIO.writeFile(todayFolder, "keyVal.txt", sb);
//            }
        }
        kvCommon = new KeyVal();
        kvStock = new KeyVal();
        kvSMS = new KeyVal();
        kvTelegram = new KeyVal();
        kvKakao = new KeyVal();
    }
}
