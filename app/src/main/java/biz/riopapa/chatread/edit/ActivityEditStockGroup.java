package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.gIdx;
import static biz.riopapa.chatread.MainActivity.gSheetUpload;
import static biz.riopapa.chatread.MainActivity.mActivity;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sbnGroup;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.stockGroupsAdapter;

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
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.StockGroupWhoAdapter;
import biz.riopapa.chatread.func.GooglePercent;
import biz.riopapa.chatread.func.GoogleStatement;

public class ActivityEditStockGroup extends AppCompatActivity {

    EditText eGroup, eGroupF, eSkip1, eSkip2, eSkip3;
    TextView tTelKa;
    SwitchCompat sIgnore;
    String mGroup, mWho, mPercent, mStatement, mTalk;
    View deleteMenu;
    RecyclerView recyclerView;
    final String GROUP = ")_(";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        Toolbar toolbar = findViewById(R.id.toolbar_edit_group);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xFF44FF33);
        toolbar.setSubtitleTextColor(0xFF000000);
        toolbar.setSubtitle("SStock Group Edit");

        eGroup = findViewById(R.id.e_group);
        eGroupF = findViewById(R.id.e_group_full);
        tTelKa = findViewById(R.id.e_telka);
        sIgnore = findViewById(R.id.s_ignore);
        eSkip1 = findViewById(R.id.e_skip1);
        eSkip2 = findViewById(R.id.e_skip2);
        eSkip3 = findViewById(R.id.e_skip3);

        eGroup.setText(nowSGroup.grp);
        eGroupF.setText(nowSGroup.grpF);
        tTelKa.setText(telka2String(nowSGroup.telKa));
        sIgnore.setChecked(nowSGroup.ignore);
        eSkip1.setText(nowSGroup.skip1);
        eSkip2.setText(nowSGroup.skip2);
        eSkip3.setText(nowSGroup.skip3);

        stockGetPut.applyStockCounts();
        setRecycler();
    }

    private void setRecycler() {
        StockGroupWhoAdapter stockGroupsWhoAdapter = new StockGroupWhoAdapter();
        recyclerView = findViewById(R.id.recycle_whos);
        recyclerView.setAdapter(stockGroupsWhoAdapter);
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
            deleteStockGroup();
            return true;
        });
    }

    private void deleteStockGroup() {

        String time = new SimpleDateFormat(".MM/dd HH:mm", Locale.KOREA).format(new Date());
        mStatement = new GoogleStatement().make();
        String mWho = "\n삭제됨\n" + nowSGroup.grpF + "\n" + time;
        String mPercent = "\n삭제됨\n" + new GooglePercent().make() + "\n" + time;
        sGroups.remove(gIdx);
        stockGetPut.sort();
        stockGetPut.put( nowSGroup.grp+" Deleted "+ nowSGroup.grpF);
        stockGroupsAdapter.notifyDataSetChanged();
        gSheetUpload.uploadGroupInfo(nowSGroup.grp, "timeStamp", mWho, mPercent,
                time, mStatement, "key12");
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_this) {
            saveGroup();

        } else if (item.getItemId() == R.id.duplicate_this) {
            duplicateGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    private void duplicateGroup() {
        sGroups.add(nowSGroup);
        stockGetPut.sort();
        Toast.makeText(mContext,"Duplicated "+ nowSGroup.grp+" / " + nowSGroup.grpF, Toast.LENGTH_SHORT).show();
        stockGroupsAdapter.notifyDataSetChanged();
        finish();
    }

    private void saveGroup() {
        String group = eGroup.getText().toString();
        String groupF = eGroupF.getText().toString();
        String telKa = tTelKa.getText().toString();
        boolean ignore = sIgnore.isChecked();
        nowSGroup.grp = group;
        nowSGroup.grpF = groupF;
        nowSGroup.telKa = telKa.equals("t") ? 't' : (telKa.equals("k") ? 'k' : '_');
        nowSGroup.ignore = ignore;
        sGroups.set(gIdx, nowSGroup);

        stockGetPut.sort();
        stockGetPut.put("group");
        stockGetPut.setStockCounts();
        stockGroupsAdapter.notifyDataSetChanged();

        mPercent = new GooglePercent().make();
        mStatement = new GoogleStatement().make();
        mTalk = new SimpleDateFormat("yy/MM/dd\nHH:mm", Locale.KOREA).format(new Date());
        gSheetUpload.uploadGroupInfo(group, GROUP, groupF, mPercent,
                mTalk, mStatement, "key12");
        finish();
    }

    String telka2String(char c) {
        return (c == 't') ? "tel" : (c == 'k') ? "ka" : "";
    }
}