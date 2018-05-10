package com.example.archi.visuallyimpairedassistappfin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

public class FetchProcessedImage extends Activity implements TextToSpeech.OnInitListener{
    private ImageView img;
    private TextView t1;
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_processed_image);
        tts = new TextToSpeech(this, this);
        Intent intent = getIntent();
        String fin_ans = intent.getStringExtra("tttt");
        String natos = intent.getStringExtra("tttt1");
        img=(ImageView)findViewById(R.id.imageView2);
        img.setImageBitmap(StringtoBitMap(fin_ans));
        t1=(TextView)findViewById(R.id.textView4);
        t1.setText(natos);
        speakOut();


    }
    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            // tts.setPitch(5); // set pitch level

            // tts.setSpeechRate(2); // set speech speed rate

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed");
        }

    }

    public Bitmap StringtoBitMap(String encodedString){
        byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    private void speakOut() {

        String text = t1.getText().toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
