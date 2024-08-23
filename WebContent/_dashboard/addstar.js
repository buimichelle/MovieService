let star_form = $("#star_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleResult(resultDataString) {
    console.log("added the movie");
    $("#message").text(resultDataString[0]["message"]);
    star_form[0].reset();
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitForm(formSubmitEvent) {
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    $.ajax(
        "../api/_dashboard/addstar", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: star_form.serialize(),
            success: handleResult,
            error: (xhr, status, error) => {
                console.error("Error:", error);
            }
        }
    );
}

// Bind the submit action of the form to a handler function
star_form.submit(submitForm);

