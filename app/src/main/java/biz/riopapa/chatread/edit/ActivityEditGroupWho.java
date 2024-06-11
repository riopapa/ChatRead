package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.gIdx;
import static biz.riopapa.chatread.MainActivity.gSheetUpload;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.nowSWho;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.groupWhoAdapter;
import static biz.riopapa.chatread.MainActivity.groupWhoStockAdapter;
import static biz.riopapa.chatread.MainActivity.groupsAdapter;
import static biz.riopapa.chatread.MainActivity.toolbar;
import static biz.riopapa.chatread.MainActivity.wIdx;

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

public class ActivityEditGroupWho extends AppCompatActivity {

    TextView tGroup, tGroupF, tSkip1, tSkip2, tSkip3;
    TextView tTelKa;
    EditText eWho, eWhoF;
    SwitchCompat sIgnore;
    String mPercent, mStatement, mTalk;
    View deleteMenu;
    RecyclerView recyclerView;
    final String GROUP = ")_(";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_who);
//        Toolbar toolbar = findViewById(R.id.toolbar_edit_group);
        if (toolbar == null) {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(0xFF44FF33);
            toolbar.setSubtitleTextColor(0xFF000000);
            toolbar.setSubtitle("SStock Group Edit");
        }

        tGroup = findViewById(R.id.who_group);
        tGroupF = findViewById(R.id.who_group_full);
        tTelKa = findViewById(R.id.who_telka);
        sIgnore = findViewById(R.id.who_ignore);
        tSkip1 = findViewById(R.id.who_skip1);
        tSkip2 = findViewById(R.id.who_skip2);
        tSkip3 = findViewById(R.id.who_skip3);
        eWho = findViewById(R.id.who_who);
        eWhoF = findViewById(R.id.who_who_full);

        tGroup.setText(nowSGroup.grp);
        tGroupF.setText(nowSGroup.grpF);
        tTelKa.setText(telka2String(nowSGroup.telKa));
        sIgnore.setChecked(nowSGroup.ignore);
        tSkip1.setText(nowSGroup.skip1);
        tSkip2.setText(nowSGroup.skip2);
        tSkip3.setText(nowSGroup.skip3);
        eWho.setText(nowSWho.who);
        eWhoF.setText(nowSWho.whoF);

        setRecycler();
    }

    private void setRecycler() {
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
                setLongClick();
            }
        });
        return true;
    }

    private void setLongClick() {
        deleteMenu.setOnLongClickListener(v -> {
            deleteStockWhoGroup();
            return true;
        });
    }

    private void deleteStockWhoGroup() {

        String time = new SimpleDateFormat(".MM/dd HH:mm", Locale.KOREA).format(new Date());
        mStatement = new GoogleStatement().make();
        String mWho = "\n삭제됨\n" + nowSGroup.grpF + "\n" + time;
        String mPercent = "\n삭제됨\n" + new GooglePercent().make() + "\n" + time;
        nowSGroup.whos.remove(wIdx);
        sGroups.set(gIdx, nowSGroup);
        stockGetPut.put( " Deleted "+ mWho);
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
        nowSGroup.whos.add(nowSWho);
        sGroups.set(gIdx, nowSGroup);
        stockGetPut.sort();
        Toast.makeText(mContext,"Duplicated "+ nowSGroup.grp+" / " + nowSGroup.grpF, Toast.LENGTH_SHORT).show();
        groupWhoAdapter.notifyDataSetChanged();
        finish();
    }

    private void saveGroupWho() {
        String who = eWho.getText().toString();
        String whoF = eWhoF.getText().toString();
        nowSWho.who = who;
        nowSWho.whoF = whoF;
        nowSGroup.whos.set(wIdx, nowSWho);
        sGroups.set(gIdx, nowSGroup);
        stockGetPut.put("group");

        groupWhoAdapter.notifyDataSetChanged();

        mPercent = new GooglePercent().make();
        mStatement = new GoogleStatement().make();
        mTalk = new SimpleDateFormat("yy/MM/dd\nHH:mm", Locale.KOREA).format(new Date());
        gSheetUpload.uploadGroupInfo(nowSGroup.grp, GROUP, nowSGroup.grpF, mPercent,
                mTalk, mStatement, "key12");
        finish();
    }

    String telka2String(char c) {
        return (c == 't') ? "tel" : (c == 'k') ? "ka" : "";
    }

}