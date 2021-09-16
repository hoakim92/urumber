package common

data class StepResult(val step: String, val result: RESULT? = null, val exception: String = "")

enum class RESULT {PASSED, FAILED}

data class Page(
    val appDescriptions: List<AppDescription>,
    val methods: List<MethodView>,
    val steps: List<StepResult>
)

data class MethodView(val regexp: String, val description: String)

data class AppDescription(val description: String)
