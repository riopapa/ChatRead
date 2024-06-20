package biz.riopapa.chatread.stocks;

import static biz.riopapa.chatread.MainActivity.gSheetUpload;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.mMainActivity;
import static biz.riopapa.chatread.MainActivity.mAudioManager;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.phoneVibrate;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockName;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.utils;

import android.media.AudioManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import biz.riopapa.chatread.MainActivity;
import biz.riopapa.chatread.common.Copy2Clipboard;
import biz.riopapa.chatread.common.PhoneVibrate;
import biz.riopapa.chatread.common.Sounds;
import biz.riopapa.chatread.common.Utils;
import biz.riopapa.chatread.models.SStock;

public class StockInform {

    public void talkNlog(SStock stock) {

        if (utils == null) {
            utils = new Utils();
            utils.logW("sayNlog", "utils null");
        }
        if (sounds == null) {
            sounds = new Sounds();
            utils.logW("sayNlog", "sounds null");
        }

        String percent = (!sbnText.contains("매수") && (sbnText.contains("매도") || sbnText.contains("익절")))? "1.9"
                : stock.talk;
        if (stockName == null)
            stockName = new StockName();
        String [] sParse = stockName.get(stock.prv, stock.nxt, sbnText);
        String shortText = strUtil.strShorten(sbnGroup, strUtil.removeSpecialChars(sParse[1]));
        String key12 = " {" + stock.key1 + "." + stock.key2 + "}";

        if (!stock.talk.isEmpty()) {
            String [] joins;
            String won = "";
            // 매수가 가 있으면 금액 말하기
            String [] ss = shortText.split("매수가");
            if (ss.length > 1) {
                int p = ss[1].indexOf("원");
                won = (p > 0) ? ss[1].substring(2,p) :ss[1].substring(0,7);
            }
            joins = new String[]{sbnGroup, sbnWho, sParse[0], stock.talk, won};
            sounds.speakBuyStock(String.join(" , ", joins));
            String netStr = won + " " + ((shortText.length() > 50) ? shortText.substring(0, 50) : shortText);
            Log.w(sbnGroup, netStr);
            String title = sParse[0]+" / " + sbnWho;
            notificationBar.update(title, netStr, true);

            logUpdate.addStock(sParse[0] + " ["+sbnGroup+":"+sbnWho+"]", shortText+key12);
            new Copy2Clipboard(sParse[0]);
            if (isSilentNow()) {
                if (phoneVibrate == null)
                    phoneVibrate = new PhoneVibrate();
                phoneVibrate.vib(1);
            }
            new StockToast().show(mContext, mMainActivity, title);
        } else {
            String title = sParse[0]+" | "+sbnGroup+". "+sbnWho;
            logUpdate.addStock(title, shortText + key12);
            if (!isSilentNow()) {
                sounds.beepOnce(MainActivity.soundType.ONLY.ordinal());
            }
            String shortParse1 = (shortText.length() > 50) ? shortText.substring(0, 50) : shortText;

            notificationBar.update(title, shortParse1, false);
        }
        String timeStamp = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(new Date());
        gSheetUpload.add2Stock(sbnGroup, timeStamp, sbnWho, percent, sParse[0], shortText, key12);

    }

    boolean isSilentNow() {
        return (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT ||
                mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE);
    }
}
