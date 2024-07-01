package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.gIDX;
import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.groupWhoAdapter;
import static biz.riopapa.chatread.MainActivity.groupWhoStockAdapter;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.nowSStock;
import static biz.riopapa.chatread.MainActivity.nowSWho;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.sIDX;
import static biz.riopapa.chatread.MainActivity.stockGetPut;
import static biz.riopapa.chatread.MainActivity.wIDX;
import static biz.riopapa.chatread.edit.ActivityEditGroupWho.whoContext;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.models.SStock;

public class GroupWhoStockAdapter extends RecyclerView.Adapter<GroupWhoStockAdapter.ViewHolder> {

    SStock newStock;

    @Override
    public int getItemCount() {
        return nowSWho.stocks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tKey1, tKey2, tPrev, tNext, tCount, tSkip, tTalk;
        View tLine;

    private   ViewHolder(final View itemView) {
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
        holder.tCount.setText("" + stock.count);
        holder.tSkip.setText(stock.skip1);
        holder.tTalk.setText(stock.talk);

        holder.tLine.setOnClickListener(v -> {
            sIDX = holder.getAdapterPosition();
            nowSStock = nowSWho.stocks.get(sIDX);
            try {
                newStock = (SStock) nowSStock.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
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
                newStock.key1 = eKey1.getText().toString();
                newStock.key2 = eKey2.getText().toString();
                newStock.prv = ePrev.getText().toString();
                newStock.nxt = eNext.getText().toString();
                newStock.talk = eTalk.getText().toString();
                newStock.count = Integer.parseInt(eCount.getText().toString());
                newStock.skip1 = eSkip.getText().toString();
                nowSWho.stocks.set(sIDX, newStock);
                nowSStock = nowSWho.stocks.get(sIDX);
                nowSGroup.whos.set(wIDX, nowSWho);
                nowSWho = nowSGroup.whos.get(wIDX);
                sGroups.set(gIDX, nowSGroup);
                nowSGroup = sGroups.get(gIDX);
                dataSetChanged();
                dialog.dismiss();

                // Implement logic for "Apply" button (e.g., get data from EditTexts)
            })
            .setNeutralButton("Dup", (dialog, which) -> {
                newStock.key1 = eKey1.getText().toString();
                newStock.key2 = eKey2.getText().toString();
                newStock.prv = ePrev.getText().toString();
                newStock.nxt = eNext.getText().toString();
                newStock.talk = eTalk.getText().toString();
                newStock.count = Integer.parseInt(eCount.getText().toString());
                newStock.skip1 = eSkip.getText().toString();
                nowSWho.stocks.add(newStock);
                nowSGroup.whos.set(wIDX, nowSWho);
                sGroups.set(gIDX, nowSGroup);
                dataSetChanged();
                dialog.dismiss();
                gSheet.updateGSheetGroup(nowSGroup);
            })
            .setNegativeButton("Del", (dialog, which) -> {
                if (nowSWho.stocks.size() > 1) {
                    nowSWho.stocks.remove(sIDX);
                    nowSGroup.whos.set(wIDX, nowSWho);
                    dialog.dismiss();
                    dataSetChanged();
                    gSheet.updateGSheetGroup(nowSGroup);
                }
            })
            ;

        builder.create().show();
    }

    void dataSetChanged() {
        for (int i = 0; i < nowSWho.stocks.size(); i++)
            groupWhoStockAdapter.notifyItemChanged(i);
        for (int i = 0; i < nowSGroup.whos.size(); i++)
            groupWhoAdapter.notifyItemChanged(i);

        stockGetPut.put("stock");
        stockGetPut.get();
    }
}