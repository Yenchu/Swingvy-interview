package idv.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimesheetRequest {
    @Min(0)
    @Max(1000000)
    private int page;
    private String startDate;
    private String endDate;
    private String timeZoneOffset;
}
