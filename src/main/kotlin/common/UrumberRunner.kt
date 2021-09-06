package common

import java.lang.UnsupportedOperationException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class UrumberRunner {
companion object {
    fun runTest(steps: List<String>, obj: Any): List<StepResult> {
        return runSteps(steps, RegExpHelper.getMethodsByRegExp(obj.javaClass), obj)
    }

    fun runSteps(steps: List<String>, methods: List<Pair<String, Method>>, obj: Any) =
        steps.map {
            runStep(it, methods, obj)
        }


    fun createListOfMaps(dataTable: String): List<Map<String, String>> {
        val values = dataTable.split("\r\n").map { it.split("|").map { it.trim() }.filterNot { p -> p.isBlank() } }
        val columns = values.first()
        return values.subList(1, values.size).map {
            columns.mapIndexed { index, s ->
                s to it[index]
            }.toMap()
        }
    }

    fun runStep(step: String, methods: List<Pair<String, Method>>, obj: Any): StepResult {
        fun getStepAndDataTable(fullStep: String): Pair<String, List<Map<String, String>>?> {
            val containsDataTable = fullStep.contains(Regex("| (.*) |"))
            val dataTable = if (containsDataTable) {
                createListOfMaps(fullStep.substring(fullStep.indexOf("|")))
            } else {
                null
            }
            val step = if (containsDataTable) {
                fullStep.substringBefore("|").trim()
            } else {
                fullStep.trim()

            }
            return step to dataTable
        }

        fun didvideStepAndDataTable(fullStep: String): Pair<String, String?> {
            val containsDataTable = fullStep.contains(Regex("\\| (.*) \\|"))
            return if (containsDataTable) {
                fullStep.substringBefore("|").trim() to fullStep.substring(fullStep.indexOf("|"))
            } else {
                fullStep to null
            }
        }

        fun addDataTableToArgs(args: List<String>, dataTable: String?): List<String> {
            return if (dataTable != null) {
                args.plus(dataTable!!)
            } else {
                args
            }

        }
        try {
            val stepAndDataTable = didvideStepAndDataTable(step)
            if (methods.find { Regex(it.first).matches(stepAndDataTable.first) } != null) {
                val regExpAndMethods = methods.find { Regex(it.first).matches(stepAndDataTable.first) }!!
                val groupValues = RegExpHelper.getArgsFromCommand(stepAndDataTable.first, regExpAndMethods.first)
                val arguments = groupValues.subList(1, groupValues.size)


                runMethod(regExpAndMethods.second, addDataTableToArgs(arguments, stepAndDataTable.second), obj)
            } else {
                throw UnsupportedOperationException("NO SUCH METHOD")
            }
        } catch (e: InvocationTargetException) {
            return StepResult(step, RESULT.FAILED, e.cause!!.toString())
        } catch (e: Exception) {
            return StepResult(step, RESULT.FAILED, e.toString())
        }
        return StepResult(step, RESULT.PASSED)
    }

    fun runMethod(method: Method, arguments: List<String>, obj: Any) {
        val argumentsAfterCast = arguments.mapIndexed { index, s ->
            when (method.parameters[index].parameterizedType.typeName) {
                "int" -> s.toInt()
                "java.lang.String" -> s
                "java.util.List<? extends java.util.Map<java.lang.String, java.lang.String>>" -> createListOfMaps(s)
                else -> throw Exception("Unsupported Argument")
            }
        }
        method.invoke(obj, *argumentsAfterCast.toTypedArray())
    }

    fun getStepsViewAndDescription(clazz: Class<*>) = clazz.methods.filter { it.isAnnotationPresent(Urumber::class.java) }
        .map { it.getAnnotation(Urumber::class.java).stepView to it.getAnnotation(Urumber::class.java).stepDescription }

    fun getDefaultSteps(clazz: Class<*>) = clazz.getAnnotation(Urumber::class.java).defaultSteps

    fun getAppDescription(clazz: Class<*>) = clazz.getAnnotation(Urumber::class.java).appDescription
}}