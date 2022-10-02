package idv.attendance.service;

import idv.attendance.dto.AttendanceDTO;

public interface AttendanceService {

    AttendanceDTO getCurrentAttendance(long userId);

    AttendanceDTO clockIn(long companyId, long userId);

    AttendanceDTO clockOut(long userId);

    AttendanceDTO takeBreak(long userId);

    AttendanceDTO resume(long userId);

}
