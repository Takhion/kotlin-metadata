package com.example

import me.eugeniomarletti.WithMethods

typealias Foo<T, R> = TestDataClass2<T, R>
typealias Bar = TestDataClass2<String, Int>

class IntArrayList: ArrayList<Int>()

@WithMethods
data class TestDataClass1(val counter: Int, val name: List<String?>) {
    constructor() : this(0, listOf())

    fun copy(): TestDataClass1 = TestDataClass1()
    fun copy(name: List<String?>): TestDataClass1 = TestDataClass1(counter = this.counter, name = name)
}

@WithMethods
@Suppress("AddVarianceModifier")
data class TestDataClass2<out T, R>(val generic1: T, val generic2: R)

@WithMethods
data class TestDataClass3<out T: Any, R>(val foo: Foo<T, R>, val bar: Bar?)

@WithMethods
data class TestDataClass4<Z, out T: TestDataClass2<Z, Z>, R>(val a: TestDataClass2<T, R>, val b: TestDataClass2<Z, *>)

@WithMethods(extensionName = "copyIfNecessary")
data class TestDataClass5<out T, R>(val foo: Foo<T, R>, val list: IntArrayList?, val mList: MutableList<Any?>?)

@WithMethods
data class TestDataClass6(val a: Int)

class Parent {
    @WithMethods
    data class TestDataClass6<out T, R>(val foo: Foo<T, R>, val list: IntArrayList?) where R : Any, R : Runnable
}

//TODO write proper tests
fun main(vararg args: String) {
    TestDataClass2(0, "").let {
        check(it !== it.copy(generic1 = 1))
        check(it !== it.with(generic1 = 1))
        check(it !== it.copy(generic1 = 0, generic2 = ""))
        check(it === it.with(generic1 = 0, generic2 = ""))
        check(it === it.withGeneric1(0))
        check(it === it.withGeneric2(""))
    }
}
