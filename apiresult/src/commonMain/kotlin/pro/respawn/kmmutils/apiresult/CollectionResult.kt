@file:Suppress("MemberVisibilityCanBePrivate", "unused", "NOTHING_TO_INLINE", "TooManyFunctions", "FunctionName")

package pro.respawn.kmmutils.apiresult

import pro.respawn.kmmutils.apiresult.ApiResult.Error
import pro.respawn.kmmutils.apiresult.ApiResult.Success

public inline fun <T> ApiResult<Collection<T>>.orEmpty(): Collection<T> = or(emptyList())
public inline fun <T> ApiResult<List<T>>.orEmpty(): List<T> = or(emptyList())

public inline fun <T> ApiResult<Set<T>>.orEmpty(): Set<T> = or(emptySet())
public inline fun <T> ApiResult<Sequence<T>>.orEmpty(): Sequence<T> = or(emptySequence())

public inline fun <T, R> Iterable<ApiResult<T>>.mapResults(
    transform: (T) -> R
): List<ApiResult<R>> = map { it.map(transform) }

public inline fun <T, R> Sequence<ApiResult<T>>.mapResults(
    crossinline transform: (T) -> R
): Sequence<ApiResult<R>> = map { it.map(transform) }

public inline fun <T> Iterable<ApiResult<T>>.mapErrors(
    transform: (Exception) -> Exception
): List<ApiResult<T>> = map { it.mapError(transform) }

public inline fun <T> Sequence<ApiResult<T>>.mapErrors(
    crossinline transform: (Exception) -> Exception,
): Sequence<ApiResult<T>> = map { it.mapError(transform) }

public inline fun <T> Iterable<ApiResult<T>>.filterErrors(): List<Error> = filterIsInstance<Error>()
public inline fun <T> Sequence<ApiResult<T>>.filterErrors(): Sequence<Error> = filterIsInstance<Error>()

public inline fun <T> Iterable<ApiResult<T>>.filterSuccesses(): List<Success<T>> = filterIsInstance<Success<T>>()
public inline fun <T> Sequence<ApiResult<T>>.filterSuccesses(): Sequence<Success<T>> = filterIsInstance<Success<T>>()

public inline fun <T> Iterable<ApiResult<T?>>.filterNulls(): List<ApiResult<T & Any>> =
    filter { it !is Success || it.result != null }.mapResults { it!! }

public inline fun <T> Sequence<ApiResult<T?>>.filterNulls(): Sequence<ApiResult<T & Any>> =
    filter { it !is Success || it.result != null }.mapResults { it!! }

public inline fun <T, R : Iterable<T>> ApiResult<R>.errorIfEmpty(
    exception: () -> Exception = { ConditionNotSatisfiedException("Collection was empty") },
): ApiResult<R> = errorIf(exception) { it.none() }

@JvmName("sequenceErrorIfEmpty")
public inline fun <T, R : Sequence<T>> ApiResult<R>.errorIfEmpty(
    exception: () -> Exception = { ConditionNotSatisfiedException("Sequence was empty") },
): ApiResult<R> = errorIf(exception) { it.none() }

/**
 * Executes [ApiResult.map] on each value of the collection
 */
public inline fun <T, R> ApiResult<Iterable<T>>.mapValues(
    transform: (T) -> R
): ApiResult<List<R>> = map { it.map(transform) }

/**
 * Executes [ApiResult.map] on each value of the sequence
 */
@JvmName("sequenceMapValues")
public inline fun <T, R> ApiResult<Sequence<T>>.mapValues(
    noinline transform: (T) -> R
): ApiResult<Sequence<R>> = map { it.map(transform) }