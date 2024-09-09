package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.appsAdapter;
import static biz.riopapa.chatread.MainActivity.mAppsPos;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.fragment.FragmentAppsList.appsRecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.List;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.AppsAdapter;
import biz.riopapa.chatread.common.ClipboardHelper;
import biz.riopapa.chatread.func.AppsTable;
import biz.riopapa.chatread.models.App;

public class ActivityEditApp extends AppCompatActivity {

    App app;
    final String deli = "\\^";
    final String deliStr = " ^ ";

    EditText eFullName, eNickName, eMemo;
    SwitchCompat saySwitch, logSwitch, grpSwitch, whoSwitch, addWhoSwitch, numSwitch;
    EditText ignores, infoTalk, replFromTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_app);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        eFullName = findViewById(R.id.e_app_full_name);
        eNickName = findViewById(R.id.e_nick_name);
        eMemo = findViewById(R.id.e_memo);
        saySwitch = findViewById(R.id.say_switch);
        logSwitch = findViewById(R.id.log_switch);
        grpSwitch = findViewById(R.id.grp_switch);
        whoSwitch = findViewById(R.id.who_switch);
        addWhoSwitch = findViewById(R.id.addWho_switch);
        numSwitch = findViewById(R.id.num_switch);
        ignores = findViewById(R.id.ignores);
        infoTalk = findViewById(R.id.info_talk);
        replFromTo = findViewById(R.id.repl_from_to);

        if (mAppsPos == -1) {
            actionBar.setTitle("new App");
            app = new App();
            app.nick = "@";
            app.say = true;
            app.log = true;
            app.grp = true;
            app.who = true;
            app.addWho = false;
            app.num = true;
            app.infoFrom = null;
            app.replF = null;
            app.replT = null;
            app.igStr = null;
//            app.fullName = ClipboardHelper.getClipboardText(mContext);

        } else {
            app = apps.get(mAppsPos);
            actionBar.setTitle("App Edit");
            actionBar.setSubtitle(app.nick + " " + app.fullName);
        }
        eFullName.setText(app.fullName);
        eNickName.setText(app.nick);
        eMemo.setText(app.memo);
        saySwitch.setChecked(app.say);
        logSwitch.setChecked(app.log);
        grpSwitch.setChecked(app.grp);
        whoSwitch.setChecked(app.who);
        addWhoSwitch.setChecked(app.addWho);
        numSwitch.setChecked(app.num);
        saySwitch.setOnClickListener(v -> app.say = !app.say);
        logSwitch.setOnClickListener(v -> app.log = !app.log);
        grpSwitch.setOnClickListener(v -> app.grp = !app.grp);
        whoSwitch.setOnClickListener(v -> app.who = !app.who);
        addWhoSwitch.setOnClickListener(v -> app.addWho = !app.addWho);
        numSwitch.setOnClickListener(v -> app.num = !app.num);

        if (app.igStr != null) {
            StringBuilder sb = new StringBuilder();
            for (String key : app.igStr) {
                if (!key.isEmpty())
                    sb.append(key).append(deliStr);
            }
            ignores.setText(sb.toString());
        }

        ignores.setFocusable(true);
        ignores.setEnabled(true);
        ignores.setClickable(true);
        ignores.setFocusableInTouchMode(true);

        if (app.infoFrom != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < app.infoFrom.length; i++)
                sb.append(app.infoFrom[i]).append(deliStr).append(app.infoTo[i]).append("\n\n");
            infoTalk.setText(sb.toString());
        }
        infoTalk.setFocusable(true);
        infoTalk.setEnabled(true);
        infoTalk.setClickable(true);
        infoTalk.setFocusableInTouchMode(true);

        if (app.replF != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < app.replF.length; i++)
                sb.append(app.replF[i]).append(deliStr).append(app.replT[i]).append("\n\n");
            replFromTo.setText(sb.toString());
        }
        replFromTo.setFocusable(true);
        replFromTo.setEnabled(true);
        replFromTo.setClickable(true);
        replFromTo.setFocusableInTouchMode(true);

        if (mAppsPos == -1) {
            eFullName.post(() -> {
                String clip = ClipboardHelper.getClipboardText(mContext);
                eFullName.setText(clip);
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.edit_save_app) {
            saveApp();
        } else if (item.getItemId() == R.id.edit_delete_app) {
            deleteApp();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteApp() {
        if (mAppsPos != -1) {
            apps.remove(mAppsPos);
            new AppsTable().put();
            appsAdapter = new AppsAdapter();
            appsRecyclerView.setAdapter(appsAdapter);
            finish();
        }
    }

    private void saveApp() {

        AppsTable appsTable = new AppsTable();
        appsTable.putSV();

        app.fullName = eFullName.getText().toString();
        app.nick = eNickName.getText().toString();
        app.memo = eMemo.getText().toString();

        app.say = saySwitch.isChecked();
        app.log = logSwitch.isChecked();
        app.grp = grpSwitch.isChecked();
        app.who = whoSwitch.isChecked();
        app.addWho = addWhoSwitch.isChecked();
        app.num = numSwitch.isChecked();
        String ignoreStr = ignores.getText().toString();
        String [] ss = ignoreStr.split(deli);
        for (int i = 0; i < ss.length; i++)
            ss[i] = ss[i].trim();
//        Arrays.sort(ss);
        List<String> igList = new ArrayList<>();
        for (String s : ss) {
            if (s.isEmpty())
                continue;
            igList.add(s);
        }
        app.igStr = (igList.isEmpty()) ? null : igList.toArray(new String[0]);

        String[] infoTalkLines = infoTalk.getText().toString().split("\n");
        List<String> infoStrings = new ArrayList<>();
        List<String> talkStrings = new ArrayList<>();
        for (String line : infoTalkLines) {
            if (!line.isEmpty()) {
                String[] parts = line.split(deli);
                if (parts.length == 2) {
                    infoStrings.add(parts[0].trim());
                    talkStrings.add(parts[1].trim());
                } else {
                    String errorMessage = "Invalid data format at line "+line;
                    Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        if (!infoStrings.isEmpty()) {
            app.infoFrom = infoStrings.toArray(new String[0]);
            app.infoTo = talkStrings.toArray(new String[0]);
        } else {
            app.infoFrom = null;
            app.infoTo = null;
        }

        String [] repl = replFromTo.getText().toString().split("\n");
        ArrayList<String> replF = new ArrayList<>();
        ArrayList<String> replT = new ArrayList<>();
        for (String s : repl) {
            if (!s.isEmpty()) {
                String[] t = s.split(deli);
                if (t.length == 2) {
                    replF.add(t[0].trim());
                    replT.add(t[1].trim());
                } else {
                    Toast.makeText(mContext, "replace from to error line=" + s,
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        if (repl.length != 0) {
            app.replF = replF.toArray(new String[0]);
            app.replT = replT.toArray(new String[0]);
        } else {
            app.replF = null;
            app.replT = null;
        }

        if (mAppsPos == -1)
            apps.add(app);
        else
            apps.set(mAppsPos, app);

        appsTable.put();
        appsTable.makeTable();
        appsAdapter = new AppsAdapter();
        appsRecyclerView.setAdapter(appsAdapter);
        finish();
    }

}