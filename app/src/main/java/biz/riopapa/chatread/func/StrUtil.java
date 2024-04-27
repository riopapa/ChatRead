package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.replGroup;
import static biz.riopapa.chatread.MainActivity.replGroupCnt;
import static biz.riopapa.chatread.MainActivity.replLong;
import static biz.riopapa.chatread.MainActivity.replShort;

public class StrUtil {

    public String removeSpecialChars(String text) {
        return text.replace("──", "").replace("==", "-")
                .replace("=", "ￚ").replace("--", "-")
                .replaceAll("[^\\da-zA-Z:|#().@,%/~가-힣\\s\\-+]", "")
//                .replaceAll("[^\\da-zA-Z:|#().@,%/~ㄱ-ㅎ가-힣\\s\\-+]", "")
                ;
    }

    public String strShorten(String groupOrWho, String text) {
        for (int i = 0; i < replGroupCnt; i++) {
            int compared = groupOrWho.compareTo(replGroup[i]);
            if (compared == 0) {
                for (int j = 0; j < replLong[i].length; j++)
                    text = text.replace(replLong[i][j], replShort[i][j]);
                return text;
            }
            if (compared < 0) {
                return text;
            }
        }
        return text;
    }

    public String makeEtc (String s, int len) {
        return (s.length() < len)? s : s.substring(0, len) + " 등등";
    }

    public String replaceKKHH(String text) {
        return text.replace("ㅇㅋ", " 오케이 ")
                .replace("ㅊㅋ", " 축하 ")
                .replace("ㅠㅠ", " 흑 ")
                .replace("ㅠ", " 흑 ")
                ;
    }

    public String text2OneLine(String mText) {
        return mText.replace("\n", "|").replace("\r", "").replace("||", "|").replace("||", "|");
    }

    public String reMoveDigit(String str) {
        return str.replaceAll("[\\d,:/]", "");
    }

}
