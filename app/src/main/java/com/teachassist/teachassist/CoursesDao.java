package com.teachassist.teachassist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface CoursesDao {

    @Query("SELECT * FROM CoursesEntity WHERE courseCode = (:courseCode)")
    CoursesEntity getCourseByCourseCode(String courseCode);

    @Query("SELECT * FROM CoursesEntity")
    CoursesEntity[] getAllCoureses();

    @Update
    void updateCourse(CoursesEntity course);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CoursesEntity... courses);

    @Delete
    void delete(CoursesEntity courses);
}