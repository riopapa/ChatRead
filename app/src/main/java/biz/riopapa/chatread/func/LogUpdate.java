package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.logQue;
import static biz.riopapa.chatread.MainActivity.logStock;
import static biz.riopapa.chatread.MainActivity.logWork;
import static biz.riopapa.chatread.MainActivity.sharedEditor;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogUpdate {

    final SimpleDateFormat TIME_INFO = new SimpleDateFormat("MM-dd HH:mm ", Locale.KOREA);
    public void addLog(String header, String text) {
        logQue += "\n" + TIME_INFO.format(new Date()) + header + "\n" + text+"\n";
        sharedEditor.putString("logQue", logQue);
        sharedEditor.apply();
    }

    public void addWork(String header, String text) {
        logWork += "\n" + TIME_INFO.format(new Date()) + header + "\n" + text+"\n";
        sharedEditor.putString("logWork", logWork);
        sharedEditor.apply();
    }

    public void addStock(String header, String text) {
        logStock += "\n" + TIME_INFO.format(new Date()) + header + "\n" + text+"\n";
        sharedEditor.putString("logStock", logStock);
        sharedEditor.apply();
    }

    /*
        Remove upper lines, then 3/4 is without \n
     */
    public String squeezeLog(String logStr) {
        logStr = logStr.replace("    ","")
                        .replace("\n\n","\n");
        String [] sLog = logStr.split("\n");
        int sLen = sLog.length;
        int row = sLen / 4;   // remove 1/4 front part

        while (sLog[row].length() < 6)
            row++;
        while (!StringUtils.isNumeric(""+sLog[row].charAt(0)))
            row++;

        StringBuilder sb = new StringBuilder();
        for (; row < sLen * 3/4; row++) {   // without blank line
            String s = sLog[row].trim();
            if (!s.isEmpty()) {
                if (s.length() > 50)
                    s = s.substring(0, 50) + "...";
                sb.append(s).append("\n");
            }
        }
        String s = sLog[row].trim();
        if (!StringUtils.isNumeric("" + s.charAt(0))) {
            sb.append(s).append("\n\n");
            row++;
        }

        for (; row < sLen; row++) { // with blank line
            s = sLog[row].trim();
            if (!s.isEmpty()) {
                if (StringUtils.isNumeric("" + s.charAt(0)))
                    sb.append("\n");
                sb.append("\n").append(s);
            }
        }
        return  sb.toString();
    }

}
