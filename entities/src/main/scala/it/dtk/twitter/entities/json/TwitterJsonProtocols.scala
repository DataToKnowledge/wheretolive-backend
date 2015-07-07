package it.dtk.twitter.entities.json

import java.util.{TimeZone, Locale, Date}
import java.text.SimpleDateFormat

import it.dtk.twitter.entities._
import spray.json._

/**
 * Created by gigitsu on 27/06/15.
 */
trait TwitterJsonProtocols extends DefaultJsonProtocol {

  implicit object PointFormat extends JsonFormat[Point] {
    override def read(json: JsValue): Point = json match {
      case JsArray(a) => a match {
        case Seq(x:JsNumber,y:JsNumber) => Point(x.value,y.value,None)
        case Seq(x:JsNumber,y:JsNumber,z:JsNumber) => Point(x.value,y.value,Some(z.value))
        case x => deserializationError("Expected min 2 imtes and max 3 numbers for point, but got " + x.length)
      }

      case x => deserializationError("Expected Array[BidDecimal] as JsArray, but got " + x)
    }

    override def write(obj: Point): JsValue = ( Seq(obj.x, obj.y) ++ obj.z ).toJson
  }

  implicit object LineStringFormat extends JsonFormat[LineString] {
    override def read(json: JsValue): LineString = LineString(json.convertTo[Seq[Point]])

    override def write(obj: LineString): JsValue = obj.points.toJson
  }

  implicit object PolygonFormat extends JsonFormat[Polygon] {
    override def read(json: JsValue): Polygon = {
      val lines = json.convertTo[Seq[LineString]]
      Polygon(lines.head, lines.tail)
    }

    override def write(obj: Polygon): JsValue = (obj.bounds +: obj.holes).toJson
  }

  implicit object MultiPointFormat extends JsonFormat[MultiPoint] {
    override def read(json: JsValue) = MultiPoint(json.convertTo[Seq[Point]])

    override def write(obj: MultiPoint): JsValue = obj.points.toJson
  }

  implicit object MultiPolygonFormat extends JsonFormat[MultiPolygon] {
    override def read(json: JsValue) = MultiPolygon(json.convertTo[Seq[Polygon]])

    override def write(obj: MultiPolygon): JsValue = obj.polygons.toJson
  }

  implicit object MultiLineStringFormat extends JsonFormat[MultiLineString] {
    override def read(json: JsValue) = MultiLineString(json.convertTo[Seq[LineString]])

    override def write(obj: MultiLineString): JsValue = obj.lineStrings.toJson
  }

  implicit object CoordinatesFormat extends JsonFormat[Geometry] {
    override def read(json: JsValue): Geometry = fromField[Symbol](json, "type") match {
      case 'Point => Geometry(fromField[Point](json, "coordinates"))
      case 'Polygon => Geometry(fromField[Polygon](json, "coordinates"))
      case 'LineString => Geometry(fromField[LineString](json, "coordinates"))

      case 'MultiPoint => Geometry(fromField[MultiPoint](json, "coordinates"))
      case 'MultiPolygon => Geometry(fromField[MultiPolygon](json, "coordinates"))
      case 'MultiLineString => Geometry(fromField[MultiLineString](json, "coordinates"))
    }

    override def write(obj: Geometry): JsValue = {
      obj.shape match {
        case x: Point           => JsObject(Map( ("coordinates",x.toJson), ("type",JsString('Point))           ))
        case x: Polygon         => JsObject(Map( ("coordinates",x.toJson), ("type",JsString('Polygon))         ))
        case x: LineString      => JsObject(Map( ("coordinates",x.toJson), ("type",JsString('LineString))      ))
        case x: MultiPoint      => JsObject(Map( ("coordinates",x.toJson), ("type",JsString('MultiPoint))      ))
        case x: MultiPolygon    => JsObject(Map( ("coordinates",x.toJson), ("type",JsString('MultiPolygon))    ))
        case x: MultiLineString => JsObject(Map( ("coordinates",x.toJson), ("type",JsString('MultiLineString)) ))
      }
    }
  }

  implicit object LocalDateTimeFormat extends JsonFormat[Date] {
    val twitterFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH)
    twitterFormatter.setTimeZone(TimeZone.getTimeZone("Z"))

    override def read(json: JsValue): Date = json match {
      case JsString(s) => twitterFormatter.parse(s)
      case x => deserializationError("Expected ZoneDateTime as JsString, but got " + x)
    }

    override def write(obj: Date): JsValue = JsString(twitterFormatter.format(obj))
  }

  implicit object PlaceFormat extends JsonFormat[Place] {
    override def read(json: JsValue): Place = {
      val attr = fromField[JsObject](json, "attributes")
      Place(
        id            = fromField[String]         (json, "id"),
        name          = fromField[String]         (json, "name"),
        iso3          = fromField[Option[Symbol]] (attr, "iso3"),
        region        = fromField[Option[String]] (attr, "region"),
        country       = fromField[String]         (json, "country"),
        locality      = fromField[Option[String]] (attr, "locality"),
        fullName      = fromField[String]         (json, "full_name"),
        placeType     = fromField[Symbol]         (json, "place_type"),
        postalCode    = fromField[Option[Symbol]] (attr, "postal_code"),
        countryCode   = fromField[Symbol]         (json, "country_code"),
        boundingBox   = fromField[Geometry]       (json, "bounding_box"),
        streetAddress = fromField[Option[String]] (attr, "street_address")
      )
    }

    override def write(obj: Place): JsValue = {
      val attr = JsObject(Map(
        ("iso3", obj.iso3.toJson),
        ("region", obj.region.toJson),
        ("locality", obj.locality.toJson),
        ("postal_code", obj.postalCode.toJson),
        ("street_address", obj.streetAddress.toJson)
      ))

      JsObject(Map(
        ("id", obj.id.toJson),
        ("name", obj.name.toJson),
        ("country", obj.country.toJson),
        ("full_name", obj.fullName.toJson),
        ("place_type", obj.placeType.toJson),
        ("country_code", obj.countryCode.toJson),
        ("bounding_box", obj.boundingBox.toJson),

        ("attributes", attr)
      ))
    }
  }

  implicit object UserFormat extends JsonFormat[User] {
    override def read(json: JsValue): User = User (
      id             = fromField[String](json, "id_str"),
      name           = fromField[String](json, "name"),
      location       = fromField[Option[String]](json, "location"),
      friendsCount   = fromField[Long](json, "friends_count"),
      statusesCount  = fromField[Long](json, "statuses_count"),
      followersCount = fromField[Long](json, "followers_count")
    )

    override def write(obj: User): JsValue = JsObject(Map(
      ("id_str", obj.id.toJson),
      ("name", obj.name.toJson),
      ("location", obj.location.toJson),
      ("friends_count", obj.friendsCount.toJson),
      ("statuses_count", obj.statusesCount.toJson),
      ("followers_count", obj.followersCount.toJson)
    ))
  }

  implicit object TweetFormat extends JsonFormat[Tweet] {
    override def read(json: JsValue): Tweet = {
      val entities = fromField[JsObject](json, "entities")

      Tweet(
        id            = fromField[String](json, "id_str"),
        text          = fromField[String](json, "text"),
        urls          = fromField[JsArray](entities, "urls").elements.map(fromField[String](_, "expanded_url")),
        lang          = fromField[Option[Symbol]](json, "lang"),
        user          = fromField[User](json, "user"),
        place         = fromField[Option[Place]](json, "place"),
        hashtags      = fromField[JsArray](entities, "hashtags").elements.map(fromField[String](_, "text")),
        truncated     = fromField[Boolean](json, "truncated"),
        createdAt     = fromField[Date](json, "created_at"),
        coordinates   = fromField[Option[Geometry]](json, "coordinates"),
        userMentions  = fromField[JsArray](entities, "user_mentions").elements.map(um => fromField[String](um,"id_str") -> fromField[String](um,"name")).toMap,
        retweetCount  = fromField[Long](json, "retweet_count"),
        favoriteCount = fromField[Long](json, "favorite_count")
      )
    }

    override def write(obj: Tweet): JsValue = {
      val entities = JsObject(Map(
        ("urls", obj.urls.map(url => JsObject(Map( ("expanded_url", url.toJson) ))).toJson),
        ("hashtags", obj.hashtags.map(text => JsObject(Map( ("text", text.toJson) ))).toJson),
        ("user_mentions", obj.userMentions.map(kv => JsObject(Map( ("id_str", kv._1.toJson), ("name", kv._2.toJson) ))).toJson)
      ))

      JsObject(Map(
        ("text",           obj.text.toJson),
        ("lang",           obj.lang.toJson),
        ("user",           obj.user.toJson),
        ("place",          obj.place.toJson),
        ("id_str",         obj.id.toJson),
        ("truncated",      obj.truncated.toJson),
        ("created_at",     obj.createdAt.toJson),
        ("coordinates",    obj.coordinates.toJson),
        ("retweet_count",  obj.retweetCount.toJson),
        ("favorite_count", obj.favoriteCount.toJson),

        ("entities", entities)
      ))
    }
  }
}

object TwitterJsonProtocols extends TwitterJsonProtocols