package biz.riopapa.chatread.stocks;

import static biz.riopapa.chatread.MainActivity.gIdx;
import static biz.riopapa.chatread.MainActivity.gSheetUpload;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.mAudioManager;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.mMainActivity;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.nowSStock;
import static biz.riopapa.chatread.MainActivity.nowSWho;
import static biz.riopapa.chatread.MainActivity.phoneVibrate;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockName;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.utils;
import static biz.riopapa.chatread.MainActivity.wIdx;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import biz.riopapa.chatread.MainActivity;
import biz.riopapa.chatread.common.Copy2Clipboard;
import biz.riopapa.chatread.common.PhoneVibrate;
import biz.riopapa.chatread.common.Sounds;
import biz.riopapa.chatread.common.Utils;
import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.SStock;
import biz.riopapa.chatread.models.SWho;

public class StockCheck {

    public void check(ArrayList<SStock> stocks) {

        for (int s = 0; s < stocks.size() ; s++) {
            nowSStock = stocks.get(s);
            if (sbnText.contains(nowSStock.key1) && sbnText.contains(nowSStock.key2)) {
                nowSStock.count++;
                try {
                    nowSWho.stocks.set(s, (SStock) nowSStock.clone());
                    nowSGroup.whos.set(wIdx, (SWho) nowSWho.clone());
                    sGroups.set(gIdx, (SGroup) nowSGroup.clone());
                } catch (CloneNotSupportedException e) {
                    utils.logE("StockCheck", "CloneNotSupportedException");
                }
                talkNlog(nowSStock);
                break;
            }
        }
    }

    void talkNlog(SStock stock) {
        if (utils == null) {
            utils = new Utils();
            utils.logW("talkNlog", "utils null");
        }
        if (sounds == null) {
            sounds = new Sounds();
            utils.logW("talkNlog", "sounds null");
        }

        String percent = (!sbnText.contains("매수") && (sbnText.contains("매도") || sbnText.contains("익절")))?
                "1.9" : stock.talk;
        if (stockName == null)
            stockName = new StockName();
        String [] sParse = stockName.get(stock.prv, stock.nxt, sbnText);
        String key12 = " {" + stock.key1 + "." + stock.key2 + "}";
        String shortText = makeShort(strUtil.removeSpecialChars(sParse[1]), nowSGroup);
        if (!stock.talk.isEmpty()) {
            String [] joins;
            String won = "";
            // 매수가 가 있으면 금액 말하기
            String [] ss = shortText.split("매수가");
            if (ss.length > 1) {
                int p = ss[1].indexOf("원");
                won = (p > 0) ? ss[1].substring(2,p) :ss[1].substring(0,7);
            } else {
                ss = shortText.split("진입가");
                if (ss.length > 1) {
                    int p = ss[1].indexOf("원");
                    won = (p > 0) ? ss[1].substring(2, p) : ss[1].substring(0, 7);
                }
            }
            joins = new String[]{sbnGroup, sbnWho, sParse[0], stock.talk, won};
            sounds.speakBuyStock(String.join(" , ", joins));
            String netStr = won + " " + ((shortText.length() > 50) ? shortText.substring(0, 50) : shortText);
            utils.logW(sbnGroup, netStr);
            String title = sParse[0]+" / " + sbnWho;
            notificationBar.update(title, netStr, true);
            logUpdate.addStock(sParse[0] + " ["+sbnGroup+":"+sbnWho+"]", shortText+key12
                    + " " + stock.count);
            new Copy2Clipboard(sParse[0]);
            if (isSilentNow()) {
                if (phoneVibrate == null)
                    phoneVibrate = new PhoneVibrate();
                phoneVibrate.vib(1);
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                if (isScreenOn(mContext) && mMainActivity != null) {
                    mMainActivity.runOnUiThread(() -> Toast.makeText(mContext, title, Toast.LENGTH_LONG).show());
                }
            });

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

    String makeShort(String text, SGroup sGroup) {
        if (sGroup.replF != null) {
            for (int i = 0; i < sGroup.replF.size(); i++) {
                text = text.replace(sGroup.replF.get(i), sGroup.replT.get(i));
            }
        }
        return text;
    }


    boolean isScreenOn(Context context) {
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        boolean screenOn = false;
        for (Display display : dm.getDisplays()) {
            if (display.getState() != Display.STATE_OFF) {
                screenOn = true;
            }
        }
        return screenOn;
    }

}