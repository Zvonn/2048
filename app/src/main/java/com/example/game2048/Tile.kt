package com.example.game2048

class Tile(var value: Int) {
    var mergedFrom: Array<Tile>? = null

    fun copy(): Tile {
        return Tile(value)
    }
}