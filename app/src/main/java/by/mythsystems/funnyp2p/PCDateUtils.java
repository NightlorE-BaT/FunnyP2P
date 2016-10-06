package by.mythsystems.funnyp2p;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PCDateUtils {

	public static Date stringToDate( String dateString, String pattern ) {
		if ( dateString == null || dateString.isEmpty() ) {
			return null;
		}

		SimpleDateFormat format = new SimpleDateFormat( pattern, Locale.ENGLISH );

		try {
			return format.parse( dateString );
		} catch ( ParseException e ) {
			e.printStackTrace();
		}

		return null;
	}

	public static Date stringToDate( String dateString ) {
		return stringToDate( dateString, Constants.DATE_STRING_FORMAT_PATTERN );
	}

	public static String dateToString( Date date, String pattern ) {
		return new SimpleDateFormat( pattern, Locale.ENGLISH ).format( date );
	}

	public static String dateToString( Date date ) {
		return dateToString( date, Constants.DATE_STRING_FORMAT_PATTERN );
	}

	public static long millisSinceDate(Date date, TimeZone timezone) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone( timezone );
		return calendar.getTime().getTime() - date.getTime();
	}

}
