package biz.riopapa.chatread.fragment;

import static biz.riopapa.chatread.MainActivity.alertLines;
import static biz.riopapa.chatread.MainActivity.alertsAdapter;
import static biz.riopapa.chatread.MainActivity.mAlertPos;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.todayFolder;
import static biz.riopapa.chatread.MainActivity.toolbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.AlertsAdapter;
import biz.riopapa.chatread.alerts.AlertSave;
import biz.riopapa.chatread.alerts.AlertTable;
import biz.riopapa.chatread.func.OptionTables;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.models.AlertLine;

public class FragmentAlert extends Fragment {

    Menu mainMenu;
    RecyclerView recyclerView;

    public FragmentAlert() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        toolbar.setTitle("Alert");
        toolbar.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.bar_alert));
        if (alertsAdapter == null)
            alertsAdapter = new AlertsAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.fragment_alert, container, false);
        recyclerView = thisView.findViewById(R.id.recycle_alerts);
        recyclerView.setAdapter(alertsAdapter);
        if (todayFolder == null)
            new ReadyToday();

        if (mAlertPos > 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            assert layoutManager != null;
            layoutManager.scrollToPositionWithOffset(
                    mAlertPos, (mAlertPos > 3) ? mAlertPos - 3 : mAlertPos - 2);
        }

        return thisView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_frag_alert, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.reload_all_tables) {
            new OptionTables();
            new AlertTable().get();
        } else if (item.getItemId() == R.id.clear_matched_number) {
            for (int i = 0; i < alertLines.size(); i++) {
                AlertLine al = alertLines.get(i);
                if (al.matched != -1) {
                    if (!al.talk.isEmpty())
                        al.matched = 1000;
                    else
                        al.matched = (al.matched+99) / 100  * 100;
                }
                alertLines.set(i, al);
            }
            new AlertSave("Clear Matches");
        } else if (item.getItemId() == R.id.log2save) {
            new AlertSave("Copy");
        }
        alertsAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }
}