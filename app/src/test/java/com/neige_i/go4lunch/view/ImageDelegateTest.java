package com.neige_i.go4lunch.view;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.view.View;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;

public class ImageDelegateTest {

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final ImageDelegate imageDelegate = new ImageDelegate();

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final ImageView star1Mock = mock(ImageView.class);
    private final ImageView star2Mock = mock(ImageView.class);
    private final ImageView star3Mock = mock(ImageView.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void showNoStars_when_set0Star() {
        // WHEN
        imageDelegate.setStarVisibility(0, star1Mock, star2Mock, star3Mock);

        // THEN
        verify(star1Mock).setVisibility(View.GONE);
        verify(star2Mock).setVisibility(View.GONE);
        verify(star3Mock).setVisibility(View.GONE);
    }

    @Test
    public void showStar1Only_when_set1Star() {
        // WHEN
        imageDelegate.setStarVisibility(1, star1Mock, star2Mock, star3Mock);

        // THEN
        verify(star1Mock).setVisibility(View.VISIBLE);
        verify(star2Mock).setVisibility(View.GONE);
        verify(star3Mock).setVisibility(View.GONE);
    }

    @Test
    public void showStar1And2_when_set2Stars() {
        // WHEN
        imageDelegate.setStarVisibility(2, star1Mock, star2Mock, star3Mock);

        // THEN
        verify(star1Mock).setVisibility(View.VISIBLE);
        verify(star2Mock).setVisibility(View.VISIBLE);
        verify(star3Mock).setVisibility(View.GONE);
    }

    @Test
    public void showAllStars_when_set3Stars() {
        // WHEN
        imageDelegate.setStarVisibility(3, star1Mock, star2Mock, star3Mock);

        // THEN
        verify(star1Mock).setVisibility(View.VISIBLE);
        verify(star2Mock).setVisibility(View.VISIBLE);
        verify(star3Mock).setVisibility(View.VISIBLE);
    }
}