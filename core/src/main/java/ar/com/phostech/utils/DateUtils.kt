package ar.com.phostech.utils

import java.time.*
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.util.*


/**
 * @author luis
 * Date 02/04/18 13:02
 * Project: vertxms
 */
object DateUtils {

    private val utcZone = ZoneId.of("UTC").normalized()

    fun toInstant(calendar: Calendar): Instant = calendar.toInstant()

    fun parseToInstant(datetime: String): Instant {
        return ISO_DATE_TIME.parse(datetime, Instant::from)
    }

    fun zonedDateTimeToZone(zonedDateTime: ZonedDateTime, zoneId: ZoneId): String =
        zonedDateTime
            .withZoneSameInstant(zoneId.normalized())
            .format(ISO_DATE_TIME)

    fun zonedDateTimeToUTC(zonedDateTime: ZonedDateTime): String = zonedDateTimeToZone(zonedDateTime, utcZone)

    fun offsetDateTimeToZone(offsetDateTime: OffsetDateTime, zoneOffset: ZoneOffset): String =
        offsetDateTime
            .withOffsetSameInstant(zoneOffset)
            .format(ISO_DATE_TIME)

    fun offsetDateTimeToUTC(offsetDateTime: OffsetDateTime): String =
        offsetDateTimeToZone(offsetDateTime, ZoneOffset.UTC)

    fun instantOnUTC(instant: Instant): String =
        ZonedDateTime.ofInstant(instant, utcZone)
            .format(ISO_DATE_TIME)

    fun toOffsetDateTime(calendar: Calendar): OffsetDateTime = internalConverter(calendar, OffsetDateTime::ofInstant)

    fun toZonedDateTime(calendar: Calendar): ZonedDateTime = internalConverter(calendar, ZonedDateTime::ofInstant)

    fun toLocalDateTime(calendar: Calendar): LocalDateTime = internalConverter(calendar, LocalDateTime::ofInstant)

    private inline fun <T> internalConverter(
        calendar: Calendar,
        converter: (instant: Instant, zone: ZoneId) -> T
    ): T {
        // @Smell & WARN : Check behaviour on null TimeZone
        val tz = calendar.timeZone
        val zid = if (tz == null) ZoneId.systemDefault() else tz.toZoneId()
        return converter.invoke(calendar.toInstant(), zid)
    }

    fun formatDateOnZone(date: LocalDateTime, offset: ZoneOffset): String = OffsetDateTime.of(date, offset)
        .format(ISO_DATE_TIME)

}
