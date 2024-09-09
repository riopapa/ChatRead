package biz.riopapa.chatread.models;

public class App {
    public String fullName, nick, memo;
    public boolean say, log, grp, who, addWho, num;
    public String [] infoFrom, infoTo;      // if "inform" found say "talk" only
    public String [] igStr;
    public String [] replF, replT;      // if contains replFrom, replace to replTo
}