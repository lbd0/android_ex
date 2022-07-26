
package com.lbd0;

import androidx.appcompat.app.AppCompatActivity;

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


    TextView timer;
    TextView score;
    TextView bestScore;
    Button start;

    ImageView[] img_array = new ImageView[9];
    int[] imageID = {R.id.img1, R.id.img2, R.id.img3, R.id.img4, R.id.img5, R.id.img6, R.id.img7, R.id.img8, R.id.img9};

    final String TAG_ON = "on";
    final String TAG_OFF = "off";
    int scores = 0;

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
        }

        for(int i=0; i<img_array.length; i++) {
            img_array[i] = findViewById(imageID[i]);
            img_array[i].setImageResource(R.drawable.off);
            img_array[i].setTag(TAG_OFF);

            img_array[i].setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    if(((ImageView)v).getTag().toString().equals(TAG_ON)) {
                        Toast.makeText(getApplicationContext(), "good", Toast.LENGTH_LONG).show();
                        score.setText(String.valueOf(scores++));
                        ((ImageView)v).setImageResource(R.drawable.off);
                        v.setTag(TAG_OFF);
                    }else {
                        Toast.makeText(getApplicationContext(), "bad", Toast.LENGTH_LONG).show();
                        if(scores<=0) {
                            scores = 0;
                            score.setText(String.valueOf(scores));
                        }else {
                            score.setText(String.valueOf(scores--));
                        }

                        ((ImageView)v).setImageResource(R.drawable.animal);
                        v.setTag(TAG_ON);
                    }
                }
            });
        }

        timer.setText("Timer 30sec");
        score.setText("0");

        start.setOnClickListener(new View.OnClickListener(){
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

    Handler onHandler = new Handler() {

        public void handleMessage(Message msg) {
            img_array[msg.arg1].setImageResource(R.drawable.animal);
            img_array[msg.arg1].setTag(TAG_ON);
        }
    };

    Handler offHandler = new Handler() {
        public void handleMessage(Message msg) {
            img_array[msg.arg1].setImageResource(R.drawable.off);
            img_array[msg.arg1].setTag(TAG_OFF);
        }
    };

    public class DThread implements Runnable {
        int index = 0;

        DThread(int index) {
            this.index = index;
        }

        public void run() {
            while(true) {
                try{
                    Message msg1 = new Message();
                    int offtime = new Random().nextInt(5000) + 500;
                    Thread.sleep(offtime);

                    msg1.arg1 = index;
                    onHandler.sendMessage(msg1);

                    int ontime = new Random().nextInt(1000) + 500;
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

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            timer.setText("Timer " + msg.arg1 + "sec");
        }
    };

    public class timeCheck implements Runnable{

        final int MAXTIME = 30;

        public void run() {
            for(int i=MAXTIME; i >= 0; i--) {
                Message msg = new Message();
                msg.arg1 = i;
                handler.sendMessage(msg);

                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("score" , scores);
            startActivity(intent);
            finish();
        }
    }
}