package biz.riopapa.chatread.func;

public class GetStockGroup {

    public int getIdx(String sbnWho, String [] matches, int [] index) {
        for (int i = 0; i < matches.length; i++) {
            int compared = sbnWho.compareTo(matches[i]);
            if (compared < 0)
                return -1;
            if (sbnWho.startsWith(matches[i]))
                return index[i];
        }
        return -1;
    }
}
