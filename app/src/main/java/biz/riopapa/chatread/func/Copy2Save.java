package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.logSave;
import static biz.riopapa.chatread.MainActivity.log_Save;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.prefSaveEditor;

import android.widget.Toast;

public class Copy2Save {
    public Copy2Save(String logNow, int ps) {
        ps = logNow.lastIndexOf("\n", ps - 1);
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
        prefSaveEditor.putString(log_Save, logSave);
        prefSaveEditor.apply();

        copied = copied.replace("\n", "▶️");
        Toast.makeText(mContext, copied, Toast.LENGTH_SHORT).show();
    }
}
