package biz.riopapa.chatread;


import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.notificationBar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.util.Objects;


public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            new AllVariables().set(context, "boot");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent nIntent = new Intent(mContext, MainActivity.class);
                nIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(nIntent);
            }, 100);

            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    notificationBar.update("After Boot", "Rebooted", false), 5000);
        }
    }

}