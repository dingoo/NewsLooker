package com.example.NewsLooker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import com.example.NewsLooker.util.CommonTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WelcomActivity extends Activity {

    private AlphaAnimation start_anima;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view=View.inflate(this,R.layout.welcome,null);
        setContentView(view);

        File file= new File("/data/data/"+getPackageName().toString()+"/shared_prefs","userinfo.xml");

        if(!file.exists()){
            List<Object> list=new ArrayList<Object>();
            list.add(0,false);
            list.add(1,"");
            list.add(2,"");
            list.add(3,"");
            list.add(4,false);
            list.add(5,false);
            list.add(6,-1);
            CommonTools.putCacheUserinfo(this,list);
        }

        start_anima=new AlphaAnimation(0.3f,1.0f);
        start_anima.setDuration(1000);
        view.setAnimation(start_anima);
        start_anima.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                WelcomActivity.this.finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
