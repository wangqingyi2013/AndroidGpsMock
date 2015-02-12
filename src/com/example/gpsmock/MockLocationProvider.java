/*
 * Copyright © 2014 Pandabus Ltd., All Rights Reserved.
 * For licensing terms please contact Pandabus LTD.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.gpsmock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

/**
 * The MockLocationProvider represents
 * @version $Id$
 * @author wangqingyi
 * @datetime Dec 29, 2014 6:24:55 PM
 */
public class MockLocationProvider extends Service {

	private static final String TAG = "gps_mock";
	private static final int GPS_FREQUENCE = 1000;
	private List<String> data;

	private LocationManager mLocationManager;

	private String mocLocationProvider = LocationManager.NETWORK_PROVIDER;

	/**
	 * {@inheritDoc}
	 * @see android.app.Service#onCreate()
	 */
	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		Log.i(TAG, "start service of Mock gps--------");
		super.onCreate();
		registerReceiver(stopServierReceiver, new IntentFilter("GP_PROVIDER_STOP_SERVICE"));
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.addTestProvider(mocLocationProvider, false, false, false, false, true, true, true, 0, 5);
		mLocationManager.setTestProviderEnabled(mocLocationProvider, true);
		// mLocationManager.requestLocationUpdates(mocLocationProvider, 2000, 0, this);

		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... params) {
				initGpsLatLng();

				return null;
			}

			/**
			 * {@inheritDoc}
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(String result) {
				handler.sendEmptyMessageDelayed(0, 3000);
				super.onPostExecute(result);
			}

		}.execute("");

	}

	BroadcastReceiver stopServierReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("GP_PROVIDER_STOP_SERVICE")) {
				System.out.println("STOP SERVICE");
				data.clear();// clear all gps,so can stop mock gps
				MockLocationProvider.this.stopSelf();
			}

		}
	};

	private void initGpsLatLng() {
		data = new ArrayList<String>();
		InputStream is;
		try {
			is = getApplicationContext().getAssets().open("GX08");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null) {

				data.add(line);
				reader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	Handler handler = new Handler() {

		/**
		 * {@inheritDoc}
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			if (data.size() > 0) {
				int i = msg.what + 1;
				if (i == data.size() - 1) {
					i = 0;
					sendEmptyMessageDelayed(i, 15000);
					return;
				}
				startMockGps(i);
				sendEmptyMessageDelayed(i, GPS_FREQUENCE);
			}
			super.handleMessage(msg);
		}

	};

	@SuppressLint("NewApi")
	private void startMockGps(int i) {

		String str = data.get(i);
		if (TextUtils.isEmpty(str)) {
			return;
		}
		// Set one position
		String[] parts = str.split(",");
		Double latitude = Double.valueOf(parts[0]);
		Double longitude = Double.valueOf(parts[1]);
		Double altitude = Double.valueOf(parts[2]);
		Location location = new Location(mocLocationProvider);
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		location.setAltitude(altitude);
		location.setAccuracy(Float.valueOf(parts[3]));
		location.setBearing(Float.valueOf(parts[4]));
		location.setSpeed(Float.valueOf(parts[5]));
		location.setTime(Long.valueOf(parts[6]));
		location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
		// set the time in the location. If the time on this location
		// matches the time on the one in the previous set call, it will be
		// ignored
		Log.i(TAG, "SET GPS ----------" + latitude + "---" + longitude);
		mLocationManager.setTestProviderLocation(mocLocationProvider, location);

	}

	/**
	 * {@inheritDoc}
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * {@inheritDoc}
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		unregisterReceiver(stopServierReceiver);
		super.onDestroy();
	}

	/**
	 * {@inheritDoc}
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
