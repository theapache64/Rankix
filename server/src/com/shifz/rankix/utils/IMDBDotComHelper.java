package com.shifz.rankix.utils;

import com.shifz.rankix.models.Movie;

/**
 * Created by shifar on 15/12/15.
 */
public final class IMDBDotComHelper {

    private static final String MOVIE_NAME_EXP1_REGEX = "<h1 itemprop=\"name\" class=\"\">";
    private static final String MOVIE_NAME_EXP2_REGEX = "&nbsp;<";
    private static final String MOVIE_GENDER_EXP1_REGEX = "<\\/time>";
    private static final String MOVIE_GENDER_EXP2_REGEX = "<span class=\"ghost\">\\|<\\/span>";
    private static final String HTML_REMOVE_REGEX = "<[^>]*>";
    private static final String MOVIE_RATING_EXP1_REGEX = "<span itemprop=\"ratingValue\">";
    private static final String MOVIE_RATING_EXP2_REGEX = "</span></strong>";
    private static final String MOVIE_PLOT_EXP1_REGEX = "<div class=\"summary_text\" itemprop=\"description\">";
    private static final String MOVE_PLOT_EXP2_REGEX = "<";
    private static final String MOVIE_POSTER_EXP1_REGEX = "itemprop=\"image\"";
    private static final String MOVIE_POSTER_EXP2_REGEX = "src=\"";
    private final String imdbId, imdbHtml;
    private Movie movie;
    private String rating;

    public IMDBDotComHelper(String imdbId, String imdbHtml) {
        this.imdbId = imdbId;
        this.imdbHtml = imdbHtml;
    }

    private String getMovieName() {
        final String[] exp1 = this.imdbHtml.split(MOVIE_NAME_EXP1_REGEX);
        final String movieName = exp1[1].split(MOVIE_NAME_EXP2_REGEX)[0];
        System.out.println("Returning Movie name : " + movieName);
        return movieName;
    }

    public String getRating() {
        if (this.rating == null) {
            final String[] exp1 = this.imdbHtml.split(MOVIE_RATING_EXP1_REGEX);
            if (exp1.length >= 2) {
                this.rating = exp1[1].split(MOVIE_RATING_EXP2_REGEX)[0];
            }
        }
        return this.rating;
    }

    private static void debug(String[] exp1) {
        System.out.println("------------------");
        System.out.println("Length: " + exp1.length);
        for (final String exp : exp1) {
            System.out.println(exp);
        }
        System.out.println("------------------");
    }

    private String getPlot() {
        final String[] exp1 = this.imdbHtml.split(MOVIE_PLOT_EXP1_REGEX);
        final String descr = exp1[1];
        final int firstLessThanPos = descr.indexOf('<');
        if (firstLessThanPos != -1) {
            return descr.substring(0, firstLessThanPos);
        }
        return null;
    }

    private String getPosterUrl(final String movieName) {
        final String posterRegEx = String.format("<img alt=\"%s Poster", movieName);
        System.out.println(posterRegEx);
        final String[] exp1 = this.imdbHtml.split(posterRegEx);
        if (exp1.length >= 2) {
            final String exp2 = exp1[1].split(MOVIE_POSTER_EXP1_REGEX)[0];
            String imageUrl = exp2.split(MOVIE_POSTER_EXP2_REGEX)[1];
            System.out.println("ImageUrl: " + imageUrl);
            return imageUrl.substring(0, imageUrl.length() - 1);
        }
        return null;
    }

    private String getGender() {
        final String[] exp1 = this.imdbHtml.split(MOVIE_GENDER_EXP1_REGEX);
        if (exp1.length >= 2) {
            final String[] exp2 = exp1[1].split(MOVIE_GENDER_EXP2_REGEX);
            return exp2[1].replaceAll(HTML_REMOVE_REGEX, "");
        }
        return null;
    }

    public Movie getMovie() {

        if (movie == null) {
            //Parsing MovieName
            final String movieName = getMovieName();

            System.out.println("MovieName : " + movieName);

            //Parsing gender
            final String gender = getGender();

            System.out.println("Gender : " + gender);

            //Parsing Gender , getRating() is public so there's a chance to  get called before getMovie().
            final String rating = this.rating == null ? getRating() : this.rating;

            System.out.println("Rating : " + rating);

            //Parsing Plot
            final String plot = getPlot();

            System.out.println("Plot : " + plot);

            //Parsing PosterUrl
            final String posterUrl = getPosterUrl(movieName);

            System.out.println("PosterUrl : " + posterUrl);

            movie = new Movie(movieName, this.imdbId, rating, gender, plot, posterUrl);

            System.out.println("Movie: " + movie);
        }

        return movie;
    }
}
