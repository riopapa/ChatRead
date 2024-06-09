package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.nowSGroup;

import biz.riopapa.chatread.models.SStock;
import biz.riopapa.chatread.models.SWho;

public class GoogleStatement {

    public String make() {

        StringBuilder sb = new StringBuilder();
        for (SWho who : nowSGroup.whos) {
            sb.append(who.who);
            for (SStock stock : who.stocks) {
                if (sb.length() > 0)
                    sb.append("\n");
                sb.append(", Key (").append(stock.key1).append(", ").append(stock.key2).append(")");
                sb.append(", Talk (").append(stock.talk).append(")");
                sb.append(", Skip (").append(stock.skip1).append(")");
                sb.append(", Prv/Nxt (").append(stock.prv).append("/").append(stock.nxt).append(")");
                sb.append(", Count (").append(stock.count).append(")");
            }
        }
        return sb.toString();
    }

}
