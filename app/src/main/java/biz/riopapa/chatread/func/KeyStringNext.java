package biz.riopapa.chatread.func;

import android.text.Editable;
import android.text.Selection;
import android.widget.EditText;

public class KeyStringNext {
    public KeyStringNext(EditText etKeyword, EditText etTable) {
        String key = etKeyword.getText().toString();
        if (key.length() < 2)
            return;
        Editable etText = etTable.getText();
        String s = etText.toString();
        int strPos = etTable.getSelectionStart();
        strPos = s.indexOf(key, strPos +1);
        if (strPos > 0) {
            Selection.setSelection(etText, strPos);
            etTable.requestFocus();
        }

    }
}
