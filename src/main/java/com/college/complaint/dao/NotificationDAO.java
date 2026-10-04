package com.college.complaint.dao;

import com.college.complaint.model.Notification;

import java.util.List;

public interface NotificationDAO {
    boolean create(Notification notification);
    List<Notification> findByStudentId(int studentId, int limit);
    int countUnreadByStudentId(int studentId);
    boolean markAllAsRead(int studentId);
    boolean markAsRead(int notificationId, int studentId);
}
