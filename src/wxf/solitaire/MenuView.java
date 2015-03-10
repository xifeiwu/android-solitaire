package wxf.solitaire;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MenuView extends SurfaceView implements Callback, Runnable{
	private SurfaceHolder sfh;
	private Canvas canvas;
	private Paint paint;
	private int screenW, screenH;
	
	private Bitmap menubg;
	private Bitmap[] btnBmp;	
	private int btnPos[][], btnW, btnH;
//	private Bitmap start, start_pressed, score, score_pressed, about, 
//	about_pressed, exit, exit_pressed;
	public MenuView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		
		Resources res = this.getResources();
		btnBmp = new Bitmap[8];
		btnPos = new int[4][2];
		menubg = BitmapFactory.decodeResource(res, R.drawable.menubg);
		btnBmp[0] = BitmapFactory.decodeResource(res, R.drawable.start);
		btnBmp[1] = BitmapFactory.decodeResource(res, R.drawable.score);
		btnBmp[2] = BitmapFactory.decodeResource(res, R.drawable.about);
		btnBmp[3] = BitmapFactory.decodeResource(res, R.drawable.exit);
		btnBmp[4] = BitmapFactory.decodeResource(res, R.drawable.start_pressed);
		btnBmp[5] = BitmapFactory.decodeResource(res, R.drawable.score_pressed);
		btnBmp[6] = BitmapFactory.decodeResource(res, R.drawable.about_pressed);
		btnBmp[7] = BitmapFactory.decodeResource(res, R.drawable.exit_pressed);
		btnW = btnBmp[0].getWidth();
		btnH = btnBmp[0].getHeight();		
	}
	private final byte NONE = -1, START = 0, SCORE = 1, ABOUT = 2, EXIT = 3;
	private byte selectedKey = -1;
	private float eventX, eventY;
	private int eventAction;
	private byte ti;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		eventX = event.getX();
		eventY = event.getY();
		eventAction = event.getAction();
		switch(eventAction){
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			selectedKey = NONE;
			for(ti = 0; ti < btnPos.length; ti++){
				if((eventX > btnPos[ti][0]) && (eventX < (btnPos[ti][0] + btnW)) 
				&& (eventY > btnPos[ti][1]) && (eventY < (btnPos[ti][1] + btnH))){
					selectedKey = ti;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			for(ti = 0; ti < btnPos.length; ti++){
				if((eventX > btnPos[ti][0]) && (eventX < (btnPos[ti][0] + btnW)) 
				&& (eventY > btnPos[ti][1]) && (eventY < (btnPos[ti][1] + btnH))){
					if(selectedKey == ti){
						switch(selectedKey){
						case START:
							SOLITAIREActivity.instance.showGameView();
							break;
						case SCORE:
							SOLITAIREActivity.instance.showScoreView();
							break;
						case ABOUT:
							SOLITAIREActivity.instance.showAboutView();
							break;
						case EXIT:
							SOLITAIREActivity.instance.onDestroy();
							System.exit(0);
							break;							
						}
					}
					break;
				}
			}
			selectedKey = NONE;
			break;
		}
		return true;
	}
	private int di;
	private void myDraw(){
		canvas = sfh.lockCanvas();
		canvas.drawBitmap(menubg, 0, 0, paint);
		for(di = 0; di < btnPos.length; di++){
			if(selectedKey == di){
				canvas.drawBitmap(btnBmp[di + btnPos.length], btnPos[di][0], btnPos[di][1], paint);
			}else{
				canvas.drawBitmap(btnBmp[di], btnPos[di][0], btnPos[di][1], paint);
			}
		}
		sfh.unlockCanvasAndPost(canvas);
	}
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		screenW = this.getWidth();
		screenH = this.getHeight();
		SOLITAIREActivity.screenW = screenW;
		SOLITAIREActivity.screenH = screenH;
		
		btnPos[0][0] = screenW / 2;
		btnPos[0][1] = screenH / 2;
		btnPos[1][0] = screenW * 3 / 4;
		btnPos[1][1] = screenH / 2;
		btnPos[2][0] = screenW / 2;
		btnPos[2][1] = screenH * 3 / 4;
		btnPos[3][0] = screenW * 3 / 4;
		btnPos[3][1] = screenH * 3 / 4;
		
		flag = true;
		th = new Thread(this);
		th.start();
		this.myDraw();
		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		flag = false;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	private Thread th;
	private boolean flag;
	private long start, during;
	private final long MILLISPERSECOND = 200;
	public void run() {
		// TODO Auto-generated method stub
		while(flag){
			start = System.currentTimeMillis();
			myDraw();
			during = System.currentTimeMillis() - start;
			if(during < MILLISPERSECOND){
				try {
					Thread.sleep(MILLISPERSECOND - during);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
