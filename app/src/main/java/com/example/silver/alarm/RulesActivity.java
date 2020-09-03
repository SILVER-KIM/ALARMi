package com.example.silver.alarm;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static android.graphics.Color.TRANSPARENT;

public class RulesActivity extends Activity {
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        this.setFinishOnTouchOutside(false);

        relativeLayout = (RelativeLayout)findViewById(R.id.rulesBack);

        String what = getIntent().getExtras().getString("what");

        if(what.equals("mole")){
            relativeLayout.setBackgroundResource(R.drawable.mrules);
        }
        else if(what.equals("arrow")){
            relativeLayout.setBackgroundResource(R.drawable.arules);
        }
        else if(what.equals("shake")){
            relativeLayout.setBackgroundResource(R.drawable.srules);
        }

        getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

