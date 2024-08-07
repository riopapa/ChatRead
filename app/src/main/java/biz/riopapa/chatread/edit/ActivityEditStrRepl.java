package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.mTableName;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.tableListFile;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Arrays;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.func.OptionTables;

public class ActivityEditStrRepl extends AppCompatActivity {

    final String dummyHead = "- [ ";
    int pos = -1;
    String key, fullText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_table);
        mTableName = "strRepl";

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("String Replace");
        actionBar.setSubtitle(mTableName);

        EditText eTable = findViewById(R.id.text_table);
        File file = new File(tableFolder, mTableName + ".txt");
        String[] lines = tableListFile.readRaw(file);
        String text = insertHeader(lines);
        eTable.setText(text);
        eTable.setFocusable(true);
        eTable.setEnabled(true);
        eTable.setClickable(true);
        eTable.setFocusableInTouchMode(true);
        eTable.setTextColor(0xFF000000);
        EditText eKey = findViewById(R.id.key_table);
        eKey.setText("");
        eKey.setTextColor(0xFF000000);

        ImageView ivSearch = findViewById(R.id.search_table);
        ivSearch.setOnClickListener(v -> {
            int cnt = 0;
            key = eKey.getText().toString();
            fullText = eTable.getText().toString().trim();
            eTable.setText(fullText);   // reset previous searched color
            Spannable Word2Span = new SpannableString( eTable.getText() );
            int offsetEnd = fullText.indexOf(key);
            for(int offsetStart=0;offsetStart<fullText.length() && offsetEnd!=-1;offsetStart=offsetEnd+1) {
                offsetEnd = fullText.indexOf(key,offsetStart);
                if(offsetEnd > 0) {
                    if (cnt == 0)
                        pos = offsetEnd;
                    cnt++;
                    Word2Span.setSpan(new BackgroundColorSpan(0xFFFFFF00), offsetEnd, offsetEnd+key.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    eTable.setText(Word2Span, TextView.BufferType.SPANNABLE);
                }
            }
            if (pos > 0)
                eTable.setSelection(pos, pos+key.length());
            Toast.makeText(this, "Total "+cnt+" words found", Toast.LENGTH_SHORT).show();
        });

        ImageView ivSearchNxt = findViewById(R.id.searchNext_table);
        ivSearchNxt.setOnClickListener(v -> {
            pos = eTable.getText().toString().indexOf(key, pos + key.length());
            if (pos > 0)
                eTable.setSelection(pos, pos+key.length());
        });
    }

    String insertHeader(String [] textLines) {
        String svGroup = "x";
        StringBuilder sb = new StringBuilder();
        for (String textLine: textLines) {
            if (textLine.length() < 2)
                continue;
            String [] oneL = textLine.split("\\^");
            if (!oneL[0].equals(svGroup)) {
                svGroup = oneL[0];
                String grp = " // 없는 그룹 // ";
                for (int i = 0; i < sGroups.size(); i++) {
                    if (sGroups.get(i).grp.equals(svGroup)) {
                        grp = svGroup;
                        break;
                    }
                }
                sb.append(dummyHead).append(svGroup).append(grp).append(" ] -\n\n");    // dummy some chars between groups
            }
            sb.append(textLine).append("\n").append("\n");
        }
        return sb.toString();
    }
    String removeHeader(String txt) {
        String[] arrText = txt.split("\n");
        Arrays.sort(arrText);
        StringBuilder sortedText = new StringBuilder();
        String sv = "";
        for (String t : arrText) {
            if (t.length() < 3 || t.startsWith(dummyHead)) // ignore if not "20^주식^aa^some text blank lines"
                continue;
            t = t.trim();
            if (!sv.equals(t.substring(0, 3))) {
                sortedText.append("\n");
                sv = t.substring(0,3);
            }
            sortedText.append(t).append("\n");
        }
        return sortedText.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_table) {
            TextView tv = findViewById(R.id.text_table);
            String s = tv.getText().toString();
            fileIO.writeFile(tableFolder, mTableName +".txt", removeHeader(s));
            new OptionTables();
            finish();

        } else if (item.getItemId() == R.id.del_1_line_table) {
            EditText et = findViewById(R.id.text_table);
            String logNow = et.getText().toString();
            int lineF = et.getSelectionStart();
            int lineS = logNow.lastIndexOf("\n", lineF-1);
            if (lineS < 0)
                lineS = 0;
            StringBuilder sb = new StringBuilder(logNow);
            sb.replace(lineS, lineF,"");
            et.setText(sb.toString());
            et.setSelection(lineS);
        }
        return false;
    }

}