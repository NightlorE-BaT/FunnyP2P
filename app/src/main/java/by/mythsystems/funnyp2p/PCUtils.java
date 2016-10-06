package by.mythsystems.funnyp2p;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
public class PCUtils {

	private static final String LOG_TAG = "PCUtils";
	public static final int REQ_PERMISSION_WITH_CUSTOM_LISTENER = 255;

	public static int stringToInt( String strNum ) {
		if ( strNum != null )
			try {
				return Integer.parseInt( strNum );
			} catch ( NumberFormatException e ) {
				e.printStackTrace();
			}

		return -1;
	}

	public static float stringToFloat( String strNum ) {
		if ( strNum != null )
			try {
				return Float.parseFloat( strNum );
			} catch ( NumberFormatException e ) {
				e.printStackTrace();
			}

		return -1F;
	}

	public static int getStringMemoryUsage( String strValue ) {
		float rawSize = (float)strValue.length() * 2f + 38f;
		int roundedSize = Math.round( rawSize / 8f ) * 8;
		return ( roundedSize < Math.round( rawSize ) ? roundedSize + 8 : roundedSize );
	}

	public static String getDeviceManufacturer() {
		return String.format( "%s%s", Character.toUpperCase( Build.MANUFACTURER.charAt( 0 ) ),
				Build.MANUFACTURER.substring( 1 ) );
	}

	public static String getDeviceModel() {
		return Build.MODEL.toUpperCase();
	}

	public static String getDeviceDisplayName() { // There's some magic
		return String.format( "%s %s", getDeviceManufacturer(), Build.MODEL.toUpperCase() );
	}

	public static String getOSVersion() {
		return "Android " + Build.VERSION.RELEASE;
	}

	public static <VT> Bundle mapToBundle( Map<String, VT> map ) {
		Bundle result = new Bundle();

		if ( map == null ) {
			return result;
		}

		for ( Map.Entry<String, VT> entry : map.entrySet() ) {
			if ( entry.getValue() instanceof Map ) {
				//noinspection unchecked
				result.putBundle( entry.getKey(), mapToBundle( (Map)entry.getValue() ) );
			} else if ( entry.getValue() instanceof String ) {
				result.putString( entry.getKey(), (String)entry.getValue() );
			} else if ( entry.getValue() instanceof Boolean ) {
				result.putBoolean( entry.getKey(), (Boolean)entry.getValue() );
			} else if ( entry.getValue() instanceof Integer ) {
				result.putInt( entry.getKey(), (Integer)entry.getValue() );
			} else if ( entry.getValue() instanceof Float ) {
				result.putFloat( entry.getKey(), (Float)entry.getValue() );
			} else if ( entry.getValue() instanceof Long ) {
				result.putLong( entry.getKey(), (Long)entry.getValue() );
			} else if ( entry.getValue() instanceof Double ) {
				result.putDouble( entry.getKey(), (Double)entry.getValue() );
			}
		}

		return result;
	}

	public static <VT> HashMap<String, VT> bundleToMap( Bundle bundle ) {
		HashMap<String, VT> result = new HashMap<String, VT>();

		if ( bundle == null ) {
			return result;
		}

		for ( String key : bundle.keySet() ) {
			Object value = bundle.get( key );
			if ( value instanceof Bundle ) {
				try {
					//noinspection unchecked
					result.put( key, (VT)bundleToMap( (Bundle)value ) );
				} catch ( ClassCastException e ) {
					e.printStackTrace();
				}
			} else {
				try {
					//noinspection unchecked
					result.put( key, (VT)value );
				} catch ( ClassCastException e ) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public static boolean isIntentServiceAvailable( Context context, final Intent serviceIntent ) {
		if ( context == null ) {
			return false;
		}

		final PackageManager packageManager = context.getPackageManager();
		if ( packageManager == null ) {
			return false;
		}

		List<ResolveInfo> infos = packageManager.queryIntentServices( serviceIntent, 0 );
		return ( infos != null && infos.size() > 0 );
	}

	public static void postInMainThread( Context context, Runnable runnable, long delayMillis ) {
		Handler mainHandler = new Handler( context.getMainLooper() );
		if ( delayMillis > 0 ) {
			mainHandler.postDelayed( runnable, delayMillis );
		} else {
			mainHandler.post( runnable );
		}
	}

	public static void postInMainThread( Context context, Runnable runnable ) {
		postInMainThread( context, runnable, 0 );
	}

	public static int dipToPix( Context context, int dipValue ) {
		DisplayMetrics dispMetr = context.getResources().getDisplayMetrics();
		return (int)TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, dipValue, dispMetr );
	}

	public static int spToPix( Context context, int spValue ) {
		DisplayMetrics dispMetr = context.getResources().getDisplayMetrics();
		return (int)TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_SP, spValue, dispMetr );
	}

// USELESS
//	public static int pixToDip( Context context, int pixValue ) {
//		DisplayMetrics dispMetr = context.getResources().getDisplayMetrics();
//		return (int)TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_PX, pixValue, dispMetr );
//	}

	/**
	 * Determines if app is debuggable.
	 *
	 * @param context The {@link Context} to get {@link ApplicationInfo} to determine this flag.
	 * @return true if app debuggable.
	 */
	public static boolean isDebuggableBuild( Context context ) {
		return ( 0 != ( context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );
	}

	public static String domainOf( String urlStr ) {
		try {
			URL url = new URL( urlStr );
			return url.getHost();
		} catch ( MalformedURLException ignore ) {
		}

		return null;
	}

	public static void showGallery( Context context, String url ) {
		Intent pickIntent = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );

		if ( context != null && context instanceof Activity ) {
			( (Activity)context ).startActivityForResult( pickIntent, Constants.REQ_CODE_PICK_IMAGE );
		}
	}

	public static InputStream pickImageInput( Context context, Uri uri ) {
		try {
			return context.getContentResolver().openInputStream( uri );
		} catch ( FileNotFoundException e ) {
			PCLog.print( Log.ERROR, LOG_TAG.concat( ".pickImageInput(Context, Intent)" ), e.getMessage() );
		}

		return null;
	}

	public static InputStream pickImageInput( Context context, Intent intent ) {
		return pickImageInput( context, intent.getData() );
	}

	public static boolean pickImageToFile( Context context, Uri uri, File file, boolean override ) throws IOException {
		if ( !override && file.exists() ) {
			throw new IOException( String.format( "File '%s' already exists!", file.getAbsolutePath() ) );
		}

		//noinspection ResultOfMethodCallIgnored
		file.getParentFile().mkdirs();
		InputStream inputStream = pickImageInput( context, uri );
		if ( inputStream == null ) {
			return false;
		}

		OutputStream output = null;
		//noinspection TryWithIdenticalCatches
		try {
			output = new FileOutputStream( file );
			byte[] buffer = new byte[4 * 1024]; // or other buffer size
			int read;

			while ( ( read = inputStream.read( buffer ) ) != -1 ) {
				output.write( buffer, 0, read );
			}
			output.flush();
			return true;
		} catch ( FileNotFoundException e ) {
			PCLog.print( Log.ERROR, LOG_TAG.concat( ".pickImageFile(Context, Intent)" ), e.getMessage() );
		} catch ( IOException e ) {
			PCLog.print( Log.ERROR, LOG_TAG.concat( ".pickImageFile(Context, Intent)" ), e.getMessage() );
		} finally {
			try {
				if ( output != null ) {
					output.close();
				}
				inputStream.close();
			} catch ( IOException e ) {
				PCLog.print( Log.ERROR, LOG_TAG.concat( ".pickImageFile(Context, Intent)" ), e.getMessage() );
			}
		}

		return false;
	}

	public static boolean pickImageToFile( Context context, Intent intent, File file, boolean override ) throws IOException {
		return pickImageToFile( context, intent.getData(), file, override );
	}

	public static void copyStream( InputStream input, OutputStream output ) throws IOException {
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ( ( bytesRead = input.read( buffer ) ) != -1 ) {
			output.write( buffer, 0, bytesRead );
		}
	}

	/**
	 * Represents HTTP response entity parsed as {@link String} to
	 * {@link JSONObject}
	 *
	 * @param entityString the {@link String} representation of a {@link JSONObject}.
	 * @return JSONObject of HTTP response entity
	 */
	public static JSONObject stringToJSON( String entityString, String key ) {
		try {
			if ( key != null ) {
				return ( new JSONObject( entityString ) ).getJSONObject( key );
			} else {
				return new JSONObject( entityString );
			}
		} catch ( IllegalStateException | JSONException | ClassCastException | NullPointerException e ) {
			PCLog.print( Log.ERROR, LOG_TAG.concat( ".getJSONObject(String, String)" ), e.getMessage() );
		}

		return null;
	}

	public static String bufferedReaderToString( BufferedReader input ) {
		String inputStr = null;
		try {
			inputStr = input.readLine();
			String duplicationStr = "FaxContainerFile";
			int duplicationPos = inputStr.lastIndexOf( duplicationStr );
			if ( duplicationPos > 0 ) {
				inputStr = String.format( "%s,%s", inputStr.substring( 0, duplicationPos - 3 ),
						inputStr.substring( duplicationPos + duplicationStr.length() + 3 ) );
			}
		} catch ( IOException e ) {
			PCLog.print( Log.ERROR, LOG_TAG.concat( ".bufferedReaderToString(BufferedReader)" ), e.getMessage() );
		}

		return inputStr;
	}

	public static String getPathByContentUri( Context context, Uri uri ) {
		Cursor cursor = null;

		try {
			String[] media = { MediaStore.Images.Media.DATA };

			if ( ( cursor = context.getContentResolver().query( uri, media, null, null, null ) ) != null ) {
				int column_index = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
				cursor.moveToFirst();

				return cursor.getString( column_index );
			}
		} finally {
			if ( cursor != null ) {
				cursor.close();
			}
		}

		return null;
	}

	/**
	 * Represents HTTP response entity parsed as {@link String} to
	 * {@link JSONObject}
	 *
	 * @param input the {@link BufferedReader} representation of a {@link JSONObject}.
	 * @return JSONObject of HTTP response entity
	 */
	public static JSONObject bufferedToJSON( BufferedReader input, String key ) {
		String entityString = bufferedReaderToString( input );

		if ( entityString == null ) {
			return null;
		}

		return stringToJSON( entityString, key );
	}

	/**
	 * Represents HTTP response entity parsed as {@link String} to
	 * {@link JSONObject}
	 *
	 * @param entityString the {@link String} representation of a {@link JSONObject}.
	 * @return JSONObject of HTTP response entity
	 */
	public static JSONObject stringToJSON( String entityString ) {
		return stringToJSON( entityString, null );
	}

	public static boolean isAndroidVersionSince( int sdkVerNum ) {
		return ( Build.VERSION.SDK_INT >= sdkVerNum );
	}

	public static String strToMD( String str, String mdFormat ) {
		String result = null;
		MessageDigest mdStr;

		try {
			mdStr = MessageDigest.getInstance( mdFormat );
			mdStr.update( str.getBytes( "UTF-8" ) );
			result = new BigInteger( 1, mdStr.digest() ).toString( 16 );

			if ( mdFormat.compareToIgnoreCase( "MD5" ) == 0 ) {
				while ( result.length() < 32 ) {
					result = "0" + result;
				}
			} else {
				while ( result.length() < 40 ) {
					result = "0" + result;
				}
			}

		} catch ( NoSuchAlgorithmException e ) {
			PCLog.print( Log.ERROR, LOG_TAG, e.getMessage() );
		} catch ( UnsupportedEncodingException e ) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Returns resource ID using available {@link Resources} to search by resName and resType.
	 *
	 * @param context The {@link Context} to get access for {@link Resources}.
	 * @param resName The name of searched resource.
	 * @param resType The type of searched resource (see Android docs for available res types).
	 * @return The associated resource identifier.  Returns 0 if no such
	 * resource was found. (0 is not a valid resource ID.)
	 */
	public static int getResId( Context context, String resName, String resType ) {
		if ( resName == null ) {
			return 0;
		}

		return context.getResources().getIdentifier( resName, resType, context.getPackageName() );
	}

	/**
	 * Returns {@link Uri} link for Raw resource with specified resName.
	 *
	 * @param context The {@link Context} to get access for {@link Resources}.
	 * @param resName The name of the resource which {@link Uri} should be obtained.
	 * @return the {@link Uri} or null if the resource was not found.
	 */
	public static Uri getUriForRaw( Context context, String resName ) {
		if ( resName.isEmpty() ) {
			return null;
		}

		try {
			return Uri.parse( String.format( "%s%s/%s/%s",
					Constants.ANDROID_RESOURCE_PREFIX,
					context.getPackageName(),
					Constants.ANDROID_RESOURCE_RAW_TYPE, resName ) );
		} catch ( NullPointerException e ) {
			e.printStackTrace();
		}

		return null;
	}

//	/**
//	 * Returns {@link PendingIntent} to use it with {@link Notification} as its contentIntent.
//	 *
//	 * @param context              The {@link Context} to set appropriated class for {@link Intent} contained in result {@link PendingIntent}.
//	 * @param intent               The {@link Intent} which will be contained in result {@link PendingIntent}.
//	 * @param useNotificationPopup The flag determines if result {@link PendingIntent} should open popup declared in {@link PCNotificationActivity}.
//	 * @return The {@link PendingIntent}.
//	 */
//	public static PendingIntent getStartPendingIntent( Context context, Intent intent, String activityName, boolean useNotificationPopup ) {
//		if ( useNotificationPopup ) {
//			intent.setClass( context, PCNotificationActivity.class );
//		} else {
//			try {
//				Class<?> activityClass = Class.forName( activityName );
//				intent.setClass( context, activityClass );
//			} catch ( ClassNotFoundException e ) {
//				e.printStackTrace();
//			}
//		}
//
//		return PendingIntent.getActivity( context,
//				( intent.getExtras().getInt( PCNotificationConfig.DATA_KEY_ID ) ),
//				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ), PendingIntent.FLAG_UPDATE_CURRENT );
//	}

	/**
	 * Use this method to get {@link String} value from {@link JSONObject} without crash if
	 * the key not contained.
	 *
	 * @param jsonObj The {@link JSONObject} to get value from.
	 * @param key     The key of the value that should be obtained.
	 * @return The value or null if key not obtained in specified {@link JSONObject}.
	 */
	public static String getStringFromJSON( JSONObject jsonObj, String key ) {
		if ( jsonObj.has( key ) ) {
			try {
				return jsonObj.getString( key );
			} catch ( JSONException ignore ) {
			}
		}

		return null;
	}

	/**
	 * Use this method to get {@link String} value from {@link JSONObject} without crash if
	 * the key not contained.
	 *
	 * @param jsonObj The {@link JSONObject} to get value from.
	 * @param key     The key of the value that should be obtained.
	 * @return The value or null if key not obtained in specified {@link JSONObject}.
	 */
	public static int getIntFromJSON( JSONObject jsonObj, String key ) {
		if ( jsonObj.has( key ) ) {
			try {
				return jsonObj.getInt( key );
			} catch ( JSONException ignore ) {
			}
		}

		return -1;
	}

	/**
	 * Use this method to get {@link String} object from {@link Resources} of specified {@link Context}.
	 *
	 * @param context The {@link Context} to get access for {@link Resources}.
	 * @param name    The name of the {@link String} resource that should be obtained.
	 * @return The string value.
	 */
	public static String getStringValue( Context context, String name ) {
		return context.getString( getResId( context, name, "string" ) );
	}

	/**
	 * Returns intent to share data via native app.
	 *
	 * @param context     the context.
	 * @param packageName the package name to search intent for.
	 * @return the {@link Intent} if app with packageName found or null.
	 */
	public static Intent getIntentForPackage( Context context, String packageName ) {
		Intent intent = new Intent();
		List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities( intent, 0 );

		for ( ResolveInfo info : matches ) {
			if ( info.activityInfo.packageName.toLowerCase().startsWith( packageName ) ) {
				intent.setPackage( info.activityInfo.packageName );
				return intent;
			}
		}

		return null;
	}

	@TargetApi(Build.VERSION_CODES.M)
	public static <ActivityC extends Activity & ActivityCompat.OnRequestPermissionsResultCallback> void requestPermissions( ActivityC activity,
	                                                                                                                        String[] permissions,
	                                                                                                                        int resultCode ) {
		ArrayList<String> reqList = new ArrayList<String>();

		for ( String permission : permissions ) {
			if ( ActivityCompat.checkSelfPermission( activity, permission ) != PackageManager.PERMISSION_GRANTED ) {
				reqList.add( permission );
			}
		}

		if ( !reqList.isEmpty() ) {
			String[] reqArray = new String[reqList.size()];
			ActivityCompat.requestPermissions( activity, reqList.toArray( reqArray ), resultCode );
		} else {
			PackageManager packageMgr = activity.getPackageManager();
			String packageName = activity.getPackageName();

			int[] granted = new int[permissions.length];
			for ( int i = 0; i < permissions.length; i++ ) {
				granted[i] = packageMgr.checkPermission(
						permissions[i], packageName );
			}

			if ( PCUtils.isAndroidVersionSince( 23 ) ) {
				activity.onRequestPermissionsResult( resultCode, permissions, granted );
			} else {
				//noinspection TryWithIdenticalCatches
				try {
					Method resultMethod = activity.getClass().getMethod( "onRequestPermissionsResult", int.class, String[].class, int[].class );
					resultMethod.invoke( activity, resultCode, permissions, granted );
				} catch ( NoSuchMethodException e ) {
					e.printStackTrace();
				} catch ( InvocationTargetException e ) {
					e.printStackTrace();
				} catch ( IllegalAccessException e ) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param context     The activity context.
	 * @param permissions List of permissions to ask.
	 * @param callback    The callback to handle onRequestPermissionsResult( ) in Android SDK < 23.
	 */
	public static void requestPermissions( final Context context, final String[] permissions,
	                                       final ActivityCompat.OnRequestPermissionsResultCallback callback ) {
		ArrayList<String> reqList = new ArrayList<String>();

		for ( String permission : permissions ) {
			if ( ActivityCompat.checkSelfPermission( context, permission ) != PackageManager.PERMISSION_GRANTED ) {
				reqList.add( permission );
			}
		}

		final int[] grantResults = new int[permissions.length];

		if ( reqList.size() > 0 ) {
			String[] reqArray = new String[reqList.size()];
			Handler handler = new Handler( Looper.getMainLooper() );
			handler.post( new Runnable() {
				@TargetApi(Build.VERSION_CODES.M)
				@Override
				public void run() {
					PackageManager packageManager = context.getPackageManager();
					String packageName = context.getPackageName();

					final int permissionCount = permissions.length;

					if ( isAndroidVersionSince( 23 ) && context instanceof Activity ) {
						( (Activity)context ).requestPermissions( permissions, REQ_PERMISSION_WITH_CUSTOM_LISTENER );
					} else {
						for ( int i = 0; i < permissionCount; i++ ) {
							grantResults[i] = packageManager.checkPermission( permissions[i], packageName );
						}

						callback.onRequestPermissionsResult( REQ_PERMISSION_WITH_CUSTOM_LISTENER, permissions, grantResults );
					}
				}
			} );
		} else {
			for ( int i = 0; i < permissions.length; i++ ) {
				grantResults[i] = PackageManager.PERMISSION_GRANTED;
			}

			callback.onRequestPermissionsResult( REQ_PERMISSION_WITH_CUSTOM_LISTENER, permissions, grantResults );
		}

	}


	public static String getAppVersionName( Context context ) {
		try {
			PackageInfo packInfo = context.getPackageManager().getPackageInfo( context.getPackageName(), 0 );
			return packInfo.versionName;
		} catch ( PackageManager.NameNotFoundException e ) {
			e.printStackTrace();
		}

		return null;
	}

	public static int getAppVersionCode( Context context ) {
		try {
			PackageInfo packInfo = context.getPackageManager().getPackageInfo( context.getPackageName(), 0 );
			return packInfo.versionCode;
		} catch ( PackageManager.NameNotFoundException e ) {
			e.printStackTrace();
		}

		return -1;
	}

	public static Account getUserAccount( Context context, @Nullable String accountType ) {
		if ( ActivityCompat.checkSelfPermission( context, Manifest.permission.GET_ACCOUNTS ) != PackageManager.PERMISSION_GRANTED ) {
			Account[] accounts;

			if ( accountType != null ) {
				accounts = AccountManager.get( context ).getAccountsByType( "com.google" );
			} else {
				accounts = AccountManager.get( context ).getAccounts();
			}

			if ( accounts.length > 0 ) {
				return accounts[0];
			}
		}

		return null;
	}

	public static ArrayList<String> getHashKeys( Context context ) {
		ArrayList<String> keys = new ArrayList<String>();

		//noinspection TryWithIdenticalCatches
		try {
			@SuppressLint("PackageManagerGetSignatures")
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_SIGNATURES );
			for ( Signature signature : info.signatures ) {
				MessageDigest md = MessageDigest.getInstance( "SHA" );
				md.update( signature.toByteArray() );
				keys.add( Base64.encodeToString( md.digest(), Base64.DEFAULT ) );
			}
		} catch ( PackageManager.NameNotFoundException ignore ) {
		} catch ( NoSuchAlgorithmException ignore ) {
		}

		return keys;
	}

	public static int[] listToArray( List<Integer> list ) {
		int[] result = new int[list.size()];
		for ( int i = 0; i < list.size(); i++ ) {
			result[i] = list.get( i );
		}

		//noinspection unchecked
		return result;
	}

	public static Class<? extends Activity> getActivityClass( Context context, String packageName ) {
		Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage( packageName );

		if ( launchIntent == null ) {
			return null;
		}

		String className = launchIntent.getComponent().getClassName();
		Class<? extends Activity> cls = null;
		//noinspection TryWithIdenticalCatches
		try {
			//noinspection unchecked
			cls = (Class<? extends Activity>)Class.forName( className );
		} catch ( ClassNotFoundException ignore ) {
		} catch ( ClassCastException ignore ) {
		}

		return cls;
	}

	public static Class<? extends Activity> getActivityClass( Context context ) {
		return getActivityClass( context, context.getPackageName() );
	}

	private static TelephonyManager mTelephonyManager = null;

	public static String getIMEI( Context context ) {
		if ( mTelephonyManager == null ) {
			mTelephonyManager = (TelephonyManager)context.getApplicationContext().getSystemService( Context.TELEPHONY_SERVICE );
		}

		try {
			return mTelephonyManager.getDeviceId();
		} catch ( SecurityException e ) {
			PCLog.print( Log.WARN, LOG_TAG, e.getMessage() );
			return "NEEDS READ_PHONE_STATE_PERMISSION";
		}
	}

}
