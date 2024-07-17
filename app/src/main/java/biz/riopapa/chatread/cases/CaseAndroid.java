package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.ignoreString;
import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.sbnApp;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;

public class CaseAndroid {
    public void check() {
        if (kvCommon.isDup("an", sbnText))
            return;
        if (ignoreString.check(sbnApp))
            return;
        String head = "< an > "+sbnWho;
        logUpdate.addLog(head, sbnWho+" / "+sbnText);
    }
}
