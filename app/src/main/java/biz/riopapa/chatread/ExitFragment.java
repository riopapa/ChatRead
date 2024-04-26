package biz.riopapa.chatread;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExitFragment extends Fragment {

    public ExitFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exit, container, false);

    }

    public void exit(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Quitter l'application");
            builder.setMessage("Êtes-vous sûr de vouloir quitter l'application ?");
            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
            builder.setNegativeButton("Non", null);
            builder.show();
        }
    }