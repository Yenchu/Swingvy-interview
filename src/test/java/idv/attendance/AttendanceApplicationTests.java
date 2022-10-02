package idv.attendance;

import idv.attendance.dto.*;
import idv.attendance.repository.AttendanceRepository;
import idv.attendance.service.AttendanceService;
import idv.attendance.service.TimesheetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.List;

import static java.lang.System.out;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AttendanceApplicationTests {

    private final long companyId = 1;
    private final long userId = 2;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private TimesheetService timesheetService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Test
    void clockIn() {
        AttendanceDTO attendance = attendanceService.clockIn(companyId, userId);
        assertThat(attendance.getAttendanceId()).isPositive();
        assertThat(attendance.getClockInTime()).isPositive();
        assertThat(attendance.getClockOutTime()).isZero();
    }

    @Test
    void breakAndResume() {
        attendanceService.clockIn(companyId, userId);
        AttendanceDTO attendance = attendanceService.takeBreak(userId);
        List<BreakDTO> breaks = attendance.getBreaks();
        assertThat(breaks.size()).isEqualTo(1);

        BreakDTO brk = breaks.get(0);
        assertThat(brk.getStartTime()).isPositive();
        assertThat(brk.getEndTime()).isZero();

        attendance = attendanceService.resume(userId);
        breaks = attendance.getBreaks();
        brk = breaks.get(0);
        assertThat(brk.getEndTime()).isPositive();
    }

    @Test
    void clockOut() {
        attendanceService.clockIn(companyId, userId);
        AttendanceDTO attendance = attendanceService.clockOut(userId);
        assertThat(attendance.getClockOutTime()).isPositive();
    }

    @Test
    void findTimesheet() {
        String startDate = "08/09/2022";
        String endDate = "10/09/2022";
        for (int page = 0; page < 2; page++) {
            TimesheetRequest req = TimesheetRequest.builder().page(page).startDate(startDate).endDate(endDate).build();

            PageDTO<TimesheetRecord> tsPage = timesheetService.findTimesheet(companyId, req);
            List<TimesheetRecord> records = tsPage.getContent();
            if (CollectionUtils.isEmpty(records)) {
                return;
            }

            for (TimesheetRecord record : records) {
                assertThat(record.getClockInDate().compareTo(startDate)).isGreaterThanOrEqualTo(0);
                assertThat(record.getClockInDate().compareTo(endDate)).isLessThan(0);
            }
        }
    }

    @Test
    void pageBySql() {
        OffsetDateTime startDate = OffsetDateTime.of(2022, 9, 8, 0, 0, 0, 0, OffsetDateTime.now().getOffset());
        OffsetDateTime endDate = OffsetDateTime.of(2022, 9, 10, 0, 0, 0, 0, OffsetDateTime.now().getOffset());
        int limit = 2;
        for (int page = 0; page < 2; page++) {
            List<AttendanceBreak> records = attendanceRepository.pageBySql(companyId, startDate, endDate, limit, page * limit);
            if (CollectionUtils.isEmpty(records)) {
                return;
            }
            List<TimesheetRecord> trs = AttendanceBreak.toTimesheetRecord(records);
            trs.forEach(tr -> out.println("timesheet: " + tr));
        }
    }
}
