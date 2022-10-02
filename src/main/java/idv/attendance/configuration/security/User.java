package idv.attendance.configuration.security;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Getter
@Entity
public class User {
    @Id
    private long userId;
    private long companyId;
    private String name;
    private String loginName;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
