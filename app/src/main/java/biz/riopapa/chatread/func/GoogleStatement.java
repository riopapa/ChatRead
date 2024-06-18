package biz.riopapa.chatread.func;

import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.SStock;
import biz.riopapa.chatread.models.SWho;

public class GoogleStatement {

    public String make(SGroup nGroup, String newLine) {

        StringBuilder sb = new StringBuilder();
        for (SWho who : nGroup.whos) {
            if (sb.length() > 0)
                sb.append("\n");
            sb.append(who.who).append(" : ").append(who.whoM);
            for (SStock stock : who.stocks) {
                sb.append("\n   Key(").append(stock.key1).append(", ").append(stock.key2).append(")");
                sb.append(",Talk(").append(stock.talk).append(")");
                sb.append(",Skip(").append(stock.skip1).append(")");
                sb.append(newLine);
                sb.append("Prv/Nxt(").append(stock.prv).append("/").append(stock.nxt).append(")");
                sb.append(",Count(").append(stock.count).append(")");
            }
        }
        return sb.toString();
    }
}
