package wxf.solitaire;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Card {
	public static final int EHearts = 0, EDiamond = 1, ESpades = 2, EClub = 3;
	
	public int cardType, cardNum;
	float px, py;
	int w, h;
	public Bitmap cardBmp;
	public boolean isVisible;
    public Card(int type, int num, float x, float y, Bitmap bmp){
    	cardType = type;
    	cardNum = num;
    	px = x;
    	py = y;
    	w = GameView.CARD_WIDTH;
    	h = GameView.CARD_HEIGHT;
    	cardBmp = bmp;
    	isVisible = true;
    }
    public void setPos(float x, float y){
    	px = x;
    	py = y;
    }
    public boolean isInRect(int tx, int ty){
    	if((tx > px)  && (tx < (px + h)) && (ty > py) && (ty < (py + w))){
    		return true;
    	}else{
    		return false;
    	}
    }
//    public void setXY(int x, int y){
//    	px = x;
//    	py = y;
//    }
    public void setVisible(boolean visible){
    	isVisible = visible;
    }
    public void draw(Canvas canvas, Paint paint){
    	if(isVisible){
    		canvas.drawBitmap(cardBmp, px, py, paint);
    	}else{
    		canvas.drawBitmap(GameView.cardBack, px, py, paint);
    	}
    }
//	public static enum ECardSuite
//  {
//  EHearts,    // HerttaºìÌÒ
//  EDiamond,   // Ruutu·½¿é
//  ESpades,    // PataºÚÌÒ
//  EClub       // RistiÃ·»¨
//  };
}
