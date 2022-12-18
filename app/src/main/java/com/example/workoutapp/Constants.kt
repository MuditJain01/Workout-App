package com.example.workoutapp

object Constants {
    fun getExercises(): ArrayList<ExerciseModel>{
        var exercises = ArrayList<ExerciseModel>()

        val plank = ExerciseModel(1,"Plank",R.drawable.ic_plank,false,false)
        exercises.add(plank)

        val pushUp = ExerciseModel(2,"Push Up",R.drawable.ic_push_up,false,false)
        exercises.add(pushUp)

        val squat = ExerciseModel(3,"Squat",R.drawable.ic_squat,false,false)
        exercises.add(squat)

        return exercises
    }
}