package biz.riopapa.chatread.common;

import static android.widget.Toast.LENGTH_SHORT;
import static com.google.android.material.snackbar.Snackbar.make;
import static biz.riopapa.chatread.MainActivity.mMainActivity;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import biz.riopapa.chatread.R;

public class SnackBar {
    public void show(String title, String text) {

        View v = mMainActivity.findViewById(R.id.myFrame);
        Snackbar snackbar = make(v, "", LENGTH_SHORT);
        View sView = mMainActivity.getLayoutInflater().inflate(R.layout.snack_message, null);

        TextView tv1 = sView.findViewById(R.id.text_header);
        TextView tv2 = sView.findViewById(R.id.text_body);

        tv1.setText(title);
        tv2.setText(text);

        // now change the layout of the ToastText
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarLayout.setBackgroundColor(0x0033FFFF);  // transparent background
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)snackBarLayout.getLayoutParams();
        params.gravity = Gravity.CENTER_VERTICAL;
        sView.setLayoutParams(params);
        snackBarLayout.addView(sView, 0);
        snackbar.show();
    }

}
