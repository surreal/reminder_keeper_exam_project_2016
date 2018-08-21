package com.reminder_keeper.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.reminder_keeper.views.ToolbarView;
import com.reminder_keeper.R;
import com.reminder_keeper.SignIn;

public class AccountActivity extends AppCompatActivity {

    public static final String ACCOUNT_ACTIVITY = "AccountActivity";
    private TextView signInButtonTV;
    private TextView emailTV;
    private TextView userNameTV;
    private ImageView profileImage;
    private SignIn signIn;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {   super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_custom));
        new ToolbarView(this, getSupportActionBar(), ACCOUNT_ACTIVITY);
        castings();
        signIn = new SignIn(this, signInButtonTV);
        signIn.checkIfAccountLogged(userNameTV, emailTV, profileImage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SignIn.clickedOnce = false;
    }

    private void castings()
    {
        emailTV = (TextView) findViewById(R.id.activity_account_user_email);
        userNameTV = (TextView) findViewById(R.id.activity_account_user_name);
        profileImage = (ImageView) findViewById(R.id.activity_account_image_view);
        signInButtonTV = (TextView) findViewById(R.id.activity_account_signin_button_tv);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        signIn.checkIfAccountLogged(userNameTV, emailTV, profileImage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
