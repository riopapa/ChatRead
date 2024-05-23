package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.sharedEditor;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

import biz.riopapa.chatread.models.DelItem;

public class ScrollUp {
    public ScrollUp (EditText etTable, ScrollView scrollView, String shareFile, DelItem delItem) {
        sharedEditor.putString(shareFile, delItem.logNow);
        sharedEditor.apply();

        new Handler(Looper.getMainLooper()).post(() -> {
            etTable.setText(delItem.ss);
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(scrollView.getWindowToken(), 0);
            Editable etText = etTable.getText();
            Selection.setSelection(etText, delItem.pStart, delItem.pFinish);
            etTable.requestFocus();
            int scrollBack = (delItem.pStart - delItem.pFinish) * 5 - 40;
            scrollView.smoothScrollBy(0, scrollBack);
        });
    }
}
