package jp.kanazawaapp.kit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class ArView extends View {

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
		
	}
	
	
}
