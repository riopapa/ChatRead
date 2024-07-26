package biz.riopapa.chatread;

import static biz.riopapa.chatread.fragment.FragmentStockList.groupRecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import biz.riopapa.chatread.adapters.AppsAdapter;
import biz.riopapa.chatread.adapters.GroupAdapter;
import biz.riopapa.chatread.adapters.WhoAdapter;
import biz.riopapa.chatread.adapters.StockAdapter;
import biz.riopapa.chatread.cases.CaseKaTalk;
import biz.riopapa.chatread.edit.ActivityEditGroup;
import biz.riopapa.chatread.fragment.FragmentKaTalk;
import biz.riopapa.chatread.cases.CaseAndroid;
import biz.riopapa.chatread.cases.CaseApp;
import biz.riopapa.chatread.cases.CaseSMS;
import biz.riopapa.chatread.cases.CaseTelegram;
import biz.riopapa.chatread.cases.CaseTesla;
import biz.riopapa.chatread.cases.CaseWork;
import biz.riopapa.chatread.func.GetStockGroup;
import biz.riopapa.chatread.func.IgnoreString;
import biz.riopapa.chatread.func.StrReplace;
import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.StrRepl;
import biz.riopapa.chatread.stocks.StockGetPut;
import biz.riopapa.chatread.stocks.StockLine;
import biz.riopapa.chatread.stocks.StockName;
import biz.riopapa.chatread.common.Permission;
import biz.riopapa.chatread.common.PhoneVibrate;
import biz.riopapa.chatread.common.Sounds;
import biz.riopapa.chatread.common.Utils;
import biz.riopapa.chatread.edit.ActivityEditTable;
import biz.riopapa.chatread.fragment.FragmentAppsList;
import biz.riopapa.chatread.fragment.FragmentLogNorm;
import biz.riopapa.chatread.fragment.FragmentSave;
import biz.riopapa.chatread.fragment.FragmentLogStock;
import biz.riopapa.chatread.fragment.FragmentLogWork;
import biz.riopapa.chatread.fragment.FragmentStockList;
import biz.riopapa.chatread.func.FileIO;
import biz.riopapa.chatread.func.GSheet;
import biz.riopapa.chatread.func.LogUpdate;
import biz.riopapa.chatread.func.MsgNamoo;
import biz.riopapa.chatread.func.StrUtil;
import biz.riopapa.chatread.func.TableListFile;
import biz.riopapa.chatread.models.App;
import biz.riopapa.chatread.models.KeyVal;
import biz.riopapa.chatread.notification.NotificationBar;
import biz.riopapa.chatread.notification.NotificationService;

public class MainActivity extends AppCompatActivity {

    public static Toolbar toolbar = null;
    public static Context mContext;
    public static Activity mActivity;

    private DrawerLayout drawerLayout;

    public static File packageDirectory = null;
    public static File tableFolder = null;
    public static File downloadFolder = null;
    public static File todayFolder = null;

    public static String toDay = "To";
    public static long timeBegin = 0, timeEnd = 0;

    public static ArrayList<String> appIgnores;
    public static ArrayList<String> appFullNames;
    public static ArrayList<Integer> appNameIdx;
    public static String sbnGroup, sbnWho, sbnText, sbnAppName, sbnAppType, sbnAppNick;
    public static int sbnAppIdx;

    public static App sbnApp;

    public static TableListFile tableListFile = null;

    public static String[] ktGroupIgnores = null;
    public static String[] ktWhoIgnores = null;

    public static String[] smsNoNumbers = null;

    public static String[] ktNoNumbers = null;
    public static String[] ktTxtIgnores = null;

    public static String mTableName;

    public static String logQue = "", logStock = "", logSave = "", logWork = "";
    public static boolean queFlag = false, stockFlag = false, workFlag = false;

    public static ArrayList<StrRepl> ktStrRepl = null;
    public static ArrayList<StrRepl> smsStrRepl = null;

    public static SharedPreferences sharePref, prefLog, prefWork, prefSave, prefStock;
    public static SharedPreferences.Editor sharedEditor, prefLogEditor, prefWorkEditor, prefSaveEditor, prefStockEditor;
    public final static String log_Que = "logQue", log_Stock = "logStock", log_Save = "logSave", log_Work = "logWork";
    public static final String [] OPERATION = {"Show Message","show Noty","stop say",
            "reload app","hide_stop"};
    public static final int SHOW_MESSAGE = 1000;
    public static final int SHOW_NOTIFICATION_BAR = 1001;
    public static final int STOP_SAY = 1002;
    public static final int RELOAD_APP = 1003;
    public static final int HIDE_STOP = 1004;

    public static AudioFocusRequest mFocusGain = null;

    public static NotificationService notificationService;
    public static NotificationBar notificationBar;
    public static Intent mBackgroundServiceIntent;

    public static boolean isPhoneBusy = false;

    /* Stock variables */
    public static ArrayList<SGroup> sGroups = null;
    public static StockGetPut stockGetPut = null;
    public static StockLine stockLine = null;
    public static StrReplace strReplace = null;
    public static int gIDX, wIDX, sIDX;
    public static String [] stockTGroupTbl, stockKGroupTbl, stockSGroupTbl;
    public static int [] stockTGroupIdx, stockKGroupIdx, stockSGroupIdx;
    public static ArrayList<App> apps;
    public static AppsAdapter appsAdapter;

    public static final String lastChar = "힝";
    public static int mAppsPos = -1;  // updated or duplicated recycler position

    public enum soundType { PRE, POST, ERR, HI_TESLA, ONLY, STOCK, INFO, KAKAO}
    public static final int[] beepRawIds = { R.raw.pre, R.raw.post, R.raw.err,
            R.raw.hi_tesla, R.raw.only, R.raw.stock_check, R.raw.inform, R.raw.kakao_talk};

    public static KeyVal kvCommon = null;
    public static KeyVal kvKakao = null;
    public static KeyVal kvSMS = null;
    public static KeyVal kvTelegram = null;
    public static KeyVal kvStock = null;
    public static int menu_selected;

    /** case modules **/
    public static CaseAndroid caseAndroid = null;
    public static CaseApp caseApp = null;
    public static CaseKaTalk caseKaTalk = null;
    public static CaseSMS caseSMS = null;
    public static CaseTelegram caseTelegram = null;
    public static CaseTesla caseTesla = null;
    public static CaseWork caseWork = null;

    /** common modules **/
    public static Sounds sounds = null;
    public static Utils utils = null;
    public static StrUtil strUtil = null;
    public static LogUpdate logUpdate = null;
    public static StockName stockName = null;
    public static GetStockGroup getStockGroup = null;
    public static IgnoreString ignoreString = null;

    public static AudioManager mAudioManager = null;
    public static PhoneVibrate phoneVibrate = null;

    public static MsgNamoo msgNamoo = null;

    public static GSheet gSheet = null;
    public static FileIO fileIO;

    public static GroupAdapter groupAdapter = null;
    public static WhoAdapter whoAdapter = null;
    public static StockAdapter stockAdapter = null;
    public static RecyclerView whoRecyclerView;
    public static RecyclerView stockRecyclerView;

    public static ItemClickListener groupListener;
    public static boolean deBug = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mContext = this;
        mActivity = this;
        toolbar = findViewById(R.id.myToolBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle("Main");
        }
        drawerLayout = findViewById(R.id.myDrawer);
        NavigationView navigationView = findViewById(R.id.myNav);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            if (id == R.id.log) {
                fragment = new FragmentLogNorm();
            } else if (id == R.id.stock) {
                fragment = new FragmentLogStock();
            } else if (id == R.id.work) {
                fragment = new FragmentLogWork();
            } else if (id == R.id.save) {
                fragment = new FragmentSave();
            } else if (id == R.id.apps) {
                fragment = new FragmentAppsList();
            } else if (id == R.id.group) {
                fragment = new FragmentStockList();
            } else if (id == R.id.kt_log) {
                fragment = new FragmentKaTalk();
            } else {
                if (id == R.id.table_sms_no_num || id == R.id.table_sms_repl ||
                        id == R.id.table_sys_ig ||
                        id == R.id.table_kt_grp_ig || id == R.id.table_kt_no_num ||
                        id == R.id.table_kt_txt_ig || id == R.id.table_kt_who_ig ||
                        id == R.id.table_kt_repl
                    ) {
                    menu_selected = id;
                    Intent intent = new Intent(mContext, ActivityEditTable.class);
                    startActivity(intent);
                }
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                        fragment).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        });

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(),
                    PackageManager.GET_PERMISSIONS);
            Permission.ask(this, this, info);
        } catch (Exception e) {
            Log.e("Permission", "No Permission " + e);
        }

// If you have access to the external storage, do whatever you need
        if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }

        if (!isNotificationAllowed(this.getPackageName())) {
            Toast.makeText(getApplicationContext(), "Allow permission on Android notification", Toast.LENGTH_LONG).show();
            Intent intListener = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intListener);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            //permissions not granted -> request them
            requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 6562);
        } else {
            Toast.makeText(getApplicationContext(), " permission OK", Toast.LENGTH_LONG).show();
            //permissions are granted - do your stuff here :)
        }

        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())) {        //ask for permission
            Log.w("Permission","required "+getPackageName());
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
        new SetVariables(this,"main");
        notificationBar.startShow();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back is pressed... Ignore backPress
//                finish();
            }
        });


//        View decorView = getWindow().getDecorView();
//        decorView.post(() -> {
//            getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
//                    new FragmentLogNorm()).commit();
//            drawerLayout.closeDrawer(GravityCompat.START);
//        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                        new FragmentLogNorm()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }, 30);

        groupListener = position -> {
            gIDX = position;
            Intent subActivityIntent = new Intent(this, ActivityEditGroup.class);
            mActivity.startActivityForResult(subActivityIntent, 123);
        };
    }

    private boolean isNotificationAllowed(String packageName) {
        Set<String> listenerSet = NotificationManagerCompat.getEnabledListenerPackages(this);

        for (String pkg : listenerSet) {
            if (pkg != null && pkg.equals(packageName))
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        groupAdapter = new GroupAdapter(groupListener);
        groupRecyclerView.setAdapter(groupAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
