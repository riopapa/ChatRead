package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.gIDX;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.sGroups;
import static biz.riopapa.chatread.MainActivity.stockGetPut;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.ItemClickListener;
import biz.riopapa.chatread.R;
import biz.riopapa.chatread.models.SStock;
import biz.riopapa.chatread.models.SWho;
import biz.riopapa.chatread.models.SGroup;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private final ItemClickListener listener;

    public GroupAdapter(ItemClickListener listener) {
        this.listener = listener;
    }

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

        SGroup sgrp = sGroups.get(position);
        holder.tGroup.setText(sgrp.grp);
        holder.tGroupM.setText(sgrp.grpM);
        holder.tGroupF.setText(sgrp.grpF);

        SpannableStringBuilder grpBuilder = new SpannableStringBuilder();

        for (SWho w : sgrp.whos) {
            if (grpBuilder.length() > 1)
                grpBuilder.append("\n");
            SpannableString ssWho = new SpannableString(w.who + " : "+w.whoM + ", "+w.whoF + "\n");
            ssWho.setSpan(new ForegroundColorSpan(0xFF131455), 0, ssWho.length()
                    , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            grpBuilder.append(ssWho);
            SpannableStringBuilder stBuilder = new SpannableStringBuilder();
            for (SStock s : w.stocks) {
                if (stBuilder.length() > 1)
                    stBuilder.append("\n");
                SpannableString ssStock = new SpannableString(s.key1 + "/" + s.key2 + ", " + s.prv + "/" + s.nxt
                        + ", " + s.count + ", " + s.talk);
                ssStock.setSpan(new ForegroundColorSpan(0xFF132518), 0, ssStock.length()
                        , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssStock.setSpan(new BackgroundColorSpan(0xFFC9AABB), 0, ssStock.length()
                        , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stBuilder.append(ssStock);
            }
            grpBuilder.append(stBuilder);
        }

        holder.tSkip1.setText(sgrp.skip1);
        holder.tSkip2.setText(sgrp.skip2);
        holder.tSkip3.setText(sgrp.skip3);
        holder.tTelegram.setText(sgrp.telKa);
        holder.tIgnore.setText((sgrp.ignore) ? "무시" : "  ");
        holder.tInfo.setText(grpBuilder);
        holder.tLine.setBackgroundColor(ContextCompat.getColor(mContext,
                (gIDX == position)? R.color.line_now : R.color.line_default));
        holder.tLine.setOnClickListener(view -> listener.onItemClicked(position));
    }
}