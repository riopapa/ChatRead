package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.sounds;

class MsgAndroid {

    void say(String appFullName, String who, String text) {
        text = " Android [" + who + "] " + text;
        logUpdate.addLog(appFullName, text);
        sounds.speakAfterBeep(text);
    }
}