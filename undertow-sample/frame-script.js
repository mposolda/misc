var onBodyLoad = function() {
        document.getElementById("frame1").onload = function() {
            frameLoaded('frame1-post');
        }

        document.getElementById("frame2").onload = function() {
            frameLoaded('frame2-get');
        }

        var formHtml1 = "<HTML><BODY>" +
        "    <FORM ID=\"form1\" METHOD=\"POST\" ACTION=\"http://localhost:8081\">" +
        "      <INPUT name=\"SAMLRequest\" value=\"foo-postt\" />" +
        "      <NOSCRIPT>" +
        "        <P>JavaScript is disabled. We strongly recommend to enable it. Click the button below to continue .</P>" +
        "        <INPUT name=\"continue\" TYPE=\"SUBMIT\" VALUE=\"CONTINUE\" />" +
        "      </NOSCRIPT>" +
        "    </FORM>" +
        "    <SCRIPT>document.getElementById('form1').submit();" +
        "    </SCRIPT>"
        "  </BODY></HTML>";

        var frame1 = document.getElementById("frame1");
        var fdoc1 = frame1.contentDocument;
        fdoc1.write(formHtml1);


        var frame2 = document.getElementById('frame2').src = "http://localhost:8080?SAMLRequest=foo-get";
}



var frameLoaded = function(frameID) {
        eventHandled("Frame loaded: " + frameID);
}

var counter = 0;

var eventHandled = function(eventt) {
        var e = document.getElementById('messages').innerHTML;
        document.getElementById('messages').innerHTML = new Date().toLocaleString() + "\t" + eventt + "<br>" + e;

        counter++;
        if (counter == 2) {
           eventHandled("BOTH IFRAMES LOADED. WILL REDIRECT TO DIFFERENT SITE IN 5 SECONDS");
           setTimeout(function() {
              document.location = "http://www.seznam.cz?SAMLResponse=fff"
           }, 5000);
        }
}

