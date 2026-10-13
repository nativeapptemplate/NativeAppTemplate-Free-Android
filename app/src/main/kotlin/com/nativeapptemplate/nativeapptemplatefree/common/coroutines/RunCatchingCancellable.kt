package com.nativeapptemplate.nativeapptemplatefree.common.coroutines

import kotlin.coroutines.cancellation.CancellationException

/**
 * Like [runCatching], but rethrows [CancellationException] so a cancelled coroutine stops instead
 * of treating its cancellation as an ordinary failure and carrying on.
 */
inline fun <T> runCatchingCancellable(block: () -> T): Result<T> = try {
  Result.success(block())
} catch (cancellation: CancellationException) {
  throw cancellation
} catch (throwable: Throwable) {
  Result.failure(throwable)
}
