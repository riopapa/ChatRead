package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.gIdx;
import static biz.riopapa.chatread.MainActivity.gSheetUpload;
import static biz.riopapa.chatread.MainActivity.groupWhoAdapter;
import static biz.riopapa.chatread.MainActivity.groupWhoStockAdapter;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.nowSWho;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.toolbar;
import static biz.riopapa.chatread.MainActivity.wIdx;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.GroupWhoStockAdapter;
import biz.riopapa.chatread.func.GooglePercent;
import biz.riopapa.chatread.func.GoogleStatement;
import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.SWho;

public class ActivityEditGroupWho extends AppCompatActivity {

    TextView tGroup, tGroupM, tGroupF, tSkip1, tSkip2, tSkip3;
    TextView tTelKa;
    EditText eWho, eWhoM, eWhoF;
    SwitchCompat sIgnore;
    String mPercent, mStatement, mTalk;
    View deleteMenu;
    RecyclerView recyclerView;
    final String GROUP = ")_(";
    public static Activity whoActivity;
    public static Context whoContext;
    SWho nWho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_who);
        whoActivity = this;
        whoContext = this;

        tGroup = findViewById(R.id.who_group);
        tGroupM = findViewById(R.id.who_group_match);
        tGroupF = findViewById(R.id.who_group_full);
        tTelKa = findViewById(R.id.who_telka);
        sIgnore = findViewById(R.id.who_ignore);
        tSkip1 = findViewById(R.id.who_skip1);
        tSkip2 = findViewById(R.id.who_skip2);
        tSkip3 = findViewById(R.id.who_skip3);
        eWho = findViewById(R.id.who_who);
        eWhoM = findViewById(R.id.who_who_match);
        eWhoF = findViewById(R.id.who_who_full);

        tGroup.setText(nowSGroup.grp);
        tGroupM.setText(nowSGroup.grpM);
        tGroupF.setText(nowSGroup.grpF);
        tTelKa.setText(nowSGroup.telKa);
        sIgnore.setChecked(nowSGroup.ignore);
        tSkip1.setText(nowSGroup.skip1);
        tSkip2.setText(nowSGroup.skip2);
        tSkip3.setText(nowSGroup.skip3);
        eWho.setText(nowSWho.who);
        eWhoM.setText(nowSWho.whoM);
        eWhoF.setText(nowSWho.whoF);

        groupWhoStockAdapter = new GroupWhoStockAdapter();
        recyclerView = findViewById(R.id.recycle_who_stocks);
        recyclerView.setAdapter(groupWhoStockAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_edit, menu);
        new Handler(Looper.getMainLooper()).post(() -> {
            deleteMenu = this.findViewById(R.id.delete_this);
            if (deleteMenu != null) {
                deleteMenu.setOnLongClickListener(v -> {
                    deleteStockWhoGroup();
                    return true;
                });
            }
        });
        return true;
    }

    void deleteStockWhoGroup() {
        if (nowSGroup.whos.size() < 2) {
            Toast.makeText(this, "하나 밖에 없어 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String time = new SimpleDateFormat(".MM/dd HH:mm", Locale.KOREA).format(new Date());
        mStatement = new GoogleStatement().make(nowSGroup,",");
        String mWho = "\n삭제됨\n" + nowSGroup.grpM + "\n" + time;
        String mPercent = "\n삭제됨\n" + new GooglePercent().make(nowSGroup) + "\n" + time;
        nowSGroup.whos.remove(wIdx);
        try {
            sGroups.set(gIdx, (SGroup) nowSGroup.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        stockGetPut.put( " Deleted "+ mWho);
        stockGetPut.get();
        groupWhoAdapter.notifyDataSetChanged();
        gSheetUpload.uploadGroupInfo(nowSGroup.grp, "timeStamp", mWho, mPercent,
                time, mStatement, "key12");
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_this) {
            saveGroupWho();

        } else if (item.getItemId() == R.id.duplicate_this) {
            duplicateGroupWho();
        }
        return super.onOptionsItemSelected(item);
    }

private void duplicateGroupWho() {
        try {
            nWho =  (SWho) nowSWho.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        nWho.who = eWho.getText().toString();
        nWho.whoM = eWhoM.getText().toString();
        nWho.whoF = eWhoF.getText().toString();

        nowSGroup.whos.add(wIdx, nWho);
        nowSWho = nowSGroup.whos.get(wIdx);
        try {
        sGroups.set(gIdx, (SGroup) nowSGroup.clone());
        } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
        }
        nowSGroup = sGroups.get(gIdx);
        stockGetPut.put("Dup who "+ nWho.who+" / " + nWho.whoM);
        stockGetPut.get();
        groupWhoAdapter.notifyDataSetChanged();
        finish();
    }

    void saveGroupWho() {
        try {
            nWho = (SWho) nowSWho.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        nWho.who = eWho.getText().toString();
        nWho.whoM = eWhoM.getText().toString();
        nWho.whoF = eWhoF.getText().toString();
        nowSGroup.whos.set(wIdx, nWho);
        nowSWho = nowSGroup.whos.get(wIdx);
        try {
            sGroups.set(gIdx, (SGroup) nowSGroup.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        nowSGroup = sGroups.get(gIdx);
        stockGetPut.put("Save Who "+ nWho.who+" / " + nWho.whoM);
        stockGetPut.get();
        groupWhoAdapter.notifyDataSetChanged();

        mPercent = new GooglePercent().make(nowSGroup);
        mStatement = new GoogleStatement().make(nowSGroup,",");
        mTalk = new SimpleDateFormat("yy/MM/dd\nHH:mm", Locale.KOREA).format(new Date());
        gSheetUpload.uploadGroupInfo(nowSGroup.grp, GROUP, nowSGroup.grpM, mPercent,
                mTalk, mStatement, "key12");
        finish();
    }
}