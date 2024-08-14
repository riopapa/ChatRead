package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.sbnAppNick;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.soundType.HI_TESLA;
import static biz.riopapa.chatread.MainActivity.sounds;

public class CaseTesla {

    static long tesla_time = 0;

    public void tesry() {

        if (sbnText.contains("연결됨")) {
            long nowTime = System.currentTimeMillis();
            if ((nowTime - tesla_time) > 30 * 60 * 1000) {   // nn min.
                sounds.beepOnce(HI_TESLA.ordinal());
                tesla_time = nowTime;
            }
            return;
        }
        logUpdate.addLog("[ 테스리 ]", sbnText);
        notificationBar.update(sbnAppNick, sbnText, true);
        sounds.speakAfterBeep("테스리, 테스리, " + sbnText);
    }
}
