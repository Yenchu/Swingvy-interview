package idv.attendance.model;

import idv.attendance.enums.AttendanceStatus;
import idv.attendance.util.DateTimeUtil;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(name = "attendance-breaks", attributeNodes = @NamedAttributeNode("breaks"))
public class Attendance {

    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "native"
    )
    @GenericGenerator(
            name = "native",
            strategy = "native"
    )
    private long attendanceId;

    private long userId;

    private long companyId;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime clockInTime;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime clockOutTime;

    private int activeTime;

    private int breakTime;

    @OneToMany(mappedBy = "attendance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Break> breaks;

    public AttendanceStatus getStatus() {
        if (isClockedOut()) {
            if (DateTimeUtil.isToday(clockInTime)) {
                return AttendanceStatus.CLOCKED_OUT;
            } else {
                return AttendanceStatus.BEFORE_CLOCK_IN;
            }
        } else {
            if (isOnBreak()) {
                return AttendanceStatus.ON_BREAK;
            } else {
                return AttendanceStatus.CLOCKED_IN;
            }
        }
    }

    public void addBreak(Break brk) {
        if (breaks == null) {
            breaks = new ArrayList<>();
        }
        breaks.add(brk);
    }

    public Optional<Break> getLatestBreak() {
        return breaks.stream().max(Comparator.comparing(Break::getStartTime));
    }

    public boolean isClockedOut() {
        return clockOutTime != null;
    }

    public boolean isOnBreak() {
        if (breaks == null || breaks.size() == 0) {
            return false;
        }

        for (Break brk : breaks) {
            if (brk.getEndTime() == null) {
                return true;
            }
        }
        return false;
    }

    public void setActiveAndBreakTime() {
        int clockTime = calculateClockTime();
        breakTime = calculateBreakTime();
        activeTime = clockTime - breakTime;
    }

    public int calculateBreakTime() {
        if (breaks == null || breaks.size() == 0) {
            return 0;
        }

        long breakTime = 0;
        for (Break brk : breaks) {
            OffsetDateTime endTime = brk.getEndTime() != null ? brk.getEndTime() : OffsetDateTime.now();
            breakTime += Duration.between(brk.getStartTime(), endTime).getSeconds();
        }
        return Math.toIntExact(breakTime);
    }

    public int calculateClockTime() {
        OffsetDateTime endTime = clockOutTime != null ? clockOutTime : OffsetDateTime.now();
        return Math.toIntExact(Duration.between(clockInTime, endTime).getSeconds());
    }

    public static class Property {
        public static final String CLOCK_IN_TIME = "clockInTime";
    }
}
