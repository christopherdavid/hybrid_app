package com.neatorobotics.android.slide.framework.robot.schedule;

import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes;

public class SchedulerConstants {

    public static final int SCHEDULE_TYPE_BASIC = 0;

    public static final int CLEANING_MODE_ECO = 1;
    public static final int CLEANING_MODE_NORMAL = 2;

    public static final int SERVER_SCHEDULE_TYPE_BASIC = 1;

    public static enum Day {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    };

    public static Day determineDay(int day) {

        if (day == Day.SUNDAY.ordinal())
            return Day.SUNDAY;
        if (day == Day.MONDAY.ordinal())
            return Day.MONDAY;
        if (day == Day.TUESDAY.ordinal())
            return Day.TUESDAY;
        if (day == Day.WEDNESDAY.ordinal())
            return Day.WEDNESDAY;
        if (day == Day.THURSDAY.ordinal())
            return Day.THURSDAY;
        if (day == Day.FRIDAY.ordinal())
            return Day.FRIDAY;
        if (day == Day.SATURDAY.ordinal())
            return Day.SATURDAY;

        return null;
    }

    public static String getScheduleType(int scheduleVal) {
        String scheduleType = null;
        if (scheduleVal == SchedulerConstants.SCHEDULE_TYPE_BASIC) {
            scheduleType = NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_BASIC;
        }
        return scheduleType;
    }

    public static int getScheduleIntType(String scheduleType) {
        int scheduleVal = -1;
        if (scheduleType.equals(NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_BASIC)) {
            scheduleVal = SchedulerConstants.SCHEDULE_TYPE_BASIC;
        }
        return scheduleVal;
    }

    public static Schedules getEmptySchedule(int type) {
        Schedules group = null;
        String uuid = AppUtils.generateScheduleUUId();
        if (type == SCHEDULE_TYPE_BASIC) {
            group = new BasicScheduleGroup(uuid);
        }
        return group;
    }

    public static int convertToServerConstants(int scheduleType) {
        int scheduleVal = -1;
        switch (scheduleType) {
            case SchedulerConstants.SCHEDULE_TYPE_BASIC:
                scheduleVal = SchedulerConstants.SERVER_SCHEDULE_TYPE_BASIC;
                break;
            default:
                break;
        }
        return scheduleVal;
    }
}
