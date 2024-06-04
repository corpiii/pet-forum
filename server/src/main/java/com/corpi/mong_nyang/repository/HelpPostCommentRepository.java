package com.corpi.mong_nyang.repository;

import com.corpi.mong_nyang.domain.help.HelpPostComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HelpPostCommentRepository extends JpaRepository<HelpPostComments, Long> {

    @Query("select h from HelpPostComments h " +
            "where h.author.id = :id")
    List<HelpPostComments> findAllByUser(@Param("id") Long id);
}
