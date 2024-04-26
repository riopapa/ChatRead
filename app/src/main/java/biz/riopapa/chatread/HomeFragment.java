package biz.riopapa.chatread;

import static biz.riopapa.chatread.MainActivity.logQue;
import static biz.riopapa.chatread.MainActivity.logSave;
import static biz.riopapa.chatread.MainActivity.mActivity;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.sharedEditor;

import android.app.ActionBar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import biz.riopapa.chatread.func.LogSpan;
import biz.riopapa.chatread.func.LogUpdate;
import biz.riopapa.chatread.models.DelItem;

public class HomeFragment extends Fragment {

    ScrollView scrollView1;
    SpannableString ss, sv;
    EditText etTable, etKeyword;
    ImageView ivFind, ivClear, ivNext, ivVolume;
    Menu mainMenu;
    int logPos = -1;
    ActionBar aBar = null;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.fragment_home, container, false);
        etTable = thisView.findViewById(R.id.text_log);
        etKeyword = thisView.findViewById(R.id.key_log);
        ivFind = thisView.findViewById(R.id.find_log);
        ivNext = thisView.findViewById(R.id.next_log);
        ivClear = thisView.findViewById(R.id.clear_log);
        ivVolume = thisView.findViewById(R.id.volumes);

        ss = new LogSpan().make(logQue, this.getContext());

        etTable.setText(ss);
        etTable.setTextColor(0xff00cc);
        etKeyword.setTextColor(0xFFcc00);
        etKeyword.setText("KeyWord");


        return thisView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
        aBar = mActivity.getActionBar();
        aBar.setTitle("Log");
        aBar.setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.del_log_one_set) {
            showNextQue(new LogSpan().delOneSet(etTable.getText().toString(),
                    etTable.getSelectionStart(), mContext));

        } else if (item.getItemId() == R.id.de_log_many) {
            int currPos = etTable.getSelectionStart();
            int logLen = logQue.length();
            logQue = new LogUpdate(mContext).squeezeLog(logQue,"logQue");
            if (currPos > 0)
                currPos += logQue.length() - logLen;
            ss = new LogSpan().make(logQue, mContext);
            etTable.setText(ss);
            Selection.setSelection(ss, currPos, currPos + 1);
            etTable.requestFocus();

        } else if (item.getItemId() == R.id.del_log_1_line) {
            showNextQue(new LogSpan().delOneLine(etTable.getText().toString(),
                    etTable.getSelectionStart(), mContext));

        } else if (item.getItemId() == R.id.log2save) {
            String logNow = etTable.getText().toString().trim() + "\n";
            int ps = logNow.lastIndexOf("\n", etTable.getSelectionStart() - 1);
            if (ps == -1)
                ps = 0;
            int pf = logNow.indexOf("\n", ps + 1);
            if (pf == -1)
                pf = logNow.length();

            ps = logNow.lastIndexOf("\n", ps - 1);
            if (ps == -1)
                ps = 0;
            else
                ps = logNow.lastIndexOf("\n", ps - 1);

            String copied = logNow.substring(ps+1, pf);
            logSave += "\n" + copied;
            sharedEditor.putString("logSave", logSave);
            sharedEditor.apply();
            copied = copied.replace("\n", " ▶️ ");
            Toast.makeText(mContext, "que copied " + copied, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNextQue(DelItem delItem) {
//        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(etTable.getWindowToken(), 0);
        logQue = delItem.logNow;
        sharedEditor.putString("logQue", logQue);
        sharedEditor.apply();
        etTable.setText(delItem.ss);
        scrollView1.post(() -> {
//            new Timer().schedule(new TimerTask() {
//                public void run() {
            mActivity.runOnUiThread(() -> {
                Editable etText = etTable.getText();
                Selection.setSelection(etText, delItem.ps, delItem.pf);
                etTable.requestFocus();
            });
//                }
//            }, 50);
        });
    }

}