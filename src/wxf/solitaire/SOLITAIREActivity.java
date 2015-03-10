package wxf.solitaire;

import java.util.Calendar;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SOLITAIREActivity extends Activity {
    /** Called when the activity is first created. */
	public static SOLITAIREActivity instance;
	public static int screenW, screenH;
	private SharedPreferences sharedData;
	private Editor editor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        getSharedData();
		//����ȫ��
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.setContentView(R.layout.main);
		showMenuView();
    }	
    
    // �����
	public static final int MENUVIEW = 1, SETTINGVIEW = 3, SCOREVIEW = 4,
			ABOUTVIEW = 5, GAMEVIEW = 2, CLIPBMPVIEW = 6;
	public int currentView;
	
    public void showMenuView(){
    	instance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	currentView = MENUVIEW;
    	setContentView(new MenuView(this));
    }
    private GameView gameView = null;
    public void showGameView(){
    	Calendar calendar = Calendar.getInstance();
    	int year = calendar.get(Calendar.YEAR);
    	if(year > 2016){
    		return;
    	}
    	instance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	currentView = GAMEVIEW;
    	if(gameView == null){
    		gameView = new GameView(this);
    	}else{
    		gameView.newGame();
    	}
    	setContentView(gameView);    	
    }

	public int numbersInScoreView;
	private  Vector<ScoreItem> scoreItems;
	private TextView tv;
	private ListView lv;
    public void showScoreView(){
    	instance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	currentView = SCOREVIEW;       
    	setContentView(R.layout.score);
    	tv = (TextView) findViewById(R.id.tv_scoretitle);
    	lv = (ListView) findViewById(R.id.list);
		lv.setCacheColorHint(0);
		if (scoreItems.size() == 0) {
			tv.setText("����û�гɹ���Ŷ");
		} else {
			tv.setText("�ɼ����а�");
			lv.setAdapter(new ScoreAdapter(this, 0, scoreItems));
		}

    }
    private TextView about_content1, about_content2, about_content3;
    public void showAboutView(){
//    	Log.v("showAboutView", "come in");
    	instance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	currentView = ABOUTVIEW;       
    	setContentView(R.layout.about);
    	String content1 = "    ��ϷĿ�꣺��Ϸ��������Ͻ�Ϊ����Ӧ���������Ͻ�Ϊ��Ŀ��������" +
    			"�ײ�Ϊ��������������Ϸ��Ŀ���ǰ������򣨴� A��K������ͬ��ɫ��ֽ�Ʒ����ĸ���Ŀ��������";
    	String content2 = "    �淨����Ϸ�Ĺ�����windows�Դ�ֽ����Ϸ��ͬ��";
    	String content3 = "    ��ʾ���ڳɼ����汣����õ�ʮ���ɼ�������Ϸ���浥��MENU���ᵯ��ѡ��˵������¿�ʼ�򷵻����˵���" +
    			"Ϊ�˷�ֹ�����������ؼ���������Ϸ����������Ϸ���������˷��ؼ���";
    	about_content1 = (TextView) findViewById(R.id.about_content1);
    	about_content2 = (TextView) findViewById(R.id.about_content2);
    	about_content3 = (TextView) findViewById(R.id.about_content3);
    	about_content1.setText(content1);
    	about_content2.setText(content2);
    	about_content3.setText(content3);  	
//    	Log.v("showAboutView", "come out");
    }

	private String[] time_int = { "t1", "t2", "t3", "t4", "t5", "t6", "t7",
			"t8", "t9", "t10" };
	private String[] fn_str = { "fn1", "fn2", "fn3", "fn4", "fn5", "fn6",
			"fn7", "fn8", "fn9", "fn10" };
    private void getSharedData(){        
		sharedData = this.getSharedPreferences("SOLITAIRE",
				Context.MODE_PRIVATE);
		editor = sharedData.edit();
		numbersInScoreView = 0;
		int tmp;
		tmp = sharedData.getInt("vecsize", -1);
		if (tmp != -1) {
			numbersInScoreView = tmp;
		}
		int i, timeused;
		String time;
		scoreItems = new Vector<ScoreItem>();
		for (i = 0; i < numbersInScoreView; i++) {
			// ������ȡ��
			timeused = sharedData.getInt(time_int[i], Integer.MAX_VALUE);
			time = sharedData.getString(fn_str[i], "");
			// �������û�д��󣬼��뵽vec��
			if ((time != "") && (timeused != Integer.MAX_VALUE)) {
				ScoreItem item = new ScoreItem(timeused, time);
				scoreItems.add(item);
			}
		}
		// ����numbersInScoreView�Ĵ�С
		numbersInScoreView = scoreItems.size();    	
    }
    public int setScore(int timeint, String timestr) {
		ScoreItem item = new ScoreItem(timeint, timestr);
		int i, pos = -1, timeused, rank;
		if (numbersInScoreView == 0) {// ���û�д洢���ݣ�ֱ�Ӽ���vec
			scoreItems.add(item);
			rank = 1;
		} else {// ������ʷ����
				// ���µĳɼ����뵽�ʵ���λ��
			for (i = 0; i < numbersInScoreView; i++) {
				timeused = scoreItems.get(i).timeused;
				if (timeused > timeint) {
					pos = i;
					break;
				}
			}
			if (pos == -1) {
				scoreItems.add(item);
				rank = numbersInScoreView + 1;
			} else {
				scoreItems.add(pos, item);
				rank = pos + 1;
			}
		}
		numbersInScoreView++;
		if (numbersInScoreView > 10) {
			numbersInScoreView = 10;
		}
		return rank;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.option_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.item_goback: 
			showMenuView();
			break;
		case R.id.item_restart:
			if(gameView != null){
				gameView.newGame();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
//		Toast.makeText(this, "keyCode:" + keyCode, Toast.LENGTH_LONG).show();
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			switch (currentView) {
			case SCOREVIEW:
			case ABOUTVIEW:
				showMenuView();
				return true;
			case GAMEVIEW:
				Toast.makeText(instance, "�������˵��뵥��menu��", Toast.LENGTH_SHORT).show();
//			case MENUVIEW:
				return true;
			}
		}
		if (currentView == GAMEVIEW) {
			if (keyCode == KeyEvent.KEYCODE_MENU) {
				openOptionsMenu();
				return true;
			}
			if ((keyCode == KeyEvent.KEYCODE_HOME)
					|| (keyCode == KeyEvent.KEYCODE_SEARCH)) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
    
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if((currentView == GAMEVIEW) && ((keyCode == KeyEvent.KEYCODE_HOME)
//				|| (keyCode == KeyEvent.KEYCODE_SEARCH))){
//			return true;
//		}
//		return super.onKeyUp(keyCode, event);
//	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		int tmp;
		// �����Ҫͬ��vec�е���Ŀ
		numbersInScoreView = scoreItems.size();
		if (numbersInScoreView > 10) {
			numbersInScoreView = 10;
		}
		tmp = numbersInScoreView;
		editor.putInt("vecsize", tmp);
		for (int i = 0; i < numbersInScoreView; i++) {
			editor.putInt(time_int[i], scoreItems.get(i).timeused);
			editor.putString(fn_str[i], scoreItems.get(i).timeStr);
		}
		editor.commit();
		super.onDestroy();
	}
    
}