package com.example.setalarm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class ActivityReceiveAlarm extends Activity 
{

	private MediaPlayer mMediaPlayer;
	private Vibrator vibrator;
	private long[] pattern = { 0, 2000, 1000 };//start from, vibrate till , pause duration
	private Animation shake;
	private SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive_alarm);
		initData();
		initUI();
		
		
	}
	private void initUI() 
	{
		
	playAlarm();
	
	
		findViewById(R.id.ibCancelAlarm).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				mMediaPlayer.stop();
				vibrator.cancel();
				finish();
			}
		});
		
		
		long time=getIntent().getLongExtra("TIME", 0);
		((TextView)findViewById(R.id.tvTimeRecAct)).setText(sdfTime.format(new Date(time))+"");
		((TextView)findViewById(R.id.tvTimeRecAct)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf"));
		shake=AnimationUtils.loadAnimation(getApplicationContext(), R.anim.vibrate_anim);
		findViewById(R.id.ibCancelAlarm).setAnimation(shake);
	}

	private void playAlarm() 
	{
		try {
			
			/*timerVibrate=new Timer();
			timerVibrate.sc*/
				
				vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			    vibrator.vibrate(pattern,0);
			    
	    	 Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
	    	 mMediaPlayer = new MediaPlayer();
	    	 mMediaPlayer.setDataSource(getApplicationContext(), alert);
	    	 getSystemService(AUDIO_SERVICE);
	    	 
	    	 mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
	    	 mMediaPlayer.setLooping(true);
	    	 mMediaPlayer.prepare();
	    	 mMediaPlayer.start();
	    	 //}
	    	} 
		catch(Exception e) 
	    	{
			
	    	}  
		
	}

	private void initData() 
	{
		Window wind;
		wind = this.getWindow();
		wind.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		wind.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		wind.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}
		
		
}
