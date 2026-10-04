package com.college.complaint.dao;

import com.college.complaint.model.Feedback;

import java.util.List;
import java.util.Map;

public interface FeedbackDAO {
    boolean create(Feedback feedback);
    List<Feedback> findAll(int offset, int limit);
    List<Feedback> findByStudentId(int studentId, int offset, int limit);
    int countAll();
    Map<String, Double> getAverageRatingByDepartment();
    Map<Integer, Integer> getRatingDistribution();
    double getOverallAverageRating();
}
