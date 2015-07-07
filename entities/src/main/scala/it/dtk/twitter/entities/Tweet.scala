package it.dtk.twitter.entities

import java.util.Date

/**
 * Created by gigitsu on 24/06/15.
 */
case class Tweet(id:             String,
                 text:           String,
                 urls:           Seq[String],
                 lang:           Option[Symbol],
                 user:           User,
                 place:          Option[Place],
                 hashtags:       Seq[String],
                 truncated:      Boolean,
                 createdAt:      Date,
                 coordinates:    Option[Geometry],
                 userMentions:   Map[String, String],
                 retweetCount:   Long,
                 favoriteCount:  Long )
