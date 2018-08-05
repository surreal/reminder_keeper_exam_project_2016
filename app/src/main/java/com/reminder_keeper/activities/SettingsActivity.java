package com.reminder_keeper.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.reminder_keeper.views.ToolbarView;
import com.reminder_keeper.CursorsDBMethods;
import com.reminder_keeper.R;
import com.reminder_keeper.SignIn;

public class SettingsActivity extends AppCompatActivity {

    public static final String SETTINGS_ACTIVITY = "SettingsActivity";
    public static final int REQUEST_CODE_SETTINGS_ACTIVITY = 1;
    private SignIn signIn;
    private CursorsDBMethods cursors;
    private TextView userNameTV, emailTV, signInButtonTV;
    private ImageView profileImageIV, editFoldersIcon, recyclingBinIcon, aboutIcon;
    private TextView countOfReminders;
    private int resultCodePassed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {   super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        castings();
        setListeners();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_custom));
        new ToolbarView(this, getSupportActionBar(), SETTINGS_ACTIVITY);
        cursors = new CursorsDBMethods(this);
        cursors.getCursorRecyclingBin();
        signIn = new SignIn(this, signInButtonTV);
        signIn.checkIfAccountLogged(userNameTV, emailTV, profileImageIV);
        setRecyclingBinRelevantIcon();
    }

    private void castings()
    {
        signInButtonTV = (TextView) findViewById(R.id.activity_settings_signin_button_tv);
        emailTV = (TextView) findViewById(R.id.activity_settings_user_email);
        userNameTV = (TextView) findViewById(R.id.activity_settings_user_name);
        profileImageIV = (ImageView) findViewById(R.id.activity_settings_image_view);
        editFoldersIcon = (ImageView) findViewById(R.id.activity_settings_edit_folders_icon);
        recyclingBinIcon = (ImageView) findViewById(R.id.activity_settings_recycling_bin_icon);
        aboutIcon = (ImageView) findViewById(R.id.activity_settings_about_icon);
        countOfReminders = (TextView) findViewById(R.id.activity_settings_count_of_reminders);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        signIn.checkIfAccountLogged(userNameTV, emailTV, profileImageIV);
        setRecyclingBinRelevantIcon();
        new ToolbarView(this, getSupportActionBar(), SETTINGS_ACTIVITY);
        resultCodePassed = resultCode;
    }

    private void setListeners()
    {
        editFoldersIcon.setOnClickListener(onViewsClickListener);
        recyclingBinIcon.setOnClickListener(onViewsClickListener);
        aboutIcon.setOnClickListener(onViewsClickListener);
    }
    View.OnClickListener onViewsClickListener = new View.OnClickListener()
    { @Override
        public void onClick(View view) {

            if (view == editFoldersIcon) {
                startActivityForResult(new Intent(SettingsActivity.this, TheArrangeActivity.class), REQUEST_CODE_SETTINGS_ACTIVITY);
            } else if (view == recyclingBinIcon) {
                startActivityForResult(new Intent(SettingsActivity.this, RecyclingBinActivity.class), REQUEST_CODE_SETTINGS_ACTIVITY);
            } else if (view == aboutIcon) {
                startActivityForResult(new Intent(SettingsActivity.this, AboutActivity.class), REQUEST_CODE_SETTINGS_ACTIVITY);
            }
        }
    };

    private void countOfRemindersInsideRecyclingBin() { countOfReminders.setText(cursors.getCursorRecyclingBin().getCount() + ""); }

    //TODO: set relevant Recycling Bin icon (empty, full)
    private void setRecyclingBinRelevantIcon()
    {
        if (cursors.getCursorRecyclingBin().getCount() == 0)
        {
            recyclingBinIcon.setImageResource(R.mipmap.recycling_bin_empty);
        } else {
            recyclingBinIcon.setImageResource(R.mipmap.recycling_bin_full);
        }

        countOfRemindersInsideRecyclingBin();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            setResult(resultCodePassed);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        setResult(resultCodePassed);
        finish();
    }
}
