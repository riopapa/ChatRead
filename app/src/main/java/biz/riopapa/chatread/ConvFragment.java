package biz.riopapa.chatread;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ConvFragment extends Fragment {

    private TextView txt1;
    private TextView txt2;
    private TextView txt3;

    private Button btn,btn2,btn3, btnAnnuler;

    private EditText nbr;
    private EditText nbr2;
    private EditText nbr3;

    public ConvFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_conv, container, false);
        txt1 = view.findViewById(R.id.txtCF);
        txt2 = view.findViewById(R.id.txtmile);
        txt3 = view.findViewById(R.id.txtlbs);

        btn=view.findViewById(R.id.btnCF);
        btn2=view.findViewById(R.id.btnKM);
        btn3=view.findViewById(R.id.btnKG);
        btnAnnuler=view.findViewById(R.id.btnAnnuler);

        nbr=view.findViewById(R.id.txtA);
        nbr2=view.findViewById(R.id.txtB);
        nbr3=view.findViewById(R.id.txtC);


        btn.setOnClickListener(v -> {
            if (nbr.getText().toString().isEmpty()) {
                txt1.setText("Pls enter a temperature");
            } else {
            float a=Float.parseFloat(nbr.getText().toString());
            txt1.setText(a*9/5 +  32+" °F");
            txt1.setTextColor(Color.RED);}
        });

        btn2.setOnClickListener(v -> {
            if (nbr2.getText().toString().isEmpty()) {
                txt2.setText("Pls enter a distance");
            } else {
            float b=Float.parseFloat(nbr2.getText().toString());
            txt2.setText(String.format("%.2f miles", b/1.609));
            txt2.setTextColor(Color.RED);}
        });

        btn3.setOnClickListener(v -> {
            if (nbr3.getText().toString().isEmpty()) {
                txt3.setText("Pls enter a weight");
            } else {
            float c=Float.parseFloat(nbr3.getText().toString());
            txt3.setText(String.format("%.2f lbs", c*2.205));
            txt3.setTextColor(Color.RED);}
        });
        btnAnnuler.setOnClickListener(v -> {
            txt1.setText("0");
            txt2.setText("0");
            txt3.setText("0");
            nbr.setText("");
            nbr2.setText("");
            nbr3.setText("");
        });


        return view;
    }}
