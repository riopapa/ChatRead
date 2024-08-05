package biz.riopapa.chatread.stocks;

import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.kvTelegram;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.mAudioManager;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.mActivity;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.phoneVibrate;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.stockName;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.utils;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
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

public class StockLine {

    String strHead, strText;

    public void sayIfMatched(int g, int w, ArrayList<SStock> stocks) {

        for (int s = 0; s < stocks.size() ; s++) {
            if (sbnText.contains(stocks.get(s).key1) && sbnText.contains(stocks.get(s).key2)) {

                SStock stock = stocks.get(s);
                if (stockName == null)
                    stockName = new StockName();
                String [] sParse = stockName.get(stock.prv, stock.nxt, sbnText);
                if (kvTelegram.isDup(sbnWho+sParse[0], sbnText))    // 같은 주식 내용 반복
                    return;

                // 안정 되면 아래 null check는 지우는 걸로..
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
                String key12 = " {" + stock.key1 + "." + stock.key2 + "}";
                strHead = sParse[0]+" / " + sbnWho + ":" +sbnGroup;

                if (!stock.talk.isEmpty()) {
                    String [] joins;
                    String won = wonValue(sParse[1]);
                    joins = new String[]{sbnGroup, sbnWho, sParse[0], stock.talk, won};
                    sounds.speakBuyStock(String.join(" , ", joins));
                    if (sGroups.get(g).log) {
                        strText = makeShort(strUtil.removeSpecialChars(sParse[1]), sGroups.get(g));
                        strText = won + " " + ((strText.length() > 70) ? strText.substring(0, 70) : strText);
                        utils.logB(sbnGroup, strText);
                    }
                    new Copy2Clipboard(sParse[0]);
                    if (isSilentNow()) {
                        if (phoneVibrate == null)
                            phoneVibrate = new PhoneVibrate();
                        phoneVibrate.vib(0);
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (isScreenOn(mContext) && mActivity != null) {
                            mActivity.runOnUiThread(() -> Toast.makeText(mContext, strHead, Toast.LENGTH_LONG).show());
                        }
                    });

                } else {
                    strText = makeShort(strUtil.removeSpecialChars(sParse[1]), sGroups.get(g));
                    strHead = sParse[0]+" | "+sbnGroup+". "+sbnWho;
                    if (!isSilentNow()) {
                        sounds.beepOnce(MainActivity.soundType.ONLY.ordinal());
                    }
                    if (sGroups.get(g).log) {
                        utils.logB(sbnGroup, strText);
                    }
                }
                logUpdate.addStock(sParse[0] + " ["+sbnGroup+":"+sbnWho+"]", strText
                        + key12);
                notificationBar.update(strHead, strText, true);
                String timeStamp = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(new Date());
                gSheet.add2Stock(sbnGroup, timeStamp, sbnWho, percent, sParse[0], strText, key12);

                sGroups.get(g).whos.get(w).stocks.get(s).count++;
                stockGetPut.save(sGroups.get(g).whos.get(w).whoF+" "+sGroups.get(g).whos.get(w).stocks.get(s).count);

                break;
            }
        }
    }

    private String wonValue (String shortText) {
        // 매수가, 진입가 가 있으면 금액 말하기
        String [] ss = shortText.split("매수가");
        if (ss.length < 2) {
            ss = shortText.split("진입가");
            if (ss.length < 2)
                return "";
        } else
            return  "";
        int p = ss[1].indexOf("원");
        return (p > 0) ? ss[1].substring(2,p) :ss[1].substring(2,8);
    }

    boolean isSilentNow() {
        return (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT ||
                mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE);
    }

    String makeShort(String text, SGroup sGroup) {
        if (sGroup.replF == null)
            return text;
        for (int i = 0; i < sGroup.replF.size(); i++) {
            text = text.replace(sGroup.replF.get(i), sGroup.replT.get(i));
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