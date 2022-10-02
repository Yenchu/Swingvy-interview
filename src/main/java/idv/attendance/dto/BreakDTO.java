package idv.attendance.dto;

import idv.attendance.model.Break;
import idv.attendance.util.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreakDTO {

    private long startTime;
    private long endTime;
    private String startTimeStr;
    private String endTimeStr;

    public static List<BreakDTO> of(List<Break> breaks) {
        if (breaks == null || breaks.size() == 0) {
            return Collections.emptyList();
        }

        return breaks.stream()
                .map(brk -> BreakDTO.builder()
                        .startTime(brk.getStartTime() != null ? brk.getStartTime().toEpochSecond() : 0)
                        .endTime(brk.getEndTime() != null ? brk.getEndTime().toEpochSecond() : 0)
                        .startTimeStr(DateTimeUtil.formatClockTime(brk.getStartTime()))
                        .endTimeStr(DateTimeUtil.formatClockTime(brk.getEndTime()))
                        .build())
                .collect(Collectors.toList());
    }
}
