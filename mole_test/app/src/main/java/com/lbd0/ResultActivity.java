package com.lbd0;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    TextView sub_result;
    Button sub_retry;
    public static SharedPreferences spf = null;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_result);

        spf = getSharedPreferences("spfScore", MODE_PRIVATE); // 키 값이 또 있으면 덮어씀

        sub_result = findViewById(R.id.sub_result);
        sub_retry = findViewById(R.id.sub_retry);

        int score = getIntent().getIntExtra("score", -1);
        sub_result.setText(String.valueOf(score));

        if(spf.getInt("spfscore",0) < score) {
            spf.edit().putInt("spfscore", score).commit();
            sub_result.setText("New Best\n" + score);
        } else {
            sub_result.setText("Score\n" + score);
        }

        sub_retry.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
