package io.gitlab.arturbosch.detekt.rules.complexity

import io.github.detekt.metrics.CyclomaticComplexity
import io.github.detekt.metrics.CyclomaticComplexity.Companion.DEFAULT_NESTING_FUNCTIONS
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Complex methods are hard to understand and read. It might not be obvious what side-effects a complex method has.
 * Prefer splitting up complex methods into smaller methods that are in turn easier to understand.
 * Smaller methods can also be named much clearer which leads to improved readability of the code.
 *
 * This rule uses McCabe's Cyclomatic Complexity (MCC) metric to measure the number of
 * linearly independent paths through a function's source code (https://www.ndepend.com/docs/code-metrics#CC).
 * The higher the number of independent paths, the more complex a method is.
 * Complex methods use too many of the following statements.
 * Each one of them adds one to the complexity count.
 *
 * - __Conditional statements__ - `if`, `else if`, `when`
 * - __Jump statements__ - `continue`, `break`
 * - __Loops__ - `for`, `while`, `do-while`, `forEach`
 * - __Operators__ `&&`, `||`, `?:`
 * - __Exceptions__ - `catch`, `use`
 * - __Scope Functions__ - `let`, `run`, `with`, `apply`, and `also` ->
 *  [Reference](https://kotlinlang.org/docs/reference/scope-functions.html)
 *
 * @configuration threshold - McCabe's Cyclomatic Complexity (MCC) number for a method (default: `15`)
 * @configuration ignoreSingleWhenExpression - Ignores a complex method if it only contains a single when expression.
 * (default: `false`)
 * @configuration ignoreSimpleWhenEntries - Whether to ignore simple (braceless) when entries. (default: `false`)
 * @configuration ignoreNestingFunctions - Whether to ignore functions which are often used instead of an `if` or
 * `for` statement (default: `false`)
 * @configuration nestingFunctions - Comma separated list of function names which add complexity
 * (default: `[run, let, apply, with, also, use, forEach, isNotNull, ifNull]`)
 *
 * @active since v1.0.0
 */
class ComplexMethod(
    config: Config = Config.empty,
    threshold: Int = DEFAULT_THRESHOLD_METHOD_COMPLEXITY
) : ThresholdRule(config, threshold) {

    override val issue = Issue("ComplexMethod",
        Severity.Maintainability,
        "Prefer splitting up complex methods into smaller, easier to understand methods.",
        Debt.TWENTY_MINS)

    private val ignoreSingleWhenExpression = valueOrDefault(IGNORE_SINGLE_WHEN_EXPRESSION, false)
    private val ignoreSimpleWhenEntries = valueOrDefault(IGNORE_SIMPLE_WHEN_ENTRIES, false)
    private val ignoreNestingFunctions = valueOrDefault(IGNORE_NESTING_FUNCTIONS, false)
    private val nestingFunctions = valueOrDefaultCommaSeparated(NESTING_FUNCTIONS, DEFAULT_NESTING_FUNCTIONS.toList())
        .toSet()

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (ignoreSingleWhenExpression && hasSingleWhenExpression(function.bodyExpression)) {
            return
        }

        val complexity = CyclomaticComplexity.calculate(function) {
            this.ignoreSimpleWhenEntries = this@ComplexMethod.ignoreSimpleWhenEntries
            this.ignoreNestingFunctions = this@ComplexMethod.ignoreNestingFunctions
            this.nestingFunctions = this@ComplexMethod.nestingFunctions
        }

        if (complexity >= threshold) {
            report(
                ThresholdedCodeSmell(
                    issue,
                    Entity.atName(function),
                    Metric("MCC", complexity, threshold),
                    "The function ${function.nameAsSafeName} appears to be too complex ($complexity). " +
                            "Defined complexity threshold for methods is set to '$threshold'"
                )
            )
        }
    }

    private fun hasSingleWhenExpression(bodyExpression: KtExpression?): Boolean = when {
        bodyExpression is KtBlockExpression && bodyExpression.statements.size == 1 -> {
            val statement = bodyExpression.statements.single()
            statement is KtWhenExpression || statement.returnsWhenExpression()
        }
        // the case where function-expression syntax is used: `fun test() = when { ... }`
        bodyExpression is KtWhenExpression -> true
        else -> false
    }

    private fun KtExpression.returnsWhenExpression() =
        this is KtReturnExpression && this.returnedExpression is KtWhenExpression

    companion object {
        const val DEFAULT_THRESHOLD_METHOD_COMPLEXITY = 15
        const val IGNORE_SINGLE_WHEN_EXPRESSION = "ignoreSingleWhenExpression"
        const val IGNORE_SIMPLE_WHEN_ENTRIES = "ignoreSimpleWhenEntries"
        const val IGNORE_NESTING_FUNCTIONS = "ignoreNestingFunctions"
        const val NESTING_FUNCTIONS = "nestingFunctions"
    }
}
