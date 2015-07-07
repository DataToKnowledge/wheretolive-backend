package it.dtk.twitter.entities

import java.util.{Date, Calendar,TimeZone}

import org.scalatest._
import spray.json._

import json.TwitterJsonProtocols._

/**
 * Created by gigitsu on 27/06/15.
 */
class TwitterJsonProtocolsSpec extends FlatSpec {

  "A zoned date time field" should "be correctly parsed" in {
    val json = JsString("Wed Aug 27 13:08:45 +0000 2008")
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Z"))
    cal.setTime(json.convertTo[Date])

    assert(cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
    assert(cal.get(Calendar.MONTH) == Calendar.AUGUST)
    assert(cal.get(Calendar.YEAR) == 2008)
    assert(cal.get(Calendar.DAY_OF_MONTH) == 27)

    assert(cal.get(Calendar.HOUR_OF_DAY) == 13)
    assert(cal.get(Calendar.MINUTE) == 8)
    assert(cal.get(Calendar.SECOND) == 45)

    assert(cal.get(Calendar.ZONE_OFFSET) == 0)
  }
  it should "be formatted to twitter format as well" in {
    val json = JsString("Wed Aug 27 13:08:45 +0000 2008")
    val dt = json.convertTo[Date]
    val newJson = dt.toJson

    assert(newJson == json)
  }

  "A geo point without altitude" should "be correctly parsed" in {
    val json = "[100.0, 0.0]".parseJson
    val p = json.convertTo[Point]

    assert(p.x == BigDecimal(100))
    assert(p.y == BigDecimal(0))
    assert(p.z.isEmpty)
  }
  it should "be formatted to json array as well" in {
    val json = "[100.0, 0.0]".parseJson
    val p  = json.convertTo[Point]
    val newJson = p.toJson

    assert(newJson == json)
  }

  "A geo point with altitude" should "be correctly parsed" in {
    val json = "[100.0, 0.0, 1.0]".parseJson
    val pa = json.convertTo[Point]

    assert(pa.x == BigDecimal(100))
    assert(pa.y == BigDecimal(0))
    assert(pa.z.get == BigDecimal(1))
  }
  it should "be formatted to json" in {
    val json = "[100.0, 0.0, 1.0]".parseJson
    val p = json.convertTo[Point]
    val newJson = p.toJson

    assert(newJson == json)
  }

  "A geo line string" should "be correctly parsed" in {
    val json = "[ [100.0, 0.0], [101.0, 1.0] ]".parseJson
    val l = json.convertTo[LineString]

    assert(l.points.length == 2)
    assert(l.points.head.x == BigDecimal(100))
    assert(l.points.head.y == BigDecimal(0))
    assert(l.points.head.z.isEmpty)
  }
  it should "be formatted to json" in {
    val json = "[ [100.0, 0.0], [101.0, 1.0] ]".parseJson
    val l = json.convertTo[LineString]
    val newJson = l.toJson

    assert(newJson == json)
  }

  "A geo polygon without holes" should "be correctly parsed" in {
    val json = "[ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ] ]".parseJson
    val p = json.convertTo[Polygon]

    assert(p.holes.isEmpty)
    assert(p.bounds.points.length == 5)
    assert(p.bounds.points.head.x == BigDecimal(100))
    assert(p.bounds.points.head.y == BigDecimal(0))
    assert(p.bounds.points(1).x == BigDecimal(101))
    assert(p.bounds.points(1).y == BigDecimal(0))
  }
  it should "be formatted to json" in {
    val json = "[ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ] ]".parseJson
    val p = json.convertTo[Polygon]
    val newJson = p.toJson

    assert(newJson == json)
  }

  "A geo polygon with holes" should "be correctly parsed" in {
    val json = "[ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ], [ [90.0, 2.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ] ]".parseJson
    val p = json.convertTo[Polygon]

    assert(p.holes.length == 1)
    assert(p.holes.head.points.head.x == BigDecimal(90))
    assert(p.holes.head.points.head.y == BigDecimal(2))
  }
  it should "be formatted to json" in {
    val json = "[ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ], [ [90.0, 2.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ] ]".parseJson
    val p = json.convertTo[Polygon]
    val newJson = p.toJson

    assert(newJson == json)
  }

  "A generic multi position" should "be correctly parsed" in {
    val jsonMpo = "[ [100.0, 0.0], [101.0, 1.0] ]".parseJson
    val mpo = jsonMpo.convertTo[MultiPoint]
    assert(mpo.points.length == 2)

    val jsonMls = "[ [ [100.0, 0.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ] ]".parseJson
    val mls = jsonMls.convertTo[MultiLineString]
    assert(mls.lineStrings.length == 2)

    val jsonMpl = "[ [[[102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.0], [102.0, 2.0]]], [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2]]] ]".parseJson
    val mpl = jsonMpl.convertTo[MultiPolygon]
    assert(mpl.polygons.length == 2)
    assert(mpl.polygons(1).holes.nonEmpty)
  }
  it should "be formatted to json" in {
    val jsonMpo = "[ [100.0, 0.0], [101.0, 1.0] ]".parseJson
    val mpo = jsonMpo.convertTo[MultiPoint]
    val newJsonMpo = mpo.toJson
    assert(newJsonMpo == jsonMpo)

    val jsonMls = "[ [ [100.0, 0.0], [101.0, 1.0] ], [ [102.0, 2.0], [103.0, 3.0] ] ]".parseJson
    val mls = jsonMls.convertTo[MultiLineString]
    val newJsonMls = mls.toJson
    assert(newJsonMls == jsonMls)

    val jsonMpl = "[ [[[102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.0], [102.0, 2.0]]], [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]], [[100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2]]] ]".parseJson
    val mpl = jsonMpl.convertTo[MultiPolygon]
    val newJsonMpl = mpl.toJson
    assert(newJsonMpl == jsonMpl)
  }

  "A coordinates field" should "be correctly parsed" in { //TODO: improve geometry test
    // Single point
    val json = """{ "coordinates": [ -75.14310264, 40.05701649 ], "type":"Point" }""".parseJson
    val c = json.convertTo[Geometry]

    // Multiple points
    val jsonMultiPoints = """{ "coordinates":[ [ [2.2241006,48.8155414], [2.4699099,48.8155414], [2.4699099,48.9021461], [2.2241006,48.9021461] ] ], "type":"Polygon" }""".parseJson
    val cMultiPoints = jsonMultiPoints.convertTo[Geometry]

  }
  it should "be formatted to geoJson as well" in {
    // Single point
    val json = """{ "coordinates": [ -75.14310264, 40.05701649 ], "type":"Point" }""".parseJson
    val c = json.convertTo[Geometry]
    val newJson = c.toJson

    assert(newJson == json)

    // Multiple points
    val jsonMultiPoints = """{ "coordinates":[ [ [2.2241006,48.8155414], [2.4699099,48.8155414], [2.4699099,48.9021461], [2.2241006,48.9021461] ] ], "type":"Polygon" }""".parseJson
    val cMultiPoints = jsonMultiPoints.convertTo[Geometry]
    val newJsonMultiPoints = cMultiPoints.toJson

    assert(newJsonMultiPoints == jsonMultiPoints)
  }

  "A twitter place object" should "be correctly parsed" in {
    val json = """{"name":"Twitter HQ","polylines":[],"country":"United States","country_code":"US","attributes":{"street_address":"795 Folsom St","623:id":"210176","twitter":"twitter"},"url":"https://api.twitter.com/1.1/geo/id/247f43d441defc03.json","bounding_box":{"coordinates":[[[-122.400612831116,37.7821120598956],[-122.400612831116,37.7821120598956],[-122.400612831116,37.7821120598956],[-122.400612831116,37.7821120598956]]],"type":"Polygon"},"id":"247f43d441defc03","contained_within":[{"name":"San Francisco","country":"United States","country_code":"US","attributes":{},"url":"https://api.twitter.com/1.1/geo/id/5a110d312052166f.json","bounding_box":{"coordinates":[[[-122.51368188,37.70813196],[-122.35845384,37.70813196],[-122.35845384,37.83245301],[-122.51368188,37.83245301]]],"type":"Polygon"},"id":"5a110d312052166f","full_name":"San Francisco, CA","place_type":"city"}],"full_name":"Twitter HQ, San Francisco","geometry":{"coordinates":[-122.400612831116,37.7821120598956],"type":"Point"},"place_type":"poi"}""".parseJson
    val c = json.convertTo[Place]

    assert(c.iso3.isEmpty)
    assert(c.region.isEmpty)
    assert(c.locality.isEmpty)
    assert(c.placeType == 'poi)
    assert(c.postalCode.isEmpty)
    assert(c.countryCode == 'US)
    assert(c.name == "Twitter HQ")
    assert(c.id == "247f43d441defc03")
    assert(c.country == "United States")
    assert(c.fullName == "Twitter HQ, San Francisco")
    assert(c.streetAddress.isDefined && c.streetAddress.get == "795 Folsom St")
  }
  it should "be formatted to json as well" in {
    val json = """{"name":"Twitter HQ","polylines":[],"country":"United States","country_code":"US","attributes":{"street_address":"795 Folsom St","623:id":"210176","twitter":"twitter"},"url":"https://api.twitter.com/1.1/geo/id/247f43d441defc03.json","bounding_box":{"coordinates":[[[-122.400612831116,37.7821120598956],[-122.400612831116,37.7821120598956],[-122.400612831116,37.7821120598956],[-122.400612831116,37.7821120598956]]],"type":"Polygon"},"id":"247f43d441defc03","contained_within":[{"name":"San Francisco","country":"United States","country_code":"US","attributes":{},"url":"https://api.twitter.com/1.1/geo/id/5a110d312052166f.json","bounding_box":{"coordinates":[[[-122.51368188,37.70813196],[-122.35845384,37.70813196],[-122.35845384,37.83245301],[-122.51368188,37.83245301]]],"type":"Polygon"},"id":"5a110d312052166f","full_name":"San Francisco, CA","place_type":"city"}],"full_name":"Twitter HQ, San Francisco","geometry":{"coordinates":[-122.400612831116,37.7821120598956],"type":"Point"},"place_type":"poi"}""".parseJson.convertTo[Place].toJson
    val c = json.convertTo[Place]
    val newJson = c.toJson

    assert(newJson == json)
  }

  "A twitter user object" should "be correctly parsed" in {
    val json ="""{"statuses_count":3080, "favourites_count":22, "protected":false, "profile_text_color":"437792", "profile_image_url":"...", "name":"Twitter API", "profile_sidebar_fill_color":"a9d9f1", "listed_count":9252, "following":true, "profile_background_tile":false, "utc_offset":-28800, "description":"The Real Twitter API. I tweet about API changes, service issues and happily answer questions about Twitter and our API. Don't get an answer? It's on my website.", "location":"San Francisco, CA", "contributors_enabled":true, "verified":true, "profile_link_color":"0094C2", "followers_count":665829, "url":"http:\/\/dev.twitter.com", "default_profile":false, "profile_sidebar_border_color":"0094C2", "screen_name":"twitterapi", "default_profile_image":false, "notifications":false, "display_url":null, "show_all_inline_media":false, "geo_enabled":true, "profile_use_background_image":true, "friends_count":32, "id_str":"6253282", "entities":{"hashtags":[], "urls":[], "user_mentions":[]}, "expanded_url":null, "is_translator":false, "lang":"en", "time_zone":"Pacific Time (US & Canada)", "created_at":"Wed May 23 06:01:13 +0000 2007", "profile_background_color":"e8f2f7", "id":6253282, "follow_request_sent":false, "profile_background_image_url_https":"...", "profile_background_image_url":"...", "profile_image_url_https":"..."}""".parseJson
    val u = json.convertTo[User]

    assert(u.id == "6253282")
    assert(u.friendsCount == 32)
    assert(u.name == "Twitter API")
    assert(u.statusesCount == 3080)
    assert(u.followersCount == 665829)
    assert(u.location.isDefined && u.location.get == "San Francisco, CA")
  }
  it should "be formatted to json as well" in {
    val json = """{"statuses_count":3080, "favourites_count":22, "protected":false, "profile_text_color":"437792", "profile_image_url":"...", "name":"Twitter API", "profile_sidebar_fill_color":"a9d9f1", "listed_count":9252, "following":true, "profile_background_tile":false, "utc_offset":-28800, "description":"The Real Twitter API. I tweet about API changes, service issues and happily answer questions about Twitter and our API. Don't get an answer? It's on my website.", "location":"San Francisco, CA", "contributors_enabled":true, "verified":true, "profile_link_color":"0094C2", "followers_count":665829, "url":"http:\/\/dev.twitter.com", "default_profile":false, "profile_sidebar_border_color":"0094C2", "screen_name":"twitterapi", "default_profile_image":false, "notifications":false, "display_url":null, "show_all_inline_media":false, "geo_enabled":true, "profile_use_background_image":true, "friends_count":32, "id_str":"6253282", "entities":{"hashtags":[], "urls":[], "user_mentions":[]}, "expanded_url":null, "is_translator":false, "lang":"en", "time_zone":"Pacific Time (US & Canada)", "created_at":"Wed May 23 06:01:13 +0000 2007", "profile_background_color":"e8f2f7", "id":6253282, "follow_request_sent":false, "profile_background_image_url_https":"...", "profile_background_image_url":"...", "profile_image_url_https":"..."}""".parseJson.convertTo[User].toJson
    val u = json.convertTo[User]
    val newJson = u.toJson

    assert(newJson == json)
  }

  "A tweet object" should "be correctly parsed" in {
    val json = """{"created_at":"Tue Jun 16 14:35:03 +0000 2015","id":610817868918325248,"id_str":"610817868918325248","text":"RT @TheScript_Danny: @yustisiadentia just watching on the news , a balcony collapsed at a 21st birthday heartbreaking","source":"\u003ca href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\"\u003eTwitter for iPhone\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":179211291,"id_str":"179211291","name":"Dan:)","screen_name":"katieanddannny","location":"Ireland","url":null,"description":"Danny O'Donoghue is my favourite person xx Snapchat - katieanddanny Love Tan&Jim","protected":false,"verified":false,"followers_count":2040,"friends_count":1951,"listed_count":9,"favourites_count":5406,"statuses_count":10040,"created_at":"Mon Aug 16 19:44:40 +0000 2010","utc_offset":3600,"time_zone":"Casablanca","geo_enabled":true,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"0099B9","profile_background_image_url":"http:\/\/pbs.twimg.com\/profile_background_images\/837162092\/395ba21053a5c07c1e6386a938819349.png","profile_background_image_url_https":"https:\/\/pbs.twimg.com\/profile_background_images\/837162092\/395ba21053a5c07c1e6386a938819349.png","profile_background_tile":true,"profile_link_color":"0099B9","profile_sidebar_border_color":"FFFFFF","profile_sidebar_fill_color":"95E8EC","profile_text_color":"3C3940","profile_use_background_image":true,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/610219492887674881\/Bw_aidTE_normal.jpg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/610219492887674881\/Bw_aidTE_normal.jpg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/179211291\/1432755561","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweeted_status":{"created_at":"Tue Jun 16 14:23:29 +0000 2015","id":610814959602245633,"id_str":"610814959602245633","text":"@yustisiadentia just watching on the news , a balcony collapsed at a 21st birthday heartbreaking","source":"\u003ca href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\"\u003eTwitter for iPhone\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":610810276942905344,"in_reply_to_status_id_str":"610810276942905344","in_reply_to_user_id":45771160,"in_reply_to_user_id_str":"45771160","in_reply_to_screen_name":"yustisiadentia","user":{"id":498716358,"id_str":"498716358","name":"Danny O'Donoghue","screen_name":"TheScript_Danny","location":"","url":"http:\/\/thescriptmusic.com","description":"Download No Sound Without Silence: @Itunes: http:\/\/smarturl.it\/NSWSDigi Listen on @Spotify: http:\/\/smarturl.it\/TheScriptSptfy","protected":false,"verified":true,"followers_count":1134594,"friends_count":3868,"listed_count":1493,"favourites_count":1521,"statuses_count":4026,"created_at":"Tue Feb 21 10:32:57 +0000 2012","utc_offset":3600,"time_zone":"Casablanca","geo_enabled":true,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"050505","profile_background_image_url":"http:\/\/pbs.twimg.com\/profile_background_images\/433310182\/Twitter_background_copy.jpg","profile_background_image_url_https":"https:\/\/pbs.twimg.com\/profile_background_images\/433310182\/Twitter_background_copy.jpg","profile_background_tile":false,"profile_link_color":"050505","profile_sidebar_border_color":"FFFFFF","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":false,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/430055732260913152\/bwpeT1Wp_normal.jpeg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/430055732260913152\/bwpeT1Wp_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/498716358\/1405702112","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":11,"favorite_count":23,"entities":{"hashtags":[{"indices":[32,36],"text":"lol"}],"trends":[],"urls":[{"indices":[32,52], "url":"http:\/\/t.co\/IOwBrTZR", "display_url":"youtube.com\/watch?v=oHg5SJ\u2026", "expanded_url":"http:\/\/www.youtube.com\/watch?v=oHg5SJYRHA0"}],"user_mentions":[{"screen_name":"yustisiadentia","name":"Dentia Yustisia","id":45771160,"id_str":"45771160","indices":[0,15]}],"symbols":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"filter_level":"low","lang":"en"},"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[{"indices":[32,36],"text":"lol"}],"trends":[],"urls":[{"indices":[32,52], "url":"http:\/\/t.co\/IOwBrTZR", "display_url":"youtube.com\/watch?v=oHg5SJ\u2026", "expanded_url":"http:\/\/www.youtube.com\/watch?v=oHg5SJYRHA0"}],"user_mentions":[{"screen_name":"TheScript_Danny","name":"Danny O'Donoghue","id":498716358,"id_str":"498716358","indices":[3,19]},{"screen_name":"yustisiadentia","name":"Dentia Yustisia","id":45771160,"id_str":"45771160","indices":[21,36]}],"symbols":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"filter_level":"low","lang":"en","timestamp_ms":"1434465303054"}""".parseJson
    val t = json.convertTo[Tweet]
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Z"))
    cal.setTime(t.createdAt)

    assert(cal.get(Calendar.HOUR_OF_DAY) == 14)
    assert(cal.get(Calendar.MINUTE) == 35)
    assert(cal.get(Calendar.SECOND) == 3)
    assert(cal.get(Calendar.YEAR) == 2015)
    assert(cal.get(Calendar.MONTH) == Calendar.JUNE)
    assert(cal.get(Calendar.DAY_OF_MONTH) == 16)
    assert(cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
    assert(cal.get(Calendar.ZONE_OFFSET) == 0)
    assert(t.id == "610817868918325248")
    assert(t.user.id == "179211291")
    assert(t.lang.isDefined && t.lang.get == 'en)
    assert(!t.truncated)
    assert(t.coordinates.isEmpty)
    assert(t.place.isEmpty)
    assert(t.retweetCount == 0)
    assert(t.favoriteCount == 0)
    assert(Some(t.text).exists(_.trim.nonEmpty))
    assert(t.urls.nonEmpty && t.urls.head == "http://www.youtube.com/watch?v=oHg5SJYRHA0")
    assert(t.hashtags.nonEmpty && t.hashtags.head == "lol")
    assert(t.userMentions.size == 2 && t.userMentions.head._2 == "Danny O'Donoghue")
  }
  it should "be formatted to json as well" in {
    val json = """{"created_at":"Tue Jun 16 14:35:03 +0000 2015","id":610817868918325248,"id_str":"610817868918325248","text":"RT @TheScript_Danny: @yustisiadentia just watching on the news , a balcony collapsed at a 21st birthday heartbreaking","source":"\u003ca href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\"\u003eTwitter for iPhone\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":179211291,"id_str":"179211291","name":"Dan:)","screen_name":"katieanddannny","location":"Ireland","url":null,"description":"Danny O'Donoghue is my favourite person xx Snapchat - katieanddanny Love Tan&Jim","protected":false,"verified":false,"followers_count":2040,"friends_count":1951,"listed_count":9,"favourites_count":5406,"statuses_count":10040,"created_at":"Mon Aug 16 19:44:40 +0000 2010","utc_offset":3600,"time_zone":"Casablanca","geo_enabled":true,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"0099B9","profile_background_image_url":"http:\/\/pbs.twimg.com\/profile_background_images\/837162092\/395ba21053a5c07c1e6386a938819349.png","profile_background_image_url_https":"https:\/\/pbs.twimg.com\/profile_background_images\/837162092\/395ba21053a5c07c1e6386a938819349.png","profile_background_tile":true,"profile_link_color":"0099B9","profile_sidebar_border_color":"FFFFFF","profile_sidebar_fill_color":"95E8EC","profile_text_color":"3C3940","profile_use_background_image":true,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/610219492887674881\/Bw_aidTE_normal.jpg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/610219492887674881\/Bw_aidTE_normal.jpg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/179211291\/1432755561","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweeted_status":{"created_at":"Tue Jun 16 14:23:29 +0000 2015","id":610814959602245633,"id_str":"610814959602245633","text":"@yustisiadentia just watching on the news , a balcony collapsed at a 21st birthday heartbreaking","source":"\u003ca href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\"\u003eTwitter for iPhone\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":610810276942905344,"in_reply_to_status_id_str":"610810276942905344","in_reply_to_user_id":45771160,"in_reply_to_user_id_str":"45771160","in_reply_to_screen_name":"yustisiadentia","user":{"id":498716358,"id_str":"498716358","name":"Danny O'Donoghue","screen_name":"TheScript_Danny","location":"","url":"http:\/\/thescriptmusic.com","description":"Download No Sound Without Silence: @Itunes: http:\/\/smarturl.it\/NSWSDigi Listen on @Spotify: http:\/\/smarturl.it\/TheScriptSptfy","protected":false,"verified":true,"followers_count":1134594,"friends_count":3868,"listed_count":1493,"favourites_count":1521,"statuses_count":4026,"created_at":"Tue Feb 21 10:32:57 +0000 2012","utc_offset":3600,"time_zone":"Casablanca","geo_enabled":true,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"050505","profile_background_image_url":"http:\/\/pbs.twimg.com\/profile_background_images\/433310182\/Twitter_background_copy.jpg","profile_background_image_url_https":"https:\/\/pbs.twimg.com\/profile_background_images\/433310182\/Twitter_background_copy.jpg","profile_background_tile":false,"profile_link_color":"050505","profile_sidebar_border_color":"FFFFFF","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":false,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/430055732260913152\/bwpeT1Wp_normal.jpeg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/430055732260913152\/bwpeT1Wp_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/498716358\/1405702112","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":11,"favorite_count":23,"entities":{"hashtags":[{"indices":[32,36],"text":"lol"}],"trends":[],"urls":[{"indices":[32,52], "url":"http:\/\/t.co\/IOwBrTZR", "display_url":"youtube.com\/watch?v=oHg5SJ\u2026", "expanded_url":"http:\/\/www.youtube.com\/watch?v=oHg5SJYRHA0"}],"user_mentions":[{"screen_name":"yustisiadentia","name":"Dentia Yustisia","id":45771160,"id_str":"45771160","indices":[0,15]}],"symbols":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"filter_level":"low","lang":"en"},"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[{"indices":[32,36],"text":"lol"}],"trends":[],"urls":[{"indices":[32,52], "url":"http:\/\/t.co\/IOwBrTZR", "display_url":"youtube.com\/watch?v=oHg5SJ\u2026", "expanded_url":"http:\/\/www.youtube.com\/watch?v=oHg5SJYRHA0"}],"user_mentions":[{"screen_name":"TheScript_Danny","name":"Danny O'Donoghue","id":498716358,"id_str":"498716358","indices":[3,19]},{"screen_name":"yustisiadentia","name":"Dentia Yustisia","id":45771160,"id_str":"45771160","indices":[21,36]}],"symbols":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"filter_level":"low","lang":"en","timestamp_ms":"1434465303054"}""".parseJson.convertTo[Tweet].toJson
    val t = json.convertTo[Tweet]
    val newJson = t.toJson

    assert(newJson == json)
  }
}
