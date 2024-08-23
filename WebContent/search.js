

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
function getParameterByName() {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    let parameter = url.split('?');
    if (parameter.length == 2) return parameter[1];
    return "";
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
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";

        const splitgenres = resultData[i]["movie_genres"].split(',');
        const splitgenresID = resultData[i]["genre_Id"].split(',');

        let genresLinks = "<th>";
        if (splitgenres[0]!="") {
            for (let index = 0; index < splitgenres.length; index++) {
                genresLinks += '<a href="single-genre-movie.html?id=' + splitgenresID[index] + '&page=1&sort=titleratingup&limit=10">' + splitgenres[index] + '</a>';

                genresLinks += "<br>";
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
            '<input type = "hidden" name="item" value = "24">' +
            '<button class="Addbuttons" type="submit" onclick="purchaseMovie(' + i + ', event)">Add</button>'
            + '</form>'
            + "</th>";

        // Append the row created to the table body, which will refresh the pag
        movieTableBodyElement.append(rowHTML);
        console.log(i);
    }
}
/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function getSessionAttribute(target, resultData) {
    const selected = resultData[0]["S" + target];
    if (selected == null || selected == '') {
        return '';
    }
    return selected;
}

function handleResult(resultData) {


    if (resultData[0]["Squery"] == null) {
        if (getParamInfo("limit") == null) {
            let newURL = window.location.href + "?";
            newURL += "movie=" + getSessionAttribute("movie", resultData);
            newURL += "&year=" + getSessionAttribute("year", resultData);
            newURL += "&director=" + getSessionAttribute("director", resultData);
            newURL += "&name=" + getSessionAttribute("name", resultData);
            newURL += "&page=" + getSessionAttribute("page", resultData);
            newURL += "&sort=" + getSessionAttribute("sort", resultData);
            newURL += "&limit=" + getSessionAttribute("limit", resultData);
            console.log("here in no query " + resultData[0]["Squery"]);
            window.location.href = newURL;
        }
    } else {
        console.log("HEY!");

        if (getParamInfo("query") == null) {
            let newURL = window.location.href + "?";
            newURL += "query=" + getSessionAttribute("query", resultData);
            newURL += "&page=" + getSessionAttribute("page", resultData);
            newURL += "&sort=" + getSessionAttribute("sort", resultData);
            newURL += "&limit=" + getSessionAttribute("limit", resultData);
            console.log("here in yes query " + resultData[0]["Squery"]);
            console.log(newURL);
            window.location.href = newURL;
        } else if (resultData[0]["message"] == "empty") {
            let limitElement = jQuery("#SHOW_LIMIT");
            limitElement.append("<h4>No Movies Avaliable</h4>");
            return;
        }
    }


    let currentURL = window.location.href;
    let currentPage = parseInt(getPageNumber());
    if (currentPage == null) {
        currentPage = resultData[0]["Spage"];
    }
    const previousButton = document.getElementById("previous");
    const nextButton = document.getElementById("next");
    const selectElement = document.getElementById("limit-by");
    const previousSelect = selectElement.value;
    const selectButton = document.getElementById("limit-button");
    const sortElement = document.getElementById("sort-by");
    const sortButton = document.getElementById("sort-button");
    const previousSortSelect = sortElement.value;


    let limit = getParamInfo("limit");
    if (limit == null) {
        limit = resultData[0]["Slimit"];
    }
    console.log("this is the limit: " + limit);
    selectElement.value = limit;
    selectElement.querySelector('option[value="' + previousSelect + '"]').selected = false;
    selectElement.querySelector('option[value="' + limit + '"]').selected = true;

    let sort = getParamInfo("sort");
    if (sort == null) {
        console.log(resultData[0]["Ssort"])
        sort = resultData[0]["Ssort"];
    }
    console.log("this is the sort: " + sort);
    sortElement.value = limit;
    sortElement.querySelector('option[value="' + previousSortSelect + '"]').selected = false;
    sortElement.querySelector('option[value="' + sort + '"]').selected = true;


    let limitElement = jQuery("#SHOW_LIMIT");
    limitElement.append("<h4>Showing " + resultData.length + " Movies Each Page!</h4>");
    if (currentPage > 1) {
        previousButton.href = currentURL.replace(/(\?|&)page=\d+/, '&page=' + String(currentPage - 1));
    }
    if (resultData.length == limit) {
        nextButton.href = currentURL.replace(/(\?|&)page=\d+/, '&page=' + String(currentPage + 1));
    }
    createTable(resultData);


    selectButton.addEventListener("click", function () {
        let url = currentURL.replace(/(\?|&)limit=\d+/, '')
        let newLink = url + '&limit=' + selectElement.value;
        console.log(newLink);
        window.location.href = newLink;
    });

    sortButton.addEventListener("click", function () {
        let currentURL = window.location.href;
        let url = currentURL.replace(/([?&])sort=[^&]*/, "&sort=" + sortElement.value)
        console.log("current value: " + sortElement.value);
        console.log("the url: " + url);
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
let parameter = getParameterByName();
let myUrl = "moviedb/search";

if (parameter != "") {
   myUrl += "?" + parameter;
}

console.log(myUrl + parameter);// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: myUrl,
    success: (resultData) => {
        handleResult(resultData);
    },
    error: (xhr, status, error) => {
        console.error("Error:", error);
    }
})


