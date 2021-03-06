package com.moonshine.pokemongonotifications;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.moonshine.pokemongonotifications.Utils.UserPreferences;
import com.moonshine.pokemongonotifications.model.LoginResponse;
import com.moonshine.pokemongonotifications.network.RestClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GoogleAuthActivity extends AppCompatActivity {
    private static final String TAG = "GoogleAuthActivity";

    private static final String ARG_URL = "Google Auth Url";
    private static final String ARG_CODE = "Google User Code";

    private String url;
    private String userCode;
    private View mProgressView;

    private WebView webView;
    private TextView codeView;

    public static void startForResult(Activity starter, int requestCode,
                                              String url, String code){
        Intent intent = new Intent(starter, GoogleAuthActivity.class);
        intent.putExtra(ARG_URL, url);
        intent.putExtra(ARG_CODE, code);
        starter.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_auth);

        fetchIntentData();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

        mProgressView = findViewById(R.id.login_progress);

        codeView = (TextView) findViewById(R.id.auth_code);
        webView = (WebView) findViewById(R.id.webview);

        codeView.setText(userCode);

        WebViewClient client = new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "onPageFinished: url = " + url);
                if(url.contains("https://accounts.google.com/o/oauth2/approval?")){
//                    sendResults();
                    showProgress(true);
                    webView.loadUrl("javascript:window.HtmlViewer.showHTML" +
                            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                }
            }
        };

        final EditText code = (EditText) findViewById(R.id.auth_code);
        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(code.getText() != null) {
                    String authcode = code.getText().toString();
                    RestClient.getInstance().login(authcode).enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            UserPreferences.saveToken(GoogleAuthActivity.this, response.body().getRefreshToken());
                            sendResults();
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            //todo handle
                        }
                    });
                }
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(client);
        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");
        webView.loadUrl(url);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchIntentData(){
        Intent intent = getIntent();
        url = intent.getStringExtra(ARG_URL);
        userCode = intent.getStringExtra(ARG_CODE);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void sendResults(){
        setResult(RESULT_OK);
        showProgress(false);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html) {

            Document doc = Jsoup.parse(html);
            String code = doc.getElementById("code").attr("value");
            Log.e("WEBVIEW", code);
            if(code != null) {
                String authcode = code;
                RestClient.getInstance().login(authcode).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        UserPreferences.saveToken(GoogleAuthActivity.this, response.body().getRefreshToken());
                        sendResults();
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        //todo handle
                    }
                });
            }
        }

    }
}
