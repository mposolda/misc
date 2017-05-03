        var formHtml = "<HTML><BODY>" +
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

        var frame = document.getElementById("frame1");
        var fdoc = frame.contentDocument;
        fdoc.write(formHtml);

