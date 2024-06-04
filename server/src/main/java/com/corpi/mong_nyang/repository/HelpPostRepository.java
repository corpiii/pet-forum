package com.corpi.mong_nyang.repository;

import com.corpi.mong_nyang.domain.help.HelpPosts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HelpPostRepository extends JpaRepository<HelpPosts, Long> {

    @Query("select h from HelpPosts h")
    Page<HelpPosts> findHelpPosts(Pageable pageable);

    @Query("select h from HelpPosts h " +
            "where h.author.id = :id")
    List<HelpPosts> findAllByUser(@Param("id") Long id);
}
