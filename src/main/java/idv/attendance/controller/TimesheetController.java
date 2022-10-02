package idv.attendance.controller;

import idv.attendance.configuration.security.CustomUserDetails;
import idv.attendance.dto.PageDTO;
import idv.attendance.dto.TimesheetRecord;
import idv.attendance.dto.TimesheetRequest;
import idv.attendance.service.TimesheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Attendance", description = "Time & Attendance API")
@RestController
@RequestMapping("timesheet")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TimesheetController {

    private final TimesheetService timesheetService;

    @Operation(summary = "Find timesheet", description = "Only for admin access", tags = {"Attendance"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Find timesheet success",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TimesheetRecord.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid user input")
    })
    @GetMapping(value = "page", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDTO<TimesheetRecord> findTimesheet(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  TimesheetRequest timesheetRequest) {
        log.info("timesheet request: {}", timesheetRequest);
        return timesheetService.findTimesheet(userDetails.getCompanyId(), timesheetRequest);
    }
}
