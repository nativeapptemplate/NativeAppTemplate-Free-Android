package com.nativeapptemplate.nativeapptemplatefree.testing.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.reflect.KFunction

/**
 * Error injection shared by the repository test doubles.
 *
 * A test names the call by the repository interface's function reference, so a renamed or
 * removed call fails to compile instead of silently never failing:
 *
 * ```
 * shopRepository.fail(ShopRepository::createShop, testApiError)
 * ```
 *
 * The real repositories throw from inside the flow (`emitApiResponse`), so a failing call
 * returns a flow that throws when collected, not a call that throws.
 */
class CallFailures {
  private val errors = mutableMapOf<String, Throwable>()

  /** Makes every later [call] fail with [error]. */
  fun fail(call: KFunction<*>, error: Throwable) {
    errors[call.name] = error
  }

  /** [success], or a flow that throws the error set for [call] by [fail]. */
  fun <T> orFail(call: KFunction<*>, success: Flow<T>): Flow<T> = errors[call.name]?.let { error -> flow { throw error } } ?: success
}

/** Implemented by the repository test doubles that can fail chosen calls. */
interface FailableRepository {
  val failures: CallFailures

  /** Makes every later [call] fail with [error]. */
  fun fail(call: KFunction<*>, error: Throwable) = failures.fail(call, error)
}
