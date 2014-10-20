package com.example.setalarm;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class LibFile 
{
	Context context;
	
	String days[]={"Daily","Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	
	SharedPreferences settings;
	
	private static LibFile instance;
	
	public static LibFile getInstance(Context context)
	{
		if(instance == null)
		{
			instance = new LibFile(context);
			
		}
		return instance;
	}
	private LibFile(Context context)
	{	
		this.context  = context;
		
	 settings = context.getSharedPreferences("ALARM_MANAGER", 0);

	}
	public void setTotalAlarm(int id)
	{
		settings.edit().putInt("total_alarm_delivered", id).commit();
	}
	public int getTotalAlarm()
	{
		return settings.getInt("total_alarm_delivered",0);
	}
	
	public void setAlarmTime(int id,long time)
	{
		settings.edit().putLong("alarm_time"+id ,time ).commit();
	}
	public long getAlarmTime(int id)
	{
		return settings.getLong("alarm_time"+id , 0);
	}
	
	public void setAlarmRepeatEveryDay(int id,boolean repeatEveryday)
	{
		settings.edit().putBoolean("alarm_repeatEveryday"+id ,repeatEveryday).commit();
		
		if(repeatEveryday)
		{
			for(int i=0;i<7;i++)
			{
				setAlarmDay(id,i+1);//cal.Sun to Cal.Sat
			}
		}
		
	}
	public boolean isAlarmRepeatEveryDay(int id)
	{
		return settings.getBoolean("alarm_repeatEveryday"+id , false);
	}
	
	
	public void setAlarmDay(int id,int day )
	{
		Log.v("TAG", "SET DAY:"+days[day]+" : "+day);
		settings.edit().putInt("alarm_day:"+id+":"+day, AppsConstant.SET_DAY).commit();
	}
	
	public void removeAlarmDay(int id,int day )
	{
		Log.v("TAG", "SET REMOVE DAY:"+days[day]);
		settings.edit().putInt("alarm_day:"+id+":"+day, AppsConstant.REMOVE_DAY).commit();
	}
	
	public int getAlarmDay(int id,int day)
	{
		Log.v("TAG", "GET DAY:"+days[day]);
		return settings.getInt("alarm_day:"+id+":"+day, AppsConstant.REMOVE_DAY);
	}
	
	
	public void setAlarmActive(int id,boolean active)
	{
		settings.edit().putBoolean("alarm_Active"+id ,active).commit();
	}
	public boolean isAlarmActive(int id)
	{
		return settings.getBoolean("alarm_Active"+id , false);
	}
	
	public void clearData()
	{
		 SharedPreferences.Editor editor = settings.edit();
		    editor.clear();
		    editor.commit(); 
	}
}
