package io.openexchange.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SmsRepository extends JpaRepository<SmsEntity, Integer> {
}
