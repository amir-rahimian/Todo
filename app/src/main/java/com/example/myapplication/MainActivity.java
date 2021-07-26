package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RvHolder.ClickListener {

    private RecyclerView tasksRV, doneRV;

    private FloatingActionButton btnAdd;
    private Button btnDone;
    int i = 0;
    private List<Task> tasks = new ArrayList<>();
    private List<Task> done = new ArrayList<>();
    private RvAdapter taskAdapter, doneAdapter;
    private EditText edtTask, edtAdd;
    private TextView edtTC, edtAC, txtDone;

    private CardView bottomSeet;
    private BottomSheetBehavior bottomSheetBehavior;
    private SqlManager sqlManager = SqlManager.getSqlManager(this);
    private SQLiteDatabase sqLiteDatabase ;
    private boolean is_close = false;
    private boolean for_update = false ;

    Task onTask ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle("Todo List");
        // UI
        tasksRV = findViewById(R.id.rv_tasks);
        doneRV = findViewById(R.id.rv_Done);
        btnDone = findViewById(R.id.btnDone);
        edtTask = findViewById(R.id.edtTask);
        txtDone = findViewById(R.id.txtDone);
        edtTC = findViewById(R.id.taskCounter);
        edtAdd = findViewById(R.id.edtAdd);
        edtAC = findViewById(R.id.additionCounter);
        bottomSeet = findViewById(R.id.btomSheet);
        btnAdd = findViewById(R.id.btnAdd);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSeet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        sqLiteDatabase = openOrCreateDatabase("database.db",MODE_PRIVATE,null);
//Todo : create
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+FeedReaderContract.FeedEntry.TABLE_NAME
                + " (" + FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
                FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT," +
                FeedReaderContract.FeedEntry.COLUMN_IS_CHECKED + " BIT)");

        getdata();

        tasksRV.setLayoutManager(new LinearLayoutManager(this));
        doneRV.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new RvAdapter(tasks, this);
        doneAdapter = new RvAdapter(done, this);
        tasksRV.setAdapter(taskAdapter);
        doneRV.setAdapter(doneAdapter);

        edtAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtAC.setText(edtAdd.getText().length() + "/70");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtTC.setText(edtTask.getText().length() + "/25");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = edtTask.getText().toString();
                String add = edtAdd.getText().toString();
                if (task.matches("^(?=.{3,25}$)(?![_.])(?!.*[_.]{2})[\\u0600-\\u06FF a-zA-Z0-9._]+(?<![_.])$")) {
                    Task t = new Task(task, (add.matches("")) ? "" : add);
                    if (for_update){
                        t.setDone(onTask.isDone());
//                        sqlManager.update(onTask,t);
//Todo : update
                        sqLiteDatabase.execSQL("UPDATE "+ FeedReaderContract.FeedEntry.TABLE_NAME+" SET "
                        + FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE+"='"+t.getTitle()+"' , "
                        + FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + "='"+t.getSubTitle()+"'"
                                +" WHERE "+FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE+"='"+onTask.getSubTitle()+"'"
                        );

                        getdata();
                        doneAdapter.notifyDataSetChanged();
                        taskAdapter.notifyDataSetChanged();
                        for_update = false ;
                    }else {
                        tasks.add(t);
//                    SharePreferenceManager.addToSharePreference(t, MainActivity.this);
//                        sqlManager.insert(t);
//TOdo : insert
                        sqLiteDatabase.execSQL("INSERT INTO "+FeedReaderContract.FeedEntry.TABLE_NAME
                                +" VALUES (NULL,'"+t.getTitle()+"','"+t.getSubTitle()+"',"+(t.isDone()?1:0)+")");
                    }

                    edtTask.setText("");
                    edtAdd.setText("");
                    hideKeyBoard(MainActivity.this);
                    taskAdapter.notifyDataSetChanged();


                } else {
                    Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vib.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vib.vibrate(500);
                    }
                }

            }
        });

    }

    private void getdata() {
        tasks.clear();
        done.clear();

//        List<Task> fromshare = SharePreferenceManager.getFromSharePreference(this);
//        for (Task t :
//                fromshare) {
//            if (t.isDone())
//                done.add(t);
//            else tasks.add(t);
//        }

//        List<Task> l = sqlManager.getData();
// Todo : get data
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+ FeedReaderContract.FeedEntry.TABLE_NAME
              //  + " WHERE " + filter
                + " ORDER BY "+ FeedReaderContract.FeedEntry._ID+" ASC" // Or DESC
                ,null);
        List<Task> l = new ArrayList<>();
        while (cursor.moveToNext()) {
            String title = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE));
            String subTitle = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE));
            boolean is_checked = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_IS_CHECKED)) != 0;
            Task task = new Task(title, subTitle);
            task.setDone(is_checked);
            l.add(task);
        }
        cursor.close();


        for (Task temp :
                l) {
            if (temp.isDone())
                done.add(temp);
            else tasks.add(temp);
        }
        if (done.size() != 0) txtDone.setVisibility(View.VISIBLE);
        else txtDone.setVisibility(View.GONE);
        if (tasks.size() == 0) txtDone.setText("All Done :");
        else txtDone.setText("Done");
    }


    private void hideKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtTask.getWindowToken(), 0);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }, 300);
    }

    @Override
    public void onCheckedChange(int position, boolean isChecked) {
        if (isChecked) {
            tasks.get(position).setDone(true);
//            SharePreferenceManager.updateSharePreference(tasks.get(position), this);
            sqlManager.update(tasks.get(position),tasks.get(position));
//Todo : update
            sqLiteDatabase.execSQL("UPDATE "+ FeedReaderContract.FeedEntry.TABLE_NAME+" SET "
                    + FeedReaderContract.FeedEntry.COLUMN_IS_CHECKED+"='"+(tasks.get(position).isDone()?1:0)+"'"
                    +" WHERE "+FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE+"='"+tasks.get(position).getSubTitle()+"'"
            );
        } else {
            done.get(position).setDone(false);
//            SharePreferenceManager.updateSharePreference(done.get(position), this);
            sqlManager.update(done.get(position),done.get(position));
//Todo : update
            sqLiteDatabase.execSQL("UPDATE "+ FeedReaderContract.FeedEntry.TABLE_NAME+" SET "
                    + FeedReaderContract.FeedEntry.COLUMN_IS_CHECKED+"='"+(done.get(position).isDone()?1:0)+"'"
                    +" WHERE "+FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE+"='"+done.get(position).getSubTitle()+"'"
            );
        }
        getdata();
        doneAdapter.notifyDataSetChanged();
        taskAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(int postion, boolean isChecked) {

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0: // Edit
                updateIthem();
                return true;
            case 1: // Delete
                deleteIthem();
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }

    private  void  updateIthem (){
        for_update = true;
        edtTask.setText(onTask.getTitle());
        edtAdd.setText(onTask.getSubTitle());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    private void deleteIthem (){

        new AlertDialog.Builder(MainActivity.this,R.style.Theme_MaterialComponents_BottomSheetDialog)
                .setTitle("Delete Task ? ")
                .setMessage(onTask.getTitle()+"\n"+onTask.getSubTitle()+"\n")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        sqlManager.delete(onTask);
//Todo : delete
                        sqLiteDatabase.execSQL("DELETE FROM "+FeedReaderContract.FeedEntry.TABLE_NAME+" WHERE "+FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE+" ='"+onTask.getSubTitle()+"'");

                        getdata();
                        doneAdapter.notifyDataSetChanged();
                        taskAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void onLongClick(int postion, boolean isChecked) {

        onTask = (isChecked)?done.get(postion):tasks.get(postion);

    }

    @Override
    public void onBackPressed() {
        hideKeyBoard(MainActivity.this);
        if (is_close) {
            super.onBackPressed();
        } else {
            is_close = true;
            Toast.makeText(MainActivity.this,"You need to tap ON Back 2 times in order to close app !", Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    is_close = false;
                }
            }, 500);
        }
    }

    @Override
    protected void onDestroy() {
//        sqlManager.close();
//Todo : close
        sqLiteDatabase.close();
        super.onDestroy();
    }
}