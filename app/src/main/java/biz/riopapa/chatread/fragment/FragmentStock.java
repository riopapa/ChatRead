package biz.riopapa.chatread.fragment;

import static biz.riopapa.chatread.MainActivity.logStock;
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
import biz.riopapa.chatread.func.Copy2Save;
import biz.riopapa.chatread.func.KeyStringFind;
import biz.riopapa.chatread.func.KeyStringNext;
import biz.riopapa.chatread.func.LogSpan;
import biz.riopapa.chatread.func.LogUpdate;
import biz.riopapa.chatread.func.ScrollUp;

public class FragmentStock extends Fragment {

    SpannableString ss;
    EditText etTable, etKeyword;
    ImageView ivFind, ivClear, ivNext;
    Menu mainMenu;
    ScrollView scrollView;
    final String logName = "logStock";

    public FragmentStock() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        toolbar.setTitle(logName);
        toolbar.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.bar_stock));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.fragment_stock, container, false);
        etTable = thisView.findViewById(R.id.text_stock);
        etKeyword = thisView.findViewById(R.id.key_stock);
        ivFind = thisView.findViewById(R.id.find_stock);
        ivNext = thisView.findViewById(R.id.next_stock);
        ivClear = thisView.findViewById(R.id.clear_stock);

        ss = new LogSpan().make(logStock, this.getContext());
        etTable.setText(ss);

        ivNext.setVisibility(View.GONE);
        ivFind.setOnClickListener(v -> {
            new KeyStringFind(etKeyword, etTable, ss, ivNext);
        });

        ivNext.setOnClickListener(v -> {
            new KeyStringNext(etKeyword, etTable);
        });

        ivClear.setOnClickListener(v -> new SetFocused(etKeyword));
        scrollView = thisView.findViewById(R.id.scroll_stock);
        new Handler(Looper.getMainLooper()).post(() -> scrollView.smoothScrollBy(0, 90000));
        super.onResume();
        return thisView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_frag_stock, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.del_stock_one_set) {
            new ScrollUp(etTable, scrollView, logName,
                    new LogSpan().delOneSet(etTable.getText().toString(),
                            etTable.getSelectionStart(), mContext));

        } else if (item.getItemId() == R.id.del_stock_many) {
            int currPos = etTable.getSelectionStart();
            int logLen = logStock.length();
            logStock = new LogUpdate(mContext).squeezeLog(logStock);
            if (currPos > 0)
                currPos += logStock.length() - logLen;
            ss = new LogSpan().make(logStock, mContext);
            etTable.setText(ss);
            Selection.setSelection(ss, currPos, currPos + 1);
            etTable.requestFocus();

        } else if (item.getItemId() == R.id.del_stock_1_line) {
            new ScrollUp(etTable, scrollView, logName,
                    new LogSpan().delOneLine(etTable.getText().toString(),
                            etTable.getSelectionStart(), mContext));

        } else if (item.getItemId() == R.id.stock2save) {
            new Copy2Save(etTable.getText().toString().trim() + "\n", etTable.getSelectionStart());
        }
        return super.onOptionsItemSelected(item);
    }
}