package com.example.data.local

import com.example.data.model.SolverItem

object SolversList {

    // Central Registry: EXACTLY 240 BCA-MCA Calculators
    val allSolvers: List<SolverItem> get() = CalculatorRegistry.allSolvers

    val length: Int get() = CalculatorRegistry.allSolvers.size
    val size: Int get() = CalculatorRegistry.allSolvers.size

    fun evaluate(solver: SolverItem, aStr: String, bStr: String, cStr: String): Pair<String, String> {
        return CalculatorRegistry.evaluate(solver, aStr, bStr, cStr)
    }

    fun evaluateSolver(id: Int, aStr: String, bStr: String, cStr: String): Pair<String, String> {
        return CalculatorRegistry.evaluateSolver(id, aStr, bStr, cStr)
    }
}
