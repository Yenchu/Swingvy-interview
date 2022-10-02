package idv.attendance.service;

import idv.attendance.dto.PageDTO;
import idv.attendance.dto.TimesheetRecord;
import idv.attendance.dto.TimesheetRequest;

public interface TimesheetService {
    PageDTO<TimesheetRecord> findTimesheet(long companyId, TimesheetRequest timesheetRequest);
}