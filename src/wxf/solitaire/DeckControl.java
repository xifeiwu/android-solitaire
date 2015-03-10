package wxf.solitaire;

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class DeckControl extends Container{

//	private int left, top, width, height;
	public RectF drect;
//	private Vector<Card> cards;
	public DeckControl(int l, int t){
		left = l;
		top = t;
		width = GameView.CARD_WIDTH;
		height = GameView.CARD_HEIGHT;
		drect = new RectF(left, top, left + width - 1, top + height - 1);
		rect = new RectF(left - GameView.CARDS_INTER_CAP / 2, top - GameView.CARDS_TOP_CAP / 2, 
				left + width + GameView.CARDS_INTER_CAP / 2 - 1, top + height + GameView.CARDS_TOP_CAP / 2 - 1);
		
		cards = new Vector<Card>();
	}
	private int lastEle;
	public void draw(Canvas canvas, Paint paint){
		canvas.drawRoundRect(drect, GameView.CARD_ROUNDING, GameView.CARD_ROUNDING, paint);
//		for(di = 0; di < cards.size(); di++){
//			cards.elementAt(di).draw(canvas, paint);
//		}
		lastEle = cards.size() - 1;
		if(lastEle >= 0){
			cards.elementAt(lastEle).draw(canvas, paint);
		}
	}
	
	public void putCard(int kind, int num, Bitmap bmp){
		Card card = new Card(kind, num, left, top, bmp);// + cards.size() * GameView.CARDS_CAP
//		cards.elementAt(cards.size() - 1).setVisible(false);
		card.setVisible(true);
		cards.add(card);
	}
	public boolean isCardFit(Card fcard, int num){
		lastEle = cards.size() - 1;
		if(num > 1){
			return false;
		}
		if(lastEle >= 0){
			Card card;
			card = cards.elementAt(lastEle);
			if((card.cardNum == (fcard.cardNum - 1)) 
					&& (card.cardType == fcard.cardType)){
				return true;
			}else{
				return false;
			}
		}else{
			if(fcard.cardNum == 0){
				return true;
			}else{
				return false;
			}
		}
	}
	public void putCard(Card card){
		card.setPos(left, top);
		cards.add(card);
	}
//	public void desertCard(){
//		if(cards.size() > 0){
//			cards.remove(cards.size() - 1);
//		}else{
//			
//		}
//	}
}
