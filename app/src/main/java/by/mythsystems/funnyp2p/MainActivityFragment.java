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

		float rotStep = 360 / membersCount;
		LayoutInflater inflater = getActivity().getLayoutInflater();
		int memberWidth = 300 / ( ( membersCount + 1 ) / 2 );
		int memberHeight = 300 / ( ( membersCount + 1 ) / 2 );

		for ( int mi = 0; mi < membersCount; mi++ ) {
			boolean isNextSide = mi > ( mi + 1 ) / 2;
//			ViewGroup memberView = new RelativeLayout( tableView.getContext() );
			RelativeLayout memberView = (RelativeLayout)inflater.inflate( R.layout.layout_table_member, null, false );
			memberView.setBackgroundResource( R.drawable.segment_1_3_white );
//			RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams( 150, 200 );
			memberView.setRotation( mi * rotStep );
			if ( isNextSide ) {
				lParams.addRule( RelativeLayout.LE, 200 );
				lParams.addRule( RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE );
			} else {
				lParams.addRule( RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE );
			}
//			lParams.addRule( RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE );
			tableView.addView( memberView, mi, lParams );
		}
	}
}
