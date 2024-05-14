package biz.riopapa.chatread.func;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;

import androidx.core.content.res.ResourcesCompat;

import org.apache.commons.lang3.StringUtils;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.models.DelItem;

public class LogSpan {

    Typeface font1, font2;
    String dayNow = "x";
    SpannableString ss;
    boolean fontSwitch = true;

    public SpannableString make(String log, Context context) {

        font1 = Typeface.create(ResourcesCompat.getFont(context, R.font.mayplestory), Typeface.NORMAL);
        font2 = Typeface.create(ResourcesCompat.getFont(context, R.font.cookie_run), Typeface.NORMAL);
        int nPos = 0, sLen;

        int [][]colors = new int[2][];

        colors[0] = new int[]{
                context.getColor(R.color.log_head_f0), context.getColor(R.color.log_head_b0),
                context.getColor(R.color.log_line_f0), context.getColor(R.color.log_line_b0),
                context.getColor(R.color.log_line_x0)
        };
        colors[1] = new int[]{
                context.getColor(R.color.log_head_f1), context.getColor(R.color.log_head_b1),
                context.getColor(R.color.log_line_f1), context.getColor(R.color.log_line_b1),
                context.getColor(R.color.log_line_x1)
        };

        int colorIdx = 0;
        int colorFore, colorBack;

        Typeface font = font1;
        String tmp = (log+"\n").replace("\n\n\n","\n\n").trim();
        ss = new SpannableString(tmp);
        String[] msgLine = tmp.split("\n");
        for (String s : msgLine) {
            sLen = s.length();
            if (sLen == 0) {
                nPos += 1;
                continue;
            }
            if (s.length() < 2) {
                nPos += s.length() + 1;
                continue;
            }

            if (StringUtils.isNumeric(String.valueOf(s.charAt(0))) && s.length() > 5) {  // timestamp + who
                if (s.substring(0, 5).equals(dayNow)) {
                    colorFore = colors[colorIdx][0];
                    colorBack = colors[colorIdx][1];
                } else {    // new day
                    colorIdx = (colorIdx + 1) % 2;
                    colorFore = colors[colorIdx][0];
                    colorBack = colors[colorIdx][1];
                    dayNow = s.substring(0, 5);
                    font = (fontSwitch) ? font1 : font2;
                    fontSwitch = !fontSwitch;
                }
            } else {
                colorFore = colors[colorIdx][2];
                colorBack = colors[colorIdx][3];
            }

            int endPos = nPos + sLen;
            ss.setSpan(new ForegroundColorSpan(colorFore), nPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new BackgroundColorSpan(colorBack), nPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new TypefaceSpan(font), nPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nPos += sLen + 1;
        }
        return ss;
    }

    public DelItem delOneSet(String logNow, int pCurr, Context context) {
        int pStart  = logNow.lastIndexOf("\n", pCurr - 1);
        int pFinish = logNow.indexOf("\n", pStart+1);
        if (pFinish == -1) {
            pFinish = logNow.length();
            logNow += "\n";
        }
        pStart = logNow.lastIndexOf("\n", pStart - 1);
        if (pStart < 2)
            pStart = 1;
        if (logNow.charAt(pStart - 1) == '\n')
            logNow = logNow.substring(0, pStart - 1) + logNow.substring(pFinish);
        else
            logNow = logNow.substring(0, pStart) + logNow.substring(pFinish);
        // positioned 2 lines deleted and now to skip if \n
        if (logNow.charAt(0) == '\n')
            logNow = logNow.substring(1);
        if (logNow.charAt(0) == '\n')
            logNow = logNow.substring(1);
        if (pStart >= logNow.length())
            pStart = logNow.length() - 2;
        pStart = logNow.lastIndexOf("\n", pStart - 2) + 1;
        pFinish = logNow.indexOf("\n", pStart);
        if (pStart > logNow.length())
            pStart = logNow.lastIndexOf("\n") - 1;
        if (pFinish > logNow.length() || pFinish == -1)
            pFinish = logNow.length();
        SpannableString ss = make(logNow, context);
        ss.setSpan(new StyleSpan(Typeface.ITALIC), pStart, pFinish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), pStart, pFinish,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return new DelItem(logNow, pStart, pFinish, pCurr, ss);
    }

    public DelItem delOneLine(String logStr, int pCurr, Context context) {
        int pStart = logStr.lastIndexOf("\n", pCurr -1);
        if (pStart == -1)
            pStart = 0;
        int pFinish = logStr.indexOf("\n", pStart+1);
        if (pFinish == -1)
            pFinish = logStr.length();
        logStr = logStr.substring(0, pStart) + logStr.substring(pFinish);
        SpannableString ss = make(logStr, context);
        if (pStart > 1) {
            pStart = logStr.lastIndexOf("\n", pStart - 1) + 1;
            pFinish = logStr.indexOf("\n", pStart) - 1;
            if (pFinish < pStart) {
                if (pStart > 0)
                    pStart--;
                pFinish = pStart + 1;
            }
        }
        ss.setSpan(new StyleSpan(Typeface.ITALIC), pStart, pFinish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), pStart, pFinish,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return new DelItem(logStr, pStart, pFinish, pCurr, ss);
    }
}
