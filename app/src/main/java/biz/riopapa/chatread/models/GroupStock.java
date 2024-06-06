package biz.riopapa.chatread.models;

import java.util.ArrayList;

public class GroupStock {
    public String grp; // group Short name
    public String grpF; // group Full name
    public String skip1, skip2, skip3;  // if contains this string then skip this group
    public boolean telGrp;   // true if telegram group
    public boolean ignore;
    public ArrayList<Who> whos;

}
