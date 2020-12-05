package com.example.todolisttut;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //the duration of the animation of splash screen
    private static final int SPLASH = 3300;

    Animation mbelowAnim;
    //call the Animation attrbute
    ImageView mImageView;
    TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //TODO:hmm what is setFlags (?)
        setContentView(R.layout.activity_main);
        //so when I run the application the status bar is going to be removed
        Animation mtopAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);

        //mtopAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);

        //TODO:how to settle this animation thingy (if wanna swtich from left to rite
        //this.overridePendingTransition(R.anim.top_animation,R.anim.below_animation);
        mbelowAnim = AnimationUtils.loadAnimation(this,R.anim.below_animation);
        mImageView = findViewById(R.id.imgView);
        mTextView = findViewById(R.id.textSplash);
        mImageView.setAnimation(mtopAnim);
        //image go downwards
        mTextView.setAnimation(mbelowAnim);
        //text view to go upwards

        //also what is Handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                //when a user clicks back, splash screen loads again
                finish();
            }
        },SPLASH);
    }
}