package biz.riopapa.chatread.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class SGroup implements Cloneable {
    public String grp; // group Short name
    public String grpF; // group Full name
    public String skip1, skip2, skip3;  // if contains this string then skip this group
    public String telKa; // 't' for telegram, 'k' for kakaoTalk;
    public boolean ignore;
    public ArrayList<SWho> whos;

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); // Shallow copy by default
    }
}
