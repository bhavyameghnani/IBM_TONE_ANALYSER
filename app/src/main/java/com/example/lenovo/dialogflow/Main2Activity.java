package com.example.lenovo.dialogflow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Tone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.util.List;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    Button bt;
    EditText et;
    final ToneAnalyzer toneAnalyzer = new ToneAnalyzer("2017-01-01");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        bt = (Button)findViewById(R.id.buttonSend);
        et =(EditText)findViewById(R.id.editTextMessage);

        try {

            JSONObject credentials; // Convert the file into a JSON object
            credentials = new JSONObject(IOUtils.toString(getResources().openRawResource(R.raw.credentials), "UTF-8"));

            String username = credentials.getString("username");
            String password = credentials.getString("password");

            toneAnalyzer.setUsernameAndPassword(username, password);

        } catch(Exception e){}


        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == bt){
            final String textToAnalyze = et.getText().toString().trim();
            ToneOptions options = new ToneOptions.Builder()
                    .addTone(Tone.EMOTION)
                    .html(false).build();

            toneAnalyzer.getTone(textToAnalyze, options).enqueue(
                    new ServiceCallback<ToneAnalysis>() {
                        @Override
                        public void onResponse(ToneAnalysis response) {
                            List<ToneScore> scores = response.getDocumentTone()
                                    .getTones()
                                    .get(0)
                                    .getTones();

                            String detectedTones = "";
                            for(ToneScore score:scores) {
                                if(score.getScore() > 0.5f) {
                                    detectedTones += score.getName() + " ";
                                }
                            }

                            final String toastMessage =
                                    "The following emotions were detected:\n\n"
                                            + detectedTones.toUpperCase();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(),
                                            toastMessage, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            e.printStackTrace();
                        }
                    });


        }
    }
}
