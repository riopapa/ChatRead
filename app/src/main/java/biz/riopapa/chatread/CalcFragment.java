package biz.riopapa.chatread;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class CalcFragment extends Fragment {
    private TextView res;
    private String operation="";
    private int nbr1, nbr2;
    private boolean op = true;
    private boolean virgule = false;
    private int p = 1;
    private float decimal1,decimal2;


    public CalcFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_calc, container, false);
        res = v.findViewById(R.id.txtResult);
        Button btn0 = v.findViewById(R.id.btn0);
        Button btn1 = v.findViewById(R.id.btn1);
        Button btn2 = v.findViewById(R.id.btn2);
        Button btn3 = v.findViewById(R.id.btn3);
        Button btn4 = v.findViewById(R.id.btn4);
        Button btn5 = v.findViewById(R.id.btn5);
        Button btn6 = v.findViewById(R.id.btn6);
        Button btn7 = v.findViewById(R.id.btn7);
        Button btn8 = v.findViewById(R.id.btn8);
        Button btn9 = v.findViewById(R.id.btn9);
        Button btnDiv = v.findViewById(R.id.btnDiv);
        Button btnMul = v.findViewById(R.id.btnMul);
        Button btnSous = v.findViewById(R.id.btnSou);
        Button btnAdd = v.findViewById(R.id.btnAdd);
        Button btnPoint = v.findViewById(R.id.btnDot);
        Button btnEqual = v.findViewById(R.id.btnEq);
        Button btnReset = v.findViewById(R.id.btnAc);
        Button btnplusoumoins = v.findViewById(R.id.btnaddOrMoins);


        btn0.setOnClickListener(this::onClick);
        btn1.setOnClickListener(this::onClick);
        btn2.setOnClickListener(this::onClick);
        btn3.setOnClickListener(this::onClick);
        btn4.setOnClickListener(this::onClick);
        btn5.setOnClickListener(this::onClick);
        btn6.setOnClickListener(this::onClick);
        btn7.setOnClickListener(this::onClick);
        btn8.setOnClickListener(this::onClick);
        btn9.setOnClickListener(this::onClick);
        btnDiv.setOnClickListener(this::onClick);
        btnMul.setOnClickListener(this::onClick);
        btnSous.setOnClickListener(this::onClick);
        btnAdd.setOnClickListener(this::onClick);
        btnPoint.setOnClickListener(this::onClick);
        btnEqual.setOnClickListener(this::onClick);
        btnReset.setOnClickListener(this::onClick);
        btnplusoumoins.setOnClickListener(this::onClick);

        return v;

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn0:
            case R.id.btn1:
            case R.id.btn2:
            case R.id.btn3:
            case R.id.btn4:
            case R.id.btn5:
            case R.id.btn6:
            case R.id.btn7:
            case R.id.btn8:
            case R.id.btn9:
                lireChiffre(v);
                break;
            case R.id.btnDiv:
            case R.id.btnMul:
            case R.id.btnSou:
            case R.id.btnAdd:
                lireOperation(v);
                break;
            case R.id.btnDot:
                activerVirgule(v);
                break;
            case R.id.btnEq:
                calculer(v);
                break;
            case R.id.btnAc:
                reset(v);
                break;
            case R.id.btnaddOrMoins:
                negatif(v);
                break;

        }
    }
    public void afficher() {
        if (!virgule) {
            if (decimal1 != 0) {
                if (op) {
                    res.setText(String.valueOf(nbr1 + decimal1) + operation);
                } else {
                    res.setText(String.valueOf(nbr1 + decimal1) + operation + String.valueOf(nbr2));
                }
            } else {
                if (op) {
                    res.setText(String.valueOf(nbr1) + operation);
                } else {
                    res.setText(String.valueOf(nbr1) + operation + String.valueOf(nbr2));
                }
            }
        } else {
            if (op) {
                res.setText(String.valueOf(nbr1 + decimal1) + operation);
            } else {
                res.setText(String.valueOf(nbr1 + decimal1) + operation + String.valueOf(nbr2 + decimal2));
            }

        }
    }

    //-------------------lireDecimale---------------
    private void lireDecimale(View v) {

        float val = Float.parseFloat(((Button)v).getText().toString());
        if(op){
            decimal1 = (float)(decimal1 +  val*Math.pow(10,-p));
            afficher();
        }else {
            decimal2 = (float)(decimal2 +  val*Math.pow(10,-p));
            afficher();
        }
        p++;
    }
    public void lireChiffre(View v){
        int val = Integer.parseInt(((Button)v).getText().toString());
        if(!virgule){
            if(op){
                nbr1 = nbr1*10 + val;
                afficher();
            }else {
                nbr2 = nbr2*10 + val;
                afficher();
            }
        }else{
            lireDecimale(v);
        }
    }


    //--------------------------------------------------
    public void lireOperation(View v){
        switch (v.getId()){
            case R.id.btnMul:
                operation = "×";
                break;
            case R.id.btnAdd:
                operation = "+";
                break;
            case R.id.btnSou:
                operation = "-";
                break;
            case  R.id.btnDiv:
                operation = "/";
                break;

        }
        afficher();
        p=1;
        virgule = false;
        op = false;

    }
    public void negatif(View v) {

            if (op) {
                nbr1 = -nbr1;
                afficher();
            } else {
                nbr2 = -nbr2;
                afficher();
            }
    }
    //--------------calcule-----------------------
    public void calculer(View v){
        if(!op){
            switch (operation){
                case "+":
                    float som = (nbr1+decimal1) + (nbr2+decimal2);
                    nbr1 =(int) som;
                    decimal1 = som - nbr1;
                    break;
                case "-":
                    float soust = (nbr1+decimal1) - (nbr2+decimal2);
                    nbr1 =(int) soust;
                    decimal1 = soust - nbr1;
                    break;
                case "×":
                    float prod = (nbr1+decimal1) * (nbr2+decimal2);
                    nbr1 =(int) prod;
                    decimal1 = prod - nbr1;
                    break;
                case "/":
                    float div = (nbr1+decimal1) / (nbr2+decimal2);
                    nbr1 =(int) div;
                    decimal1 = div - nbr1;
                    break;
                default:
                    return;
            }
            nbr2 = 0;
            operation = "";
            op = true;
            afficher();

        }
    }
    //----------virgule----------
    public void activerVirgule(View v){
        virgule = true;
    }
    //------------reset---------------
    public void reset(View v){
        nbr1 = nbr2 = 0;
        decimal1 = decimal2 = 0;
        operation = "";
        op = true;
        virgule = false;
        p=1;
        afficher();
    }

}


