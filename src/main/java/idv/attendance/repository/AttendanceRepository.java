package idv.attendance.repository;

import idv.attendance.dto.AttendanceBreak;
import idv.attendance.dto.AttendanceId;
import idv.attendance.model.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @EntityGraph(value = "attendance-breaks", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Attendance> findTopByUserIdOrderByClockInTimeDesc(long userId);

    @EntityGraph(value = "attendance-breaks", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Attendance> findTopByUserIdAndClockInTimeGreaterThanEqualOrderByClockInTimeDesc(long userId, OffsetDateTime clockInTime);

    Page<AttendanceId> findIdByCompanyIdAndClockInTimeBetween(long companyId, OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable);

    @EntityGraph(value = "attendance-breaks", type = EntityGraph.EntityGraphType.LOAD)
    List<Attendance> findByAttendanceIdIn(Set<Long> attendanceIds, Sort sort);

    @Query(
            value = "SELECT a.attendance_id as attendanceId, a.company_id as companyId, a.user_id as userId,"
                    + " a.clock_in_time as clockInTime, a.clock_out_time as clockOutTime, a.active_time as activeTime, a.break_time as breakTime,"
                    + " b.start_time as startTime, b.end_time as endTime"
                    + " FROM attendance a LEFT OUTER JOIN break b ON a.attendance_id = b.attendance_id WHERE a.attendance_id IN"
                    + " (SELECT attendance_id from attendance WHERE company_id = :companyId AND (clock_in_time BETWEEN :startDate AND :endDate)"
                    + " ORDER BY clock_in_time DESC LIMIT :limit OFFSET :offset)",
            nativeQuery = true
    )
    List<AttendanceBreak> pageBySql(long companyId, OffsetDateTime startDate, OffsetDateTime endDate, int limit, int offset);
}
