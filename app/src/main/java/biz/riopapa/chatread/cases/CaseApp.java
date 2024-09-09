package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.caseSMS;
import static biz.riopapa.chatread.MainActivity.caseTesla;
import static biz.riopapa.chatread.MainActivity.caseWork;
import static biz.riopapa.chatread.MainActivity.ignoreString;
import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.msgNamoo;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.utils;
import static biz.riopapa.chatread.NotificationListener.isWorking;

import biz.riopapa.chatread.models.SBar;

public class CaseApp {

    public void app(SBar sb) {
        if (kvCommon.isDup(sb.app.nick, sb.text))
            return;

        if (sb.app.igStr != null && ignoreString.check(sb))
            return;

        if (sb.app.nick.equals("문자")) {
            caseSMS.sms(sb);
            return;
        }

        sb.text = strUtil.text2OneLine(sb.text);
        if (sb.app.infoFrom != null) {
            for (int i = 0; i < sb.app.infoFrom.length; i++) {
                if (sb.who.contains(sb.app.infoFrom[i]) ||
                    sb.text.contains(sb.app.infoFrom[i])) {
                    sb.who = sb.app.nick;
                    sb.text = sb.app.infoTo[i];
                    break;
                }
            }
        }

        if (sb.app.replF != null) {
            for (int i = 0; i < sb.app.replF.length; i++) {
                if ((sb.text).contains(sb.app.replF[i])) {
                    sb.text = sb.text.replace(sb.app.replF[i],sb.app.replT[i]);
                }
            }
        }

        String nickCode = sb.app.nick;

        if (nickCode.equals("NH나무")) {
            utils.logB(nickCode,sb.text);
            msgNamoo.namoo(strUtil.text2OneLine(sb.text));
            return;
        }

        if (nickCode.equals("팀즈") || nickCode.equals("아룩")) {
            caseWork.work(sb);
            return;
        }

        if (nickCode.equals("테스리")) {
            caseTesla.tesla(sb);
            return;
        }

        if (sb.app.say) {
            String say = sb.app.nick + " ";
            say += (sb.app.grp) ? sb.group+" ": " ";
            say += (sb.app.who) ? sb.who: "";
            say = say + ", ";
            say = say + ((sb.app.num) ? sb.text : strUtil.removeDigit(sb.text));
            sounds.speakAfterBeep(strUtil.makeEtc(say, isWorking()? 20: 200));
        }

        if (sb.app.addWho)
            sb.text = "👨‍🦱" + sb.who + "👨‍🦱" + sb.text;

        String head = sb.app.nick + ((sb.app.grp && !sb.group.isEmpty()) ? "_" +sb.group+".": "")
                + ((sb.app.who)? "@" + sb.who : "");

        if (sb.app.log)
            logUpdate.addLog(head, sb.text);

        notificationBar.update(head, sb.text, true);
    }

}
