package biz.riopapa.chatread.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chatread.R;
import biz.riopapa.chatread.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserRecyclerView extends RecyclerView.Adapter<UserRecyclerView.MyolderView> {
    List<User> userss = new ArrayList<>();
    Context ctxx;

    public UserRecyclerView(List<User> users, Context ctx) {
        this.userss = users;
        this.ctxx = ctx;
    }

    @NonNull
    @Override
    public UserRecyclerView.MyolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(ctxx);
        View view = inflater.inflate(R.layout.listh_view,parent,false);
        return new UserRecyclerView.MyolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecyclerView.MyolderView holder, int position) {
        User user=userss.get(position);
        holder.imgU.setImageResource(user.getImage());
        holder.txtName.setText(user.getName());
        holder.txtJob.setText(user.getJob());

    }

    @Override
    public int getItemCount() {
        return userss.size();
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
