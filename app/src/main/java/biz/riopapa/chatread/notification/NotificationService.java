package biz.riopapa.chatread.notification;

import static android.text.TextUtils.replace;
import static biz.riopapa.chatread.MainActivity.HIDE_STOP;
import static biz.riopapa.chatread.MainActivity.SHOW_NOTIFICATION_BAR;
import static biz.riopapa.chatread.MainActivity.RELOAD_APP;
import static biz.riopapa.chatread.MainActivity.SHOW_MESSAGE;
import static biz.riopapa.chatread.MainActivity.STOP_SAY;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.sharePref;
import static biz.riopapa.chatread.MainActivity.sharedEditor;
import static biz.riopapa.chatread.MainActivity.sounds;
import static biz.riopapa.chatread.MainActivity.strUtil;
import static biz.riopapa.chatread.MainActivity.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import biz.riopapa.chatread.MainActivity;
import biz.riopapa.chatread.R;
import biz.riopapa.chatread.SetVariables;

public class NotificationService extends Service {

    NotificationCompat.Builder mBuilder = null;
    NotificationManager mNotificationManager;
    NotificationChannel mNotificationChannel = null;
    int currentDndMode;

    private static RemoteViews mRemoteViews;
    static String msg1 = "", head1 = "00:99";
    static String msg2 = "", head2 = "00:99";
    static String msg3 = "", head3 = "00:99";
    static boolean show_stop = false;

    public NotificationService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        if (mContext == null)
            mContext = this;
        Log.w("NotificationService","onCreate");
        mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_bar);
    }

    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotification();

        int operation = -1;
        try {
            operation = intent.getIntExtra("operation", -1);
        } catch (Exception e) {
            Log.e("operation"+operation,e.toString());
        }
        if (operation == -1) {
            return START_NOT_STICKY;
        }
//        if (operation > 1001)
//            utils.logB("onStartCommand", "operation = "+operation+ " " + OPERATION[operation-1000]);
        if (msg1.isEmpty())
            msgGet();

        switch (operation) {

            case SHOW_MESSAGE:

                msg3 = msg2;
                head3 = head2;
                msg2 = msg1;
                head2 = head1;

//                msg1 = strUtil.makeEtc(Objects.requireNonNull(intent.getStringExtra("msg")), 100)
//                        .replace(" ", "\u2008"); // Punctuation Space
//                head1 = new SimpleDateFormat("HH:mm", Locale.KOREA).format(new Date())
//                        + "\u00A0" + Objects.requireNonNull(intent.getStringExtra("who"))
//                        .replace(" ", "\u2008");

                msg1 = strUtil.makeEtc(""+intent.getStringExtra("msg"), 100);
                head1 = new SimpleDateFormat("HH:mm", Locale.KOREA).format(new Date())
                        + "\u00A0" + intent.getStringExtra("who");

                show_stop = intent.getBooleanExtra("stop", true);

                break;

            case RELOAD_APP:

                reload_App();
                break;

            case STOP_SAY:

                if (sounds != null)
                    sounds.stopTTS();
                show_stop = false;
                break;

            case SHOW_NOTIFICATION_BAR:
            case HIDE_STOP:
                show_stop = false;
                break;

            default:
                utils.logW("onStartCommand", "operation = "+operation+ " Case Error");
                break;
        }
        updateRemoteViews();
        return START_STICKY;
    }

    private void reload_App() {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            mRemoteViews = null;
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }, 100);
    }

    private void createNotification() {

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationChannel = new NotificationChannel("default","default", NotificationManager.IMPORTANCE_DEFAULT);
        mNotificationManager.createNotificationChannel(mNotificationChannel);
//        currentDndMode = mNotificationManager.getCurrentInterruptionFilter();
//
//        if (currentDndMode == NotificationManager.INTERRUPTION_FILTER_NONE) {
//            Log.w("currentDndMode", "currentDndMode = NotificationManager.INTERRUPTION_FILTER_NONE");
//            // Total silence: Suppress notification
//        } else if (currentDndMode == NotificationManager.INTERRUPTION_FILTER_ALARMS) {
//            Log.w("currentDndMode", "currentDndMode = NotificationManager.INTERRUPTION_FILTER_ALARMS");
//            // Alarms only: Deliver only if essential
//        } else if (currentDndMode == NotificationManager.INTERRUPTION_FILTER_PRIORITY) {
//            Log.w("currentDndMode", "currentDndMode = NotificationManager.INTERRUPTION_FILTER_PRIORITY");
//            // Priority only: Check notification importance
//        } else
//            Log.w("currentDndMode", "currentDndMode = NotificationManager.INTERRUPTION_FILTER_UNKNOWN "+
//                    currentDndMode);

        if (mRemoteViews == null)
            mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_bar);

        mBuilder = new NotificationCompat.Builder(this, mNotificationChannel.getId())
//                .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
//                .setColor(getApplicationContext().getColor(R.color.barLine1))
                .setContent(mRemoteViews)
                .setSmallIcon(R.drawable.stock1_icon)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setContentTitle("Chat.Read")
                .setContentText("Chat.Read Text")
                .setCustomBigContentView(mRemoteViews)
                .setLargeIcon(null)
//                .setStyle(new NotificationCompat.BigTextStyle())
                .setOngoing(true);

        Intent sIntent = new Intent(mContext, NotificationService.class);
        sIntent.putExtra("operation", STOP_SAY);
        PendingIntent pendingStop = PendingIntent.getService(mContext, STOP_SAY, sIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingStop);
        mRemoteViews.setOnClickPendingIntent(R.id.stop_now, pendingStop);

        Intent intMain = new Intent(mContext, MainActivity.class);
        intMain.putExtra("operation", RELOAD_APP);
        PendingIntent pendingMain = PendingIntent.getService(mContext, RELOAD_APP, intMain,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingMain);

        mRemoteViews.setOnClickPendingIntent(R.id.line_1,
                PendingIntent.getActivity(mContext, 0, intMain,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));

        mRemoteViews.setOnClickPendingIntent(R.id.line_2,
                PendingIntent.getActivity(mContext, 0, intMain,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));

        mRemoteViews.setOnClickPendingIntent(R.id.line_3,
                PendingIntent.getActivity(mContext, 0, intMain,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));

    }

    private void updateRemoteViews() {

        mBuilder.setSmallIcon(R.drawable.chat_read_logo);
        mRemoteViews.setTextViewText(R.id.msg_head1, head1);
        mRemoteViews.setTextViewText(R.id.msg_text1, msg1);
        mRemoteViews.setTextViewText(R.id.msg_head2, head2);
        mRemoteViews.setTextViewText(R.id.msg_text2, msg2);
        mRemoteViews.setTextViewText(R.id.msg_head3, head3);
        mRemoteViews.setTextViewText(R.id.msg_text3, msg3);
        mRemoteViews.setViewVisibility(R.id.stop_now, (show_stop)? View.VISIBLE : View.GONE);
        mNotificationManager.notify(100,mBuilder.build());
        msgPut();
    }

    public void msgGet() {

        if (sharePref == null)
            new SetVariables(this, "notiSvc");
        msg1 = sharePref.getString("msg1", "None 1");
        msg2 = sharePref.getString("msg2", "None 2");
        msg3 = sharePref.getString("msg3", "None 3");
        head1 = sharePref.getString("head1","00:59");
        head2 = sharePref.getString("head2","00:59");
        head3 = sharePref.getString("head3","00:53");
    }
    public static void msgPut() {
        sharedEditor.putString("msg1", msg1);
        sharedEditor.putString("msg2", msg2);
        sharedEditor.putString("msg3", msg3);
        sharedEditor.putString("head1", head1);
        sharedEditor.putString("head2", head2);
        sharedEditor.putString("head3", head3);
        sharedEditor.apply();
    }

}