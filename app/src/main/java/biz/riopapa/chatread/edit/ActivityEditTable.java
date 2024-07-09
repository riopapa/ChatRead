package biz.riopapa.chatread.edit;

import static biz.riopapa.chatread.MainActivity.fileIO;
import static biz.riopapa.chatread.MainActivity.menu_selected;
import static biz.riopapa.chatread.MainActivity.mTableName;
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
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.func.OptionTables;


public class ActivityEditTable extends AppCompatActivity {

    int pos = -1;
    String key, fullText;
    int [] menuId = { R.id.table_sms_no_num,   R.id.table_sms_repl,     R.id.table_sms_txt_ig,
                        R.id.table_sms_who_ig,  R.id.table_sys_ig,
                        R.id.table_kt_grp_ig,   R.id.table_kt_no_num,   R.id.table_kt_txt_ig,
                        R.id.table_kt_who_ig,   R.id.table_kt_repl
    };
    String [] fileId = {
                        "smsNoNum",             "smsRepl",              "smsTxtIg",
                        "smsWhoIg",             "sysIg",
                        "ktGrpIg",              "ktNoNum",              "ktTxtIg",
                        "ktWhoIg",              "ktRepl"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_table);
        mTableName = "";
        for (int i = 0; i < menuId.length; i++) {
            if (menu_selected == menuId[i]) {
                mTableName = fileId[i];
                break;
            }
        }
        if (mTableName.isEmpty()) {
            Toast.makeText(this, "Menu Resource Id not Found "+menu_selected,
                    Toast.LENGTH_LONG).show();
            return;
        }
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Edit Table");
        actionBar.setSubtitle(mTableName);

        EditText eTable = findViewById(R.id.text_table);
        File file = new File(tableFolder, mTableName + ".txt");
        String[] lines = tableListFile.readRaw(file);
        String text = Arrays.stream(lines).map(s -> s + "\n\n").collect(Collectors.joining());
        eTable.setText(text);
        eTable.setFocusable(true);
        eTable.setEnabled(true);
        eTable.setClickable(true);
        eTable.setTextColor(0xFF000000);
        eTable.setFocusableInTouchMode(true);

        EditText eKey = findViewById(R.id.key_table);
        eKey.setText(mTableName);
        eKey.setTextColor(0xFF000000);

        ImageView ivSearch = findViewById(R.id.search_table);
        ivSearch.setOnClickListener(v -> {
            int cnt = 0;
            key = eKey.getText().toString();
            fullText = eTable.getText().toString();
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

    String sortText(String txt) {
        String[] arrText = txt.split("\n");
        Arrays.sort(arrText);
        StringBuilder sortedText = new StringBuilder();
        Arrays.stream(arrText).filter(t -> t.length() > 2).forEach(t -> sortedText.append(t).append("\n"));
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
            fileIO.writeFile(tableFolder, mTableName +".txt", sortText(s));
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
