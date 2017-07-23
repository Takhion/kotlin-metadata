package me.eugeniomarletti

internal object Generator {

    private const val checkIfChanged = "me.eugeniomarletti.checkIfChanged"

    internal data class Input(
        val fqClassName: String,
        val `package`: String,
        val typeArgumentList: List<TypeParameter>,
        val parameterList: List<Parameter>,
        val extensionName: String) {

        fun generate() = Generator.generate(this)
    }

    internal data class TypeParameter(
        val name: String,
        val upperBoundsFqClassNames: List<String>)

    internal data class Parameter(
        val name: String,
        val fqClassName: String)

    fun generate(input: Input) = run {
        val (fqClassName, `package`, typeArgumentList, parameterList, extensionName) = input
        val main = main(fqClassName, typeArgumentList, extensionName, parameterList)
        val specifics = parameterList.joinToString(separator = "\n") { (parameterName, parameterClassName) ->
            specific(fqClassName, typeArgumentList, extensionName, parameterName, parameterClassName)
        }

        """
        |package $`package`
        |
        |$main
        |
        |$specifics
        """.trimMargin()
    }

    private fun main(
        className: String,
        typeArgumentList: List<TypeParameter>,
        extensionName: String,
        parameters: List<Parameter>
    ) = run {
        val typeArguments = typeArguments(typeArgumentList)
        val functionArgs = parameters.joinToString { (name, className) -> "$name: $className = this.$name" }
        val whereClause = whereClause(typeArgumentList)
        val checks = parameters.joinToString(separator = "\n    ", transform = { (name) -> check(name) })
        val copyArgs = parameters.joinToString { (name) -> "$name = _$name" }

        """
        |fun $typeArguments $className$typeArguments.$extensionName($functionArgs) $whereClause
        |    = run {
        |    var copy = false
        |    $checks
        |    if (!copy) this else copy($copyArgs)
        |}
        """.trimMargin()
    }

    private fun typeArguments(typeArgumentList: List<TypeParameter>) =
        typeArgumentList
            .takeIf { it.isNotEmpty() }
            ?.joinToString(prefix = "<", postfix = ">") { it.name }
            ?: ""

    private fun whereClause(typeArguments: List<TypeParameter>) =
        typeArguments
            .flatMap { (name, upperBounds) -> upperBounds.map { Pair(name, it) } }
            .filterNot { (_, upperBound) -> upperBound.isNullOrBlank() }
            .takeIf { it.isNotEmpty() }
            ?.joinToString(prefix = "\n    where ") { (name, upperBound) -> "$name : $upperBound" }
            ?: ""

    private fun check(parameterName: String) =
        "val _$parameterName = $checkIfChanged(old = this.$parameterName, new = $parameterName, ifChanged = { copy = true })"

    private fun specific(
        className: String,
        typeArgumentList: List<TypeParameter>,
        extensionName: String,
        parameterName: String,
        parameterClassName: String
    ) = run {
        val _methodName = extensionName + parameterName.capitalize()
        val typeArguments = typeArguments(typeArgumentList)
        val whereClause = whereClause(typeArgumentList)

        """
        |fun $typeArguments $className$typeArguments.$_methodName($parameterName: $parameterClassName = this.$parameterName) $whereClause
        |    = if (this.$parameterName == $parameterName) this else copy($parameterName = $parameterName)
        |""".trimMargin()
    }
}
