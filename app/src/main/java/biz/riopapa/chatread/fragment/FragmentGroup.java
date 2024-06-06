package biz.riopapa.chatread.fragment;

import static biz.riopapa.chatread.MainActivity.stockGroups;
import static biz.riopapa.chatread.MainActivity.groupsAdapter;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.mGroupPos;
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
import biz.riopapa.chatread.adapters.GroupAdapter;
import biz.riopapa.chatread.alerts.AlertSave;
import biz.riopapa.chatread.alerts.AlertTable;
import biz.riopapa.chatread.alerts.GroupSave;
import biz.riopapa.chatread.func.OptionTables;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.models.StockGroup;

public class FragmentGroup extends Fragment {

    Menu mainMenu;
    RecyclerView recyclerView;

    public FragmentGroup() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        toolbar.setTitle("Group");
        toolbar.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.bar_group));
        if (groupsAdapter == null)
            groupsAdapter = new GroupAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.fragment_group, container, false);
        recyclerView = thisView.findViewById(R.id.recycle_groups);
        recyclerView.setAdapter(groupsAdapter);
        if (todayFolder == null)
            new ReadyToday();

        if (mGroupPos > 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            assert layoutManager != null;
            layoutManager.scrollToPositionWithOffset(
                    mGroupPos, (mGroupPos > 3) ? mGroupPos - 3 : mGroupPos - 2);
        }

        return thisView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_frag_group, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.reload_all_tables) {
            new OptionTables();
            new AlertTable().get();
        } else if (item.getItemId() == R.id.clear_matched_number) {
            for (int i = 0; i < stockGroups.size(); i++) {
                StockGroup stockGroup = stockGroups.get(i);
//                for (int j = 0; j < stockGroup.stocks.size(); j++) {
//                    Stock stock = stockGroup.stocks.get(j);
//                    if (stock.quiet)
//                        stock.count = 1000;
//                    else
//                        stock.count = (stock.count+99)/100 * 100;
//                    stockGroup.stocks.set(j, stock);
//                }
                stockGroups.set(i, stockGroup);
            }
            new GroupSave("clear matches");
        } else if (item.getItemId() == R.id.log2save) {
            new AlertSave("Copy");
        }
        groupsAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }
}