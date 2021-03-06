package com.xhermes.android.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.xhermes.android.R;
import com.xhermes.android.dao.NoticeDao;
import com.xhermes.android.network.DataReceiver;
import com.xhermes.android.util.DateController;
import com.xhermes.android.util.OverallFragmentController;

public class MainActivity extends SherlockFragmentActivity {

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("onDestroy");
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("onResume");
		if("messageFragment".equals(bundle.getString("fragment"))){
			MessageFragment messFrag =new MessageFragment();
			messFrag.setArguments(bundle);
			FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
			transaction2.replace(R.id.fragment_container, messFrag);
			//transaction.addToBackStack(null);
			transaction2.commit();
			OverallFragmentController.addFragment("messageFragment", messFrag);
			bundle.remove("fragment");
			NotificationManager nm=(NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
			nm.cancel(DataReceiver.nid);
		}

	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		System.out.println("onStart");
		super.onStart();
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		HashMap map = OverallFragmentController.popFragment();
		if(map!=null){
			if(map.get("tag").equals("main")){
				if(OverallFragmentController.onMain){
					exit();
				}
				else{
					Fragment f = (Fragment) map.get("fragment");
					FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.fragment_container, f);
					transaction.commit();
				}
			}
			else{
				Fragment f = (Fragment) map.get("fragment");
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, f);
				transaction.commit();
			}
		}
	}

	public void refreshMailMenuItem(){
		//int random_test_num = (int) (Math.random()*11);
		NoticeDao ndao=new NoticeDao(this);
		int notRead=ndao.queryReadOrNot(terminalId, 0+"");
		MenuItem menuItem = (MenuItem) mailMenu.findItem(R.id.mail);
		int test_int_str[] = {0,1,2,3,4,5,6,7,8,9,10};
		int test_R_str[] = {R.drawable.mail_icon,
				R.drawable.mail_icon_1,
				R.drawable.mail_icon_2,
				R.drawable.mail_icon_3,
				R.drawable.mail_icon_4,
				R.drawable.mail_icon_5,
				R.drawable.mail_icon_6,
				R.drawable.mail_icon_7,
				R.drawable.mail_icon_8,
				R.drawable.mail_icon_9,
				R.drawable.mail_icon_n
		};
		for(int i=0;i<test_int_str.length;i++){
			if(test_int_str[i]==notRead){
				menuItem.setIcon(test_R_str[i]);
			}
			if(notRead>=10){
				menuItem.setIcon(test_R_str[10]);
			}
		}
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
		mailMenu=menu;
		refreshMailMenuItem();
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){  
		case android.R.id.home: 
			menu.toggle();
			break;
		case R.id.mail:
			Bundle arguments2 = new Bundle();
			arguments2.putString("terminalId", terminalId);
			MessageFragment mFragment = new MessageFragment();
			mFragment.setArguments(arguments2);
			OverallFragmentController.removeFragment("message");
			OverallFragmentController.addFragment("message", mFragment);
			FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
			transaction2.replace(R.id.fragment_container, mFragment,"message"); 
			transaction2.commit();
			break;
			
		}  
		return super.onOptionsItemSelected(item);  
	}


	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflator = getSupportMenuInflater();  
		inflator.inflate(R.menu.main, menu);  
		return true;
	}



	private int[] leftViewIcons;
	private String[] iconNames;
	private boolean slideOut = false;
	private SlidingMenu menu;
	Bundle bundle;
	String terminalId;
	private static final String TAG = MainActivity.class.getSimpleName(); 
	private long clickTime = 0; 
	private Handler mainActivityHandler;
	private Menu mailMenu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		menu = new SlidingMenu(this);
		setMenu();
		Intent intent = getIntent();
		bundle = intent.getBundleExtra("bundle");
		terminalId = bundle.getString("terminalId");
		DataReceiver.setEqid(terminalId);
		/*初始化应用日期*/
		DateController.init();
		DataReceiver.setAct(this);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.menu_icon);
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.backcolor));
		actionBar.setTitle("");

		MainViewFragment mFragment = new MainViewFragment();
		mFragment.setArguments(bundle);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, mFragment);
		//transaction.addToBackStack(null);
		transaction.commit();
		System.out.println("OverallFragmentController list size:"+OverallFragmentController.list.size());
		OverallFragmentController.removeAll();
		OverallFragmentController.addFragment("main", mFragment);

		mainActivityHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				refreshMailMenuItem();
			}
		};
		DataReceiver.setMainActivityHandler(mainActivityHandler);
		MessageFragment.setMainActivityHandler(mainActivityHandler);
	}

	private void exit() { 
		if ((System.currentTimeMillis() - clickTime) > 2000) { 
			Toast.makeText(getApplicationContext(), "再按一次后退键 退出程序", Toast.LENGTH_SHORT).show(); 
			clickTime = System.currentTimeMillis(); 
		} 
		else { 
			Log.e(TAG, "exit application"); 
			//this.finish(); 
			System.exit(0);
		} 
	} 



	//private void changeFragment
	private void setMenu() {
		View lv= getLayoutInflater().inflate(R.layout.leftlistview,null);

		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		if(screenWidth>600){
			menu.setBehindWidth(600);
		}
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		//menu.setMenu(R.layout.activity_main);
		menu.setMenu(lv);


		ListView list=(ListView) lv.findViewById(R.id.leftlistView);
		leftViewIcons=new int[]{
				R.drawable.home,
				R.drawable.carinfo,
				R.drawable.mail,
				R.drawable.records,
				R.drawable.travelinfo,
				R.drawable.report,
				R.drawable.setting

		};
		iconNames=new String[]{
				getString(R.string.home),
				getString(R.string.carinfo),
				getString(R.string.message),
				getString(R.string.records),
				getString(R.string.travelinfo),
				getString(R.string.report),
				getString(R.string.setting)

		};
		MyAdapter adapter =new MyAdapter(leftViewIcons,iconNames,MainActivity.this);
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch(arg2){

				case 0:
					int len=OverallFragmentController.list.size();
					HashMap map=OverallFragmentController.popFragment();
					if(map.get("tag").equals("main")){
						if(OverallFragmentController.onMain){
							break;
						}
						else{
							Fragment f = (Fragment) map.get("fragment");
							FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
							transaction.replace(R.id.fragment_container, f,"main");
							transaction.commit();
							break;
						}
					}
					else {
						for (int i=0;i<len;i++){
							map=OverallFragmentController.popFragment();
							Fragment mvFragment=(Fragment) map.get("fragment");
							if(map.get("tag").equals("main")){
								System.out.println("is main");
								FragmentTransaction transaction8 = getSupportFragmentManager().beginTransaction();
								transaction8.replace(R.id.fragment_container, mvFragment,"main");
								transaction8.commit();
								OverallFragmentController.removeAll();
								OverallFragmentController.addFragment("main", mvFragment);
								break;
							}
						}
					}
					break;

				case 1:
					Bundle arguments1 = new Bundle();
					arguments1.putString("terminalId", terminalId);
					VehicleDetailFragment vdFragment = new VehicleDetailFragment();
					vdFragment.setArguments(arguments1);
					OverallFragmentController.removeFragment("info");
					OverallFragmentController.addFragment("info", vdFragment);
					FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
					transaction1.replace(R.id.fragment_container, vdFragment,"info"); 
					transaction1.commit();
					break;
				case 2:
					Bundle arguments2 = new Bundle();
					arguments2.putString("terminalId", terminalId);
					MessageFragment mFragment = new MessageFragment();
					mFragment.setArguments(arguments2);
					OverallFragmentController.removeFragment("message");
					OverallFragmentController.addFragment("message", mFragment);
					FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
					transaction2.replace(R.id.fragment_container, mFragment,"message"); 
					transaction2.commit();
					break;
				case 3:
					break;
				case 4:

					Bundle arguments4 = new Bundle();
					arguments4.putString("terminalId", terminalId);
					TravelInfoFragment tFragment = new TravelInfoFragment();
					tFragment.setArguments(arguments4);
					OverallFragmentController.removeFragment("travelinfo");
					OverallFragmentController.addFragment("travelinfo", tFragment);
					FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
					transaction4.replace(R.id.fragment_container, tFragment,"travelinfo"); 
					transaction4.commit();
					break;
				case 5:
					Bundle arguments5 = new Bundle();
					arguments5.putString("terminalId", terminalId);
					DrivingMonthlyReportFragment dFragment = new DrivingMonthlyReportFragment();
					dFragment.setArguments(arguments5);
					OverallFragmentController.removeFragment("report");
					OverallFragmentController.addFragment("report", dFragment);
					FragmentTransaction transaction5 = getSupportFragmentManager().beginTransaction();
					transaction5.replace(R.id.fragment_container, dFragment,"travelinfo"); 
					transaction5.commit();
					break;
				case 6:
					SystemSetFragment ssf=new SystemSetFragment();
					ssf.setArguments(bundle);
					OverallFragmentController.removeFragment("systemset");
					OverallFragmentController.addFragment("systemset", ssf);
					FragmentTransaction transaction6 = getSupportFragmentManager().beginTransaction();
					transaction6.replace(R.id.fragment_container, ssf,"systemset"); 
					transaction6.commit();
					break;
				}
				menu.toggle();
			}

		});

	}

	class MyAdapter extends BaseAdapter{
		private int[] icons;
		private String[] names;
		private Activity ctx;
		private LayoutInflater inflater ; 

		public MyAdapter(int[] icons,String[] names,Activity context){
			ctx=context;
			this.icons=icons;
			this.names=names;
			inflater=ctx.getLayoutInflater();
		}
		@Override
		public int getCount() {
			return icons.length;
		}

		@Override
		public Object getItem(int arg0) {
			return	arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View tv=inflater.inflate(R.layout.listitemview, null);
			ImageView imgView=(ImageView) tv.findViewById(R.id.listimageView);
			TextView txtView= (TextView) tv.findViewById(R.id.listTextView);
			imgView.setImageResource( icons[position]);
			txtView.setText(names[position]);
			return tv;
		}

	}
	private ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>(
			10);

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		for (MyOnTouchListener listener : onTouchListeners) {
			listener.onTouch(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
		onTouchListeners.add(myOnTouchListener);
	}
	public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
		onTouchListeners.remove(myOnTouchListener) ;
	}
	public interface MyOnTouchListener {
		public boolean onTouch(MotionEvent ev);
	}
}
