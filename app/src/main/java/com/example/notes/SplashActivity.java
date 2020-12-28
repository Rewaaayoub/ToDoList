package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Button nextBtn = (Button) findViewById(R.id.btn_next);
        nextBtn.setPaintFlags(nextBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }



    public void goToSignUp(View view){
        Intent intent = new Intent(this , SignUpActivity.class);
        startActivity(intent);
    }

}