package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.logQue;
import static biz.riopapa.chatread.MainActivity.sharedEditor;
import static biz.riopapa.chatread.MainActivity.utils;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import biz.riopapa.chatread.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GSheetUpload {

    public GSheetUpload() {
        sheetQues = new ArrayList<>();
    }
    static class SheetQue {

        String group, timeStamp, who, percent, talk, statement, key12;

        SheetQue(String group, String timeStamp, String who, String percent, String talk, String statement, String key12) {
            this.group = group; this.timeStamp = timeStamp; this.who = who; this.percent = percent;
            this.talk = talk; this.statement = statement; this.key12 = key12;
        }
    }

    static ArrayList<SheetQue> sheetQues = null;
    static boolean nowUploading = false;
    public void initSheetQue() {
        sheetQues = new ArrayList<>();
    }

    public void add2Stock(String group, String timeStamp, String who, String percent,
                          String talk, String statement, String key12) {
        if (statement.length() > 120)
            statement = statement.substring(0, 120);
        sheetQues.add(new SheetQue(group, timeStamp, who, percent, talk, statement, key12));
        uploadStock();
    }
    public void uploadStock() {
//        if (sheetQues.isEmpty() || nowUploading ) //  || WifiMonitor.wifiName.equals(none))
//            return;
//        nowUploading = true;
        if (sheetQues.isEmpty())
            return;
        Thread thread = new Thread(() -> {
            SheetQue que = sheetQues.get(0);
            sheetQues.remove(0);
            String action = "stock";
            String group = que.group, timeStamp = que.timeStamp, who = que.who;
            String percent = que.percent; //  + "Q"+hourMinFormat.format(System.currentTimeMillis());
            String talk = que.talk; String statement = que.statement; String key12 = que.key12;
            JSONObject valueRange = new JSONObject();
            JSONArray values = new JSONArray();
            values.put(new JSONArray().put(action).put(group).put(timeStamp).put(who)
                    .put(percent).put(talk).put(statement).put(key12)
            );
            post2GSheet(valueRange);
        });
        thread.start();

    }

    public void uploadGroupInfo(String group, String who, String percent, String talk, String statement) {
        nowUploading = true;
    }


    void post2GSheet(JSONObject valueRange) {

        final String SPREADSHEET_ID = Resources.getSystem().getString(R.string.sheets_stock);

        OkHttpClient client = new OkHttpClient();

        String jsonBody = valueRange.toString();

        // Build the request
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
        Request request = new Request.Builder()
                .url(SPREADSHEET_ID)
                .post(requestBody)
                .build();
        utils.logW("post2GSheet","uploading "+jsonBody);
        // Execute request asynchronously
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String errStr = "stock Upload Fail "+ jsonBody + "\n"+e;
                utils.logE("gSheet",errStr);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    utils.logW("gSheet Done",jsonBody);
                } else {
                    Log.e("SheetsWriter", "Error writing data: " + response.body().string());
                }
            }
        });
    }


}