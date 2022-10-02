package idv.attendance.dto;

import idv.attendance.util.DateTimeUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface AttendanceBreak {
    long getAttendanceId();

    long getCompanyId();

    long getUserId();

    // OffsetDateTime is not supported by Projection, it would throw java.lang.IllegalArgumentException: Projection type must be an interface!
    Instant getClockInTime();

    Instant getClockOutTime();

    int getActiveTime();

    int getBreakTime();

    Instant getStartTime();

    Instant getEndTime();

    static TimesheetRecord toTimesheetRecord(AttendanceBreak ab) {
        // Note: activeTime and breakTime of today attendance are not calculated
        TimesheetRecord tr = TimesheetRecord.builder()
                .attendanceId(ab.getAttendanceId())
                .clockInDate(DateTimeUtil.formatDate(ab.getClockInTime()))
                .clockInTime(ab.getClockInTime() != null ? ab.getClockInTime().getEpochSecond() : 0)
                .clockOutTime(ab.getClockOutTime() != null ? ab.getClockOutTime().getEpochSecond() : 0)
                .clockInTimeStr(DateTimeUtil.formatClockTime(ab.getClockInTime()))
                .clockOutTimeStr(DateTimeUtil.formatClockTime(ab.getClockOutTime()))
                .activeTimeSeconds(ab.getActiveTime())
                .breakTimeSeconds(ab.getBreakTime())
                .activeTimeStr(DateTimeUtil.toHourMinutes(ab.getActiveTime()))
                .breakTimeStr(DateTimeUtil.toHourMinutes(ab.getBreakTime()))
                .build();

        if (ab.getStartTime() != null) {
            List<BreakDTO> breaks = new ArrayList<>();
            breaks.add(toBreakDTO(ab));
            tr.setBreaks(breaks);
        }
        return tr;
    }

    static BreakDTO toBreakDTO(AttendanceBreak ab) {
        return BreakDTO.builder()
                .startTime(ab.getStartTime() != null ? ab.getStartTime().getEpochSecond() : 0)
                .endTime(ab.getEndTime() != null ? ab.getEndTime().getEpochSecond() : 0)
                .startTimeStr(DateTimeUtil.formatClockTime(ab.getStartTime()))
                .endTimeStr(DateTimeUtil.formatClockTime(ab.getEndTime()))
                .build();
    }

    static void addBreakDTO(TimesheetRecord tr, AttendanceBreak ab) {
        if (ab.getStartTime() == null) {
            return;
        }

        BreakDTO brk = toBreakDTO(ab);
        if (tr.getBreaks() == null) {
            List<BreakDTO> breaks = new ArrayList<>();
            breaks.add(brk);
            tr.setBreaks(breaks);
        } else {
            tr.getBreaks().add(brk);
        }
    }

    static List<TimesheetRecord> toTimesheetRecord(List<AttendanceBreak> abs) {
        Map<Long, TimesheetRecord> trMap = new LinkedHashMap<>();
        for (AttendanceBreak ab : abs) {
            TimesheetRecord tr;
            if (trMap.containsKey(ab.getAttendanceId())) {
                tr = trMap.get(ab.getAttendanceId());
                addBreakDTO(tr, ab);
            } else {
                tr = toTimesheetRecord(ab);
                trMap.put(tr.getAttendanceId(), tr);
            }
        }
        return new ArrayList<>(trMap.values());
    }
}
