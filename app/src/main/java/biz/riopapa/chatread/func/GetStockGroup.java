package biz.riopapa.chatread.func;

public class GetStockGroup {

    public int getIdx(String groupName, String [] matches, int [] index) {
        for (int i = 0; i < matches.length; i++) {
            int compared = groupName.compareTo(matches[i]);
            if (compared < 0)
                return -1;
            if (groupName.startsWith(matches[i]))
                return index[i];
        }
        return -1;
    }
}
