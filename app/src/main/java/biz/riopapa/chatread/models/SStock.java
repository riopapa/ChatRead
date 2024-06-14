package biz.riopapa.chatread.models;

import androidx.annotation.NonNull;

public class SStock implements Cloneable{
    public String key1, key2;
    public String prv, nxt;
    public String talk;
    public String skip1;
    public int count;   // how many matched?
    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); // Shallow copy by default
    }

}