package idv.attendance.service;

import idv.attendance.dto.UserNameDTO;

import java.util.Map;
import java.util.Set;

public interface UserService {

    Map<Long, UserNameDTO> findNameByUserIds(Set<Long> userIds);

}
