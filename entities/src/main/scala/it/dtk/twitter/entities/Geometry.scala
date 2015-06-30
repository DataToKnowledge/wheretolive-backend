package it.dtk.twitter.entities

/**
 * Created by gigitsu on 30/06/15.
 */
case class Geometry(shape: Shape) {
  val Point = 'Point
  val Polygon = 'Polygon
  val LineString = 'LineString

  val MultiPoint = 'MultiPoint
  val MultiPolygon = 'MultiPolygon
  val MultiLineString = 'MultiLineString
}
