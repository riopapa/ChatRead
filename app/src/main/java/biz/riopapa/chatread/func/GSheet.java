package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.utils;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.SStock;
import biz.riopapa.chatread.models.SWho;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GSheet {

    static class SheetQue {

        String action, group, timeStamp, who, percent, talk, statement, key12;

        SheetQue(String action, String group, String timeStamp, String who, String percent, String talk, String statement, String key12) {
            this.action = action; this.group = group; this.timeStamp = timeStamp; this.who = who;
            this.percent = percent; this.talk = talk; this.statement = statement; this.key12 = key12;
        }
    }

    static ArrayList<SheetQue> sheetQues = null;

    public GSheet() {
        sheetQues = new ArrayList<>();
    }

    public void add2Stock(String group, String timeStamp, String who, String percent,
                          String talk, String statement, String key12) {
        if (statement.length() > 120)
            statement = statement.substring(0, 120);
        sheetQues.add(new SheetQue("stock", group, timeStamp, who, percent, talk, statement, key12));
        uploadStock();
    }

    public void updateGSheetGroup(SGroup sGroup) {
        String mPercent = gSheet.makePercent(sGroup);
        String mStatement = gSheet.makeStatement(sGroup,",");
        String mTalk = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.KOREA).format(new Date());
        sheetQues.add(new SheetQue("group", sGroup.grp,  mTalk, sGroup.grpF, mPercent, mTalk, mStatement, "key12"));
        uploadStock();
    }

    public void deleteGSheetGroup(SGroup sGroup) {
        String time = new SimpleDateFormat(".MM/dd HH:mm", Locale.KOREA).format(new Date());
        String mWho = "\n삭제됨\n" + sGroup.grpF + "\n" + time;
        String mPercent = "\n삭제됨\n" +gSheet.makePercent(sGroup) + "\n" + time;
        String mStatement = gSheet.makeStatement(sGroup,",");
        sheetQues.add(new SheetQue("group", sGroup.grp, time, mWho, mPercent, time, mStatement, "key12"));
        uploadStock();
    }

    void uploadStock() {

        while (!sheetQues.isEmpty()) {
            SheetQue que = sheetQues.get(0);
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("action", que.action);
            formBuilder.add("group", que.group);
            formBuilder.add("timeStamp", que.timeStamp);
            formBuilder.add("who", que.who);
            formBuilder.add("percent", que.percent);
            formBuilder.add("talk", que.talk);
            formBuilder.add("statement", que.statement);
            formBuilder.add("key12", que.key12);
            post2GSheet(formBuilder);
            sheetQues.remove(0);
        }
    }

    void post2GSheet(FormBody.Builder formBuilder) {

        String SPREADSHEET_ID = mContext.getString(R.string.sheets_stock);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = formBuilder.build();
        Request request = new Request.Builder()
                .url(SPREADSHEET_ID)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String errStr = "stock Upload Fail "+ formBuilder + "\n"+e;
                utils.logE("gSheet",errStr);
            }
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) {
//                if (!response.isSuccessful()){
//                    utils.logW("gSheet", "Error: " + response.body());
//                }
//            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) { // Try-with-resources here
                    if (!response.isSuccessful()) {
                        utils.logE("gSheet", "Error Replied : " + responseBody.string()); // Consume body if needed
                    } else {
                        utils.logW("gSheet", "Success Replied : " + responseBody.string());
                        // Process successful response here, consuming responseBody if needed
                    }
                }
            }
        });
    }

    String makePercent(SGroup sGroup) {
        return "Skip (" + sGroup.skip1 + ", " + sGroup.skip2 + ", " + sGroup.skip3 + ")" +
                "\nActive:" + ((sGroup.active) ? "yes" : "no") +
                " TelKa (" + sGroup.telKa + ")";
    }

    public String makeStatement(SGroup nGroup, String newLine) {
        StringBuilder sb = new StringBuilder();
        for (SWho who : nGroup.whos) {
            if (sb.length() > 0)
                sb.append("\n");
            sb.append(who.who).append(" : ").append(who.whoM).append(", ").append(who.whoF);
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