package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.gIdx;
import static biz.riopapa.chatread.MainActivity.mActivity;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.nowSGroup;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockGetPut;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.edit.ActivityEditStockGroup;
import biz.riopapa.chatread.models.SWho;
import biz.riopapa.chatread.models.SGroup;

public class StockGroupAdapter extends RecyclerView.Adapter<StockGroupAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        if (sGroups.isEmpty()) {
            stockGetPut.get();
        }
        return sGroups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tGroup, tGroupF, tWhos, tSkip1, tSkip2, tSkip3, tIgnore, tTelegram;
        View tLine;

        ViewHolder(final View itemView) {
            super(itemView);
            tLine = itemView.findViewById(R.id.one_line_group);
            tGroup = itemView.findViewById(R.id.one_group);
            tGroupF = itemView.findViewById(R.id.one_group_full);
            tWhos = itemView.findViewById(R.id.one_group_whos);
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

        SGroup sg = sGroups.get(position);
        holder.tGroup.setText(sg.grp);
        holder.tGroupF.setText(sg.grpF);
        String whos = "";
        for (SWho w : sg.whos) {
            whos += w.who + " ";
        }
        holder.tWhos.setText(whos);
        holder.tSkip1.setText(sg.skip1);
        holder.tSkip2.setText(sg.skip2);
        holder.tSkip3.setText(sg.skip3);
        holder.tTelegram.setText((sg.telKa == 't') ? "텔레" : "  ");
        holder.tIgnore.setText((sg.ignore) ? "무시" : "  ");

        holder.tLine.setBackgroundColor(mContext.getResources().getColor(
                (gIdx == position)? R.color.line_now : R.color.line_default));

        holder.tLine.setOnClickListener(v -> {
            gIdx = holder.getAdapterPosition();
            nowSGroup = sGroups.get(gIdx);
            Intent intent = new Intent(mContext, ActivityEditStockGroup.class);
            mActivity.startActivity(intent);
            Log.e("onBindViewHolder"," group returned");
        });
    }

}