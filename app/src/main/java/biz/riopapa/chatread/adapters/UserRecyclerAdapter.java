package biz.riopapa.chatread.adapters;
import biz.riopapa.chatread.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import biz.riopapa.chatread.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.MyolderView> {
    List<User> users = new ArrayList<>();
    Context ctx;

    public UserRecyclerAdapter(List<User> users, Context ctx) {
        this.users = users;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public UserRecyclerAdapter.MyolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.list_view,parent,false);
        return new UserRecyclerAdapter.MyolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecyclerAdapter.MyolderView holder, int position) {
        User user=users.get(position);
        holder.imgU.setImageResource(user.getImage());
        holder.txtName.setText(user.getName());
        holder.txtJob.setText(user.getJob());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    public static class MyolderView extends RecyclerView.ViewHolder{
        ImageView imgU;
        TextView txtName,txtJob;
        public MyolderView(@NonNull View itemView) {
            super(itemView);
            imgU=itemView.findViewById(R.id.imgU);
            txtName=itemView.findViewById(R.id.txtName);
            txtJob=itemView.findViewById(R.id.txtJob);
        }
    }
}