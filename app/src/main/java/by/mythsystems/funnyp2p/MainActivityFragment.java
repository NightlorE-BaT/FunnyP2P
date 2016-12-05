package by.mythsystems.funnyp2p;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

	private TextView mWiFiStatusTextView;
	private ArrayList<ViewGroup> mTableMembers = new ArrayList<ViewGroup>();

	public MainActivityFragment() {
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState ) {
//		return inflater.inflate( R.layout.fragment_main, container, false );
		return inflater.inflate( R.layout.fragment_table, container, false );
	}

	@Override
	public void onViewCreated( View view, @Nullable Bundle savedInstanceState ) {
		View contentView = getView();

		if ( contentView != null ) {
			mWiFiStatusTextView = (TextView)getView().findViewById( R.id.text_wifi_status );

			updateFiFiStatusDisplay();
		}

		createTable( (ViewGroup)view.findViewById( R.id.viewgroup_table ), 3 );
		super.onViewCreated( view, savedInstanceState );

	}

	public void updateFiFiStatusDisplay() {
		byte status = MainActivity.getWiFiStatusCode();

		updateWiFiStatusText( status );
	}

	private void updateWiFiStatusText( byte statusCode ) {
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

	private void createTable( ViewGroup tableView, int membersCount ) {
		mTableMembers.clear();

		for ( int mi = 0; mi < membersCount; mi++ ) {
			ViewGroup memberView = new RelativeLayout( tableView.getContext() );
			memberView.setBackgroundResource( R.drawable.segment_1_3_white );
			RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			lParams.addRule( RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE );
			tableView.addView( memberView, mi, lParams );
		}
	}
}
