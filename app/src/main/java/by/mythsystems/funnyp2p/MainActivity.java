package by.mythsystems.funnyp2p;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	private final IntentFilter mIntentFilter = new IntentFilter();
	WifiP2pManager.Channel mChannel;
	WifiP2pManager mManager;
	private static byte WIFI_CONN_STATUS_CODE = Constants.WiFiStatusCode.DISABLED;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		Toolbar toolbar = (Toolbar)findViewById( R.id.toolbar );
		setSupportActionBar( toolbar );

		FloatingActionButton fab = (FloatingActionButton)findViewById( R.id.fab );
		fab.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG )
						.setAction( "Action", null ).show();
			}
		} );

		//  Indicates a change in the Wi-Fi P2P status.
		mIntentFilter.addAction( WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

		// Indicates a change in the list of available peers.
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

		// Indicates the state of Wi-Fi P2P connectivity has changed.
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

		// Indicates this device's details have changed.
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		mManager = (WifiP2pManager) getSystemService( Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.menu_main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if ( id == R.id.action_settings ) {
			return true;
		}

		return super.onOptionsItemSelected( item );
	}

	public static byte getWiFiStatusCode() {
		return WIFI_CONN_STATUS_CODE;
	}
}
