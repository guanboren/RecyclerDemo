package com.example.recyclerviewadapterdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListener();
    }

    private void initListener()
    {
        findViewById(R.id.button_to_recycler).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.button_to_recycler) {
            Intent intent = new Intent(this, RecyclerViewDemoActivity.class);
            startActivity(intent);
        }

    }
}
