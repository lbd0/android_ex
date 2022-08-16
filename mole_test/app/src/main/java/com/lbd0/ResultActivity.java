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
    public static SharedPreferences spf = null;     // 최고 점수 저장

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_result);

        spf = getSharedPreferences("spfScore", MODE_PRIVATE); // 키 값이 또 있으면 덮어씀

        sub_result = findViewById(R.id.sub_result);
        sub_retry = findViewById(R.id.sub_retry);

        int score = getIntent().getIntExtra("score", -1);   // 점수
        sub_result.setText(String.valueOf(score));      // 텍스트뷰로 점수 표시

        if(spf == null) {   // null이면 최고 점수 0으로 세팅 근데 이거 예외 처리로 안 되나?
            spf.edit().putInt("spfscore", 0).commit();
        }

        if(spf.getInt("spfscore",0) < score) {  // 최고 점수가 이번 점수보다 작으면 
            spf.edit().putInt("spfscore", score).commit();  // 이번 점수를 최고 점수로 올림
            sub_result.setText("New Best\n" + score);       // 텍스트 수정
        } else {    
            sub_result.setText("Score\n" + score);          // 텍스트 수정
        }

        sub_retry.setOnClickListener(new View.OnClickListener(){    // 다시하기 버튼 
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);    
                startActivity(intent);      // MainActivitiy 실행
                finish();       // 종료
            }
        });
    }

}
