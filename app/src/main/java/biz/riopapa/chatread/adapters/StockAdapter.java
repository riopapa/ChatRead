package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.gIDX;
import static biz.riopapa.chatread.MainActivity.gSheet;
import static biz.riopapa.chatread.MainActivity.stockRecyclerView;
import static biz.riopapa.chatread.MainActivity.whoAdapter;
import static biz.riopapa.chatread.MainActivity.stockAdapter;
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

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    SStock newStock;

    @Override
    public int getItemCount() {
        return sGroups.get(gIDX).whos.get(wIDX).stocks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tKey1, tKey2, tPrev, tNext, tCount, tSkip, tTalk, tWon;
        View tLine;

    private  ViewHolder(final View itemView) {
            super(itemView);
            tLine = itemView.findViewById(R.id.stock_line);
            tKey1 = itemView.findViewById(R.id.stock_key1);
            tKey2 = itemView.findViewById(R.id.stock_key2);
            tPrev = itemView.findViewById(R.id.stock_prv);
            tNext = itemView.findViewById(R.id.stock_nxt);
            tSkip = itemView.findViewById(R.id.stock_skip);
            tTalk = itemView.findViewById(R.id.stock_talk);
            tWon = itemView.findViewById(R.id.stock_won);
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

        String str;
        SStock stock = sGroups.get(gIDX).whos.get(wIDX).stocks.get(position);
        str = "Key1:"+stock.key1;       holder.tKey1.setText(str);
        str = "Key2:"+stock.key2;       holder.tKey2.setText(str);
        str = "Prv:"+stock.prv;         holder.tPrev.setText(str);
        str = "Nxt:"+stock.nxt;         holder.tNext.setText(str);
        str = "Cnt:" + stock.count;     holder.tCount.setText(str);
        str = "Skip:"+stock.skip1;      holder.tSkip.setText(str);
        holder.tTalk.setText(stock.talk.isEmpty() ? "x.x" : "Talk:"+stock.talk);
        str = "Won:"+stock.won;         holder.tWon.setText(str);
        holder.tLine.setOnClickListener(v -> {
            sIDX = holder.getAdapterPosition();
            try {
                newStock = (SStock) sGroups.get(gIDX).whos.get(wIDX).stocks.get(sIDX).clone();
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
        EditText eKey1, eKey2, ePrev, eNext, eCount, eSkip, eTalk, eWon;
        // Find the EditText views from the layout
        eKey1 = dialogView.findViewById(R.id.dlg_key1);
        eKey2 = dialogView.findViewById(R.id.dlg_key2);
        ePrev = dialogView.findViewById(R.id.dlg_prev);
        eNext = dialogView.findViewById(R.id.dlg_next);
        eCount = dialogView.findViewById(R.id.dlg_count);
        eSkip = dialogView.findViewById(R.id.dlg_skip);
        eTalk = dialogView.findViewById(R.id.dlg_talk);
        eWon = dialogView.findViewById(R.id.dlg_won);
        eKey1.setText(sGroups.get(gIDX).whos.get(wIDX).stocks.get(sIDX).key1);
        eKey2.setText(sGroups.get(gIDX).whos.get(wIDX).stocks.get(sIDX).key2);
        ePrev.setText(sGroups.get(gIDX).whos.get(wIDX).stocks.get(sIDX).prv);
        eNext.setText(sGroups.get(gIDX).whos.get(wIDX).stocks.get(sIDX).nxt);
        eCount.setText(""+sGroups.get(gIDX).whos.get(wIDX).stocks.get(sIDX).count);
        eSkip.setText(sGroups.get(gIDX).whos.get(wIDX).stocks.get(sIDX).skip1);
        eTalk.setText(sGroups.get(gIDX).whos.get(wIDX).stocks.get(sIDX).talk);
        eWon.setText(sGroups.get(gIDX).whos.get(wIDX).stocks.get(sIDX).won);

        builder.setView(dialogView)
            .setTitle("Edit Stock")
            .setPositiveButton("Updt", (dialog, which) -> {
                setNewStockFromActivity(eKey1, eKey2, ePrev, eNext, eTalk,
                                        eWon, eCount, eSkip);
                sGroups.get(gIDX).whos.get(wIDX).stocks.set(sIDX, newStock);
                gSheet.updateGSheetGroup(sGroups.get(gIDX));
                dataSetChanged();
                dialog.dismiss();
            })
            .setNeutralButton("Dup", (dialog, which) -> {
                setNewStockFromActivity(eKey1, eKey2, ePrev, eNext, eTalk,
                                        eWon, eCount, eSkip);
                sGroups.get(gIDX).whos.get(wIDX).stocks.add(newStock);
                gSheet.updateGSheetGroup(sGroups.get(gIDX));
                dataSetChanged();
                dialog.dismiss();
            })
            .setNegativeButton("Del", (dialog, which) -> {
                if (sGroups.get(gIDX).whos.get(wIDX).stocks.size() > 1) {
                    sGroups.get(gIDX).whos.get(wIDX).stocks.remove(sIDX);
                    gSheet.updateGSheetGroup(sGroups.get(gIDX));
//                    dialog.dismiss();
                    dataSetChanged();
                }
            })
            ;

        builder.create().show();
    }

    private void setNewStockFromActivity(EditText eKey1, EditText eKey2,
                         EditText ePrev, EditText eNext, EditText eTalk, EditText eWon,
                         EditText eCount, EditText eSkip) {
        newStock.key1 = eKey1.getText().toString();
        newStock.key2 = eKey2.getText().toString();
        newStock.prv = ePrev.getText().toString();
        newStock.nxt = eNext.getText().toString();
        newStock.talk = eTalk.getText().toString();
        newStock.won = eWon.getText().toString();
        newStock.count = Integer.parseInt(eCount.getText().toString());
        newStock.skip1 = eSkip.getText().toString();
    }

    void dataSetChanged() {
        stockGetPut.put("stock");
        stockAdapter = new StockAdapter();
        whoAdapter = new WhoAdapter();
        stockRecyclerView.setAdapter(stockAdapter);
        for (int i = 0; i < sGroups.get(gIDX).whos.size(); i++)
            whoAdapter.notifyItemChanged(i);
    }
}