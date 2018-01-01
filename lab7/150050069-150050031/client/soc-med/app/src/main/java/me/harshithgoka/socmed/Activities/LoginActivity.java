package me.harshithgoka.socmed.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import me.harshithgoka.socmed.Misc.Constants;
import me.harshithgoka.socmed.Misc.Utils;
import me.harshithgoka.socmed.Network.MyCookieStore;
import me.harshithgoka.socmed.R;
import me.harshithgoka.socmed.Storage.UserStorage;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;


    public static final String TAG = LoginActivity.class.getName();

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyCookieStore cookieStore = new MyCookieStore(getApplicationContext());
        List<HttpCookie> cookies = cookieStore.getCookies();
        if (cookies != null) {
            for (HttpCookie cookie : cookies) {
                Log.d(TAG, cookie.getName() + "=" + cookie.getValue());
            }
        }
        else {
            Log.d(TAG, "No cookies in CookieStore");
        }

        if( cookies.size() > 0 ) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        if (intent != null) {
            if ( intent.getIntExtra(Constants.INTENT_DATA, -1) == Constants.GET_NETWORK_STATE) {
                if (mUsernameView != null) {
                    Snackbar.make(mUsernameView.getRootView(), "You have been logged out! Please login again", Snackbar.LENGTH_INDEFINITE).show();
                }
            }
        }

        Button mRegisterButton = findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });


        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mUsernameSignInButton = (Button) findViewById(R.id.username_sign_in_button);
        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            CookieManager cookieManager = new CookieManager(new MyCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);

            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        public final String TAG = UserLoginTask.class.getName();

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String ret = new String();
            try {
                URL url = new URL(Constants.URL + "Login");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try  {
                    Gson gson = new Gson();
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    List<AbstractMap.SimpleEntry> parameters = new ArrayList<AbstractMap.SimpleEntry>();
                    parameters.add(new AbstractMap.SimpleEntry<String, String>("id", mUsername));
                    parameters.add(new AbstractMap.SimpleEntry<String, String>("password", mPassword));

                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(Utils.getQuery(parameters));
                    writer.flush();
                    writer.close();
                    os.close();

                    urlConnection.getHeaderFields();

                    if (!url.getHost().equals(urlConnection.getURL().getHost())) {
                        // we were redirected! Kick the user out to the browser to sign on?
                        throw new Exception("Login to your internet provider");
                    }

//                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//
//                    StringBuilder stringBuilder = new StringBuilder();
//
//                    int nbytes;
//                    byte[] bytes = new byte[1024];
//                    while ((nbytes = in.read(bytes, 0, 1024)) != -1 ) {
//                        stringBuilder.append(new String(bytes, 0, nbytes));
//                    }
//
//                    JsonParser jsonParser = new JsonParser();
//                    JsonObject response = jsonParser.parse(stringBuilder.toString()).getAsJsonObject();

                    JsonObject response = Utils.getAndParse(urlConnection.getInputStream());

                    Log.d(TAG, response.toString());
                    if (response.get("status").getAsBoolean()) {
                        ret = "true";
                        UserStorage.setName(response.get("data").getAsString());
                    }
                    else {
                        ret = response.get("message").getAsString();
                    }
                }
                catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                return e.toString();
            }

            // TODO: register the new account here.
            return ret;
        }

        @Override
        protected void onPostExecute(final String success) {
            mAuthTask = null;
            showProgress(false);

            if (success.equals("true")) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.MISCSTATE, 0);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.LOGINSTATE, true);
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.setError(success);
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

