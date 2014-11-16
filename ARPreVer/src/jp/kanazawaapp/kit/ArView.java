package jp.kanazawaapp.kit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class ArView extends View {

	//の描画位置を指定する 現在は中央
    private final float POS_COMPASSX = getWidth() / 2	;
    private final float POS_COMPASSY = getHeight()	/ 2;

    // 向きを保持する変数 (方角を指定する)
    float direction;
    
	public ArView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		//アンチエイリアス処理を有効化
		//アンチエイリアス処理　= 曲線や斜めのギザギザを少なくすること
		paint.setAntiAlias(true);
		
		//描画開始 複数出きるようにするため今後はfor文か何かでループ予定　紹介する場所は　ArrayListか？
		drawObject(canvas, paint);
	}
	
	private void drawObject(Canvas canvas, Paint paint) {
		 Path path = new Path();
		 path.moveTo(POS_COMPASSX, POS_COMPASSY - 20);
		 path.lineTo(POS_COMPASSX + 10, POS_COMPASSY + 10);
		 path.lineTo(POS_COMPASSX - 10, POS_COMPASSY + 10);
		 path.moveTo(POS_COMPASSX, POS_COMPASSY - 20);
		 paint.setColor(Color.RED);
		 canvas.rotate(-direction, POS_COMPASSX, POS_COMPASSY);
		 canvas.drawPath(path, paint);
		 canvas.rotate(direction, POS_COMPASSX, POS_COMPASSY);
	}
	
	//センサー値の取得と再描画
    public void drawScreen(float preDirection) {
        // センサーの値から端末の向きを計算する
        direction = (preDirection + 450) % 360;
        // onDrawを呼び出して再描画
        invalidate();
    }
}
