package biz.riopapa.chatread.common;

import static biz.riopapa.chatread.MainActivity.mContext;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class SetFocused {
    public SetFocused(EditText sKey) {
        sKey.setText("");
        sKey.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(sKey, InputMethodManager.SHOW_IMPLICIT);
    }
}
