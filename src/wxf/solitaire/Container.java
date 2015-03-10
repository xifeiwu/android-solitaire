package wxf.solitaire;

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public abstract class Container {
	public int selectedCardLoc = -1;
	public Vector<Card> cards;// = new Vector<Card>();
	public int left, top, width, height;
	public void putCard(int kind, int num, Bitmap bmp, boolean isVisible){
		
	}
	public void putCard(Card card){
		selectedCardLoc = cards.size();
		
	}
	public void dragCard(float x, float y){
		
	}
	public boolean moveCardsTo(Container container){
		return false;
	}
	public void resetCards(){
		
	}
	public boolean onTouchDown(float y){
		return false;
	}	
	public boolean isCardFit(Card fcard, int num){
		return false;
	}


	public RectF rect;
	public void draw(Canvas canvas, Paint paint){

	}
}
