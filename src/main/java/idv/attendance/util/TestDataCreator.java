package idv.attendance.util;

import idv.attendance.model.Attendance;
import idv.attendance.model.Break;
import idv.attendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class TestDataCreator {

    final Random random = new Random();

    final AttendanceRepository attendanceRepository;

    @Transactional
    public void createAttendances() {
        final long company1Id = 1;
        final long company2Id = 2;
        final long admin1Id = 1;
        final long user1Id = 2;
        final long user4Id = 6;

        createAttendance(company1Id, admin1Id, 2022, 9, 8, 10, 0);
        createAttendance(company1Id, user1Id, 2022, 9, 8, 7, 30);
        createAttendance(company1Id, user1Id, 2022, 9, 9, 9, 27);
        createAttendance(company2Id, user4Id, 2022, 9, 8, 9, 31);
    }

    private void createAttendance(long companyId, long userId, int year, int month, int day, int hour, int minute) {
        ZoneOffset systemOffset = OffsetDateTime.now().getOffset();
        OffsetDateTime clockInTime = OffsetDateTime.of(year, month, day, hour, minute, 0, 0, systemOffset);
        OffsetDateTime clockOutTime = clockInTime.plusHours(random.nextInt(3) + 9).plusMinutes(random.nextInt(60));

        Attendance attendance = Attendance.builder()
                .companyId(companyId)
                .userId(userId)
                .clockInTime(clockInTime)
                .clockOutTime(clockOutTime)
                .build();

        createBreaks(attendance);

        attendance.setActiveAndBreakTime();
        attendanceRepository.save(attendance);
    }

    private void createBreaks(Attendance attendance) {
        int brkNumb = random.nextInt(4);
        if (brkNumb == 0) {
            return;
        }

        for (int i = 0; i < brkNumb; i++) {
            OffsetDateTime breakStartTime = attendance.getClockInTime().plusHours(random.nextInt(3) + 2);
            OffsetDateTime breakEndTime = breakStartTime.plusMinutes(random.nextInt(91));

            attendance.addBreak(Break.builder()
                    .attendance(attendance)
                    .companyId(attendance.getCompanyId())
                    .userId(attendance.getUserId())
                    .startTime(breakStartTime)
                    .endTime(breakEndTime)
                    .build());
        }
    }
}
