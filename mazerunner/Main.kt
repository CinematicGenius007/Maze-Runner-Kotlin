package mazerunner

import java.io.File


val moves = arrayOf(
    Pair(0, 1),
    Pair(0, -1),
    Pair(1, 0),
    Pair(-1, 0)
)

fun createMaze(incorrectRows: Int, incorrectCols: Int): Array<Array<Int>> {
    val rows = if (incorrectRows % 2 == 1) incorrectRows else incorrectRows - 1
    val cols = if (incorrectCols % 2 == 1) incorrectCols else incorrectCols - 1
    val grid = Array(rows) { Array(cols) { 0 } }  // 0 = wall, 1 = path

    val directions = arrayOf(
        Pair(0, 2),
        Pair(0, -2),
        Pair(2, 0),
        Pair(-2, 0)
    )

    var startRow = (1 until rows - 1).random()
    var startCol = (1 until cols - 1).random()

    while (startRow % 2 == 0) {
        startRow = (1 until rows - 1).random()
    }

    while (startCol % 2 == 0) {
        startCol = (1 until cols - 1).random()
    }

    grid[startRow][startCol] = 1

    val frontierCells = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
    val visited = Array(rows) { Array(cols) { false } }

    visited[startRow][startCol] = true

    for (direction in directions) {
        val row = startRow + direction.first
        val col = startCol + direction.second

        if (row in 0 until rows && col in 0 until cols) {
            frontierCells.add(Pair(Pair(row, col), Pair(startRow, startCol)))
            visited[row][col] = true
        }
    }

    while (frontierCells.isNotEmpty()) {
        val index = (0 until frontierCells.size).random()
        val (frontierCell, parentCell) = frontierCells[index]
        frontierCells.removeAt(index)

        if (frontierCell.first != 0 && frontierCell.first != rows - 1 && frontierCell.second != 0 && frontierCell.second != cols - 1) {
            grid[frontierCell.first][frontierCell.second] = 1
            grid[(frontierCell.first + parentCell.first) / 2][(frontierCell.second + parentCell.second) / 2] = 1
        }


        for (direction in directions) {
            val row = frontierCell.first + direction.first
            val col = frontierCell.second + direction.second

            if (row in 0 until rows && col in 0 until cols && !visited[row][col]) {
                frontierCells.add(Pair(Pair(row, col), frontierCell))
                visited[row][col] = true
            }
        }
    }

    var row = 1
    while (row < rows - 1) {
        if (grid[row][1] == 1 && isValidMaze(grid, Pair(row, 1), Pair(row, 0), rows, cols)) {
            break
        }
        row++
    }

    if (row == rows - 1) {
        return createMaze(rows, cols)
    }

    grid[row][0] = 1

    var count = 0
    for (i in 0 until rows - 1) {
        for (j in 0 until cols - 1) {
            count += grid[i][j]
        }
    }

    if (rows == incorrectRows && cols == incorrectCols) {
        return grid
    }

    val newGrid = Array(incorrectRows) { Array(incorrectCols) { 0 } }
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            newGrid[i][j] = grid[i][j]
        }
    }

    if (incorrectCols > cols) {
        var i = 1
        while (i < incorrectRows - 1) {
            if (newGrid[i][cols - 1] == 1) {
                newGrid[i][incorrectCols - 1] = 1
                break
            }
            i++
        }
    }

    return newGrid
}

fun isValidMaze(grid: Array<Array<Int>>, currentCell: Pair<Int, Int>, parentCell: Pair<Int, Int>, rows: Int, cols: Int): Boolean {
    val visited = Array(rows) { Array(cols) { false } }
    visited[currentCell.first][currentCell.second] = true
    visited[parentCell.first][parentCell.second] = true

    for (move in moves) {
        val row = currentCell.first + move.first
        val col = currentCell.second + move.second
        if (row in 0 until rows && col in 0 until cols && !visited[row][col]) {
            visited[row][col] = true
            val pair = traverse(grid, Pair(row, col), rows, cols, visited)
            if (pair != null) {
                grid[pair.first][pair.second] = 1
                return true
            }
        }
    }

    return false
}

fun traverse(grid: Array<Array<Int>>, currentCell: Pair<Int, Int>, rows: Int, cols: Int, visited: Array<Array<Boolean>>): Pair<Int, Int>? {
    if (currentCell.second == cols - 1) return currentCell
    if (grid[currentCell.first][currentCell.second] == 0) return null
    for (move in moves) {
        val row = currentCell.first + move.first
        val col = currentCell.second + move.second
        if (row in 0 until rows && col in 0 until cols && !visited[row][col]) {
            visited[row][col] = true
            val pair = traverse(grid, Pair(row, col), rows, cols, visited)
            if (pair != null) return pair
        }
    }
    return null
}

fun dfs(grid: Array<Array<Int>>, visited: Array<Array<Boolean>>, currentCell: Pair<Int, Int>): Boolean {
    for (move in moves) {
        val i = currentCell.first + move.first
        val j = currentCell.second + move.second

        if (i in grid.indices && j in grid[0].indices && !visited[i][j] && grid[i][j] == 1) {
            visited[i][j] = true
            if (j == grid[0].size - 1 || dfs(grid, visited, Pair(i, j))) {
                grid[i][j] = 2
                return true
            }
        }
    }

    return false
}

fun traverse(grid: Array<Array<Int>>): Array<Array<Int>> {
    var i = 0
    val j = 0
    while (i < grid.size) {
        if (grid[i][j] == 1) {
            break
        }
        i++
    }

    val copyGrid = grid.copyOf()

    val visited = Array(grid.size) { Array(grid[0].size) { false } }

    visited[i][j] = true

    if (dfs(copyGrid, visited, Pair(i, j))) {
        copyGrid[i][j] = 2
    }

    return copyGrid
}


fun printMatrix(matrix: Array<Array<Int>>) {
    for (row in matrix) {
        for (cell in row) {
            print(when (cell) {
                0 -> "\u2588\u2588"
                1 -> "  "
                else -> "//"
            })
        }
        println()
    }
    println("\n\n")
}

@Suppress("unused")
fun infinite() {
    while (true) {
        print("Input: ")
        val (rows, cols) = readln().split(" ").map { it.toInt() }
        printMatrix(createMaze(rows, cols))
        println()
    }
}


fun main() {
    var currentMaze: Array<Array<Int>> = arrayOf()
    while (true) {
        println("=== Menu ===" +
                "1. Generate a new maze" + 
                "2. Load a maze" +
                "3. Save the maze" +
                "4. Display the maze" +
                "5. Find the escape" +
                "0. Exit\n")
        
        when(readln().toInt()) {
            1 -> {
                println("Input the size of the square maze")
                val input = readln().toInt()
                if (input < 3) {
                    println("Wrong Parameter!")
                } else {
                    currentMaze = createMaze(input, input)
                    printMatrix(currentMaze)
                }
            }
            2 -> {
                println("Input the file name to load the maze from")
                val fileInput = readln()
                val file = File(fileInput)
                if (file.exists()) {
                    val fileContents = file.readLines().map {
                        it.split(",").map { el -> el.toInt() }
                    }
                    currentMaze = Array(fileContents.size) {
                        row -> Array(fileContents[row].size) {
                            col -> fileContents[row][col]
                        }
                    }
                } else {
                    println("No such file exists")
                }

            }
            3 -> {
                println("Input the file name to save the maze in")
                val fileInput = readln()
                val file = File(fileInput)
                if (!file.exists()) file.createNewFile()
                val writer = file.bufferedWriter()

                currentMaze.map {
                    writer.write(it.joinToString(","))
                    writer.newLine()
                }

                writer.close()
            }
            4 -> printMatrix(currentMaze)
            5 -> {
                val traversedMaze = traverse(currentMaze)
                printMatrix(traversedMaze)
            }
            0 -> break
            else -> println("Bad Command!")
        }
    }
}
