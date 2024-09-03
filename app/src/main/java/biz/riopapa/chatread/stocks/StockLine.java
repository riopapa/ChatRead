package biz.riopapa.chatread.stocks;

import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.hourMin;
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
import static biz.riopapa.chatread.MainActivity.toDay;
import static biz.riopapa.chatread.MainActivity.utils;

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

public class StockLine {

    public void sayIfMatched(int g, int w, ArrayList<SStock> stocks, String sText) {

        for (int s = 0; s < stocks.size(); s++) {
            SStock stock = stocks.get(s);
            if (isMatchingStock(stock, sText)) {
                processStock(stock, s, sText, g, w, sbnGroup, sbnWho);
                break;
            }
        }
    }

    private boolean isMatchingStock(SStock stock, String txt) {
        return txt.contains(stock.key1) && txt.contains(stock.key2);
    }

    private void processStock(SStock stock, int stockIndex, String stockTxt,
                      int gCode, int wCode, String grpName, String whoName) {
        Runnable runnable = () -> {
            String[] sParse = stockName.get(stock.prv, stock.nxt, stockTxt);
            String stkName = sParse[0];
            String stkText = sParse[1];

            if (kvTelegram.isDup(grpName + stkName, stockTxt))
                return;

            String percent = (!stockTxt.contains("매수") && (stockTxt.contains("매도") || stockTxt.contains("익절"))) ?
                    "1.9" : stock.talk;
            String key12 = " {" + stock.key1 + "." + stock.key2 + "}";
            String strHead = stkName + " / " + whoName + ":" + grpName;

            if (!stock.talk.isEmpty()) {
                String[] joins;
                String won = wonValue(stkText);
                joins = new String[]{grpName, whoName, stkName, stock.talk, won};
                sounds.speakBuyStock(String.join(" , ", joins));

                if (sGroups.get(gCode).log) {
                    String strText = makeShort(strUtil.removeSpecialChars(stkText), sGroups.get(gCode));
                    strText = won + " " + ((strText.length() > 70) ? strText.substring(0, 70) : strText);
                    utils.logB(grpName+" Log", strText);
                }

                new Copy2Clipboard(stkName);
                if (isSilentNow()) {
                    if (phoneVibrate == null) {
                        phoneVibrate = new PhoneVibrate();
                    }
                    phoneVibrate.go(1);
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isScreenOn(mContext) && mActivity != null) {
                        String head = stkName + " / " + whoName + ":" + grpName;
                        mActivity.runOnUiThread(() -> Toast.makeText(mContext, head, Toast.LENGTH_LONG).show());
                        // Removed duplicate Toast
                    }
                });
            } else {
                String strText = makeShort(strUtil.removeSpecialChars(stkText), sGroups.get(gCode));
                strHead = stkName + " | " + grpName + ". " + whoName;
                if (!isSilentNow()) {
                    sounds.beepOnce(MainActivity.soundType.ONLY.ordinal());
                }
                if (sGroups.get(gCode).log) {
                    utils.logB(grpName+"x", strText);
                }
            }

            logUpdate.addStock(stkName + " [" + grpName + ":" + whoName + "]", stkText + key12);
            notificationBar.update(strHead, stkText, true);

            String timeStamp = toDay + new SimpleDateFormat(hourMin, Locale.KOREA).format(new Date());
            gSheet.add2Stock(grpName, timeStamp, whoName, percent, stkName, stkText, key12);

            sGroups.get(gCode).whos.get(wCode).stocks.get(stockIndex).count++;
            stockGetPut.save(sGroups.get(gCode).whos.get(wCode).whoF + " " + sGroups.get(gCode).whos.get(wCode).stocks.get(stockIndex).count);
        };

        new Thread(runnable).start();
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