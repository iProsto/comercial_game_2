package com.main.goplay;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import bolts.AppLinks;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

public class WebView extends AppCompatActivity {

    private android.webkit.WebView webView;
    private boolean bigBack = false;
    String url = MainActivity.webViewURL;

    //# OTHER
    public ValueCallback<Uri[]> uploadMessage;
    private ValueCallback<Uri> mUploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = findViewById(R.id.webView);
        webView.setBackgroundColor(Color.argb(255, 30,30,68));

        initWebView();
        initOneSignal();
        initFB();
        initGoogleAnalytics();
    }

    private void initGoogleAnalytics(){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setDisplayZoomControls(false);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadWithOverviewMode(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onReceivedSslError(android.webkit.WebView view, final SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(WebView.this);
                String message = "SSL Certificate error.";
                console(message);
                switch (error.getPrimaryError()) {
                    case SslError.SSL_UNTRUSTED:
                        message = "The certificate authority is not trusted.";
                        break;
                    case SslError.SSL_EXPIRED:
                        message = "The certificate has expired.";
                        break;
                    case SslError.SSL_IDMISMATCH:
                        message = "The certificate Hostname mismatch.";
                        break;
                    case SslError.SSL_NOTYETVALID:
                        message = "The certificate is not yet valid.";
                        break;
                }
                message += "Do you want to continue anyway?";

                builder.setTitle("SSL Certificate Error");
                builder.setMessage(message);
                builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        };

        WebChromeClient myChromeClient = new WebChromeClient() {
            // For Lollipop 5.0+ Devices
            public boolean onShowFileChooser(android.webkit.WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                uploadMessage = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(WebView.this, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        };

        webView.setWebChromeClient(myChromeClient);
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(url);
    }

    private void initOneSignal() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    private void initFB() {
        FacebookSdk.setApplicationId(String.valueOf(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(this);
        FacebookSdk.fullyInitialize();
        FacebookSdk.setAutoInitEnabled(true);
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            setDeepLink(targetUrl.getQuery());
        } else {
            AppLinkData.fetchDeferredAppLinkData(
                    this, String.valueOf(R.string.facebook_app_id),
                    appLinkData -> {
                        if (appLinkData != null) {
                            setDeepLink(appLinkData.getTargetUri().getQuery());
                        }
                    });
        }
    }

    private void setDeepLink(String deepLink) {
        String newUrl = url;
        console("Link got: " + deepLink);
        console("Link webview: " + url);
        if (url.indexOf('?') == -1) {
            newUrl += "/?" + deepLink;
        } else {
            newUrl += "&" + deepLink;
        }
        webView.loadUrl(newUrl);
        console("Link got2: " + newUrl);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(this, "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            if (webView.copyBackForwardList().getSize() >= 7) {
                webView.goBackOrForward(-3);
                bigBack = true;
            } else {
                webView.goBack();
            }
            return;
        }
        super.onBackPressed();
    }

    private void console(String msg) {
        Log.d("Loger", msg);
    }
}