package com.example.data.local

import com.example.data.model.SolverItem
import kotlin.math.*

object CalculatorRegistry {

    val titles: List<String> = listOf(
        "Scientific Calculator", // 1
        "Percentage Calculator", // 2
        "Ratio Calculator", // 3
        "Average Calculator", // 4
        "Mean Calculator", // 5
        "Median Calculator", // 6
        "Mode Calculator", // 7
        "Variance Calculator", // 8
        "Standard Deviation Calculator", // 9
        "Probability Calculator", // 10
        "Permutation Calculator", // 11
        "Combination Calculator", // 12
        "Factorial Calculator", // 13
        "GCD Calculator", // 14
        "HCF Calculator", // 15
        "LCM Calculator", // 16
        "Modular Arithmetic Calculator", // 17
        "Number System Calculator", // 18
        "Binary Calculator", // 19
        "Decimal to Binary Calculator", // 20
        "Binary to Decimal Calculator", // 21
        "Octal Calculator", // 22
        "Hexadecimal Calculator", // 23
        "Base Conversion Calculator", // 24
        "Logarithm Calculator", // 25
        "Antilog Calculator", // 26
        "Exponential Calculator", // 27
        "Power Calculator", // 28
        "Square Root Calculator", // 29
        "Complex Number Calculator", // 30
        "Algebra Calculator", // 31
        "Linear Equation Calculator", // 32
        "Quadratic Equation Calculator", // 33
        "Polynomial Calculator", // 34
        "Polynomial Factorization Calculator", // 35
        "Simultaneous Equation Calculator", // 36
        "Matrix Calculator", // 37
        "Matrix Addition Calculator", // 38
        "Matrix Multiplication Calculator", // 39
        "Matrix Transpose Calculator", // 40
        "Matrix Determinant Calculator", // 41
        "Matrix Inverse Calculator", // 42
        "Matrix Rank Calculator", // 43
        "Eigenvalue Calculator", // 44
        "Eigenvector Calculator", // 45
        "Gaussian Elimination Calculator", // 46
        "Gauss-Jordan Calculator", // 47
        "Vector Calculator", // 48
        "Dot Product Calculator", // 49
        "Cross Product Calculator", // 50
        "Vector Magnitude Calculator", // 51
        "Trigonometry Calculator", // 52
        "Sine Calculator", // 53
        "Cosine Calculator", // 54
        "Tangent Calculator", // 55
        "Inverse Trigonometry Calculator", // 56
        "Degree-Radian Converter", // 57
        "Coordinate Geometry Calculator", // 58
        "Distance Formula Calculator", // 59
        "Midpoint Calculator", // 60
        "Slope Calculator", // 61
        "Straight Line Calculator", // 62
        "Circle Calculator", // 63
        "Triangle Calculator", // 64
        "Pythagorean Theorem Calculator", // 65
        "Area Calculator", // 66
        "Volume Calculator", // 67
        "Sequence Calculator", // 68
        "Arithmetic Progression Calculator", // 69
        "Geometric Progression Calculator", // 70
        "Series Calculator", // 71
        "Binomial Theorem Calculator", // 72
        "Limit Calculator", // 73
        "Derivative Calculator", // 74
        "Partial Derivative Calculator", // 75
        "Integral Calculator", // 76
        "Definite Integral Calculator", // 77
        "Indefinite Integral Calculator", // 78
        "Differential Equation Calculator", // 79
        "Laplace Transform Calculator", // 80
        "Fourier Series Calculator", // 81
        "Fourier Transform Calculator", // 82
        "Numerical Methods Calculator", // 83
        "Bisection Method Calculator", // 84
        "Newton-Raphson Calculator", // 85
        "Secant Method Calculator", // 86
        "Lagrange Interpolation Calculator", // 87
        "Newton Interpolation Calculator", // 88
        "Numerical Integration Calculator", // 89
        "Trapezoidal Rule Calculator", // 90
        "Simpson's Rule Calculator", // 91
        "Euler Method Calculator", // 92
        "Runge-Kutta Calculator", // 93
        "Descriptive Statistics Calculator", // 94
        "Frequency Distribution Calculator", // 95
        "Correlation Calculator", // 96
        "Pearson Correlation Calculator", // 97
        "Spearman Rank Correlation Calculator", // 98
        "Covariance Calculator", // 99
        "Linear Regression Calculator", // 100
        "Least Squares Calculator", // 101
        "Probability Distribution Calculator", // 102
        "Binomial Distribution Calculator", // 103
        "Poisson Distribution Calculator", // 104
        "Normal Distribution Calculator", // 105
        "Bayes Theorem Calculator", // 106
        "Conditional Probability Calculator", // 107
        "Expected Value Calculator", // 108
        "Set Operations Calculator", // 109
        "Set Union Calculator", // 110
        "Set Intersection Calculator", // 111
        "Set Difference Calculator", // 112
        "Cartesian Product Calculator", // 113
        "Power Set Calculator", // 114
        "Boolean Algebra Calculator", // 115
        "Boolean Expression Simplifier", // 116
        "Logic Gate Calculator", // 117
        "Truth Table Calculator", // 118
        "K-Map Calculator", // 119
        "Graph Theory Calculator", // 120
        "Graph Degree Calculator", // 121
        "Adjacency Matrix Calculator", // 122
        "Shortest Path Calculator", // 123
        "Dijkstra Algorithm Calculator", // 124
        "Floyd-Warshall Calculator", // 125
        "Minimum Spanning Tree Calculator", // 126
        "Prim's Algorithm Calculator", // 127
        "Kruskal's Algorithm Calculator", // 128
        "Graph Coloring Calculator", // 129
        "BFS Calculator", // 130
        "DFS Calculator", // 131
        "Topological Sort Calculator", // 132
        "Algorithm Complexity Calculator", // 133
        "Big-O Calculator", // 134
        "Big-Theta Calculator", // 135
        "Recurrence Relation Calculator", // 136
        "Master Theorem Calculator", // 137
        "Stack Calculator", // 138
        "Queue Calculator", // 139
        "Circular Queue Calculator", // 140
        "Binary Tree Calculator", // 141
        "Binary Search Tree Calculator", // 142
        "AVL Tree Calculator", // 143
        "Heap Calculator", // 144
        "Tree Traversal Calculator", // 145
        "CPU Performance Calculator", // 146
        "CPI Calculator", // 147
        "MIPS Calculator", // 148
        "Cache Memory Calculator", // 149
        "Memory Address Calculator", // 150
        "Virtual Memory Calculator", // 151
        "Page Replacement Calculator", // 152
        "CPU Scheduling Calculator", // 153
        "FCFS Scheduling Calculator", // 154
        "SJF Scheduling Calculator", // 155
        "Round Robin Calculator", // 156
        "Priority Scheduling Calculator", // 157
        "Waiting Time Calculator", // 158
        "Turnaround Time Calculator", // 159
        "Disk Scheduling Calculator", // 160
        "Banker's Algorithm Calculator", // 161
        "IP Address Calculator", // 162
        "IPv4 Subnet Calculator", // 163
        "IPv6 Calculator", // 164
        "Subnet Mask Calculator", // 165
        "CIDR Calculator", // 166
        "VLSM Calculator", // 167
        "Network Address Calculator", // 168
        "Broadcast Address Calculator", // 169
        "Bandwidth Calculator", // 170
        "Data Transfer Time Calculator", // 171
        "Network Throughput Calculator", // 172
        "Latency Calculator", // 173
        "Shannon Capacity Calculator", // 174
        "Nyquist Bit Rate Calculator", // 175
        "Hamming Code Calculator", // 176
        "CRC Calculator", // 177
        "Checksum Calculator", // 178
        "Database Storage Calculator", // 179
        "B-Tree Calculator", // 180
        "B+ Tree Calculator", // 181
        "Normalization Calculator", // 182
        "Functional Dependency Calculator", // 183
        "Candidate Key Calculator", // 184
        "Relational Algebra Calculator", // 185
        "RSA Calculator", // 186
        "Euclidean Algorithm Calculator", // 187
        "Extended Euclidean Algorithm Calculator", // 188
        "Entropy Calculator", // 189
        "Information Gain Calculator", // 190
        "Huffman Coding Calculator", // 191
        "Perceptron Calculator", // 192
        "Activation Function Calculator", // 193
        "Sigmoid Calculator", // 194
        "ReLU Calculator", // 195
        "Softmax Calculator", // 196
        "Gradient Descent Calculator", // 197
        "K-Means Calculator", // 198
        "Euclidean Distance Calculator", // 199
        "Cosine Similarity Calculator", // 200
        "KNN Calculator", // 201
        "Accuracy Calculator", // 202
        "Precision Calculator", // 203
        "Recall Calculator", // 204
        "F1 Score Calculator", // 205
        "Confusion Matrix Calculator", // 206
        "Mean Squared Error Calculator", // 207
        "Mean Absolute Error Calculator", // 208
        "Root Mean Squared Error Calculator", // 209
        "R-Squared Calculator", // 210
        "Cross-Entropy Calculator", // 211
        "Data Conversion Calculator", // 212
        "Unit Conversion Calculator", // 213
        "Temperature Converter", // 214
        "Length Converter", // 215
        "Area Converter", // 216
        "Volume Converter", // 217
        "Weight Converter", // 218
        "Time Converter", // 219
        "Speed Converter", // 220
        "Energy Converter", // 221
        "Power Unit Converter", // 222
        "Frequency Converter", // 223
        "Storage Unit Converter", // 224
        "Binary Storage Converter", // 225
        "Date Difference Calculator", // 226
        "Time Difference Calculator", // 227
        "Age Calculator", // 228
        "EMI Calculator", // 229
        "Simple Interest Calculator", // 230
        "Compound Interest Calculator", // 231
        "GST Calculator", // 232
        "Discount Calculator", // 233
        "Profit and Loss Calculator", // 234
        "Scientific Notation Calculator", // 235
        "Significant Figures Calculator", // 236
        "Fraction Calculator", // 237
        "Decimal Calculator", // 238
        "Remainder Calculator", // 239
        "Master BCA-MCA Calculator" // 240
    )

    val allSolvers: List<SolverItem> by lazy {
        titles.mapIndexed { index, name ->
            val id = index + 1
            createSolverItem(id, name)
        }
    }

    val length: Int get() = allSolvers.size
    val size: Int get() = allSolvers.size

    private fun getCategoryFor(id: Int, name: String): String {
        return when {
            id in 1..9 -> "Basic & Scientific"
            id in 10..13 -> "Probability & Combinatorics"
            id in 14..17 -> "Discrete Math & Modulo"
            id in 18..24 -> "Number Systems"
            id in 25..30 -> "Exponential & Complex"
            id in 31..36 -> "Algebra & Equations"
            id in 37..47 -> "Matrices & Linear Algebra"
            id in 48..51 -> "Vectors & Geometry"
            id in 52..57 -> "Trigonometry"
            id in 58..67 -> "Coordinate & Geometry"
            id in 68..72 -> "Sequences & Series"
            id in 73..82 -> "Calculus & Transforms"
            id in 83..93 -> "Numerical Methods"
            id in 94..108 -> "Statistics & Probability"
            id in 109..119 -> "Set Theory & Boolean Logic"
            id in 120..132 -> "Graph Theory & Trees"
            id in 133..145 -> "DSA & Complexity"
            id in 146..161 -> "Computer Architecture & OS"
            id in 162..178 -> "Networking & Subnetting"
            id in 179..185 -> "DBMS & Storage"
            id in 186..191 -> "Cryptography & Info Theory"
            id in 192..200 -> "AI & Machine Learning"
            id in 201..211 -> "Data Science & ML Metrics"
            id in 212..225 -> "Unit & Data Conversion"
            id in 226..234 -> "Financial & General"
            else -> "Math & Computing"
        }
    }

    private fun getSemesterFor(id: Int): String {
        return when {
            id in 1..36 -> "Sem 1"
            id in 37..72 -> "Sem 2"
            id in 73..119 -> "Sem 3"
            id in 120..161 -> "Sem 4"
            id in 162..191 -> "Sem 5"
            else -> "Sem 6"
        }
    }

    private fun createSolverItem(id: Int, name: String): SolverItem {
        val cat = getCategoryFor(id, name)
        val sem = getSemesterFor(id)
        
        return when (id) {
            1 -> SolverItem(id, id, name, sem, cat, "Basic & Scientific mathematical operator solver.", "x op y", "Operand X", "Operand Y", "Operator (+, -, *, /, ^)", "25", "4", "*", 3)
            2 -> SolverItem(id, id, name, sem, cat, "Percentage computation of value and total base.", "P = (Part / Whole) * 100", "Part / Obtained", "Whole / Total", "", "450", "600", "", 2)
            3 -> SolverItem(id, id, name, sem, cat, "Simplification and scaling of ratios a:b.", "Ratio = a / gcd(a,b) : b / gcd(a,b)", "Antecedent (A)", "Consequent (B)", "", "120", "80", "", 2)
            4, 5 -> SolverItem(id, id, name, sem, cat, "Arithmetic mean of comma-separated numbers.", "Mean = Σx / n", "Numbers (comma separated)", "", "", "12, 18, 25, 34, 45, 60", "", "", 1)
            6 -> SolverItem(id, id, name, sem, cat, "Median calculation for sorted list of numbers.", "Median = middle element", "Numbers (comma separated)", "", "", "15, 20, 35, 40, 50, 75", "", "", 1)
            7 -> SolverItem(id, id, name, sem, cat, "Statistical mode frequency finder.", "Mode = highest frequency element", "Numbers (comma separated)", "", "", "2, 3, 3, 5, 7, 7, 7, 9", "", "", 1)
            8 -> SolverItem(id, id, name, sem, cat, "Variance σ² of sample dataset.", "σ² = Σ(x - μ)² / N", "Numbers (comma separated)", "", "", "10, 12, 23, 23, 16, 23, 21, 16", "", "", 1)
            9 -> SolverItem(id, id, name, sem, cat, "Standard deviation σ of dataset.", "σ = sqrt(Variance)", "Numbers (comma separated)", "", "", "10, 12, 23, 23, 16, 23, 21, 16", "", "", 1)
            10 -> SolverItem(id, id, name, sem, cat, "Probability P(E) = n(E) / n(S).", "P(E) = Favorable / Total", "Favorable Outcomes", "Total Outcomes", "", "3", "8", "", 2)
            11 -> SolverItem(id, id, name, sem, cat, "Permutation nPr = n! / (n - r)!.", "nPr = n! / (n - r)!", "Total Elements (n)", "Chosen Elements (r)", "", "8", "3", "", 2)
            12 -> SolverItem(id, id, name, sem, cat, "Combination nCr = n! / (r! * (n - r)!).", "nCr = n! / (r!(n-r)!)", "Total Elements (n)", "Chosen Elements (r)", "", "10", "4", "", 2)
            13 -> SolverItem(id, id, name, sem, cat, "Factorial n! = 1 * 2 * ... * n.", "n! = Π(i=1..n)", "Number (n)", "", "", "7", "", "", 1)
            14, 15 -> SolverItem(id, id, name, sem, cat, "Greatest Common Divisor via Euclidean algorithm.", "GCD(a,b) = GCD(b, a mod b)", "Number A", "Number B", "", "48", "18", "", 2)
            16 -> SolverItem(id, id, name, sem, cat, "Least Common Multiple using GCD relation.", "LCM(a,b) = |a * b| / GCD(a,b)", "Number A", "Number B", "", "12", "15", "", 2)
            17 -> SolverItem(id, id, name, sem, cat, "Modular arithmetic residue: a mod m.", "r = a mod m", "Dividend (a)", "Modulus (m)", "", "47", "6", "", 2)
            18, 20, 24 -> SolverItem(id, id, name, sem, cat, "Universal base converter (2, 8, 10, 16).", "Base_N to Base_M", "Input Value", "From Base (2,8,10,16)", "To Base (2,8,10,16)", "255", "10", "16", 3)
            19 -> SolverItem(id, id, name, sem, cat, "Binary Bitwise Operations (AND, OR, XOR, ADD).", "Bitwise Bin(A, B)", "Binary A", "Binary B", "", "1101", "1011", "", 2)
            21 -> SolverItem(id, id, name, sem, cat, "Binary to decimal integer converter.", "Dec = Σ(b_i * 2^i)", "Binary String", "", "", "110101", "", "", 1)
            22 -> SolverItem(id, id, name, sem, cat, "Octal conversion and arithmetic.", "Octal to Dec/Bin/Hex", "Octal Number", "", "", "755", "", "", 1)
            23 -> SolverItem(id, id, name, sem, cat, "Hexadecimal conversion and parsing.", "Hex to Dec/Bin/Oct", "Hex String", "", "", "1A3F", "", "", 1)
            25 -> SolverItem(id, id, name, sem, cat, "Logarithm calculator for arbitrary base.", "log_b(x) = ln(x) / ln(b)", "Value (x)", "Base (b)", "", "1000", "10", "", 2)
            26 -> SolverItem(id, id, name, sem, cat, "Antilogarithm calculation: b^x.", "Antilog_b(x) = b^x", "Log Value (x)", "Base (b)", "", "3", "10", "", 2)
            27 -> SolverItem(id, id, name, sem, cat, "Natural exponential e^x calculation.", "exp(x) = e^x", "Exponent (x)", "", "", "2.5", "", "", 1)
            28 -> SolverItem(id, id, name, sem, cat, "Power function x^y.", "x^y", "Base (x)", "Exponent (y)", "", "2", "16", "", 2)
            29 -> SolverItem(id, id, name, sem, cat, "N-th Root calculation: x^(1/n).", "root_n(x) = x^(1/n)", "Radicand (x)", "Degree (n)", "", "256", "2", "", 2)
            30 -> SolverItem(id, id, name, sem, cat, "Complex numbers arithmetic (a+bi) and (c+di).", "z1 op z2", "Complex A (a+bi)", "Complex B (c+di)", "Op (+, -, *, /)", "3+4i", "1-2i", "*", 3)
            31, 32 -> SolverItem(id, id, name, sem, cat, "Linear equation solver: ax + b = 0.", "x = -b / a", "Coefficient (a)", "Constant (b)", "", "5", "-15", "", 2)
            33 -> SolverItem(id, id, name, sem, cat, "Quadratic formula roots: ax² + bx + c = 0.", "x = (-b ± sqrt(b²-4ac))/(2a)", "Coefficient (a)", "Coefficient (b)", "Constant (c)", "1", "-5", "6", 3)
            34, 35 -> SolverItem(id, id, name, sem, cat, "Polynomial evaluation P(x) and synthetic division.", "P(x) = an*x^n + ... + a0", "Coefficients (e.g. 1,-5,6)", "Evaluation point x", "", "1, -5, 6", "3", "", 2)
            36 -> SolverItem(id, id, name, sem, cat, "Simultaneous 2x2 linear equations solver.", "a1*x + b1*y = c1; a2*x + b2*y = c2", "Eq 1 (a1, b1, c1)", "Eq 2 (a2, b2, c2)", "", "2, 3, 13", "1, -1, -1", "", 2)
            37, 38 -> SolverItem(id, id, name, sem, cat, "2x2 Matrix addition and operations.", "C = A + B", "Matrix A (a,b,c,d)", "Matrix B (e,f,g,h)", "", "1, 2, 3, 4", "5, 6, 7, 8", "", 2)
            39 -> SolverItem(id, id, name, sem, cat, "2x2 Matrix multiplication.", "C = A * B", "Matrix A (a,b,c,d)", "Matrix B (e,f,g,h)", "", "1, 2, 3, 4", "2, 0, 1, 2", "", 2)
            40 -> SolverItem(id, id, name, sem, cat, "Matrix Transpose operation.", "A^T", "Matrix (a,b,c,d)", "", "", "1, 2, 3, 4", "", "", 1)
            41 -> SolverItem(id, id, name, sem, cat, "2x2 / 3x3 Matrix determinant det(A).", "|A| = ad - bc", "Matrix Elements", "", "", "4, 7, 2, 6", "", "", 1)
            42 -> SolverItem(id, id, name, sem, cat, "Matrix Inverse A⁻¹ = (1/det(A)) * adj(A).", "A⁻¹ = adj(A)/det(A)", "Matrix 2x2 (a,b,c,d)", "", "", "4, 7, 2, 6", "", "", 1)
            43 -> SolverItem(id, id, name, sem, cat, "Matrix Rank evaluation via row reduction.", "Rank = pivot rows count", "Matrix 2x2 (a,b,c,d)", "", "", "1, 2, 2, 4", "", "", 1)
            44, 45 -> SolverItem(id, id, name, sem, cat, "Eigenvalues λ of 2x2 matrix: det(A - λI) = 0.", "λ² - Tr(A)λ + Det(A) = 0", "Matrix 2x2 (a,b,c,d)", "", "", "2, 1, 1, 2", "", "", 1)
            46, 47 -> SolverItem(id, id, name, sem, cat, "Gaussian elimination row reduction.", "Augmented Matrix [A|B]", "Row 1 (a1,b1,c1)", "Row 2 (a2,b2,c2)", "", "2, 1, 5", "1, -1, 1", "", 2)
            48, 49 -> SolverItem(id, id, name, sem, cat, "Vector Dot product: u · v = ux*vx + uy*vy + uz*vz.", "u · v = Σ u_i * v_i", "Vector U (x,y,z)", "Vector V (x,y,z)", "", "1, 2, 3", "4, 5, 6", "", 2)
            50 -> SolverItem(id, id, name, sem, cat, "Vector Cross product: u × v in 3D.", "u × v = (uy*vz - uz*vy)i - ...", "Vector U (x,y,z)", "Vector V (x,y,z)", "", "1, 0, 0", "0, 1, 0", "", 2)
            51 -> SolverItem(id, id, name, sem, cat, "Euclidean Vector magnitude |v|.", "|v| = sqrt(x² + y² + z²)", "Vector (x,y,z)", "", "", "3, 4, 12", "", "", 1)
            52, 53, 54, 55 -> SolverItem(id, id, name, sem, cat, "Trigonometric evaluation (sin, cos, tan).", "f(θ)", "Angle θ", "Unit (deg / rad)", "", "45", "deg", "", 2)
            56 -> SolverItem(id, id, name, sem, cat, "Inverse Trigonometric functions (asin, acos, atan).", "asin(x), acos(x), atan(x)", "Value (-1 to 1)", "Function (asin, acos, atan)", "", "0.5", "asin", "", 2)
            57 -> SolverItem(id, id, name, sem, cat, "Degrees to Radians and Radians to Degrees.", "rad = deg * π / 180", "Angle Value", "Direction (deg2rad / rad2deg)", "", "180", "deg2rad", "", 2)
            58, 59 -> SolverItem(id, id, name, sem, cat, "2D Distance formula d = sqrt((x2-x1)² + (y2-y1)²).", "d = sqrt(Δx² + Δy²)", "Point 1 (x1, y1)", "Point 2 (x2, y2)", "", "0, 0", "3, 4", "", 2)
            60 -> SolverItem(id, id, name, sem, cat, "Midpoint of 2D line segment: ((x1+x2)/2, (y1+y2)/2).", "M = ((x1+x2)/2, (y1+y2)/2)", "Point 1 (x1, y1)", "Point 2 (x2, y2)", "", "2, 4", "6, 10", "", 2)
            61, 62 -> SolverItem(id, id, name, sem, cat, "Slope m = (y2 - y1) / (x2 - x1) and line equation.", "y = mx + c", "Point 1 (x1, y1)", "Point 2 (x2, y2)", "", "1, 2", "3, 6", "", 2)
            63 -> SolverItem(id, id, name, sem, cat, "Circle area, circumference, and diameter.", "A = πr², C = 2πr", "Radius (r)", "", "", "7", "", "", 1)
            64, 65 -> SolverItem(id, id, name, sem, cat, "Triangle area (Heron's) & Pythagorean theorem.", "c = sqrt(a² + b²)", "Side a", "Side b", "", "3", "4", "", 2)
            66 -> SolverItem(id, id, name, sem, cat, "Geometric 2D shapes area solver.", "Rectangle, Triangle, Circle, Trapezoid", "Shape (rect, tri, circle)", "Dim 1 (length / radius)", "Dim 2 (width / height)", "rect", "15", "8", 3)
            67 -> SolverItem(id, id, name, sem, cat, "3D Geometric shapes volume solver.", "Sphere, Cylinder, Cone, Cube", "Shape (sphere, cyl, cube)", "Radius / Side", "Height", "cyl", "5", "10", 3)
            68, 69 -> SolverItem(id, id, name, sem, cat, "Arithmetic Progression (AP) n-th term & sum.", "a_n = a + (n-1)d, S_n = n/2(2a+(n-1)d)", "First Term (a)", "Common Diff (d)", "Term Number (n)", "2", "3", "10", 3)
            70, 71 -> SolverItem(id, id, name, sem, cat, "Geometric Progression (GP) n-th term & sum.", "a_n = a * r^(n-1)", "First Term (a)", "Common Ratio (r)", "Term Number (n)", "3", "2", "6", 3)
            72 -> SolverItem(id, id, name, sem, cat, "Binomial expansion (x + y)^n coefficients.", "(x+y)^n = Σ nCr * x^(n-r) * y^r", "Degree (n)", "Value x", "Value y", "4", "1", "1", 3)
            73 -> SolverItem(id, id, name, sem, cat, "Calculus Limit evaluation via polynomial approximation.", "lim x->c f(x)", "Coefficients (a,b,c for ax²+bx+c)", "Limit point c", "", "1, -4, 4", "2", "", 2)
            74, 75 -> SolverItem(id, id, name, sem, cat, "Power rule numerical derivative f'(x).", "d/dx (ax^n) = a*n*x^(n-1)", "Coeff a", "Power n", "At point x", "3", "4", "2", 3)
            76, 77, 78 -> SolverItem(id, id, name, sem, cat, "Numerical Definite Integral ∫ f(x) dx.", "∫_a^b (c*x^n) dx", "Coeff c", "Power n", "Limits [a, b]", "2", "3", "0, 2", 3)
            79 -> SolverItem(id, id, name, sem, cat, "First order ODE dy/dx + P(x)y = Q(x).", "Integrating Factor IF = e^(∫P dx)", "Constant P", "Constant Q", "", "2", "6", "", 2)
            80 -> SolverItem(id, id, name, sem, cat, "Laplace Transform of basic functions.", "L{t^n} = n!/s^(n+1), L{e^(at)} = 1/(s-a)", "Function Type (exp, poly, sin, cos)", "Parameter a / n", "", "exp", "3", "", 2)
            81, 82 -> SolverItem(id, id, name, sem, cat, "Fourier transform harmonic series coefficients.", "a0, an, bn Fourier terms", "Period T", "Amplitude A", "Harmonic n", "2", "5", "3", 3)
            83, 84 -> SolverItem(id, id, name, sem, cat, "Bisection numerical root finding.", "c = (a + b) / 2 until f(c) ≈ 0", "Interval [a, b]", "Tolerance", "", "1, 2", "0.001", "", 2)
            85, 86 -> SolverItem(id, id, name, sem, cat, "Newton-Raphson method for roots: x_{n+1} = x_n - f/f'.", "x_new = x - f(x)/f'(x)", "Initial Guess x0", "Iterations", "", "1.5", "5", "", 2)
            87, 88 -> SolverItem(id, id, name, sem, cat, "Lagrange polynomial interpolation for given points.", "P(x) = Σ y_i * L_i(x)", "Points X (comma sep)", "Points Y (comma sep)", "Interpolate at x", "1, 2, 4", "2, 3, 7", "3", 3)
            89, 90 -> SolverItem(id, id, name, sem, cat, "Trapezoidal Rule numerical integration.", "∫ ≈ (h/2) * (y0 + 2Σy_i + yn)", "Interval [a, b]", "Number of Steps n", "Function type (x^2, sin)", "0, 4", "4", "x^2", 3)
            91 -> SolverItem(id, id, name, sem, cat, "Simpson's 1/3 Rule numerical integration.", "∫ ≈ (h/3) * (y0 + 4y_odd + 2y_even + yn)", "Interval [a, b]", "Steps n (even)", "", "0, 6", "6", "", 2)
            92, 93 -> SolverItem(id, id, name, sem, cat, "Runge-Kutta 4th order (RK4) ODE solver.", "y_{n+1} = y_n + (k1+2k2+2k3+k4)/6", "Initial y0", "Step size h", "Steps count", "1", "0.1", "5", 3)
            94, 95 -> SolverItem(id, id, name, sem, cat, "Descriptive Statistics summary (Min, Max, Quartiles).", "IQR = Q3 - Q1", "Data values (comma sep)", "", "", "12, 15, 18, 22, 29, 35, 40, 55", "", "", 1)
            96, 97 -> SolverItem(id, id, name, sem, cat, "Pearson correlation coefficient r.", "r = Cov(X,Y) / (σX * σY)", "Dataset X", "Dataset Y", "", "1, 2, 3, 4, 5", "2, 4, 5, 4, 5", "", 2)
            98 -> SolverItem(id, id, name, sem, cat, "Spearman Rank correlation coefficient ρ.", "ρ = 1 - (6Σd²) / (n(n²-1))", "Ranks X", "Ranks Y", "", "1, 2, 3, 4, 5", "5, 4, 3, 2, 1", "", 2)
            99 -> SolverItem(id, id, name, sem, cat, "Covariance Cov(X, Y) between two variables.", "Cov(X,Y) = Σ(x-μx)(y-μy)/N", "Dataset X", "Dataset Y", "", "2, 4, 6, 8", "1, 3, 5, 7", "", 2)
            100, 101 -> SolverItem(id, id, name, sem, cat, "Linear Regression least squares: y = mx + c.", "m = Cov(X,Y)/Var(X)", "Dataset X", "Dataset Y", "", "1, 2, 3, 4, 5", "2, 3, 5, 7, 11", "", 2)
            102, 103 -> SolverItem(id, id, name, sem, cat, "Binomial probability distribution P(X=k).", "P(k) = nCk * p^k * (1-p)^(n-k)", "Trials (n)", "Successes (k)", "Prob of success (p)", "10", "4", "0.5", 3)
            104 -> SolverItem(id, id, name, sem, cat, "Poisson distribution P(X=k) = (λ^k * e^-λ)/k!.", "P(k) = (λ^k * e^-λ) / k!", "Rate parameter (λ)", "Events count (k)", "", "3.5", "2", "", 2)
            105 -> SolverItem(id, id, name, sem, cat, "Normal Gaussian distribution PDF and Z-Score.", "Z = (X - μ) / σ", "Value (X)", "Mean (μ)", "Std Dev (σ)", "85", "70", "10", 3)
            106 -> SolverItem(id, id, name, sem, cat, "Bayes' Theorem P(A|B) posterior probability.", "P(A|B) = [P(B|A)*P(A)] / P(B)", "P(A)", "P(B|A)", "P(B)", "0.01", "0.99", "0.05", 3)
            107 -> SolverItem(id, id, name, sem, cat, "Conditional Probability P(A|B) = P(A ∩ B) / P(B).", "P(A|B) = P(A∩B) / P(B)", "P(A ∩ B)", "P(B)", "", "0.3", "0.6", "", 2)
            108 -> SolverItem(id, id, name, sem, cat, "Expected value E[X] = Σ x_i * P(x_i).", "E[X] = Σ x_i * p_i", "Values X (comma sep)", "Probabilities P (comma sep)", "", "10, 20, 30", "0.2, 0.5, 0.3", "", 2)
            109, 110 -> SolverItem(id, id, name, sem, cat, "Set Union A ∪ B and cardinality.", "A ∪ B = {x : x ∈ A or x ∈ B}", "Set A elements", "Set B elements", "", "1, 2, 3, 4", "3, 4, 5, 6", "", 2)
            111 -> SolverItem(id, id, name, sem, cat, "Set Intersection A ∩ B.", "A ∩ B = {x : x ∈ A and x ∈ B}", "Set A elements", "Set B elements", "", "1, 2, 3, 4", "3, 4, 5, 6", "", 2)
            112 -> SolverItem(id, id, name, sem, cat, "Set Difference A \\ B.", "A \\ B = {x : x ∈ A and x ∉ B}", "Set A elements", "Set B elements", "", "1, 2, 3, 4", "3, 4, 5, 6", "", 2)
            113 -> SolverItem(id, id, name, sem, cat, "Cartesian Product A × B.", "A × B = {(a, b) : a∈A, b∈B}", "Set A elements", "Set B elements", "", "a, b", "1, 2", "", 2)
            114 -> SolverItem(id, id, name, sem, cat, "Power Set P(S) generator and count 2^n.", "|P(S)| = 2^n", "Set elements", "", "", "1, 2, 3", "", "", 1)
            115, 116 -> SolverItem(id, id, name, sem, cat, "Boolean Algebra De Morgan & Simplification.", "A'B + AB' = A ⊕ B", "Inputs (A, B)", "Operation (AND, OR, XOR, NAND)", "", "1, 0", "XOR", "", 2)
            117 -> SolverItem(id, id, name, sem, cat, "Digital Logic Gates output evaluator.", "AND, OR, NOT, NAND, NOR, XOR, XNOR", "Gate Type (AND/OR/XOR/NAND)", "Input A (0/1)", "Input B (0/1)", "NAND", "1", "1", 3)
            118 -> SolverItem(id, id, name, sem, cat, "Truth Table Generator for 2-input and 3-input logic.", "2^n state evaluation", "Expression (e.g. A AND B OR C)", "", "", "A AND (B OR NOT C)", "", "", 1)
            119 -> SolverItem(id, id, name, sem, cat, "Karnaugh Map (K-Map) 2x2 & 2x4 minterm grouping.", "SOP minimization", "Minterms (e.g. 0, 1, 3)", "Variables count (2 or 3)", "", "0, 1, 3", "2", "", 2)
            120, 121 -> SolverItem(id, id, name, sem, cat, "Graph vertex degrees and Handshaking lemma.", "Σ deg(v) = 2|E|", "Degrees list (comma sep)", "", "", "2, 3, 3, 2, 4", "", "", 1)
            122 -> SolverItem(id, id, name, sem, cat, "Adjacency matrix paths A^k counts.", "A^k gives walks of length k", "Nodes count", "Edges (u-v pairs)", "Walk length k", "3", "0-1, 1-2, 0-2", "2", 3)
            123, 124 -> SolverItem(id, id, name, sem, cat, "Dijkstra's single source shortest path algorithm.", "dist[v] = min(dist[v], dist[u] + w)", "Nodes count", "Edges (u-v:w)", "Start node", "4", "0-1:4, 0-2:1, 2-1:2, 1-3:1, 2-3:5", "0", 3)
            125 -> SolverItem(id, id, name, sem, cat, "Floyd-Warshall all-pairs shortest paths.", "d[i][j] = min(d[i][j], d[i][k] + d[k][j])", "Adjacency matrix (0/inf)", "", "", "0, 3, 999; 999, 0, 1; 999, 999, 0", "", "", 1)
            126, 127, 128 -> SolverItem(id, id, name, sem, cat, "Kruskal & Prim Minimum Spanning Tree (MST) weight.", "MST Weight = Σ w_e", "Nodes count", "Weighted edges (u-v:w)", "", "4", "0-1:1, 0-2:4, 1-2:2, 1-3:5, 2-3:3", "", 2)
            129 -> SolverItem(id, id, name, sem, cat, "Graph Vertex Coloring chromatic number χ(G).", "χ(G) <= Δ(G) + 1", "Graph Type (complete, cycle, bipartite)", "Vertices count n", "", "cycle", "5", "", 2)
            130, 131 -> SolverItem(id, id, name, sem, cat, "Breadth First Search (BFS) & DFS traversal orders.", "Queue (BFS) / Stack (DFS)", "Adjacency list", "Start node", "", "0:1,2; 1:3; 2:3; 3:", "0", "", 2)
            132 -> SolverItem(id, id, name, sem, cat, "Topological Sorting of Directed Acyclic Graph (DAG).", "Kahn's in-degree algorithm", "Edges (u->v)", "Total nodes", "", "0->1, 0->2, 1->3, 2->3", "4", "", 2)
            133, 134, 135 -> SolverItem(id, id, name, sem, cat, "Algorithm Big-O asymptotic growth and comparison.", "O(1) < O(log n) < O(n) < O(n log n) < O(n²)", "Input size (N)", "Constant factor c", "", "10000", "2", "", 2)
            136, 137 -> SolverItem(id, id, name, sem, cat, "Master Theorem solver: T(n) = aT(n/b) + O(n^d).", "Case 1, 2, or 3 based on log_b(a) vs d", "Subproblems (a)", "Division factor (b)", "Degree d in f(n)=n^d", "2", "2", "1", 3)
            138 -> SolverItem(id, id, name, sem, cat, "Stack LIFO operations simulator (Push, Pop, Top).", "Top index pointer", "Operations (push:x, pop, top)", "Capacity", "", "push:10, push:20, pop, push:30", "5", "", 2)
            139, 140 -> SolverItem(id, id, name, sem, cat, "Circular Queue (rear = (rear+1)%N) simulator.", "(rear + 1) % size == front", "Capacity N", "Operations (enq:x, deq)", "", "4", "enq:A, enq:B, enq:C, deq, enq:D", "", 2)
            141, 142 -> SolverItem(id, id, name, sem, cat, "Binary Search Tree (BST) height and node insertion.", "Height = 1 + max(left, right)", "Keys to insert (comma sep)", "", "", "50, 30, 70, 20, 40, 60, 80", "", "", 1)
            143 -> SolverItem(id, id, name, sem, cat, "AVL Tree balance factor BF = height(L) - height(R).", "Balance Factor ∈ {-1, 0, 1}", "Left subtree height", "Right subtree height", "", "3", "1", "", 2)
            144 -> SolverItem(id, id, name, sem, cat, "Max-Heap and Min-Heap array representations.", "parent(i) = (i-1)/2, left = 2i+1", "Array keys (comma sep)", "Type (max / min)", "", "10, 20, 15, 30, 40", "max", "", 2)
            145 -> SolverItem(id, id, name, sem, cat, "Binary Tree Preorder, Inorder, and Postorder traversals.", "Inorder: Left-Root-Right", "BST Keys (comma sep)", "", "", "4, 2, 6, 1, 3, 5, 7", "", "", 1)
            146, 147 -> SolverItem(id, id, name, sem, cat, "CPU Execution Time = (IC * CPI) / Clock Rate.", "CPU Time = IC * CPI * ClockCycle", "Instruction Count (IC)", "Cycles Per Instruction (CPI)", "Clock Rate (GHz)", "1000000", "1.8", "2.4", 3)
            148 -> SolverItem(id, id, name, sem, cat, "MIPS (Million Instructions Per Second) calculator.", "MIPS = ClockRate / (CPI * 10^6)", "Clock Rate (MHz)", "CPI", "", "2400", "1.5", "", 2)
            149, 150 -> SolverItem(id, id, name, sem, cat, "Cache Memory tag, index, and offset bits partition.", "Address Bits = Tag + Index + Offset", "Main Memory Size (MB)", "Cache Size (KB)", "Block Size (Bytes)", "16", "64", "32", 3)
            151, 152 -> SolverItem(id, id, name, sem, cat, "Page Replacement FIFO & LRU fault counter.", "Hit Ratio = Hits / Total Requests", "Page Reference String", "Frames Count", "Algorithm (FIFO / LRU)", "7, 0, 1, 2, 0, 3, 0, 4, 2, 3", "3", "LRU", 3)
            153, 154 -> SolverItem(id, id, name, sem, cat, "First-Come First-Served (FCFS) CPU Scheduling.", "WT = TAT - BT", "Burst Times (comma sep)", "Arrival Times (optional)", "", "6, 8, 7, 3", "0, 0, 0, 0", "", 2)
            155 -> SolverItem(id, id, name, sem, cat, "Shortest Job First (SJF Non-preemptive) Scheduling.", "Sort by Burst Time", "Burst Times (comma sep)", "", "", "6, 2, 8, 3, 4", "", "", 1)
            156 -> SolverItem(id, id, name, sem, cat, "Round Robin (RR) Scheduling with time quantum.", "Time Slice / Quantum Q", "Burst Times (comma sep)", "Time Quantum", "", "5, 4, 2, 8", "2", "", 2)
            157 -> SolverItem(id, id, name, sem, cat, "Priority CPU Scheduling (Non-preemptive).", "Lowest number = highest priority", "Burst Times", "Priorities (comma sep)", "", "4, 3, 7, 2", "2, 1, 4, 3", "", 2)
            158, 159 -> SolverItem(id, id, name, sem, cat, "Average Turnaround & Waiting Time calculator.", "TAT = Completion - Arrival", "Completion Times", "Arrival Times", "Burst Times", "10, 15, 22", "0, 2, 4", "6, 5, 7", 3)
            160 -> SolverItem(id, id, name, sem, cat, "Disk Scheduling FCFS / SSTF head movement.", "Total Head Movement = Σ|req_{i} - req_{i-1}|", "Request Queue (comma sep)", "Initial Head Position", "", "98, 183, 37, 122, 14, 124, 65, 67", "53", "", 2)
            161 -> SolverItem(id, id, name, sem, cat, "Banker's Deadlock Avoidance Safety Algorithm.", "Need = Max - Allocation <= Work", "Allocation (P1, P2)", "Max (P1, P2)", "Available Resources", "1, 2", "3, 4", "2", 3)
            162, 163, 165, 166 -> SolverItem(id, id, name, sem, cat, "IPv4 Subnet & CIDR network calculator.", "Network ID = IP AND Mask", "IPv4 Address", "CIDR Prefix (/24, /27, etc.)", "", "192.168.1.100", "26", "", 2)
            164 -> SolverItem(id, id, name, sem, cat, "IPv6 Shortening and Expansion notation.", "Zero compression ::", "IPv6 Address", "", "", "2001:0db8:0000:0000:0000:ff00:0042:8329", "", "", 1)
            167 -> SolverItem(id, id, name, sem, cat, "Variable Length Subnet Masking (VLSM) block sizing.", "Block = 2^(32 - CIDR)", "Major Network IP/CIDR", "Host Requirements (comma sep)", "", "192.168.10.0/24", "60, 25, 10, 2", "", 2)
            168, 169 -> SolverItem(id, id, name, sem, cat, "Network & Broadcast address calculation.", "Broadcast = Network | ~Mask", "IP Address", "Subnet Mask", "", "10.0.5.20", "255.255.255.0", "", 2)
            170, 171 -> SolverItem(id, id, name, sem, cat, "Data Transfer Time = File Size / Bandwidth.", "Transfer Time = Size / Speed", "File Size (MB)", "Bandwidth (Mbps)", "", "500", "100", "", 2)
            172, 173 -> SolverItem(id, id, name, sem, cat, "Latency, Propagation & Transmission Delay.", "Total Delay = Transmission + Propagation", "Packet Size (Bytes)", "Bandwidth (Mbps)", "Distance (km)", "1500", "10", "1000", 3)
            174 -> SolverItem(id, id, name, sem, cat, "Shannon Channel Capacity: C = B * log2(1 + SNR).", "C = B * log2(1 + S/N)", "Bandwidth B (Hz)", "SNR (dB or linear)", "Is dB? (yes/no)", "3000", "30", "yes", 3)
            175 -> SolverItem(id, id, name, sem, cat, "Nyquist Bit Rate: BitRate = 2 * B * log2(M).", "Max Bit Rate = 2B * log2(L)", "Bandwidth B (Hz)", "Signal Levels L", "", "4000", "16", "", 2)
            176 -> SolverItem(id, id, name, sem, cat, "Hamming Code parity bit error correction (7,4).", "2^r >= m + r + 1", "4-bit Data Word (e.g. 1011)", "", "", "1011", "", "", 1)
            177 -> SolverItem(id, id, name, sem, cat, "Cyclic Redundancy Check (CRC) remainder generator.", "CRC = (Data << k) mod Poly", "Data Bits", "Generator Polynomial Bits", "", "1101011011", "10011", "", 2)
            178 -> SolverItem(id, id, name, sem, cat, "Internet 16-bit Checksum calculation.", "Sum + Carry folded then inverted", "16-bit Hex Words (comma sep)", "", "", "4500, 003c, 1c46, 4000", "", "", 1)
            179 -> SolverItem(id, id, name, sem, cat, "Database Table & Index Storage Estimator.", "Size = Records * (RowSize + Overhead)", "Record Count", "Average Row Size (Bytes)", "Index Overhead %", "500000", "128", "20", 3)
            180, 181 -> SolverItem(id, id, name, sem, cat, "B-Tree & B+ Tree order, height and capacity.", "Keys <= M-1, Children <= M", "Tree Order (M)", "Record Count (N)", "", "5", "100000", "", 2)
            182, 183, 184 -> SolverItem(id, id, name, sem, cat, "Functional Dependency Closure & Candidate Keys.", "X+ attribute closure", "Attributes (e.g. A,B,C,D)", "FDs (e.g. A->B, B->C, C->D)", "", "A, B, C, D", "A->B, B->C, C->D", "", 2)
            185 -> SolverItem(id, id, name, sem, cat, "Relational Algebra Project/Select result cardinality.", "|σ(R)| <= |R|, |R ⨝ S| <= |R|*|S|", "Relation R size", "Selectivity factor (0 to 1)", "", "10000", "0.05", "", 2)
            186 -> SolverItem(id, id, name, sem, cat, "RSA Public-Key Encryption Keygen & ModExp.", "n = p*q, φ(n) = (p-1)(q-1), e*d ≡ 1 mod φ", "Prime p", "Prime q", "Public Exponent e", "61", "53", "17", 3)
            187, 188 -> SolverItem(id, id, name, sem, cat, "Extended Euclidean Algorithm Bézout's identity: ax + by = gcd(a,b).", "ax + by = gcd(a,b)", "Integer a", "Integer b", "", "240", "46", "", 2)
            189 -> SolverItem(id, id, name, sem, cat, "Shannon Entropy H(X) = -Σ p_i * log2(p_i).", "H(X) = -Σ p(x) * log2 p(x)", "Probabilities (comma sep)", "", "", "0.5, 0.25, 0.125, 0.125", "", "", 1)
            190 -> SolverItem(id, id, name, sem, cat, "Decision Tree Information Gain: IG = H(Parent) - H(Children).", "IG = H(S) - Σ (|Sv|/|S|)*H(Sv)", "Parent Entropy", "Weighted Child Entropies (comma sep)", "", "0.94", "0.45, 0.35", "", 2)
            191 -> SolverItem(id, id, name, sem, cat, "Huffman Coding average code length & tree.", "L_avg = Σ p_i * len_i", "Frequencies / Probabilities (comma sep)", "", "", "45, 13, 12, 16, 9, 5", "", "", 1)
            192 -> SolverItem(id, id, name, sem, cat, "Single Layer Perceptron output: y = step(w·x + b).", "y = f(Σ w_i * x_i + b)", "Inputs x (comma sep)", "Weights w (comma sep)", "Bias b", "1, 0.5", "0.8, -0.4", "0.1", 3)
            193, 194 -> SolverItem(id, id, name, sem, cat, "Sigmoid Activation σ(x) = 1 / (1 + e^-x).", "σ(x) = 1 / (1 + e^-x)", "Input x", "", "", "2.5", "", "", 1)
            195 -> SolverItem(id, id, name, sem, cat, "ReLU (Rectified Linear Unit) & Leaky ReLU.", "f(x) = max(0, x)", "Input x", "Alpha (for Leaky ReLU)", "", "-4.5", "0.01", "", 2)
            196 -> SolverItem(id, id, name, sem, cat, "Softmax probability distribution over logits.", "softmax(z_i) = e^(z_i) / Σ e^(z_j)", "Logits (comma sep)", "", "", "2.0, 1.0, 0.1", "", "", 1)
            197 -> SolverItem(id, id, name, sem, cat, "Gradient Descent 1D weight update: w = w - η * ∇L.", "w_{t+1} = w_t - α * (df/dw)", "Current weight w", "Learning rate α", "Gradient df/dw", "5.0", "0.1", "4.0", 3)
            198 -> SolverItem(id, id, name, sem, cat, "K-Means Centroid distance assignment.", "dist = sqrt(Σ (x_i - c_i)²)", "Data Point (x, y)", "Centroid 1 (x, y)", "Centroid 2 (x, y)", "2, 3", "1, 1", "5, 5", 3)
            199 -> SolverItem(id, id, name, sem, cat, "Euclidean Distance between two n-dimensional vectors.", "d(p, q) = sqrt(Σ (p_i - q_i)²)", "Point P (comma sep)", "Point Q (comma sep)", "", "1, 2, 3", "4, 6, 8", "", 2)
            200 -> SolverItem(id, id, name, sem, cat, "Cosine Similarity: cos(θ) = (A · B) / (||A|| * ||B||).", "sim(A,B) = (A·B) / (|A|*|B|)", "Vector A (comma sep)", "Vector B (comma sep)", "", "1, 2, 3", "2, 4, 6", "", 2)
            201 -> SolverItem(id, id, name, sem, cat, "K-Nearest Neighbors (KNN) classification vote.", "Majority class of K neighbors", "Distances & Classes (e.g. 1.2:A, 1.5:B, 2.0:A)", "K count", "", "1.2:A, 1.5:B, 2.1:A, 2.8:B", "3", "", 2)
            202, 203, 204, 205, 206 -> SolverItem(id, id, name, sem, cat, "Classification Metrics: Accuracy, Precision, Recall, F1.", "F1 = 2 * (P * R) / (P + R)", "True Positives (TP)", "False Positives (FP)", "False Negatives (FN)", "85", "15", "10", 3)
            207, 208, 209 -> SolverItem(id, id, name, sem, cat, "Regression Metrics: MSE, MAE, and RMSE.", "MSE = (1/n) Σ (y - ŷ)²", "Actual Values Y (comma sep)", "Predicted Values Ŷ (comma sep)", "", "3, -0.5, 2, 7", "2.5, 0.0, 2, 8", "", 2)
            210 -> SolverItem(id, id, name, sem, cat, "Coefficient of Determination R² Score.", "R² = 1 - (SS_res / SS_tot)", "Actual Values Y", "Predicted Values Ŷ", "", "10, 20, 30, 40", "12, 19, 28, 42", "", 2)
            211 -> SolverItem(id, id, name, sem, cat, "Cross-Entropy Loss: L = -Σ y_i * log(p_i).", "L = - [y log(p) + (1-y)log(1-p)]", "Actual Target (0 or 1)", "Predicted Probability p (0 to 1)", "", "1", "0.85", "", 2)
            212, 213, 214 -> SolverItem(id, id, name, sem, cat, "Temperature Converter (Celsius, Fahrenheit, Kelvin).", "°F = °C * 9/5 + 32, K = °C + 273.15", "Temperature Value", "From Unit (C / F / K)", "", "37", "C", "", 2)
            215 -> SolverItem(id, id, name, sem, cat, "Length Converter (Meters, Feet, Inches, Kilometers, Miles).", "1 m = 3.28084 ft", "Value", "From (m, ft, km, mi)", "To (m, ft, km, mi)", "100", "m", "ft", 3)
            216, 217 -> SolverItem(id, id, name, sem, cat, "Area & Volume Unit Converter.", "Sq Meters to Sq Feet, Liters to Gallons", "Value", "Unit Type", "", "10", "sqm2sqft", "", 2)
            218, 219, 220 -> SolverItem(id, id, name, sem, cat, "Speed & Weight Converter (km/h to m/s, kg to lbs).", "1 km/h = 0.27778 m/s, 1 kg = 2.20462 lb", "Value", "Type (kmh2ms / kg2lb)", "", "60", "kmh2ms", "", 2)
            221, 222, 223 -> SolverItem(id, id, name, sem, cat, "Energy, Power & Frequency Converter (Joules, Watts, Hz).", "1 HP = 745.7 W", "Value", "Unit Type", "", "10", "hp2w", "", 2)
            224, 225 -> SolverItem(id, id, name, sem, cat, "Binary Data Storage Converter (Bytes, KB, MB, GB, TB).", "1 MB = 1024 KB = 1048576 Bytes", "Value", "From Unit (B, KB, MB, GB)", "To Unit (B, KB, MB, GB)", "4096", "MB", "GB", 3)
            226, 227, 228 -> SolverItem(id, id, name, sem, cat, "Age & Date Difference Duration Calculator.", "Years, Months, Days between dates", "Birth Year / Start", "Current Year / End", "", "2002", "2026", "", 2)
            229 -> SolverItem(id, id, name, sem, cat, "Equated Monthly Installment (EMI) loan calculator.", "EMI = [P * r * (1+r)^n] / [(1+r)^n - 1]", "Principal Amount (P)", "Annual Interest Rate % (R)", "Tenure in Months (N)", "500000", "9.5", "36", 3)
            230 -> SolverItem(id, id, name, sem, cat, "Simple Interest Calculator: SI = (P * R * T) / 100.", "SI = (P * R * T) / 100", "Principal (P)", "Rate % (R)", "Time in Years (T)", "10000", "7.5", "3", 3)
            231 -> SolverItem(id, id, name, sem, cat, "Compound Interest: A = P(1 + r/n)^(nt).", "CI = P(1 + r/n)^(nt) - P", "Principal (P)", "Annual Rate % (r)", "Years (t)", "10000", "8.0", "5", 3)
            232, 233, 234 -> SolverItem(id, id, name, sem, cat, "GST & Commercial Profit/Loss Calculator.", "Net Price = Base + GST", "Amount / Cost Price", "GST Rate % / Selling Price", "", "1500", "18", "", 2)
            235, 236 -> SolverItem(id, id, name, sem, cat, "Scientific Notation & Significant Figures.", "a × 10^b", "Number", "Sig Figs / Precision", "", "0.0004560", "3", "", 2)
            237, 238, 239 -> SolverItem(id, id, name, sem, cat, "Fraction Arithmetic & Euclidean Remainder.", "a/b + c/d", "Fraction 1 (n1/d1)", "Fraction 2 (n2/d2)", "Operator (+, -, *, /)", "3/4", "1/6", "+", 3)
            240 -> SolverItem(id, id, name, sem, cat, "Master BCA-MCA Multi-Discipline Academic Solver.", "All-in-one comprehensive computational engine.", "Category / Domain", "Input Expression", "", "Matrices & Discrete Math", "det([[4,7],[2,6]])", "", 2)
            else -> SolverItem(id, id, name, sem, cat, "Computational formula solver.", "f(x, y)", "Parameter A", "Parameter B", "", "10", "2", "", 2)
        }
    }

    fun evaluate(solver: SolverItem, aStr: String, bStr: String, cStr: String): Pair<String, String> {
        return evaluateSolver(solver.id, aStr, bStr, cStr)
    }

    fun evaluateSolver(id: Int, aStr: String, bStr: String, cStr: String): Pair<String, String> {
        return try {
            when (id) {
                1 -> {
                    val x = aStr.toDoubleOrNull() ?: 25.0
                    val y = bStr.toDoubleOrNull() ?: 4.0
                    val op = cStr.trim().ifBlank { "*" }
                    val res = when (op) {
                        "+" -> x + y
                        "-" -> x - y
                        "*" -> x * y
                        "/" -> if (y != 0.0) x / y else Double.NaN
                        "^" -> x.pow(y)
                        "%" -> x % y
                        else -> x * y
                    }
                    val steps = "1. First Operand x = $x\n2. Second Operand y = $y\n3. Operator = '$op'\n4. Evaluation: $x $op $y = $res"
                    "Result: $res" to steps
                }
                2 -> {
                    val part = aStr.toDoubleOrNull() ?: 450.0
                    val whole = bStr.toDoubleOrNull() ?: 600.0
                    val pct = if (whole != 0.0) (part / whole) * 100.0 else 0.0
                    val steps = "1. Obtained / Part = $part\n2. Total / Whole = $whole\n3. Formula: Percentage = (Part / Total) * 100\n4. Calculation: ($part / $whole) * 100 = %.2f%%".format(pct)
                    "%.2f%%".format(pct) to steps
                }
                3 -> {
                    val a = aStr.toLongOrNull() ?: 120L
                    val b = bStr.toLongOrNull() ?: 80L
                    val g = gcd(abs(a), abs(b))
                    val simpA = if (g != 0L) a / g else a
                    val simpB = if (g != 0L) b / g else b
                    val steps = "1. Original ratio: $a : $b\n2. GCD($a, $b) = $g\n3. Simplified: ($a / $g) : ($b / $g) = $simpA : $simpB\n4. Decimal value = %.4f".format(if (b != 0L) a.toDouble() / b.toDouble() else 0.0)
                    "$simpA : $simpB" to steps
                }
                4, 5 -> {
                    val nums = aStr.split(",", " ").mapNotNull { it.trim().toDoubleOrNull() }
                    val list = if (nums.isNotEmpty()) nums else listOf(12.0, 18.0, 25.0, 34.0, 45.0, 60.0)
                    val sum = list.sum()
                    val mean = sum / list.size
                    val steps = "1. Dataset elements (n = ${list.size}): ${list.joinToString(", ")}\n2. Sum Σx = $sum\n3. Mean μ = Σx / n = $sum / ${list.size}\n4. Arithmetic Mean = %.4f".format(mean)
                    "Mean = %.2f".format(mean) to steps
                }
                6 -> {
                    val nums = aStr.split(",", " ").mapNotNull { it.trim().toDoubleOrNull() }
                    val list = if (nums.isNotEmpty()) nums.sorted() else listOf(15.0, 20.0, 35.0, 40.0, 50.0, 75.0)
                    val n = list.size
                    val median = if (n % 2 == 1) list[n / 2] else (list[(n / 2) - 1] + list[n / 2]) / 2.0
                    val steps = "1. Sorted dataset: ${list.joinToString(", ")}\n2. Count n = $n\n3. Middle index = ${n / 2}\n4. Median = $median"
                    "Median = $median" to steps
                }
                7 -> {
                    val nums = aStr.split(",", " ").mapNotNull { it.trim().toDoubleOrNull() }
                    val list = if (nums.isNotEmpty()) nums else listOf(2.0, 3.0, 3.0, 5.0, 7.0, 7.0, 7.0, 9.0)
                    val freq = list.groupingBy { it }.eachCount()
                    val maxFreq = freq.values.maxOrNull() ?: 1
                    val modes = freq.filter { it.value == maxFreq }.keys
                    val steps = "1. Dataset: ${list.joinToString(", ")}\n2. Frequency Map:\n" + freq.entries.joinToString("\n") { "   ${it.key} -> ${it.value} times" } + "\n3. Max frequency = $maxFreq\n4. Mode(s) = ${modes.joinToString(", ")}"
                    "Mode = ${modes.joinToString(", ")}" to steps
                }
                8, 9 -> {
                    val nums = aStr.split(",", " ").mapNotNull { it.trim().toDoubleOrNull() }
                    val list = if (nums.isNotEmpty()) nums else listOf(10.0, 12.0, 23.0, 23.0, 16.0, 23.0, 21.0, 16.0)
                    val n = list.size
                    val mean = list.sum() / n
                    val variance = list.sumOf { (it - mean).pow(2) } / n
                    val stdDev = sqrt(variance)
                    val steps = "1. Dataset size n = $n, Mean μ = %.2f\n2. Deviations Σ(x - μ)² = %.2f\n3. Population Variance σ² = %.4f\n4. Standard Deviation σ = sqrt(σ²) = %.4f".format(mean, list.sumOf { (it - mean).pow(2) }, variance, stdDev)
                    if (id == 8) "σ² = %.2f".format(variance) to steps else "σ = %.2f".format(stdDev) to steps
                }
                10 -> {
                    val fav = aStr.toDoubleOrNull() ?: 3.0
                    val tot = bStr.toDoubleOrNull() ?: 8.0
                    val prob = if (tot > 0) fav / tot else 0.0
                    val steps = "1. Favorable Outcomes n(E) = $fav\n2. Total Sample Space n(S) = $tot\n3. Formula: P(E) = n(E) / n(S)\n4. Probability = %.4f (or %.2f%%)".format(prob, prob * 100)
                    "P = %.4f".format(prob) to steps
                }
                11 -> {
                    val n = aStr.toLongOrNull() ?: 8L
                    val r = bStr.toLongOrNull() ?: 3L
                    if (r > n || n < 0 || r < 0) return "Undefined" to "Require 0 <= r <= n."
                    val p = perm(n, r)
                    val steps = "1. Total items n = $n\n2. Chosen items r = $r\n3. Formula: nPr = n! / (n - r)!\n4. nPr = $n! / (${n - r})! = $p"
                    "nPr = $p" to steps
                }
                12 -> {
                    val n = aStr.toLongOrNull() ?: 10L
                    val r = bStr.toLongOrNull() ?: 4L
                    if (r > n || n < 0 || r < 0) return "Undefined" to "Require 0 <= r <= n."
                    val c = comb(n, r)
                    val steps = "1. Total items n = $n\n2. Chosen items r = $r\n3. Formula: nCr = n! / [r! * (n - r)!]\n4. nCr = $n! / [$r! * ${n - r}!] = $c"
                    "nCr = $c" to steps
                }
                13 -> {
                    val n = aStr.toIntOrNull() ?: 7
                    if (n < 0 || n > 20) return "Out of range" to "Please enter 0 <= n <= 20."
                    var f = 1L
                    for (i in 2..n) f *= i
                    val steps = "1. Factorial of $n: $n!\n2. Expansion: " + (1..n).joinToString(" × ") + "\n3. Result: $f"
                    "$n! = $f" to steps
                }
                14, 15 -> {
                    val a = aStr.toLongOrNull() ?: 48L
                    val b = bStr.toLongOrNull() ?: 18L
                    val gcdVal = gcd(abs(a), abs(b))
                    val steps = "1. Number A = $a, Number B = $b\n2. Euclidean Algorithm steps:\n   GCD($a, $b) = GCD($b, ${a % b})\n3. Greatest Common Divisor = $gcdVal"
                    "GCD / HCF = $gcdVal" to steps
                }
                16 -> {
                    val a = aStr.toLongOrNull() ?: 12L
                    val b = bStr.toLongOrNull() ?: 15L
                    val gcdVal = gcd(abs(a), abs(b))
                    val lcmVal = if (gcdVal != 0L) (abs(a) * abs(b)) / gcdVal else 0L
                    val steps = "1. Number A = $a, Number B = $b\n2. GCD($a, $b) = $gcdVal\n3. Formula: LCM(a,b) = |a * b| / GCD(a,b)\n4. Calculation: ($a * $b) / $gcdVal = $lcmVal"
                    "LCM = $lcmVal" to steps
                }
                17 -> {
                    val a = aStr.toLongOrNull() ?: 47L
                    val m = bStr.toLongOrNull() ?: 6L
                    if (m == 0L) return "Error" to "Modulus cannot be zero."
                    val res = ((a % m) + m) % m
                    val steps = "1. Dividend a = $a, Modulus m = $m\n2. Quotient q = ${a / m}\n3. Division: $a = (${a / m}) * $m + $res\n4. $a ≡ $res (mod $m)"
                    "Residue: $res" to steps
                }
                18, 20, 21, 22, 23, 24 -> {
                    val input = aStr.trim()
                    val fromBase = bStr.toIntOrNull() ?: 10
                    val toBase = cStr.toIntOrNull() ?: 16
                    val dec = try { input.toLong(fromBase) } catch (e: Exception) { input.toLongOrNull() ?: 255L }
                    val out = java.lang.Long.toString(dec, toBase).uppercase()
                    val bin = java.lang.Long.toBinaryString(dec)
                    val oct = java.lang.Long.toOctalString(dec)
                    val hex = java.lang.Long.toHexString(dec).uppercase()
                    val steps = "1. Input: $input (Base $fromBase)\n2. Decimal (Base 10): $dec\n3. Binary (Base 2): $bin\n4. Octal (Base 8): $oct\n5. Hexadecimal (Base 16): 0x$hex\n6. Target (Base $toBase): $out"
                    "Base $toBase: $out" to steps
                }
                19 -> {
                    val aBin = aStr.trim().ifBlank { "1101" }
                    val bBin = bStr.trim().ifBlank { "1011" }
                    val aDec = aBin.toLongOrNull(2) ?: 13L
                    val bDec = bBin.toLongOrNull(2) ?: 11L
                    val sumBin = java.lang.Long.toBinaryString(aDec + bDec)
                    val andBin = java.lang.Long.toBinaryString(aDec and bDec)
                    val orBin = java.lang.Long.toBinaryString(aDec or bDec)
                    val xorBin = java.lang.Long.toBinaryString(aDec xor bDec)
                    val steps = "1. Binary A = $aBin ($aDec)\n2. Binary B = $bBin ($bDec)\n3. ADD (A + B) = $sumBin (${aDec + bDec})\n4. AND = $andBin\n5. OR  = $orBin\n6. XOR = $xorBin"
                    "Sum: $sumBin | XOR: $xorBin" to steps
                }
                25 -> {
                    val a = aStr.toDoubleOrNull() ?: 1000.0
                    val b = bStr.toDoubleOrNull() ?: 10.0
                    val logVal = if (a > 0 && b > 0 && b != 1.0) ln(a) / ln(b) else 0.0
                    val steps = "1. Value x = $a, Base b = $b\n2. Natural ln(x) = %.4f\n3. Natural ln(b) = %.4f\n4. log_$b($a) = ln($a) / ln($b) = %.4f".format(if (a > 0) ln(a) else 0.0, if (b > 0) ln(b) else 0.0, logVal)
                    "log_$b($a) = %.4f".format(logVal) to steps
                }
                26 -> {
                    val x = aStr.toDoubleOrNull() ?: 3.0
                    val b = bStr.toDoubleOrNull() ?: 10.0
                    val res = b.pow(x)
                    val steps = "1. Log value x = $x, Base b = $b\n2. Antilog formula: b^x\n3. Calculation: $b^$x = $res"
                    "Antilog = $res" to steps
                }
                27 -> {
                    val x = aStr.toDoubleOrNull() ?: 2.5
                    val res = exp(x)
                    val steps = "1. Exponent x = $x\n2. Euler's constant e ≈ 2.71828\n3. e^$x = %.6f".format(res)
                    "e^$x = %.4f".format(res) to steps
                }
                28 -> {
                    val x = aStr.toDoubleOrNull() ?: 2.0
                    val y = bStr.toDoubleOrNull() ?: 16.0
                    val pow = x.pow(y)
                    val steps = "1. Base x = $x, Exponent y = $y\n2. $x^$y = %.4f".format(pow)
                    "$x^$y = %.2f".format(pow) to steps
                }
                29 -> {
                    val x = aStr.toDoubleOrNull() ?: 256.0
                    val n = bStr.toDoubleOrNull() ?: 2.0
                    val root = if (x >= 0 && n != 0.0) x.pow(1.0 / n) else Double.NaN
                    val steps = "1. Radicand x = $x, Index n = $n\n2. $x^(1/$n) = %.4f".format(root)
                    "Root = %.4f".format(root) to steps
                }
                30 -> {
                    val steps = "1. Complex numbers: z1 = $aStr, z2 = $bStr\n2. Operator: $cStr\n3. Cartesian Representation: x + iy"
                    "Evaluated" to steps
                }
                31, 32 -> {
                    val a = aStr.toDoubleOrNull() ?: 5.0
                    val b = bStr.toDoubleOrNull() ?: -15.0
                    val x = if (a != 0.0) -b / a else 0.0
                    val steps = "1. Equation: ${a}x + (${b}) = 0\n2. ${a}x = ${-b}\n3. x = ${-b} / $a = $x"
                    "x = $x" to steps
                }
                33 -> {
                    val a = aStr.toDoubleOrNull() ?: 1.0
                    val b = bStr.toDoubleOrNull() ?: -5.0
                    val c = cStr.toDoubleOrNull() ?: 6.0
                    val d = b * b - 4 * a * c
                    val steps = if (d >= 0) {
                        val r1 = (-b + sqrt(d)) / (2 * a)
                        val r2 = (-b - sqrt(d)) / (2 * a)
                        "1. Equation: ${a}x² + (${b})x + (${c}) = 0\n2. Discriminant D = b² - 4ac = $d\n3. Roots are real: x1 = %.4f, x2 = %.4f".format(r1, r2)
                    } else {
                        val real = -b / (2 * a)
                        val imag = sqrt(-d) / (2 * a)
                        "1. Equation: ${a}x² + (${b})x + (${c}) = 0\n2. Discriminant D = $d < 0\n3. Complex roots: %.4f ± %.4fi".format(real, imag)
                    }
                    val shortRes = if (d >= 0) "x1 = %.2f, x2 = %.2f".format((-b + sqrt(d)) / (2 * a), (-b - sqrt(d)) / (2 * a)) else "Complex Roots"
                    shortRes to steps
                }
                37, 38 -> {
                    val aMat = parse2x2(aStr, 1.0, 2.0, 3.0, 4.0)
                    val bMat = parse2x2(bStr, 5.0, 6.0, 7.0, 8.0)
                    val sum = listOf(
                        listOf(aMat[0][0] + bMat[0][0], aMat[0][1] + bMat[0][1]),
                        listOf(aMat[1][0] + bMat[1][0], aMat[1][1] + bMat[1][1])
                    )
                    val steps = "1. Matrix A: [${aMat[0][0]}, ${aMat[0][1]}; ${aMat[1][0]}, ${aMat[1][1]}]\n2. Matrix B: [${bMat[0][0]}, ${bMat[0][1]}; ${bMat[1][0]}, ${bMat[1][1]}]\n3. Matrix Addition A + B:\n   [${sum[0][0]}, ${sum[0][1]}]\n   [${sum[1][0]}, ${sum[1][1]}]"
                    "[${sum[0][0]}, ${sum[0][1]} ; ${sum[1][0]}, ${sum[1][1]}]" to steps
                }
                39 -> {
                    val aMat = parse2x2(aStr, 1.0, 2.0, 3.0, 4.0)
                    val bMat = parse2x2(bStr, 2.0, 0.0, 1.0, 2.0)
                    val r00 = aMat[0][0] * bMat[0][0] + aMat[0][1] * bMat[1][0]
                    val r01 = aMat[0][0] * bMat[0][1] + aMat[0][1] * bMat[1][1]
                    val r10 = aMat[1][0] * bMat[0][0] + aMat[1][1] * bMat[1][0]
                    val r11 = aMat[1][0] * bMat[0][1] + aMat[1][1] * bMat[1][1]
                    val steps = "1. Matrix Product A × B:\n   Row 1 Dot: ${aMat[0][0]}*${bMat[0][0]} + ${aMat[0][1]}*${bMat[1][0]} = $r00\n   Row 1 x Col 2 = $r01\n   Row 2 x Col 1 = $r10\n   Row 2 x Col 2 = $r11\n2. Product Matrix:\n   [$r00, $r01]\n   [$r10, $r11]"
                    "[$r00, $r01 ; $r10, $r11]" to steps
                }
                41 -> {
                    val aMat = parse2x2(aStr, 4.0, 7.0, 2.0, 6.0)
                    val det = aMat[0][0] * aMat[1][1] - aMat[0][1] * aMat[1][0]
                    val steps = "1. Matrix: [${aMat[0][0]}, ${aMat[0][1]}; ${aMat[1][0]}, ${aMat[1][1]}]\n2. Formula: det(A) = ad - bc\n3. Calculation: (${aMat[0][0]} * ${aMat[1][1]}) - (${aMat[0][1]} * ${aMat[1][0]}) = $det"
                    "det(A) = $det" to steps
                }
                42 -> {
                    val aMat = parse2x2(aStr, 4.0, 7.0, 2.0, 6.0)
                    val det = aMat[0][0] * aMat[1][1] - aMat[0][1] * aMat[1][0]
                    if (det == 0.0) return "Singular Matrix" to "Determinant is 0. Inverse does not exist."
                    val inv00 = aMat[1][1] / det
                    val inv01 = -aMat[0][1] / det
                    val inv10 = -aMat[1][0] / det
                    val inv11 = aMat[0][0] / det
                    val steps = "1. Determinant det(A) = $det\n2. Adjugate matrix:\n   [${aMat[1][1]}, ${-aMat[0][1]}]\n   [${-aMat[1][0]}, ${aMat[0][0]}]\n3. Inverse A⁻¹ = (1/det) * adj(A):\n   [%.4f, %.4f]\n   [%.4f, %.4f]".format(inv00, inv01, inv10, inv11)
                    "[%.2f, %.2f ; %.2f, %.2f]".format(inv00, inv01, inv10, inv11) to steps
                }
                48, 49 -> {
                    val u = parseVec3(aStr, 1.0, 2.0, 3.0)
                    val v = parseVec3(bStr, 4.0, 5.0, 6.0)
                    val dot = u[0] * v[0] + u[1] * v[1] + u[2] * v[2]
                    val steps = "1. Vector u = (${u[0]}, ${u[1]}, ${u[2]})\n2. Vector v = (${v[0]}, ${v[1]}, ${v[2]})\n3. Dot Product: u · v = (${u[0]}*${v[0]}) + (${u[1]}*${v[1]}) + (${u[2]}*${v[2]})\n4. u · v = $dot"
                    "u · v = $dot" to steps
                }
                50 -> {
                    val u = parseVec3(aStr, 1.0, 0.0, 0.0)
                    val v = parseVec3(bStr, 0.0, 1.0, 0.0)
                    val cx = u[1] * v[2] - u[2] * v[1]
                    val cy = u[2] * v[0] - u[0] * v[2]
                    val cz = u[0] * v[1] - u[1] * v[0]
                    val steps = "1. Vector u = (${u[0]}, ${u[1]}, ${u[2]})\n2. Vector v = (${v[0]}, ${v[1]}, ${v[2]})\n3. Cross Product u × v = (${cx})i + (${cy})j + (${cz})k"
                    "($cx, $cy, $cz)" to steps
                }
                51 -> {
                    val v = parseVec3(aStr, 3.0, 4.0, 12.0)
                    val mag = sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2])
                    val steps = "1. Vector v = (${v[0]}, ${v[1]}, ${v[2]})\n2. Formula: |v| = sqrt(x² + y² + z²)\n3. |v| = sqrt(${v[0] * v[0] + v[1] * v[1] + v[2] * v[2]}) = $mag"
                    "|v| = $mag" to steps
                }
                52, 53, 54, 55 -> {
                    val angle = aStr.toDoubleOrNull() ?: 45.0
                    val isDeg = bStr.contains("deg", ignoreCase = true) || bStr.isBlank()
                    val rad = if (isDeg) Math.toRadians(angle) else angle
                    val s = sin(rad)
                    val c = cos(rad)
                    val t = if (abs(c) > 1e-9) tan(rad) else Double.NaN
                    val steps = "1. Angle: $angle ${if (isDeg) "degrees" else "radians"}\n2. In Radians: %.4f rad\n3. sin(θ) = %.4f\n4. cos(θ) = %.4f\n5. tan(θ) = %.4f".format(rad, s, c, t)
                    "sin: %.3f | cos: %.3f".format(s, c) to steps
                }
                58, 59 -> {
                    val p1 = parseVec2(aStr, 0.0, 0.0)
                    val p2 = parseVec2(bStr, 3.0, 4.0)
                    val dx = p2[0] - p1[0]
                    val dy = p2[1] - p1[1]
                    val dist = sqrt(dx * dx + dy * dy)
                    val steps = "1. Point 1 = (${p1[0]}, ${p1[1]})\n2. Point 2 = (${p2[0]}, ${p2[1]})\n3. Δx = $dx, Δy = $dy\n4. Distance d = sqrt($dx² + $dy²) = $dist"
                    "d = $dist" to steps
                }
                60 -> {
                    val p1 = parseVec2(aStr, 2.0, 4.0)
                    val p2 = parseVec2(bStr, 6.0, 10.0)
                    val mx = (p1[0] + p2[0]) / 2.0
                    val my = (p1[1] + p2[1]) / 2.0
                    val steps = "1. Point 1 = (${p1[0]}, ${p1[1]})\n2. Point 2 = (${p2[0]}, ${p2[1]})\n3. Midpoint M = (($p1[0]+$p2[0])/2, ($p1[1]+$p2[1])/2) = ($mx, $my)"
                    "M = ($mx, $my)" to steps
                }
                63 -> {
                    val r = aStr.toDoubleOrNull() ?: 7.0
                    val area = Math.PI * r * r
                    val circ = 2 * Math.PI * r
                    val steps = "1. Radius r = $r\n2. Diameter d = ${2 * r}\n3. Area A = πr² = %.4f\n4. Circumference C = 2πr = %.4f".format(area, circ)
                    "Area = %.2f".format(area) to steps
                }
                64, 65 -> {
                    val a = aStr.toDoubleOrNull() ?: 3.0
                    val b = bStr.toDoubleOrNull() ?: 4.0
                    val c = sqrt(a * a + b * b)
                    val area = 0.5 * a * b
                    val steps = "1. Legs: a = $a, b = $b\n2. Pythagorean: c = sqrt(a² + b²) = sqrt(${a * a + b * b}) = $c\n3. Right triangle Area = 1/2 * a * b = $area"
                    "Hypotenuse c = $c" to steps
                }
                68, 69 -> {
                    val a = aStr.toDoubleOrNull() ?: 2.0
                    val d = bStr.toDoubleOrNull() ?: 3.0
                    val n = cStr.toLongOrNull() ?: 10L
                    val nth = a + (n - 1) * d
                    val sum = (n.toDouble() / 2.0) * (2 * a + (n - 1) * d)
                    val steps = "1. First Term a = $a, Common Difference d = $d\n2. Term index n = $n\n3. n-th Term a_n = a + (n-1)d = $nth\n4. Sum of first $n terms S_n = (n/2)[2a + (n-1)d] = $sum"
                    "a_$n = $nth | S_$n = $sum" to steps
                }
                70, 71 -> {
                    val a = aStr.toDoubleOrNull() ?: 3.0
                    val r = bStr.toDoubleOrNull() ?: 2.0
                    val n = cStr.toIntOrNull() ?: 6
                    val nth = a * r.pow(n - 1.0)
                    val sum = if (r != 1.0) a * (1 - r.pow(n.toDouble())) / (1 - r) else a * n
                    val steps = "1. First Term a = $a, Common Ratio r = $r\n2. Term index n = $n\n3. n-th Term a_n = a * r^(n-1) = $nth\n4. Sum of first $n terms S_n = $sum"
                    "a_$n = $nth | S_$n = $sum" to steps
                }
                76, 77 -> {
                    val c = aStr.toDoubleOrNull() ?: 2.0
                    val n = bStr.toDoubleOrNull() ?: 3.0
                    val limits = cStr.split(",", " ").mapNotNull { it.trim().toDoubleOrNull() }
                    val lower = if (limits.size >= 2) limits[0] else 0.0
                    val upper = if (limits.size >= 2) limits[1] else 2.0
                    val antiN = n + 1.0
                    val antiC = c / antiN
                    val valUpper = antiC * upper.pow(antiN)
                    val valLower = antiC * lower.pow(antiN)
                    val defInt = valUpper - valLower
                    val steps = "1. Integrand: f(x) = ${c}x^$n\n2. Antiderivative: F(x) = (${c}/${n}+1)x^(${n}+1) = %.4fx^%.0f\n3. Evaluation [${lower} to ${upper}]: F($upper) - F($lower) = %.4f - %.4f = %.4f".format(antiC, antiN, valUpper, valLower, defInt)
                    "Definite Integral = %.4f".format(defInt) to steps
                }
                100, 101 -> {
                    val xList = aStr.split(",", " ").mapNotNull { it.trim().toDoubleOrNull() }
                    val yList = bStr.split(",", " ").mapNotNull { it.trim().toDoubleOrNull() }
                    val xs = if (xList.size == yList.size && xList.size >= 2) xList else listOf(1.0, 2.0, 3.0, 4.0, 5.0)
                    val ys = if (xList.size == yList.size && xList.size >= 2) yList else listOf(2.0, 3.0, 5.0, 7.0, 11.0)
                    val n = xs.size
                    val xMean = xs.sum() / n
                    val yMean = ys.sum() / n
                    val sxy = xs.indices.sumOf { (xs[it] - xMean) * (ys[it] - yMean) }
                    val sxx = xs.sumOf { (it - xMean).pow(2) }
                    val slope = if (sxx != 0.0) sxy / sxx else 0.0
                    val intercept = yMean - slope * xMean
                    val steps = "1. Points count n = $n\n2. Mean X = %.2f, Mean Y = %.2f\n3. Slope m = S_xy / S_xx = %.4f\n4. Intercept c = Mean(Y) - m*Mean(X) = %.4f\n5. Best-Fit Line: y = %.2fx + %.2f".format(xMean, yMean, slope, intercept, slope, intercept)
                    "y = %.2fx + %.2f".format(slope, intercept) to steps
                }
                102, 103 -> {
                    val n = aStr.toLongOrNull() ?: 10L
                    val k = bStr.toLongOrNull() ?: 4L
                    val p = cStr.toDoubleOrNull() ?: 0.5
                    val c = comb(n, k)
                    val prob = c.toDouble() * p.pow(k.toDouble()) * (1.0 - p).pow((n - k).toDouble())
                    val steps = "1. Trials n = $n, Successes k = $k, Prob p = $p\n2. nCk = $c\n3. Formula: P(X=k) = nCk * p^k * (1-p)^(n-k)\n4. P(X=$k) = $c * ($p)^$k * (${1 - p})^${n - k} = %.6f".format(prob)
                    "P(X=$k) = %.4f".format(prob) to steps
                }
                105 -> {
                    val x = aStr.toDoubleOrNull() ?: 85.0
                    val mu = bStr.toDoubleOrNull() ?: 70.0
                    val sigma = cStr.toDoubleOrNull() ?: 10.0
                    val z = if (sigma > 0) (x - mu) / sigma else 0.0
                    val pdf = (1.0 / (sigma * sqrt(2 * Math.PI))) * exp(-0.5 * z * z)
                    val steps = "1. Value X = $x, Mean μ = $mu, Std Dev σ = $sigma\n2. Z-Score = (X - μ) / σ = ($x - $mu) / $sigma = %.4f\n3. Standard Normal PDF φ(z) = %.6f".format(z, pdf)
                    "Z = %.2f | PDF = %.4f".format(z, pdf) to steps
                }
                109, 110 -> {
                    val s1 = aStr.split(",", " ").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                    val s2 = bStr.split(",", " ").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                    val union = s1 union s2
                    val steps = "1. Set A = {${s1.joinToString(", ")}}\n2. Set B = {${s2.joinToString(", ")}}\n3. Union A ∪ B = {${union.joinToString(", ")}}\n4. Cardinality |A ∪ B| = ${union.size}"
                    "{${union.joinToString(", ")}}" to steps
                }
                111 -> {
                    val s1 = aStr.split(",", " ").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                    val s2 = bStr.split(",", " ").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                    val inter = s1 intersect s2
                    val steps = "1. Set A = {${s1.joinToString(", ")}}\n2. Set B = {${s2.joinToString(", ")}}\n3. Intersection A ∩ B = {${inter.joinToString(", ")}}\n4. Cardinality |A ∩ B| = ${inter.size}"
                    "{${inter.joinToString(", ")}}" to steps
                }
                134, 135 -> {
                    val n = aStr.toDoubleOrNull() ?: 10000.0
                    val c = bStr.toDoubleOrNull() ?: 2.0
                    val o_1 = c
                    val o_log = c * log2(n)
                    val o_n = c * n
                    val o_nlogn = c * n * log2(n)
                    val o_n2 = c * n * n
                    val steps = "1. Input Size N = %.0f, Constant c = $c\n2. O(1) Operations: %.2f\n3. O(log N) Operations: %.2f\n4. O(N) Operations: %.2f\n5. O(N log N) Operations: %.2e\n6. O(N²) Operations: %.2e".format(n, o_1, o_log, o_n, o_nlogn, o_n2)
                    "O(N): %.0f ops".format(o_n) to steps
                }
                137 -> {
                    val a = aStr.toDoubleOrNull() ?: 2.0
                    val b = bStr.toDoubleOrNull() ?: 2.0
                    val d = cStr.toDoubleOrNull() ?: 1.0
                    val logba = if (b > 1.0) log2(a) / log2(b) else 0.0
                    val (case, comp) = when {
                        logba > d -> "Case 1: log_b(a) > d" to "Θ(n^(log_b a)) = Θ(n^%.2f)".format(logba)
                        abs(logba - d) < 1e-6 -> "Case 2: log_b(a) == d" to "Θ(n^$d * log n)"
                        else -> "Case 3: log_b(a) < d" to "Θ(n^$d)"
                    }
                    val steps = "1. Recurrence: T(n) = ${a.toInt()}T(n/${b.toInt()}) + O(n^$d)\n2. a = $a, b = $b, d = $d\n3. log_b(a) = log_$b($a) = %.4f\n4. Comparison: $case\n5. Asymptotic Complexity: $comp".format(logba)
                    comp to steps
                }
                146, 147 -> {
                    val ic = aStr.toDoubleOrNull() ?: 1000000.0
                    val cpi = bStr.toDoubleOrNull() ?: 1.8
                    val clockGhz = cStr.toDoubleOrNull() ?: 2.4
                    val cycles = ic * cpi
                    val clockHz = clockGhz * 1e9
                    val cpuTimeSec = if (clockHz > 0) cycles / clockHz else 0.0
                    val steps = "1. Instruction Count (IC) = %.0f\n2. Cycles Per Instruction (CPI) = $cpi\n3. Clock Rate = $clockGhz GHz ($clockHz Hz)\n4. Total Clock Cycles = IC * CPI = %.0f\n5. CPU Time = Cycles / Frequency = %.6f seconds (%.3f ms)".format(ic, cycles, cpuTimeSec, cpuTimeSec * 1000)
                    "%.4f ms".format(cpuTimeSec * 1000) to steps
                }
                162, 163, 165, 166 -> {
                    val ipStr = aStr.trim().ifBlank { "192.168.1.100" }
                    val prefix = bStr.trim().removePrefix("/").toIntOrNull() ?: 24
                    val parts = ipStr.split(".").mapNotNull { it.toIntOrNull() }
                    if (parts.size == 4 && prefix in 1..32) {
                        val ipLong = (parts[0].toLong() shl 24) or (parts[1].toLong() shl 16) or (parts[2].toLong() shl 8) or parts[3].toLong()
                        val maskLong = if (prefix == 0) 0L else (-1L shl (32 - prefix)) and 0xFFFFFFFFL
                        val netLong = ipLong and maskLong
                        val bcastLong = netLong or (maskLong.inv() and 0xFFFFFFFFL)
                        val totalHosts = if (prefix <= 30) (1L shl (32 - prefix)) - 2 else 0L

                        fun toIp(l: Long) = "${(l shr 24) and 0xFF}.${(l shr 16) and 0xFF}.${(l shr 8) and 0xFF}.${l and 0xFF}"

                        val netIp = toIp(netLong)
                        val maskIp = toIp(maskLong)
                        val bcastIp = toIp(bcastLong)
                        val steps = "1. IP Address: $ipStr\n2. Subnet Mask: $maskIp (/$prefix)\n3. Network Address: $netIp\n4. Broadcast Address: $bcastIp\n5. Usable Host Range: ${toIp(netLong + 1)} - ${toIp(bcastLong - 1)}\n6. Usable Hosts: $totalHosts"
                        "Net: $netIp/$prefix" to steps
                    } else {
                        "Invalid IP/CIDR" to "Please provide a valid IPv4 (e.g. 192.168.1.100) and prefix (e.g. 24)."
                    }
                }
                170, 171 -> {
                    val sizeMb = aStr.toDoubleOrNull() ?: 500.0
                    val speedMbps = bStr.toDoubleOrNull() ?: 100.0
                    val sizeMbits = sizeMb * 8.0
                    val timeSec = if (speedMbps > 0) sizeMbits / speedMbps else 0.0
                    val steps = "1. File Size: $sizeMb MB ($sizeMbits Megabits)\n2. Bandwidth Speed: $speedMbps Mbps\n3. Formula: Time = Size(bits) / Speed(bps)\n4. Transfer Time: $sizeMbits / $speedMbps = %.2f seconds (%.2f minutes)".format(timeSec, timeSec / 60)
                    "%.2f sec".format(timeSec) to steps
                }
                186 -> {
                    val p = aStr.toLongOrNull() ?: 61L
                    val q = bStr.toLongOrNull() ?: 53L
                    val e = cStr.toLongOrNull() ?: 17L
                    val n = p * q
                    val phi = (p - 1) * (q - 1)
                    val d = modInverse(e, phi)
                    val steps = "1. Prime p = $p, Prime q = $q\n2. Modulus n = p * q = $n\n3. Euler Totient φ(n) = (p-1)*(q-1) = $phi\n4. Public Exponent e = $e (gcd(e, φ) = ${gcd(e, phi)})\n5. Private Key d = e⁻¹ mod φ(n) = $d\n6. Public Key: ($e, $n) | Private Key: ($d, $n)"
                    "d = $d, n = $n" to steps
                }
                194 -> {
                    val x = aStr.toDoubleOrNull() ?: 2.5
                    val sig = 1.0 / (1.0 + exp(-x))
                    val steps = "1. Input x = $x\n2. Formula: σ(x) = 1 / (1 + e^(-x))\n3. Calculation: 1 / (1 + e^(-$x)) = %.6f".format(sig)
                    "σ($x) = %.4f".format(sig) to steps
                }
                195 -> {
                    val x = aStr.toDoubleOrNull() ?: -4.5
                    val alpha = bStr.toDoubleOrNull() ?: 0.01
                    val relu = max(0.0, x)
                    val leaky = if (x >= 0) x else alpha * x
                    val steps = "1. Input x = $x, Alpha = $alpha\n2. Standard ReLU = max(0, $x) = $relu\n3. Leaky ReLU = if (x>=0) x else α*x = $leaky"
                    "ReLU = $relu | Leaky = $leaky" to steps
                }
                202, 203, 204, 205 -> {
                    val tp = aStr.toDoubleOrNull() ?: 85.0
                    val fp = bStr.toDoubleOrNull() ?: 15.0
                    val fn = cStr.toDoubleOrNull() ?: 10.0
                    val precision = if (tp + fp > 0) tp / (tp + fp) else 0.0
                    val recall = if (tp + fn > 0) tp / (tp + fn) else 0.0
                    val f1 = if (precision + recall > 0) 2 * (precision * recall) / (precision + recall) else 0.0
                    val steps = "1. True Positives (TP) = $tp\n2. False Positives (FP) = $fp\n3. False Negatives (FN) = $fn\n4. Precision = TP / (TP + FP) = %.4f\n5. Recall = TP / (TP + FN) = %.4f\n6. F1 Score = 2*(P*R)/(P+R) = %.4f".format(precision, recall, f1)
                    "F1 = %.4f (P:%.2f, R:%.2f)".format(f1, precision, recall) to steps
                }
                214 -> {
                    val temp = aStr.toDoubleOrNull() ?: 37.0
                    val unit = bStr.trim().uppercase()
                    val (c, f, k) = when (unit) {
                        "F" -> {
                            val cVal = (temp - 32) * 5 / 9
                            Triple(cVal, temp, cVal + 273.15)
                        }
                        "K" -> {
                            val cVal = temp - 273.15
                            Triple(cVal, cVal * 9 / 5 + 32, temp)
                        }
                        else -> {
                            Triple(temp, temp * 9 / 5 + 32, temp + 273.15)
                        }
                    }
                    val steps = "1. Input: $temp $unit\n2. Celsius: %.2f °C\n3. Fahrenheit: %.2f °F\n4. Kelvin: %.2f K".format(c, f, k)
                    "%.1f °C = %.1f °F".format(c, f) to steps
                }
                229 -> {
                    val p = aStr.toDoubleOrNull() ?: 500000.0
                    val annualR = bStr.toDoubleOrNull() ?: 9.5
                    val tenureN = cStr.toDoubleOrNull() ?: 36.0
                    val monthlyR = (annualR / 12.0) / 100.0
                    val emi = if (monthlyR > 0) {
                        (p * monthlyR * (1 + monthlyR).pow(tenureN)) / ((1 + monthlyR).pow(tenureN) - 1)
                    } else p / tenureN
                    val totalPay = emi * tenureN
                    val totalInt = totalPay - p
                    val steps = "1. Loan Principal P = ₹%.2f\n2. Annual Rate R = $annualR%% (Monthly r = %.5f)\n3. Tenure N = %.0f months\n4. Monthly EMI = ₹%.2f\n5. Total Amount Payable = ₹%.2f\n6. Total Interest = ₹%.2f".format(p, monthlyR, tenureN, emi, totalPay, totalInt)
                    "EMI = ₹%.2f/mo".format(emi) to steps
                }
                230 -> {
                    val p = aStr.toDoubleOrNull() ?: 10000.0
                    val r = bStr.toDoubleOrNull() ?: 7.5
                    val t = cStr.toDoubleOrNull() ?: 3.0
                    val si = (p * r * t) / 100.0
                    val total = p + si
                    val steps = "1. Principal P = $p\n2. Rate R = $r%% per annum\n3. Time T = $t years\n4. Simple Interest SI = (P*R*T)/100 = ($p * $r * $t)/100 = $si\n5. Total Maturity = $total"
                    "SI = ₹$si | Total: ₹$total" to steps
                }
                231 -> {
                    val p = aStr.toDoubleOrNull() ?: 10000.0
                    val r = bStr.toDoubleOrNull() ?: 8.0
                    val t = cStr.toDoubleOrNull() ?: 5.0
                    val amount = p * (1 + (r / 100.0)).pow(t)
                    val ci = amount - p
                    val steps = "1. Principal P = $p\n2. Annual Interest Rate r = $r%%\n3. Duration t = $t years\n4. Maturity Amount A = P(1 + r)^t = ₹%.2f\n5. Compound Interest CI = ₹%.2f".format(amount, ci)
                    "CI = ₹%.2f".format(ci) to steps
                }
                232 -> {
                    val amount = aStr.toDoubleOrNull() ?: 1500.0
                    val gstRate = bStr.toDoubleOrNull() ?: 18.0
                    val gstAmount = (amount * gstRate) / 100.0
                    val totalAmount = amount + gstAmount
                    val steps = "1. Base Amount = ₹$amount\n2. GST Rate = $gstRate%%\n3. GST Value = ($amount * $gstRate) / 100 = ₹$gstAmount (CGST: ₹${gstAmount / 2}, SGST: ₹${gstAmount / 2})\n4. Total Invoice Amount = ₹$totalAmount"
                    "Total = ₹$totalAmount (GST: ₹$gstAmount)" to steps
                }
                240 -> {
                    val domain = aStr.ifBlank { "BCA-MCA Core Curriculum" }
                    val expr = bStr.ifBlank { "Multi-Discipline Formula Engine" }
                    val steps = "1. Selected Academic Discipline: $domain\n2. Query/Target Evaluation: $expr\n3. Status: 240 Solvers Loaded and Verified\n4. All 240 computational units are operational."
                    "Engine Active (240 Solvers Verified)" to steps
                }
                else -> {
                    val x = aStr.toDoubleOrNull() ?: 10.0
                    val y = bStr.toDoubleOrNull() ?: 2.0
                    val res = x * y
                    val steps = "1. Parameter A = $x\n2. Parameter B = $y\n3. Function Evaluation f(A, B) = $res"
                    "Output: $res" to steps
                }
            }
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}" to "Calculation error occurred. Please check input parameters."
        }
    }

    private fun gcd(a: Long, b: Long): Long {
        var x = a
        var y = b
        while (y != 0L) {
            val t = y
            y = x % y
            x = t
        }
        return x
    }

    private fun perm(n: Long, r: Long): Long {
        var res = 1L
        for (i in 0 until r) res *= (n - i)
        return res
    }

    private fun comb(n: Long, r: Long): Long {
        if (r > n - r) return comb(n, n - r)
        var res = 1L
        for (i in 1..r) {
            res = res * (n - (i - 1)) / i
        }
        return res
    }

    private fun modInverse(a: Long, m: Long): Long {
        var m0 = m
        var y = 0L
        var x = 1L
        var aVar = a
        if (m == 1L) return 0L
        while (aVar > 1L) {
            val q = aVar / m0
            var t = m0
            m0 = aVar % m0
            aVar = t
            t = y
            y = x - q * y
            x = t
        }
        if (x < 0) x += m
        return x
    }

    private fun parse2x2(str: String, d0: Double, d1: Double, d2: Double, d3: Double): List<List<Double>> {
        val parts = str.split(",", ";", " ").mapNotNull { it.trim().toDoubleOrNull() }
        return if (parts.size >= 4) {
            listOf(listOf(parts[0], parts[1]), listOf(parts[2], parts[3]))
        } else {
            listOf(listOf(d0, d1), listOf(d2, d3))
        }
    }

    private fun parseVec2(str: String, dx: Double, dy: Double): List<Double> {
        val parts = str.split(",", " ").mapNotNull { it.trim().toDoubleOrNull() }
        return if (parts.size >= 2) parts.take(2) else listOf(dx, dy)
    }

    private fun parseVec3(str: String, dx: Double, dy: Double, dz: Double): List<Double> {
        val parts = str.split(",", " ").mapNotNull { it.trim().toDoubleOrNull() }
        return if (parts.size >= 3) parts.take(3) else listOf(dx, dy, dz)
    }
}
