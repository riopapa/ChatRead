package biz.riopapa.chatread.fragment;

import static android.content.Context.MODE_PRIVATE;
import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.appsAdapter;
import static biz.riopapa.chatread.MainActivity.mAppsPos;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.todayFolder;
import static biz.riopapa.chatread.MainActivity.toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.AppsAdapter;
import biz.riopapa.chatread.common.SetFocused;
import biz.riopapa.chatread.edit.ActivityEditApp;
import biz.riopapa.chatread.func.AppsTable;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.models.App;

public class FragmentAppsList extends Fragment {

    Menu mainMenu;
    public static RecyclerView appsRecyclerView;
    public static boolean [] fnd;
    String key;
    int appPos = -1;

    public FragmentAppsList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        toolbar.setTitle("Apps");
        toolbar.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.bar_apps));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.fragment_apps, container, false);

        appsAdapter = new AppsAdapter();
        appsRecyclerView = thisView.findViewById(R.id.recycle_apps);
        appsRecyclerView.setAdapter(appsAdapter);
        if (todayFolder == null)
            new ReadyToday();

        fnd = new boolean[apps.size()];
        if (appsAdapter == null)
            appsAdapter = new AppsAdapter();
        if (mAppsPos > 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) appsRecyclerView
                    .getLayoutManager();
            assert layoutManager != null;
            layoutManager.scrollToPositionWithOffset(
                    mAppsPos, (mAppsPos > 3) ? mAppsPos - 3 : mAppsPos - 2);
        }
        SharedPreferences shPref = mContext.getSharedPreferences("searchKey", MODE_PRIVATE);
        SharedPreferences.Editor shEditor = shPref.edit();
        key = shPref.getString("key","");

        EditText sKey = thisView.findViewById(R.id.app_search_key);
        sKey.setText(key);

        ImageView iSearch = thisView.findViewById(R.id.app_search);

        iSearch.setOnClickListener(v -> {
            key = sKey.getText().toString();
            if (!key.isEmpty()) {
                shEditor.putString("key", key);
                shEditor.apply();
                searchApps(0);
            }
        });
        ImageView iNext = thisView.findViewById(R.id.app_searchNext);
        iNext.setOnClickListener(v -> {
            key = sKey.getText().toString();
            if (!key.isEmpty()) {
                searchApps(appPos+2);
            }
        });
        ImageView iClear = thisView.findViewById(R.id.app_clear);
        iClear.setOnClickListener(v -> {
            new SetFocused(sKey);
        });

        return thisView;
    }


    void searchApps(int startPos) {
        appPos = -1;
        if (startPos == 0)
            fnd = new boolean[apps.size()];

//        String result = "";
        for (int i = startPos; i < apps.size(); i++) {
            App app = apps.get(i);
            if (app.nickName.contains(key) || app.fullName.contains(key) ||
                    app.memo.contains(key)) {
                if (appPos == -1) {
                    appPos = i;
//                    result = app.nickName + " " + app.fullName + " " + app.memo;
                }
                fnd[i] = true;
                appsAdapter.notifyItemChanged(i);
            }
        }
        if (appPos > 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) appsRecyclerView
                    .getLayoutManager();
            layoutManager.scrollToPositionWithOffset((appPos > 2) ? appPos-2:appPos, 10);
//            String str = key + " found " + result;
//            Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_frag_apps, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        AppsTable appsTable = new AppsTable();
        if (item.getItemId() == R.id.reload_apps) {
            appsTable.get();
            appsAdapter = new AppsAdapter();
            appsRecyclerView.setAdapter(appsAdapter);
        } else if (item.getItemId() == R.id.save_apps) {
            appsTable.put();
        } else if (item.getItemId() == R.id.add_apps) {
            mAppsPos = -1;
            Intent intent = new Intent(mContext, ActivityEditApp.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }
}