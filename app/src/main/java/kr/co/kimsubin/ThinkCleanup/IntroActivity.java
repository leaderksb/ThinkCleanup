package kr.co.kimsubin.ThinkCleanup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class IntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        // 액션바 숨김
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendDiary();
            }
        }, 3000); // 3초 뒤 화면넘김
    }

    public void sendDiary() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // 부드럽게 화면전환
        Intent intent = new Intent(IntroActivity.this, DiaryActivity.class);  // DiaryActivity.class 액티비티 이동
        startActivity(intent);
    }

}
