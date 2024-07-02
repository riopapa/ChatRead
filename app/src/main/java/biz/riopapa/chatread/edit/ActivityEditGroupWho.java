package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.gIDX;
import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.groupWhoAdapter;
import static biz.riopapa.chatread.MainActivity.groupWhoStockAdapter;
import static biz.riopapa.chatread.MainActivity.groupAdapter;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.nowSWho;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.wIDX;

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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.GroupWhoStockAdapter;
import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.SWho;

public class ActivityEditGroupWho extends AppCompatActivity {

    TextView tGroup, tGroupM, tGroupF, tSkip1, tSkip2, tSkip3;
    TextView tTelKa;
    EditText eWho, eWhoM, eWhoF;
    SwitchCompat sIgnore;
    View deleteMenu;
    RecyclerView recyclerView;
    final String GROUP = ")_(";
    public static Activity whoActivity;
    public static Context whoContext;
    SWho newWho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_who);
        whoActivity = this;
        whoContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        nowSWho = nowSGroup.whos.get(wIDX);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(nowSGroup.grp+":"+nowSGroup.grpF);
        actionBar.setSubtitle(nowSWho.who+":"+nowSWho.whoF);

        try {
            newWho = (SWho) nowSGroup.whos.get(wIDX).clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

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
        recyclerView.setAdapter(groupWhoStockAdapter);    }

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

        gSheet.updateGSheetGroup(nowSGroup);
        nowSGroup.whos.remove(wIDX);
        try {
            sGroups.set(gIDX, (SGroup) nowSGroup.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        stockGetPut.put( " Deleted "+ nowSWho.who+" / " + nowSWho.whoM);
        stockGetPut.get();
        updateAdaptor();
        finish();
    }

    private static void updateAdaptor() {
        for (int i = 0; i < nowSWho.stocks.size(); i++)
            groupWhoStockAdapter.notifyItemChanged(i);
        for (int i = 0; i < nowSGroup.whos.size(); i++)
            groupWhoAdapter.notifyItemChanged(i);
        for (int i = 0; i < sGroups.size(); i++)
            groupAdapter.notifyItemChanged(i);
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

        newWho.who = eWho.getText().toString();
        newWho.whoM = eWhoM.getText().toString();
        newWho.whoF = eWhoF.getText().toString();
        nowSGroup.whos.add(newWho);
        sGroups.set(gIDX, nowSGroup);
        stockGetPut.put("Dup who "+ newWho.who+" / " + newWho.whoM);
        stockGetPut.get();
        updateAdaptor();
        finish();
    }

    void saveGroupWho() {

        newWho.who = eWho.getText().toString();
        newWho.whoM = eWhoM.getText().toString();
        newWho.whoF = eWhoF.getText().toString();
        nowSGroup.whos.set(wIDX, newWho);
        nowSWho = nowSGroup.whos.get(wIDX);
        sGroups.set(gIDX, nowSGroup);
        stockGetPut.put("Save Who "+ newWho.who+" / " + newWho.whoM);
        stockGetPut.get();
        updateAdaptor();
        gSheet.updateGSheetGroup(nowSGroup);
        finish();
    }
}