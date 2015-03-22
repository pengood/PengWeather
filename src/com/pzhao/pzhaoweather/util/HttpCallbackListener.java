package com.pzhao.pzhaoweather.util;

public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);
}
