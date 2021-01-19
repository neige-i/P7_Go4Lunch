package com.neige_i.go4lunch.view.home;

public class HomeUiModel {

    private final String fragmentToShow;
    private final String fragmentToHide;
    private final int titleId;

    public HomeUiModel(String fragmentToShow, String fragmentToHide, int titleId) {
        this.fragmentToShow = fragmentToShow;
        this.fragmentToHide = fragmentToHide;
        this.titleId = titleId;
    }

    public String getFragmentToShow() {
        return fragmentToShow;
    }

    public String getFragmentToHide() {
        return fragmentToHide;
    }

    public int getTitleId() {
        return titleId;
    }
}
