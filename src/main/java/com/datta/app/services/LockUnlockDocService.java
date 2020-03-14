package com.datta.app.services;

public interface LockUnlockDocService {

    public boolean lock(Long userId, Long documentId);

    public boolean unlock(Long userId, Long documentId);
}
