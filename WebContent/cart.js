let cart = $("#cart");


/**
 * Handle the data returned by ItemsServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    const counter = {};

    resultArray.forEach(ele => {
        if (counter[ele]) {
            counter[ele] += 1;
        } else {
            counter[ele] = 1;
        }
    });
    let totalCost = 0;
    let displayTotal = jQuery("#display_total").empty();
    let item_list = jQuery("#item_list_body").empty();
    let index = 0;
    // change it to html list
    Object.keys(counter).forEach(function (key) {
        let rowHTML = ""
        rowHTML += "<tr>";
        rowHTML += "<th>" + key + "</th>";
        rowHTML += "<th>" + counter[key] + "</th>";
        rowHTML += "<th>$" + (20 * counter[key]) + "</th>";
        totalCost += (20 * counter[key]);
        rowHTML += "<th>" +
            '<form ACTION="#" id="' + "adding" + index + '" METHOD="post">' +
            '<input type = "hidden" name="item" value = "' + key + '">' +
            '<input type = "hidden" name="action" value = "add">' +
            '<button class="Addbuttons" type="submit" onclick="purchaseMovie(' + index + ', event)">Add One</button>'
            + '</form>'
            + "</th>";
        rowHTML += "<th>" +
            '<form ACTION="#" id="' + "dropping"  + index + '" METHOD="post">' +
            '<input type = "hidden" name="item" value = "' + key + '">' +
            '<input type = "hidden" name="action" value = "drop">' +
            '<button class="Addbuttons" type="submit" onclick="dropMovie(' + index + ', event)">Drop One</button>'
            + '</form>'
            + "</th>";
        rowHTML += "<th>" +
            '<form ACTION="#" id="' + "deleting" + index + '" METHOD="post">' +
            '<input type = "hidden" name="item" value = "' + key + '">' +
            '<input type = "hidden" name="action" value = "delete">' +
            '<button class="Addbuttons" type="submit" onclick="deleteMovie(' + index + ', event)">Delete All</button>'
            + '</form>'
            + "</th>";
        item_list.append(rowHTML);
        //displayTotal.append("<h1>$" + totalCost + "</h1>");
        console.log(rowHTML);
        index = index + 1;
    });
    displayTotal.append("<h1>Total Cost: $" + totalCost + "</h1>");
}

function toPayment() {
    console.log("clicked");
    window.location.href = 'payment.html';
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("moviedb/items", {
        method: "POST",
        data: cart.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });

    // clear input form
    cart[0].reset();
}

function purchaseMovie(num, event) {
    console.log("submitting to cart");

    let rowId = "#adding" + num;
    console.log("HI:" + rowId);
    event.preventDefault();
    var formData = $(rowId).serialize();
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

function deleteMovie(num, event) {
    console.log("deleting from cart");

    let rowId = "#deleting" + num;
    var formData = $(rowId).serialize();
    event.preventDefault();
    console.log(rowId);
    $.ajax("moviedb/items", {
        method: "POST",
        data: formData,
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
            window.alert("Deleted from cart");
        },
        error: (xhr, status, error) => {
            console.error("Error:", error);
            window.alert("Failed to delete");
        }
    });
}

function dropMovie(num, event) {
    console.log("deleting from cart");
    let rowId = "#dropping" + num;
    var formData = $(rowId).serialize();
    event.preventDefault();
    console.log(rowId);
    $.ajax("moviedb/items", {
        method: "POST",
        data: formData,
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
            window.alert("Drop from cart");
        },
        error: (xhr, status, error) => {
            console.error("Error:", error);
            window.alert("Failed to drop");
        }
    });
}

$.ajax("moviedb/items", {
    method: "GET",
    success: handleSessionData
});

// Bind the submit action of the form to a event handler function
document.getElementById('checkout').addEventListener('click', toPayment);
cart.submit(handleCartInfo);
