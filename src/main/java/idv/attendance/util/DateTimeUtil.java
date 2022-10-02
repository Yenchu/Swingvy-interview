package idv.attendance.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter CLOCK_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a").withLocale(Locale.ENGLISH);

    public static boolean isToday(OffsetDateTime time) {
        return OffsetDateTime.now(time.getOffset()).toLocalDate().equals(time.toLocalDate());
    }

    public static OffsetDateTime getStartOfDay() {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
    }

    public static OffsetDateTime getStartOfDay(String zoneOffsetStr) {
        if (zoneOffsetStr == null || zoneOffsetStr.length() == 0) {
            return getStartOfDay();
        }
        ZoneOffset zoneOffset = parseZoneOffset(zoneOffsetStr);
        return LocalDate.now().atStartOfDay(zoneOffset).toOffsetDateTime();
    }

    public static ZoneOffset parseZoneOffset(String timeZoneOffset) {
        int offsetMinutes = Integer.parseInt(timeZoneOffset);
        return ZoneOffset.ofTotalSeconds(offsetMinutes * -60);
    }

    public static OffsetDateTime parseDate(String dateStr) {
        return parseDate(dateStr, ZoneId.systemDefault());
    }

    public static OffsetDateTime parseDate(String dateStr, ZoneId zoneId) {
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
        return date.atStartOfDay(zoneId).toOffsetDateTime();
    }

    public static OffsetDateTime parseDate(String dateStr, ZoneOffset zoneOffset) {
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
        return date.atStartOfDay().atOffset(zoneOffset);
    }

    public static OffsetDateTime parseDateAtZoneOffset(String dateStr, String zoneOffsetStr) {
        if (zoneOffsetStr == null || zoneOffsetStr.length() == 0) {
            return parseDate(dateStr);
        }
        ZoneOffset zoneOffset = parseZoneOffset(zoneOffsetStr);
        return parseDate(dateStr, zoneOffset);
    }

    public static String formatDate(OffsetDateTime time) {
        return time != null ? time.format(DATE_FORMAT) : "";
    }

    public static String formatDate(Instant time) {
        return time != null ? formatDate(OffsetDateTime.ofInstant(time, ZoneId.systemDefault())) : "";
    }

    public static String formatClockTime(OffsetDateTime time) {
        return time != null ? time.format(CLOCK_TIME_FORMAT).toLowerCase() : "";
    }

    public static String formatClockTime(Instant time) {
        return time != null ? formatClockTime(OffsetDateTime.ofInstant(time, ZoneId.systemDefault())) : "";
    }

    public static String toHourMinutes(long seconds) {
        if (seconds <= 0) {
            return "";
        }

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h");
        }
        if (minutes > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(minutes).append("m");
        }
        return sb.toString();
    }
}
