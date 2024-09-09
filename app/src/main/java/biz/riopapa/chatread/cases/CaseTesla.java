package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.soundType.HI_TESLA;
import static biz.riopapa.chatread.MainActivity.sounds;

import biz.riopapa.chatread.models.SBar;

public class CaseTesla {

    static long tesla_time = 0;

    public void tesla(SBar sb) {

        if (sb.text.contains("연결됨")) {
            long nowTime = System.currentTimeMillis();
            if ((nowTime - tesla_time) > 30 * 60 * 1000) {   // nn min.
                sounds.beepOnce(HI_TESLA.ordinal());
                tesla_time = nowTime;
            }
            return;
        }
        logUpdate.addLog("[ 테스리 ]", sb.text);
        notificationBar.update(sb.app.nick, sb.text, true);
        sounds.speakAfterBeep("테스리, 테스리, " + sb.text);
    }
}
