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
function getParamInfo(target) {
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

function getParam() {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    let parameter = url.split('?');
    if (parameter.length == 1) return null;
    if (parameter.length == 2) return parameter[1];
    return "";
}

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

function getPageNumber() {

    let target = "page";
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + "page" + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function createTable(resultData) {
    // Remove all rows from the table

    let movieTableBodyElement = jQuery("#movie_table_body");
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = ""
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display movie_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";

        const splitgenres = resultData[i]["movie_genres"].split(',');
        const splitgenresID = resultData[i]["genre_Id"].split(',');
        let genresLinks = "<th>";
        if (splitgenres[0] != "") {
            for (let index = 0; index < splitgenres.length; index++) {

                genresLinks += '<a href="single-genre-movie.html?id=' + splitgenresID[index] + '&page=1&sort=titleratingup&limit=10">' + splitgenres[index] + '</a>';

                genresLinks += "<br>";
                console.log('<a href="single-genre-movie.html?id=' + splitgenresID[index] + '&page=1&sort=titleratingup&limit=10">' + splitgenres[index])
            }

            genresLinks += "</th>";
            rowHTML += genresLinks;
        }
        else {

            genresLinks += "NULL</th>";
            rowHTML += genresLinks;
        }
        const splitStars = resultData[i]["movie_stars"].split(', ');
        const splitStarsID = resultData[i]["star_Id"].split(', ');
        console.log(splitStars);
        let starLinks = "<th>";
        if (splitStars[0]!="") {
            for (let index = 0; index < Math.min(3, splitStars.length); index++) {
                starLinks += '<a href="single-star.html?id=' + splitStarsID[index].replace(/,/g, '') + '">' + splitStars[index] + '</a>';
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
            '<form ACTION="#" id="' + i + '" METHOD="post">' +
            '<input type = "hidden" name="item" value = "' + resultData[i]["movie_title"] + '">' +
            '<input type = "hidden" name="item" value = "22">' +
            '<button class="Addbuttons" type="submit" onclick="purchaseMovie(' + i + ', event)">Add</button>'
            + '</form>'
            + "</th>";

        // Append the row created to the table body, which will refresh the pag
        movieTableBodyElement.append(rowHTML);
        console.log(i);
    }
}

function getSessionAttribute(target, resultData) {
    const selected = resultData[0]["R" + target];
    if (selected == null || selected == '') {
        return '';
    }
    return selected;
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    if (getParamInfo("limit") == null) {
        let newURL = window.location.href + "?";
        newURL += "id=" + getSessionAttribute("id", resultData);
        newURL += "&page=" + getSessionAttribute("page", resultData);
        newURL += "&sort=" + getSessionAttribute("sort", resultData);
        newURL += "&limit=" + getSessionAttribute("limit", resultData);
        window.location.href = newURL;
    }

    let currentURL = window.location.href;
    let currentPage = parseInt(getPageNumber());
    const previousButton = document.getElementById("previous");
    const nextButton = document.getElementById("next");
    const selectElement = document.getElementById("limit-by");
    const previousSelect = selectElement.value;
    const selectButton = document.getElementById("limit-button");
    const sortElement = document.getElementById("sort-by");
    const sortButton = document.getElementById("sort-button");
    const previousSortSelect = sortElement.value;


    let limit = getParameterByName("limit");
    console.log("this is the limit: " + limit);
    selectElement.value = limit;
    selectElement.querySelector( 'option[value="' + previousSelect + '"]').selected = false;
    selectElement.querySelector('option[value="' + limit + '"]').selected = true;

    let sort = getParameterByName("sort");
    console.log("this is the sort: " + sort);
    sortElement.value = limit;
    sortElement.querySelector( 'option[value="' + previousSortSelect + '"]').selected = false;
    sortElement.querySelector('option[value="' + sort + '"]').selected = true;


    let limitElement = jQuery("#SHOW_LIMIT");
    limitElement.append("<h4>Showing " + resultData.length + " Movies Each Page!</h4>");
    if (currentPage > 1) {
        previousButton.href = currentURL.replace(/(\?|&)page=\d+/, '&page=' + String(currentPage-1));
    }
    if (resultData.length == limit) {
        nextButton.href = currentURL.replace(/(\?|&)page=\d+/, '&page=' + String(currentPage + 1));
    }    createTable(resultData);


    selectButton.addEventListener("click", function() {
        let url = currentURL.replace(/(\?|&)limit=\d+/, '')
        let newLink = url + '&limit=' +  selectElement.value;
        window.location.href = newLink;
    });

    sortButton.addEventListener("click", function() {
        let currentURL = window.location.href;
        let url = currentURL.replace(/([?&])sort=[^&]*/, "&sort=" + sortElement.value)

        window.location.href = url;
    });

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
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let queryStr = getParam();
console.log(queryStr);


// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "moviedb/single-genre-movie?" + queryStr, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData), // Setting callback function to handle data returned successfully by the SingleStarServlet
    error: (xhr, status, error) => {
        console.error("Error:", error);
    }
});