package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.alerts;
import static biz.riopapa.chatread.MainActivity.mAlertPos;
import static biz.riopapa.chatread.MainActivity.mMainActivity;
import static biz.riopapa.chatread.MainActivity.mContext;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.stocks.AlertTable;
import biz.riopapa.chatread.edit.ActivityEditAlert;
import biz.riopapa.chatread.models.Alert;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        if (alerts == null || alerts.isEmpty()) {
            new AlertTable().get();
        }
        return alerts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tGroup, tWho, tKey1, tKey2, tPrev, tNext, tTalk, tCount, tSkip, tQuiet;
        View tLine;

        ViewHolder(final View itemView) {
            super(itemView);
            tLine = itemView.findViewById(R.id.one_alert_line);
            tGroup = itemView.findViewById(R.id.one_group);
            tWho = itemView.findViewById(R.id.one_who);
            tKey1 = itemView.findViewById(R.id.one_key1);
            tKey2 = itemView.findViewById(R.id.one_key2);
            tPrev = itemView.findViewById(R.id.one_prev);
            tNext = itemView.findViewById(R.id.one_next);
            tTalk = itemView.findViewById(R.id.one_talk);
            tCount = itemView.findViewById(R.id.one_matched);
            tSkip = itemView.findViewById(R.id.one_skip);
            tQuiet = itemView.findViewById(R.id.one_quiet);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_alert, parent, false);
        return new ViewHolder(view);
    }

    String svGroup = "sv", svWho = "sv";
    int textColor = 0xFF000000;
    final int textBlack = 0xff000000;
    final int textRed = 0xff771111;

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Alert al = alerts.get(position);
        if (!svGroup.equals(al.group))
            svGroup = al.group;

        holder.tGroup.setText(al.group);
        int matched = al.matched;
        String who = al.who;
        holder.tWho.setText(who);
        if (!svWho.equals(al.who))
            svWho = al.who;

        holder.tKey1.setText(al.key1);
        holder.tKey2.setText(al.key2);
        holder.tPrev.setText(al.prev);
        holder.tNext.setText(al.next);
        holder.tSkip.setText(al.skip);
        holder.tSkip.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f));
        if (al.matched == -1) {
            textColor = (al.quiet)? textRed : textBlack;
            holder.tCount.setVisibility(View.GONE);
            holder.tTalk.setText(al.talk);
            holder.tTalk.setTextColor(ContextCompat.getColor(mContext, R.color.textFore));
            holder.tQuiet.setText((al.quiet)? "quiet" : "  ");
            holder.tQuiet.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, .5f));
            holder.tLine.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_header));
        } else {
            textColor = (al.quiet)? ContextCompat.getColor(mContext, R.color.quietTalk) : textBlack;
            holder.tGroup.setText("");
            holder.tWho.setSingleLine(true);
            holder.tWho.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.tWho.setSelected(true);
            holder.tCount.setVisibility(View.VISIBLE);
            holder.tCount.setText(String.valueOf(matched));
            holder.tTalk.setText(al.talk);
            holder.tTalk.setTextColor(ContextCompat.getColor(mContext, R.color.alertTalk));
            holder.tQuiet.setText((al.quiet)? "quiet" : "  ");
            holder.tQuiet.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, .5f));
            holder.tLine.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_line));
        }

        holder.tGroup.setTextColor(textColor);
        holder.tWho.setTextColor(textColor);
        holder.tKey1.setTextColor(textColor);
        holder.tKey2.setTextColor(textColor);
        holder.tPrev.setTextColor(textColor);
        holder.tNext.setTextColor(textColor);
        holder.tCount.setTextColor(textColor);
        holder.tSkip.setTextColor(textColor);
        holder.tTalk.setTextColor(textColor);
        holder.tQuiet.setTextColor(textColor);

        holder.tLine.setOnClickListener(v -> {
        Intent intent = new Intent(mContext, ActivityEditAlert.class);
        mAlertPos = holder.getAdapterPosition();
        mMainActivity.startActivity(intent);
        });
    }

}