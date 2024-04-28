package biz.riopapa.chatread.fragment;

import static biz.riopapa.chatread.MainActivity.aBar;
import static biz.riopapa.chatread.MainActivity.logSave;
import static biz.riopapa.chatread.MainActivity.logWork;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.nowFileName;
import static biz.riopapa.chatread.MainActivity.sharedEditor;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.alerts.AlertTableIO;
import biz.riopapa.chatread.common.SnackBar;
import biz.riopapa.chatread.common.WifiName;
import biz.riopapa.chatread.databinding.FragmentTableBinding;
import biz.riopapa.chatread.edit.ActivityEditTable;
import biz.riopapa.chatread.func.LogSpan;
import biz.riopapa.chatread.func.LogUpdate;
import biz.riopapa.chatread.func.OptionTables;
import biz.riopapa.chatread.models.DelItem;

public class FragmentTable extends Fragment {

    Menu mainMenu;
    FragmentTableBinding binding;
    public FragmentTable() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        aBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        aBar.setTitle("Table");
        aBar.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.bar_table));
        binding = FragmentTableBinding.inflate(getLayoutInflater());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.fragment_table, container, false);
        TextView tv_WifiState = thisView.findViewById(R.id.tv_wifi_state);
        String s = "Wifi : " + WifiName.get(mContext);
        tv_WifiState.setText(s);


        thisView.findViewById(R.id.sms_who_ignores).setOnClickListener(this::edit_table);
        thisView.findViewById(R.id.sms_text_ignores).setOnClickListener(this::edit_table);
        thisView.findViewById(R.id.sms_with_no_number).setOnClickListener(this::edit_table);
        thisView.findViewById(R.id.sms_replace).setOnClickListener(this::edit_table);

        thisView.findViewById(R.id.k_group_ignores).setOnClickListener(this::edit_table);
        thisView.findViewById(R.id.k_who_ignores).setOnClickListener(this::edit_table);
        thisView.findViewById(R.id.k_text_ignores).setOnClickListener(this::edit_table);
        thisView.findViewById(R.id.kt_no_number).setOnClickListener(this::edit_table);
        thisView.findViewById(R.id.who_names).setOnClickListener(this::edit_table);
        thisView.findViewById(R.id.group_telegrams).setOnClickListener(this::edit_table);

        thisView.findViewById(R.id.string_replace).setOnClickListener(this::edit_replace);

        return thisView;
    }

    public void edit_table(View v) {
        nowFileName = v.getTag().toString();
        Intent intent;
        intent = new Intent(mContext, ActivityEditTable.class);
        startActivity(intent);

    }

    public void edit_replace(View v) {
        nowFileName = v.getTag().toString();
/*        Intent intent = new Intent(mContext, ActivityStringReplace.class);
        startActivity(intent);

 */
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_frag_table, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.reload_all_tables) {
            new OptionTables().readAll();
//            AlertTable.readFile("read All");
            new AlertTableIO().get();
            new SnackBar().show("All Table", "Reloaded");
        }
        return super.onOptionsItemSelected(item);
    }
}