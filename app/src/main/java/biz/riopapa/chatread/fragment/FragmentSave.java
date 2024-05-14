package biz.riopapa.chatread.fragment;

import static biz.riopapa.chatread.MainActivity.logSave;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.toolbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.common.SetFocused;
import biz.riopapa.chatread.func.KeyStringFind;
import biz.riopapa.chatread.func.KeyStringNext;
import biz.riopapa.chatread.func.LogSpan;
import biz.riopapa.chatread.func.LogUpdate;
import biz.riopapa.chatread.func.ScrollUp;

public class FragmentSave extends Fragment {

    SpannableString ss;
    EditText etTable, etKeyword;
    ImageView ivFind, ivClear, ivNext;
    Menu mainMenu;
    ScrollView scrollView;
    final String logName = "logSave";

    public FragmentSave() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        toolbar.setTitle(logName);
        toolbar.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.bar_save));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.fragment_save, container, false);
        etTable = thisView.findViewById(R.id.text_save);
        etKeyword = thisView.findViewById(R.id.key_save);
        ivFind = thisView.findViewById(R.id.find_save);
        ivNext = thisView.findViewById(R.id.next_save);
        ivClear = thisView.findViewById(R.id.clear_save);

        ss = new LogSpan().make(logSave, this.getContext());
        etTable.setText(ss);

        ivNext.setVisibility(View.GONE);
        ivFind.setOnClickListener(v -> {
            new KeyStringFind(etKeyword, etTable, ss, ivNext);
        });

        ivNext.setOnClickListener(v -> {
            new KeyStringNext(etKeyword, etTable);
        });

        ivClear.setOnClickListener(v -> new SetFocused(etKeyword));
        scrollView = thisView.findViewById(R.id.scroll_save);
        new Handler(Looper.getMainLooper()).post(() -> scrollView.smoothScrollBy(0, 90000));
        super.onResume();
        return thisView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_frag_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.del_save_one_set) {
            new ScrollUp(etTable, scrollView, logName,
                    new LogSpan().delOneSet(etTable.getText().toString(),
                            etTable.getSelectionStart(), mContext));

        } else if (item.getItemId() == R.id.del_save_many) {
            int currPos = etTable.getSelectionStart();
            int logLen = logSave.length();
            logSave = new LogUpdate(mContext).squeezeLog(logSave,logName);
            if (currPos > 0)
                currPos += logSave.length() - logLen;
            ss = new LogSpan().make(logSave, mContext);
            etTable.setText(ss);
            Selection.setSelection(ss, currPos, currPos + 1);
            etTable.requestFocus();

        } else if (item.getItemId() == R.id.del_save_1_line) {
            new ScrollUp(etTable, scrollView, logName,
                    new LogSpan().delOneLine(etTable.getText().toString(),
                            etTable.getSelectionStart(), mContext));
        }
        return super.onOptionsItemSelected(item);
    }

}