package com.lbd0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Fragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragment = new MainFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();

        Button saveButton = findViewById(R.id.savebtn);
        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                saveToDo();

                Toast.makeText(getApplicationContext(), "추가되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        openDatabase();

    }

    EditText inputToDo;
    Context context;

    private void saveToDo() {
        inputToDo = findViewById(R.id.inputToDo);

        String todo = inputToDo.getText().toString();

        String sqlSave = "INSERT INTO " + NoteDatabase.TABLE_NOTE + " (TODO) VALUES (" + "'" + todo + "')";

        NoteDatabase database = NoteDatabase.getInstance(context);
        database.execSQL(sqlSave);

        inputToDo.setText("");
    }

    public static NoteDatabase noteDatabase = null;

    public void openDatabase() {
        if(noteDatabase != null) {
            noteDatabase.close();
            noteDatabase = null;
        }

        noteDatabase = NoteDatabase.getInstance(this);
        boolean isOpen = noteDatabase.open();
        if(isOpen) {
            Log.d(TAG, "Note database is open.");
        }else {
            Log.d(TAG, "Note database is not open.");
        }
    }

    protected void onDestroy() {
        super.onDestroy();

        if(noteDatabase != null) {
            noteDatabase.close();
            noteDatabase = null;
        }
    }
}