package biz.riopapa.chatread.fragment;

import static biz.riopapa.chatread.MainActivity.gIDX;
import static biz.riopapa.chatread.MainActivity.groupListener;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.groupAdapter;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.adapters.GroupAdapter;
import biz.riopapa.chatread.func.OptionTables;
import biz.riopapa.chatread.func.ReadyToday;
import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.SStock;
import biz.riopapa.chatread.models.SWho;

public class FragmentStockList extends Fragment {

    Menu mainMenu;

    public static RecyclerView groupRecyclerView;
    public FragmentStockList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (toolbar != null)
            toolbar.setTitle("StockList");
        groupAdapter = new GroupAdapter(groupListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.fragment_stock_group, container, false);
        groupRecyclerView = thisView.findViewById(R.id.recycle_stock_group);
        groupRecyclerView.setAdapter(groupAdapter);
        if (todayFolder == null)
            new ReadyToday();
        thisView.invalidate();
        return thisView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupRecyclerView = view.findViewById(R.id.recycle_stock_group);
        groupRecyclerView.setAdapter(groupAdapter);
        if (gIDX > 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) groupRecyclerView
                    .getLayoutManager();
            assert layoutManager != null;
            layoutManager.scrollToPositionWithOffset(
                    gIDX, (gIDX > 3) ? gIDX - 3 : gIDX - 2);
        }
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
            stockGetPut.getFromSVFile();
            stockGetPut.setStockTelKaCount();

        } else if (item.getItemId() == R.id.clear_matched_number) {
            for (int g = 0; g < sGroups.size(); g++) {
                SGroup sGroup = sGroups.get(g);
                for (int w = 0; w < sGroup.whos.size(); w++) {
                    SWho sWho = sGroup.whos.get(w);
                    for (int s = 0; s < sWho.stocks.size(); s++) {
                        SStock sStock = sWho.stocks.get(s);
                        if (sStock.count > 1000)
                            sStock.count = 1000;
                        else {
                            sStock.count = (sStock.count + 99) / 100 * 100;
                        }
                        sWho.stocks.set(s, sStock);
                    }
                    sGroup.whos.set(w, sWho);
                }
                sGroups.set(g, sGroup);
            }
            stockGetPut.put("All reset count");
            stockGetPut.get();
        } else if (item.getItemId() == R.id.saveStocks) {
            stockGetPut.putSV("All save group");
        }
        groupAdapter = new GroupAdapter(groupListener);
        groupRecyclerView.setAdapter(groupAdapter);
        return super.onOptionsItemSelected(item);
    }

}