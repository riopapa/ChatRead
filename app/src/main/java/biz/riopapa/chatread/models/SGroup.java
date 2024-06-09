package biz.riopapa.chatread.models;

import java.util.ArrayList;

public class SGroup {
    public String grp; // group Short name
    public String grpF; // group Full name
    public String skip1, skip2, skip3;  // if contains this string then skip this group
    public Character telKa; // 't' for telegram, 'k' for kakaoTalk;
    public boolean ignore;
    public ArrayList<SWho> whos;

}
