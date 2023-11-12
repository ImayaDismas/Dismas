package inc.smart.solutions.dismas.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    // This class should not be initialized
    private DateUtils() {

    }

    /**
     * Formats timestamp to 'month day, year | hour:minute' format (e.g. 'Feb 3, 2019 | 04:00 AM').
     */
    public static String formatMonthDayYearTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy | hh:mm a", Locale.getDefault());
        return formatter.format(date).toUpperCase();
    }
}
