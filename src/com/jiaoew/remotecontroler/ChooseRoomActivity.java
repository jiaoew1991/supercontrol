package com.jiaoew.remotecontroler;

import org.json.JSONException;

import com.jiaoew.remotecontroler.model.RoomInfoModel;
import com.jiaoew.remotecontroler.util.HttpRequestHelper;
import com.jiaoew.remotecontroler.util.JsonParser;
import com.jiaoew.remotecontroler.view.PostListViewFragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseRoomActivity extends FragmentActivity implements ActionBar.TabListener{
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will display the three primary sections of the app, one at a
	 * time.
	 */
	ViewPager mViewPager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_room);

		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), this);

		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setText(mAppSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_choose_room, menu);
		return true;
	}
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		
	}
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

    	Context mContext;
    	
        public AppSectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }
        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new ChooseRoomFragment();
                case 1:
                	return new AllRoomFragment();
                default:
                	return null;
            }
        }
        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
        	switch (position) {
        	case 0:
        		return mContext.getResources().getString(R.string.choose_room_tab1_label);
        	case 1:
        		return mContext.getResources().getString(R.string.choose_room_tab2_label);
        	}
        	return "";
        }
    }
    public static class ChooseRoomFragment extends PostListViewFragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = super.onCreateView(inflater, container, savedInstanceState);
			mRoomListView = (ListView) rootView.findViewById(R.id.choose_room_select_list);
			mRoomListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					RoomInfoModel roomModel = mModelList.get(arg2);				
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable(ROOM_INFO, roomModel);
					intent.putExtras(bundle);
					intent.setClass(mContext, MainActivity.class);
					startActivity(intent);
				}
			});
			return rootView;
		}

		@Override
		protected void updateLocation(final Location location) {
			new Thread() {
	
				@Override
				public void run() {
					super.run();
					HttpRequestHelper request = new HttpRequestHelper("http://" + RemoteApp.SERVER_IP + "/location");
					String serverRst = request.doPostJSONResult(makePostRoomData(location));
					JsonParser parser = new JsonParser(mContext);
					try {
						mModelList = parser.getRoomInfo(serverRst);
					} catch (JSONException e) {
						mModelList = null;
						Log.e("Choose", "fetch error");
						e.printStackTrace();
					}
					Message msg = handler.obtainMessage();
					msg.arg1 = (mModelList == null) ? Messages.NETWORK_FAILED : Messages.NETWORK_SUCCESS;
					handler.sendMessage(msg);
				}
				
			}.start();
	    }
    }
    public static class AllRoomFragment extends PostListViewFragment {

    	@Override
    	protected void updateLocation(Location location) {
			new Thread() {
	
				@Override
				public void run() {
					super.run();
					HttpRequestHelper request = new HttpRequestHelper("http://" + RemoteApp.SERVER_IP + "/location");
					String serverRst = request.doGetJSONResult();
					JsonParser parser = new JsonParser(mContext);
					try {
						mModelList = parser.getRoomInfo(serverRst);
					} catch (JSONException e) {
						mModelList = null;
						Log.e("Choose", "fetch error");
						e.printStackTrace();
					}
					Message msg = handler.obtainMessage();
					msg.arg1 = (mModelList == null) ? Messages.NETWORK_FAILED : Messages.NETWORK_SUCCESS;
					handler.sendMessage(msg);
				}
				
			}.start();

    	}

    }
}
