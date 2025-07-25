package com.phirom_02.blog_api.repository;

import com.phirom_02.blog_api.domain.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    @Query("SELECT c from Tag c LEFT JOIN FETCH c.posts")
    List<Tag> findAllWithPostCount();

    List<Tag> findAllByNameIn(Set<String> names);

    //    List<Tag> findAllBy(Set<UUID> ids);
    List<Tag> findAllByIdIn(Set<UUID> ids);
}
