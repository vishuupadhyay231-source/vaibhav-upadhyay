package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CalculatorRegistry
import com.example.data.local.SolversList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Vishu Connect", appName)
  }

  @Test
  fun `verify exact 240 calculators in registry`() {
    assertEquals(240, CalculatorRegistry.length)
    assertEquals(240, CalculatorRegistry.allSolvers.size)
    assertEquals(240, SolversList.allSolvers.size)
  }

  @Test
  fun `verify required critical calculator IDs exist`() {
    val solvers = CalculatorRegistry.allSolvers
    val ids = solvers.map { it.id }.toSet()

    // 001, 120, 180, 200, 220, 239, 240
    assertTrue(ids.contains(1))
    assertTrue(ids.contains(120))
    assertTrue(ids.contains(180))
    assertTrue(ids.contains(200))
    assertTrue(ids.contains(220))
    assertTrue(ids.contains(239))
    assertTrue(ids.contains(240))

    assertEquals("Scientific Calculator", solvers[0].name)
    assertEquals("Graph Theory Calculator", solvers[119].name)
    assertEquals("B-Tree Calculator", solvers[179].name)
    assertEquals("Cosine Similarity Calculator", solvers[199].name)
    assertEquals("Speed Converter", solvers[219].name)
    assertEquals("Remainder Calculator", solvers[238].name)
    assertEquals("Master BCA-MCA Calculator", solvers[239].name)
  }

  @Test
  fun `verify search returns matched calculators across 240 items`() {
    val all = CalculatorRegistry.allSolvers
    
    val rsa = all.filter { it.name.contains("RSA", ignoreCase = true) }
    assertTrue(rsa.isNotEmpty())

    val subnet = all.filter { it.name.contains("Subnet", ignoreCase = true) }
    assertTrue(subnet.isNotEmpty())

    val matrix = all.filter { it.name.contains("Matrix", ignoreCase = true) }
    assertTrue(matrix.size >= 7)

    val kmeans = all.filter { it.name.contains("K-Means", ignoreCase = true) }
    assertTrue(kmeans.isNotEmpty())

    val f1 = all.filter { it.name.contains("F1", ignoreCase = true) }
    assertTrue(f1.isNotEmpty())

    val master = all.filter { it.name.contains("Master", ignoreCase = true) }
    assertTrue(master.size >= 2)
  }

  @Test
  fun `verify calculator mathematical evaluations`() {
    // 1. Scientific
    val (sciRes, _) = CalculatorRegistry.evaluateSolver(1, "12", "8", "+")
    assertTrue(sciRes.contains("20"))

    // 2. Percentage
    val (pctRes, _) = CalculatorRegistry.evaluateSolver(2, "50", "200", "")
    assertTrue(pctRes.contains("25.00%"))

    // 14. GCD
    val (gcdRes, _) = CalculatorRegistry.evaluateSolver(14, "48", "18", "")
    assertTrue(gcdRes.contains("6"))

    // 186. RSA
    val (rsaRes, _) = CalculatorRegistry.evaluateSolver(186, "61", "53", "17")
    assertTrue(rsaRes.contains("d ="))

    // 240. Master Solver
    val (masterRes, _) = CalculatorRegistry.evaluateSolver(240, "Core", "Test", "")
    assertNotNull(masterRes)
  }
}
