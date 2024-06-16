package biz.riopapa.chatread.fragment;

import static biz.riopapa.chatread.MainActivity.logSave;
import static biz.riopapa.chatread.MainActivity.logUpdate;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.toolbar;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.Arrays;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.common.SetFocused;
import biz.riopapa.chatread.common.SnackBar;
import biz.riopapa.chatread.func.KeyStringFind;
import biz.riopapa.chatread.func.KeyStringNext;
import biz.riopapa.chatread.func.LogSpan;
import biz.riopapa.chatread.func.ScrollUp;
import biz.riopapa.chatread.func.SelectChats;
import biz.riopapa.chatread.models.KakaoLog;

public class FragmentKaTalk extends Fragment {

    SpannableString ss;
    EditText etTable, eKey1, eKey2;
    ImageView ivFind, ivClear, ivNext;
    Menu mainMenu;
    ScrollView scrollView;
    int chatIdx = -1, chatMax, chatPos = -1;
    File[] chatFolders = null; // KakaoTalk > Chats > KakaoTalk_Chats_nnnn s
    File[] nowChatFiles;
    // KakaoTalk > Chats > KakaoTalk_Chats_2021-04-01_2 > KakaoTalkChats.txt
    File nowChatFile;
    KakaoLog kaLog;
    public FragmentKaTalk() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        toolbar.setTitle("");
        toolbar.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.bar_save));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.fragment_katalk, container, false);
        eKey1 = thisView.findViewById(R.id.ka_search_key1);
        eKey2 = thisView.findViewById(R.id.ka_search_key2);
        ivFind = thisView.findViewById(R.id.ka_search);
        ivNext = thisView.findViewById(R.id.ka_find_next);
        ivClear = thisView.findViewById(R.id.ka_clear_key);
        scrollView = thisView.findViewById(R.id.ka_table_scroll);
        etTable = thisView.findViewById(R.id.ka_table_text);

        ss = new SpannableString("Chat Log View");
        etTable.setText(ss);

        ivNext.setVisibility(View.GONE);
        ivFind.setOnClickListener(v -> {
            new KeyStringFind(eKey1, etTable, ss, ivNext);
        });

        ivNext.setOnClickListener(v -> {
            new KeyStringNext(eKey1, etTable);
        });

        ivClear.setOnClickListener(v -> new SetFocused(eKey1));
        new Handler(Looper.getMainLooper()).post(() -> scrollView.smoothScrollBy(0, 90000));
        chatPos = -1;
        chatIdx = -1;
        chatMax = -1;
        getChatFolders();
        showChats();
        super.onResume();
        return thisView;
    }

    private void showChats() {
        if (chatFolders.length == 0)
            return;
        nowChatFiles = chatFolders[chatIdx].listFiles(file -> (file.getPath().endsWith(".txt")));
        if (nowChatFiles == null || nowChatFiles.length == 0) {
            new SnackBar().show("Folder "+ chatFolders[chatIdx]," empty or Error");
            //noinspection ResultOfMethodCallIgnored
            chatFolders[chatIdx].delete();
            getChatFolders();
            return;
        }
        nowChatFile = nowChatFiles[0];
        if (nowChatFile.getName().startsWith("Kakao")) {
            kaLog = new SelectChats().generate(nowChatFile, false);
        }
        else {
            new SnackBar().show("Chat File", "not start with Kakao");
            getChatFolders();
            return;
        }
        etTable.setText(kaLog.ss);
        etTable.setOnClickListener(v -> {
            chatPos = -1;
            String key1 = eKey1.getText().toString();
            String key2 = eKey2.getText().toString();
            if (key2.length() < 2)
                key2 = null;
            String chatText = kaLog.ss.toString();
            String[] lines = chatText.split("\n");
            int cnt = 0;
            int pos1, pos2;
            int nPos = 0;
            for (String line : lines) {
                boolean found = false;
                pos1 = line.indexOf(key1);
                if (pos1 > 0) {
                    if (chatPos < 0)
                        chatPos = chatText.indexOf(key1);
                    if (key2 != null) {
                        pos2 = line.indexOf(key2);
                        if (pos2 > 0) {
                            kaLog.ss.setSpan(new BackgroundColorSpan(0xFF00FF00), nPos + pos1,
                                    nPos + pos1 + key1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            kaLog.ss.setSpan(new BackgroundColorSpan(0xFF00FF00), nPos + pos2,
                                    nPos + pos2 + key2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            found = true;
                        }
                    } else {
                        kaLog.ss.setSpan(new BackgroundColorSpan(0xFFFFFF00), nPos + pos1,
                                nPos + pos1 + key1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        found = true;
                    }
                }
                if (found)
                    cnt++;
                nPos += line.length() + 1;
            }

            etTable.setText(kaLog.ss);
            new SnackBar().show("Keywords","Total "+cnt+" keywords found");
            if (chatPos > 0) {
                Editable editText = etTable.getText();
                Selection.setSelection(editText, chatPos);
                etTable.requestFocus();
                ivNext.setVisibility(View.VISIBLE);
            }
        });

        ivClear.setOnClickListener(v -> {
            eKey1 =  this.getView().findViewById(R.id.ka_search_key1);
            eKey2 =  this.getView().findViewById(R.id.ka_search_key2);
            eKey1.setText("");
            eKey2.setText("");
        });

        ivNext.setVisibility(View.GONE);
        ivNext.setOnClickListener(v -> {
            if (chatPos > 0) {
                eKey1 =  this.getView().findViewById(R.id.ka_search_key1);
                eKey2 =  this.getView().findViewById(R.id.ka_search_key2);
                String key1 = eKey1.getText().toString();
                String chatText = etTable.getText().toString();
                chatPos = chatText.indexOf(key1, chatPos + 1);
                if (chatPos > 0) {
                    Editable editText = etTable.getText();
                    Selection.setSelection(editText, chatPos);
                    etTable.requestFocus();
                }
            }
        });
        showChatCounts();
    }

    private void showChatCounts() {
        if (chatIdx == -1) {
            getChatFolders();
            chatIdx = chatMax - 1;
        }
        toolbar.setTitle(kaLog.grp);
        toolbar.setSubtitle("  "+(chatIdx+1)+" / "+(chatFolders.length));
    }

    private void getChatFolders() {
        File kChatFolder = new File(Environment.getExternalStorageDirectory(), "Documents/KakaoTalk/Chats");
        chatFolders = kChatFolder.listFiles();
        if (chatFolders == null || chatFolders.length == 0)
            return;
        Arrays.sort(chatFolders);
        chatMax = chatFolders.length;
        chatIdx = chatMax-1;
    }

    View uploadMenu;
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_katalk, menu);
        super.onCreateOptionsMenu(menu, inflater);
        new Handler(Looper.getMainLooper()).post(() -> {
            uploadMenu = this.getView().findViewById(R.id.action_upload);
            if (uploadMenu != null) {
                uploadMenu.setOnLongClickListener(view -> {
                    kaLog = new SelectChats().generate(nowChatFile, true);
                    return false;
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_left && chatIdx > 0) {
            chatIdx--;
            showChats();
        } else if (item.getItemId() == R.id.action_right && chatIdx < chatFolders.length-1) {
            chatIdx++;
            showChats();
        } else if (item.getItemId() == R.id.action_delete) {
            nowChatFile.delete();
            String name = nowChatFile.getName();
            String fullName = nowChatFile.toString().replace("/"+name, "");
            //noinspection ResultOfMethodCallIgnored
            new File (fullName).delete();
    //        new SnackBar().show(chatGroup, chatGroup+ " deleted\n");
            getChatFolders();
            chatIdx--;
            if (chatIdx < 0)
                chatIdx = 0;
            showChats();

        }
        return super.onOptionsItemSelected(item);
    }

}