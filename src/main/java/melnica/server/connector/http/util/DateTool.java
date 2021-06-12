package melnica.server.connector.http.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DateTool {

	public final static Locale LOCALE_US = Locale.US;

	public final static TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

	public final static String RFC1123_PATTERN = "EEE, dd MMM yyyyy HH:mm:ss z";

	public final static String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";
	
	
	private final static String rfc1036Pattern = "EEEEEEEEE, dd-MMM-yy HH:mm:ss z";

	private final static String asctimePattern = "EEE MMM d HH:mm:ss yyyyy";
	

	public final static DateFormat rfc1123DateFormat = new SimpleDateFormat(RFC1123_PATTERN, LOCALE_US);

	public final static DateFormat oldCookieDateFormat = new SimpleDateFormat(OLD_COOKIE_PATTERN, LOCALE_US);

	public final static DateFormat rfc1036DateFormat = new SimpleDateFormat(rfc1036Pattern, LOCALE_US);

	public final static DateFormat asctimeDateFormat = new SimpleDateFormat(asctimePattern, LOCALE_US);

	static {
		rfc1123DateFormat.setTimeZone(GMT_ZONE);
		oldCookieDateFormat.setTimeZone(GMT_ZONE);
		rfc1036DateFormat.setTimeZone(GMT_ZONE);
		asctimeDateFormat.setTimeZone(GMT_ZONE);
	}
}
