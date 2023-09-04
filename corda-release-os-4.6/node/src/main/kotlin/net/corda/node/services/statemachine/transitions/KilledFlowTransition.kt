package net.corda.node.services.statemachine.transitions

import net.corda.core.flows.FlowException
import net.corda.core.flows.KilledFlowException
import net.corda.node.services.statemachine.Action
import net.corda.node.services.statemachine.DeduplicationId
import net.corda.node.services.statemachine.ErrorSessionMessage
import net.corda.node.services.statemachine.Event
import net.corda.node.services.statemachine.FlowError
import net.corda.node.services.statemachine.FlowRemovalReason
import net.corda.node.services.statemachine.SessionId
import net.corda.node.services.statemachine.SessionState
import net.corda.node.services.statemachine.StateMachineState

class KilledFlowTransition(
    override val context: TransitionContext,
    override val startingState: StateMachineState,
    val event: Event
) : Transition {

    override fun transition(): TransitionResult {
        return builder {

            val killedFlowError = createKilledFlowError()
            val killedFlowErrorMessage = createErrorMessageFromError(killedFlowError)
            val errorMessages = listOf(killedFlowErrorMessage)

            val (initiatedSessions, newSessions) = bufferErrorMessagesInInitiatingSessions(
                startingState.checkpoint.checkpointState.sessions,
                errorMessages
            )
            currentState = currentState.copy(
                checkpoint = startingState.checkpoint.setSessions(sessions = newSessions),
                pendingDeduplicationHandlers = emptyList(),
                isRemoved = true
            )
            actions += Action.PropagateErrors(
                errorMessages,
                initiatedSessions,
                startingState.senderUUID
            )

            if (!startingState.isFlowResumed) {
                actions += Action.CreateTransaction
            }
            // The checkpoint and soft locks are also removed directly in [StateMachineManager.killFlow]
            if (startingState.isAnyCheckpointPersisted) {
                actions += Action.RemoveCheckpoint(context.id, mayHavePersistentResults = true)
            }
            actions += Action.PersistDeduplicationFacts(startingState.pendingDeduplicationHandlers)
            actions += Action.ReleaseSoftLocks(context.id.uuid)
            actions += Action.CommitTransaction(currentState)
            actions += Action.AcknowledgeMessages(startingState.pendingDeduplicationHandlers)
            actions += Action.RemoveSessionBindings(startingState.checkpoint.checkpointState.sessions.keys)
            actions += Action.RemoveFlow(context.id, createKilledRemovalReason(killedFlowError), currentState)

            FlowContinuation.Abort
        }
    }

    private fun createKilledFlowError(): FlowError {
        val exception = when (event) {
            is Event.Error -> event.exception
            else -> KilledFlowException(context.id)
        }
        return FlowError(context.secureRandom.nextLong(), exception)
    }

    // Purposely left the same as [bufferErrorMessagesInInitiatingSessions] in [ErrorFlowTransition] so that it can be refactored
    private fun createErrorMessageFromError(error: FlowError): ErrorSessionMessage {
        val exception = error.exception
        // If the exception doesn't contain an originalErrorId that means it's a fresh FlowException that should
        // propagate to the neighbouring flows. If it has the ID filled in that means it's a rethrown FlowException and
        // shouldn't be propagated.
        return if (exception is FlowException && exception.originalErrorId == null) {
            ErrorSessionMessage(flowException = exception, errorId = error.errorId)
        } else {
            ErrorSessionMessage(flowException = null, errorId = error.errorId)
        }
    }

    // Purposely left the same as [bufferErrorMessagesInInitiatingSessions] in [ErrorFlowTransition] so that it can be refactored
    // Buffer error messages in Initiating sessions, return the initialised ones.
    private fun bufferErrorMessagesInInitiatingSessions(
        sessions: Map<SessionId, SessionState>,
        errorMessages: List<ErrorSessionMessage>
    ): Pair<List<SessionState.Initiated>, Map<SessionId, SessionState>> {
        val newSessions = sessions.mapValues { (sourceSessionId, sessionState) ->
            if (sessionState is SessionState.Initiating && sessionState.rejectionError == null) {
                // *prepend* the error messages in order to error the other sessions ASAP. The other messages will
                // be delivered all the same, they just won't trigger flow resumption because of dirtiness.
                val errorMessagesWithDeduplication = errorMessages.map {
                    DeduplicationId.createForError(it.errorId, sourceSessionId) to it
                }
                sessionState.copy(bufferedMessages = errorMessagesWithDeduplication + sessionState.bufferedMessages)
            } else {
                sessionState
            }
        }
        // if we have already received error message from the other side, we don't include that session in the list to avoid propagating errors.
        val initiatedSessions = sessions.values.mapNotNull { session ->
            if (session is SessionState.Initiated && !session.otherSideErrored) {
                session
            } else {
                null
            }
        }
        return Pair(initiatedSessions, newSessions)
    }

    private fun createKilledRemovalReason(error: FlowError): FlowRemovalReason.ErrorFinish {
        return FlowRemovalReason.ErrorFinish(listOf(error))
    }
}