package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.mMainActivity;
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
import biz.riopapa.chatread.edit.ActivityEditGroupWho;
import biz.riopapa.chatread.models.SStock;
import biz.riopapa.chatread.models.SWho;

public class GroupWhoAdapter extends RecyclerView.Adapter<GroupWhoAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        return nowSGroup.whos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tWho, tWhoM, tWhoF, tInfo;
        View tLine;

        ViewHolder(final View itemView) {
            super(itemView);
            tLine = itemView.findViewById(R.id.line_group_who);
            tWho = itemView.findViewById(R.id.grp_who);
            tWhoM = itemView.findViewById(R.id.grp_who_match);
            tWhoF = itemView.findViewById(R.id.grp_who_full);
            tInfo = itemView.findViewById(R.id.who_info);
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

        SWho sWho = nowSGroup.whos.get(position);
        holder.tWho.setText(sWho.who);
        holder.tWhoM.setText(sWho.whoM);
        holder.tWhoF.setText(sWho.whoF);
        String info = "";
        for (SStock s : sWho.stocks) {
            if (!info.isEmpty())
                info += "\n";
            info += s.key1 + "/" + s.key2 + ", " + s.prv + "/" + s.nxt + ", " + s.count;
        }
        holder.tInfo.setText(info);
        holder.tLine.setOnClickListener(v -> {
            wIdx = holder.getAdapterPosition();
            nowSWho = nowSGroup.whos.get(wIdx);
            Intent intent = new Intent(mContext, ActivityEditGroupWho.class);
            mMainActivity.startActivity(intent);
        });
    }

}