package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.kvWork;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.strUtil;

import biz.riopapa.chatread.models.SBar;

public class CaseWork {
    public void work(SBar sb) {

        if (kvWork.isDup(sb.who, sb.text))
            return;

        if (sb.app.replF != null) {
            for (int i = 0; i < sb.app.replF.length; i++) {
                if ((sb.who).contains(sb.app.replF[i]))
                    sb.who = sb.who.replace(sb.app.replF[i], sb.app.replT[i]);
            }
        }

        String head = sb.app.nickName + "." + sb.who;
        sb.text = strUtil.makeEtc(sb.text, 140);
        logUpdate.addWork(head, sb.text);
        notificationBar.update(head, sb.text, true);
        String say = head + ", " + sb.text;
        sounds.speakAfterBeep(strUtil.makeEtc(say, 70));
    }
}
