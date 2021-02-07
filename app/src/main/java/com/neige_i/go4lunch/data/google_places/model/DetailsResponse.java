package com.neige_i.go4lunch.data.google_places.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailsResponse extends PlacesResponse {

    @SerializedName("html_attributions")
    @Expose
    private final List<Object> htmlAttributions;
    @SerializedName("result")
    @Expose
    private final Result result;
    @SerializedName("status")
    @Expose
    private final String status;

    public DetailsResponse(List<Object> htmlAttributions, Result result, String status) {
        this.htmlAttributions = htmlAttributions;
        this.result = result;
        this.status = status;
    }

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public Result getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }

    public static class Close {

        @SerializedName("day")
        @Expose
        private final Integer day;
        @SerializedName("time")
        @Expose
        private final String time;

        public Close(Integer day, String time) {
            this.day = day;
            this.time = time;
        }

        public Integer getDay() {
            return day;
        }

        public String getTime() {
            return time;
        }
    }

    public static class Geometry {

        @SerializedName("location")
        @Expose
        private final Location location;

        public Geometry(Location location) {
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }
    }

    public static class Location {

        @SerializedName("lat")
        @Expose
        private final Double lat;
        @SerializedName("lng")
        @Expose
        private final Double lng;

        public Location(Double lat, Double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public Double getLat() {
            return lat;
        }

        public Double getLng() {
            return lng;
        }
    }

    public static class Open {

        @SerializedName("day")
        @Expose
        private final Integer day;
        @SerializedName("time")
        @Expose
        private final String time;

        public Open(Integer day, String time) {
            this.day = day;
            this.time = time;
        }

        public Integer getDay() {
            return day;
        }

        public String getTime() {
            return time;
        }
    }

    public static class OpeningHours {

        @SerializedName("open_now")
        @Expose
        private final Boolean openNow;
        @SerializedName("periods")
        @Expose
        private final List<Period> periods;
        @SerializedName("weekday_text")
        @Expose
        private final List<String> weekdayText;

        public OpeningHours(Boolean openNow, List<Period> periods, List<String> weekdayText) {
            this.openNow = openNow;
            this.periods = periods;
            this.weekdayText = weekdayText;
        }

        public Boolean getOpenNow() {
            return openNow;
        }

        public List<Period> getPeriods() {
            return periods;
        }

        public List<String> getWeekdayText() {
            return weekdayText;
        }
    }

    public static class Period {

        @SerializedName("close")
        @Expose
        private final Close close;
        @SerializedName("open")
        @Expose
        private final Open open;

        public Period(Close close, Open open) {
            this.close = close;
            this.open = open;
        }

        public Close getClose() {
            return close;
        }

        public Open getOpen() {
            return open;
        }
    }

    public static class Photo {

        @SerializedName("height")
        @Expose
        private final Integer height;
        @SerializedName("html_attributions")
        @Expose
        private final List<String> htmlAttributions;
        @SerializedName("photo_reference")
        @Expose
        private final String photoReference;
        @SerializedName("width")
        @Expose
        private final Integer width;

        public Photo(Integer height, List<String> htmlAttributions, String photoReference, Integer width) {
            this.height = height;
            this.htmlAttributions = htmlAttributions;
            this.photoReference = photoReference;
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public List<String> getHtmlAttributions() {
            return htmlAttributions;
        }

        public String getPhotoReference() {
            return photoReference;
        }

        public Integer getWidth() {
            return width;
        }
    }

    public static class Result {

        @SerializedName("formatted_address")
        @Expose
        private final String formattedAddress;
        @SerializedName("formatted_phone_number")
        @Expose
        private final String formattedPhoneNumber;
        @SerializedName("geometry")
        @Expose
        private final Geometry geometry;
        @SerializedName("name")
        @Expose
        private final String name;
        @SerializedName("obfuscated_type")
        @Expose
        private final List<Object> obfuscatedType;
        @SerializedName("opening_hours")
        @Expose
        private final OpeningHours openingHours;
        @SerializedName("photos")
        @Expose
        private final List<Photo> photos;
        @SerializedName("place_id")
        @Expose
        private final String placeId;
        @SerializedName("rating")
        @Expose
        private final Double rating;
        @SerializedName("website")
        @Expose
        private final String website;

        public Result(String formattedAddress, String formattedPhoneNumber, Geometry geometry, String name, List<Object> obfuscatedType, OpeningHours openingHours, List<Photo> photos, String placeId, Double rating, String website) {
            this.formattedAddress = formattedAddress;
            this.formattedPhoneNumber = formattedPhoneNumber;
            this.geometry = geometry;
            this.name = name;
            this.obfuscatedType = obfuscatedType;
            this.openingHours = openingHours;
            this.photos = photos;
            this.placeId = placeId;
            this.rating = rating;
            this.website = website;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public String getFormattedPhoneNumber() {
            return formattedPhoneNumber;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public String getName() {
            return name;
        }

        public List<Object> getObfuscatedType() {
            return obfuscatedType;
        }

        public OpeningHours getOpeningHours() {
            return openingHours;
        }

        public List<Photo> getPhotos() {
            return photos;
        }

        public String getPlaceId() {
            return placeId;
        }

        public Double getRating() {
            return rating;
        }

        public String getWebsite() {
            return website;
        }
    }
}