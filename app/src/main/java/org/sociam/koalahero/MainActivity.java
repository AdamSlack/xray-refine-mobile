package org.sociam.koalahero;

import android.arch.core.util.Function;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import org.sociam.koalahero.csm.CSMAPI;
import org.sociam.koalahero.csm.CSMAppInfo;
import org.sociam.koalahero.xray.XRayAPI;
import org.sociam.koalahero.xray.XRayAppInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Start with Loading Screen Layout.
        super.onCreate(savedInstanceState);


        foo();
    }

    private void beginLoading() {
        setContentView(R.layout.loading_screen);
        // Set loading screen anim.
        WebView animWebView = (WebView) findViewById(R.id.loading_screen_web_view);
        animWebView.loadUrl("file:///android_asset/Loading_icon.gif");
        animWebView.setBackgroundColor(Color.TRANSPARENT);
        animWebView.setVerticalScrollBarEnabled(false);
        animWebView.setHorizontalScrollBarEnabled(false);
        animWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
    }

    private void launchMainView() {
        setContentView(R.layout.activity_main);
    }

    private void foo() {
        XRayAPI api = XRayAPI.getInstance();

        new XRayAPI.XRayAppData(
                new Function<Void, Void>(){
                    @Override
                    public Void apply(Void nothing){
                        return null;
                    }
                },
                new Function<XRayAppInfo, Void>() {
                    @Override
                    public Void apply(XRayAppInfo appInfo){
                        System.out.println(appInfo.appStoreInfo.title);
                        return null;
                    }
                },
                getApplicationContext()

        ).execute("com.linkedin.android","com.whatsapp","com.tencent.mm");

        new CSMAPI.CSMRequest(
                new Function<CSMAppInfo, Void>() {
                    @Override
                    public Void apply(CSMAppInfo csmAppInfo) {
                        System.out.println(csmAppInfo.oneLiner);
                        return null;
                    }
                },
                getApplicationContext()
        ).execute("com.linkedin.android","com.whatsapp","com.tencent.mm");
    }
}
