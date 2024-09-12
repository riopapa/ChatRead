package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.mActivity;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.gIDX;
import static biz.riopapa.chatread.MainActivity.wIDX;

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

public class WhoAdapter extends RecyclerView.Adapter<WhoAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        return sGroups.get(gIDX).whos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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

        SWho sWho = sGroups.get(gIDX).whos.get(position);
        holder.tWho.setText(sWho.who);
        holder.tWhoM.setText(sWho.whoM);
        holder.tWhoF.setText(sWho.whoF);
        StringBuilder info = new StringBuilder();
        for (SStock s : sWho.stocks) {
            if (info.length() > 0)
                info.append("\n");
            info.append(s.key1).append(" / ").append(s.key2).append(", ")
                    .append(s.prv).append(" / ").append(s.nxt).append(", ")
                    .append(s.count).append(", ").append(s.talk.isEmpty() ? "¯-¯": s.talk)
                    .append(", ").append(s.won);
        }
        holder.tInfo.setText(info.toString());
        holder.tLine.setOnClickListener(v -> {
            wIDX = holder.getAdapterPosition();
            Intent intent = new Intent(mContext, ActivityEditGroupWho.class);
            mActivity.startActivity(intent);
        });
    }

}