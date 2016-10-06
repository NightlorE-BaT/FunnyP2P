package by.mythsystems.funnyp2p;

import android.util.Log;

public class PCLog {

	public static boolean PC_INAPP_TRACKING_ENABLED = false;

	private String mLogTag = getClass().getName();
	private boolean mEnabled = true;
	private int mCauseSearchDeep = 3;

	public PCLog( String logTag, int causeSearchDeep ) {
		init( logTag, causeSearchDeep );
	}

	public PCLog( String logTag ) {
		init( logTag, 0 );
	}

	private void init( String logTag, int causeSearchDeep ) {
		mLogTag = logTag;

		if ( causeSearchDeep > 0 ) {
			mCauseSearchDeep = causeSearchDeep;
		}
	}

	public static void print( int logType, String logTag, String msg ) {
		if ( msg == null ) {
			msg = "LOG HAS NO MESSAGE!";
		}

		String logTypeName;

		switch ( logType ) {
			case Log.VERBOSE:
				logTypeName = "VERBOSE";
				Log.v( logTag, msg );
				break;
			case Log.DEBUG:
				logTypeName = "DEBUG";
				Log.d( logTag, msg );
				break;
			case Log.INFO:
				logTypeName = "INFO";
				Log.i( logTag, msg );
				break;
			case Log.WARN:
				logTypeName = "WARN";
				Log.w( logTag, msg );
				break;
			case Log.ERROR:
				logTypeName = "ERROR";
				Log.e( logTag, msg );
				break;
			case Log.ASSERT:
				logTypeName = "ASSERT";
				Log.e( logTag, msg );
				break;
			default:
				logTypeName = "WTF";
				Log.wtf( logTag, msg );
				break;
		}

		if ( PC_INAPP_TRACKING_ENABLED ) {
			String logAction = String.format( "%s: %s", logTypeName, ( logTag != null && !logTag.isEmpty() ? logTag : "TAG_NULL!" ) );
			PCAppTracker.track( "PC_LOG", logAction, msg );
		}
	}

	public void print( int logType, String msg ) {
		if ( !mEnabled ) {
			return;
		}

		print( logType, mLogTag, msg );
	}

	public void print( int logType, Exception exception, boolean localized ) {
		if ( exception == null ) {
			print( logType, "NULL exception instance to print!" );
			return;
		}

		String msg = null;

		Throwable cause = exception;

		for ( int i = 0; i < mCauseSearchDeep; i++ ) {
			if ( cause == null ) {
				msg = "Exception cause is NULL! Here was the crash early...";
				break;
			}

			if ( cause.getMessage() != null ) {
				msg = ( localized ? cause.getLocalizedMessage() : cause.getMessage() );
				break;
			}

			cause = cause.getCause();
		}

		if ( msg == null || msg.isEmpty() ) {
			msg = "UNKNOWN EXCEPTION! You may try to increminate cause deep parameter...";
		}

		print( logType, msg );
	}

	public void print( int logType, Exception exception ) {
		print( logType, exception, false );
	}

	@SuppressWarnings("UnusedDeclaration")
	public void setEnabled( boolean enabled ) {
		mEnabled = enabled;
	}

	public static void setInAppTrackingEnabled( boolean enabled ) {
		PC_INAPP_TRACKING_ENABLED = enabled;
	}

}
