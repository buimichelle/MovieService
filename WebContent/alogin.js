let login_formA = $("#login_formA");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLogResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log("this is the data: " + resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("home.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/loginandroid", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: login_formA.serialize(),
            success: handleLogResult
        }
    );
}

// Bind the submit action of the form to a handler function
login_formA.submit(submitLoginForm);

