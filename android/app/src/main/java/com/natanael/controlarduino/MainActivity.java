package com.natanael.controlarduino;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.getcapacitor.BridgeActivity;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends BridgeActivity {
    private static final int REQUEST_CODE_SPEECH = 1000;
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // REGISTRAR TU PLUGIN DE BLUETOOTH EN CAPACITOR
        registerPlugin(AndroidBluetooth.class);

        super.onCreate(savedInstanceState);

        this.bridge.getWebView().post(() -> {
            webView = this.bridge.getWebView();
            webView.addJavascriptInterface(new WebAppInterface(), "AndroidVoz");
        });
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void abrirMicrofono() {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di un comando...");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String texto = result.get(0).toLowerCase();
                runOnUiThread(() -> {
                    if (webView != null) {
                        webView.evaluateJavascript("javascript:procesarVozNativa('" + texto + "')", null);
                    }
                });
            }
        }
    }
}