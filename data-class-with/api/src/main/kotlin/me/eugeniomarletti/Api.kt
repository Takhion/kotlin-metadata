package me.eugeniomarletti

import kotlin.annotation.AnnotationTarget.CLASS

@Target(CLASS)
annotation class WithMethods(
    val extensionName: String = "with")

inline fun <T> checkIfChanged(old: T, new: T, ifChanged: () -> Unit): T =
    if (old === new || old == new) old
    else {
        ifChanged()
        new
    }
