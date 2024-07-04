package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.gIDX;
import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.whoAdapter;
import static biz.riopapa.chatread.MainActivity.stockAdapter;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.nowSWho;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.stockRecyclerView;
import static biz.riopapa.chatread.MainActivity.wIDX;
import static biz.riopapa.chatread.MainActivity.whoRecyclerView;

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

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.WhoAdapter;
import biz.riopapa.chatread.adapters.StockAdapter;
import biz.riopapa.chatread.models.SWho;

public class ActivityEditGroupWho extends AppCompatActivity {

    TextView tGroup, tGroupM, tGroupF, tSkip1, tSkip2, tSkip3;
    TextView tTelKa;
    EditText eWho, eWhoM, eWhoF;
    SwitchCompat sIgnore;
    View deleteMenu;
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

        nowSWho = sGroups.get(gIDX).whos.get(wIDX);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(nowSGroup.grp+":"+nowSGroup.grpF);
        actionBar.setSubtitle(nowSWho.who+":"+nowSWho.whoF);

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

        nowSGroup = sGroups.get(gIDX);
        nowSWho = sGroups.get(gIDX).whos.get(wIDX);

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

        try {
            newWho = (SWho) nowSGroup.whos.get(wIDX).clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        stockAdapter = new StockAdapter();
        stockRecyclerView = findViewById(R.id.recycle_who_stocks);
        stockRecyclerView.setAdapter(stockAdapter);
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

        gSheet.updateGSheetGroup(nowSGroup);
        sGroups.get(gIDX).whos.remove(wIDX);
        stockGetPut.put( " Deleted "+ nowSWho.who+" / " + nowSWho.whoM);
        updateAdaptor();
        finish();
    }

    private void updateAdaptor() {
        stockAdapter = new StockAdapter();
        stockRecyclerView.setAdapter(stockAdapter);
        whoAdapter = new WhoAdapter();
        whoRecyclerView.setAdapter(whoAdapter);
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
        sGroups.get(gIDX).whos.add(newWho);
        stockGetPut.put("Dup who "+ newWho.who+" / " + newWho.whoM);
        updateAdaptor();
        finish();
    }

    void saveGroupWho() {

        newWho.who = eWho.getText().toString();
        newWho.whoM = eWhoM.getText().toString();
        newWho.whoF = eWhoF.getText().toString();
        sGroups.get(gIDX).whos.set(wIDX, newWho);
        stockGetPut.put("Save Who "+ newWho.who+" / " + newWho.whoM);
        updateAdaptor();
        gSheet.updateGSheetGroup(nowSGroup);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateAdaptor();
    }
}