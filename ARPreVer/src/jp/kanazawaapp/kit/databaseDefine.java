package jp.kanazawaapp.kit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class databaseDefine {
	/**データベースの要素*/
	final static String  TYPES = "(name,latitude,longitude,genre,open,close,rest)";
	
	/**データベース挿入のテンプレート文*/
	final static String NAMELIST ="INSERT INTO namelist"+TYPES+"VALUES";
	
	/** データ名と保存場所を登録*/
    static String dbName = "data/data/" + ARPreviewActivity.packageName + "/myDatabase1.db";
    
    /**データベースオブジェクトを生成*/
    static SQLiteDatabase dbObject = SQLiteDatabase.openOrCreateDatabase(dbName, null);
   
    /**古いテーブルを破棄するSQL文*/
    static String dropTable = "DROP TABLE IF EXISTS namelist";
   
    /**3つのカラムを持つテーブルを作成するSQL文を登録*/
    static String createTable = "CREATE TABLE namelist" + "(id INTEGER PRIMARY KEY, name STRING, latitude NUMERIC, longitude NUMERIC, genre INTEGER, open INTEGER, close INTEGER, rest INTEGER)";
	
    /**クアリを設定 (後に営業中のものに変更する)*/
    static String query = "SELECT * FROM namelist WHERE genre >= 0";
    
    
    /**データベースにデータを追加*/
	public static void databaseInsert(){
		Log.i("database", "挿入開始");
		String[] insert = {
				NAMELIST + "('海鮮どん屋',36.570923,136.648972,0,1100,2100,10001000)",
				NAMELIST + "('廻る近江町市場寿し',36.571633,136.656835,0,930,2000,10000000)",
				NAMELIST + "('金沢工業大学',36.530349,136.627751,0,930,2000,10000000)",
		};
		Log.i("database", "完了");
		//古いテーブルを破棄
		dbObject.execSQL(dropTable);
		//テーブルを作成
		dbObject.execSQL(createTable);
		
		//データを追加
		for(int i = 0;i < insert.length; i++){
			dbObject.execSQL(insert[i]);
		}
		Log.i("database", "挿入完了");
		readDatabase();
	}
	
	private static void readDatabase(){
		Log.i("database", "読み込み開始");
		Cursor cursor = dbObject.rawQuery(query, null);
		GPSData data =  new GPSData();
		while (cursor.moveToNext()) {
			//各カラムのidを取得
			int idId = cursor.getColumnIndex("id");
			int idName = cursor.getColumnIndex("name");
	    	int idLatitude = cursor.getColumnIndex("latitude");
	    	int idLongitude = cursor.getColumnIndex("longitude");
	    	int idOpen = cursor.getColumnIndex("open");
	    	int idClose = cursor.getColumnIndex("close");
	    	int idRest = cursor.getColumnIndex("rest");
	    	int idGenre = cursor.getColumnIndex("genre");
	
	    	int id = cursor.getInt(idId);
	    	String name = cursor.getString(idName);
	    	double latitude = cursor.getDouble(idLatitude);
	    	double longitude = cursor.getDouble(idLongitude);
	    	int open = cursor.getInt(idOpen);
	    	int close = cursor.getInt(idClose);
	    	int rest = cursor.getInt(idRest);
	    	int genre = cursor.getInt(idGenre);
	    	
	    	//データの追加
	    	data.info = name;
	    	data.latitude = latitude;
	    	data.longitude = longitude;
	    	data.genre = genre;
	    	data.open = open;
	    	data.close = close;
	    	data.rest = rest;
	    	
	    	ArView.gpsDataList.add(data);
			
		}
	}
	
	/**GPS情報を保持するクラス*/
	static class GPSData {
		/**
		 * 施設名<br>
		 * 漢字、英字可<br>
		 * 文字数制限なし<br>
		 */
		public String info;
		/**
		 * 緯度<br>
		 * 10進法で実数
		 */
		public double latitude; 
		/**
		 * 経度<br>
		 * 10進法で実数
		 */
		public double longitude;
		/**
		 * ジャンルの種類<br>
		 * 1 観光<br>
		 * 2 飲食*/
		public int genre;
		/**
		 * オープン時間
		 */
		public int open;
		/**
		 * 閉店時間
		 */
		public int close;
		/**
		 * 定休日
		 * */
		public int rest;
	}

}
