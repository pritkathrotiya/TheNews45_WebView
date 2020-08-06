package com.pritkathrotiya.thenews45;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class splashActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0272R.layout.activity_splash);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                splashActivity.this.startActivity(new Intent(splashActivity.this, MainActivity.class));
                splashActivity.this.finish();
            }
        }, 2000);
    }
}
