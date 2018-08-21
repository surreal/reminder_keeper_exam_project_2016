package com.reminder_keeper.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.reminder_keeper.views.ToolbarView;
import com.reminder_keeper.R;
import com.reminder_keeper.SignIn;
import com.google.android.gms.common.SignInButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String LOGIN_ACTIVITY = "LoginActivity";
    private EditText userNameInput;
    private TextView signInButtonTV;
    private SignInButton googleSignIn;
    private SignIn signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    { super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_custom));
        new ToolbarView(this, getSupportActionBar(), LOGIN_ACTIVITY);

        signIn = new SignIn(this, signInButtonTV);
        casting();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SignIn.clickedOnce = false;
    }

    private void casting()
    {
        userNameInput = (EditText) findViewById(R.id.activity_login_input_user_name);
        signInButtonTV = (TextView) findViewById(R.id.activity_login_login_button);
        googleSignIn = (SignInButton) findViewById(R.id.activity_login_google_button);
        googleSignIn.setOnClickListener(this);
        signInButtonTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.activity_login_login_button:
                userNameInput.getText().toString().trim();
                if (!userNameInput.getText().toString().equals(""))
                {
                    signIn.loginLocally(userNameInput.getText().toString());
                    finish();
                } else {
                    Toast.makeText(this, "Insert Name OR Sign in with Google", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.activity_login_google_button:
                signIn.signInWithGoogle();
                startActivityForResult(signIn.googleSignInIntent, SignIn.RC_SIGN_IN);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        signIn.getGoogleResult(data);
        finish();
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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
