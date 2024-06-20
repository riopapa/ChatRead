package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.gIdx;
import static biz.riopapa.chatread.MainActivity.gSheetUpload;
import static biz.riopapa.chatread.MainActivity.groupWhoAdapter;
import static biz.riopapa.chatread.MainActivity.groupWhoStockAdapter;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.nowSStock;
import static biz.riopapa.chatread.MainActivity.nowSWho;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sIdx;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.wIdx;
import static biz.riopapa.chatread.edit.ActivityEditGroupWho.whoContext;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.func.GooglePercent;
import biz.riopapa.chatread.func.GoogleStatement;
import biz.riopapa.chatread.models.SGroup;
import biz.riopapa.chatread.models.SStock;
import biz.riopapa.chatread.models.SWho;

public class GroupWhoStockAdapter extends RecyclerView.Adapter<GroupWhoStockAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        return nowSWho.stocks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tKey1, tKey2, tPrev, tNext, tCount, tSkip, tTalk;
        View tLine;

        ViewHolder(final View itemView) {
            super(itemView);
            tLine = itemView.findViewById(R.id.stock_line);
            tKey1 = itemView.findViewById(R.id.stock_key1);
            tKey2 = itemView.findViewById(R.id.stock_key2);
            tPrev = itemView.findViewById(R.id.stock_prv);
            tNext = itemView.findViewById(R.id.stock_nxt);
            tSkip = itemView.findViewById(R.id.stock_skip);
            tTalk = itemView.findViewById(R.id.stock_talk);
            tCount = itemView.findViewById(R.id.stock_count);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_group_who_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        SStock stock = nowSWho.stocks.get(position);
        holder.tKey1.setText(stock.key1);
        holder.tKey2.setText(stock.key2);
        holder.tPrev.setText(stock.prv);
        holder.tNext.setText(stock.nxt);
        holder.tCount.setText(""+stock.count);
        holder.tSkip.setText(stock.skip1);
        holder.tTalk.setText(stock.talk);

        holder.tLine.setOnClickListener(v -> {
            sIdx = holder.getAdapterPosition();
            nowSStock = nowSWho.stocks.get(sIdx);
            editStock();
        });
    }

    void editStock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(whoContext);
        LayoutInflater inflater = LayoutInflater.from(whoContext);
        View dialogView = inflater.inflate(R.layout.dialog_who_stock, null);
        EditText eKey1, eKey2, ePrev, eNext, eCount, eSkip, eTalk;
        // Find the EditText views from the layout
        eKey1 = dialogView.findViewById(R.id.dlg_key1);
        eKey2 = dialogView.findViewById(R.id.dlg_key2);
        ePrev = dialogView.findViewById(R.id.dlg_prev);
        eNext = dialogView.findViewById(R.id.dlg_next);
        eCount = dialogView.findViewById(R.id.dlg_count);
        eSkip = dialogView.findViewById(R.id.dlg_skip);
        eTalk = dialogView.findViewById(R.id.dlg_talk);
        eKey1.setText(nowSStock.key1);
        eKey2.setText(nowSStock.key2);
        ePrev.setText(nowSStock.prv);
        eNext.setText(nowSStock.nxt);
        eCount.setText(""+nowSStock.count);
        eSkip.setText(nowSStock.skip1);
        eTalk.setText(nowSStock.talk);

        builder.setView(dialogView)
            .setTitle("Edit Stock")
            .setPositiveButton("Updt", (dialog, which) -> {
                try {
                    SStock nStock = (SStock) nowSStock.clone();
                    nStock.key1 = eKey1.getText().toString();
                    nStock.key2 = eKey2.getText().toString();
                    nStock.prv = ePrev.getText().toString();
                    nStock.nxt = eNext.getText().toString();
                    nStock.talk = eTalk.getText().toString();
                    nStock.count = Integer.parseInt(eCount.getText().toString());
                    nStock.skip1 = eSkip.getText().toString();
                    nowSWho.stocks.set(sIdx, nStock);
                    nowSStock = nowSWho.stocks.get(sIdx);
                    nowSGroup.whos.set(wIdx, (SWho) nowSWho.clone());
                    nowSWho = nowSGroup.whos.get(wIdx);
                    sGroups.set(gIdx, (SGroup) nowSGroup.clone());
                    nowSGroup = sGroups.get(gIdx);
                    stockGetPut.put("stock");
                    stockGetPut.get();
                    groupWhoStockAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }

                // Implement logic for "Apply" button (e.g., get data from EditTexts)
            })
            .setNeutralButton("Dup", (dialog, which) -> {
                try {
                    SStock nStock = (SStock) nowSStock.clone();
                    nStock.key1 = eKey1.getText().toString();
                    nStock.key2 = eKey2.getText().toString();
                    nStock.prv = ePrev.getText().toString();
                    nStock.nxt = eNext.getText().toString();
                    nStock.talk = eTalk.getText().toString();
                    nStock.count = Integer.parseInt(eCount.getText().toString());
                    nStock.skip1 = eSkip.getText().toString();
                    nowSWho.stocks.add(sIdx, nStock);
                    nowSStock = nowSWho.stocks.get(sIdx);
                    nowSGroup.whos.set(wIdx, (SWho) nowSWho.clone());
                    nowSWho = nowSGroup.whos.get(wIdx);
                    sGroups.set(gIdx, (SGroup) nowSGroup.clone());
                    nowSGroup = sGroups.get(gIdx);
                    stockGetPut.put("stock dup "+nowSGroup.grpF +" "+ nowSWho.whoF+ " / " + nStock.key1);
                    stockGetPut.get();
                    groupWhoStockAdapter.notifyDataSetChanged();
                    upload2Google();
                    dialog.dismiss();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            })
            .setNegativeButton("Del", (dialog, which) -> {
                if (nowSWho.stocks.size() > 1) {
                    nowSWho.stocks.remove(sIdx);
                    nowSGroup.whos.set(wIdx, nowSWho);
                    sGroups.set(gIdx, nowSGroup);
                    stockGetPut.put("stock");
                    stockGetPut.get();
                    groupWhoStockAdapter.notifyDataSetChanged();
                    nowSGroup.whos.remove(wIdx);
                    wIdx = 0;
                    sGroups.set(gIdx, nowSGroup);
                    stockGetPut.put("stock del "+nowSGroup.grpF +" "+ nowSWho.whoF);
                    stockGetPut.get();
                    groupWhoAdapter.notifyDataSetChanged();
                    upload2Google();
                    dialog.dismiss();
                }
            })
            ;

        builder.create().show();
    }

    void upload2Google() {
        final String GROUP = ")_(";
        String mPercent = new GooglePercent().make(nowSGroup);
        String mStatement = new GoogleStatement().make(nowSGroup,",");
        String mTalk = new SimpleDateFormat("yy/MM/dd\nHH:mm", Locale.KOREA).format(new Date());
        gSheetUpload.uploadGroupInfo(nowSGroup.grp, GROUP, nowSGroup.grpM, mPercent,
                mTalk, mStatement, "key12");

    }
}