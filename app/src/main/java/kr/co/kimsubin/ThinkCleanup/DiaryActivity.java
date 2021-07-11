package kr.co.kimsubin.ThinkCleanup;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class DiaryActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    EditText titleEt, contentsEt;
    public String date = "", title = "", contents = "", cnt;
    Button btnSelectDate;
    Calendar c;
    int nowYear, nowMonth, nowDay;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary);

        btnSelectDate = (Button)findViewById(R.id.dateBtn);

        titleEt = (EditText)findViewById(R.id.title);
        contentsEt = (EditText)findViewById(R.id.contents);

        c = Calendar.getInstance(); // 현재시간을 얻음
        nowYear = c.get(Calendar.YEAR);
        nowMonth = c.get(Calendar.MONTH);
        nowDay = c.get(Calendar.DAY_OF_MONTH);
        date = nowYear + "/" + (nowMonth + 1) + "/" + nowDay;

        // 데이터베이스 열기 또는 생성
        db = openOrCreateDatabase("userDB", MODE_PRIVATE, null);

        // 테이블이 존재하지 않을때만 생성
        db.execSQL("create table if not exists diary(" +
                "date date primary key," +
                "title text," +
                "contents text);");

        selectDiary();

        titleEt.setText(title);
        contentsEt.setText(contents);

        disableEt();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.todo:
                sendTodo();
                return true;
            case R.id.diary:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendTodo() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // 부드럽게 화면전환
        Intent intent = new Intent(DiaryActivity.this, TodoActivity.class);  // TodoActivity.class 액티비티 이동
        startActivity(intent);

    }

    public void dateChoice(View v) {
        if (v == btnSelectDate) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    date = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                    title = "";
                    contents = "";

                    selectDiary();

                    titleEt.setText(title);
                    contentsEt.setText(contents);

                    btnSelectDate.setText(date);
                }
            }, nowYear, nowMonth, nowDay);
            datePickerDialog.show();
        }
    }

    public void save(View target) {
        // 데이터베이스 열기 또는 생성
        db = openOrCreateDatabase("userDB", MODE_PRIVATE, null);

        db.execSQL("pragma foreign_keys = on;"); // 외래키 기능 활성화

        title = titleEt.getText().toString();
        contents = contentsEt.getText().toString();

        // diary 테이블 기본키 date 존재여부 확인
        Cursor cursor = db.rawQuery("select count(date) from diary where date = '" + date + "';", null);
        while (cursor.moveToNext()) {
            cnt = cursor.getString(0);
        }

        // date 중복 확인
        if (cnt.equals("0")) {
            if (!date.trim().equals("")) {
                // diary 테이블에 데이터 삽입
                db.execSQL("insert into diary values ('" + date + "', '" + title + "', '" + contents + "');");
                Toast.makeText(getApplicationContext(), date + " 저장 되었습니다.", Toast.LENGTH_SHORT).show();
                db.close();

                disableEt();
            } else {
                Toast.makeText(getApplicationContext(), "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // SQLite 수정 기능 미구현으로 인해
            // diary 테이블에 데이터 삭제
            db.execSQL("delete from diary where date = '" + date + "';");
            // diary 테이블에 데이터 삽입
            db.execSQL("insert into diary values ('" + date + "', '" + title + "', '" + contents + "');");
            Toast.makeText(getApplicationContext(), date + " 저장 되었습니다.", Toast.LENGTH_SHORT).show();
            db.close();

            disableEt();
        }
    }

    public void write(View target) {
        ableEt();
        Toast.makeText(getApplicationContext(), "쓰기 기능을 사용합니다.", Toast.LENGTH_SHORT).show();
    }

    public void delete(View target) {
        // 데이터베이스 열기 또는 생성
        db = openOrCreateDatabase("userDB", MODE_PRIVATE, null);

        // diary 테이블에 데이터 삭제
        db.execSQL("delete from diary where date = '" + date + "';");
        Toast.makeText(getApplicationContext(), date + " 삭제 되었습니다.", Toast.LENGTH_SHORT).show();

        titleEt.setText("");
        contentsEt.setText("");

        db.close();
    }

    public void ableEt(){
        // EditText 활성화
        titleEt.setFocusableInTouchMode(true);
        titleEt.setFocusable(true);
        contentsEt.setFocusableInTouchMode(true);
        contentsEt.setFocusable(true);
    }

    public void disableEt(){
        // EditText 비활성화
        titleEt.setFocusableInTouchMode(false);
        titleEt.setFocusable(false);
        contentsEt.setFocusableInTouchMode(false);
        contentsEt.setFocusable(false);
    }

    public void selectDiary(){
        // 데이터베이스 열기 또는 생성
        db = openOrCreateDatabase("userDB", MODE_PRIVATE, null);

        // 선택한 날짜의 데이터 불러오기
        Cursor cursor = db.rawQuery("select * from diary where date='" + date + "';", null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    title = cursor.getString(cursor.getColumnIndex("title"));
                    contents = cursor.getString(cursor.getColumnIndex("contents"));
                } while (cursor.moveToNext());
            }
        }

        db.close();
    }
}
