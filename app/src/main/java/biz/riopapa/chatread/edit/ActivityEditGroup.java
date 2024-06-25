package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.gIdx;
import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.groupWhoAdapter;
import static biz.riopapa.chatread.MainActivity.groupsAdapter;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockGetPut;

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
import biz.riopapa.chatread.models.SGroup;

public class ActivityEditGroup extends AppCompatActivity {

    EditText eGroup, eGroupM, eGroupF, eSkip1, eSkip2, eSkip3, eRepl;
    TextView tTelKa;
    SwitchCompat sIgnore;
    String mPercent, mStatement, mTalk;
    View deleteMenu;
    RecyclerView recyclerView;
    final String GROUP = ")_(";
    final String NOTHING = "_n_";
    SGroup newGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
//        Toolbar toolbar = findViewById(R.id.toolbar_edit_group);
//        if (toolbar != null) {
////            setSupportActionBar(toolbar);
//            toolbar.setTitleTextColor(0xFF44FF33);
//            toolbar.setSubtitleTextColor(0xFF000000);
//            toolbar.setSubtitle("Group Edit");
//        }
        nowSGroup = sGroups.get(gIdx);
        try {
            newGroup = (SGroup) sGroups.get(gIdx).clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

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
        groupWhoAdapter = new GroupWhoAdapter();
        recyclerView = findViewById(R.id.recycle_whos);
        recyclerView.setAdapter(groupWhoAdapter);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_edit, menu);
        new Handler(Looper.getMainLooper()).post(() -> {
            deleteMenu = this.findViewById(R.id.delete_this);
            if (deleteMenu != null) {
                deleteMenu.setOnLongClickListener(v -> {
                    deleteStockGroup();
                    return true;
                });
            }
        });
        return true;
    }

    private void deleteStockGroup() {

        gSheet.deleteGSheetGroup(nowSGroup);
        sGroups.remove(gIdx);
        stockGetPut.put( nowSGroup.grp+" Deleted "+ nowSGroup.grpM);
        groupsAdapter.notifyDataSetChanged();
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

        newGroup.grp = eGroup.getText().toString();
        newGroup.grpM = eGroupM.getText().toString();
        newGroup.grpF = eGroupF.getText().toString();
        newGroup.telKa = tTelKa.getText().toString();
        newGroup.ignore = sIgnore.isChecked();
        newGroup.skip1 = eSkip1.getText().toString();
        newGroup.skip2 = eSkip2.getText().toString();
        newGroup.skip3 = eSkip3.getText().toString();
        if (newGroup.skip1.isEmpty())
            newGroup.skip1 = NOTHING;
        if (newGroup.skip2.isEmpty())
            newGroup.skip2 = NOTHING;
        if (newGroup.skip3.isEmpty())
            newGroup.skip3 = NOTHING;

        buildRepl(eRepl.getText().toString(), newGroup);

        sGroups.add(gIdx, newGroup);
        nowSGroup = sGroups.get(gIdx);
        stockGetPut.put("group dup");
        stockGetPut.get();
        groupsAdapter.notifyDataSetChanged();
        gSheet.updateGSheetGroup(newGroup);
        finish();
    }

    private void buildRepl(String s, SGroup nGroup) {
        String[] split = s.split("\n");
        nGroup.replT = new ArrayList<>();
        nGroup.replF = new ArrayList<>();
        for (String sp1 : split) {
            if (sp1.isEmpty())
                continue;
            String[] split2 = sp1.split("\\^");
            nGroup.replT.add(split2[0].trim());
            nGroup.replF.add(split2[1].trim());
        }
    }
    private void saveGroup() {

        newGroup.grp = eGroup.getText().toString();
        newGroup.grpM = eGroupM.getText().toString();
        newGroup.grpF = eGroupF.getText().toString();
        newGroup.telKa = tTelKa.getText().toString();
        newGroup.ignore = sIgnore.isChecked();
        newGroup.skip1 = eSkip1.getText().toString();
        newGroup.skip2 = eSkip2.getText().toString();
        newGroup.skip3 = eSkip3.getText().toString();
        if (newGroup.skip1.isEmpty())
            newGroup.skip1 = NOTHING;
        if (newGroup.skip2.isEmpty())
            newGroup.skip2 = NOTHING;
        if (newGroup.skip3.isEmpty())
            newGroup.skip3 = NOTHING;
        buildRepl(eRepl.getText().toString(), newGroup);
        sGroups.set(gIdx, newGroup);
        nowSGroup = sGroups.get(gIdx);
        stockGetPut.put("group save");
        stockGetPut.get();

        Toast.makeText(mContext,"Saving "+ newGroup.grp+" / " + newGroup.grpM, Toast.LENGTH_SHORT).show();
        groupsAdapter.notifyDataSetChanged();
        gSheet.updateGSheetGroup(newGroup);
        finish();

    }
}