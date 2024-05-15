package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;

import biz.riopapa.chatread.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GSheetUpload {

    public GSheetUpload() {
        sheetQues = new ArrayList<>();
    }
    static class SheetQue {

        String action, group, timeStamp, who, percent, talk, statement, key12;

        SheetQue(String action, String group, String timeStamp, String who, String percent, String talk, String statement, String key12) {
            this.action = action; this.group = group; this.timeStamp = timeStamp; this.who = who;
            this.percent = percent; this.talk = talk; this.statement = statement; this.key12 = key12;
        }
    }

    static ArrayList<SheetQue> sheetQues = null;

    public void add2Stock(String group, String timeStamp, String who, String percent,
                          String talk, String statement, String key12) {
        if (statement.length() > 120)
            statement = statement.substring(0, 120);
        sheetQues.add(new SheetQue("stock", group, timeStamp, who, percent, talk, statement, key12));
        uploadStock();
    }
    public void uploadStock() {
        if (sheetQues.isEmpty())
            return;
        Thread thread = new Thread(() -> {
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
            if (!sheetQues.isEmpty())
                uploadStock();
        });
        thread.start();

    }

    public void uploadGroupInfo(String group, String timeStamp, String who, String percent,
                String talk, String statement, String key12) {
        sheetQues.add(new SheetQue("group", group, timeStamp, who, percent, talk, statement, key12));
        uploadStock();
    }
    
    void post2GSheet(FormBody.Builder formBuilder) {

        String SPREADSHEET_ID = mContext.getString(R.string.sheets_stock);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = formBuilder.build();
        Request request = new Request.Builder()
                .url(SPREADSHEET_ID)
                .post(requestBody)
                .build();
        utils.logW("post2GSheet","uploading "+formBuilder);
        // Execute request asynchronously
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String errStr = "stock Upload Fail "+ formBuilder + "\n"+e;
                utils.logE("gSheet",errStr);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    utils.logW("gSheet Done",response.message());
                } else {
                    Log.e("SheetsWriter", "Error writing data: " + response.body().string());
                }
            }
        });
    }


}