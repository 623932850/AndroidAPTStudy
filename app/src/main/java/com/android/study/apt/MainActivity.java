package com.android.study.apt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.study.annotation.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.message)
    TextView mTvMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
