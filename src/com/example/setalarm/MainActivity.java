package com.example.setalarm;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends Activity 
{
	//private boolean circleChanged;
	private RelativeLayout rlViewHolder;
	private LayoutInflater inflater;
	private int centerX;
	private int centerY;
	private int radMinCircle; //1st Circle
	private int radHourCircle; //0th Circle
	private View highliterView;
	private int selectedCircle;
	private int selectedCircleRadious;
	private double selectedCircleDeltaThetaChilds;
	private HashMap<Integer, Integer> mapCircleRadious;//to hold radius(value) of nth circle (n-key)
	private HashMap<Integer, Integer> mapCircleChildCounts; //to hold items(value) of child for nth circle (n-key)
	private HashMap<Integer, Integer> mapViewHoveredIndex;// key no of circle, val-index of child in layout, to store index of last hovered clock digit view
	private HashMap<Integer, Integer> mapPrevSelectedIndex;// to hold last hovered view on action up so on action up we will need to change only last selected views bg and text color instead of all views 
	private float density;
	private int totalNoOfCircle;
	private double thetaRadianCurr;
	private Typeface RalewayMedium;
	private Typeface RalewayBold;
	private Typeface RalewayExBold;
	private Typeface RalewayExLight;
	private Typeface RalewayHeavy;
	private Typeface RalewayLight;
	private Typeface RalewayRegular;
	private Typeface RalewaySemibold;
	private Typeface RalewayThin;
	private TextView highliterView2;
	private LibFile libFile;
	private ViewFlipper viewFlipper;
	private LinearLayout llListAlarms;
	private ArrayList<Alarms> alArms;
	private SimpleDateFormat sdf;
	private Vibrator vibrator;
	protected boolean positonFocusedChanged;
	protected int prevHoveredChildIndexOnParent;
	private TextView tvTimeOfViewHovered;
	private Typeface HelveticaNeueUltraLight;
	private SimpleDateFormat sdfTime;
	private LinearLayout toastLayout;
	
	
	//private View highliterSelectedView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		initData();
		initUi();
	}

	

	private void initData() {
		
		sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault());
		sdfTime = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
		
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		
		int width = metrics.widthPixels;
		
		 density = getApplicationContext().getResources().getDisplayMetrics().density;
		
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	    
	    
		libFile=LibFile.getInstance(getApplicationContext());
		
		radMinCircle=width/3+50;
		radHourCircle=width/4+50;
		centerX=(int) width/2;
		centerY=(int) radMinCircle+100;
		
		mapCircleChildCounts=new HashMap<Integer, Integer>();
		mapCircleRadious=new HashMap<Integer, Integer>();
		mapViewHoveredIndex=new HashMap<Integer, Integer>();
		mapPrevSelectedIndex=new HashMap<Integer, Integer>();
		alArms=new ArrayList<Alarms>();
		
		RalewayMedium = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");		
		RalewayBold = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Bold.ttf");		
	    RalewayExBold= Typeface.createFromAsset(getAssets(), "fonts/Raleway-ExtraBold.ttf");
	    RalewayExLight=Typeface.createFromAsset(getAssets(), "fonts/Raleway-ExtraLight.ttf");
	    RalewayHeavy=Typeface.createFromAsset(getAssets(), "fonts/Raleway-Heavy.ttf");
	    RalewayLight=Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
	    RalewayRegular=Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
	    RalewaySemibold=Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");
	    RalewayThin=Typeface.createFromAsset(getAssets(), "fonts/Raleway-Thin.ttf");
	    HelveticaNeueUltraLight = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueUltraLight.ttf");		
	
	}

	private void initUi() 
	{
		 

		/*// Output yes if can vibrate, no otherwise
		if (vibrator.hasVibrator()) 
		{ 
		    Toast.makeText(getApplicationContext(), "VIBRATE: YES", Toast.LENGTH_LONG).show();
		} 
		else 
		{
		    //Log.v("Can Vibrate", "VIBRATE: NO");
			Toast.makeText(getApplicationContext(), "VIBRATE: NO", Toast.LENGTH_LONG).show();
		}*/
		inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
	
		rlViewHolder=(RelativeLayout)findViewById(R.id.rlViewHolder);
		
		inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		
		highliterView=findViewById(R.id.llHighliter);
		
		highliterView2=(TextView)findViewById(R.id.tvhighliter2);
		highliterView2.setTypeface(RalewayRegular);
		
		viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		
		llListAlarms=(LinearLayout)findViewById(R.id.llListAlarms);
        
		toastLayout = (LinearLayout) inflater.inflate(R.layout.toast_layout, null);
		
		addChildViewDynamically(radHourCircle,24);
		
		addChildViewDynamically(radMinCircle,60);
		
		rlViewHolder.getChildAt(0).setSelected(true);// select 24
		
		//changeTextColorOnHover(0);
		
		//mapPrevSelectedIndex.put(1, 0);
		
		//mapViewHoveredIndex.put(1, 0);
		
		//rlViewHolder.getChildAt(mapCircleChildCounts.get(1)).setSelected(true);// select 60
		
		//changeTextColorOnHover(mapCircleChildCounts.get(1)+1);
		
		//mapPrevSelectedIndex.put(2, mapCircleChildCounts.get(1)+1); 
		
		mapPrevSelectedIndex.put(1, 0);
		
		mapPrevSelectedIndex.put(2, mapCircleChildCounts.get(1));
		
		mapViewHoveredIndex.put(2, mapCircleChildCounts.get(1));
		
		displayAlarmList();
		
	    setTimeDisplayLabel();
		
		findViewById(R.id.btnDone).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				long alarmTime=getAlarmTime();
				
				setAlarm(alarmTime,libFile.getTotalAlarm()+1);
				
			}
		});
		
		findViewById(R.id.btnClearAll).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				/*long alarmTime=getAlarmTime();
				
				setAlarm(alarmTime,libFile.getTotalAlarm()+1);*/
				libFile.clearData();
				
				
				for(int i=0;i<alArms.size() ;i++)
				{
					cancelAlarm(alArms.get(i).getId());
				}
				
				alArms.clear();
				//snapchat
				llListAlarms.removeAllViews();
				showTost( "All alarms removed");
				//Toast.makeText(getApplicationContext(), "All alarms removed", Toast.LENGTH_LONG).show();
			}
		});
		
		
		((Button)findViewById(R.id.btnSetAlarm)).setTypeface(RalewayMedium);
		findViewById(R.id.btnSetAlarm).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{	
				viewFlipper.setInAnimation(getApplicationContext(), R.anim.slide_in_from_left_to_right);
				
				viewFlipper.setOutAnimation(getApplicationContext(), R.anim.slide_out_from_left_to_right);
				
				viewFlipper.setDisplayedChild(0);
				
				showTost("Drag your finger on clock to select time and select DONE .");
				
			}
		});
		
		((Button)findViewById(R.id.btnViewAlarms)).setTypeface(RalewayMedium);
		findViewById(R.id.btnViewAlarms).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				viewFlipper.setInAnimation(getApplicationContext(), R.anim.slide_in_right_to_left);
				
				viewFlipper.setOutAnimation(getApplicationContext(), R.anim.slide_out_right_to_left);
				
				viewFlipper.setDisplayedChild(1);
				
				if(alArms.size()==0) showTost("No alarms have been set yet.");//Toast.makeText(getApplicationContext(), "No alarms have been set yet.", Toast.LENGTH_LONG).show();
			}
		});
		
		

		rlViewHolder.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				int currentSelectedChildIndexOnParent; 
				
				float touchX=event.getX();
				
				float touchY=event.getY();
			    
				float deltaX = (float) (centerX - touchX );
				
				float	deltaY = (float) (centerY - touchY );
				
				thetaRadianCurr = Math.atan2(deltaY, deltaX);
				
				thetaRadianCurr=thetaRadianCurr+Math.PI;
				
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					
					
					double distance=Math.sqrt(Math.pow(centerX - touchX, 2)+Math.pow(centerY - touchY, 2));
					if(distance<mapCircleRadious.get(2)-20) 
						{
						selectedCircle=1;
						}
					else 
						{
						selectedCircle=2;
						}
					
					selectedCircleDeltaThetaChilds = getDeltaBetnChilds(selectedCircle);
					
					selectedCircleRadious = mapCircleRadious.get(selectedCircle);
					
					break;
				case MotionEvent.ACTION_UP:

						
						
						thetaRadianCurr=getNearestPosition(thetaRadianCurr);
						
						currentSelectedChildIndexOnParent=getcurrSelectedViewIndexOnParent(thetaRadianCurr);
						
						changeTextColorOnHover(currentSelectedChildIndexOnParent);
						
						updateSelectedView(currentSelectedChildIndexOnParent);
						
						highliterView.setVisibility(View.GONE);
						
						highliterView2.setVisibility(View.GONE);
					break;
				case MotionEvent.ACTION_MOVE:
					
					highliterView.setVisibility(View.VISIBLE);
					
					setLocationOnScreen(selectedCircleRadious, highliterView ,  thetaRadianCurr);
					
					currentSelectedChildIndexOnParent = getcurrSelectedViewIndexOnParent(thetaRadianCurr);
					
					TextView childAt=(TextView)rlViewHolder.getChildAt(currentSelectedChildIndexOnParent);
					
					if(highliterView2.getVisibility()==View.GONE)highliterView2.setVisibility(View.VISIBLE);
					
					setLocationOnScreen(selectedCircleRadious, highliterView2 ,  thetaRadianCurr);
					
					highliterView2.setText(childAt.getText());
					
					if(mapViewHoveredIndex.get(selectedCircle) != currentSelectedChildIndexOnParent)
					{
						vibrator.vibrate(9);
						
					}
					changeTextColorOnHover(currentSelectedChildIndexOnParent);
					
					
					break;
					

				default:
					break;
				}
				
				
				
				return true;
			}
		});
		
		
		viewFlipper.setDisplayedChild(1);
		
		if(alArms.size()==0) showTost("No alarms have been set yet.");//Toast.makeText(getApplicationContext(), "No alarms have been set yet.", Toast.LENGTH_LONG).show();
		
	}

	protected void showTost(String msg) 
	{
		
		Toast toast = new Toast(getApplicationContext());  
        toast.setDuration(Toast.LENGTH_SHORT);  
        ((TextView)toastLayout.findViewById(R.id.tvToast)).setText(msg);
        ((TextView)toastLayout.findViewById(R.id.tvToast)).setTypeface(RalewaySemibold);
        toast.setView(toastLayout);//setting the view of custom toast layout  
        toast.show();  
	}



	private void setTimeDisplayLabel()
	{
		tvTimeOfViewHovered = (TextView) inflater.inflate(R.layout.single_view_clock_child, null);
		
		rlViewHolder.addView(tvTimeOfViewHovered);
		
		tvTimeOfViewHovered.setText("12:00 AM");
		
		tvTimeOfViewHovered.setTypeface(HelveticaNeueUltraLight);
		
		tvTimeOfViewHovered.setTextColor(getResources().getColor(R.color.txt_color_red));
		
		//tvTimeOfViewHovered.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_Size_label_time));//((float) (radHourCircle*7.5/50));//7sp at normal width(50px) so ? at radious width 
		 
		tvTimeOfViewHovered.setTextSize( (float) (((float)radHourCircle/2.75)/(float)density));
		
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() 
					{
						//setting text at center of circle
						tvTimeOfViewHovered.setX(centerX - tvTimeOfViewHovered.getMeasuredWidth()/2 +10);//radHourCircle+50);
						
						tvTimeOfViewHovered.setY(centerY - tvTimeOfViewHovered.getMeasuredHeight()/2+20);
						
					}
				});
				
			}
		}, 100);
		
		
	}



	protected long getAlarmTime() 
	{
		int hr=Integer.parseInt(((TextView)rlViewHolder.getChildAt(mapPrevSelectedIndex.get(1))).getText()+"");
		int min=Integer.parseInt(((TextView)rlViewHolder.getChildAt(mapPrevSelectedIndex.get(2))).getText()+"");
		
		Calendar cad=Calendar.getInstance();
		cad.set(Calendar.HOUR, 0);
		cad.set(Calendar.MINUTE, 0);
		cad.set(Calendar.SECOND, 0);
		cad.set(Calendar.AM_PM,  Calendar.AM );
		
		//dt.setDate()
		return ((hr%24)*60*60+(min%60)*60)*1000+cad.getTimeInMillis();
	}



	private void setAlarm(long alarmTime,int id)
	{
		boolean exist=checkAlreadyExist(alarmTime);
		if(exist)
		{
			showTost("Alarm already exist: ");//Toast.makeText(this, "Alarm already exist: ",Toast.LENGTH_LONG).show();
			return;
		}
			//Date dt=new Date(alarmTime);
			
			//String format = sdf.format(dt);
			
			//Toast.makeText(this, "Alarm set at " + format + " ",Toast.LENGTH_LONG).show();
			
			
		    
		    Alarms alarm =new Alarms();
		    alarm.setId(id);
		    alarm.setRepeadEveryDay(true);
		    alarm.setTime(alarmTime);
		    alarm.setActive(true);
		    alArms.add(alarm);
		    
		    addAlarmViewOnUi(alarm);
		    
		    createPendingIntent(alarmTime,id);
		    
		    libFile.setTotalAlarm(id);	    
		    libFile.setAlarmRepeatEveryDay(id, true);
		    libFile.setAlarmTime(id, alarmTime);
		    libFile.setAlarmActive(id, true);
		    
		    
	}
	
	private void createPendingIntent(long activeAlarmTime, int id) 
	{
		
		
		
		
		if( System.currentTimeMillis() > activeAlarmTime) // if curr time is greater than active alarm time and if user on it after offing it then skip current alarm and set it from next day.  
			{
			long deltaAlarmTime = System.currentTimeMillis() - activeAlarmTime;
			long  newAlarmTime=activeAlarmTime +(int) Math.floor(deltaAlarmTime /(24*60*60*1000))* 24*60*60*1000; //delta/time here gives us no of day between alarm created and current day 
			
			activeAlarmTime = newAlarmTime + 24*60*60*1000;
			
			}
		
	    Intent intent = new Intent(this, AlarmReceiver.class);
	   
	    intent.putExtra("ID_ALARM", id);
	    
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);//(this.getApplicationContext(), 234324243, intent, 0);
	    
	    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	    
	    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, activeAlarmTime, 24*60*60*1000, pendingIntent);
	   
	    if(alArms.get(id-1).isRepeadEveryDay()) showTost("Alarm set for Daily at " + sdfTime.format(new Date(activeAlarmTime)) + ". ");// Toast.makeText(this, "Alarm set for Daily at" + sdfTime.format(new Date(activeAlarmTime)) + ". ",Toast.LENGTH_LONG).show();
	    
	    //Toast.makeText(this, "Alarm set at " + sdf.format(new Date(activeAlarmTime)) + " ",Toast.LENGTH_LONG).show();
	}



	private boolean checkAlreadyExist(long alarmTime) 
	{
		for(int i=0; i < alArms.size() ; i++)
		{
			if(sdf.format(new Date(alArms.get(i).getTime())).equals(sdf.format(new Date(alarmTime))))
			{
				return true;
			}
			
		}
		
		return false;
	}



	private  void displayAlarmList()
	{
		for(int id=1; id<=libFile.getTotalAlarm(); id++)
		{
		
		
			Alarms alarm =new Alarms();
		    alarm.setId(id);
		    alarm.setRepeadEveryDay(libFile.isAlarmRepeatEveryDay(id));
		    alarm.setTime(libFile.getAlarmTime(id));
		    alarm.setActive(libFile.isAlarmActive(id));
		    
		    for(int i=Calendar.SUNDAY;i< Calendar.SATURDAY ;i++)
		    {
		    	if(libFile.getAlarmDay(id, i)==AppsConstant.SET_DAY)
		    		{
		    		alarm.setDay(i);
		    		}
		    
		    }
		    alArms.add(alarm);
		    
		    addAlarmViewOnUi(alarm);
		}
	}
	
	private void addAlarmViewOnUi(Alarms alarm)
	{
		
		View v=inflater.inflate(R.layout.single_item_list_alarm, null);	
		
		long when=  alarm.getTime();   
		
		Date dt=new Date(when);
		
		SimpleDateFormat s = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
		
		String format = s.format(dt);
		
		((TextView)v.findViewById(R.id.tvTime)).setText(format);
		
		((TextView)v.findViewById(R.id.tvTime)).setTypeface(RalewayRegular);
		
		((TextView)v.findViewById(R.id.tvRepeat)).setTypeface(RalewayRegular);
		
		v.findViewById(R.id.ibtnOnOffAlrm).setTag(alarm.getId());
		
		v.findViewById(R.id.ibtnOnOffAlrm).setSelected(alarm.isActive());
		
		v.findViewById(R.id.ibtnOnOffAlrm).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				int id=Integer.parseInt(view.getTag()+"");
				
				boolean active=!view.isSelected();
				
				view.setSelected(active);
				
				Alarms alarm = alArms.get(id-1);
				
				if(!active)
				{
					cancelAlarm(id);
				}
				else 
				{
					createPendingIntent(alarm.getTime(), id);
				
					boolean anyDaySelected = false;
					
					//check if all alarm is on for any day
					for(int i=Calendar.SUNDAY ; i <= Calendar.SATURDAY ;i++)
					{
						
						anyDaySelected = libFile.getAlarmDay(id, i) == AppsConstant.SET_DAY ? true : false;
						
						if(anyDaySelected) break;
						
					}
					
					if(!anyDaySelected) // repeat alarm everyday and select all days btn true to highlite(blue) 
					{
						libFile.setAlarmRepeatEveryDay(id, true);
						
						LinearLayout llListDays = (LinearLayout)llListAlarms.getChildAt(id-1).findViewById(R.id.llDayList);
						
						for(int i=0; i< llListDays.getChildCount() ;i++)
						{
							llListDays.getChildAt(i).setSelected(true);
						}
						
						alarm.setRepeadEveryDay(true);
					}
					
					showTost("Alarm set for Daily at " + sdfTime.format(new Date(alarm.getTime())) + ". ");
				}
				
				
				libFile.setAlarmActive(id, active);
				
				alarm.setActive(active);// object is at id-1 pos in arrayList
			}
		});
		
		LinearLayout llDayList=(LinearLayout)v.findViewById(R.id.llDayList);
		
		for(int i=0;i<llDayList.getChildCount();i++)
		{
			Button btnDayName = (Button) llDayList.getChildAt(i);
			
			btnDayName.setTag((alarm.getId()-1)+":"+i);
			
			btnDayName.setTypeface(RalewayMedium);
			
			if(i!=0) btnDayName.setSelected(alarm.isDaySelected(i-1));
			
			else  llDayList.getChildAt(0).setSelected(alarm.isRepeadEveryDay());
			
			llDayList.getChildAt(i).setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View view) 
				{
					int indexOnList=Integer.parseInt(view.getTag().toString().split(":")[0]);
					
					int day=Integer.parseInt(view.getTag().toString().split(":")[1]);
					
					manageAlarmDay(alArms.get(indexOnList) , day, (Button)view);
				}
			});
			
		}
		
		
		llListAlarms.addView(v);
	}
	
	protected void manageAlarmDay(Alarms alarm, int day, Button view)
	{
		String days[]={"Daily","Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
		
		boolean selected=!view.isSelected();
		
		if(day!=0)view.setSelected(selected);
		
		LinearLayout llParent=((LinearLayout)view.getParent());
		
		int alarmId=alarm.getId();
		
		switch (day)
		{
		case 0: // for daily
			
			if(selected) 
				{
					view.setSelected(true);
					
					alarm.setRepeadEveryDay(true);
					
					libFile.setAlarmRepeatEveryDay(alarmId, true);
					
					for(int i=0; i < llParent.getChildCount() ;i++)
					{
						llParent.getChildAt(i).setSelected(true);
					}
					
					showTost("Alarm set for Repeat Daily.");//Toast.makeText(getApplicationContext(), "Alarm set for Repeat Daily.", Toast.LENGTH_LONG).show();
				}
			
			
			break;

		default: // for particular day
			
			if(selected) 
				{
				    //	set day for alarm time
				
					alarm.setDay(day);
			
					libFile.setAlarmDay(alarmId, day);
					
					ImageButton ibAlarmOnOff = (ImageButton) llListAlarms.getChildAt(alarmId-1).findViewById(R.id.ibtnOnOffAlrm);
					
					if(!ibAlarmOnOff.isSelected()) 
					{
						ibAlarmOnOff.setSelected(true);
						
						alarm.setActive(true);
						
						libFile.setAlarmActive(alarmId, true);
						
						createPendingIntent(alarm.getTime(), alarmId);
						//if(checkAlreadyExist(alarm.getTime()))
					}
					
					//checkAllSelected(llParent);
					
					boolean allDaysSelected = true;
					
					//set daily btn selected if all days selected
					for(int i=1;i< llParent.getChildCount() ;i++)
					{
						allDaysSelected = llParent.getChildAt(i).isSelected();
						
						if(!allDaysSelected) break;
					}
					
					if(allDaysSelected)
						{
						llParent.getChildAt(0).setSelected(true);
						
						showTost("Alarm set for Repeat Daily.");
						
						}
					else
					{
						showTost("Alarm set for Repeat On ."+days[day]);
						
					}
					alarm.setRepeadEveryDay(allDaysSelected);
					
					libFile.setAlarmRepeatEveryDay(alarmId, allDaysSelected);
					
					
					
				}
			else 
				{
					
				
					//remove day for alarm time and set daily repetition false
					
					alarm.removeDay(day);
				
					libFile.removeAlarmDay(alarmId, day);
					
					alarm.setRepeadEveryDay(false);
					
					libFile.setAlarmRepeatEveryDay(alarmId, false);
					
					llParent.getChildAt(0).setSelected(false);
					

					// if all days are disselected then turn off alarm
					boolean anyDaysSelected = false;
					
					for(int i=1;i< llParent.getChildCount() ;i++)
					{
						anyDaysSelected = llParent.getChildAt(i).isSelected();
						
						if(anyDaysSelected) break;
					}
					
					if(!anyDaysSelected)
					{
						alarm.setActive(false);
						
						cancelAlarm(alarmId);
						
						libFile.setAlarmActive(alarmId, false);
						
						llListAlarms.getChildAt(alarmId-1).findViewById(R.id.ibtnOnOffAlrm).setSelected(false);
						
						showTost("Alarm off.");
					}
					else
					{
					showTost("Alarm removed for "+days[day]);
					}
					
				}
			
			break;
		}
		
		
	}



	private void updateSelectedView(int currentSelectedChildIndexOnParent)
	{
		if(mapPrevSelectedIndex.get(selectedCircle) != currentSelectedChildIndexOnParent)
			{
			
			rlViewHolder.getChildAt(mapPrevSelectedIndex.get(selectedCircle)).setSelected(false);
		
			((TextView) rlViewHolder.getChildAt(mapPrevSelectedIndex.get(selectedCircle))).setTextColor(getResources().getColor(R.color.txt_color_view_not_selected));
			
			if(mapPrevSelectedIndex.get(selectedCircle) != currentSelectedChildIndexOnParent)rlViewHolder.getChildAt(currentSelectedChildIndexOnParent).setSelected(true);
			
			mapPrevSelectedIndex.put(selectedCircle, currentSelectedChildIndexOnParent);
			}
		
		
	}

	private void cancelAlarm(int id)
	{
		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);//(this.getApplicationContext(), 234324243, intent, 0);
	    
	    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	    alarmManager.cancel(pendingIntent);
	    pendingIntent.cancel();
	}

	protected void changeTextColorOnHover(int currentHoveredChildIndexOnParent) 
	{
		
		TextView tvPrevHovered=((TextView)rlViewHolder.getChildAt(mapViewHoveredIndex.get(selectedCircle)));
		
		if(!tvPrevHovered.isSelected())(tvPrevHovered).setTextColor(getResources().getColor(R.color.txt_color_view_not_selected));//"#fffd4f06"));
		
		 mapViewHoveredIndex.put(selectedCircle,currentHoveredChildIndexOnParent);
		
		((TextView)rlViewHolder.getChildAt(currentHoveredChildIndexOnParent)).setTextColor(getResources().getColor(R.color.txt_color_view_selected));
		
		updateTimeLabel();
	}



	private void updateTimeLabel() 
	{
		String hr=((TextView)rlViewHolder.getChildAt(mapViewHoveredIndex.get(1))).getText()+"";
		
		String min=((TextView)rlViewHolder.getChildAt(mapViewHoveredIndex.get(2))).getText()+"";
		
		int hour=Integer.parseInt(hr);
		int minute=Integer.parseInt(min);
		
		String ampm = "";
		
		if(hour>=12 && hour < 24) ampm=" PM";
		else if(hour== 24 || hour <12) ampm = " AM";
		
		hour=hour%12;
		if(hour==0 ) hour=12;
		tvTimeOfViewHovered.setText(String.format("%02d", hour % 24)+":"+String.format("%02d",minute % 60) + ampm);
		
		
	}



	protected int getcurrSelectedViewIndexOnParent(double thetaRadianCurr) 
	{
		//selectedChildIndex = current index + number of childs in pi/2 ;//because we have rotated circle to -pi/2 for proper arrangement 
		int selectedChildIndexInCircle=(int)( Math.round((thetaRadianCurr/selectedCircleDeltaThetaChilds) + ((Math.PI/2)/selectedCircleDeltaThetaChilds))) % mapCircleChildCounts.get(selectedCircle);
		
		setLocationOnScreen(selectedCircleRadious, highliterView ,  thetaRadianCurr);
		
		int currentSelectedChildIndexOnParent = mapCircleChildCounts.get(selectedCircle-1)==null ? selectedChildIndexInCircle : mapCircleChildCounts.get(selectedCircle-1) +selectedChildIndexInCircle;
		
		//Log.v("Tag", " nearestIndex "+selectedChildIndexInCircle+" inParent "+ currentSelectedChildIndexOnParent);
		
		return currentSelectedChildIndexOnParent;
	}



	//we have rotated circle by -pi/2 for correct position of numbers.so we will need to find correct focused posi
	//tion by adding +pi/2
	protected double getNearestPosition(	double thetaRadianCurr) 
	{
		int nearestIndex=0;
		
		thetaRadianCurr=thetaRadianCurr+Math.PI/2; //leg theta to its orignal position bt substracting -pi/2
		
		
		double mod=thetaRadianCurr % selectedCircleDeltaThetaChilds;
		
		nearestIndex=(int) Math.abs(thetaRadianCurr / selectedCircleDeltaThetaChilds); 
		
		if(Math.abs(mod) > selectedCircleDeltaThetaChilds/2 ) nearestIndex=nearestIndex+1; //if thetaRad falls between two childs then adjust it
		
		return nearestIndex*selectedCircleDeltaThetaChilds-Math.PI/2;
	}



	private double getDeltaBetnChilds(int nthCircle)
	{
		
		return ((360/mapCircleChildCounts.get(nthCircle))*Math.PI)/180;
	}
	
	private void addChildViewDynamically(int radious,int childCount) 
	{
		
		double deltaThetaRadian=((360/childCount)*Math.PI)/180;
		
		mapCircleChildCounts.put(++totalNoOfCircle, childCount);
		mapCircleRadious.put(totalNoOfCircle, radious);
		mapViewHoveredIndex.put(totalNoOfCircle, 0);
		mapPrevSelectedIndex.put(totalNoOfCircle, 0);
		//deltaThetaRadian=deltaThetaRadian-90;
		for(int i=0;i<childCount;i++)
		{
			View v=inflater.inflate(R.layout.single_view_clock_child, null);

			((TextView)v).setText(i+"");
			if(i==0)
			{
				((TextView)v).setText(childCount+"");
			}
			((TextView)v).setTextSize(10);
			((TextView)v).setTypeface(RalewayExBold);
			rlViewHolder.addView(v);
			
			setLocationOnScreen(radious,v,i*deltaThetaRadian-Math.PI/2);
			
			
			
		}



	}


	public void setLocationOnScreen(int radious, View v, double angleInRadiantheta)
	{	

		final PointF pointOnCircle = new PointF();
		pointOnCircle.x = (float) (centerX + ((float)(radious*(Math.cos(angleInRadiantheta)))));
		pointOnCircle.y = (float) (centerY + ((float)(radious*(Math.sin(angleInRadiantheta)))));
		v.setTranslationX(pointOnCircle.x);
		v.setTranslationY(pointOnCircle.y);


	} 
	protected void showLongTost(String msg ) 
	{
		 
		Toast toast = new Toast(getApplicationContext());  
        toast.setDuration(Toast.LENGTH_LONG);  
        ((TextView)toastLayout.findViewById(R.id.tvToast)).setText(msg);
        ((TextView)toastLayout.findViewById(R.id.tvToast)).setTypeface(RalewaySemibold);
        toast.setView(toastLayout);//setting the view of custom toast layout  
        toast.show();  
	}
	@Override
	protected void onPause() {
		showLongTost("Created by Swapnil CHaudhari :\nhttps://github.com/SwapnilChaudhari");
		super.onPause();
	}
	@Override
	protected void onDestroy() 
	{
		showLongTost("Created by Swapnil CHaudhari :\nhttps://github.com/SwapnilChaudhari");
		super.onDestroy();
	}

	
}
