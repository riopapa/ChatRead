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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tGroup, tGroupM, tGroupF, tSkip1, tSkip2, tSkip3, tActive, tTelegram, tInfo, tLog;
        View tLine, tHead;

        ViewHolder(final View itemView) {
            super(itemView);
            tLine = itemView.findViewById(R.id.one_line_group);
            tHead = itemView.findViewById(R.id.one_line_head);
            tGroup = itemView.findViewById(R.id.one_group);
            tGroupM = itemView.findViewById(R.id.one_group_match);
            tGroupF = itemView.findViewById(R.id.one_group_full);
            tSkip1  = itemView.findViewById(R.id.one_group_skip1);
            tSkip2  = itemView.findViewById(R.id.one_group_skip2);
            tSkip3  = itemView.findViewById(R.id.one_group_skip3);
            tActive = itemView.findViewById(R.id.one_group_active);
            tTelegram = itemView.findViewById(R.id.one_group_telegram);
            tInfo = itemView.findViewById(R.id.one_group_info);
            tLog = itemView.findViewById(R.id.one_group_log);
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

        SGroup sGrp = sGroups.get(position);
        holder.tGroup.setText(sGrp.grp);
        holder.tGroupM.setText(sGrp.grpM);
        holder.tGroupF.setText(sGrp.grpF);
        int tColor = (sGrp.active) ? ContextCompat.getColor(mContext,R.color.black):
                                    ContextCompat.getColor(mContext,R.color.grey);
        holder.tGroup.setTextColor(tColor);
        holder.tGroupM.setTextColor(tColor);
        holder.tGroupF.setTextColor(tColor);

        int bColor = (sGrp.active) ? ContextCompat.getColor(mContext,R.color.teal_400):
                                ContextCompat.getColor(mContext,R.color.teal_200);
        holder.tHead.setBackgroundColor(bColor);

        SpannableStringBuilder grpBuilder = new SpannableStringBuilder();

        for (SWho w : sGrp.whos) {
            if (grpBuilder.length() > 1)
                grpBuilder.append("\n");
            SpannableString ssWho = new SpannableString(" " + w.who + " : "+w.whoM
                    + ", "+w.whoF + " \n");
            ssWho.setSpan(new BackgroundColorSpan(
                    ContextCompat.getColor(mContext,R.color.teal_300)),
                    0, ssWho.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            grpBuilder.append(ssWho);
            SpannableStringBuilder stBuilder = new SpannableStringBuilder();
            for (SStock s : w.stocks) {
                if (stBuilder.length() > 10)
                    stBuilder.append("\n");
                SpannableString ssStock = new SpannableString(
                        " " + s.key1 + "/" + s.key2 + ", " + s.prv + "/" + s.nxt
                        + ", " + s.count + ", "
                        + (s.talk.isEmpty() ? "\uD83E\uDD10": s.talk)
                        + ", " + s.won);
                stBuilder.append(ssStock);
            }
            grpBuilder.append(stBuilder);
        }

        holder.tSkip1.setTextColor(tColor);
        holder.tSkip2.setTextColor(tColor);
        holder.tSkip3.setTextColor(tColor);

        holder.tSkip1.setText(sGrp.skip1);
        holder.tSkip2.setText(sGrp.skip2);
        holder.tSkip3.setText(sGrp.skip3);
        holder.tTelegram.setText(sGrp.telKa);
        holder.tActive.setText((sGrp.active) ? "활성" : "조용");
        holder.tInfo.setText(grpBuilder);
        holder.tInfo.setTextColor(tColor);

        holder.tLog.setText((sGrp.log) ? "Log" : "no Log");
        holder.tLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.teal_100));
        holder.tLine.setOnClickListener(view -> listener.onItemClicked(position));
    }
}