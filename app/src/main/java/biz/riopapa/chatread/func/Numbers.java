package biz.riopapa.chatread.func;

public class Numbers {
    public String deduct(String str) {
        return str.replaceAll("[\\d,:/]", "");
    }
}
