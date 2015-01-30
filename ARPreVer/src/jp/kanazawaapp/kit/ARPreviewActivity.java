package jp.kanazawaapp.kit;

import java.util.Date;
import java.util.List;

import jp.kanazawaapp.kit.databaseDefine.GPSData;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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
	private float[] magneticValues = new float[3];
	
//location関係
	private LocationManager locationManager;
	//グーグルマップからの位置取得
	private GeoPoint geoPoint;
	//磁北補正用
	private GeomagneticField geomagneticField;  
	
	
	/**パッケージネーム*/
	public static String packageName;
	
	
//	//カーソル
//	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//パッケージネームの取得
		packageName = getPackageName();
		
		// フルスクリーン指定
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//タイトルバーの表示を解除
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// データベースの用意

		// ARViewの取得
		
		arView = new ArView(this);
		//データベースを使用するのでカーソルをコンテキストに渡す
//		arView = new ArView(this,cursor);

		
		//閉じる
		//cursor.close();

		
		//各種センサーの用意
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//地磁気センサー
		listMag = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		//加速度センサー
		listAcc = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		
		//Viewの重ね合わせ
		setContentView(new CameraView(this));
		addContentView(arView, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		//ボタン用のレイアアウト
		createButton();
		View view = getLayoutInflater().inflate(R.layout.activity_arpreview, null);
		addContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

		

	}
	//activity実行直前
		@Override
		protected void onResume() {
			super.onResume();
			
			
			 //ロケーションマネージャの設定
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if(locationManager != null){
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this);
			}
			
			checkGpsService();
			//センサー処理の登録
			/* sensorManager
			 * 第1引数		センサーによって呼び出される関数を持つクラス
			 * 第2引数		対象となる値を
			 *　第3引数		呼び出し頻度   (ゲームにするなら SENSOR_DELAY_GAME)
			 * */
			sensorManager.registerListener(this,listMag.get(0), SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(this,listAcc.get(0), SensorManager.SENSOR_DELAY_NORMAL);
		}
		/**GPSが有効になっていないか調べ無効なら有効にするかの設定画面を表示
		 * @see <a href="http://www.noveluck.co.jp/blog/archives/159">GPS設定</a>
		 */
		private void checkGpsService() {
			//GPSセンサーが使用可か？
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		        alertDialogBuilder.setMessage(getResources().getText(R.string.Gps_Setting))
		         .setCancelable(false)
		 
		        //GPS設定画面起動用ボタンとイベントの定義
		         .setPositiveButton(getResources().getText(R.string.Gps_Setting_Start),
		              new DialogInterface.OnClickListener(){
		              public void onClick(DialogInterface dialog, int id){
		                  Intent callGPSSettingIntent = new Intent(
		                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		                startActivity(callGPSSettingIntent);
		              }
		         });
		        //終了ボタン処理
		         alertDialogBuilder.setNegativeButton(getResources().getText(R.string.Gps_Setting_Cancel),
		              new DialogInterface.OnClickListener(){
		              public void onClick(DialogInterface dialog, int id){
		                   dialog.cancel();
		                   finish();
		              }
		         });
		        AlertDialog alert = alertDialogBuilder.create();
		    // 設定画面へ移動するかの問い合わせダイアログを表示
		        alert.show();
			}
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
		if (magneticValues != null && accelerometerValues != null && geomagneticField != null) {
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
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		
	}
	
	
	
	
	public void ClickButton(View view){
		TextView textView = (TextView)findViewById(R.id.textView1);
		textView.setText("クリックされました");
	}
	
	/**ボタンを作成する*/
	public void createButton(){
		for (int i = 0; i < ArView.gpsDataList.size(); i++) {
			GPSData data =  ArView.gpsDataList.get(i);
		}
	}
	
}
