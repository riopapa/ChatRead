package biz.riopapa.chatread.models;

public class Alert {
    public String group, who, key1, key2, talk, skip, prev, next;
    public boolean quiet;
    public int matched;
    public Alert(String group, String who, String key1, String key2, String talk, int matched,
                 String skip, String prev, String next, boolean quiet) {
        this.group = group;this.who = who;
        this.key1 = key1;this.key2 = key2;this.talk = talk;
        this.matched = matched; this.skip = skip;
        this.prev = prev; this.next = next; this.quiet = quiet;
    }
}