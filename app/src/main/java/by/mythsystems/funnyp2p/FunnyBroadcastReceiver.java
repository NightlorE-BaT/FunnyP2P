package by.mythsystems.funnyp2p;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class FunnyBroadcastReceiver extends WakefulBroadcastReceiver {

	private static String LOG_TAG = "FUNNY BROADCAST RECEIVER";

	@Override
	public void onReceive( Context context, Intent intent ) {
		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			// Determine if Wifi P2P mode is enabled or not, alert the Activity.
			int state = intent.getIntExtra( WifiP2pManager.EXTRA_WIFI_STATE, -1);

			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				PCLog.print( Log.DEBUG, LOG_TAG, "WifiP2p Enabled" );
//				activity.setIsWifiP2pEnabled(true);
			} else {
				PCLog.print( Log.DEBUG, LOG_TAG, "WifiP2p Disabled" );
//				activity.setIsWifiP2pEnabled(false);
			}
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

			// The peer list has changed!  We should probably do something about
			// that.

		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

			// Connection state changed!  We should probably do something about
			// that.

		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
//			DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//					.findFragmentById(R.id.frag_list);
//			fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//					WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
		}
	}
}
