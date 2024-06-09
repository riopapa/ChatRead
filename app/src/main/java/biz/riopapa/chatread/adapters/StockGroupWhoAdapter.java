package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.mActivity;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.nowSWho;
import static biz.riopapa.chatread.MainActivity.wIdx;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.edit.ActivityEditAlert;
import biz.riopapa.chatread.models.SWho;

public class StockGroupWhoAdapter extends RecyclerView.Adapter<StockGroupWhoAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        return nowSGroup.whos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tWho, tWhoF, tStocks;
        View tLine;

        ViewHolder(final View itemView) {
            super(itemView);
            tLine = itemView.findViewById(R.id.grp_line_group_who);
            tWho = itemView.findViewById(R.id.grp_who);
            tWhoF = itemView.findViewById(R.id.grp_who_full);
            tStocks = itemView.findViewById(R.id.grp_who_stocks);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_group_who, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        SWho SWho = nowSGroup.whos.get(position);
        holder.tWho.setText(SWho.who);
        holder.tWhoF.setText(SWho.whoF);
        holder.tStocks.setText("Stocks: " + SWho.stocks.size());

        holder.tLine.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ActivityEditAlert.class);
            wIdx = holder.getAdapterPosition();
            nowSWho = nowSGroup.whos.get(wIdx);
            mActivity.startActivity(intent);
        });
    }

}