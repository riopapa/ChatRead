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
import android.view.Display;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import biz.riopapa.chatread.MainActivity;
import biz.riopapa.chatread.common.Copy2Clipboard;
import biz.riopapa.chatread.common.PhoneVibrate;
import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.SStock;

public class StockCheck {

    public void sayIfMatched(int g, int w, ArrayList<SStock> stocks,
                 String gName, String wName, String sText) {

        for (int s = 0; s < stocks.size(); s++) {
            SStock stock = stocks.get(s);
            if (sText.contains(stock.key1) && sText.contains(stock.key2)) {
                processStock(stock, s, sText, g, w, gName, wName);
                break;
            }
        }
    }

    private void processStock(SStock stock, int stockIndex, String stockTxt,
                      int gCode, int wCode, String grpName, String whoName) {
        Runnable runnable = () -> {
            String[] sParse = stockName.get(stock.prv, stock.nxt, stockTxt);
            String stkName = sParse[0];

            if (kvTelegram.isDup(grpName + stkName, stockTxt))
                return;

            String percent = (!stockTxt.contains("매수") && (stockTxt.contains("매도") || stockTxt.contains("익절"))) ?
                    "1.9" : stock.talk;
            String key12 = " {" + stock.key1 + "." + stock.key2 + "}";
            String strHead;
            SGroup sGroup = sGroups.get(gCode);
            String shortText = makeShort(removeSpecialChars(sParse[1]), sGroup);

            if (!stock.talk.isEmpty()) {
                strHead = stkName + " / " + grpName + "/" + whoName;
                String[] joins;
                String won = wonValue(shortText);

                joins = new String[]{stock.talk, grpName, whoName, stkName, stkName,
                        won, won};
                sounds.speakBuyStock(String.join(" , ", joins));
                if (sGroup.log) {
                    utils.logB(grpName+" Log", won + " " +
                            ((shortText.length() > 90) ? shortText.substring(0, 90) : shortText));
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
                        String head = stkName + " / " + grpName + " / " + whoName;
                        mActivity.runOnUiThread(() -> Toast.makeText(mContext, head, Toast.LENGTH_LONG).show());
                        // Removed duplicate Toast
                    }
                });
            } else {
                strHead = stkName + " | " + grpName + " - " + whoName;
                if (!isSilentNow()) {
                    sounds.beepOnce(MainActivity.soundType.ONLY.ordinal());
                }
            }

            logUpdate.addStock("[" + strHead + "]", shortText + key12);
            notificationBar.update(strHead, shortText, true);

            String timeStamp = toDay + new SimpleDateFormat(hourMin, Locale.KOREA).format(new Date());
            gSheet.add2Stock(grpName, timeStamp, whoName, percent, stkName, shortText, key12);

            sGroup.whos.get(wCode).stocks.get(stockIndex).count++;
            stockGetPut.save(grpName+"|"+sGroup.whos.get(wCode).whoF + " " + sGroup.whos.get(wCode).stocks.get(stockIndex).count);
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

    String removeSpecialChars(String text) {
        return text.replace("──", "").replace("==", "-")
                .replace("=", "ￚ").replace("--", "-")
//                .replaceAll("[^\\w\\s가-힣]", "")
                .replaceAll("[^\\da-zA-Z:|#)(.@,%/~가-힣\\s\\-+]", "")
                ;
    }

}