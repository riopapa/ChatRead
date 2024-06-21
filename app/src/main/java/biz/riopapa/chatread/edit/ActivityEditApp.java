package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.appsAdapter;
import static biz.riopapa.chatread.MainActivity.mAppsPos;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.toolbar;
import static biz.riopapa.chatread.fragment.FragmentAppsList.appsRecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.List;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.AppsAdapter;
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
//        Toolbar toolbar = findViewById(R.id.toolbar_edit_app);
//        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xFFFFDD00);
        toolbar.setTitle("App EDIT");
        toolbar.setSubtitleTextColor(0xFF667788);
        toolbar.setSubtitle("App Edit");

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
//            actionBar.setTitle("Add App");
            app = new App();
            app.nickName = "@";
            app.say = true;
            app.log = true;
            app.grp = true;
            app.who = true;
            app.addWho = false;
            app.num = true;
            app.inform = null;
            app.replF = null;

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData pData = clipboard.getPrimaryClip();
            if (pData != null) {
                ClipData.Item item = pData.getItemAt(0);
                app.fullName = item.getText().toString();
            }
        } else {
            app = apps.get(mAppsPos);
//            actionBar.setTitle("Edit App");
        }
        eFullName.setText(app.fullName);
        eNickName.setText(app.nickName);
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

        if (app.inform != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < app.inform.length; i++)
                sb.append(app.inform[i]).append(deliStr).append(app.talk[i]).append("\n\n");
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

        app.fullName = eFullName.getText().toString();
        app.nickName = eNickName.getText().toString();
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

        String [] infoTalkStr = infoTalk.getText().toString().split("\n");
        ArrayList<String> infStr = new ArrayList<>();
        ArrayList<String> talkStr = new ArrayList<>();
        for (int i = 0; i < infoTalkStr.length; i++) {
            if (!infoTalkStr[i].isEmpty()) {
                String[] t = infoTalkStr[i].split(deli);
                if (t.length == 2) {
                    infStr.add(t[0].trim());
                    talkStr.add(t[1].trim());
                } else {
                    Toast.makeText(mContext, "inform data error line=" + i + " =>" + infoTalkStr[i],
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        if (!infStr.isEmpty()) {
            app.inform = infStr.toArray(new String[0]);
            app.talk = talkStr.toArray(new String[0]);
        } else {
            app.inform = null;
        }

        String [] repl = replFromTo.getText().toString().split("\n");
        ArrayList<String> replF = new ArrayList<>();
        ArrayList<String> replT = new ArrayList<>();
        for (int i = 0; i < repl.length; i++) {
            if (!repl[i].isEmpty()) {
                String[] t = repl[i].split(deli);
                if (t.length == 2) {
                    replF.add(t[0].trim());
                    replT.add(t[1].trim());
                } else {
                    Toast.makeText(mContext, "replace from to error line=" + i + " =>" + infoTalkStr[i],
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        if (!replF.isEmpty()) {
            app.replF = replF.toArray(new String[0]);
            app.replT = replT.toArray(new String[0]);
        } else
            app.replF = null;

        if (mAppsPos == -1)
            apps.add(app);
        else
            apps.set(mAppsPos, app);
        AppsTable appsTable = new AppsTable();
        appsTable.put();
        appsTable.makeTable();
        appsAdapter = new AppsAdapter();
        appsRecyclerView.setAdapter(appsAdapter);
        finish();
    }

}