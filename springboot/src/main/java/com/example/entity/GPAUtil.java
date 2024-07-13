package com.example.entity;

import java.util.List;
import java.util.Map;

public class GPAUtil {
    public static double convertScoreToGPA(double score) {
        if (score >= 90) return 4.0 + (score - 90) * 0.1; // 90-100: 4.0 - 5.0
        else if (score >= 80) return 3.0 + (score - 80) * 0.1; // 80-89: 3.0 - 3.9
        else if (score >= 70) return 2.0 + (score - 70) * 0.1; // 70-79: 2.0 - 2.9
        else if (score >= 60) return 1.0 + (score - 60) * 0.1; // 60-69: 1.0 - 1.9
        else return 0.0; // < 60: 0.0
    }

    public static double calculateWeightedGPA(List<Score> scores, Map<Integer, Course> courseMap) {
        double totalGPA = 0.0;
        double totalCredits = 0.0;
        for (Score score : scores) {
            Course course = courseMap.get(score.getCourseId());
            if (course != null) {
                double gpa = convertScoreToGPA(score.getScore());
                totalGPA += gpa * course.getScore();
                totalCredits += course.getScore();
            }
        }
        return totalCredits == 0 ? 0 : totalGPA / totalCredits;
    }
}
