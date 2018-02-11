package ar.com.phostech.loging.formatter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


public class ApplicationFormatter extends Formatter {

    public ApplicationFormatter() {}

    @Override
    public synchronized String format(LogRecord record) {

        // Date and time
        String formatedDate = fromMillis(record.getMillis()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        // String formatedDate = formatIsoUtcDateTime(new Date(record.getMillis()));

        String className = Optional.ofNullable(record.getSourceClassName()).orElse(record.getLoggerName());

        String methodName = Optional.ofNullable(record.getSourceMethodName()).orElse("");

        String message = formatMessage(record);
        String level = record.getLevel().getName();

        StringBuilder sb = new StringBuilder()
            .append(asKeyValueTag("eventdate", formatedDate))
            .append(asKeyValueTag("level", level))
            .append(asKeyValueTag("class", className))
            .append(asKeyValueTag("method", methodName))
            .append(" ").append(message)
            .append("\n");

        if (record.getThrown() != null) {
            sb.append(throwableAsString(record.getThrown()));
        }

        return sb.toString();
    }

    public String throwableAsString(Throwable throwable) {
        if (null == throwable) {
            return "";
        }

        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        } catch (IOException ioe) {
        } //
        return "";
    }

    public String asKeyValueTag(String key, String value) {
        if (null == key || null == value) {
            return "";
        }
        if(0 == value.length()){
            return "";
        }
        return new StringBuilder().append("[").append(key).append(":").append(value).append("]").toString();
    }

    public String formatIsoUtcDateTime(Date date) {
        if (null == date) {
            return null;
        }
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
    }

    private static OffsetDateTime fromMillis(long epochMillis) {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
      }
}
