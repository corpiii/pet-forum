package com.corpi.mong_nyang.repository;

import com.corpi.mong_nyang.domain.help.HelpPostComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpPostCommentRepository extends JpaRepository<HelpPostComments, Long> {
}
