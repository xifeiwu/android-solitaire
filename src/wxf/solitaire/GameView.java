package wxf.solitaire;

import java.util.Calendar;
import java.util.Random;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

public class GameView extends SurfaceView implements Callback, Runnable{
	private SurfaceHolder sfh;
	private Canvas canvas;
	private Paint paint;
	private int[] randomCard;
	private int screenW, screenH;
	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setTextSize(20);
		//得到用到了图片资源
		getBitmaps();	
		//定位各个容器的坐标
		screenW = SOLITAIREActivity.screenW;
		screenH = SOLITAIREActivity.screenH;
		
		timeStrX = UNTURNED_CARDS_CAP;
		timeStrY = (int) (screenH - UNTURNED_CARDS_CAP);

		//开始新的游戏
		newGame();
	}
	public void newGame(){
		initContainer();
		randomCards();
		gameState = ONPLAYING;
		secs = 0;
		mins = 0;
		hours = 0;
		timeStr = "";
	}

	private Container fromCtainer = null, toCtainer = null;
	private float eventX, eventY;
	private float distanceX, distanceY, oldX, oldY;
	private int eventAction;
	private byte ti;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		eventX = event.getX();
		eventY = event.getY();
		eventAction = event.getAction();
		switch (eventAction) {
		case MotionEvent.ACTION_DOWN:
			fromCtainer = null;
			// stocks  R
			if (stocks.rect.contains(eventX, eventY)) {
				if (stocks.onTouchDown(eventY)) {
					fromCtainer = stocks;
					oldX = eventX;
					oldY = eventY;
				}
			}
			// foundation
			for (ti = 0; ti < 7; ti++) {
				if (fdations[ti].rect.contains(eventX, eventY)) {
					if (fdations[ti].onTouchDown(eventY)) {
						fromCtainer = fdations[ti];
						oldX = eventX;
						oldY = eventY;
					}
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (fromCtainer != null) {
				distanceX = eventX - oldX;
				distanceY = eventY - oldY;
				fromCtainer.dragCard(distanceX, distanceY);
				oldX = eventX;
				oldY = eventY;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (stocks.outrect.contains(eventX, eventY)) {
				if (stocks.inrectL.contains(eventX, eventY)) {
					if((fromCtainer == null)){
						stocks.moveCardFromLToR();
					}
				}
				if (stocks.rect.contains(eventX, eventY)) {
					if (stocks.cardsL.size() == 0) {
						stocks.moveCardFromRToL();
					}
				}
			}

			toCtainer = null;
			// foundation
			for (ti = 0; ti < 7; ti++) {
				if (fdations[ti].rect.contains(eventX, eventY)) {
					toCtainer = fdations[ti];
				}
			}
			// deck
			for (ti = 0; ti < 4; ti++) {
				if (decks[ti].rect.contains(eventX, eventY)) {
					toCtainer = decks[ti];//
				}
			}

			if (fromCtainer != null) {
				if ((toCtainer != null) && (fromCtainer != toCtainer)) {
					if (!fromCtainer.moveCardsTo(toCtainer)) {
						fromCtainer.resetCards();
					}
				} else {
					fromCtainer.resetCards();
				}
			}

			fromCtainer = null;
			toCtainer = null;
			break;
		}
		if (isGameOver()) {
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minutes = calendar.get(Calendar.MINUTE);
			String fileName = year + "年" + (month + 1) + "月" + day + "日" + hour
					+ "点" + minutes + "分";
			int timeused = (hours * 3600 + mins * 60 + secs);
			int rank = SOLITAIREActivity.instance.setScore(timeused, fileName);

			gameState = GAMEOVER;
			String dialogMsg = "共耗时：" + timeused + "秒，成绩榜排名为：第" + rank + "名，去看看成绩榜？";
			Builder builder = new Builder(SOLITAIREActivity.instance);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setTitle("您赢了！");
			builder.setMessage(dialogMsg);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							SOLITAIREActivity.instance.showScoreView();
						}

					});
			builder.setNegativeButton("再来一局",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							newGame();
							// randomCards();
						}
					});
			builder.show();
		}
		return true;
	}
	//foundation:up, move, down
	//deck:up
	//stock, L:down, R:down, move

	private int di;
	private void myDraw(){
		canvas = sfh.lockCanvas();
		canvas.drawBitmap(gamebg, 0, 0, paint);
		paint.setColor(Color.BLACK);
		for(di = 0; di < 4; di++){
			decks[di].draw(canvas, paint);
		}
		for(di = 0; di < 7; di++){
			fdations[di].draw(canvas, paint);
		}	
		stocks.draw(canvas, paint);
		if(fromCtainer != null){
			fromCtainer.draw(canvas, paint);
		}
		paint.setColor(Color.RED);
		canvas.drawText(timeStr, timeStrX, timeStrY, paint);
		sfh.unlockCanvasAndPost(canvas);
	}
	private long timeCnt;
	private int secs, mins, hours;
	private String timeStr = "";
	private int timeStrX, timeStrY;
	private final int ONPLAYING = 1, GAMEOVER = 2;
	private int gameState;
	private void logic() {
		if (gameState == ONPLAYING) {
			timeCnt++;
			if((timeCnt % 20) == 0){
				timeStr = "";
				secs++;
				if (secs > 59) {
					mins++;
					secs %= 60;
				}
				if (mins > 59) {
					hours++;
					mins %= 60;
				}
				if (hours > 0) {
					timeStr = hours + "小时";
				}
				if (mins > 0) {
					timeStr = timeStr + mins + "分";
				}
				timeStr = timeStr + secs + "秒";
			}
		}
	}
	
	private int sci;
	private StockControl stocks = null;
	private DeckControl[] decks = null;
	private Foundation[] fdations = null;
	public static int UNTURNED_CARDS_CAP = 15, TURNED_CARDS_CAP = 25;//纸牌之间的距离
	public static int CARDS_TOP_CAP = 13, CARDS_INTER_CAP = 20;//纸牌容器之间的距离
	public static int CARD_WIDTH , CARD_HEIGHT ;	
	public static int CARD_ROUNDING = 8;
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		flag = true;
		th = new Thread(this);
		th.start();
		
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
	private final long MILLISPERSECOND = 50;
	public void run() {
		// TODO Auto-generated method stub
		while(flag){
			start = System.currentTimeMillis();
			logic();
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
	private int igo;
	private boolean isGameOver(){
		if((stocks.cardsL.size() != 0) || (stocks.cards.size() != 0)){
			return false;
		}
		for(igo = 0; igo < 7; igo++){
			if(fdations[igo].cards.size() != 0){
				return false;
			}
		}
		return true;
	}
	private void initContainer(){
		int LEFT = (screenW - (CARD_WIDTH + CARDS_INTER_CAP) * 7) / 2;
		int startX, startY;
//		if(fdations == null){
			fdations = new Foundation[7];
			startX = LEFT;
			startY = CARDS_TOP_CAP + CARD_HEIGHT + CARDS_INTER_CAP;
			for(sci = 0; sci < 7; sci++){
				fdations[sci] = new Foundation(startX + (CARD_WIDTH + CARDS_INTER_CAP) * sci, startY);
			}
//		}
//		if(decks == null){
			decks = new DeckControl[4];
			startX = LEFT + (CARD_WIDTH + CARDS_INTER_CAP) * 3;
			startY = CARDS_TOP_CAP;
			for(sci = 0; sci < 4; sci++){
				decks[sci] = new DeckControl(startX + (CARD_WIDTH + CARDS_INTER_CAP) * sci, startY);
			}
//		}
//		if(stocks == null){
			startX = LEFT - 5;
			startY = CARDS_TOP_CAP - 5;
			stocks = new StockControl(startX, startY);			
//		}
	}
	private void randomCards(){
		randomCard = new int[13 * 4];
		Random rand = new Random();
		int rc, ri, rj, rk, rt;
		for(rc = 0; rc < 13 * 4; rc ++){
			randomCard[rc] = rc;
		}
		for(rc = 0; rc < 13 * 4 * 13 * 4; rc ++){
			ri = rand.nextInt(13 * 4);
			rj = rand.nextInt(13 * 4);
			rt = randomCard[ri];
			randomCard[ri] = randomCard[rj];
			randomCard[rj] = rt;
		}
		rt = 0;
		for(rc = 0; rc < 7; rc++){
			for(rk = 0; rk < rc + 1; rk++){
				ri = randomCard[rt] / 13;
				rj = randomCard[rt] % 13;
				if(rk == rc){//最后一个是正面向上的；
					fdations[rc].putCard(ri, rj, cardBmps[ri][rj], true);
				}else{//其它是反面向上的。
					fdations[rc].putCard(ri, rj, cardBmps[ri][rj], false);
				}
				rt++;
			}
		}
		while(rt < 13 * 4){
			ri = randomCard[rt] / 13;
			rj = randomCard[rt] % 13;				
			stocks.putCard(ri, rj, cardBmps[ri][rj], false);
			rt++;
		}
	}
	private Bitmap gamebg;
	public static Bitmap cardBack;
	private Bitmap[][] cardBmps;	
//	EHearts = 0, EDiamond = 1, ESpades = 2, EClub = 3;
//  EHearts,    // Hertta红桃
//  EDiamond,   // Ruutu方块
//  ESpades,    // Pata黑桃
//  EClub       // Risti梅花
	private void getBitmaps(){		
		cardBmps = new Bitmap[4][13];
		Resources res = this.getResources();
		gamebg = BitmapFactory.decodeResource(res, R.drawable.gamebg);
		cardBack = BitmapFactory.decodeResource(res, R.drawable.back);
		cardBmps[Card.EHearts][0] = BitmapFactory.decodeResource(res, R.drawable.ha);
		cardBmps[Card.EHearts][1] = BitmapFactory.decodeResource(res, R.drawable.h2);
		cardBmps[Card.EHearts][2] = BitmapFactory.decodeResource(res, R.drawable.h3);
		cardBmps[Card.EHearts][3] = BitmapFactory.decodeResource(res, R.drawable.h4);
		cardBmps[Card.EHearts][4] = BitmapFactory.decodeResource(res, R.drawable.h5);
		cardBmps[Card.EHearts][5] = BitmapFactory.decodeResource(res, R.drawable.h6);
		cardBmps[Card.EHearts][6] = BitmapFactory.decodeResource(res, R.drawable.h7);
		cardBmps[Card.EHearts][7] = BitmapFactory.decodeResource(res, R.drawable.h8);
		cardBmps[Card.EHearts][8] = BitmapFactory.decodeResource(res, R.drawable.h9);
		cardBmps[Card.EHearts][9] = BitmapFactory.decodeResource(res, R.drawable.h10);
		cardBmps[Card.EHearts][10] = BitmapFactory.decodeResource(res, R.drawable.hj);
		cardBmps[Card.EHearts][11] = BitmapFactory.decodeResource(res, R.drawable.hq);
		cardBmps[Card.EHearts][12] = BitmapFactory.decodeResource(res, R.drawable.hk);

		cardBmps[Card.EDiamond][0] = BitmapFactory.decodeResource(res, R.drawable.da);
		cardBmps[Card.EDiamond][1] = BitmapFactory.decodeResource(res, R.drawable.d2);
		cardBmps[Card.EDiamond][2] = BitmapFactory.decodeResource(res, R.drawable.d3);
		cardBmps[Card.EDiamond][3] = BitmapFactory.decodeResource(res, R.drawable.d4);
		cardBmps[Card.EDiamond][4] = BitmapFactory.decodeResource(res, R.drawable.d5);
		cardBmps[Card.EDiamond][5] = BitmapFactory.decodeResource(res, R.drawable.d6);
		cardBmps[Card.EDiamond][6] = BitmapFactory.decodeResource(res, R.drawable.d7);
		cardBmps[Card.EDiamond][7] = BitmapFactory.decodeResource(res, R.drawable.d8);
		cardBmps[Card.EDiamond][8] = BitmapFactory.decodeResource(res, R.drawable.d9);
		cardBmps[Card.EDiamond][9] = BitmapFactory.decodeResource(res, R.drawable.d10);
		cardBmps[Card.EDiamond][10] = BitmapFactory.decodeResource(res, R.drawable.dj);
		cardBmps[Card.EDiamond][11] = BitmapFactory.decodeResource(res, R.drawable.dq);
		cardBmps[Card.EDiamond][12] = BitmapFactory.decodeResource(res, R.drawable.dk);

		cardBmps[Card.ESpades][0] = BitmapFactory.decodeResource(res, R.drawable.sa);
		cardBmps[Card.ESpades][1] = BitmapFactory.decodeResource(res, R.drawable.s2);
		cardBmps[Card.ESpades][2] = BitmapFactory.decodeResource(res, R.drawable.s3);
		cardBmps[Card.ESpades][3] = BitmapFactory.decodeResource(res, R.drawable.s4);
		cardBmps[Card.ESpades][4] = BitmapFactory.decodeResource(res, R.drawable.s5);
		cardBmps[Card.ESpades][5] = BitmapFactory.decodeResource(res, R.drawable.s6);
		cardBmps[Card.ESpades][6] = BitmapFactory.decodeResource(res, R.drawable.s7);
		cardBmps[Card.ESpades][7] = BitmapFactory.decodeResource(res, R.drawable.s8);
		cardBmps[Card.ESpades][8] = BitmapFactory.decodeResource(res, R.drawable.s9);
		cardBmps[Card.ESpades][9] = BitmapFactory.decodeResource(res, R.drawable.s10);
		cardBmps[Card.ESpades][10] = BitmapFactory.decodeResource(res, R.drawable.sj);
		cardBmps[Card.ESpades][11] = BitmapFactory.decodeResource(res, R.drawable.sq);
		cardBmps[Card.ESpades][12] = BitmapFactory.decodeResource(res, R.drawable.sk);

		cardBmps[Card.EClub][0] = BitmapFactory.decodeResource(res, R.drawable.ca);
		cardBmps[Card.EClub][1] = BitmapFactory.decodeResource(res, R.drawable.c2);
		cardBmps[Card.EClub][2] = BitmapFactory.decodeResource(res, R.drawable.c3);
		cardBmps[Card.EClub][3] = BitmapFactory.decodeResource(res, R.drawable.c4);
		cardBmps[Card.EClub][4] = BitmapFactory.decodeResource(res, R.drawable.c5);
		cardBmps[Card.EClub][5] = BitmapFactory.decodeResource(res, R.drawable.c6);
		cardBmps[Card.EClub][6] = BitmapFactory.decodeResource(res, R.drawable.c7);
		cardBmps[Card.EClub][7] = BitmapFactory.decodeResource(res, R.drawable.c8);
		cardBmps[Card.EClub][8] = BitmapFactory.decodeResource(res, R.drawable.c9);
		cardBmps[Card.EClub][9] = BitmapFactory.decodeResource(res, R.drawable.c10);
		cardBmps[Card.EClub][10] = BitmapFactory.decodeResource(res, R.drawable.cj);
		cardBmps[Card.EClub][11] = BitmapFactory.decodeResource(res, R.drawable.cq);
		cardBmps[Card.EClub][12] = BitmapFactory.decodeResource(res, R.drawable.ck);
		
		CARD_WIDTH = cardBmps[Card.EClub][12].getWidth();
		CARD_HEIGHT = cardBmps[Card.EClub][12].getHeight();
	}

}
