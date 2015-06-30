package it.dtk.twitter.entities

case class User(id:             String,
                name:           String,
                location:       Option[String],
                friendsCount:   Long,
                statusesCount:  Long,
                followersCount: Long )













