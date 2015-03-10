package wxf.solitaire;

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class StockControl extends Container{

//	private int left, top, width, height;
	public RectF outrect, inrectL;//, rect;
	
	public Vector<Card> cardsL;//, cardsR;
	private int lastEle;
	
	public StockControl(int l, int t){
		left = l;
		top = t;
		width = GameView.CARD_WIDTH;
		height = GameView.CARD_HEIGHT;
		
		outrect = new RectF(left, top, left + 10 + 2 * GameView.CARD_WIDTH + GameView.CARDS_INTER_CAP - 1, 
				top + 10 + GameView.CARD_HEIGHT - 1);
		left += 5;
		top += 5;
		inrectL = new RectF(left, top, left + width - 1, top + height - 1);
		left += GameView.CARD_WIDTH + GameView.CARDS_INTER_CAP;
		rect = new RectF(left, top, left + width - 1, top + height - 1);
		
		cardsL = new Vector<Card>();
		cards = new Vector<Card>();
	}

	public void draw(Canvas canvas, Paint paint){
		canvas.drawRoundRect(inrectL, GameView.CARD_ROUNDING, GameView.CARD_ROUNDING, paint);
		canvas.drawRoundRect(rect, GameView.CARD_ROUNDING, GameView.CARD_ROUNDING, paint);
		canvas.drawRoundRect(outrect, GameView.CARD_ROUNDING, GameView.CARD_ROUNDING, paint);
		//左边的只绘制最后一个。
		lastEle = cardsL.size() - 1;
		if(lastEle >= 0){
			cardsL.elementAt(lastEle).draw(canvas, paint);
		}
		//右边绘制两个，因为拖拽的时候第二个会出现。
		lastEle = cards.size() - 2;
		if(lastEle >= 0){
			cards.elementAt(lastEle).draw(canvas, paint);
		}
		lastEle = cards.size() - 1;
		if(lastEle >= 0){
			cards.elementAt(lastEle).draw(canvas, paint);
		}
	}
	//初始化是将纸牌放到方框内，左边的都不能看见
	public void putCard(int kind, int num, Bitmap bmp, boolean isVisible){
		Card card = new Card(kind, num, inrectL.left, inrectL.top, bmp);
		card.setVisible(isVisible);
		cardsL.add(card);
		super.putCard(kind, num, bmp, isVisible);
	}

	public void dragCard(float x, float y) {
		// TODO Auto-generated method stub
		lastEle = cards.size() - 1;
		if(lastEle >= 0){
			Card card;
			card = cards.elementAt(lastEle);
			card.setPos(card.px + x, card.py + y);
		}
	}
	public boolean moveCardsTo(Container container){
		lastEle = cards.size() - 1;
		Card card = cards.elementAt(lastEle);
		if(container.isCardFit(card, 1)){
			container.putCard(card);
			cards.removeElementAt(lastEle);
			return true;
		}else{
			return false;
		}
	}
	public void resetCards(){
		lastEle = cards.size() - 1;
		if(lastEle >= 0){
			Card card = cards.elementAt(lastEle);
			card.setPos(rect.left, rect.top);
		}
//		Log.v("StockControl:", "resetCards");
	}
	
	public boolean onTouchDown(float y){
		lastEle = cards.size();
		if(lastEle > 0){// && (cardsL.size() != 0)
			return true;
		}else{
			return false;
		}
	}

	private int mi;
	//从左边的容器移到右边的容器，一次只移动一个。
	public void moveCardFromLToR(){
//		Log.v("StockControl", "Enter moveCardFromLToR");
		lastEle = cardsL.size() - 1;
		if(lastEle >= 0){
			Card card = cardsL.lastElement();
			card.setVisible(true);
			card.setPos(rect.left, rect.top);
			cards.add(card);
//			cardsL.remove(lastEle);
			cardsL.removeElementAt(lastEle);
		}
//		for(mi = 0; mi < cards.size(); mi++){
//			Log.v("cards in stockcontrol:" + mi, cards.elementAt(mi).px + "*" + cards.elementAt(mi).py);
//		}
//		Log.v("StockControl", "out moveCardFromLToR");
	}	
	//从右边的框内提取，一次将所有的纸牌移到左边。
	public void moveCardFromRToL(){
		lastEle = cards.size() - 1;
		Card card;
		for(mi = 0; mi < cards.size(); mi++){
			card = cards.elementAt(lastEle - mi);
			card.setVisible(false);
			card.setPos(inrectL.left, inrectL.top);
			cardsL.add(card);
		}
		cards.removeAllElements();
	}
}
