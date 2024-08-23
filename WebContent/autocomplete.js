



/*
 * CS 122B Project 4. Autocomplete Example.
 *
 * This Javascript code uses this library: https://github.com/devbridge/jQuery-Autocomplete
 *
 * This example implements the basic features of the autocomplete search, features that are
 *   not implemented are mostly marked as "TODO" in the codebase as a suggestion of how to implement them.
 *
 * To read this code, start from the line "$('#autocomplete').autocomplete" and follow the callback functions.
 *
 */


var currentQuery = "";
var suggestionBool = false;
/*
 * This function is called by the library when it needs to lookup a query.
 *
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated for " + query)


    let sessionItem = sessionStorage.getItem(query)
    if (sessionItem != null) {
        console.log("autocomplete for " + query + "used by cache.");
        console.log("cache suggestions data: " + sessionItem);
        doneCallback({suggestions: JSON.parse(sessionItem)});
    }
    else {
        console.log("sending AJAX request to backend Java Servlet")
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "moviedb/autocomplete?query=" + escape(query) + "",
            "success": function (data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function (errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    }
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);

    sessionStorage.setItem(query, data);
    console.log("ajax suggestion data: " + data);
    doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion
    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieID"])
    $('#autocomplete').val(suggestion.value);
    suggestionBool = false;
    window.location.href = (window.location.origin + "/" + (window.location.pathname).split("/")[1] + "/" + "single-movie.html?id=" + suggestion["data"]["movieID"]);
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"

$('#autocomplete').autocomplete({
    lookup: function (query, doneCallback) {
        console.log('heyerw');
        currentQuery = query;
        handleLookup(query, doneCallback);
    },
    onSelect: function (suggestion) {
        suggestionBool = true;
        console.log('mich');
        handleSelectSuggestion(suggestion);
        return false;
    },
    deferRequestBy: 300,
    minChars: 3,
});


function handleNormalSearch(query) {

    var newquery = query;
    var page = $('#qpage').val();
    var sort = $('#qsort').val();
    var limit = $('#qlimit').val();


    var currentURL = window.location.href;
    var url = new URL(currentURL);
    var newURL = url.origin + "/" + (url.pathname).split("/")[1] + "/search.html";


    if (newquery === currentQuery || currentQuery == "") {
        newURL = newURL + "?";
        newURL += "query=" + newquery;
        newURL += "&page=" + page;
        newURL += "&sort=" + sort;
        newURL += "&limit=" +limit;
        console.log(newURL);
        window.location.href = newURL;
    }
    else {

        const storedDataString = JSON.parse(sessionStorage.getItem(currentQuery));
        var movieData = storedDataString.find(item => item.value === newquery);
        var movID = (movieData.data.movieID);
        window.location.href = (window.location.origin + "/" + (window.location.pathname).split("/")[1] + "/" + "single-movie.html?id=" + movID);
    }
}

// bind pressing enter key to a handler function


$('#autocomplete').keydown(function(event) {
    var keyCode = event.keyCode || event.which || event.key;

    console.log(keyCode);
    if (keyCode === 13) {
        if (!suggestionBool) {
            console.log("1");
            handleNormalSearch($('#autocomplete').val());
        }
        else {
            console.log("2");
            handleSelectSuggestion(($('#autocomplete').val()));
            suggestionBool = false;
        }
    }
})
$('#queryButton').click(function(event) {
    console.log("4");
    handleNormalSearch($('#autocomplete').val());
})