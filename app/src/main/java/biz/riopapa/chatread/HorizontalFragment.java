package biz.riopapa.chatread;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import biz.riopapa.chatread.adapters.UserRecyclerAdapter;
import biz.riopapa.chatread.adapters.UserRecyclerView;
import biz.riopapa.chatread.models.User;

import java.util.ArrayList;
import java.util.List;

public class HorizontalFragment extends Fragment {

        int[] images={R.drawable.adam,R.drawable.ahmed,
                R.drawable.anastasia, R.drawable.elba,
                R.drawable.elena, R.drawable.jad,
                R.drawable.jennifer, R.drawable.karim,
                R.drawable.lela, R.drawable.sam};
        List<User> users=new ArrayList<>();
        RecyclerView recyclerView;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.fragment_horizontal, container, false);
            recyclerView=view.findViewById(R.id.myListH);
            getAllUsers();
            UserRecyclerView adapter = new UserRecyclerView(users, getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
            return view;
        }
        //---------------------------------------------
        public void getAllUsers(){
            String[] names = getResources().getStringArray(R.array.names_txt);
            String[] jobs = getResources().getStringArray(R.array.job_txt);
            for (int i=0;i<names.length;i++){
                users.add(new User(names[i],jobs[i],images[i]));
            }
        }

}