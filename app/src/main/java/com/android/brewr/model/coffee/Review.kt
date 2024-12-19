package com.android.brewr.model.coffee

/**
 * Represents a review for a coffee shop, including the author's name, the review text, and the
 * rating given by the reviewer.
 *
 * @property authorName The name of the person who wrote the review.
 * @property review A textual description or comment provided by the reviewer.
 * @property rating The numerical rating given by the reviewer, typically on a scale of 1.0 to 5.0.
 */
data class Review(val authorName: String, val review: String, val rating: Double)
