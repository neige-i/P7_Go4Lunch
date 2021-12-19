

package com.neige_i.go4lunch.repository.google_places.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RawAutocompleteResponse {

    @Nullable
    @SerializedName("predictions")
    @Expose
    private final List<Prediction> predictions;
    @Nullable
    @SerializedName("status")
    @Expose
    private final String status;

    public RawAutocompleteResponse(
        @Nullable List<Prediction> predictions,
        @Nullable String status
    ) {
        this.predictions = predictions;
        this.status = status;
    }

    @Nullable
    public List<Prediction> getPredictions() {
        return predictions;
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public static class Prediction {

        @Nullable
        @SerializedName("description")
        @Expose
        private final String description;
        @Nullable
        @SerializedName("matched_substrings")
        @Expose
        private final List<MatchedSubstring> matchedSubstrings;
        @Nullable
        @SerializedName("place_id")
        @Expose
        private final String placeId;
        @Nullable
        @SerializedName("reference")
        @Expose
        private final String reference;
        @Nullable
        @SerializedName("structured_formatting")
        @Expose
        private final StructuredFormatting structuredFormatting;
        @Nullable
        @SerializedName("terms")
        @Expose
        private final List<Term> terms;
        @Nullable
        @SerializedName("types")
        @Expose
        private final List<String> types;

        public Prediction(
            @Nullable String description,
            @Nullable List<MatchedSubstring> matchedSubstrings,
            @Nullable String placeId,
            @Nullable String reference,
            @Nullable StructuredFormatting structuredFormatting,
            @Nullable List<Term> terms,
            @Nullable List<String> types
        ) {
            this.description = description;
            this.matchedSubstrings = matchedSubstrings;
            this.placeId = placeId;
            this.reference = reference;
            this.structuredFormatting = structuredFormatting;
            this.terms = terms;
            this.types = types;
        }

        @Nullable
        public String getDescription() {
            return description;
        }

        @Nullable
        public List<MatchedSubstring> getMatchedSubstrings() {
            return matchedSubstrings;
        }

        @Nullable
        public String getPlaceId() {
            return placeId;
        }

        @Nullable
        public String getReference() {
            return reference;
        }

        @Nullable
        public StructuredFormatting getStructuredFormatting() {
            return structuredFormatting;
        }

        @Nullable
        public List<Term> getTerms() {
            return terms;
        }

        @Nullable
        public List<String> getTypes() {
            return types;
        }
    }

    public static class MatchedSubstring {

        @Nullable
        @SerializedName("length")
        @Expose
        private final Integer length;
        @Nullable
        @SerializedName("offset")
        @Expose
        private final Integer offset;

        public MatchedSubstring(@Nullable Integer length, @Nullable Integer offset) {
            this.length = length;
            this.offset = offset;
        }

        @Nullable
        public Integer getLength() {
            return length;
        }

        @Nullable
        public Integer getOffset() {
            return offset;
        }
    }

    public static class StructuredFormatting {

        @Nullable
        @SerializedName("main_text")
        @Expose
        private final String mainText;
        @Nullable
        @SerializedName("main_text_matched_substrings")
        @Expose
        private final List<MainTextMatchedSubstring> mainTextMatchedSubstrings;
        @Nullable
        @SerializedName("secondary_text")
        @Expose
        private final String secondaryText;

        public StructuredFormatting(
            @Nullable String mainText,
            @Nullable List<MainTextMatchedSubstring> mainTextMatchedSubstrings,
            @Nullable String secondaryText
        ) {
            this.mainText = mainText;
            this.mainTextMatchedSubstrings = mainTextMatchedSubstrings;
            this.secondaryText = secondaryText;
        }

        @Nullable
        public String getMainText() {
            return mainText;
        }

        @Nullable
        public List<MainTextMatchedSubstring> getMainTextMatchedSubstrings() {
            return mainTextMatchedSubstrings;
        }

        @Nullable
        public String getSecondaryText() {
            return secondaryText;
        }
    }

    public static class MainTextMatchedSubstring {

        @Nullable
        @SerializedName("length")
        @Expose
        private final Integer length;
        @Nullable
        @SerializedName("offset")
        @Expose
        private final Integer offset;

        public MainTextMatchedSubstring(@Nullable Integer length, @Nullable Integer offset) {
            this.length = length;
            this.offset = offset;
        }

        @Nullable
        public Integer getLength() {
            return length;
        }

        @Nullable
        public Integer getOffset() {
            return offset;
        }
    }

    public static class Term {

        @Nullable
        @SerializedName("offset")
        @Expose
        private final Integer offset;
        @Nullable
        @SerializedName("value")
        @Expose
        private final String value;

        public Term(@Nullable Integer offset, @Nullable String value) {
            this.offset = offset;
            this.value = value;
        }

        @Nullable
        public Integer getOffset() {
            return offset;
        }

        @Nullable
        public String getValue() {
            return value;
        }
    }
}
