package com.koroliuk.emms.exception


class EventNotFoundException(message: String): RuntimeException(message)

class BlockedEventException(message: String): RuntimeException(message)

class GroupAlreadyExistsException(message: String): RuntimeException(message)
