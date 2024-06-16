package biz.riopapa.chatread.func;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static biz.riopapa.chatread.MainActivity.gSheetUpload;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.replGroup;
import static biz.riopapa.chatread.MainActivity.replGroupCnt;
import static biz.riopapa.chatread.MainActivity.replLong;
import static biz.riopapa.chatread.MainActivity.replShort;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.tableListFile;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.models.KakaoLog;
import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.SWho;
import biz.riopapa.chatread.stocks.StockName;

public class SelectChats {

    String[] whos, whoFs;
    String[] keywords, keyword1, keyword2, prevs, nexts;
    boolean upload;
    ArrayList<String> msgLines;
    SGroup sGroup = null;
    SWho sWho = null;

    public KakaoLog generate(File chatFile, boolean upload) {
        KakaoLog kalog = new KakaoLog();
        kalog.idx = -1;   // not found group

        this.upload = upload;
        String[] chatLines = tableListFile.readRaw(chatFile);
        if (chatLines == null) {
            kalog.grp = "Nothing";
            kalog.ss = new SpannableString("No Lines on "+chatFile.getName());
            return kalog;
        }
        String chatHeader = chatLines[0].replaceAll("[\uFEFC-\uFEFF]","").trim();
        int p2 = chatHeader.indexOf(" 카카오톡 대화");
        int p1 = chatHeader.lastIndexOf(" ", p2-2);
        if (p1 < 0 || p2 < 0) {
            Toast.makeText(mContext, "Invalid chat file", Toast.LENGTH_SHORT).show();
            kalog.ss = new SpannableString("Invalid chat file p1="+p1+" p2="+p2);
            return kalog;
        }
        kalog.grpF = chatHeader.substring(0,p1);
        for (int i = 1; i < sGroups.size(); i++) {
            if (kalog.grpF.contains(sGroups.get(i).grpF)) {
                kalog.grp = sGroups.get(i).grp;
                kalog.idx = i;
                sGroup = sGroups.get(i);
                break;
            }
        }
        if (kalog.idx == -1)
            return kalog;

        getWhoKeyList();

        StringBuilder headStr = new StringBuilder();

        headStr.append("그룹 : ").append(sGroup.grp).append(" : ").append(sGroup.grpF).append("\n");
        headStr.append(new GoogleStatement().make(sGroup,"\n    "));
        headStr.append("\nstrReplaces ---\n\n");
        for (int i = 0; i < replGroupCnt; i++) {
            int compared = kalog.grp.compareTo(replGroup[i]);
            if (compared == 0) {
                for (int j = 0; j < replLong[i].length; j++)
                    headStr.append(replShort[i][j]).append(" > ").append(replLong[i][j]).append("\n");
            }
        }

        msgLines = new ArrayList<>();     // message lines chosen
        StringBuilder mSb = new StringBuilder();
        for (String ln : chatLines) {
            if (ln.startsWith("202")) {
                String txt = mSb.toString();
                if (txt.contains(" : ") && txt.length() > 20)
                    msgLines.add(txt.substring(6));
                mSb = new StringBuilder(ln);
            } else {
                mSb.append("|").append(ln);
            }
        }

        String prvTxt = "";
        SpannableString matchedSS = new SpannableString("\nMatched Lines ---\n\n");
        SpannableString selectedSS = new SpannableString("\nSelected Lines ---\n\n");

        for (String txt: msgLines) {
            if (txt.equals(prvTxt) || canIgnore(txt))
                continue;
            if (txt.contains(sGroup.skip1) || txt.contains(sGroup.skip2) ||
                    txt.contains(sGroup.skip3) || txt.length() < 40)
                continue;
            prvTxt = txt;
            int p = txt.indexOf(", ");
            if (p < 0)
                continue;
            String time = txt.substring(0,p);
            String tmp = txt.substring(p+2);
            p = tmp.indexOf(":")-1;
            if (p < 0)
                continue;
            String kWhoF = tmp.substring(0, p).trim();
            String thisLine = tmp.substring(3+kWhoF.length());
            boolean whoFound = false;
            for (String whoF : whoFs) {
                if (kWhoF.contains(whoF)) {
                    whoFound = true;
                    break;
                }
            }
            boolean keyFound = false;
            for (String keyword : keywords) {
                if (thisLine.contains(keyword)) {
                    keyFound = true;
                    break;
                }
            }
            if (!keyFound)
                continue;
            boolean key1Found = false;
            for (int i = 0; i < keyword1.length; i++) {
                if (thisLine.contains(keyword1[i]) && thisLine.contains(keyword2[i])) {
                    key1Found = true;
                    break;
                }
            }
            boolean found = whoFound || key1Found;
            if (found) { //  || inWhoList(txt)) {
                thisLine = cutTail(strUtil.removeSpecialChars(thisLine));
                SpannableString ssLine = key2Matched(time, sWho.who, thisLine, upload);
                if (ssLine.length() > 10) {
                    matchedSS = concatSS(matchedSS, ssLine);
                    selectedSS = new SpannableString(TextUtils.concat(selectedSS,
                            key2Matched(time, sWho.who, thisLine, false)));
                } else
                    selectedSS = concatSS(selectedSS, checkKeywords(time+", "+kWhoF+" , "+thisLine));
            } else if (inWhoList(kWhoF)) {
                selectedSS = concatSS(selectedSS, new SpannableString("▣ "+time+" , "+kWhoF
                        +" , "+ cutTail(thisLine)+"\n\n"));
            } else if (hasKeywords(txt)) {
                selectedSS = concatSS(selectedSS, checkKeywords(time+", "+kWhoF+" , "+thisLine));
            }
        }
        if (upload)
            Toast.makeText(mContext, " Uploaded to google", Toast.LENGTH_SHORT).show();

        kalog.ss =  new SpannableString(TextUtils.concat(new SpannableString(headStr+"\n"),
                matchedSS, selectedSS));
        return kalog;
    }
    private String cutTail(String txt) {
        if (txt.length() > 90)
            return txt.substring(0, 90) + " ⋙";
        return txt;
    }

    final String [] chatIgnores = {"http", "항셍", "나스닥", "무료", "반갑", "발동", "사진", "상담",
            "입장", "파생", "프로필" };

    boolean canIgnore(String mergedLine) {
        for (String igWord : chatIgnores) {
            if (mergedLine.contains(igWord))
                return true;
        }
        return false;
    }

    boolean inWhoList (String ln) {
        for (String whoList : whos) {
            if (ln.contains(whoList))
                return true;
        }
        return false;
    }

    boolean hasKeywords (String ln) {
        for (String k : keywords) {
            if (ln.contains(k))
                return true;
        }
        return false;
    }

    SpannableString checkKeywords(String text) {
        SpannableString s = new SpannableString(text+"\n\n");
        s.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.keyTextFore, null)), 0, s.length()-1, SPAN_EXCLUSIVE_EXCLUSIVE);
        for (String keyword : keywords) {
            int p = text.indexOf(keyword);
            if (p > 0) {
                s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedBack, null)), p, p + keyword.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }

    SpannableString key2Matched(String time, String who, String thisLine, boolean upload) {
        int p1, p2;
        for (int k = 0; k < keyword1.length; k++) {
            p1 = thisLine.indexOf(keyword1[k]);
            if (p1 >= 0) {
                p2 = thisLine.indexOf(keyword2[k], p1+1);
                if (p2 >= 0) {      // both matched
                    thisLine = strUtil.strShorten(sGroup.grp, thisLine);
                    String [] sNames = new StockName().get(prevs[k], nexts[k], thisLine);
                    String keys = "<"+keyword1[k]+"~"+keyword2[k]+">";
                    String str = sNames[0]+" "+ time+", "+who+" , "+ sNames[1] + " " + keys;
                    SpannableString s = new SpannableString(str+"\n\n");
                    s.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.keyTextFore, null)), 0, s.length()-1, SPAN_EXCLUSIVE_EXCLUSIVE);
                    s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedBack, null)), 0, s.length()-1, SPAN_EXCLUSIVE_EXCLUSIVE);
                    s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.tabBackSelected, null)), 0, sNames[0].length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (str.contains(whoFs[k]))
                        s.setSpan(new UnderlineSpan(), 0, s.length()-1, SPAN_EXCLUSIVE_EXCLUSIVE);
                    p1 = str.indexOf(prevs[k]);
                    if (p1 >= 0) {
                        s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedWord, null)), p1, p1 + prevs[k].length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    p2 = str.indexOf(nexts[k], p1+1);
                    if (p2 >= 0) {
                        s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedWord, null)), p2, p2 + nexts[k].length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if (upload) {
                        String [] strs = new StockName().get(prevs[k], nexts[k], thisLine);
                        gSheetUpload.add2Stock(sGroup.grp, who, "chats", strs[0],
                                strs[1], keys, time);
                    }
                    return s;
                }
            }
        }
        return new SpannableString("");
    }

    SpannableString concatSS(SpannableString s1, SpannableString s2) {
        return new SpannableString(TextUtils.concat(s1, s2));
    }

    void getWhoKeyList() {
        ArrayList<String> aWhos = new ArrayList<>();
        ArrayList<String> aWhoFs = new ArrayList<>();
        ArrayList<String> aKeywords = new ArrayList<>();
        ArrayList<String> aKey1 = new ArrayList<>();
        ArrayList<String> aKey2 = new ArrayList<>();
        ArrayList<String> aPrev = new ArrayList<>();
        ArrayList<String> aNext = new ArrayList<>();

        for (int w = 0; w < sGroup.whos.size(); w++) {
            sWho = sGroup.whos.get(w);
            aWhos.add(sWho.who.trim());
            aWhoFs.add(sWho.whoF.trim());

            for (int s = 0; s < sWho.stocks.size(); s++) {
                aKey1.add(sWho.stocks.get(s).key1);
                aKey2.add(sWho.stocks.get(s).key2);
                aPrev.add(sWho.stocks.get(s).prv);
                aNext.add(sWho.stocks.get(s).nxt);
            }
        }

        aKeywords.add("매도"); aKeywords.add("매수");
        aKeywords.add("자율");aKeywords.add("종목");
        aKeywords.addAll(aKey1);
        aKeywords.addAll(aKey2);

        Collections.sort(aKeywords);
        for (int i = 1; i < aKeywords.size();) {
            if (aKeywords.get(i).equals(aKeywords.get(i-1))) {
                aKeywords.remove(i);
            } else
                i++;
        }
        whos = aWhos.toArray(new String[0]);
        whoFs = aWhoFs.toArray(new String[0]);
        keywords = aKeywords.toArray(new String[0]);
        keyword1 = aKey1.toArray(new String[0]);
        keyword2 = aKey2.toArray(new String[0]);
        prevs = aPrev.toArray(new String[0]);
        nexts = aNext.toArray(new String[0]);
    }
}
