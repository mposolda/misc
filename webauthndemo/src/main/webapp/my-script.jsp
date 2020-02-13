<%@ page language="java" contentType="text/javascript; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ page import="org.mposolda.WebAuthnBean" %>
<%@ page session="false" %>

    function test() {
        var testOutput = "<%= WebAuthnBean.test() %>";
        output(testOutput);
    }

    function webauthnRegister() {
        output('webauthnRegister called');

        navigator.credentials.create({
            publicKey: {
                // random, cryptographically secure, at least 16 bytes
                challenge:  Uint8Array.from("<%= WebAuthnBean.generateChallenge() %>", c => c.charCodeAt(0)),
                // relying party
                rp: {
                    name: "Awesome Corp" // sample relying party
                },
                user: {
                    id: Uint8Array.from("<%= WebAuthnBean.generateUserId() %>", c => c.charCodeAt(0)),
                    name: "<%= WebAuthnBean.getUsername() %>",
                    displayName: "<%= WebAuthnBean.getDisplayName() %>"
                },
                authenticatorSelection: { userVerification: "preferred" },
                attestation: "direct",
                pubKeyCredParams: [{
                    type: "public-key",
                    alg: -7 // "ES256" IANA COSE Algorithms registry
                }]
            }
        })
        .then(res => {
            output(res);

            var json = JSON.stringify(res);

            output(json);
            
            // Send data to relying party's servers
            post("/webauthn-demo/register", {
                state: "state?",
                provider: "provider?",
                res: JSON.stringify(json)
            });
        })
        .catch(console.error);

    }

    function webauthnLogin() {
        output('webauthnLogin called');
    }

    function output(event) {
        var e = document.getElementById('output').innerHTML;
        document.getElementById('output').innerHTML =  e + new Date().toLocaleString() + "\t" + event + "\n";
    }

