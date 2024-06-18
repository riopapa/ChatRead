package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.gIdx;
import static biz.riopapa.chatread.MainActivity.mMainActivity;
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
import biz.riopapa.chatread.edit.ActivityEditGroup;
import biz.riopapa.chatread.models.SStock;
import biz.riopapa.chatread.models.SWho;
import biz.riopapa.chatread.models.SGroup;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        if (sGroups.isEmpty()) {
            stockGetPut.get();
        }
        return sGroups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tGroup, tGroupM, tGroupF, tSkip1, tSkip2, tSkip3, tIgnore, tTelegram, tInfo;
        View tLine;

        ViewHolder(final View itemView) {
            super(itemView);
            tLine = itemView.findViewById(R.id.one_line_group);
            tGroup = itemView.findViewById(R.id.one_group);
            tGroupM = itemView.findViewById(R.id.one_group_match);
            tGroupF = itemView.findViewById(R.id.one_group_full);
            tSkip1  = itemView.findViewById(R.id.one_group_skip1);
            tSkip2  = itemView.findViewById(R.id.one_group_skip2);
            tSkip3  = itemView.findViewById(R.id.one_group_skip3);
            tIgnore = itemView.findViewById(R.id.one_group_ignore);
            tTelegram = itemView.findViewById(R.id.one_group_telegram);
            tInfo = itemView.findViewById(R.id.one_group_info);
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
        holder.tGroupM.setText(sg.grpM);
        holder.tGroupF.setText(sg.grpF);
        String info = "";

        for (SWho w : sg.whos) {
            for (SStock s : w.stocks) {
                if (!info.isEmpty())
                    info += "\n";
                info += w.whoM + " : " + s.key1 + "/" + s.key2
                        + ", " + s.prv + "/" + s.nxt + ", " + s.count;
            }
        }
        holder.tSkip1.setText(sg.skip1);
        holder.tSkip2.setText(sg.skip2);
        holder.tSkip3.setText(sg.skip3);
        holder.tTelegram.setText(sg.telKa);
        holder.tIgnore.setText((sg.ignore) ? "무시" : "  ");
        holder.tInfo.setText(info);
        holder.tLine.setBackgroundColor(mContext.getResources().getColor(
                (gIdx == position)? R.color.line_now : R.color.line_default));

        holder.tLine.setOnClickListener(v -> {
            gIdx = holder.getAdapterPosition();
            nowSGroup = sGroups.get(gIdx);
            Intent intent = new Intent(mContext, ActivityEditGroup.class);
            mMainActivity.startActivity(intent);
            Log.e("onBindViewHolder"," group returned");
        });
    }

}