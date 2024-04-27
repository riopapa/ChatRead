package biz.riopapa.chatread.alerts;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.widget.Toast;

public class AlertToast {

    public void show(Context context, Activity activity, String msg) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (isScreenOn(context) && activity != null) {
                    activity.runOnUiThread(() -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show());
            }
        });
    }

    boolean isScreenOn(Context context) {
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        boolean screenOn = false;
        for (Display display : dm.getDisplays()) {
            if (display.getState() != Display.STATE_OFF) {
                screenOn = true;
            }
        }
        return screenOn;
    }

}
