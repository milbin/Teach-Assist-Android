package com.teachassist.teachassist;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CoursesEntity {


    @PrimaryKey
    @NonNull
    public String courseCode;

    @ColumnInfo(name = "periodNumber")
    public int periodNumber;

    @ColumnInfo(name = "subjectID")
    public String subjectID;

    @ColumnInfo(name = "average")
    public Double average;

    @ColumnInfo(name = "courseName")
    public String courseName;

    @ColumnInfo(name = "roomNumber")
    public String roomNumber;

    @ColumnInfo(name = "assignments")
    public String assignments;
}