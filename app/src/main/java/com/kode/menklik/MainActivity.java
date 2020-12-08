package com.kode.menklik;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kode.menklik.data.Script;

import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    TextView console;
    Spinner spi_script;
    public static String MSG = "";
    Script current_script;
    int script_loaded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 1){
                    console.setText(String.format(Locale.US, "%s\n%s", console.getText().toString(), MSG));
                    ((ScrollView)findViewById(R.id.scrollView)).fullScroll(View.FOCUS_DOWN);
                }
                super.handleMessage(message);
            }
        };

        console = (TextView) findViewById(R.id.console);

        webView = (WebView) findViewById(R.id.webview);
        webView.addJavascriptInterface(new WebAppInterface(this, handler), "Android");
        //webView.setWebChromeClient(new MyChrome());
        webView.setWebViewClient(new MyBrowser());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

            addScript();

        spi_script = (Spinner) findViewById(R.id.spi_script);
        spi_script.setAdapter(
                new ArrayAdapter<Script>(
                        this,
                        android.R.layout.simple_list_item_1,
                        Script.listAll(Script.class)
                )
        );



    }

    public void loadSite(View view){

        current_script = (Script) spi_script.getSelectedItem();

        console.setText(String.format(Locale.US, "%s\nLoading : %s", console.getText().toString(), current_script.getLabel()));
        ((ScrollView)findViewById(R.id.scrollView)).fullScroll(View.FOCUS_DOWN);

        webView.loadUrl("http://bitcofarm.com/account");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            webView.reload();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public class WebAppInterface {
        Context mContext;
        Handler handler;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c, Handler h) {
            mContext = c;
            handler = h;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Log.d("Server says", toast);
            MainActivity.MSG = toast;
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);

        }
    }

    class MyBrowser extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            console.setText(String.format(Locale.US, "%s\nPage loaded : %s", console.getText().toString(), url));
            ((ScrollView)findViewById(R.id.scrollView)).fullScroll(View.FOCUS_DOWN);

            if (url.contains("account")){

                view.getSettings().setLoadsImagesAutomatically(false);
            }

            if (url.equals("http://bitcofarm.com/ads") && script_loaded != 1){
                script_loaded = 1;
                String s = new StringBuilder()
                        .append(current_script.getSource())
                        .toString();

                view.evaluateJavascript(s, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d("Script says", s);
                    }
                });
            }

        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Log.d("Server says", error.toString());
        }
    }

    class MyChrome extends WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            Log.v("MainActivity", "Loading progress : " + newProgress);

            if (newProgress == 100){
                String url = view.getUrl();

                console.setText(String.format(Locale.US, "%s\nPage loaded! %s", console.getText().toString(), url));
                ((ScrollView)findViewById(R.id.scrollView)).fullScroll(View.FOCUS_DOWN);

                if (url.equals("http://bitcofarm.com/ads")){
                    String s = new StringBuilder()
                            .append(current_script.getSource())
                            .toString();

                    view.evaluateJavascript(s, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("Script says", s);
                        }
                    });
                }
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return super.onConsoleMessage(consoleMessage);
        }
    }


    private void addScript(){
        Script.deleteAll(Script.class);

        Script script = new Script();
        script.setLabel("bitcofarn DESC");
        script.setDescription("Click on ads from bottom to the top");
        script.setDatecreated((new Date()).toString());
        script.setSource("!function(){\"use strict\";$(document).ready(function(){var e=document.getElementById(\"right\").getElementsByTagName(\"a\");Android.showToast(e.length);for(var o=0,t=0,d=[],a=0;a<e.length;a++){var s=e[a].getElementsByTagName(\"div\");s.length>0&&\"hap disabled_pbx\"!=s[0].className&&d.push(e[a])}e=d,Android.showToast(e.length),o=e.length-1;var n=0,i=null,l=setInterval(function(){try{var d=32,a=e[o].getElementsByClassName(\"hap_title\")[0],s=a.style.backgroundColor;if(d=\"rgb(0, 102, 153)\"===s?33:\"rgb(255, 102, 0)\"===a.style.background?20:13,Android.showToast(\"elapsed t : \"+n+\", ad_time : \"+d),n>d+5){if(n=0,Android.showToast(o),o>=0){var r=e[o].getElementsByTagName(\"div\")[0];$(\"html,body\").animate({scrollTop:$(r).offset().top},\"slow\"),\"hap disabled_pbx\"!=r.className&&i!=r.id?(Android.showToast(r.id+\" : launched !\"),i=r.id,$.get(e[o].href,function(e){Android.showToast(r.id+\" : anwsered !\"),setTimeout(function(){$.get(\"http://bitcofarm.com/modules/virtual_core.php\",\"adv=\"+r.id+\"&action=adv\",function(e){\"Completed!\"==e?($(\"#\"+r.id).addClass(\"disabled_pbx\"),t+=1,Android.showToast(r.id+\" : \"+e)):Android.showToast(r.id+\" : \"+e)})},1e3*d)})):Android.showToast(\"Already clicked\")}else o<=-1&&(Android.showToast(\"clicked \"+t+\" / \"+e.length),Android.showToast(\"done... exit!\"),clearInterval(l));o-=1}n+=5}catch(d){Android.showToast(d.name+\": \"+d.message),(o-=1)<=-1&&(Android.showToast(\"clicked \"+t+\" / \"+e.length),Android.showToast(\"done... exit!\"),clearInterval(l))}},5e3)})}();");
        script.save();
    }
}
