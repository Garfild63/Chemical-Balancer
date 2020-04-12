package com.garfild63.balancer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.garfild63.Balancer;

public class MainActivity extends Activity {
    private EditText et;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = findViewById(R.id.editText);
        tv = findViewById(R.id.textView);
    }
    
    public void onClick(View view) {
        String s = et.getText().toString();
        try {
            tv.setText(Balancer.balance(s));
        } catch (Exception e) {
            tv.setText(R.string.invalidEquation);
        }
    }
}
