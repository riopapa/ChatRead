package biz.riopapa.chatread.func;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import biz.riopapa.chatread.common.SnackBar;

public class KeyStringFind {
    public KeyStringFind(EditText etKeyword, EditText etTable, SpannableString ss, ImageView ivNext) {

        String key = etKeyword.getText().toString();
        if (key.length() < 2)
            return;
        int cnt = 0;
        int strPos = -1;
        String fullText = etTable.getText().toString();
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
        etTable.setText(ss);
        new SnackBar().show(key, cnt+" times Found");
        Editable etText = etTable.getText();
        if (strPos > 0) {
            Selection.setSelection(etText, strPos);
            etTable.requestFocus();
            ivNext.setVisibility(View.VISIBLE);
        }
    }

}
