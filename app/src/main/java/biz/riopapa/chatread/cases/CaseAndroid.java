package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.ignoreString;
import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.logUpdate;

import biz.riopapa.chatread.models.SBar;

public class CaseAndroid {
    public void droid(SBar sb) {
        if (kvCommon.isDup("an", sb.text))
            return;
        if (ignoreString.check(sb))
            return;
        String head = "< an > "+sb.who;
        logUpdate.addLog(head, sb.who+" / "+sb.text);
    }
}
