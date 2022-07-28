package io.github.leonschreuder.kotlinexec

/** This is implemented as a state machine, using state functions as the state */
var collectedStringGroups = mutableListOf<String>()
var currentString = mutableListOf<Char>()

// Variable to hold the state we are currently processing
var stateFn = ::stateNormal

/** Splits words on spaces, but respecting (nested) quoting correctly. */
fun String.splitWords(): List<String> {
    collectedStringGroups = mutableListOf()
    currentString = mutableListOf()
    chars().forEach { stateFn(it.toChar()) }
    endGroup()
    return collectedStringGroups
}

/** The state we start in, collecting characters until we find a special char */
internal fun stateNormal(c: Char) {
    when (c) {
        ' ' -> endGroup()
        '\'' -> stateFn = ::stateSingleQuoted
        '"' -> stateFn = ::stateDoubleQuoted
        else -> currentString.add(c)
    }
}

/** finish the current group, add it to the collection, and start over */
private fun endGroup() {
    if (currentString.size > 0) { // don't finish a group if we haven't started one yet
        collectedStringGroups.add(String(currentString.toCharArray()))
    }
    currentString = mutableListOf()
}

/** We are currently reading inside a single-quoted string */
internal fun stateSingleQuoted(c: Char) = handleQuoted(c, '\'')

/** We are currently reading inside a double-quoted string */
internal fun stateDoubleQuoted(c: Char) = handleQuoted(c, '"')

// we need to know if this char was preceded by an escape sequence
var lastWasEscape = false

private fun handleQuoted(c: Char, quoteType: Char) {
    if (c == '\\') {
        lastWasEscape = true
    } else {
        if (c == quoteType && !lastWasEscape) {
            endGroup()
            stateFn = ::stateNormal
        } else {
            currentString.add(c)
        }
        lastWasEscape = false
    }
}
