package wxf.solitaire;

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

public class Foundation extends Container{

//	private int left, top, width, height;
	public RectF drect;//rect
//	private Vector<Card> cards;
	int lastEle;
	private int unturnedCnt = 0, turnedCnt = 0;
	public Foundation(int l, int t){
		left = l;
		top = t;
		width = GameView.CARD_WIDTH;
		height = GameView.CARD_HEIGHT;
		drect = new RectF(left, top, left + width - 1, top + height - 1);
		rect = new RectF(left - GameView.CARDS_INTER_CAP / 2, top - GameView.CARDS_INTER_CAP / 2, 
				left + width + GameView.CARDS_INTER_CAP / 2 - 1, 
				top + height + GameView.CARDS_INTER_CAP / 2 - 1 - GameView.UNTURNED_CARDS_CAP);

		cards = new Vector<Card>();
	}
	public int di;
	public void draw(Canvas canvas, Paint paint){
		canvas.drawRoundRect(drect, GameView.CARD_ROUNDING, GameView.CARD_ROUNDING, paint);
		for(di = 0; di < cards.size(); di++){
			cards.elementAt(di).draw(canvas, paint);
		}
//		canvas.drawText(this.turnedCnt + ":" + this.unturnedCnt, left, SOLITAIREActivity.screenH - GameView.UNTURNED_CARDS_CAP, paint);
	}
	//初始化Foundation时使用
	public void putCard(int kind, int num, Bitmap bmp, boolean isVisible){
		Card card;
		//修改方框的大小
		if(isVisible){
			card = new Card(kind, num, left, 
					top + unturnedCnt * GameView.UNTURNED_CARDS_CAP + turnedCnt * GameView.TURNED_CARDS_CAP, bmp);
			turnedCnt++;
			rect.bottom += GameView.TURNED_CARDS_CAP;
		}else{
			card = new Card(kind, num, left, 
					top + unturnedCnt * GameView.UNTURNED_CARDS_CAP, bmp);
			unturnedCnt++;
			rect.bottom += GameView.UNTURNED_CARDS_CAP;
		}
		card.setVisible(isVisible);
		cards.add(card);		
//		lastEle = cards.size() - 1;
//		if(lastEle >= 0){
//			rect.bottom = top + height - 1 + GameView.CARDS_CAP * lastEle;
//		}
	}
	private int dci;
	public void dragCard(float x, float y) {
		// TODO Auto-generated method stub	
		Card card;
		for(dci = selectedCardLoc; dci < cards.size(); dci++){
			card = cards.elementAt(dci);
			card.setPos(card.px + x, card.py + y);
		}
	}
	public boolean moveCardsTo(Container container){
		Card card;
		lastEle = cards.size();
		card = cards.elementAt(selectedCardLoc);
		if(container.isCardFit(card, (lastEle - selectedCardLoc))){
			for(dci = selectedCardLoc; dci < lastEle; dci++){
				card = cards.elementAt(dci);
				container.putCard(card);
			}
			for(dci = selectedCardLoc; dci < lastEle; dci++){
				cards.removeElementAt(cards.size() - 1);
				turnedCnt--;
				rect.bottom -= GameView.TURNED_CARDS_CAP;
			}	
			lastEle = cards.size() - 1;
			if((lastEle >= 0) && (!cards.elementAt(lastEle).isVisible)){
				cards.elementAt(lastEle).isVisible = true;
				unturnedCnt--;
				turnedCnt++;
				rect.bottom += (GameView.TURNED_CARDS_CAP - GameView.UNTURNED_CARDS_CAP);
			}
//			lastEle = cards.size() - 1;
//			if(lastEle >= 0){
//				rect.bottom = top + GameView.CARD_HEIGHT + GameView.CARDS_CAP * lastEle;
//				cards.elementAt(lastEle).isVisible = true;
//			}else{
//				rect.bottom = top + GameView.CARD_HEIGHT;
//			}
			return true;
		}else{
			return false;
		}
	}
	public void resetCards(){	
		Card card;
		for(dci = selectedCardLoc; dci < cards.size(); dci++){
			card = cards.elementAt(dci);
			card.setPos(left, top + unturnedCnt * GameView.UNTURNED_CARDS_CAP + (dci - unturnedCnt) * GameView.TURNED_CARDS_CAP);
		}		
	}
	public boolean onTouchDown(float y){
		y -= top;
		if(y < (unturnedCnt - 1) * GameView.UNTURNED_CARDS_CAP){//最上面
			selectedCardLoc = -1;
			return false;			
		}else{
			selectedCardLoc = (int) ((y - unturnedCnt * GameView.UNTURNED_CARDS_CAP) / GameView.TURNED_CARDS_CAP
					+ unturnedCnt);
			lastEle = cards.size() - 1;
			if(selectedCardLoc < 0){
				selectedCardLoc = 0;
			}
			if(selectedCardLoc < unturnedCnt){//为了是推拽最上面的纸牌空间宽松一些。
				selectedCardLoc = unturnedCnt;
			}
			if(selectedCardLoc > lastEle){
				selectedCardLoc = lastEle;
			}
			return true;
		}
//		selectedCardLoc = (int) ((y - top) / GameView.CARDS_CAP);
//		lastEle = cards.size() - 1;
//		if(selectedCardLoc > lastEle){
//			selectedCardLoc = lastEle;
//		}
//		if(cards.elementAt(selectedCardLoc).isVisible){
//			return true;
//		}else{
//			selectedCardLoc = -1;
//			return false;
//		}
	}
	//加入一个纸牌，在容器之间传递使用。
	public void putCard(Card card){
		lastEle = cards.size();
		card.setPos(left, top + unturnedCnt * GameView.UNTURNED_CARDS_CAP + (lastEle - unturnedCnt) * GameView.TURNED_CARDS_CAP);
		cards.add(card);
		rect.bottom += GameView.TURNED_CARDS_CAP;
		turnedCnt++;
//		lastEle = cards.size() - 1;
//		if(lastEle >= 0){
//			rect.bottom = top + GameView.CARD_HEIGHT + GameView.CARDS_CAP * lastEle;
//		}
	}

	public boolean isCardFit(Card fcard, int num){
		lastEle = cards.size() - 1;
		if(lastEle >= 0){
			Card card;
			card = cards.elementAt(lastEle);
			if(card.isVisible && (card.cardNum == (fcard.cardNum + 1)) 
					&& ((card.cardType / 2) != (fcard.cardType / 2))){
				return true;
			}else{
				return false;
			}
		}else{
			if(fcard.cardNum == 12){
				return true;
			}else{
				return false;
			}
		}
	}
}
