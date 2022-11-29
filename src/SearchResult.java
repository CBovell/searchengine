public interface SearchResult {
    /*
    Returns the title of the page this search result is for.
     */
    String getTitle();

    /*
    Returns the search score for the page this search result is for.
     */
    double getScore();
}

