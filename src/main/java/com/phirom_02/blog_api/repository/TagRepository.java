package com.phirom_02.blog_api.repository;

import com.phirom_02.blog_api.domain.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Repository interface for managing {@link Tag} entities.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {


    /**
     * Finds all tags and fetches the associated posts with each tag.
     *
     * @return a list of tags with their associated posts
     */
    @Query("SELECT c from Tag c LEFT JOIN FETCH c.posts")
    List<Tag> findAllWithPostCount();

    /**
     * Finds tags by their names.
     *
     * @param names a set of tag names
     * @return a list of tags with names that match the given set
     */
    List<Tag> findAllByNameIn(Set<String> names);

    List<Tag> findAllByIdIn(Set<UUID> ids);
}
