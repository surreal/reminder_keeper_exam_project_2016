package com.reminder_keeper.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.reminder_keeper.R;
import com.reminder_keeper.views.ToolbarView;

public class AboutActivity extends AppCompatActivity {

    public static final String ABOUT_ACTIVITY = "aboutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_custom));
        new ToolbarView(this, getSupportActionBar(), ABOUT_ACTIVITY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
