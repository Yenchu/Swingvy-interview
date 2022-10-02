package idv.attendance.controller;

import idv.attendance.configuration.security.CustomUserDetails;
import idv.attendance.dto.AttendanceDTO;
import idv.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Attendance", description = "Time & Attendance API")
@RestController
@RequestMapping("attendances")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "Get current attendance", description = "For both user & admin access", tags = {"Attendance"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get current attendance success",
                    content = @Content(schema = @Schema(implementation = AttendanceDTO.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid user input")
    })
    @GetMapping("current")
    public AttendanceDTO getCurrentAttendance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return attendanceService.getCurrentAttendance(userDetails.getUserId());
    }

    @Operation(summary = "Clock in", description = "For both user & admin access", tags = {"Attendance"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clock in success",
                    content = @Content(schema = @Schema(implementation = AttendanceDTO.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid user input")
    })
    @PostMapping("clock-in")
    public AttendanceDTO clockIn(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("company {} user {} clock in", userDetails.getCompanyId(), userDetails.getUserId());
        return attendanceService.clockIn(userDetails.getCompanyId(), userDetails.getUserId());
    }

    @Operation(summary = "Clock out", description = "For both user & admin access", tags = {"Attendance"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clock out success",
                    content = @Content(schema = @Schema(implementation = AttendanceDTO.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid user input")
    })
    @PostMapping("clock-out")
    public AttendanceDTO clockOut(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("company {} user {} clock out", userDetails.getCompanyId(), userDetails.getUserId());
        return attendanceService.clockOut(userDetails.getUserId());
    }

    @Operation(summary = "Break", description = "For both user & admin access", tags = {"Attendance"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Break in success",
                    content = @Content(schema = @Schema(implementation = AttendanceDTO.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid user input")
    })
    @PostMapping("/break")
    public AttendanceDTO takeBreak(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("company {} user {} break", userDetails.getCompanyId(), userDetails.getUserId());
        return attendanceService.takeBreak(userDetails.getUserId());
    }

    @Operation(summary = "Resume", description = "For both user & admin access", tags = {"Attendance"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resume success",
                    content = @Content(schema = @Schema(implementation = AttendanceDTO.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid user input")
    })
    @PostMapping("resume")
    public AttendanceDTO resume(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("company {} user {} resume", userDetails.getCompanyId(), userDetails.getUserId());
        return attendanceService.resume(userDetails.getUserId());
    }
}
