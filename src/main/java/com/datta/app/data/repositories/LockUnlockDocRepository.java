package com.datta.app.data.repositories;

import com.datta.app.data.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LockUnlockDocRepository extends JpaRepository<Document, Long> {

    @Query("select doc from Document doc where doc.lockedBy is not null and doc.lockTime is not null")
    List<Document> getLockedDocuments();

    @Transactional
    @Modifying
    @Query("update Document doc set doc.lockedBy = null, doc.lockTime = null where doc.id in (:docIds)")
    void unlockDocuments(@Param("docIds") List<Long> docIds);

    @Transactional
    @Modifying
    @Query("update Document doc set doc.lockedBy = :userId, doc.lockTime = CURRENT_TIMESTAMP where doc.id = :docId")
    void lockDocument(@Param("userId") Long userId, @Param("docId") Long docId);

    @Transactional
    @Modifying
    @Query("update Document doc set doc.lockedBy = null, doc.lockTime = null where doc.id = :docId")
    void unlockDocument(@Param("docId") Long docId);

    @Query("select doc from Document doc where doc.lockedBy = :userId")
    List<Document> getUserLockedDocuments(@Param("userId") Long userId);
}
