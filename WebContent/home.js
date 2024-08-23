/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function showGenres(resultData) {
    let genreList = jQuery("#genres_list");
    for (let i = 0; i < resultData.length; i++)
    {
        let rowHTML = '<a href="single-genre-movie.html?id=' +  resultData[i]["genre_ID"] + '&page=1&sort=titleratingup&limit=10">' + resultData[i]["genre_name"] + '</a> <br>';
        genreList.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "moviedb/home", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => showGenres(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});