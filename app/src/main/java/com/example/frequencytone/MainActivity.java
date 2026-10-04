package com.example.frequencytone;

import android.Manifest;
import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.*;
import android.bluetooth.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    SeekBar freqBar, timeBar; TextView freqText,timeText,remainText,btText;
    Button btButton,startButton,stopButton; AudioTrack track; Thread audioThread; volatile boolean playing=false;
    int freq=300, seconds=60; BluetoothAdapter adapter;

    @Override public void onCreate(Bundle b){super.onCreate(b); buildUI();}

    TextView tv(String s,int size){ TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(Color.DKGRAY); t.setPadding(0,14,0,8); return t; }

    void buildUI(){
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(32,28,32,24);
        TextView title=tv("TONE GENERATOR",26); title.setTextColor(Color.rgb(20,80,150)); root.addView(title);
        freqText=tv("ความถี่: 300 Hz",22); root.addView(freqText);
        freqBar=new SeekBar(this); freqBar.setMax(400); freqBar.setProgress(200); root.addView(freqBar);
        TextView fr=tv("ช่วง 100 – 500 Hz",14); root.addView(fr);
        timeText=tv("เวลาเล่น: 60 วินาที",22); root.addView(timeText);
        timeBar=new SeekBar(this); timeBar.setMax(165); timeBar.setProgress(45); root.addView(timeBar);
        root.addView(tv("ช่วง 15 – 180 วินาที",14));
        remainText=tv("สถานะ: หยุด",20); root.addView(remainText);
        btText=tv("Bluetooth: ตรวจสอบ...",16); root.addView(btText);
        btButton=new Button(this); btButton.setText("เลือก Bluetooth"); root.addView(btButton);
        startButton=new Button(this); startButton.setText("▶ เริ่มเล่น"); root.addView(startButton);
        stopButton=new Button(this); stopButton.setText("■ หยุด"); root.addView(stopButton);
        setContentView(root);

        freqBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar s,int p,boolean u){freq=100+p; freqText.setText("ความถี่: "+freq+" Hz");}
            public void onStartTrackingTouch(SeekBar s){} public void onStopTrackingTouch(SeekBar s){}
        });
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar s,int p,boolean u){seconds=15+p; timeText.setText("เวลาเล่น: "+seconds+" วินาที");}
            public void onStartTrackingTouch(SeekBar s){} public void onStopTrackingTouch(SeekBar s){}
        });
        btButton.setOnClickListener(v->chooseBluetooth());
        startButton.setOnClickListener(v->startTone());
        stopButton.setOnClickListener(v->stopTone());
        refreshBluetooth();
    }

    void refreshBluetooth(){
        adapter=BluetoothAdapter.getDefaultAdapter();
        if(adapter==null){btText.setText("Bluetooth: ไม่รองรับ");return;}
        if(Build.VERSION.SDK_INT>=31 && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)!=PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT},10);
        else btText.setText("Bluetooth: "+(adapter.isEnabled()?"เปิดใช้งาน":"ปิดอยู่"));
    }

    void chooseBluetooth(){
        try{
            Intent i=new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(i);
        }catch(Exception e){ Toast.makeText(this,"เปิดการตั้งค่า Bluetooth ไม่ได้",Toast.LENGTH_SHORT).show(); }
    }

    void startTone(){
        stopTone();
        int sr=44100; int min=AudioTrack.getMinBufferSize(sr,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
        track=new AudioTrack(AudioManager.STREAM_MUSIC,sr,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT,min,AudioTrack.MODE_STREAM);
        track.play(); playing=true; remainText.setText("กำลังเล่น "+freq+" Hz");
        final int f=freq, dur=seconds;
        audioThread=new Thread(()->{
            byte[] buf=new byte[4096]; double phase=0, step=2*Math.PI*f/sr; long end=System.currentTimeMillis()+dur*1000L;
            while(playing && System.currentTimeMillis()<end){
                for(int i=0;i<buf.length/2;i++){ short v=(short)(Math.sin(phase)*12000); buf[2*i]=(byte)(v&255); buf[2*i+1]=(byte)((v>>8)&255); phase+=step; if(phase>2*Math.PI)phase-=2*Math.PI; }
                track.write(buf,0,buf.length);
            }
            runOnUiThread(()->{remainText.setText("สถานะ: หยุด");});
            stopToneInternal();
        }); audioThread.start();
    }

    void stopTone(){ stopToneInternal(); remainText.setText("สถานะ: หยุด"); }
    void stopToneInternal(){ playing=false; if(track!=null){try{track.pause();track.flush();track.release();}catch(Exception e){} track=null;} }
    @Override protected void onDestroy(){stopToneInternal();super.onDestroy();}
}