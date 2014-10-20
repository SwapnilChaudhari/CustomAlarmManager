package com.example.setalarm;



import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver 
{

	private LibFile libFile;

	String days[]={"Daily","Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		
		
		
		libFile=LibFile.getInstance(context);
		
		int id=intent.getIntExtra("ID_ALARM",0);
		//long time
		long activeAlarmTime=LibFile.getInstance(context).getAlarmTime(id);
		
		long deltaAlarmTime = System.currentTimeMillis() - activeAlarmTime;
		
		long  newAlarmTime=activeAlarmTime +(int) Math.floor(deltaAlarmTime /(24*60*60*1000))* 24*60*60*1000;
		
		Calendar cad= Calendar.getInstance();
		
		cad.setTimeInMillis(newAlarmTime);
		
		int currentDay=cad.get(Calendar.DAY_OF_WEEK);
		
		//Toast.makeText(context, " cu:"+days[currentDay], Toast.LENGTH_LONG ).show();
		
		int storedDay = libFile.getAlarmDay(id, currentDay);
		
		if(!libFile.isAlarmRepeatEveryDay(id) && storedDay != AppsConstant.SET_DAY) 
		{
			//Toast.makeText(context, "Wrong Day! cu:"+days[currentDay] +":: st"+storedDay,Toast.LENGTH_LONG).show();
			return;
		}
		
		if( System.currentTimeMillis() > newAlarmTime + 1*60*1000) // if curr time is greater than active alarm time and if user on it after offing it then skip current alarm and set it from next day.  
		{
		activeAlarmTime = newAlarmTime + 24*60*60*1000;
		
		//Toast.makeText(context, "Alarm skipped set from next day " +id+ " ",Toast.LENGTH_LONG).show();
		return;
		}
		
		
		
		
		 Toast.makeText(context, "Time To WakeUp!" ,Toast.LENGTH_LONG).show();
				    // Vibrate the mobile phone
				   
				    Intent intentAlarm = new Intent(context, ActivityReceiveAlarm.class);
				    
				    intentAlarm.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
	                intentAlarm.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				    intentAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    intentAlarm.putExtra("TIME", newAlarmTime);
	                
				    context.startActivity(intentAlarm);
				   
	}

	
}
