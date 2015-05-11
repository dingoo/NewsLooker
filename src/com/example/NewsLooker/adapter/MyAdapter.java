package com.example.NewsLooker.adapter;

import cn.sharesdk.framework.authorize.AuthorizeAdapter;

public class MyAdapter extends AuthorizeAdapter {
    public void onCreate() {
        // 隐藏标题栏右部的ShareSDK Logo
        hideShareSDKLogo();
    }
}
