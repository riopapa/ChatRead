package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.appsAdapter;
import static biz.riopapa.chatread.MainActivity.appsPos;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.fragment.FragmentApps.appsRecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.AppsAdapter;
import biz.riopapa.chatread.databinding.ActivityEditAppBinding;
import biz.riopapa.chatread.func.AppsTable;
import biz.riopapa.chatread.models.App;

public class ActivityEditApp extends AppCompatActivity {

    App app;
    ActivityEditAppBinding binding;
    final String deli = "\\^";
    final String deliStr = " ^ ";
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_app);
        binding = ActivityEditAppBinding.inflate(getLayoutInflater());
        if (appsPos == -1) {
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
            app.replFrom = null;

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData pData = clipboard.getPrimaryClip();
            if (pData != null) {
                ClipData.Item item = pData.getItemAt(0);
                app.fullName = item.getText().toString();
            }
        } else {
            app = apps.get(appsPos);
//            actionBar.setTitle("Edit App");
        }
        binding.eAppFullName.setText(app.fullName);
        binding.eNickName.setText(app.nickName);
        binding.eMemo.setText(app.memo);
        binding.saySwitch.setChecked(app.say);
        binding.logSwitch.setChecked(app.log);
        binding.grpSwitch.setChecked(app.grp);
        binding.whoSwitch.setChecked(app.who);
        binding.addWhoSwitch.setChecked(app.addWho);
        binding.numSwitch.setChecked(app.num);
        binding.saySwitch.setOnClickListener(v -> app.say = !app.say);
        binding.logSwitch.setOnClickListener(v -> app.log = !app.log);
        binding.grpSwitch.setOnClickListener(v -> app.grp = !app.grp);
        binding.whoSwitch.setOnClickListener(v -> app.who = !app.who);
        binding.addWhoSwitch.setOnClickListener(v -> app.addWho = !app.addWho);
        binding.numSwitch.setOnClickListener(v -> app.num = !app.num);

        if (app.igStr != null) {
            StringBuilder sb = new StringBuilder();
            for (String key : app.igStr) {
                if (!key.isEmpty())
                    sb.append(key).append(deliStr);
            }
            binding.ignores.setText(sb.toString());
        }

        binding.ignores.setFocusable(true);
        binding.ignores.setEnabled(true);
        binding.ignores.setClickable(true);
        binding.ignores.setFocusableInTouchMode(true);

        if (app.inform != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < app.inform.length; i++)
                sb.append(app.inform[i]).append(deliStr).append(app.talk[i]).append("\n\n");
            binding.infoTalk.setText(sb.toString());
        }
        binding.infoTalk.setFocusable(true);
        binding.infoTalk.setEnabled(true);
        binding.infoTalk.setClickable(true);
        binding.infoTalk.setFocusableInTouchMode(true);

        if (app.replFrom != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < app.replFrom.length; i++)
                sb.append(app.replFrom[i]).append(deliStr).append(app.replTo[i]).append("\n\n");
            binding.replFromTo.setText(sb.toString());
        }
        binding.replFromTo.setFocusable(true);
        binding.replFromTo.setEnabled(true);
        binding.replFromTo.setClickable(true);
        binding.replFromTo.setFocusableInTouchMode(true);
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
        if (appsPos != -1) {
            apps.remove(appsPos);
            new AppsTable().put();
            appsAdapter = new AppsAdapter();
            appsRecyclerView.setAdapter(appsAdapter);
            finish();
        }
    }

    private void saveApp() {

        app.fullName = binding.eAppFullName.getText().toString();
        app.nickName = binding.eNickName.getText().toString();
        app.memo = binding.eMemo.getText().toString();

        app.say = binding.saySwitch.isChecked();
        app.log = binding.logSwitch.isChecked();
        app.grp = binding.grpSwitch.isChecked();
        app.who = binding.whoSwitch.isChecked();
        app.addWho = binding.addWhoSwitch.isChecked();
        app.num = binding.numSwitch.isChecked();
        String ignoreStr = binding.ignores.getText().toString();
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

        String [] infoTalkStr = binding.infoTalk.getText().toString().split("\n");
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

        String [] repl = binding.replFromTo.getText().toString().split("\n");
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
            app.replFrom = replF.toArray(new String[0]);
            app.replTo = replT.toArray(new String[0]);
        } else
            app.replFrom = null;

        if (appsPos == -1)
            apps.add(app);
        else
            apps.set(appsPos, app);
        AppsTable appsTable = new AppsTable();
        appsTable.put();
        appsTable.makeTable();
        appsAdapter = new AppsAdapter();
        appsRecyclerView.setAdapter(appsAdapter);
        finish();
    }

}