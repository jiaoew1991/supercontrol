package com.jiaoew.remotecontroler;

import com.jiaoew.remotecontroler.view.ChartView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.widget.LinearLayout;

public class TextActivity extends Activity {

	private LinearLayout container = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text);
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				container = (LinearLayout) findViewById(R.id.test_chart_container);
				ChartView mView = new ChartView(TextActivity.this);
				container.addView(mView);
				super.handleMessage(msg);
			}
			
		};
		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					Thread.sleep(2000);
					Message msg = handler.obtainMessage();
					handler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.text, menu);
		return true;
	}

}
