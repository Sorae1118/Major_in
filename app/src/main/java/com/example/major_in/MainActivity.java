package com.example.major_in;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity"; //태그
    private View loginButton, logoutButton; //로그인, 로그아웃
    private TextView nickName;              //닉네임
    private ImageView profileImage;         //프로필이미지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login);
        logoutButton = findViewById(R.id.logout);
        nickName = findViewById(R.id.nickname);
        profileImage = findViewById(R.id.profile);

        //콜백 객체, 로그인 결과에 대한 처리
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                //토큰이 전달되면 로그인 성공, null이면 실패 (디테일한 처리인 듯)
                if (oAuthToken != null) {
                    // TBD
                }
                //결과가 오류가 있다면 처리 (이것도)
                if (throwable != null) {
                    // TBD
                }
                updateKakaoLoginUi();
                return null;
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //설치 여부 확인 (ture : 카카오톡 설치된 경우. 카카오톡을 띄워서 로그인, false : 아닌 경우. 카카오톡 홈페이지를 통해 결과를 전달 받는다)
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)) {
                    //파라미터로는 context, login 결과를 처리하기 위한 invoke 콜백(function2)
                    UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, callback); //카카오앱
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this, callback); //홈페이지
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        updateKakaoLoginUi();
                        return null;
                    }
                });
            }
        });
        //로그인 상태에 따라 UI를 달리함
        updateKakaoLoginUi();
    }

    private void updateKakaoLoginUi() {
        //로그인이 되어있는지 확인하고 invoke 메소드를 콜백함
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                //로그인 상태일 경우
                if (user != null) {
                    //얻을 수 있는 계정의 다양한 정보들
                    Log.i(TAG, "invoke: id=" + user.getId()); //계정 아이디
                    Log.i(TAG, "invoke: id=" + user.getKakaoAccount().getEmail()); //계정 이메일
                    Log.i(TAG, "invoke: id=" + user.getKakaoAccount().getProfile().
                            getThumbnailImageUrl()); //계정 프로필 이미지
                    Log.i(TAG, "invoke: id=" + user.getKakaoAccount().getProfile().
                            getNickname()); //계정 닉네임

                    nickName.setText(user.getKakaoAccount().getProfile().getNickname());
                    //글라이드 오픈소스 이미지뷰 사용하기
                    Glide.with(profileImage).load(user.getKakaoAccount().getProfile().
                            getThumbnailImageUrl()).circleCrop().into(profileImage);

                    loginButton.setVisibility(View.GONE);
                    logoutButton.setVisibility(View.VISIBLE);
                //로그인 상태가 아닐 경우
                } else {
                    nickName.setText(null);
                    profileImage.setImageBitmap(null);

                    loginButton.setVisibility(View.VISIBLE);
                    logoutButton.setVisibility(View.GONE);
                }
                return null;
            }
        });
    }
}
