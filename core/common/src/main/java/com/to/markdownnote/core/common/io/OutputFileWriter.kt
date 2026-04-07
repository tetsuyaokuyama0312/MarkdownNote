package com.to.markdownnote.core.common.io

import com.to.markdownnote.core.common.util.nowTimestampForFileName

private const val DEFAULT_OUTPUT_FILE_NAME_PATTERN = "memo_%s.%s"

fun OutputFileType.defaultFileName(): String =
    DEFAULT_OUTPUT_FILE_NAME_PATTERN.format(nowTimestampForFileName(), getExtension())
