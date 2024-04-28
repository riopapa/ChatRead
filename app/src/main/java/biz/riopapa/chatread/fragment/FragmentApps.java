package biz.riopapa.chatread.fragment;

import static biz.riopapa.chatread.MainActivity.aBar;
import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.appsAdapter;
import static biz.riopapa.chatread.MainActivity.appsPos;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.todayFolder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.AppsAdapter;
import biz.riopapa.chatread.edit.ActivityEditApp;
import biz.riopapa.chatread.func.AppsTable;
import biz.riopapa.chatread.func.ReadyToday;

public class FragmentApps extends Fragment {

    Menu mainMenu;
    public static RecyclerView appsRecyclerView;
    public static boolean [] fnd;

    public FragmentApps() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        aBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        aBar.setTitle("Apps");
        aBar.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.bar_apps));
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
        if (appsPos > 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) appsRecyclerView
                    .getLayoutManager();
            assert layoutManager != null;
            layoutManager.scrollToPositionWithOffset(
                    appsPos, (appsPos > 3) ? appsPos - 3 : appsPos - 2);
        }

        return thisView;
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
        } else if (item.getItemId() == R.id.apps_save) {
            appsTable.put();
        } else if (item.getItemId() == R.id.add_apps) {
            appsPos = -1;
            Intent intent = new Intent(mContext, ActivityEditApp.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }
}