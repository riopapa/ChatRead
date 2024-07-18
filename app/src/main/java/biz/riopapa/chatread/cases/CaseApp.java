package biz.riopapa.chatread.cases;

import static biz.riopapa.chatread.MainActivity.caseSMS;
import static biz.riopapa.chatread.MainActivity.caseTesla;
import static biz.riopapa.chatread.MainActivity.caseWork;
import static biz.riopapa.chatread.MainActivity.ignoreString;
import static biz.riopapa.chatread.MainActivity.kvCommon;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.msgNamoo;
import static biz.riopapa.chatread.MainActivity.notificationBar;
import static biz.riopapa.chatread.MainActivity.sbnApp;
import static biz.riopapa.chatread.MainActivity.sbnAppNick;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.sbnText;
import static biz.riopapa.chatread.MainActivity.sbnWho;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.utils;
import static biz.riopapa.chatread.NotificationListener.isWorking;

public class CaseApp {

    public void check () {
        if (kvCommon.isDup(sbnApp.nickName, sbnText))
            return;

        if (sbnApp.igStr != null && ignoreString.check(sbnApp))
            return;

        if (sbnApp.nickName.equals("문자")) {
            caseSMS.check();
            return;
        }

        sbnText = strUtil.text2OneLine(sbnText);
        if (sbnApp.inform != null) {
            for (int i = 0; i < sbnApp.inform.length; i++) {
                if ((sbnWho).contains(sbnApp.inform[i])) {
                    sbnWho = sbnAppNick;
                    sbnText = sbnApp.talk[i];
                    break;
                }
                if (sbnText.contains(sbnApp.inform[i])) {
                    sbnWho = sbnAppNick;
                    sbnText = sbnApp.talk[i];
                    break;
                }
            }
        }

        if (sbnApp.replF != null) {
            for (int i = 0; i < sbnApp.replF.length; i++) {
                if ((sbnText).contains(sbnApp.replF[i])) {
                    sbnText = sbnText.replace(sbnApp.replF[i],sbnApp.replT[i]);
                }
            }
        }

        if (sbnApp.nickName.equals("NH나무")) {
            utils.logB(sbnApp.nickName,sbnText);
            msgNamoo.say(strUtil.text2OneLine(sbnText));
            return;
        }

        if (sbnAppNick.equals("팀즈") || sbnAppNick.equals("아룩")) {
            caseWork.check();
            return;
        }

        if (sbnAppNick.equals("테스리")) {
            caseTesla.check();
            return;
        }

        if (sbnApp.say) {
            String say = sbnAppNick + " ";
            say += (sbnApp.grp) ? sbnGroup+" ": " ";
            say += (sbnApp.who) ? sbnWho: "";
            say = say + ", ";
            say = say + ((sbnApp.num) ? sbnText : strUtil.removeDigit(sbnText));
            sounds.speakAfterBeep(strUtil.makeEtc(say, isWorking()? 20: 200));
        }

        if (sbnApp.addWho)
            sbnText = "👨‍🦱" + sbnWho + "👨‍🦱" + sbnText;

        String head = sbnAppNick + ((sbnApp.grp && !sbnGroup.isEmpty()) ? "_" +sbnGroup+".": "")
                + ((sbnApp.who)? "@" + sbnWho : "");

        if (sbnApp.log)
            logUpdate.addLog(head, sbnText);

        notificationBar.update(head, sbnText, true);
    }

}
