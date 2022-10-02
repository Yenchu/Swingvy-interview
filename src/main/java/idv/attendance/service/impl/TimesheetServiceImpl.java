package idv.attendance.service.impl;

import idv.attendance.dto.*;
import idv.attendance.model.Attendance;
import idv.attendance.repository.AttendanceRepository;
import idv.attendance.service.TimesheetService;
import idv.attendance.service.UserService;
import idv.attendance.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimesheetServiceImpl implements TimesheetService {

    private static final int DEFAULT_PAGE_SIZE = 2;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, Attendance.Property.CLOCK_IN_TIME);

    private final AttendanceRepository attendanceRepository;

    private final UserService userService;

    @Transactional(readOnly = true)
    @Override
    public PageDTO<TimesheetRecord> findTimesheet(long companyId, TimesheetRequest timesheetRequest) {
        OffsetDateTime startDate;
        OffsetDateTime endDate;
        try {
            if (StringUtils.isEmpty(timesheetRequest.getStartDate())) {
                startDate = DateTimeUtil.getStartOfDay(timesheetRequest.getTimeZoneOffset());
            } else {
                startDate = DateTimeUtil.parseDateAtZoneOffset(timesheetRequest.getStartDate(), timesheetRequest.getTimeZoneOffset());
            }

            if (StringUtils.isEmpty(timesheetRequest.getEndDate())) {
                endDate = startDate.plusDays(1);
            } else {
                endDate = DateTimeUtil.parseDateAtZoneOffset(timesheetRequest.getEndDate(), timesheetRequest.getTimeZoneOffset());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        if (startDate.isAfter(endDate) || startDate.isEqual(endDate)) {
            throw new IllegalArgumentException(String.format("Start date %s must be before end date %s", startDate, endDate));
        }
        log.info("timesheet query startDate: {}, endDate: {}", startDate, endDate);

        PageRequest pageRequest = PageRequest.of(timesheetRequest.getPage(), DEFAULT_PAGE_SIZE, DEFAULT_SORT);
        Page<Attendance> attendancesPage = findTimesheet(companyId, startDate, endDate, pageRequest);

        List<Attendance> attendances = attendancesPage.getContent();
        if (CollectionUtils.isEmpty(attendances)) {
            return PageDTO.<TimesheetRecord>builder().content(Collections.emptyList()).build();
        }

        Set<Long> userIds = attendances.stream()
                .map(Attendance::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserNameDTO> users = userService.findNameByUserIds(userIds);

        List<TimesheetRecord> records = attendances.stream()
                .map(atte -> toTimesheetRecord(atte, users))
                .collect(Collectors.toList());

        return PageDTO.<TimesheetRecord>builder()
                .content(records)
                .page(attendancesPage.getNumber())
                .size(attendancesPage.getSize())
                .total(attendancesPage.getTotalElements())
                .build();
    }

    private Page<Attendance> findTimesheet(long companyId, OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable) {
        Page<AttendanceId> attendanceIds = attendanceRepository.findIdByCompanyIdAndClockInTimeBetween(companyId, startDate, endDate, pageable);
        if (CollectionUtils.isEmpty(attendanceIds.getContent())) {
            return new PageImpl<>(Collections.emptyList());
        }

        List<Attendance> attendances = attendanceRepository.findByAttendanceIdIn(attendanceIds.getContent()
                .stream()
                .map(AttendanceId::getAttendanceId)
                .collect(Collectors.toSet()), pageable.getSort());
        return new PageImpl<>(attendances, pageable, attendanceIds.getTotalElements());
    }

    private TimesheetRecord toTimesheetRecord(Attendance attendance, Map<Long, UserNameDTO> users) {
        UserNameDTO user = users.get(attendance.getUserId());

        String username;
        if (StringUtils.isEmpty(user)) {
            log.error("can not find user name of user id {}", attendance.getUserId());
            username = "";
        } else {
            username = user.getName();
        }
        return TimesheetRecord.of(username, attendance);
    }
}
