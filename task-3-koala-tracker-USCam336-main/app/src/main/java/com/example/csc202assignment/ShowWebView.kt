package com.example.csc202assignment

import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.webkit.RenderProcessGoneDetail
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class ShowWebView : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_web_view)
        val url = intent.getStringExtra("passedUrl")
        val webView: WebView = findViewById(R.id.webView)

        webView.settings.useWideViewPort = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onRenderProcessGone(
                view: WebView?,
                detail: RenderProcessGoneDetail?
            ): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    if (detail?.didCrash() == false) {
                        webView.loadUrl(url.toString())
                        return true
                    }
                return false
            }

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                super.onReceivedSslError(view, handler, error)
                handler.proceed()


            }


        }
    }
}

/*
class OpenWebViewClient: WebViewClient(){
    @SuppressLint("WebViewClientOnReceivedSslError")
    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        super.onReceivedSslError(view, handler, error)
        handler.proceed()


    }

}
*/






