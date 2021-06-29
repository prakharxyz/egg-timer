package com.example.eggtimer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //create objects of the required elements to use them in mainActivity
    TextView timerTextView; //textView to show time left for timer to user in format M:ss
    SeekBar timerSeekBar; //seekBar to set required timer
    Boolean counterIsActive = false; //initially timer is not active but when user presses 'go' it becomes active
    Button goButton; //button to either launch the timer or stop it
    CountDownTimer countDownTimer; //timer

    //reset timer function which is called to reset the timer whenever timer is over or user presses 'stop'
    public void resetTimer() {
        timerTextView.setText("0:30"); //set the timer
        timerSeekBar.setProgress(30); //set the progress of seekBar to 30
        timerSeekBar.setEnabled(true); //enable seekbar which was previously disabled when counter was active
        countDownTimer.cancel(); //cancel or terminate countDownTimer
        try {
            goButton.setText("GO!"); //set text in button as GO
        }
        catch(Exception e){ e.printStackTrace();}
        counterIsActive=false; //counter is set to not active as the timer is reset
    }
    //func to update timerTextView which takes second left as argument which it can get from countDown timer easily
    public void updateTimer(int secondsLeft) {
        int minutes = secondsLeft/60; //create minutes var to extract minutes from secondsLeft
        int seconds = secondsLeft-(minutes*60); //create seconds var to extract seconds (00-59) from secondsLeft

        //when seconds goes below 9 (Ex:- 0:5 -> 0:05) we want to insert an extra o before seconds so it looks good
        String secondString = Integer.toString(seconds);
        if(seconds<=9){
            secondString="0"+secondString;
        }
        timerTextView.setText(Integer.toString(minutes) + ":" + secondString); //set minutes to left of colon and seconds to right of colon in timer textView
    }

    //when "GO!" or "STOP!" is clicked
    public void buttonClicked(View view) {

        if (counterIsActive) { //reset the timer if counter was active ,ie , when 'stop' is clicked
            resetTimer(); //this sets counter not active
        }
        else { //when counter is not active , we want to activate the timer
                counterIsActive = true; //set counter condition to be active
                timerSeekBar.setEnabled(false); //disable seekbar so user cannot alter it
                try {
                    goButton.setText("STOP!"); //set its text to 'stop'
                }
                catch (Exception e){ e.printStackTrace();}

                //define countDown timer which has interval of 1 sec and starts from value of seekbar
                countDownTimer = new CountDownTimer(timerSeekBar.getProgress() * 1000 + 100 , 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        updateTimer((int) millisUntilFinished / 1000);
                        //we can get secondsLeft from its long argument by converting it to int & calling updateTimer func so taht it updates the textview with every sec
                    }

                    @Override
                    public void onFinish() {
                        try {
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tone);
                            mediaPlayer.start();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                            resetTimer();
                            //when timer gets finished play buzzing sound from mediaPlayer and call func to reset timer

                    }

                }.start();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerSeekBar = findViewById(R.id.timerSeekBar);
        timerTextView = findViewById(R.id.timerTextView);
        goButton = findViewById(R.id.goButton);

        timerSeekBar.setMax(600); //set max value of seekBar to 10 min
        timerSeekBar.setProgress(30); //set default value of seekBar to 30 sec

        //set seekbar change listener call func to update timer tv with progress set in seekBar
        timerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTimer(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
//ERROR SOLVED : NullPointerExeption solved on goButton by finding it on oncreate which was previously not
//ERROR : audio not playing when timer gets over neither it is showing any exception in logcat