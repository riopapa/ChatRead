package biz.riopapa.chatread.fragment;

import static biz.riopapa.chatread.MainActivity.aBar;
import static biz.riopapa.chatread.MainActivity.logSave;
import static biz.riopapa.chatread.MainActivity.logStock;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.sharedEditor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.common.SnackBar;
import biz.riopapa.chatread.func.LogSpan;
import biz.riopapa.chatread.func.LogUpdate;
import biz.riopapa.chatread.func.VolumeIcon;
import biz.riopapa.chatread.models.DelItem;

public class FragmentStock extends Fragment {

    SpannableString ss,sv;
    EditText etTable, etKeyword;
    ImageView ivFind, ivClear, ivNext;
    Menu mainMenu;
    int strPos;

    public FragmentStock() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        aBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        aBar.setTitle("Stock");
        aBar.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.bar_stock));
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

        sv = ss;
        ivNext.setVisibility(View.GONE);
        strPos = -1;
        ivFind.setOnClickListener(v -> {
            String key = etKeyword.getText().toString();
            if (key.length() < 2)
                return;
            int cnt = 0;
            strPos = -1;
            String fullText = etTable.getText().toString();
            ss = sv;
            int oEnd = fullText.indexOf(key);
            for (int oStart = 0; oStart < fullText.length() && oEnd != -1; oStart = oEnd + 2) {
                oEnd = fullText.indexOf(key, oStart);
                if (oEnd > 0) {
                    cnt++;
                    if (strPos < 0)
                        strPos = oEnd;
                    ss.setSpan(new BackgroundColorSpan(0xFFFFFF00), oEnd, oEnd + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            sv = ss;
            etTable.setText(ss);
            new SnackBar().show(key, cnt+" times Found");
            Editable etText = etTable.getText();
            if (strPos > 0) {
                Selection.setSelection(etText, strPos);
                etTable.requestFocus();
                ivNext.setVisibility(View.VISIBLE);
            }
        });

        ivNext.setOnClickListener(v -> {
            String key = etKeyword.getText().toString();
            if (key.length() < 2)
                return;
            Editable etText = etTable.getText();
            String s = etText.toString();
            strPos = s.indexOf(key, strPos +1);
            if (strPos > 0) {
                Selection.setSelection(etText, strPos);
                etTable.requestFocus();
            }
        });

        ivClear.setOnClickListener(v -> etKeyword.setText(""));
        ScrollView scrollView1 = thisView.findViewById(R.id.scroll_stock);
        new Handler(Looper.getMainLooper()).post(() -> scrollView1.smoothScrollBy(0, 90000));
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
            showNextQue(new LogSpan().delOneSet(etTable.getText().toString(),
                    etTable.getSelectionStart(), mContext));

        } else if (item.getItemId() == R.id.de_stock_many) {
            int currPos = etTable.getSelectionStart();
            int logLen = logStock.length();
            logStock = new LogUpdate(mContext).squeezeLog(logStock,"logQue");
            if (currPos > 0)
                currPos += logStock.length() - logLen;
            ss = new LogSpan().make(logStock, mContext);
            etTable.setText(ss);
            Selection.setSelection(ss, currPos, currPos + 1);
            etTable.requestFocus();

        } else if (item.getItemId() == R.id.del_stock_1_line) {
            showNextQue(new LogSpan().delOneLine(etTable.getText().toString(),
                    etTable.getSelectionStart(), mContext));

        } else if (item.getItemId() == R.id.stock2save) {
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
        logStock = delItem.logNow;
        sharedEditor.putString("logStock", logStock);
        sharedEditor.apply();
        etTable.setText(delItem.ss);
        Editable etText = etTable.getText();
        Selection.setSelection(etText, delItem.ps, delItem.pf);
        etTable.requestFocus();
    }

}