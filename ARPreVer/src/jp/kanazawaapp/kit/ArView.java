package jp.kanazawaapp.kit;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.google.android.maps.GeoPoint;


public class ArView extends View {

	//コンパスの描画位置を指定する
	/**コンパスの横の位置*/
	private final float POS_COMPASSX = 30;
	/**コンパスの縦の位置*/
	private final float POS_COMPASSY = 30;

	/**アイコンの最大サイズ */
	private final int ICON_MAX_SIZE = 150;

	/**向きを保持する変数 (方角を指定する)*/
	float direction;

	/**現在地の緯度*/
	int nowLocationLat;
	/**現在位置の経度*/
	int nowLocationLong;

	/** ARテキストの情報を保持するオブジェクト*/
	public static ArrayList<databaseDefine.GPSData> gpsDataList = new ArrayList();
 	/**アイコンの座標を保持するオブジェクト*/
	public static ArrayList<CoordinateIcon> coordinate = new ArrayList();
	
	// カメラの画角を指定する 後にAPIで画角を取得し動的指定
	/**画角(度表記)*/
	private final int ANGLE = 60;
	/** ARテキスを表示する最大距離(メートル表記)<br>
	 * VIRE_LIMIT = 100000*/
	private final float VIEW_LIMIT = 100000;
	/** ディスプレイサイズ*/
	private int displayX;
	Point size = new Point();
	
	/**地球の半径(キロメートル表記)*/
	public static final double EARTH = 6378.137;

 	
    //データベース用のコンテキスト
//	public ArView(Context context,Cursor cursor) {
	public ArView(Context context) {
		super(context);
		//データベースの読み込み
		//readTable(cursor);
		
		databaseDefine.databaseInsert();
		
		// 画面サイズの取得
		Display disp = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		disp.getSize(size);
		displayX = (int)size.x;
		
	}


	@Override
	protected void onDraw(Canvas canvas) {
		
		Paint paint = new Paint();
		//アンチエイリアス処理を有効化
		//アンチエイリアス処理　= 曲線や斜めのギザギザを少なくすること
		paint.setAntiAlias(true);
		
		drawCompass(canvas, paint);
		coordinateInit();
		//ARテキストの描画
			for (int i = 0; i < gpsDataList.size(); i++) {
				// データの読み込み
				databaseDefine.GPSData data = gpsDataList.get(i);
				String info = data.info;
				int genre = data.genre;
				double dlat = data.latitude;
				double dlong = data.longitude;

				float distance = calculationDistance(dlat,dlong);

				// ARテキストとの距離が一定以上離れていたら、処理を行わずに次のARテキストの処理を行う
				if (distance > VIEW_LIMIT) {
					continue;
				}
				// ARテキストと現在地のなす角を求めて正規化する
				double angle = Math.atan2(dlat*1000000 - nowLocationLat, dlong*1000000 - nowLocationLong);
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
					drawBalloonText(canvas, paint, info, left, 55,distance,genre);
					//ARPreviewActivity.setIcon(info,left,55,i);

			}
		}
		
	}
	
	private void coordinateInit() {
		// TODO 自動生成されたメソッド・スタブ
		
	}


	/**
	 * ARの噴出しの描写
	 * @param canvas キャンバス
	 * @param paint ペイント
	 * @param text 施設名
	 * @param left 左側の位置
	 * @param top 上側の位置　
	 * @param distance 施設までの距離
	 * @param genre ジャンル
	 */
	private void drawBalloonText(Canvas canvas, Paint paint, String text,float left, int top, float distance, int genre) {
		//ResourceからBitmapを生成
		Bitmap bitmap = setIcon(genre);
		bitmap = Bitmap.createScaledBitmap(bitmap, ICON_MAX_SIZE, ICON_MAX_SIZE, false);
		
	    int extension = (int) (((VIEW_LIMIT - 0)/VIEW_LIMIT) *3);
	    
	    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * extension, bitmap.getHeight() * extension, false);
	    
	    CoordinateIcon xy = new CoordinateIcon();
	    xy.left = left;
	    xy.right = bitmap.getWidth() + left;
	    xy.top = top;
	    xy.bottom = bitmap.getHeight() + top;
	    xy.info = text;
	    coordinate.add(xy);
	    //drawableの描画領域設定（必須）
	    canvas.drawBitmap(bitmap, left, top,paint);
	}

	/**
	 * @param genre ジャンル
	 * @return ジャンルに対応したアイコン
	 */
	private Bitmap setIcon(int genre) {
		switch (genre) {
		case 1:
			
		default:
			return BitmapFactory.decodeResource(getResources(), R.drawable.seafood);
		}
	}


	/**方角を示すためのコンパス描画開始 現在は三角形だが、きちんと作る　画像？パス？要選択
	 * @param canvas キャンバス
	 * @param paint ペイント*/
	private void drawCompass(Canvas canvas, Paint paint) {
		
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
	
	
	/**
	 * センサー値の取得と再描画
	 * @param preDirection 磁気センサーで取得した方位
	 * @param geoPoint GPSセンサーで取得した現在地の情報
	 */
	public void drawScreen(float preDirection,GeoPoint geoPoint) {
		// センサーの値から端末の向きを計算する
		direction = (preDirection + 450) % 360;
		if(geoPoint != null){
			nowLocationLat = geoPoint.getLatitudeE6();
			nowLocationLong = geoPoint.getLongitudeE6();
		}
		// onDrawを呼び出して再描画
		invalidate();
	}


	/**
	 * 目標地点までの距離を求める
	 * @param lat 目的地の緯度
	 * @param lon 目的地の軽度
	 * @return 目標までの距離(メートル)
	 * @see <a href="http://www.kiteretsu-so.com/archives/1183">距離を求める</a>*/
	private float calculationDistance(double lat, double lon){
		// ARテキストとの距離を求め、ラジアンに変換する
		double dlat = lat - (double)(nowLocationLat / 1000000);
		double dlon = lon - (double)(nowLocationLong / 1000000);
		
		double radlat = Math.toRadians(dlat);
		double radlon = Math.toRadians(dlon);
		
		float distance = (float) Math.sqrt(Math.pow(EARTH * radlat,2) +
				Math.pow(Math.cos(Math.toRadians(nowLocationLat)) * EARTH * radlon, 2));
				
		return distance;
		
	}

	/**
	 * 座標情報の初期化
	 */
	private void CoordinateInit(){
		coordinate = new ArrayList<CoordinateIcon>();
	}
}

/**施設のアイコン*/
class CoordinateIcon{
	/**アイコンの左側のＸ座標*/
	float left;
	/**アイコンの右側のＸ座標*/
	float right;
	/**アイコンの上側のＹ座標*/
	float top;
	/**アイコンの下側のＹ座標*/
	float bottom;
	/**施設名*/
	String info;
}
