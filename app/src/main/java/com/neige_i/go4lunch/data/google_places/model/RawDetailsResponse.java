package com.neige_i.go4lunch.data.google_places.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RawDetailsResponse {

    @Nullable
    @SerializedName("html_attributions")
    @Expose
    private final List<Object> htmlAttributions;
    @Nullable
    @SerializedName("result")
    @Expose
    private final Result result;
    @Nullable
    @SerializedName("status")
    @Expose
    private final String status;

    public RawDetailsResponse(
        @Nullable List<Object> htmlAttributions,
        @Nullable Result result,
        @Nullable String status
    ) {
        this.htmlAttributions = htmlAttributions;
        this.result = result;
        this.status = status;
    }

    @Nullable
    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    @Nullable
    public Result getResult() {
        return result;
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public static class Close {

        @Nullable
        @SerializedName("day")
        @Expose
        private final Integer day;
        @Nullable
        @SerializedName("time")
        @Expose
        private final String time;

        public Close(@Nullable Integer day, @Nullable String time) {
            this.day = day;
            this.time = time;
        }

        @Nullable
        public Integer getDay() {
            return day;
        }

        @Nullable
        public String getTime() {
            return time;
        }
    }

    public static class Geometry {

        @Nullable
        @SerializedName("location")
        @Expose
        private final Location location;

        public Geometry(@Nullable Location location) {
            this.location = location;
        }

        @Nullable
        public Location getLocation() {
            return location;
        }
    }

    public static class Location {

        @Nullable
        @SerializedName("lat")
        @Expose
        private final Double lat;
        @Nullable
        @SerializedName("lng")
        @Expose
        private final Double lng;

        public Location(@Nullable Double lat, @Nullable Double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        @Nullable
        public Double getLat() {
            return lat;
        }

        @Nullable
        public Double getLng() {
            return lng;
        }
    }

    public static class Open {

        @Nullable
        @SerializedName("day")
        @Expose
        private final Integer day;
        @Nullable
        @SerializedName("time")
        @Expose
        private final String time;

        public Open(@Nullable Integer day, @Nullable String time) {
            this.day = day;
            this.time = time;
        }

        @Nullable
        public Integer getDay() {
            return day;
        }

        @Nullable
        public String getTime() {
            return time;
        }
    }

    public static class OpeningHours {

        @Nullable
        @SerializedName("open_now")
        @Expose
        private final Boolean openNow;
        @Nullable
        @SerializedName("periods")
        @Expose
        private final List<Period> periods;
        @Nullable
        @SerializedName("weekday_text")
        @Expose
        private final List<String> weekdayText;

        public OpeningHours(
            @Nullable Boolean openNow,
            @Nullable List<Period> periods,
            @Nullable List<String> weekdayText
        ) {
            this.openNow = openNow;
            this.periods = periods;
            this.weekdayText = weekdayText;
        }

        @Nullable
        public Boolean getOpenNow() {
            return openNow;
        }

        @Nullable
        public List<Period> getPeriods() {
            return periods;
        }

        @Nullable
        public List<String> getWeekdayText() {
            return weekdayText;
        }
    }

    public static class Period {

        @Nullable
        @SerializedName("close")
        @Expose
        private final Close close;
        @Nullable
        @SerializedName("open")
        @Expose
        private final Open open;

        public Period(
            @Nullable Close close,
            @Nullable Open open
        ) {
            this.close = close;
            this.open = open;
        }

        @Nullable
        public Close getClose() {
            return close;
        }

        @Nullable
        public Open getOpen() {
            return open;
        }
    }

    public static class Photo {

        @Nullable
        @SerializedName("height")
        @Expose
        private final Integer height;
        @Nullable
        @SerializedName("html_attributions")
        @Expose
        private final List<String> htmlAttributions;
        @Nullable
        @SerializedName("photo_reference")
        @Expose
        private final String photoReference;
        @Nullable
        @SerializedName("width")
        @Expose
        private final Integer width;

        public Photo(
            @Nullable Integer height,
            @Nullable List<String> htmlAttributions,
            @Nullable String photoReference,
            @Nullable Integer width
        ) {
            this.height = height;
            this.htmlAttributions = htmlAttributions;
            this.photoReference = photoReference;
            this.width = width;
        }

        @Nullable
        public Integer getHeight() {
            return height;
        }

        @Nullable
        public List<String> getHtmlAttributions() {
            return htmlAttributions;
        }

        @Nullable
        public String getPhotoReference() {
            return photoReference;
        }

        @Nullable
        public Integer getWidth() {
            return width;
        }
    }

    public static class Result {

        @Nullable
        @SerializedName("business_status")
        @Expose
        private final String businessStatus;
        @Nullable
        @SerializedName("formatted_address")
        @Expose
        private final String formattedAddress;
        @Nullable
        @SerializedName("international_phone_number")
        @Expose
        private final String internationalPhoneNumber;
        @Nullable
        @SerializedName("geometry")
        @Expose
        private final Geometry geometry;
        @Nullable
        @SerializedName("name")
        @Expose
        private final String name;
        @Nullable
        @SerializedName("obfuscated_type")
        @Expose
        private final List<Object> obfuscatedType;
        @Nullable
        @SerializedName("opening_hours")
        @Expose
        private final OpeningHours openingHours;
        @Nullable
        @SerializedName("photos")
        @Expose
        private final List<Photo> photos;
        @Nullable
        @SerializedName("place_id")
        @Expose
        private final String placeId;
        @Nullable
        @SerializedName("rating")
        @Expose
        private final Double rating;
        @Nullable
        @SerializedName("website")
        @Expose
        private final String website;

        public Result(
            @Nullable String businessStatus,
            @Nullable String formattedAddress,
            @Nullable String internationalPhoneNumber,
            @Nullable Geometry geometry,
            @Nullable String name,
            @Nullable List<Object> obfuscatedType,
            @Nullable OpeningHours openingHours,
            @Nullable List<Photo> photos,
            @Nullable String placeId,
            @Nullable Double rating,
            @Nullable String website
        ) {
            this.businessStatus = businessStatus;
            this.formattedAddress = formattedAddress;
            this.internationalPhoneNumber = internationalPhoneNumber;
            this.geometry = geometry;
            this.name = name;
            this.obfuscatedType = obfuscatedType;
            this.openingHours = openingHours;
            this.photos = photos;
            this.placeId = placeId;
            this.rating = rating;
            this.website = website;
        }

        @Nullable
        public String getBusinessStatus() {
            return businessStatus;
        }

        @Nullable
        public String getFormattedAddress() {
            return formattedAddress;
        }

        @Nullable
        public String getInternationalPhoneNumber() {
            return internationalPhoneNumber;
        }

        @Nullable
        public Geometry getGeometry() {
            return geometry;
        }

        @Nullable
        public String getName() {
            return name;
        }

        @Nullable
        public List<Object> getObfuscatedType() {
            return obfuscatedType;
        }

        @Nullable
        public OpeningHours getOpeningHours() {
            return openingHours;
        }

        @Nullable
        public List<Photo> getPhotos() {
            return photos;
        }

        @Nullable
        public String getPlaceId() {
            return placeId;
        }

        @Nullable
        public Double getRating() {
            return rating;
        }

        @Nullable
        public String getWebsite() {
            return website;
        }
    }
}