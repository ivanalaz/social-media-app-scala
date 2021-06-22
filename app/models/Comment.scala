package models

import java.time.LocalDateTime

case class Comment(id: Int, user: User, text: String, date: LocalDateTime, post: Post)
