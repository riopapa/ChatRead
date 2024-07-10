package biz.riopapa.chatread.func;

public class StrUtil {

    public String removeSpecialChars(String text) {
        return text.replace("──", "").replace("==", "-")
                .replace("=", "ￚ").replace("--", "-")
                .replaceAll("[^\\w\\s가-힣]", "")
//                .replaceAll("[^\\da-zA-Z\\:|#().@,()%/~가-힣\\s\\-+]", "")
//                .replaceAll("[^\\da-zA-Z:|#().@,%/~ㄱ-ㅎ가-힣\\s\\-+]", "")
                ;
    }

//    public String strShorten(String groupOrWho, String text) {
//        for (int i = 0; i < replGroupCnt; i++) {
//            int compared = groupOrWho.compareTo(replGroup[i]);
//            if (compared == 0) {
//                for (int j = 0; j < replLong[i].length; j++)
//                    text = text.replace(replLong[i][j], replShort[i][j]);
//                return text;
//            }
//            if (compared < 0) {
//                return text;
//            }
//        }
//        return text;
//    }

    public String makeEtc (String s, int len) {
        return (s.length() < len)? s : s.substring(0, len) + " 등등";
    }

    public String replaceKKHH(String text) {
        return text.replace("ㅇㅋ", " 오케이 ")
                .replace("ㅊㅋ", " 축하 ")
                .replace("ㅠㅠ", " 흑흑 ")
                .replace("ㅠ", " 흑 ")
                .replace("ㅋ", "크")
                .replace("ㅎ", "후")
                ;
    }

    public String text2OneLine(String mText) {
        return mText.replace("\n", "|").replace("\r", "").replace("||", "|").replace("||", "|");
    }

    public String removeDigit(String str) {
        return str.replaceAll("[\\d,:/]", "");
    }

}
