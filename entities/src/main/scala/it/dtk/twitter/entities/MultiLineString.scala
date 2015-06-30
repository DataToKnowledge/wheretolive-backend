package it.dtk.twitter.entities

/**
 * Created by gigitsu on 30/06/15.
 */
case class MultiLineString(lineStrings: Seq[LineString]) extends Shape
