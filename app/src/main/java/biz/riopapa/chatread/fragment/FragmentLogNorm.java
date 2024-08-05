package biz.riopapa.chatread.fragment;

import static biz.riopapa.chatread.MainActivity.deBug;
import static biz.riopapa.chatread.MainActivity.logQue;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.log_Que;
import static biz.riopapa.chatread.MainActivity.mActivity;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.prefLogEditor;
import static biz.riopapa.chatread.MainActivity.sharedEditor;
import static biz.riopapa.chatread.MainActivity.toolbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Selection;
import android.text.SpannableString;
import android.util.Log;
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
import biz.riopapa.chatread.func.Copy2Save;
import biz.riopapa.chatread.func.KeyStringFind;
import biz.riopapa.chatread.func.KeyStringNext;
import biz.riopapa.chatread.func.LogSpan;
import biz.riopapa.chatread.func.ScrollUp;

public class FragmentLogNorm extends Fragment {

    SpannableString ss;
    EditText etTable, etKeyword;
    ImageView ivFind, ivClear, ivNext, ivDebug;
    Menu mainMenu;
    ScrollView scrollView;

    public FragmentLogNorm() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (toolbar != null) {
            toolbar.setTitle(log_Que);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.fragment_log, container, false);
        etTable = thisView.findViewById(R.id.text_log);
        etKeyword = thisView.findViewById(R.id.key_log);

        ivFind = thisView.findViewById(R.id.find_log);
        ivFind.setOnClickListener(v -> new KeyStringFind(etKeyword, etTable, ss, ivNext));

        ivNext = thisView.findViewById(R.id.next_log);
        ivNext.setVisibility(View.GONE);
        ivNext.setOnClickListener(v -> new KeyStringNext(etKeyword, etTable));

        ivClear = thisView.findViewById(R.id.clear_log);
        ivClear.setOnClickListener(v -> new SetFocused(etKeyword));

        ivDebug = thisView.findViewById(R.id.debug_log);
        ivDebug.setImageDrawable(ContextCompat.getDrawable(mContext, deBug ? R.drawable.debug_on : R.drawable.debug_off));
        ivDebug.setOnClickListener(v -> {
            deBug = !deBug;
            ivDebug.setImageDrawable(ContextCompat.getDrawable(mContext, deBug ? R.drawable.debug_on : R.drawable.debug_off));
        });

        ss = new LogSpan().make(logQue, this.getContext());
        etTable.setText(ss);
        scrollView = thisView.findViewById(R.id.scroll_log);
        new Handler(Looper.getMainLooper()).post(() -> scrollView.smoothScrollBy(0, 90000));
        super.onResume();
        return thisView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_frag_log, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.del_log_one_set) {
            new ScrollUp(etTable, scrollView, log_Que,
                    new LogSpan().delOneSet(etTable.getText().toString(),
                            etTable.getSelectionStart(), mContext));

        } else if (item.getItemId() == R.id.del_log_many) {
            int currPos = etTable.getSelectionStart() - logQue.length();
            logQue = logUpdate.squeezeLog(logQue);
            currPos = logQue.length() + currPos;
            if (currPos < 10)
                currPos = 10;
            ss = new LogSpan().make(logQue, mContext);
            etTable.setText(ss);
            Selection.setSelection(ss, currPos, currPos + 1);
            etTable.requestFocus();

        } else if (item.getItemId() == R.id.log2save) {
            new Copy2Save(etTable.getText().toString().trim() + "\n", etTable.getSelectionStart());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedEditor.putBoolean("deBug", deBug);
        logQue = etTable.getText().toString();
        prefLogEditor.putString(log_Que, logQue);
        prefLogEditor.apply();
    }

}