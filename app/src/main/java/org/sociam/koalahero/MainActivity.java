package org.sociam.koalahero;

import android.arch.core.util.Function;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.sociam.koalahero.xray.XRayAPI;
import org.sociam.koalahero.xray.XRayAppInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        foo();
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

        ).execute("com.linkedin.android","com.whatsapp");

    }
}
