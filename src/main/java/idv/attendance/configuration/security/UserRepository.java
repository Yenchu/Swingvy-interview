package idv.attendance.configuration.security;

import idv.attendance.dto.UserNameDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginName(String loginName);

    @Query(value = "SELECT user_id as userId, name as name FROM user u WHERE u.user_id IN :userIds", nativeQuery = true)
    Collection<UserNameDTO> findNameByUserIds(@Param("userIds") Set<Long> userIds);
}
