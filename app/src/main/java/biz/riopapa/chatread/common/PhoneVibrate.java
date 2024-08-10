package biz.riopapa.chatread.common;

import static biz.riopapa.chatread.MainActivity.mContext;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

public class PhoneVibrate {

    VibratorManager vibManager;
    Vibrator vibrator = null;
    VibrationEffect vibEffect = null;

    final long[][] vibPattern = {{0, 20, 200, 300, 300, 400},
            {0, 20, 200, 300, 300, 400, 0, 20, 200, 300, 300, 400}
    };

    public void go(int type) { // 0: short, 1: long
        if (vibManager == null) {
            vibManager =
                    (VibratorManager) mContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibManager.getDefaultVibrator();
            vibEffect = VibrationEffect.createWaveform(vibPattern[type], -1);
        }
        vibrator.cancel();
        vibrator.vibrate(vibEffect);
    }

}
