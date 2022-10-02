package idv.attendance.dto;

import idv.attendance.enums.AttendanceStatus;
import idv.attendance.model.Attendance;
import idv.attendance.util.DateTimeUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class AttendanceDTO {

    protected AttendanceStatus attendanceStatus;
    protected long attendanceId;
    protected long clockInTime;
    protected long clockOutTime;
    protected String clockInTimeStr;
    protected String clockOutTimeStr;
    protected int activeTimeSeconds;
    protected int breakTimeSeconds;
    protected String activeTimeStr;
    protected String breakTimeStr;
    protected List<BreakDTO> breaks;

    public static AttendanceDTO of(Attendance attendance) {
        return of(attendance, attendance.getStatus());
    }

    public static AttendanceDTO of(Attendance attendance, AttendanceStatus status) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.set(attendance, status);
        return dto;
    }

    public void set(Attendance attendance, AttendanceStatus status) {
        attendanceStatus = status;
        attendanceId = attendance.getAttendanceId();

        clockInTime = attendance.getClockInTime() != null ? attendance.getClockInTime().toEpochSecond() : 0;
        clockOutTime = attendance.getClockOutTime() != null ? attendance.getClockOutTime().toEpochSecond() : 0;
        clockInTimeStr = DateTimeUtil.formatClockTime(attendance.getClockInTime());
        clockOutTimeStr = DateTimeUtil.formatClockTime(attendance.getClockOutTime());

        if (status == AttendanceStatus.CLOCKED_IN || status == AttendanceStatus.ON_BREAK) {
            int clockTime = attendance.calculateClockTime();
            int breakTime = attendance.calculateBreakTime();
            activeTimeSeconds = clockTime - breakTime;
            breakTimeSeconds = breakTime;
        } else {
            activeTimeSeconds = attendance.getActiveTime();
            breakTimeSeconds = attendance.getBreakTime();
        }

        activeTimeStr = DateTimeUtil.toHourMinutes(activeTimeSeconds);
        breakTimeStr = DateTimeUtil.toHourMinutes(breakTimeSeconds);

        breaks = BreakDTO.of(attendance.getBreaks());
    }
}
