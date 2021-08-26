package common

data class StepResult(val step: String, val result: RESULT, val exception: String = "")

enum class RESULT {PASSED, FAILED}
