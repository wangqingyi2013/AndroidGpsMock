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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * The MainActivity represents
 * @version $Id$
 * @author wangqingyi
 * @datetime Dec 29, 2014 5:12:52 PM
 */
public class MainActivity extends Activity implements LocationListener {

	LocationManager locationManager;
	private TextView tv_lat;
	private TextView tv_lng;

	/**
	 * {@inheritDoc}
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		// start mock GPS service
		startService(new Intent(this, MockLocationProvider.class));

	}

	private void findViews() {
		tv_lat = (TextView) findViewById(R.id.main_lat);
		tv_lng = (TextView) findViewById(R.id.main_lng);
	}

	/**
	 * {@inheritDoc}
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		sendBroadcast(new Intent("GP_PROVIDER_STOP_SERVICE"));
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
		super.onDestroy();
	}

	public void startLocation(View v) {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// note: this must be same as mock location provider
		String mocLocationProvider = LocationManager.NETWORK_PROVIDER;

		locationManager.requestLocationUpdates(mocLocationProvider, 0, 0, this);
	}

	public void stopLocation(View v) {
		locationManager.removeUpdates(this);
	}

	/**
	 * {@inheritDoc}
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			System.out.println("main location changed:" + location.getLatitude() + "---" + location.getLongitude());
			tv_lat.setText("Lat:" + location.getLatitude());
			tv_lng.setText("Lng:" + location.getLongitude());
		}
	}

	/**
	 * {@inheritDoc}
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}
