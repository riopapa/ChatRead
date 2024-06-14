package biz.riopapa.chatread.fragment;

import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.groupsAdapter;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.mStockGroupPos;
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
import biz.riopapa.chatread.func.OptionTables;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.models.SGroup;

public class FragmentStockList extends Fragment {

    Menu mainMenu;
    RecyclerView recyclerView;

    public FragmentStockList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (toolbar != null) {
            toolbar.setTitle("Stock Group");
            toolbar.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bar_stock_group));
        }
//        if (stockGetPut == null)
//            stockGetPut = new StockGetPut();
//        stockGetPut.sort();
        if (groupsAdapter == null)
            groupsAdapter = new GroupAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.fragment_stock_group, container, false);
        recyclerView = thisView.findViewById(R.id.recycle_stock_group);
        recyclerView.setAdapter(groupsAdapter);
        if (todayFolder == null)
            new ReadyToday();

        if (mStockGroupPos > 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            assert layoutManager != null;
            layoutManager.scrollToPositionWithOffset(
                    mStockGroupPos, (mStockGroupPos > 3) ? mStockGroupPos - 3 : mStockGroupPos - 2);
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
            stockGetPut.get();
        } else if (item.getItemId() == R.id.clear_matched_number) {
            for (int i = 0; i < sGroups.size(); i++) {
                SGroup sGroup = sGroups.get(i);
                sGroups.set(i, sGroup);
            }
        } else if (item.getItemId() == R.id.saveStocks) {
            stockGetPut.put("All save");
            stockGetPut.sort();
        }
        groupsAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }
}