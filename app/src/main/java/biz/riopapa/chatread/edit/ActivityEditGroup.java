package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.gIdx;
import static biz.riopapa.chatread.MainActivity.gSheetUpload;
import static biz.riopapa.chatread.MainActivity.groupWhoAdapter;
import static biz.riopapa.chatread.MainActivity.groupsAdapter;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.toolbar;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.GroupWhoAdapter;
import biz.riopapa.chatread.func.GooglePercent;
import biz.riopapa.chatread.func.GoogleStatement;
import biz.riopapa.chatread.models.SGroup;

public class ActivityEditGroup extends AppCompatActivity {

    EditText eGroup, eGroupM, eGroupF, eSkip1, eSkip2, eSkip3, eRepl;
    TextView tTelKa;
    SwitchCompat sIgnore;
    String mPercent, mStatement, mTalk;
    View deleteMenu;
    RecyclerView recyclerView;
    public static AppCompatActivity groupActivity;
    final String GROUP = ")_(";
    final String NOTHING = "_n_";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
//        Toolbar toolbar = findViewById(R.id.toolbar_edit_group);
        if (toolbar != null) {
//            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(0xFF44FF33);
            toolbar.setSubtitleTextColor(0xFF000000);
            toolbar.setSubtitle("Group Edit");
        }
        groupActivity = this;

        eGroup = findViewById(R.id.e_group);
        eGroupM = findViewById(R.id.e_group_match);
        eGroupF = findViewById(R.id.e_group_full);
        tTelKa = findViewById(R.id.e_telka);
        sIgnore = findViewById(R.id.s_ignore);
        eSkip1 = findViewById(R.id.e_skip1);
        eSkip2 = findViewById(R.id.e_skip2);
        eSkip3 = findViewById(R.id.e_skip3);
        eRepl = findViewById(R.id.e_repl);

        eGroup.setText(nowSGroup.grp);
        eGroupM.setText(nowSGroup.grpM);
        eGroupF.setText(nowSGroup.grpF);
        tTelKa.setText(nowSGroup.telKa);
        sIgnore.setChecked(nowSGroup.ignore);
        eSkip1.setText(nowSGroup.skip1);
        eSkip2.setText(nowSGroup.skip2);
        eSkip3.setText(nowSGroup.skip3);
        tTelKa.setOnClickListener(v -> tTelKa.setText(nextTelKa(tTelKa.getText().toString())));
        if (nowSGroup.replF != null) {
            String s = "";
            for (int i = 0; i < nowSGroup.replF.size(); i++) {
                s += nowSGroup.replT.get(i) + " ^ " + nowSGroup.replF.get(i) + "\n\n";
            }
            eRepl.setText(s);
        } else
            eRepl.setText("");
        setRecycler();
    }

    String nextTelKa(String c) {
        switch (c) {
            case "t":
                return "k";
            case "k":
                return "s";
            case "s":
                return "_";
        }
        return "t";
    }

    private void setRecycler() {
        groupWhoAdapter = new GroupWhoAdapter();
        recyclerView = findViewById(R.id.recycle_whos);
        recyclerView.setAdapter(groupWhoAdapter);
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
        mStatement = new GoogleStatement().make(nowSGroup,",");
        String mWho = "\n삭제됨\n" + nowSGroup.grpM + "\n" + time;
        String mPercent = "\n삭제됨\n" + new GooglePercent().make(nowSGroup) + "\n" + time;
        sGroups.remove(gIdx);
        stockGetPut.put( nowSGroup.grp+" Deleted "+ nowSGroup.grpM);
        groupsAdapter.notifyDataSetChanged();
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
        try {
            SGroup nGroup =  (SGroup) nowSGroup.clone();

            nGroup.grp = eGroup.getText().toString();
            nGroup.grpM = eGroupM.getText().toString();
            nGroup.grpF = eGroupF.getText().toString();
            nGroup.telKa = tTelKa.getText().toString();
            nGroup.ignore = sIgnore.isChecked();
            nGroup.skip1 = eSkip1.getText().toString();
            nGroup.skip2 = eSkip2.getText().toString();
            nGroup.skip3 = eSkip3.getText().toString();
            if (nGroup.skip1.isEmpty())
                nGroup.skip1 = NOTHING;
            if (nGroup.skip2.isEmpty())
                nGroup.skip2 = NOTHING;
            if (nGroup.skip3.isEmpty())
                nGroup.skip3 = NOTHING;

            buildRepl(eRepl.getText().toString(), nGroup);

            sGroups.add(gIdx, nGroup);
            stockGetPut.put("group dup");
            stockGetPut.get();
            groupsAdapter.notifyDataSetChanged();
            mPercent = new GooglePercent().make(nGroup);
            mStatement = new GoogleStatement().make(nGroup,",");
            mTalk = new SimpleDateFormat("yy/MM/dd\nHH:mm", Locale.KOREA).format(new Date());
            gSheetUpload.uploadGroupInfo(nGroup.grp, GROUP, nGroup.grpM, mPercent,
                    mTalk, mStatement, "key12");
            finish();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildRepl(String s, SGroup nGroup) {
        String[] split = s.split("\n");
        nGroup.replT = new ArrayList<>();
        nGroup.replF = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            if (split[i].isEmpty())
                continue;
            String[] split2 = split[i].split("\\^");
            nGroup.replT.add(split2[0].trim());
            nGroup.replF.add(split2[1].trim());
        }
    }
    private void saveGroup() {
        try {
            SGroup nGroup =  (SGroup) nowSGroup.clone();
            nGroup.grp = eGroup.getText().toString();
            nGroup.grpM = eGroupM.getText().toString();
            nGroup.grpF = eGroupF.getText().toString();
            nGroup.telKa = tTelKa.getText().toString();
            nGroup.ignore = sIgnore.isChecked();
            nGroup.skip1 = eSkip1.getText().toString();
            nGroup.skip2 = eSkip2.getText().toString();
            nGroup.skip3 = eSkip3.getText().toString();
            if (nGroup.skip1.isEmpty())
                nGroup.skip1 = NOTHING;
            if (nGroup.skip2.isEmpty())
                nGroup.skip2 = NOTHING;
            if (nGroup.skip3.isEmpty())
                nGroup.skip3 = NOTHING;
            buildRepl(eRepl.getText().toString(), nGroup);

            sGroups.set(gIdx, nGroup);
            stockGetPut.put("group save");
            stockGetPut.get();

            Toast.makeText(mContext,"Saving "+ nGroup.grp+" / " + nGroup.grpM, Toast.LENGTH_SHORT).show();
            groupsAdapter.notifyDataSetChanged();
            mPercent = new GooglePercent().make(nGroup);
            mStatement = new GoogleStatement().make(nGroup,",");
            mTalk = new SimpleDateFormat("yy/MM/dd\nHH:mm", Locale.KOREA).format(new Date());
            gSheetUpload.uploadGroupInfo(nGroup.grp, GROUP, nGroup.grpM, mPercent,
                    mTalk, mStatement, "key12");
            finish();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }
}