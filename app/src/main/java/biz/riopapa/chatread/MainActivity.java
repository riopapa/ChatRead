package biz.riopapa.chatread;

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
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import biz.riopapa.chatread.adapters.AlertsAdapter;
import biz.riopapa.chatread.adapters.AppsAdapter;
import biz.riopapa.chatread.alerts.AlertStock;
import biz.riopapa.chatread.alerts.StockName;
import biz.riopapa.chatread.common.Permission;
import biz.riopapa.chatread.fragment.FragmentAlert;
import biz.riopapa.chatread.fragment.FragmentApps;
import biz.riopapa.chatread.fragment.FragmentLog;
import biz.riopapa.chatread.fragment.FragmentStock;
import biz.riopapa.chatread.fragment.FragmentWork;
import biz.riopapa.chatread.func.AppsTable;
import biz.riopapa.chatread.func.FileIO;
import biz.riopapa.chatread.func.LogUpdate;
import biz.riopapa.chatread.common.PhoneVibrate;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.common.Sounds;
import biz.riopapa.chatread.func.StrUtil;
import biz.riopapa.chatread.func.TableListFile;
import biz.riopapa.chatread.common.Utils;
import biz.riopapa.chatread.models.AlertLine;
import biz.riopapa.chatread.models.App;
import biz.riopapa.chatread.models.KeyVal;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    public static Context mContext;
    public static Activity mActivity;
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

    public static String[] aGroupSaid = null;
    public static int [][][] aAlertLineIdx;

    public static List<String> aGroups;    // {고선, 텔레, 힐}
    public static List<Boolean> aGroupsPass;
    public static String[] aGSkip1, aGSkip2, aGSkip3, aGSkip4;
    public static String[][] aGroupWhos;     // [2] 이진홍, 김선수
    public static String[][][] aGroupWhoKey1, aGroupWhoKey2, aGroupWhoSkip, aGroupWhoPrev, aGroupWhoNext;

    public static String[] smsWhoIgnores = null;
    public static String[] smsTxtIgnores = null;
    public static String[] smsNoNumbers = null;
    public static String[] smsReplFrom = null;
    public static String[] smsReplTo = null;

    public static String[] ktNoNumbers = null;
    public static String[] ktTxtIgnores = null;

    public static String[] shortWhoNames = null;
    public static String[] longWhoNames = null;

    public static String[] whoNameFrom = null;
    public static String[] whoNameTo = null;

    public static String nowFileName;

    public static int replGroupCnt = 0;
    public static String [] replGroup;
    public static String [][] replLong;
    public static String [][] replShort;
    public static String logQue = "", logStock = "", logSave = "", logWork = "";

    public static SharedPreferences sharePref;
    public static SharedPreferences.Editor sharedEditor;


    public static final int SHOW_MESSAGE = 1234;
    public static final int HIDE_STOP = 5678;
    public static final int STOP_SAY1 = 10011;
    public static final int RELOAD_APP = 2022;

    public static ActionBar aBar = null;
    public static AudioFocusRequest mFocusGain = null;

    /* module list */
//    static AlertWhoIndex alertWhoIndex = null;

    public static boolean isPhoneBusy = false;

    public static AlertsAdapter alertsAdapter = null;
    public static ArrayList<AlertLine> alertLines = null;;

    public static ArrayList<App> apps;
    public static AppsAdapter appsAdapter;

    public static String chatGroup;
    public static final String lastChar = "힝";
    public static int alertPos = -1, appsPos = -1;  // updated or duplicated recycler position

    public enum soundType { PRE, POST, ERR, HI_TESLA, ONLY, STOCK, INFO, KAKAO}
    public static final int[] beepRawIds = { R.raw.pre, R.raw.post, R.raw.err,
            R.raw.hi_tesla, R.raw.only, R.raw.stock_check, R.raw.inform, R.raw.kakao_talk};

    public static KeyVal kvCommon = null;
    public static KeyVal kvKakao = null;
    public static KeyVal kvSMS = null;
    public static KeyVal kvTelegram = null;
    public static KeyVal kvStock = null;

    public static Sounds sounds = null;
    public static Utils utils = null;
    public static StrUtil strUtil = null;
    public static LogUpdate logUpdate = null;
    public static AlertStock alertStock = null;
    public static StockName stockName = null;

    public static AudioManager audioManager = null;
    public static PhoneVibrate phoneVibrate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);
        mContext = this;
        mActivity = this;
        drawerLayout = findViewById(R.id.myDrawer);
        navigationView = findViewById(R.id.myNav);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.myFrame, new FragmentLog()).commit();

        //==================================================================
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.log:
                        getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                                new FragmentLog()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.stock:
                        getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                                new FragmentStock()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.work:
                        getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                                new FragmentWork()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.alert:
                        getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                                new FragmentAlert()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.apps:
                        getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                                new FragmentApps()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.table:
                        getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                                new ExitFragment()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.info:
                        getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                                new InfoFragment()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.calc:
                        getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                                new CalcFragment()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.liste:
                        getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                                new ListeFragment()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.conv:
                        getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                                new ConvFragment()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.listH:
                        getSupportFragmentManager().beginTransaction().replace(R.id.myFrame,
                                new HorizontalFragment()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
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

//        if (!isNotificationAllowed(this.getPackageName())) {
//            Toast.makeText(getApplicationContext(), "Allow permission on Android notification", Toast.LENGTH_LONG).show();
//            Intent intListener = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
//            startActivity(intListener);
//        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            //permissions not granted -> request them
            requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 6562);
        } else {
            Toast.makeText(getApplicationContext(), " permission OK", Toast.LENGTH_LONG).show();
            //permissions are granted - do your stuff here :)
        }

        /**
        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())) {        //ask for permission
            Log.w("Permission","required "+getPackageName());
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
**/
        getSharedValues();
        init_variables();
        new AppsTable().get();
    }

    void getSharedValues() {
        sharePref = this.getSharedPreferences("sayText", MODE_PRIVATE);
        sharedEditor = sharePref.edit();
        logQue = sharePref.getString("logQue", "");
        logStock = sharePref.getString("logStock", "");
        logSave = sharePref.getString("logSave", "");
        logWork = sharePref.getString("logWork", "");
    }

    void init_variables() {
        if (packageDirectory == null)
            packageDirectory = new File(Environment.getExternalStorageDirectory(), "_ChatTalkLog");
        downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
        tableFolder = new File(downloadFolder, "_ChatTalk");
//        new OptionTables().readAll();
        FileIO.readyPackageFolder();
//        new AlertTableIO().get();

        kvKakao = new KeyVal();
        kvTelegram = new KeyVal();
        kvCommon = new KeyVal();
        kvSMS = new KeyVal();
        kvStock = new KeyVal();
        if (utils == null)
            utils = new Utils();
        if (strUtil == null)
            strUtil = new StrUtil();
        new ReadyToday();

    }
    private boolean isNotificationAllowed(String packageName) {
        Set<String> listenerSet = NotificationManagerCompat.getEnabledListenerPackages(this);

        for (String pkg : listenerSet) {
            if (pkg != null && pkg.equals(packageName))
                return true;
        }
        return false;
    }

}
