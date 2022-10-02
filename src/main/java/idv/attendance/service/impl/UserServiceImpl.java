package idv.attendance.service.impl;

import idv.attendance.configuration.security.UserRepository;
import idv.attendance.dto.UserNameDTO;
import idv.attendance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Map<Long, UserNameDTO> findNameByUserIds(Set<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }

        Collection<UserNameDTO> users = userRepository.findNameByUserIds(userIds);
        return users.stream().collect(Collectors.toMap(UserNameDTO::getUserId, Function.identity()));
    }
}
