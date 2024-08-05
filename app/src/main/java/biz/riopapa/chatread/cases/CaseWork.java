package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.kvStock;
import static biz.riopapa.chatread.MainActivity.kvWork;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.sbnApp;
import static biz.riopapa.chatread.MainActivity.sbnAppNick;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.strUtil;

public class CaseWork {
    public void work() {


        if (kvWork.isDup(sbnWho, sbnText))
            return;

        if (sbnApp.replF != null) {
            for (int i = 0; i < sbnApp.replF.length; i++) {
                if ((sbnWho).contains(sbnApp.replF[i]))
                    sbnWho = sbnWho.replace(sbnApp.replF[i], sbnApp.replT[i]);
            }
        }

        String head = sbnAppNick + "." + sbnWho;
        sbnText = strUtil.makeEtc(sbnText, 140);
        logUpdate.addWork(head, sbnText);
        notificationBar.update(head, sbnText, true);
        String say = head + ", " + sbnText;
        sounds.speakAfterBeep(strUtil.makeEtc(say, 70));
    }
}
