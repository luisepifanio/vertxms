package ar.com.phostech.loging.formatter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


class ApplicationFormatter extends Formatter {

    public ApplicationFormatter() {
        System.out.println("Instanciando ApplicationFormatter");
    }

    @Override
    public synchronized String format(LogRecord record) {

        // Date and time
        String formatedDate = formatIsoUtcDateTime(new Date(record.getMillis()));

        String className = Optional.ofNullable(record.getSourceClassName()).orElse(record.getLoggerName());

        String methodName = Optional.ofNullable(record.getSourceMethodName()).orElse("");

        String message = formatMessage(record);
        String level = record.getLevel().getName();

        StringBuilder sb = new StringBuilder().append(asKeyValueTag("eventdate", formatedDate))
                .append(asKeyValueTag("level", level)).append(asKeyValueTag("class", className))
                .append(asKeyValueTag("method", methodName)).append(" ").append(message).append("\n");

        if (record.getThrown() != null) {
            sb.append(throwableAsString(record.getThrown()));
        }

        System.out.print(sb.toString());
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
        return new StringBuilder().append("[").append(key).append(":").append(value).append("]").toString();
    }

    public String formatIsoUtcDateTime(Date date) {
        if (null == date) {
            return null;
        }
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("TEST");
        logger.setUseParentHandlers(false);

        Formatter formatter = new ApplicationFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);

        logger.addHandler(handler);

        logger.info("Example of creating custom formatter.");
        logger.warning("A warning message.");
        logger.severe("A severe message.");
    }

}
