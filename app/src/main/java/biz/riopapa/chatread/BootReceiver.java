package biz.riopapa.chatread;


import static biz.riopapa.chatread.MainActivity.notificationBar;
import biz.riopapa.chatread.notification.NotificationService;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;


public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            new SetVariables(context, "boot");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                new NotificationServiceStart(context);
                Intent updateIntent = new Intent(context, NotificationService.class);
                context.startForegroundService(updateIntent);

                new Handler(Looper.getMainLooper()).postDelayed(() ->
                        notificationBar.update("After Boot", "Rebooted", false), 10000);
            }, 5000);
        }
    }

}