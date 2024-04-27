package biz.riopapa.chatread.func;

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

    public void vib(int type) {
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
