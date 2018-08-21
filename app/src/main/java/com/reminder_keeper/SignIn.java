package com.reminder_keeper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.reminder_keeper.activities.LoginActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class SignIn implements GoogleApiClient.OnConnectionFailedListener {

    public static final String AUTHENTICATION_SP = "userAuthentication";
    private static final int MODE_PRIVATE = 0;
    public static final String IS_LOGGED_WITH_GOOGLE = "isLoggedWithGoogle";
    public static final String USER_NAME = "userName";
    public static final String USER_EMAIL = "userEmailTV";
    public static final String IS_LOGGED_LOCALLY = "isLoggedLocally";
    public static final String USER_PHOTO_URL = "personPhotoUrl";
    public static GoogleApiClient googleApiClient;
    public static final int RC_SIGN_IN = 1;
    private Activity activity;
    public Intent googleSignInIntent;
    private SharedPreferences sharedPreferences;
    private Editor preferencesEditor;
    private static Bitmap bitmap;
    public final static String imageFileName = "image_file.png";
    private Bitmap imageBitmap;
    private TextView signInButtonTV;
    public static boolean clickedOnce;

    public SignIn(Activity activity, TextView signInButtonTV)
    {
        this.activity = activity;
        this.signInButtonTV = signInButtonTV;
        sharedPreferences = activity.getSharedPreferences(AUTHENTICATION_SP, MODE_PRIVATE);
        preferencesEditor = sharedPreferences.edit();
    }

    public void signInWithGoogle()
    {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage((FragmentActivity) activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    }

    public void getGoogleResult(Intent data)
    {
        GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

        if (googleSignInResult.isSuccess())
        {
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
            String personUserName = googleSignInAccount.getGivenName();
            String personEmail = googleSignInAccount.getEmail();

            if (googleSignInAccount.getPhotoUrl() != null)
            {
                String personPhotoUrl = googleSignInAccount.getPhotoUrl().toString();
                new DownloadProfileImage().execute(personPhotoUrl);
                preferencesEditor.putString(USER_PHOTO_URL,personPhotoUrl);
                preferencesEditor.putBoolean(IS_LOGGED_WITH_GOOGLE, true);
                preferencesEditor.putString(USER_NAME, personUserName);
                preferencesEditor.putString(USER_EMAIL,personEmail);
                preferencesEditor.commit();
            }
        }
    }

    private class DownloadProfileImage extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... url)
        {
            try {
                InputStream inputStream = new URL(url[0]).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap resultBitmap)
        {
            try {
                FileOutputStream fileOutputStream = activity.openFileOutput(imageFileName, MODE_PRIVATE);
                resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap loadImageBitmap()
    {
        File imageFile = activity.getFileStreamPath(imageFileName);
        if (imageFile.exists())
        {
            try {
                FileInputStream fileInputStream = activity.openFileInput(imageFileName);
                bitmap = BitmapFactory.decodeStream(fileInputStream);
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void loginLocally(String userNameInput)
    {
        preferencesEditor.putString(USER_NAME, userNameInput);
        preferencesEditor.putBoolean(IS_LOGGED_LOCALLY, true);
        preferencesEditor.apply();
    }

    public void logOut()
    {
        preferencesEditor.clear().commit();
    }

    //TODO: check if user logged set relevant elements (name, email, image)
    public void checkIfAccountLogged(final TextView profileNameTV, final TextView profileEmail, final ImageView profileImage)
    {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SignIn.AUTHENTICATION_SP, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(SignIn.IS_LOGGED_WITH_GOOGLE, false))
        {
            profileNameTV.setText(sharedPreferences.getString(SignIn.USER_NAME,null));
            profileEmail.setText(sharedPreferences.getString(SignIn.USER_EMAIL,null));
            imageBitmap = loadImageBitmap();

            if (signInButtonTV != null)
            {
                signInButtonTV.setText(R.string.logout);
                signInButtonTV.setOnClickListener(new View.OnClickListener()
                {@Override
                    public void onClick(View view)
                    {
                        if (!clickedOnce){
                            clickedOnce = true;
                            logOut();
                            signInButtonTV.setText(R.string.login);
                            profileNameTV.setText(R.string.user);
                            profileEmail.setText("");
                            profileImage.setImageResource(R.mipmap.profile_unsigned);
                            signInButtonTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!clickedOnce){
                                        clickedOnce = true;
                                        activity.startActivityForResult(new Intent(activity ,LoginActivity.class), 1);
                                    }
                                }
                            });
                        }
                    }
                });
            }
            if (imageBitmap != null)
            {
                profileImage.setImageBitmap(imageBitmap);
            } else {
                Glide.with(activity).load(sharedPreferences.getString(SignIn.USER_PHOTO_URL, null)).into(profileImage);
            }
        } else if (sharedPreferences.getBoolean(SignIn.IS_LOGGED_LOCALLY, false))
        {
            profileNameTV.setText(sharedPreferences.getString(SignIn.USER_NAME, null));
            profileEmail.setText(sharedPreferences.getString(SignIn.USER_EMAIL, null));
            profileImage.setImageResource(R.mipmap.profile_unsigned);
            if (signInButtonTV != null)
            {
                signInButtonTV.setText(R.string.logout);
                signInButtonTV.setOnClickListener(new View.OnClickListener()
                {   @Override
                public void onClick(View view)
                {
                    if (!clickedOnce){
                        clickedOnce = true;
                        logOut();
                        activity.finish();
                    }
                }
                });
            }
        } else
        {
            profileNameTV.setText(R.string.user);
            profileEmail.setText("");
            profileImage.setImageResource(R.mipmap.profile_unsigned);

            if (signInButtonTV != null)
            {
                signInButtonTV.setText(R.string.login);
                signInButtonTV.setOnClickListener(new View.OnClickListener()
                {   @Override
                public void onClick(View view)
                {
                    if (!clickedOnce){
                        clickedOnce = true;
                        logOut();
                        activity.startActivityForResult(new Intent(activity, LoginActivity.class),1);
                    }
                }
                });
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("SignInCheck", "onConnectionFailed() " + connectionResult.getErrorMessage());
    }
}
