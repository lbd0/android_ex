
package com.lbd0;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {


    TextView timer;     // 타이머 텍스트
    TextView score;     // 점수 텍스트
    TextView bestScore; // 최고점수 텍스트
    Button start;       // 시작 버튼

    ImageView[] img_array = new ImageView[9];   // 두더지 이미지 배열
    int[] imageID = {R.id.img1, R.id.img2, R.id.img3, R.id.img4, R.id.img5, R.id.img6, R.id.img7, R.id.img8, R.id.img9};

    final String TAG_ON = "on";     // 두더지가 올라옴
    final String TAG_OFF = "off";   // 두더지가 내려감
    int scores = 0;                 // 점수

    Thread thread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = findViewById(R.id.timer);
        score = findViewById(R.id.score);
        bestScore = findViewById(R.id.bestScore);
        start = findViewById(R.id.start);

        if(ResultActivity.spf == null) {
            bestScore.setText("Best Score : " + scores);
        } else {
            bestScore.setText("Best Score : " + ResultActivity.spf.getInt("spfscore",0));
        }       // 최고 점수 표시

        for(int i=0; i<img_array.length; i++) {
            img_array[i] = findViewById(imageID[i]);
            img_array[i].setImageResource(R.drawable.off);  // 두더지 들어가 있는 사진
            img_array[i].setTag(TAG_OFF);                   // 두더지 들어감

            img_array[i].setOnClickListener(new View.OnClickListener(){     // 그림 클릭 했는가
                public void onClick(View v) {
                    if(((ImageView)v).getTag().toString().equals(TAG_ON)) {     // 두더지가 올라와 있을 때 그림 클릭 했으면
                        Toast.makeText(getApplicationContext(), "good", Toast.LENGTH_SHORT).show();
                        score.setText(String.valueOf(scores++));    // 점수++
                        ((ImageView)v).setImageResource(R.drawable.off);    // 두더지 들어간 이미지로 변경
                        v.setTag(TAG_OFF);      // 들어감 태그로 변경
                    }else {
                        Toast.makeText(getApplicationContext(), "bad", Toast.LENGTH_SHORT).show();
                        if(scores<=0) {
                            scores = 0;
                            score.setText(String.valueOf(scores));
                        }else {
                            score.setText(String.valueOf(scores--));    // 점수--
                        }

                        ((ImageView)v).setImageResource(R.drawable.animal);
                        v.setTag(TAG_ON);       // 나옴 태그로 변경
                    }
                }
            });
        }

        timer.setText("Timer 30sec");   // 타이머 텍스트 세팅
        score.setText("0");             // 점수 텍스트 세팅

        start.setOnClickListener(new View.OnClickListener(){        // 시작 버튼 누르면 실행
            public void onClick(View v) {

                start.setVisibility(View.GONE);
                score.setVisibility(View.VISIBLE);

                thread=new Thread(new timeCheck());
                thread.start();

                for(int i=0; i< img_array.length; i++) {
                    new Thread(new DThread(i)).start();
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler onHandler = new Handler() {         // 나옴 핸들러

        public void handleMessage(Message msg) {
            img_array[msg.arg1].setImageResource(R.drawable.animal);    // 두더지가 나온 이미지로 설정
            img_array[msg.arg1].setTag(TAG_ON);                         // 나옴 태그로 설정
        }
    };

    @SuppressLint("HandlerLeak")
    Handler offHandler = new Handler() {        // 들어감 핸들러
        public void handleMessage(Message msg) {
            img_array[msg.arg1].setImageResource(R.drawable.off);      // 두더지가 들어간 이미지로 설정
            img_array[msg.arg1].setTag(TAG_OFF);                       // 들어감 태그로 설정
        }
    };

    public class DThread implements Runnable {  // 코틀린으로 할 때는 코루틴을 써야하나
        int index = 0;

        DThread(int index) {
            this.index = index;
        }

        public void run() {
            while(true) {
                try{
                    Message msg1 = new Message();
                    int offtime = new Random().nextInt(5000) + 500;     // 들어간 시간
                    Thread.sleep(offtime);

                    msg1.arg1 = index;
                    onHandler.sendMessage(msg1);

                    int ontime = new Random().nextInt(1000) + 500;      // 나온 시간
                    Thread.sleep(ontime);
                    Message msg2 = new Message();
                    msg2.arg1 = index;
                    offHandler.sendMessage(msg2);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {       // 타이머 텍스트 설정 핸들러
        public void handleMessage(Message msg) {
            timer.setText("Timer " + msg.arg1 + "sec");
        }
    };

    public class timeCheck implements Runnable{     // 타이머 클래스

        final int MAXTIME = 30; // 제한 시간

        public void run() {     // 시간 점점 줄어듦
            for(int i=MAXTIME; i >= 0; i--) {
                Message msg = new Message();
                msg.arg1 = i;
                handler.sendMessage(msg);

                try{        // 예외처리?
                    Thread.sleep(1000);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Intent intent = new Intent(MainActivity.this, ResultActivity.class);    // 인텐트
            intent.putExtra("score" , scores);      // 점수 보내
            startActivity(intent);  // ResultActivity 실행
            finish();   // 종료
        }
    }
}