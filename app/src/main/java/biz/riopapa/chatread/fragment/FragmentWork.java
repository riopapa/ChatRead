package biz.riopapa.chatread.fragment;

import static biz.riopapa.chatread.MainActivity.logSave;
import static biz.riopapa.chatread.MainActivity.logWork;
import static biz.riopapa.chatread.MainActivity.logWork;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.sharedEditor;

import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.func.LogSpan;
import biz.riopapa.chatread.func.LogUpdate;
import biz.riopapa.chatread.models.DelItem;

public class FragmentWork extends Fragment {

    SpannableString ss;
    EditText etTable, etKeyword;
    ImageView ivFind, ivClear, ivNext;
    Menu mainMenu;
    ActionBar aBar = null;

    public FragmentWork() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        aBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        aBar.setTitle("Work");
        aBar.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.bar_work));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.fragment_work, container, false);
        etTable = thisView.findViewById(R.id.text_work);
        etKeyword = thisView.findViewById(R.id.key_work);
        ivFind = thisView.findViewById(R.id.find_work);
        ivNext = thisView.findViewById(R.id.next_work);
        ivClear = thisView.findViewById(R.id.clear_work);

        ss = new LogSpan().make(logWork, this.getContext());
        etTable.setText(ss);

        return thisView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_work, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.del_work_one_set) {
            showNextQue(new LogSpan().delOneSet(etTable.getText().toString(),
                    etTable.getSelectionStart(), mContext));

        } else if (item.getItemId() == R.id.de_work_many) {
            int currPos = etTable.getSelectionStart();
            int logLen = logWork.length();
            logWork = new LogUpdate(mContext).squeezeLog(logWork,"logQue");
            if (currPos > 0)
                currPos += logWork.length() - logLen;
            ss = new LogSpan().make(logWork, mContext);
            etTable.setText(ss);
            Selection.setSelection(ss, currPos, currPos + 1);
            etTable.requestFocus();

        } else if (item.getItemId() == R.id.del_work_1_line) {
            showNextQue(new LogSpan().delOneLine(etTable.getText().toString(),
                    etTable.getSelectionStart(), mContext));

        } else if (item.getItemId() == R.id.work2save) {
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
        logWork = delItem.logNow;
        sharedEditor.putString("logWork", logWork);
        sharedEditor.apply();
        etTable.setText(delItem.ss);
        Editable etText = etTable.getText();
        Selection.setSelection(etText, delItem.ps, delItem.pf);
        etTable.requestFocus();
    }

}