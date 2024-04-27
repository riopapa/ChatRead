package biz.riopapa.chatread.func;

import static android.content.Context.CLIPBOARD_SERVICE;
import static biz.riopapa.chatread.MainActivity.mContext;

import android.content.ClipData;
import android.content.ClipboardManager;

public class Copy2Clipboard {
    public Copy2Clipboard(String s) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copy", s);
        clipboard.setPrimaryClip(clip);
    }
}
