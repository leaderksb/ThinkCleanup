package kr.co.kimsubin.ThinkCleanup;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TodoActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    EditText contentsEt;
    TextView contentsTv;
    String id = "", contents = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo);

        // 데이터베이스 열기 또는 생성
        db = openOrCreateDatabase("userDB", MODE_PRIVATE, null);

        // 테이블이 존재하지 않을때만 생성
        db.execSQL("create table if not exists todo( _id integer primary key autoincrement, contents text );");

        db.close();

        listView();
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
                return true;
            case R.id.diary:
                sendDiary();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendDiary() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // 부드럽게 화면전환
        Intent intent = new Intent(TodoActivity.this, DiaryActivity.class);  // DiaryActivity.class 액티비티 이동
        startActivity(intent);
    }

    public void listView(){
        // 데이터베이스 열기 또는 생성
        db = openOrCreateDatabase("userDB", MODE_PRIVATE, null);

        Cursor cursor = db.rawQuery("select * from todo;", null);

        startManagingCursor(cursor);

        String[] from = {"contents"};
        int[] to = {android.R.id.text1};

        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to);
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                id = cursor.getString(cursor.getColumnIndex("_id"));
                contents = cursor.getString(cursor.getColumnIndex("contents"));
                delete(contents);
            }
        });
    }

    public void add(View v) {
        final Dialog addDialog = new Dialog(this);
        addDialog.setContentView(R.layout.add_dialog);
        addDialog.setTitle("할일추가");

        Button save = (Button) addDialog.findViewById(R.id.save);
        Button cancel = (Button) addDialog.findViewById(R.id.cancel);

        contentsEt = (EditText) addDialog.findViewById(R.id.contents);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contentsEt.getText().toString().trim().length() > 0) {
                    // 데이터베이스 열기 또는 생성
                    db = openOrCreateDatabase("userDB", MODE_PRIVATE, null);

                    // todo 테이블에 데이터 삽입
                    db.execSQL("insert into todo values (null, '" + contentsEt.getText().toString() + "');");

                    db.close();

                    addDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "할일이 추가 되었습니다.", Toast.LENGTH_SHORT).show();

                    listView();
                } else {
                    Toast.makeText(getApplicationContext(), "다시 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog.dismiss();
            }
        });
        addDialog.show();
    }

    public void delete(String contents) {
        final Dialog deleteDialog = new Dialog(this);
        deleteDialog.setContentView(R.layout.delete_dialog);
        deleteDialog.setTitle("할일삭제");

        Button delete = (Button) deleteDialog.findViewById(R.id.delete);
        Button cancel = (Button) deleteDialog.findViewById(R.id.cancel);

        contentsTv = (TextView) deleteDialog.findViewById(R.id.contents);

        contentsTv.setText(contents);

        db.close();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 데이터베이스 열기 또는 생성
                db = openOrCreateDatabase("userDB", MODE_PRIVATE, null);

                // todo 테이블에 데이터 삭제
                db.execSQL("delete from todo where _id = '" + id + "';");

                db.close();

                deleteDialog.dismiss();
                Toast.makeText(getApplicationContext(), "할일이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();

                listView();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog.show();
    }

}
