package com.lzy.hasspaceedittext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;

import com.lzy.hasspaceedittext.view.HasSpaceEditText;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    HasSpaceEditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.et);
        editText.setRegulation(new int[]{4,4,4,5})
                .build();
    }

    public void printResult(View view) {
        Log.d(TAG, "printResult: " + editText.getNoSpaceText());
    }
}