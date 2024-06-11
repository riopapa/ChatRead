package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.nowSWho;
import static biz.riopapa.chatread.MainActivity.sIdx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.models.SStock;

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
        });
    }

}