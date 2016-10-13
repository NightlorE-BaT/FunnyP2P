package by.mythsystems.funnyp2p;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

	private TextView mWiFiStatusTextView;

	public MainActivityFragment() {
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState ) {
		return inflater.inflate( R.layout.fragment_main, container, false );
	}

	@Override
	public void onViewCreated( View view, @Nullable Bundle savedInstanceState ) {
		updateFiFiStatusDisplay();
		super.onViewCreated( view, savedInstanceState );
	}

	public void updateFiFiStatusDisplay() {
		View contentView = getView();

		if ( contentView != null ) {
			byte status = MainActivity.getWiFiStatusCode();
			updateWiFiStatusText( status );
		}
	}

	private void updateWiFiStatusText( byte statusCode ) {
		mWiFiStatusTextView = (TextView)getView().findViewById( R.id.text_wifi_status );

		int statusId;

		switch ( statusCode ) {
			case Constants.WiFiStatusCode.INIT:
				statusId = R.string.wifi_display_status_init;
				break;
			case Constants.WiFiStatusCode.SCAN:
				statusId = R.string.wifi_display_status_scan;
				break;
			case Constants.WiFiStatusCode.CONNECTING:
				statusId = R.string.wifi_display_status_connecting;
				break;
			case Constants.WiFiStatusCode.CONNECTED:
				statusId = R.string.wifi_display_status_connected;
				break;
			default:
				statusId = R.string.wifi_display_status_off;
				break;
		}

		mWiFiStatusTextView.setText( statusId );
	}
}
