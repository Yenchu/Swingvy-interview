package idv.attendance.service.impl;

import idv.attendance.dto.AttendanceDTO;
import idv.attendance.enums.AttendanceStatus;
import idv.attendance.model.Attendance;
import idv.attendance.model.Break;
import idv.attendance.repository.AttendanceRepository;
import idv.attendance.service.AttendanceService;
import idv.attendance.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Transactional(readOnly = true)
    @Override
    public AttendanceDTO getCurrentAttendance(long userId) {
        Optional<Attendance> attendanceOpt = attendanceRepository.findTopByUserIdOrderByClockInTimeDesc(userId);
        if (attendanceOpt.isEmpty()) {
            return AttendanceDTO.builder().attendanceStatus(AttendanceStatus.BEFORE_CLOCK_IN).build();
        }
        return AttendanceDTO.of(attendanceOpt.get());
    }

    @Transactional
    @Override
    public AttendanceDTO clockIn(long companyId, long userId) {
        Optional<Attendance> attendanceOpt = getTodayLatestAttendance(userId);
        if (attendanceOpt.isPresent()) {
            Attendance attendance = attendanceOpt.get();
            if (!attendance.isClockedOut()) {
                throw new IllegalArgumentException(String.format("User %s has not clocked out.", userId));
            }

            if (attendance.isOnBreak()) {
                throw new IllegalArgumentException(String.format("User %s is on break.", userId));
            }
        }

        Attendance attendance = Attendance.builder()
                .companyId(companyId)
                .userId(userId)
                .clockInTime(OffsetDateTime.now())
                .build();
        attendance = attendanceRepository.save(attendance);
        log.info("clocked in: {}", attendance);
        return AttendanceDTO.of(attendance, AttendanceStatus.CLOCKED_IN);
    }

    @Transactional
    @Override
    public AttendanceDTO clockOut(long userId) {
        Optional<Attendance> attendanceOpt = getTodayLatestAttendance(userId);
        if (attendanceOpt.isEmpty()) {
            throw new IllegalArgumentException(String.format("User %s has not clocked in.", userId));
        }

        Attendance attendance = attendanceOpt.get();
        if (attendance.isClockedOut()) {
            throw new IllegalArgumentException(String.format("User %s has clocked out.", userId));
        }

        if (attendance.isOnBreak()) {
            throw new IllegalArgumentException(String.format("User %s is on break.", userId));
        }

        attendance.setClockOutTime(OffsetDateTime.now());
        attendance.setActiveAndBreakTime();
        attendance = attendanceRepository.save(attendance);
        return AttendanceDTO.of(attendance, AttendanceStatus.CLOCKED_OUT);
    }

    @Transactional
    @Override
    public AttendanceDTO takeBreak(long userId) {
        Optional<Attendance> attendanceOpt = getTodayLatestAttendance(userId);
        if (attendanceOpt.isEmpty()) {
            throw new IllegalArgumentException(String.format("User %s has not clocked in.", userId));
        }

        Attendance attendance = attendanceOpt.get();
        if (attendance.isClockedOut()) {
            throw new IllegalArgumentException(String.format("User %s has clocked out.", userId));
        }

        if (attendance.isOnBreak()) {
            throw new IllegalArgumentException(String.format("User %s is on break.", userId));
        }

        Break brk = Break.builder()
                .attendance(attendance)
                .companyId(attendance.getCompanyId())
                .userId(attendance.getUserId())
                .startTime(OffsetDateTime.now())
                .build();
        attendance.addBreak(brk);
        attendance = attendanceRepository.save(attendance);
        return AttendanceDTO.of(attendance, AttendanceStatus.ON_BREAK);
    }

    @Transactional
    @Override
    public AttendanceDTO resume(long userId) {
        Optional<Attendance> attendanceOpt = getTodayLatestAttendance(userId);
        if (attendanceOpt.isEmpty()) {
            throw new IllegalArgumentException(String.format("User %s has not clocked in.", userId));
        }

        Attendance attendance = attendanceOpt.get();
        if (attendance.isClockedOut()) {
            throw new IllegalArgumentException(String.format("User %s has clocked out.", userId));
        }

        Optional<Break> breakOpt = attendance.getLatestBreak();
        if (!attendance.isOnBreak() || breakOpt.isEmpty()) {
            throw new IllegalArgumentException(String.format("User %s is not on break.", userId));
        }

        Break brk = breakOpt.get();
        brk.setEndTime(OffsetDateTime.now());
        attendance = attendanceRepository.save(attendance);
        return AttendanceDTO.of(attendance, AttendanceStatus.CLOCKED_IN);
    }

    private Optional<Attendance> getTodayLatestAttendance(long userId) {
        OffsetDateTime startOfDay = DateTimeUtil.getStartOfDay();
        return attendanceRepository.findTopByUserIdAndClockInTimeGreaterThanEqualOrderByClockInTimeDesc(userId, startOfDay);
    }
}
