/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.sessions

import io.ktor.application.*

/**
 * SessionTransport [receive], [send] or [clear] a session from/to an [ApplicationCall].
 */
interface SessionTransport {
    /**
     * Gets session information from a [call] and returns a String if success or null if failed.
     */
    fun receive(call: ApplicationCall): String?

    /**
     * Sets session information represented by [value] to a [call].
     */
    fun send(call: ApplicationCall, value: String)

    /**
     * Clears session information from a specific [call].
     */
    fun clear(call: ApplicationCall)
}

