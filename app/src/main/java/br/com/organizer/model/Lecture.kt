package br.com.organizer.model

import java.util.*

data class Lecture(var name: String,
                   var weakDay: WeakDay,
                   var startTime: Date,
                   var finishTime: Date)