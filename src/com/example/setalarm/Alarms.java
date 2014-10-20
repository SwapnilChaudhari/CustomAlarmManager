package com.example.setalarm;

public class Alarms
{
	private int id;
	private long time;
	private boolean repeadEveryDay;
	private boolean active;

	private int arrRepeatOnDays[] = new int[] { 0, 0, 0, 0, 0, 0, 0 };

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isRepeadEveryDay() {
		return repeadEveryDay;
	}

	public void setRepeadEveryDay(boolean repeadEveryDay) 
	{
		this.repeadEveryDay = repeadEveryDay;
		
		if(repeadEveryDay) 
		{
			for(int i=0; i< arrRepeatOnDays.length ;i++)
			{
				arrRepeatOnDays[i]=AppsConstant.SET_DAY;
			}
		}
	}

	public void setDay(int day)
	{
		arrRepeatOnDays[day-1]=AppsConstant.SET_DAY;
	}
	
	public boolean isDaySelected(int day)
	{
		
		return ( arrRepeatOnDays[day]==AppsConstant.SET_DAY ? true : false);
	}
	
	public void removeDay(int day)
	{
		arrRepeatOnDays[day-1]=AppsConstant.REMOVE_DAY;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
