package com.corpi.mong_nyang.repository;

import com.corpi.mong_nyang.domain.help.HelpPosts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpPostRepository extends JpaRepository<HelpPosts, Long> {
}
