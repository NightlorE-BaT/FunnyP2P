package by.mythsystems.funnyp2p;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

@SuppressWarnings({ "UnusedDeclaration", "ResultOfMethodCallIgnored" })
public class PCAppTracker {
	private static final String LINE_SEPARATOR = System.getProperty( "line.separator", "\n" );
	private static final String HISTORY_DATE_SEPARATOR = ": ";
	private static final String HISTORY_PARAM_SEPARATOR = "->";

	private static PCTrackerData mTrackerData;
	private static String mDateStringPattern;

	synchronized public static void prepare() {
		if ( mTrackerData != null )
			mTrackerData.clear();
		else
			mTrackerData = new PCTrackerData();
	}

	public static void setDateStringPattern( String formatPattern ) {
		mDateStringPattern = formatPattern;
	}

	private static class PCTrackInfo {
		private Date lDate;
		private String lCategory;
		private String lAction;
		private String lValue;

		public PCTrackInfo( String trackInfoString ) {
			int dateEndPos = trackInfoString.indexOf( HISTORY_DATE_SEPARATOR );
			String dateStr = trackInfoString.substring( 0, dateEndPos );
			String paramString = trackInfoString.substring( dateEndPos+HISTORY_DATE_SEPARATOR.length() );
			String[] paramSet = paramString.split( HISTORY_PARAM_SEPARATOR );

			init( PCDateUtils.stringToDate( dateStr ),
					paramSet[0],
					( paramSet.length > 1 ? paramSet[1] : null ),
					( paramSet.length > 2 ? paramSet[2] : null ) );
		}

		public PCTrackInfo( String category, String action, String value ) {
			init( new Date(), category, action, value );
		}

		private void init( Date date, String category, String action, String value ) {
			lDate = date;
			lCategory = category;
			lAction = action;
			lValue = value;
		}

		public Date getDate() {
			return lDate;
		}

		public String getDateString() {
			if ( mDateStringPattern == null )
				return PCDateUtils.dateToString( getDate() );
			else
				return PCDateUtils.dateToString( getDate(), mDateStringPattern );
		}

		public String getCategory() {
			return ( lCategory != null ? lCategory : "" );
		}

		public String getAction() {
			return ( lAction != null ? lAction : "" );
		}

		public String getValue() {
			return ( lValue != null ? lValue : "" );
		}

		@Override
		public String toString() {
			return ( getValue().length() > 0 ? String.format( "%s%s%s%s%s%s%s", getDateString(), HISTORY_DATE_SEPARATOR,
					getCategory(), HISTORY_PARAM_SEPARATOR, getAction(), HISTORY_PARAM_SEPARATOR, getValue() ) : String
					.format( "%s%s%s%s%s", getDateString(), HISTORY_DATE_SEPARATOR, getCategory(), HISTORY_PARAM_SEPARATOR,
							getAction() ) );
		}
	}

	private final static class PCTrackerData {
		@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
		private LinkedHashMap<Date, PCTrackInfo> lTracks = new LinkedHashMap<Date, PCTrackInfo>();
		private ArrayList<String> lHistory = new ArrayList<String>();

		public void add( PCTrackInfo track ) {
			lTracks.put( track.getDate(), track );
			lHistory.add( track.toString() );
		}

		public ArrayList<String> getDataHistory() {
			return lHistory;
		}

		public void clear() {
			lTracks.clear();
			lHistory.clear();
		}
	}

	synchronized public static void track( String category, String action, String value ) {
		if ( mTrackerData == null ) {
			prepare();
		}

		mTrackerData.add( new PCTrackInfo( category, action, value ) );
	}

	/**
	 * Track with context id as Category
	 *
	 * @param context the {@link Context}.
	 * @param action  the action.
	 * @param value   the value.
	 */
	public static void track( Context context, String action, String value ) {
		track( ( context != null ? context.toString().replace( context.getClass().getName(), "" ) : "CONTEXT_NULL!" ),
				action, value );
	}

	public static ArrayList<String> getHistory() {
		return mTrackerData.getDataHistory();
	}

	synchronized public static String getHistoryAsString() {
		String history = "";
		final PCTrackerData savedData = mTrackerData;

		for ( String historyRow : savedData.getDataHistory() ) {
			history = history.concat( historyRow ).concat( LINE_SEPARATOR );
		}

		return history;
	}

	synchronized public static boolean saveHistoryFile( String filePath, boolean append ) throws IOException {
		File file = new File( filePath );
		FileWriter hfWriter = null;
		final PCTrackerData savedData = mTrackerData;

		try {
			file.getParentFile().mkdirs();

			if ( !file.exists() ) {
				file.createNewFile();
			}

			hfWriter = new FileWriter( file, append );

			for ( String track : savedData.getDataHistory() ) {
				hfWriter.append( String.format( "%s%s", track, LINE_SEPARATOR ) );
			}

			hfWriter.flush();
		} finally {
			if ( hfWriter != null ) {
				try {
					hfWriter.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	synchronized public static boolean loadHistoryFile( String filePath, boolean deleteFile ) {
		File file = new File( filePath );

		if ( !file.exists() )
			return false;

		if ( mTrackerData == null )
			prepare();

		FileReader hfReader = null;
		BufferedReader bufferedReader = null;

		try {
			hfReader = new FileReader( file );
			bufferedReader = new BufferedReader( hfReader );
			String line;

			while ( ( line = bufferedReader.readLine() ) != null ) {
				if ( line.isEmpty() || !line.contains( HISTORY_DATE_SEPARATOR ) || !line.contains( HISTORY_PARAM_SEPARATOR ) )
					continue;

				mTrackerData.add( new PCTrackInfo( line ) );
			}

			return true;
		} catch ( IOException e ) {
			e.printStackTrace();
		} finally {
			if ( bufferedReader != null )
				try {
					bufferedReader.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}

			if ( hfReader != null )
				try {
					hfReader.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}

			if ( deleteFile )
				file.delete();
		}

		return false;
	}
}
