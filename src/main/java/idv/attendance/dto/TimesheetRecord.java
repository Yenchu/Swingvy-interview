package idv.attendance.dto;

import idv.attendance.enums.AttendanceStatus;
import idv.attendance.model.Attendance;
import idv.attendance.util.DateTimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
public class TimesheetRecord extends AttendanceDTO {

    private String username;

    private String clockInDate;

    public static TimesheetRecord of(String username, Attendance attendance) {
        TimesheetRecord dto = new TimesheetRecord();
        dto.username = username;
        dto.set(attendance, attendance.getStatus());
        return dto;
    }

    @Override
    public void set(Attendance attendance, AttendanceStatus status) {
        super.set(attendance, status);
        clockInDate = DateTimeUtil.formatDate(attendance.getClockInTime());
    }
}
