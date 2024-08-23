function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    // show cart information
    $("#sale_id").text("SaleID: " + resultDataJson["saleId"]);
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
        totalCost += (20 * counter[key]);

        item_list.append(rowHTML);
        index = index + 1;
    });
    displayTotal.append("<h1>Total Cost: $" + totalCost + "</h1>");
}

$.ajax("moviedb/confirm", {
    method: "GET",
    success: handleSessionData
});