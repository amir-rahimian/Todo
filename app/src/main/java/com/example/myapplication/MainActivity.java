package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private TextView edtTC, edtAC , txtDone;

    private CardView bottomSeet;
    private BottomSheetBehavior bottomSheetBehavior;
    private SqlManager sqlManager = new SqlManager(this) ;

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

        
        getdata();

        sqlManager.getData();
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
                    tasks.add(t);

                    sqlManager.insert(t);
                    
//                    SharePreferenceManager.addToSharePreference(t, MainActivity.this);
                    edtTask.setText("");
                    edtAdd.setText("");
                    hideKeyBoard(MainActivity.this);
                    taskAdapter.notifyDataSetChanged();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    }, 200);
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
        List<Task> l = sqlManager.getData() ;
        for (Task temp :
                l) {
            if (temp.isDone())
                done.add(temp);
            else tasks.add(temp);
        }
        if (done.size()!=0) txtDone.setVisibility(View.VISIBLE); else  txtDone.setVisibility(View.GONE);
        if (tasks.size()==0) txtDone.setText("All Done :"); else  txtDone.setText("Done");
    }


    private void hideKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtTask.getWindowToken(), 0);
    }

    @Override
    public void onCheckedChange(int position, boolean isChecked) {
        if (isChecked) {
            tasks.get(position).setDone(true);
//            SharePreferenceManager.updateSharePreference(tasks.get(position), this);
            sqlManager.update(tasks.get(position));
        } else {
            done.get(position).setDone(false);
//            SharePreferenceManager.updateSharePreference(done.get(position), this);
            sqlManager.update(done.get(position));
        }
        getdata();
        doneAdapter.notifyDataSetChanged();
        taskAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick() {

    }


    @Override
    public void onLongClick() {

    }

    @Override
    protected void onDestroy() {
        sqlManager.close();
        super.onDestroy();
    }
}