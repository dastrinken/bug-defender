package de.mow2.towerdefense.model.pathfinding

import de.mow2.towerdefense.controller.GameController
import kotlin.math.abs
import kotlin.math.max

/**
 * A-star search algorithm, used by enemies to find the fastest way across the map
 */
class Astar(val controller: GameController) : java.io.Serializable {

    fun findPath(
        startNode: Node,
        targetNode: Node,
        playGroundRows: Int,
        playGroundCols: Int
    ): MutableSet<Node>? {
        val openSet = mutableSetOf<Node>()
        val closedSet = mutableSetOf<Node>()
        openSet.add(startNode)

        while (openSet.any()) {
            var currentNode = openSet.first()

            for (i in openSet) {
                if (i.f < currentNode.f) {
                    currentNode = i
                }
            }

            openSet.remove(currentNode)
            closedSet.add(currentNode)

            if (currentNode == targetNode) {
                val path = mutableSetOf<Node>()
                var tempNode = currentNode
                while (tempNode.parent != null) {
                    path.add(tempNode)
                    tempNode = tempNode.parent!!
                }
                return path
            }

            val neighbors = currentNode.getNeighbors(playGroundRows, playGroundCols)

            neighbors.forEach neighbors@{ node ->
                if (controller.playGround.squareArray[node.x][node.y].isBlocked) {
                    return@neighbors
                }

                for (closedNode in closedSet) {
                    if (closedNode.x == node.x && closedNode.y == node.y) {
                        return@neighbors
                    }
                }

                node.parent = currentNode
                node.g = currentNode.g + 1
                node.h = calculateHeuristics(node, targetNode)
                node.f = node.g + node.h

                openSet.forEach { openNode ->
                    if (node == openNode && node.g > openNode.g) {
                        return@neighbors
                    }
                }
                openSet.add(node)
            }
        }
        return null
    }

    /**
     * Represents a node on the playground holding important information for the algorithm to work
     * @param x horizontal position
     * @param y vertical position
     */
    data class Node(val x: Int, val y: Int, val controller: GameController) : Comparable<Node>,
        java.io.Serializable {
        // parent is the node that came previous to the current one
        var parent: Node? = null

        // g = distance from selected node to start node
        var g: Int = 0

        // h = distance from selected node to end node -> optimistic assignment: either equal or less than real distance
        var h: Int = 0

        // f = g + h -> the lower the value the more attractive it is as a path option
        var f: Int = g + h

        /**
         * returns all neighbor nodes of this node in a MutableSet
         * @param maxRows max rows on the 2D grid
         * @param maxCols max columns on the 2D grid
         * @return neighbors
         */
        fun getNeighbors(maxRows: Int, maxCols: Int): MutableSet<Node> {
            val neighbors = mutableSetOf<Node>()

            /**
             * straight
             */
            //left
            if (x - 1 >= 0) {
                if (!controller.playGround.squareArray[x - 1][y].isBlocked) {
                    neighbors.add(Node(x - 1, y, controller))
                }
            }
            //top
            if (y + 1 < maxCols) {
                if (!controller.playGround.squareArray[x][y + 1].isBlocked) {
                    neighbors.add(Node(x, y + 1, controller))
                }
            }
            //right
            if (x + 1 < maxRows) {
                if (!controller.playGround.squareArray[x + 1][y].isBlocked) {
                    neighbors.add(Node(x + 1, y, controller))
                }
            }
            //bottom
            if (y - 1 >= 0) {
                if (!controller.playGround.squareArray[x][y - 1].isBlocked) {
                    neighbors.add(Node(x, y - 1, controller))
                }
            }

            /**
             * diagonal
             */
            //diagonal bottom left
            if (x - 1 > 0 && y - 1 > 0) {
                if (!controller.playGround.squareArray[x - 1][y].isBlocked && !controller.playGround.squareArray[x][y - 1].isBlocked) {
                    neighbors.add(Node(x - 1, y - 1, controller))
                }
            }
            //diagonal top left
            if (x - 1 > 0 && y + 1 < maxCols) {
                if (!controller.playGround.squareArray[x - 1][y].isBlocked && !controller.playGround.squareArray[x][y + 1].isBlocked) {
                    neighbors.add(Node(x - 1, y + 1, controller))
                }
            }
            //diagonal top right
            if (x + 1 < maxRows && y + 1 < maxCols) {
                if (!controller.playGround.squareArray[x + 1][y].isBlocked && !controller.playGround.squareArray[x][y + 1].isBlocked) {
                    neighbors.add(Node(x + 1, y + 1, controller))
                }
            }
            //diagonal bottom right
            if (x + 1 < maxRows && y - 1 > 0) {
                if (!controller.playGround.squareArray[x + 1][y].isBlocked && !controller.playGround.squareArray[x][y - 1].isBlocked) {
                    neighbors.add(Node(x + 1, y - 1, controller))
                }
            }

            return neighbors
        }

        override fun compareTo(other: Node): Int = this.f.compareTo(other.f)
    }

    /**
     * Calculates the correct values to decide which way should be preferred (straight or diagonal).
     * 8 directions, diagonal cost and straight cost are the same. (chebyshev-distance)
     */
    private fun calculateHeuristics(from: Node, to: Node): Int {
        val xDist = abs(from.x - to.x)
        val yDist = abs(from.y - to.y)

        //return weightS * (xDist + yDist) + (weightD - 2 * weightS) * min(xDist, yDist)
        //return weightS * sqrt((xDist * xDist + yDist * yDist).toDouble())
        return max(xDist, yDist)
    }
}