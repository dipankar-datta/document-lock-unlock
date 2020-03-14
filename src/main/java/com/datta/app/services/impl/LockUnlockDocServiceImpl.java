package com.datta.app.services.impl;

import com.datta.app.data.entities.Document;
import com.datta.app.data.repositories.LockUnlockDocRepository;
import com.datta.app.services.LockUnlockDocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LockUnlockDocServiceImpl implements LockUnlockDocService {

    @Value("${document.unlock.interval}")
    private Short DOC_UNLOCK_INTERVAL;

    @Value("${document.lock.ping.interval}")
    private Short DOC_LOCK_PING_INTERVAL;

    @Value("${document.lock.grace.period}")
    private Short DOC_LOCK_GRACE_PERIOD;

    @Autowired
    private LockUnlockDocRepository lockUnlockDocRepository;

    private boolean DO_MONITOR;

    public static final Logger LOGGER = LoggerFactory.getLogger(LockUnlockDocServiceImpl.class);

    @PostConstruct
    public void init(){
        DO_MONITOR = true;
        new Thread(() -> monitorLock()).start();
        LOGGER.info("Monitoring started");
    }

    public void monitorLock() {
        while(DO_MONITOR){
            try {
                Thread.sleep(DOC_UNLOCK_INTERVAL * 1000);
                unlockExpiredDocs();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void unlockExpiredDocs(){
        try {
            List<Document> lockedDocs = lockUnlockDocRepository.getLockedDocuments();
            LOGGER.info(lockedDocs.size() + " Locked documents found");
            if (!lockedDocs.isEmpty()) {
                List<Long> lockExpiredDocIds = getLockExpiredDocs(lockedDocs);
                LOGGER.info(lockExpiredDocIds.size() + " Documents have expired");
                if (!lockExpiredDocIds.isEmpty()) {
                    lockUnlockDocRepository.unlockDocuments(lockExpiredDocIds);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Long> getLockExpiredDocs(List<Document> lockedDocs) {
        final Long currentTimeMilliseconds = new Date().getTime();
        return lockedDocs
                .stream()
                .filter(document -> {
                    long timeGapMilliSeconds = currentTimeMilliseconds - document.getLockTime().getTime();
                    return (timeGapMilliSeconds / 1000) > (DOC_LOCK_PING_INTERVAL + DOC_LOCK_GRACE_PERIOD);
                })
                .map(document -> document.getId())
                .collect(Collectors.toList());
    }

    @Override
    public boolean lock(Long userId, Long documentId) {

        Document document = lockUnlockDocRepository.getOne(documentId);

        if (document != null) {

            /* Scenario 1 : When document is already locked by someone else, then don't do anything and return. */
            if (document.getLockedBy() != null && !document.getLockedBy().equals(userId)) {
                return false;
            }

            /* Scenario 2 : When document is not locked by anyone, then we combine logic with scenario 3
             * Scenario 3 : When document is already locked by same user, then clear
             * all previous locks and create new lock.
             */
            if (document.getLockedBy() == null
                    || (document.getLockedBy() != null && document.getLockedBy().equals(userId))) {
                List<Document> userLockedDocs = lockUnlockDocRepository.getUserLockedDocuments(userId);
                if (!userLockedDocs.isEmpty()) {
                    lockUnlockDocRepository.unlockDocuments(
                            userLockedDocs
                                    .stream()
                                    .map(document2 -> document2.getId())
                                    .collect(Collectors.toList()));
                }
                lockUnlockDocRepository.lockDocument(userId, documentId);
                return true;
            }
        } else {
            System.out.println("Invalid document id");
        }

        return false;
    }

    @Override
    public boolean unlock(Long userId, Long documentId) {
        Document document = lockUnlockDocRepository.getOne(documentId);
        if (document.getLockedBy() != null && document.getLockedBy().equals(userId)) {
            lockUnlockDocRepository.unlockDocument(documentId);
            return true;
        }
        return false;
    }
}
