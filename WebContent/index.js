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
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display movie_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";

        const splitgenres = resultData[i]["movie_genres"].split(',');
        let genresLinks = "<th>";
        for (let i = 0; i < splitgenres.length; i++) {
            genresLinks += splitgenres[i];
            genresLinks += "<br>";
        }
        genresLinks += "</th>";
        rowHTML += genresLinks;

        //splitting up star id's and star names to parse through
        const splitnames = resultData[i]["movie_stars"].split(',');
        const splitIds = resultData[i]["movie_starIds"].split(',');

        //add link to single-star.html with id passsed with GET url parameter
        let starLinks = "<th>";
        starLinks += '<a href="single-star.html?id=' + splitIds[0] + '">' + splitnames[0] + '</a>';
        starLinks += "<br>";
        starLinks += '<a href="single-star.html?id=' + splitIds[1] + '">' + splitnames[1] + '</a>';
        starLinks += "<br>";
        starLinks += '<a href="single-star.html?id=' + splitIds[2] + '">' + splitnames[2] + '</a>';
        starLinks += "<br>";
        starLinks += "</th>";
        rowHTML += starLinks;
        // rowHTML += "<th>" +
        //     '<form ACTION="#" id="' + i + '" METHOD="post">' +
        //     '<input type = "hidden" name="item" value = "' + resultData[i]["movie_title"] + '">' +
        //     '<input type = "hidden" name="item" value = "24">' +
        //     '<button class="Addbuttons" type="submit" onclick="purchaseMovie(' + i + ', event)">Add</button>'
        //     + '</form>'
        //     + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

function handleCartArray(resultArray) {
    console.log(resultArray);
    let item_list = $("#item_list");
    // change it to html list
    let res = "<ul>";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        res += "<li>" + resultArray[i] + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);
}

function purchaseMovie(num, event) {
    console.log("submitting to cart");

    let rowId = "#" + num;
    var formData = $(rowId).serialize();
    event.preventDefault();
    console.log(rowId);
    $.ajax("moviedb/items", {
        method: "POST",
        data: formData,
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
            window.alert("Added to cart");
        },
        error: (xhr, status, error) => {
            console.error("Error:", error);
            window.alert("Failed to add");
        }
    });
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "moviedb/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});