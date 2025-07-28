package com.appcenter.monolithicwnt.menu.infrastructure;

import com.appcenter.monolithicwnt.menu.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
