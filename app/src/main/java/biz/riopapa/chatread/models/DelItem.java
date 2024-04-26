package biz.riopapa.chatread.models;

import android.text.SpannableString;

public class DelItem {
    public String logNow;
    public int ps;
    public int pf;
    public SpannableString ss;
    public DelItem(String logNow, int ps, int pf, SpannableString ss) {
        this.logNow = logNow;
        this.ps = ps; this.pf = pf;
        this.ss = ss;
    }
}
