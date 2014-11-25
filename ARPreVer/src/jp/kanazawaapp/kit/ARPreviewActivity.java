package jp.kanazawaapp.kit;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.maps.GeoPoint;

public class ARPreviewActivity extends Activity implements SensorEventListener,LocationListener{

	//ARview
	private ArView arView;
	//センサー
	private SensorManager sensorManager;
	private List<Sensor> listMag;
	private List<Sensor> listAcc;
	private float[] accelerometerValues = new float[3];
	//x,y,z軸の磁気密度
	private float[] magneticValues =new float[3];
	
//location関係
	private LocationManager locationManager;
	//グーグルマップからの位置取得
	private GeoPoint geoPoint;
	//磁北補正用
	private GeomagneticField geomagneticField;  
// データベースで使用する変数
	//データベース名
	private final static String DB_NAME = "gps_data.db";
	//使用するテーブル
	private final static String DB_TABLE = "gps_data";
	//バージョン情報
	private final static int DB_VERSION = 1;
	//コンストラクタ
	private SQLiteDatabase db;
	//カーソル
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// フルスクリーン指定
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//タイトルバーの表示を解除
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// データベースの用意
		initData();

		// ARViewの取得
		arView = new ArView(this,cursor);
		//閉じる
		cursor.close();



		//各種センサーの用意
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//地磁気センサー
		listMag = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		//加速度センサー
		listAcc = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

		//Viewの重ね合わせ
		setContentView(new CameraView(this));
		addContentView(arView, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));



	}
	//activity実行直前
		@Override
		protected void onResume() {
			super.onResume();
			
			 //ロケーションマネージャの設定
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this);
			//センサー処理の登録
			
			/* sensorManager
			 * 第1引数		センサーによって呼び出される関数を持つクラス
			 * 第2引数		対象となる値を
			 *　第3引数		呼び出し頻度   (ゲームにするなら SENSOR_DELAY_GAME)
			 * */
			sensorManager.registerListener(this,listMag.get(0), SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(this,listAcc.get(0), SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		@Override
		protected void onStop() {
			super.onStop();
			//位置情報の取得の停止
			locationManager.removeUpdates(this);
			//センサーを終了
			sensorManager.unregisterListener(this);

		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.arpreview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//センサーの情報が変更された
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				accelerometerValues = event.values.clone();
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				magneticValues  = event.values.clone();
				break;
		}

		//magneticValues		x,y,z軸の磁気密度
		//accelerometerValues 	x,y,z軸の加速度　
		if (magneticValues != null && accelerometerValues != null) {
			float[] R = new float[16];
			float[] I = new float[16];

			SensorManager.getRotationMatrix(R, I, accelerometerValues,magneticValues);

			float[] actual_orientation = new float[3];

			SensorManager.getOrientation(R, actual_orientation);

			
			
			// 求まった方位角をラジアンから度に変換する
            float direction = (float) Math.toDegrees(actual_orientation[0])
            		//偏差を加算
            		+ geomagneticField.getDeclination();
           //ArViewに値を渡す
          //描画をする
            arView.drawScreen(direction,geoPoint);
        }
    }

	
	/*LocationLisner*/
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	@Override
	public void onLocationChanged(Location location) {
		   geoPoint = new GeoPoint((int) (location.getLatitude() * 1E6),
	                (int) (location.getLongitude() * 1E6));
		   
	        geomagneticField = new GeomagneticField((float) location.getLatitude(),
	                (float) location.getLongitude(), (float) location.getAltitude(),
	                new Date().getTime());
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	
	//データベース系
	public void initData() {
        // SQLiteOpenHelperを継承したクラスを使用してデータベースを作成します
        SQLiteOpenHelperEx helper = new SQLiteOpenHelperEx(this);
        db = helper.getWritableDatabase();

        cursor = db.query(DB_TABLE, new String[] { "info", "latitude",
                "longitude","genre" }, null, null, null, null, null,null);
        // テーブルが空の時内容をセットする
        if (cursor.getCount() < 1) {
            presetTable();
            cursor = db.query(DB_TABLE, new String[] { "info", "latitude",
                    "longitude","genre" }, null, null, null, null, null,null);
        }
    }
	private void presetTable() {
		/*
		 * ジャンルID
		 * 1 : 観光
		 * 2 : 飲食
		 * 3 : 撮影
		 */
        // テーブルの内容が空の時以下の内容をセットする
        ContentValues values = new ContentValues();
        values.put("info","金沢工業大学");
        values.put("latitude",36530349);
        values.put("longitude", 136627751);
        values.put("genre", 1);
        db.insert(DB_TABLE, "", values);
    }
	
	
	
	
	
	//データベース用のクラス
	 public class SQLiteOpenHelperEx extends SQLiteOpenHelper {
	        // コンストラクタ
	        public SQLiteOpenHelperEx(Context context) {
	        	//データベースの生成 
	        	//2つ目の引数をnullにすることでメモリ上にデータベースが作られる
	            super(context, DB_NAME, null, DB_VERSION);
	        }

	        @Override
	        public void onCreate(SQLiteDatabase db) {
	        	// テーブルの作成
	        	//実行したいSQL文を格納
	        	String sql = "create table if not exists "
	        			+ DB_TABLE
	        			+ "(info text, latitude numeric, longitude numeric)";
	        	db.execSQL(sql);

	        }

	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        	// データベースのアップグレード	
	        	// ここでは、テーブルを作り直しをしています
	        	db.execSQL("drop table if exists " + DB_TABLE);
	        	onCreate(db);
	        }
	        
	 	}
}



