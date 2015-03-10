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
		//设置全屏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.setContentView(R.layout.main);
		showMenuView();
    }	
    
    // 界面宏
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
			tv.setText("您还没有成功过哦");
		} else {
			tv.setText("成绩排行榜");
			lv.setAdapter(new ScoreAdapter(this, 0, scoreItems));
		}

    }
    private TextView about_content1, about_content2, about_content3;
    public void showAboutView(){
//    	Log.v("showAboutView", "come in");
    	instance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	currentView = ABOUTVIEW;       
    	setContentView(R.layout.about);
    	String content1 = "    游戏目标：游戏界面的左上角为“供应区”，右上角为“目标区”，" +
    			"底部为“叠加区”。游戏的目标是按照升序（从 A到K）将相同花色的纸牌放入四个“目标区”。";
    	String content2 = "    玩法：游戏的规则与windows自带纸牌游戏相同。";
    	String content3 = "    提示：在成绩界面保存最好的十个成绩；在游戏界面单击MENU键会弹出选择菜单：重新开始或返回主菜单；" +
    			"为了防止无意碰到返回键，导致游戏结束，在游戏界面屏蔽了返回键。";
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
			// 将数据取出
			timeused = sharedData.getInt(time_int[i], Integer.MAX_VALUE);
			time = sharedData.getString(fn_str[i], "");
			// 如果数据没有错误，加入到vec中
			if ((time != "") && (timeused != Integer.MAX_VALUE)) {
				ScoreItem item = new ScoreItem(timeused, time);
				scoreItems.add(item);
			}
		}
		// 重设numbersInScoreView的大小
		numbersInScoreView = scoreItems.size();    	
    }
    public int setScore(int timeint, String timestr) {
		ScoreItem item = new ScoreItem(timeint, timestr);
		int i, pos = -1, timeused, rank;
		if (numbersInScoreView == 0) {// 如果没有存储数据，直接加入vec
			scoreItems.add(item);
			rank = 1;
		} else {// 已有历史数据
				// 将新的成绩加入到适当的位置
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
				Toast.makeText(instance, "返回主菜单请单击menu键", Toast.LENGTH_SHORT).show();
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
		// 最后还是要同步vec中的数目
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