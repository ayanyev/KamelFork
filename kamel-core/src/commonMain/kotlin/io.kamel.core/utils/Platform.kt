package io.kamel.core.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public expect val Dispatchers.IO: CoroutineDispatcher

public expect class File

public expect class URL

public expect class URI