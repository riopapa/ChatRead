package biz.riopapa.chatread.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class SWho implements Cloneable{
    public String who;  // who short name
    public String whoF; // who full name
    public ArrayList<SStock> stocks;
    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); // Shallow copy by default
    }
}
