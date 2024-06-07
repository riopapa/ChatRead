package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.alerts;
import static biz.riopapa.chatread.MainActivity.gPos;
import static biz.riopapa.chatread.MainActivity.mActivity;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.stockGroups;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.stocks.StockGetPut;
import biz.riopapa.chatread.edit.ActivityEditAlert;
import biz.riopapa.chatread.models.StockGroup;

public class StockGroupAdapter extends RecyclerView.Adapter<StockGroupAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        if (alerts == null || alerts.isEmpty()) {
            new StockGetPut().get();
        }
        return stockGroups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tGroup, tGroupF, tSkip1, tSkip2, tSkip3, tIgnore, tTelegram;
        View tLine;

        ViewHolder(final View itemView) {
            super(itemView);
            tLine = itemView.findViewById(R.id.one_line_group);
            tGroup = itemView.findViewById(R.id.one_group);
            tGroupF = itemView.findViewById(R.id.one_group_full);
            tSkip1  = itemView.findViewById(R.id.one_group_skip1);
            tSkip2  = itemView.findViewById(R.id.one_group_skip2);
            tSkip3  = itemView.findViewById(R.id.one_group_skip3);
            tIgnore = itemView.findViewById(R.id.one_group_ignore);
            tTelegram = itemView.findViewById(R.id.one_group_telegram);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        StockGroup sg = stockGroups.get(position);
        holder.tGroup.setText(sg.grp);
        holder.tGroupF.setText(sg.grpF);
        holder.tSkip1.setText(sg.skip1);
        holder.tSkip2.setText(sg.skip2);
        holder.tSkip3.setText(sg.skip3);
        holder.tIgnore.setText((sg.ignore) ? "무시" : "  ");
        holder.tTelegram.setText((sg.telGrp) ? "텔레" : "  ");

        holder.tLine.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ActivityEditAlert.class);
            gPos = holder.getAdapterPosition();
            mActivity.startActivity(intent);
        });
    }

}