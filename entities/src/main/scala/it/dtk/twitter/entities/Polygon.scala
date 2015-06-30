package it.dtk.twitter.entities

/**
 * Created by gigitsu on 30/06/15.
 */
case class Polygon(bounds: LineString, holes: Seq[LineString]) extends Shape
