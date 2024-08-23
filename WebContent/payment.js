let makeSale = $("#makeSale");

function handleresult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle payment info");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);
    let total = resultDataJson["previousItems"].length * 20;
    $("#total").text("Cart Total: $" + total);
}


function handlePaymentResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle payment info");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    //console.log(resultDataJson["previousItems"].length);
    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#error_message").text(resultDataJson["message"]);
    }
}
function submitPaymentInfo(payment) {
    console.log("make sale");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    payment.preventDefault();
    $.ajax(
        "moviedb/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: makeSale.serialize(),
            success: handlePaymentResult
        }
    );
}

$.ajax("moviedb/items", {
    method: "GET",
    success: handleresult
});


// Bind the submit action of the form to a event handler function
makeSale.submit(submitPaymentInfo);