package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.sharedEditor;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.widget.EditText;
import android.widget.ScrollView;

import biz.riopapa.chatread.models.DelItem;

public class ScrollUp {
    public ScrollUp (EditText etTable, ScrollView scrollView, String shareFile, DelItem delItem) {
        sharedEditor.putString(shareFile, delItem.logNow);
        sharedEditor.apply();
        int goBack = (delItem.pStart - delItem.pOld) * 3;
        new Handler(Looper.getMainLooper()).post(() ->
                scrollView.smoothScrollBy(0, goBack));
        etTable.setText(delItem.ss);
        Editable etText = etTable.getText();
        Selection.setSelection(etText, delItem.pStart, delItem.pFinish);
        etTable.requestFocus();
    }
}
