package com.example.workoutapp

class ExerciseModel(
    private var id : Int,
    private var name : String,
    private var image : Int,
    private var completed : Boolean,
    private var isSelected : Boolean
) {
    fun getId():Int{return id}
    fun setId(id: Int){this.id = id}

    fun getName():String{return name}
    fun setName(name: String){this.name = name}

    fun getImage():Int{return image}
    fun setImage(image: Int){this.image = image}

    fun getCompleted():Boolean{return completed}
    fun setCompleted(completed: Boolean){this.completed = completed}

    fun getIsSelected():Boolean{return isSelected}
    fun setIsSelected(isSelected: Boolean){this.isSelected = isSelected}
}