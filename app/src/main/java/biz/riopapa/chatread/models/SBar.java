package biz.riopapa.chatread.models;

public class SBar {
    public String group, who, text;
    public String type; // t telegram, k kakao, d default
    public App app;

    public String appName;  // only effective if app is new Application

    public SBar() {}
}