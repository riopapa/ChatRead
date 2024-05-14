package biz.riopapa.chatread.models;

import android.text.SpannableString;

public class DelItem {
    public String logNow;
    public int pStart;  // new start position
    public int pFinish;  // new end position
    public int pOld; // old position
    public SpannableString ss;
    public DelItem(String logNow, int pStart, int pFinish, int pOld, SpannableString ss) {
        this.logNow = logNow;
        this.pStart = pStart; this.pFinish = pFinish;
        this.ss = ss;
        this.pOld = pOld;
    }
}
