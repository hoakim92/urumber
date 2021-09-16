package common

annotation class Urumber(
    val stepRegExp: String = "",
    val stepDescription: String = "",
    val stepView: String = "",
    val appDescription: Array<String> = [],
    val defaultSteps: Array<String> = []
)