package com.you.edu.live.teacher.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.utils.CommonUtil;
import com.you.edu.live.teacher.view.BaseViewActivity;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * html详情页
 *
 * @author XingRongJing
 */
public class Html5TabActivity extends BaseViewActivity {

    @BindView(R.id.ll_html_root)
    LinearLayout mLlHtmlRoot;
    @BindView(R.id.titlebar_back_iv_back)
    ImageView mTitlebarIvBack;
    @BindView(R.id.titlebar_back_tv_title)
    TextView mTvTitle;
    @BindView(R.id.html5_tab_webview)
    WebView mWebView;
    private Unbinder mUnbinder;

    private static final String H5Url = GlobalConfig.HttpUrl.BASE_URL_DOMAIN
            + "/user/RedirectUrl?";

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
//        /**
//         * 更改状态栏颜色 只适用于系统版本4.4及以上
//         */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            this.setTranslucentStatus(true);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager
//                    .setStatusBarTintResource(R.color.translucent);// 通知栏所需颜色
//        }

        super.onCreate(arg0);
        this.setContentView(R.layout.activity_html5_tab);
        mUnbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        mTvTitle.setText(title);
        mTitlebarIvBack.setImageResource(R.drawable.black_back);

        mWebView.getSettings().setJavaScriptEnabled(true);// 启用javascript支持
        mWebView.getSettings().setUseWideViewPort(true); // 加上这一行就解决了。
        mWebView.getSettings()
                .setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        String ua = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(ua+" youkuSchoolApp");


//        CookieSyncManager.createInstance(this);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.removeAllCookie();
//        String cookie = AppHelper.getAppHelper().getUser(this).getCookie();

//        cookieManager.setCookie(url, cookie + "");
//        this.setCookie(url,cookieManager,AppHelper.getAppHelper().getUser(this).getCookie());
//        CookieSyncManager.getInstance().sync();
        User user = AppHelper.getAppHelper().getUser(this);
        if (user != null) {
            url = GlobalConfig.HttpUrl.BASE_URL_DOMAIN
                    + "url="
                    + CommonUtil.urlEncode(url)
                    + "&yktk="
                    + CommonUtil.urlEncode(user.getYktk())
                    + "&atype=6";
            mWebView.setWebViewClient(mWVClient);
            mWebView.loadUrl(url);
        }
    }

    public WebViewClient mWVClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.reload();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (mWebView != null) {//清除webview所有的缓存和cookie
            mWebView.clearCache(true);
            mWebView.clearHistory();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            mWebView.destroy();
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }

    @OnClick(R.id.titlebar_back_iv_back)
    public void onClickBack() {
        this.finish();
    }

    private void setCookie(String url, CookieManager cookieManager, String cookie) {
        if (cookieManager == null) {
            return;
        }
        if (TextUtils.isEmpty(cookie) || TextUtils.isEmpty(url)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (cookie.contains(";")) {
            String pattern = ";";
            Pattern pat = Pattern.compile(pattern);
            String[] rsr = pat.split(cookie);
            for (int i = 0; i < rsr.length; i++) {//因为cookie 遇到分号会截取前面的一段数据后就不要后面的数据了
                //所以需要给把cookie分割,每一段都设置进去才能使用
                String s = rsr[i];
                cookieManager.setCookie(url, s);
            }
        }
    }

}
