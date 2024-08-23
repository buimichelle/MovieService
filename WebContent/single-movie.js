/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    let movieNameElement = jQuery("#movie_name");
    movieNameElement.append("<h1 style= 'text-align: center'>" + resultData[0]["movie_title"] + "</h1>");
    console.log("handleResult: populating movie table from resultData");

    let resultInfo = jQuery("#results");

    if (resultData[0]["previousLink"] == null) {
        resultInfo.append("<a href='home.html'>Take Me Back To Results</a>");
    }
    else {
        resultInfo.append("<a href='" + resultData[0]["previousLink"] + "'>Take Me Back To Results</a>");
    }



    // Populate the star table
    let movieTableBodyElement = jQuery("#movie_table_body");
    let rowHTML = ""
    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["movie_year"] + "</th>";
    rowHTML += "<th>" + resultData[0]["movie_rating"] + "</th>";
    let splitgenres = resultData[0]["movie_genres"];
    if (splitgenres==null) {
        return;
    }
    splitgenres = splitgenres.split(',');
    const splitgenresID = resultData[0]["genre_ID"].split(',');
    let genresLinks = "<th>";
    if (splitgenres[0] != "") {
        for (let i = 0; i < splitgenres.length; i++) {
            genresLinks += '<a href="single-genre-movie.html?id=' + splitgenresID[i] + '&page=1&sort=titleratingup&limit=10">' + splitgenres[i] + '</a>';
            genresLinks += "<br>";
        }
        genresLinks += "</th>";
        rowHTML += genresLinks;
    }
    else {
        genresLinks += "NULL</th>";
        rowHTML += genresLinks;
    }

    const splitStars = resultData[0]["star_names"].split(', ');
    const splitStarsID = resultData[0]["star_ids"].split(', ');
    let starLinks = "<th>";
    if (splitStars[0] != "") {
        for (let i = 0; i < splitStars.length; i++) {
            starLinks += '<a href="single-star.html?id=' + splitStarsID[i].replace(/,/g, '') + '">' + splitStars[i] + '</a>';
            starLinks += "<br>";
        }
        starLinks += "</th>";
        rowHTML += starLinks;
    }
    else {
        starLinks += "NULL</th>";
        rowHTML += starLinks;
    }
    rowHTML += "<th>" +
        '<form ACTION="#" id="cart" METHOD="post">' +
            '<input type = "hidden" name="item" value = "' + resultData[0]["movie_title"] + '">' +
            '<input type = "hidden" name="item" value = "20">' +
        '<button class = "Addbuttons" type="submit" onclick="purchaseMovie(\'' + resultData[0]["movie_title"] + '\', event)">Add</button>' +
        '</form>'
        + "</th>";
    rowHTML += "</tr>";

    movieTableBodyElement.append(rowHTML);
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
    var formData = $('#cart').serialize();
    event.preventDefault();
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
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "moviedb/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});