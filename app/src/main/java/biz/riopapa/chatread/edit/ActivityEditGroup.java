package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.gIDX;
import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.whoAdapter;
import static biz.riopapa.chatread.MainActivity.groupAdapter;
import static biz.riopapa.chatread.MainActivity.groupListener;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.whoRecyclerView;
import static biz.riopapa.chatread.fragment.FragmentStockList.groupRecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.GroupAdapter;
import biz.riopapa.chatread.adapters.WhoAdapter;
import biz.riopapa.chatread.models.SGroup;

public class ActivityEditGroup extends AppCompatActivity {

    EditText eGroup, eGroupM, eGroupF, eSkip1, eSkip2, eSkip3, eRepl;
    TextView tTelKa;
    SwitchCompat sIgnore;
    View deleteMenu;
    final String NOTHING = "_n_";
    SGroup newGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            newGroup = (SGroup) sGroups.get(gIDX).clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Group Edit");
        actionBar.setSubtitle(sGroups.get(gIDX).grp+":"+sGroups.get(gIDX).grpF);

        eGroup = findViewById(R.id.e_group);
        eGroupM = findViewById(R.id.e_group_match);
        eGroupF = findViewById(R.id.e_group_full);
        tTelKa = findViewById(R.id.e_telka);
        sIgnore = findViewById(R.id.s_ignore);
        eSkip1 = findViewById(R.id.e_skip1);
        eSkip2 = findViewById(R.id.e_skip2);
        eSkip3 = findViewById(R.id.e_skip3);
        eRepl = findViewById(R.id.e_repl);

        eGroup.setText(sGroups.get(gIDX).grp);
        eGroupM.setText(sGroups.get(gIDX).grpM);
        eGroupF.setText(sGroups.get(gIDX).grpF);
        tTelKa.setText(sGroups.get(gIDX).telKa);
        sIgnore.setChecked(sGroups.get(gIDX).ignore);
        eSkip1.setText(sGroups.get(gIDX).skip1);
        eSkip2.setText(sGroups.get(gIDX).skip2);
        eSkip3.setText(sGroups.get(gIDX).skip3);
        tTelKa.setOnClickListener(v -> tTelKa.setText(nextTelKa(tTelKa.getText().toString())));
        if (sGroups.get(gIDX).replF != null) {
            String s = "";
            for (int i = 0; i < sGroups.get(gIDX).replF.size(); i++) {
                s += sGroups.get(gIDX).replF.get(i) + " ^ " + sGroups.get(gIDX).replT.get(i) + "\n\n";
            }
            eRepl.setText(s);
        } else
            eRepl.setText("");
        whoAdapter = new WhoAdapter();
        whoRecyclerView = findViewById(R.id.recycle_whos);
        whoRecyclerView.setAdapter(whoAdapter);
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

        gSheet.deleteGSheetGroup(sGroups.get(gIDX));
        sGroups.remove(gIDX);
        stockGetPut.put( sGroups.get(gIDX).grp+" Deleted "+ sGroups.get(gIDX).grpM);
        for (int i = 0; i < sGroups.size(); i++)
            groupAdapter.notifyItemChanged(i);
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

        sGroups.add(gIDX, newGroup);
        stockGetPut.put("group dup");
        updateAdapter();
        gSheet.updateGSheetGroup(newGroup);
        finish();
    }

    private void updateAdapter() {
        groupAdapter = new GroupAdapter(groupListener);
        groupRecyclerView.setAdapter(groupAdapter);
    }

    private void buildRepl(String s, SGroup nGroup) {
        String[] sLines = s.split("\n");
        nGroup.replF = new ArrayList<>();
        nGroup.replT = new ArrayList<>();
        for (String sLine : sLines) {
            if (sLine.isEmpty())
                continue;
            String[] sFromTo = sLine.split("\\^");
            nGroup.replF.add(sFromTo[0].trim());
            nGroup.replT.add(sFromTo[1].trim());
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
        sGroups.set(gIDX, newGroup);
        stockGetPut.save("group save "+newGroup.grp+":"+newGroup.grpF);
        updateAdapter();
        gSheet.updateGSheetGroup(newGroup);
        finish();
    }

    @Override
    public void onBackPressed() {
        updateAdapter();
        super.onBackPressed();
    }
}