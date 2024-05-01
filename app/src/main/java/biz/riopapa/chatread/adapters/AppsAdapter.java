package biz.riopapa.chatread.adapters;

import static biz.riopapa.chatread.MainActivity.appsPos;
import static biz.riopapa.chatread.MainActivity.apps;
import static biz.riopapa.chatread.MainActivity.mActivity;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.fragment.FragmentApps.fnd;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.edit.ActivityEditApp;
import biz.riopapa.chatread.func.AppsTable;
import biz.riopapa.chatread.models.App;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    static int colorT, colorF, colorExist, colorNone;
    static Drawable NotInstalled;

    @Override
    public int getItemCount() {
        if (apps == null || apps.isEmpty()) {
            new AppsTable().get();
        }
        return apps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        View tLine;
        TextView tMemo, tNickName, sSay, sLog, sGroup, sWho, sAddWho, sNum;
        PackageManager pm;
        ImageView icon;

        ViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            icon = itemView.findViewById(R.id.app_icon);

            tLine = itemView.findViewById(R.id.app_layout);
            tMemo = itemView.findViewById(R.id.app_memo);
            tNickName = itemView.findViewById(R.id.app_nick_name);

            sSay = itemView.findViewById(R.id.app_say);
            sLog = itemView.findViewById(R.id.app_log);
            sGroup = itemView.findViewById(R.id.app_group);
            sWho = itemView.findViewById(R.id.app_who);
            sAddWho = itemView.findViewById(R.id.app_addWho);
            sNum = itemView.findViewById(R.id.app_num);

            colorT = ContextCompat.getColor(mContext,R.color.appTrue);
            colorF = ContextCompat.getColor(mContext,R.color.appFalse);
            pm = mContext.getPackageManager();
            colorExist = ContextCompat.getColor(mContext,R.color.appExist);
            colorNone = ContextCompat.getColor(mContext,R.color.appNone);
            NotInstalled = ContextCompat.getDrawable(mContext, R.drawable.delete);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_apps, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        appsPos = holder.getAdapterPosition();

        App al = apps.get(appsPos);

        holder.tNickName.setText(al.nickName);
        holder.tNickName.setBackgroundColor((fnd[position]) ? Color.CYAN: Color.LTGRAY);
        holder.tMemo.setText(al.memo);

        Drawable drawable = getPackageIcon(al.fullName, holder.pm);
        if (drawable == null ) {
            holder.icon.setImageDrawable(NotInstalled);
            holder.tMemo.setTextColor(colorNone);
            holder.tNickName.setTextColor(colorNone);
        } else {
            holder.icon.setImageDrawable(drawable);
            holder.tMemo.setTextColor(colorExist);
            holder.tNickName.setTextColor(colorExist);
        }

        holder.sSay.setTextColor((al.say)? colorT:colorF);
        holder.sLog.setTextColor((al.log)? colorT:colorF);
        holder.sGroup.setTextColor((al.grp)? colorT:colorF);
        holder.sWho.setTextColor((al.who)? colorT:colorF);
        holder.sAddWho.setTextColor((al.addWho)? colorT:colorF);
        holder.sNum.setTextColor((al.num)? colorT:colorF);

        holder.tLine.setOnClickListener(v -> {
            appsPos = holder.getAdapterPosition();
            Intent intent = new Intent(holder.context, ActivityEditApp.class);
            mActivity.startActivity(intent);
        });
    }
    private Drawable getPackageIcon(String packageName, PackageManager packageManager) {
        try {
            return packageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}