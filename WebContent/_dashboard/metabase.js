function createTable(resultData) {
    for (var i = 0; i < resultData.length; i++) {
        var div = document.createElement('div');
        div.className = 'content';

        let rsList = resultData[i]["cols"].split(",");
        let dtList = resultData[i]["data"].split(",");
        let myStr = "";
        myStr=
            "<h2 style=\"text-align: center;\">" + resultData[i]["table"] + "</h2>" +
            "<table id='movie_table' class='table table-striped'>" +
            "<thead>" +
            "<tr>" +
            "<th style='text-align: center;'>Attributes</th>" +
            "<th style='text-align: center;'>Datatype</th>" +
            "</tr>" +
            "</thead>" +
            "<tbody id='movie_table_body'>";

        // Loop through both rsList and dtList
        for (var j = 0; j < Math.max(rsList.length, dtList.length); j++) {
            myStr +=
                "<tr>" +
                "<td style='text-align: center;'>" + (rsList[j] || "") + "</td>" +
                "<td style='text-align: center;'>" + (dtList[j] || "") + "</td>" +
                "</tr>";
        }

        myStr += "</tbody></table>";  // Close the table
        div.innerHTML = myStr;
        console.log(div.innerHTML);
        document.body.appendChild(div);
    }
}








jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "../api/_dashboard/home", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => createTable(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

