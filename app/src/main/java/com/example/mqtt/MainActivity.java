package com.example.mqtt;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {



    private static final int speak_request = 1000;

    ImageButton speak;
    EditText command;
    Button Connect,Publish;
    MqttAndroidClient client;
    TextView t1,temp;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speak = findViewById(R.id.speak);
        Connect = findViewById(R.id.Connect);
        Publish = findViewById(R.id.publish);
      //  command=findViewById(R.id.data);
        t1=findViewById(R.id.t1);
        temp=findViewById(R.id.temp);


        if (ConnectionStatus()) {

            Toast.makeText(MainActivity.this, "Connected to Internet", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        //start connect




        ///connect

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoiceInput();

            }
        });

        Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();

            }
        });

        Publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Publish();
            }
        });

    }

    public void VoiceInput() {

        Intent voiceintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceintent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hii  Speak Now");
        voiceintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.
                LANGUAGE_MODEL_FREE_FORM);
        voiceintent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);



        try {
            startActivityForResult(voiceintent, speak_request);
        } catch (Exception e)
        {
            Toast.makeText(MainActivity.this,"Unable to Recognise the voice ",Toast.LENGTH_SHORT).show();

        }

        }
//To receive voice
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode)
            {
                case speak_request:
                {

                       ArrayList<String> output = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                       if(data!=null)
                       {
                       // command.setText(output.get(0));
                       temp.setText(output.get(0));

                       //publish
                           String topic = "inTopic";

                           String payload = temp.getText().toString();
                           Toast.makeText(MainActivity.this,"Messsage Published",Toast.LENGTH_SHORT).show();
                           byte[] encodedPayload = new byte[0];
                           try {
                               encodedPayload = payload.getBytes("UTF-8");
                               MqttMessage message = new MqttMessage(encodedPayload);
                               client.publish(topic, message);
                           } catch (UnsupportedEncodingException | MqttException e) {
                               e.printStackTrace();
                           }





                   }
                   break;
                }
            }
    }

    public boolean ConnectionStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activenetwork = connectivityManager.getActiveNetworkInfo();


        return activenetwork != null && activenetwork.isConnected();


    }

    public void connect ()
    {

        String clientId = MqttClient.generateClientId();

         client = new MqttAndroidClient(this.getApplicationContext(),"tcp://192.168.1.204:1883",clientId);

        MqttConnectOptions options= new MqttConnectOptions();

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener(){
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                   Toast.makeText(MainActivity.this,"Connected",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this,"Unable to Connect", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void Publish()
    {
//        String topic = "inTopic";
//
//        String payload = temp.getText().toString();
//        Toast.makeText(MainActivity.this,"Messsage Published",Toast.LENGTH_SHORT).show();
//        byte[] encodedPayload = new byte[0];
//        try {
//            encodedPayload = payload.getBytes("UTF-8");
//            MqttMessage message = new MqttMessage(encodedPayload);
//            client.publish(topic, message);
//        } catch (UnsupportedEncodingException | MqttException e) {
//            e.printStackTrace();
//        }
    }
}

