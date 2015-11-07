package aks.geotrends.android.utils;

import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Created by a77kumar on 2015-11-01.
 */
public class DurationFormatter {

    private static DurationFormatter instance;

    private final PeriodFormatter fweeks;
    private final PeriodFormatter fdays;
    private final PeriodFormatter fhours;
    private final PeriodFormatter fmins;
    private final PeriodFormatter fseconds;

    private DurationFormatter() {
        fweeks = new PeriodFormatterBuilder().appendWeeks().appendSuffix("w").toFormatter();
        fdays = new PeriodFormatterBuilder().appendDays().appendSuffix("d").toFormatter();
        fhours = new PeriodFormatterBuilder().appendHours().appendSuffix("h").toFormatter();
        fmins = new PeriodFormatterBuilder().appendMinutes().appendSuffix("m").toFormatter();
        fseconds = new PeriodFormatterBuilder().appendSeconds().appendSuffix("s").toFormatter();
    }

    public static DurationFormatter getInstance() {
        if (null == instance) {
            instance = new DurationFormatter();
        }

        return instance;
    }

    public String formatInterval(Interval interval) {
        final Duration duration = interval.toDuration();
        final Period period = interval.toPeriod();

        final Weeks weeks = Weeks.weeksIn(interval);
        final Days days = Days.daysIn(interval);
        final Hours hours = Hours.hoursIn(interval);
        final Minutes minutes = Minutes.minutesIn(interval);
        final Seconds seconds = Seconds.secondsIn(interval);

        if (weeks.getWeeks() != 0) {
            return fweeks.print(period);
        } else if (weeks.getWeeks() == 0 && days.getDays() != 0) {
            return fdays.print(period);
        } else if (days.getDays() == 0 && hours.getHours() != 0) {
            return fhours.print(period);
        } else if (hours.getHours() == 0 && minutes.getMinutes() != 0) {
            return fmins.print(period);
        } else if (minutes.getMinutes() == 0 && seconds.getSeconds() > 10) {
            return fseconds.print(period);
        } else {
            return "just now";
        }
    }
}
