package biz.riopapa.chatread.stocks;

import static biz.riopapa.chatread.MainActivity.utils;

public class StockName {

    // returns stockname, and dot added iText
    public String[] get(String prevKey, String nextKey, String iText) {
        String str = iText;
        int p1 = iText.indexOf(prevKey);
//        utils.logB("getStockName", "p1 index="+p1+", ["+prevKey+"/"+nextKey+"], "
//                +iText);
        if (p1 >= 0) {
            p1 += prevKey.length();
            int p2 = str.indexOf(nextKey, p1);
            String sName;
            if (p2 > 0) {
//                sName = str.substring(p1, p2).replaceAll(shorten, "").trim();
                sName = str.substring(p1, p2).replaceAll("[^a-zA-Z가-힝]", "").trim();
                if (sName.length() > 8)
                    sName = sName.substring(0,8);
                str = str.substring(0, p1) + " " +
                        new StringBuffer(sName).insert(1, ".") + " " +
                        str.substring(p2);
            } else {    // if second keyword not found then just before blank found

                p1 = p1 + 1;
                while (true) {  // skip white
                    char ch = str.charAt(p1);
                    if (ch >= 0xAC00 && ch <= 0xD7A3)
                        break;
                    if (ch >= 'A' && ch <= 'Z')
                        break;
                    p1++;
                }
                p2 = p1 + 2;
                while (true) {  // until valid chars
                    char ch = str.charAt(p2);
                    if ((ch >= 0xAC00 && ch <= 0xD7A3) ||
                            (ch >= 'A' && ch <= 'Z')) {
                        p2++;
                        continue;
                    }
                    break;
                }
                sName = str.substring(p1,p2);
                String sNameDot = sName + "s";
                if (sName.length() > 3)
                    sNameDot = new StringBuffer(sName).insert(1, ".").toString();
                str = str.substring(0, p1) + " " + sNameDot + " " +
                        str.substring(p1+10);
            }
            return new String[]{sName, str};
        }
        return new String[]{"noPrv", str};
    }
}
