package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.nowSGroup;

import biz.riopapa.chatread.models.SGroup;

public class GooglePercent {

    public String make(SGroup sGroup) {
        return "Skip (" + sGroup.skip1 + ", " + sGroup.skip1 + ", " + sGroup.skip2 + ")" +
                " Ignore:" + ((sGroup.ignore) ? "yes" : "") +
                " TelKa (" + ((sGroup.telKa == 't') ? "tel" : (sGroup.telKa == 'k') ? "ka" : "") +
                ")";
    }
}
