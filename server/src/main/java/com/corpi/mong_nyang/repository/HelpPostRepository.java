package com.corpi.mong_nyang.repository;

import com.corpi.mong_nyang.domain.help.HelpPosts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HelpPostRepository extends JpaRepository<HelpPosts, Long> {

    @Query("select h from HelpPosts h")
    Page<HelpPosts> findHelpPosts(Pageable pageable);
}
