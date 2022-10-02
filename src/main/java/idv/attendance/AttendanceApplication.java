package idv.attendance;

import idv.attendance.util.TestDataCreator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AttendanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AttendanceApplication.class, args);
    }

    @Bean
    public CommandLineRunner createTestData(TestDataCreator testDataCreator) {
        return arg -> {
            testDataCreator.createAttendances();
        };
    }
}
