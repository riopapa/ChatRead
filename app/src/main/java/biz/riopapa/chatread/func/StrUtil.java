package biz.riopapa.chatread.func;

public class StrUtil {


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
