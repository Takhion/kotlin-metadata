import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

typealias ProjectExt0<R> = Project.() -> R
typealias ProjectExt1<T, R> = Project.(T) -> R

inline fun <reified T : Any> extension(): ProjectExt0<T> = { the() }
inline fun <reified T : Any> configuration(): ProjectExt1<T.() -> Unit, Unit> = { configure(it) }

inline fun <reified T : Plugin<Project>> Project.applyPlugin() = apply { plugin(T::class.java) }

operator fun <This, R> (This.() -> R).getValue(thisRef: This, property: KProperty<*>) = invoke(thisRef)

fun extraOrEnv(envName: String) =
    findProjectProperty<String?> { it as? String ?: System.getenv(envName) }

fun extraOrDefault(defaultValue: Boolean) =
    findProjectProperty {
        when {
            it is Boolean -> it
            it is String && it.equals("true", ignoreCase = true) -> true
            it is String && it.equals("false", ignoreCase = true) -> false
            else -> defaultValue
        }
    }

inline fun <T> findProjectProperty(crossinline transformValue: (Any?) -> T) =
    readOnlyProperty<Project, T> { findProperty(it.name).let(transformValue) }

inline fun <This, T> readOnlyProperty(crossinline getValue: This.(property: KProperty<*>) -> T) =
    object : ReadOnlyProperty<This, T> {
        override fun getValue(thisRef: This, property: KProperty<*>): T = getValue(thisRef, property)
    }
