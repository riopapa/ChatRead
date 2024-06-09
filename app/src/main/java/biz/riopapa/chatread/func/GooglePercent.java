package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.nowSGroup;

public class GooglePercent {

    public String make() {
        String sb = "Skip (" + nowSGroup.skip1 + ", " + nowSGroup.skip1 + ", " +
                nowSGroup.skip2 + ")" +
                " Ignore:" + ((nowSGroup.ignore) ? "yes" : "") +
                " TelKa (" + ((nowSGroup.telKa == 't') ? "tel" : (nowSGroup.telKa == 'k') ? "ka" : "") +
                ")";
        return sb;
    }
}
