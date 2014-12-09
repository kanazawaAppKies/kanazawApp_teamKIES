package jp.kanazawaapp.kit;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.google.android.maps.GeoPoint;

public class ArView extends View {

	//コンパスの描画位置を指定する
	private final float POS_COMPASSX = 30;
	private final float POS_COMPASSY = 30;


	// 向きを保持する変数 (方角を指定する)
	float direction;

	// 現在地を保持する変数
	int nowLocationX, nowLocationY;

	// ARテキストの情報を保持するオブジェクト
	private ArrayList<GPSData> gpsDataList;
 	
	// カメラの画角を指定する 後にAPIで画角を取得し動的指定
	private final int ANGLE = 60;
	// ARテキストの見える範囲を指定する(メートル表記)
	private final float VIEW_LIMIT = 100000;
	// ディスプレイサイズ
	private int displayX;
	Point size = new Point();
	
	//地球の半径
	public static final double EARTH = 6378.137;

 	
    
//	public ArView(Context context,Cursor cursor) {
	public ArView(Context context) {
		super(context);
		//データベースの読み込み
		//readTable(cursor);
		
		Log.i("readTable", "データの読み込み開始", new Throwable());
		
		readTable();
		
		Log.i("readTable", "データの読み込み完了", new Throwable());
		Log.i("DpsplaySize", "GetDisplaySizeStart", new Throwable());
		
		// 画面サイズの取得
		Display disp = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		disp.getSize(size);
		displayX = (int)size.x;
		
		Log.i("DpsplaySize", "GetDisplaySizeEnd", new Throwable());
	}


	@Override
	protected void onDraw(Canvas canvas) {
		
		Log.i("onDrawSTART", "onDrawを開始", new Throwable());
		
		Paint paint = new Paint();
		//アンチエイリアス処理を有効化
		//アンチエイリアス処理　= 曲線や斜めのギザギザを少なくすること
		paint.setAntiAlias(true);
		
		//方角を示すためのコンパス描画開始 現在は三角形だが、きちんと作る　画像？パス？要選択
		drawCompass(canvas, paint);
		
		//ARテキストの描画
			for (int i = 0; i < gpsDataList.size(); i++) {
				// データの読み込み
				GPSData data = gpsDataList.get(i);
				String info = data.info;
				int y = data.latitude;
				int x = data.longitude;

				float distance = calculationDistance(x,y);

				// ARテキストとの距離が一定以上離れていたら、処理を行わずに次のARテキストの処理を行う
				if (distance > VIEW_LIMIT) {
					continue;
				}
				// ARテキストと現在地のなす角を求めて正規化する
				double angle = Math.atan2(y - nowLocationY, x - nowLocationX);
				//度に変換
				float degree = (float) Math.toDegrees(angle);
				degree = -degree + 90;
				if (degree < 0)
					degree = 360 + degree;

				// 端末の向きとARテキストとの角度の差を求める
				float sub = degree - direction;
				if (sub < -180.0)
					sub += 360;
				if (sub > 180.0)
					sub -= 360;

				// ARテキストが視野に存在すれば描画処理を行う
				if (Math.abs(sub) < (ANGLE / 2)) {
					// 距離によってARテキストのサイズを決める
					float textSize = 50 * (float) (VIEW_LIMIT - distance)/ VIEW_LIMIT;
					paint.setTextSize(textSize);

					// ARテキストの描画を描画する
					float textWidth = paint.measureText(info);
					float diff = (sub / (ANGLE / 2)) / 2;
					float left = (displayX / 2 + displayX * diff) - (textWidth / 2);
					drawBalloonText(canvas, paint, info, left, 55);

			}
		}
		
			Log.i("onDrawEND", "onDrawを終了", new Throwable());
		
	}
	
	private void drawBalloonText(Canvas canvas, Paint paint, String text,float left, int top) {
		// 文字列の幅を取得
		float textWidth = paint.measureText(text);
		// フォント情報の取得
		FontMetrics fontMetrics = paint.getFontMetrics();

		// 文字列の5ポイント外側を囲む座標を求める
		float bLeft = left - 5;
		float bRight = left + textWidth + 5;
		float bTop = top + fontMetrics.ascent - 5;
		float bBottom = top + fontMetrics.descent + 5;

		// 吹き出しの描画
		RectF rectF = new RectF(bLeft, bTop, bRight, bBottom);
		paint.setColor(Color.LTGRAY);
		canvas.drawRoundRect(rectF, 5, 5, paint);

		// 三角形の描画
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		Path path = new Path();
		float center = left + textWidth / 2;
		float triangleSize = paint.getTextSize() / 3;
		path.moveTo(center, bBottom + triangleSize);
		path.lineTo(center - triangleSize / 2, bBottom - 1);
		path.lineTo(center + triangleSize / 2, bBottom - 1);
		path.lineTo(center, bBottom + triangleSize);
		canvas.drawPath(path, paint);

		// 文字列の描画
		paint.setColor(Color.WHITE);
		canvas.drawText(text, left, top, paint);
		
	}

	private void drawCompass(Canvas canvas, Paint paint) {
		
		Log.i("drawCompassSTART", "drawCompassを開始!", new Throwable());
		
		 Path path = new Path();
		 path.moveTo(POS_COMPASSX, POS_COMPASSY - 20);
		 path.lineTo(POS_COMPASSX + 10, POS_COMPASSY + 10);
		 path.lineTo(POS_COMPASSX - 10, POS_COMPASSY + 10);
		 path.moveTo(POS_COMPASSX, POS_COMPASSY - 20);
		 paint.setColor(Color.RED);
		 canvas.rotate(-direction, POS_COMPASSX, POS_COMPASSY);
		 canvas.drawPath(path, paint);
		 canvas.rotate(direction, POS_COMPASSX, POS_COMPASSY);
		 
		 Log.i("drawCompassEND", "drawCompassを終了!", new Throwable());
	}
	
	//センサー値の取得と再描画
	public void drawScreen(float preDirection,GeoPoint geoPoint) {
		// センサーの値から端末の向きを計算する
		direction = (preDirection + 450) % 360;
		if(geoPoint != null){
			nowLocationX = geoPoint.getLatitudeE6();
			nowLocationY = geoPoint.getLongitudeE6();
		}
		// onDrawを呼び出して再描画
		invalidate();
	}


//	public void readTable(Cursor cursor) {
//		// データベースに保存されている
//		// 全てのARテキストの情報をlistに読み込む
//		if (gpsDataList != null)
//			gpsDataList.clear();
//		gpsDataList = new ArrayList();
//
//		cursor.moveToFirst();
//		do {
//			GPSData data = new GPSData();
//			data.info = cursor.getString(0);
//			data.latitude = cursor.getInt(1);
//			data.longitude = cursor.getInt(2);
//			data.genre = cursor.getInt(3);
//			gpsDataList.add(data);
//		} while (cursor.moveToNext());
//		//なくなるまでリストに追加する
//	}
	
	public void readTable(){
		gpsDataList = new ArrayList();
		GPSData data = new GPSData();
		data.info = "金沢工業大学";
		data.latitude = 36530349;
		data.longitude = 136627751;
		data.genre = 1;
		gpsDataList.add(data);
	}
	
	//距離を求める
	private float calculationDistance(int x, int y){
		// ARテキストとの距離を求め、ラジアンに変換する
		//処理時間の関係で10^(-6)をかけるのではなく割った
		double dx = (x - nowLocationX) / 1000000;
		double dy = (y - nowLocationY) / 1000000;
		
		double radx = Math.toRadians(dx);
		double rady = Math.toRadians(dy);
		
		float distance = (float) Math.sqrt(Math.pow(EARTH * radx,2) +
				Math.pow(Math.cos(Math.toRadians(nowLocationX)) * EARTH * rady, 2));
		//distanceの単位をキロメートルからメートルに直す
		distance *= 1000;
		
		return distance;
		
	}

	//GPS情報を保持するクラス
	class GPSData {
		public String info;
		public int latitude; // 緯度
		public int longitude; // 経度
		public int genre;
	}
}
