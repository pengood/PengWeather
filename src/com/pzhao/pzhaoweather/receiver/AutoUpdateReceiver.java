package com.pzhao.pzhaoweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pzhao.pzhaoweather.service.AudoUpdateService;

public class AutoUpdateReceiver extends BroadcastReceiver {
	private static int count=0;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		count++;
		Log.d("TAG", "Time: "+count);
		Intent intent2=new Intent(context,AudoUpdateService.class);
		context.startService(intent2);
	}

}
