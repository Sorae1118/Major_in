package com.example.major_in;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, "6bcb3cd66c62feef8a7a93eeecafa638");
    }
}
