package com.teachassist.teachassist;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CoursesEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CoursesDao coursesDao();
}