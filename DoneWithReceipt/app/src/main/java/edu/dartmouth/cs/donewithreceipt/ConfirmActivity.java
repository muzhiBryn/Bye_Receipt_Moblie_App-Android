package edu.dartmouth.cs.donewithreceipt;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfirmActivity extends AppCompatActivity {
    public static final String ENTRY_TO_CONFIRM_KEY = "ENTRY_TO_CONFIRM_KEY";

    EditText mStoreName;
    EditText mDate;
    EditText mTime;
    EditText mSubtotal;
    Button mConfirm2;

    //private static final String entryTitles = "StoreName";
    private static final String DATE_PATTERN = "yyyy/MM/dd";
    private static final String TIME_PATTERN = "HH:mm";

    private final String SYNC_FAILED = "Sync failed! Please check your network";



    //private HistoryItemAdapter mAdapter;
    DatabaseReference mFirebaseDatabase;
    private List<HistoryEntry> allEntriesData;
    private HashMap<String, Long> allDataCloudKeys; //用来存放本地的有的cloudkey

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        mStoreName = findViewById(R.id.edit_store);
        //mTime = findViewById(R.id.edit_time);
        mDate = findViewById(R.id.edit_date);
        mSubtotal = findViewById(R.id.edit_amount);
        mConfirm2 = findViewById(R.id.confirm_button2);



        allEntriesData = new ArrayList<>();
        allDataCloudKeys = new HashMap<>();

        //deal with extra
        Intent intent = getIntent();
        final HistoryEntry historyEntry = intent.getParcelableExtra(ENTRY_TO_CONFIRM_KEY);
        mDate.setText(historyEntry.getDateStr());
        mStoreName.setText(historyEntry.getStoreName());
        mSubtotal.setText(historyEntry.getSubtotal()+"");


        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();



        mConfirm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HistoryEntry historyEntryToSave = new HistoryEntry();
                historyEntryToSave.setStoreName(mStoreName.getText().toString());
                historyEntryToSave.setSubtotal(Double.parseDouble(mSubtotal.getText().toString()));
                historyEntryToSave.setDateStr(mDate.getText().toString());
                if (checkInvalidInput()){
                    syncOneEntryWithFirebase(historyEntryToSave);
                }
            }
        });

    }

    public boolean checkInvalidInput(){
        if (mSubtotal.getText().toString().equals("0.0")){
            Toast.makeText(ConfirmActivity.this, "Please Check total amount", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mDate.getText().toString().equals("")){
            Toast.makeText(ConfirmActivity.this, "Please add correct date format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mStoreName.getText().toString().equals("")){
            Toast.makeText(ConfirmActivity.this,"Please add store name",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void syncOneEntryWithFirebase (HistoryEntry entry) {
        // 向firebase insert新纪录，调用push后，firebase会生成一个random的key
        DatabaseReference pushRef = mFirebaseDatabase.child("history_entries").push();

        // 上传到firebase
        pushRef.setValue(entry).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    // Failed
                    Toast.makeText(ConfirmActivity.this, SYNC_FAILED, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
