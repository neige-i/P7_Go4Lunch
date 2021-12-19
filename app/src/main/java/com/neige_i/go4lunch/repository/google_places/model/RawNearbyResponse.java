package com.neige_i.go4lunch.repository.google_places.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RawNearbyResponse {

    @Nullable
    @SerializedName("html_attributions")
    @Expose
    private final List<Object> htmlAttributions;
    @Nullable
    @SerializedName("next_page_token")
    @Expose
    private final String nextPageToken;
    @Nullable
    @SerializedName("results")
    @Expose
    private final List<Result> results;
    @Nullable
    @SerializedName("status")
    @Expose
    private final String status;

    public RawNearbyResponse(
        @Nullable List<Object> htmlAttributions,
        @Nullable String nextPageToken,
        @Nullable List<Result> results,
        @Nullable String status
    ) {
        this.htmlAttributions = htmlAttributions;
        this.nextPageToken = nextPageToken;
        this.results = results;
        this.status = status;
    }

    @Nullable
    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    @Nullable
    public String getNextPageToken() {
        return nextPageToken;
    }

    @Nullable
    public List<Result> getResults() {
        return results;
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public static class Result {

        @Nullable
        @SerializedName("business_status")
        @Expose
        private final String businessStatus;
        @Nullable
        @SerializedName("geometry")
        @Expose
        private final Geometry geometry;
        @Nullable
        @SerializedName("icon")
        @Expose
        private final String icon;
        @Nullable
        @SerializedName("name")
        @Expose
        private final String name;
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
        @SerializedName("plus_code")
        @Expose
        private final PlusCode plusCode;
        @Nullable
        @SerializedName("rating")
        @Expose
        private final Double rating;
        @Nullable
        @SerializedName("reference")
        @Expose
        private final String reference;
        @Nullable
        @SerializedName("scope")
        @Expose
        private final String scope;
        @Nullable
        @SerializedName("types")
        @Expose
        private final List<String> types;
        @Nullable
        @SerializedName("user_ratings_total")
        @Expose
        private final Integer userRatingsTotal;
        @Nullable
        @SerializedName("vicinity")
        @Expose
        private final String vicinity;
        @Nullable
        @SerializedName("price_level")
        @Expose
        private final Integer priceLevel;
        @Nullable
        @SerializedName("permanently_closed")
        @Expose
        private final Boolean permanentlyClosed;

        public Result(
            @Nullable String businessStatus,
            @Nullable Geometry geometry,
            @Nullable String icon,
            @Nullable String name,
            @Nullable OpeningHours openingHours,
            @Nullable List<Photo> photos,
            @Nullable String placeId,
            @Nullable PlusCode plusCode,
            @Nullable Double rating,
            @Nullable String reference,
            @Nullable String scope,
            @Nullable List<String> types,
            @Nullable Integer userRatingsTotal,
            @Nullable String vicinity,
            @Nullable Integer priceLevel,
            @Nullable Boolean permanentlyClosed
        ) {
            this.businessStatus = businessStatus;
            this.geometry = geometry;
            this.icon = icon;
            this.name = name;
            this.openingHours = openingHours;
            this.photos = photos;
            this.placeId = placeId;
            this.plusCode = plusCode;
            this.rating = rating;
            this.reference = reference;
            this.scope = scope;
            this.types = types;
            this.userRatingsTotal = userRatingsTotal;
            this.vicinity = vicinity;
            this.priceLevel = priceLevel;
            this.permanentlyClosed = permanentlyClosed;
        }

        @Nullable
        public String getBusinessStatus() {
            return businessStatus;
        }

        @Nullable
        public Geometry getGeometry() {
            return geometry;
        }

        @Nullable
        public String getIcon() {
            return icon;
        }

        @Nullable
        public String getName() {
            return name;
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
        public PlusCode getPlusCode() {
            return plusCode;
        }

        @Nullable
        public Double getRating() {
            return rating;
        }

        @Nullable
        public String getReference() {
            return reference;
        }

        @Nullable
        public String getScope() {
            return scope;
        }

        @Nullable
        public List<String> getTypes() {
            return types;
        }

        @Nullable
        public Integer getUserRatingsTotal() {
            return userRatingsTotal;
        }

        @Nullable
        public String getVicinity() {
            return vicinity;
        }

        @Nullable
        public Integer getPriceLevel() {
            return priceLevel;
        }

        @Nullable
        public Boolean getPermanentlyClosed() {
            return permanentlyClosed;
        }
    }

    public static class Geometry {

        @Nullable
        @SerializedName("location")
        @Expose
        private final Location location;
        @Nullable
        @SerializedName("viewport")
        @Expose
        private final Viewport viewport;

        public Geometry(
            @Nullable Location location,
            @Nullable Viewport viewport
        ) {
            this.location = location;
            this.viewport = viewport;
        }

        @Nullable
        public Location getLocation() {
            return location;
        }

        @Nullable
        public Viewport getViewport() {
            return viewport;
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

    public static class Viewport {

        @Nullable
        @SerializedName("northeast")
        @Expose
        private final Northeast northeast;
        @Nullable
        @SerializedName("southwest")
        @Expose
        private final Southwest southwest;

        public Viewport(
            @Nullable Northeast northeast,
            @Nullable Southwest southwest
        ) {
            this.northeast = northeast;
            this.southwest = southwest;
        }

        @Nullable
        public Northeast getNortheast() {
            return northeast;
        }

        @Nullable
        public Southwest getSouthwest() {
            return southwest;
        }
    }

    public static class Northeast {

        @Nullable
        @SerializedName("lat")
        @Expose
        private final Double lat;
        @Nullable
        @SerializedName("lng")
        @Expose
        private final Double lng;

        public Northeast(@Nullable Double lat, @Nullable Double lng) {
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

    public static class Southwest {

        @Nullable
        @SerializedName("lat")
        @Expose
        private final Double lat;
        @Nullable
        @SerializedName("lng")
        @Expose
        private final Double lng;

        public Southwest(@Nullable Double lat, @Nullable Double lng) {
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

    public static class OpeningHours {

        @Nullable
        @SerializedName("open_now")
        @Expose
        private final Boolean openNow;

        public OpeningHours(@Nullable Boolean openNow) {
            this.openNow = openNow;
        }

        @Nullable
        public Boolean getOpenNow() {
            return openNow;
        }
    }

    public static class PlusCode {

        @Nullable
        @SerializedName("compound_code")
        @Expose
        private final String compoundCode;
        @Nullable
        @SerializedName("global_code")
        @Expose
        private final String globalCode;

        public PlusCode(@Nullable String compoundCode, @Nullable String globalCode) {
            this.compoundCode = compoundCode;
            this.globalCode = globalCode;
        }

        @Nullable
        public String getCompoundCode() {
            return compoundCode;
        }

        @Nullable
        public String getGlobalCode() {
            return globalCode;
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
}