package biz.riopapa.chatread.func;

import biz.riopapa.chatread.models.SBar;

public class IgnoreString {
    public boolean check(SBar sb) {
        for (String t: sb.app.igStr) {
            if (sb.who.contains(t))
                return true;
        }
        for (String t: sb.app.igStr) {
            if (sb.text.contains(t))
                return true;
        }
        return false;
    }
}
