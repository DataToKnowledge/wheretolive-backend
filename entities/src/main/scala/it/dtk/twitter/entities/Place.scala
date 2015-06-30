package it.dtk.twitter.entities

/**
 * Created by gigitsu on 30/06/15.
 */
case class Place(id:            String,
                 name:          String,
                 iso3:          Option[Symbol],
                 region:        Option[String],
                 country:       String,
                 locality:      Option[String],
                 fullName:      String,
                 placeType:     Symbol,
                 postalCode:    Option[Symbol],
                 countryCode:   Symbol,
                 boundingBox:   Geometry,
                 streetAddress: Option[String] )
