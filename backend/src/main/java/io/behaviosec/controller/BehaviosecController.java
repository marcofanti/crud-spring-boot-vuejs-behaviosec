package io.behaviosec.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.behaviosec.isdk.client.APICall;
import com.behaviosec.isdk.client.ClientConfiguration;
import com.behaviosec.isdk.client.RestClient;
import com.behaviosec.isdk.client.RestClientOktHttpImpl;
import com.behaviosec.isdk.config.BehavioSecException;
import com.behaviosec.isdk.entities.Report;
import com.behaviosec.isdk.entities.Response;
import com.behaviosec.isdk.evaluators.BooleanEvaluator;
import com.behaviosec.isdk.evaluators.ScoreEvaluator;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

//@CrossOrigin(origins = "http://openam.example.com:8000", maxAge = 3600)
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class BehaviosecController {
    private static final String DEBUG_NAME = "BehaviosecController";
    @Value( "${behaviosec.url}" )
    private String url;
    @Value( "${behaviosec.tenant.id}" )
    private String tenantID;

    @Autowired
    ResourceLoader resourceLoader;

 //   Debug debug = Debug.getInstance(DEBUG_NAME);
    Logger log = Logger.getLogger(BehaviosecController.class.getName());

    private String[] parseBdata(String originalBody) {
        if (originalBody == null || originalBody.length() == 0) {
            return null;
        }

        String bdata = null;
        String username = null;

        JacksonJsonParser parse = new JacksonJsonParser();
        JsonFactory factory = new JsonFactory();
        JsonParser parser = null;

        try {
            parser = factory.createJsonParser(originalBody.toString());
        } catch (JsonParseException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        while(!parser.isClosed()){
            JsonToken jsonToken = null;
            try {
                jsonToken = parser.nextToken();
                if(JsonToken.FIELD_NAME.equals(jsonToken)){
                    String fieldName = parser.getCurrentName();
                    System.out.println(fieldName + parser.getText());

                    jsonToken = parser.nextToken();

                    if("bdata".equalsIgnoreCase(fieldName) || "behaviodata".equalsIgnoreCase(fieldName)){
                        System.out.println(fieldName + parser.getText());
                        bdata = parser.getText();
                        if (bdata == null || bdata.equals("null")) {
                            bdata = null;
                            log.fine("bdata is null");
                        } else {
                            log.fine("********** bdata is set" + bdata);
                        }
                    }
                    if("username".equalsIgnoreCase(fieldName)){
                        System.out.println(fieldName + parser.getText());
                        username = parser.getText();
                        if (username == null || username.equals("null")) {
                            username = null;
                            log.fine("username is null");
                        } else {
                            log.fine("********** username is set" + username);
                        }
                    }

                }
            } catch (IOException e) {
            }
        }

        String [] result = {bdata, username};
        return result;
    }


    @PostMapping("/BehavioSenseAPI/GetAjaxAsync")
    @ResponseBody
    public String ajaxPostPage(HttpServletRequest request) {
        String data = "";
        String userid = "";
        String useragent = "";
        String clientIp = "127.0.0.1";
        String sessionid = "";

        String requestURI = request.getRequestURI();
        log.finest("requestURI is " + requestURI);
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {

            String paramName = parameterNames.nextElement();
            log.finest(paramName);

            String[] paramValues = request.getParameterValues(paramName);
            for (int i = 0; i < paramValues.length; i++) {
                String paramValue = paramValues[i];
                log.finest("t" + paramValue);
            }
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {

            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.finest(headerName + " " + headerValue);
            if ("user-agent".equalsIgnoreCase(headerName)) {
                useragent = headerValue;
            }
        }

        String body = getBody(request);

        if (body != null && body.startsWith("{")) {
            String [] result = parseBdata(body);
            data = result[0];
            userid = result[1];
            if (sessionid == null || sessionid == "") {
                UUID uuid = UUID.randomUUID();
                sessionid = uuid.toString().substring(0, 20);
            }
            getReport(data, userid, useragent, clientIp, sessionid);
            return "{done}";
        } else {
            String[] arrOfStr = body.split("&");

            for (String entry : arrOfStr) {
                log.finest(entry);
                if (entry.startsWith("userid")) {
                    userid = entry.substring("userid=".length());
                } else if (entry.startsWith("sessionid")) {
                    sessionid = entry.substring("sessionid=".length());
                } else if (entry.startsWith("data")) {
                    data = entry.substring("data=".length());
                } else if (entry.startsWith("timing")) {
                    data = entry.substring("timing=".length());
                }
            }

            log.finest(body);
            if (userid != null && ("undefined".equalsIgnoreCase(userid) || "".equalsIgnoreCase(userid))) {
                userid = "test123";
            }

            if (userid != null && !"undefined".equalsIgnoreCase(userid) && !"".equalsIgnoreCase(userid)) {
                getReport(data, userid, useragent, clientIp, sessionid);
            }

            return "100";
        }
    }

    private String getBody(HttpServletRequest request) {
        StringBuilder body = new StringBuilder();
        try (Reader reader = new BufferedReader(
                new InputStreamReader(request.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                body.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String decoded = java.net.URLDecoder.decode(body.toString());
        return decoded;
    }

    public Report getReport(String bdata, String username, String useragent, String clientIp, String uuid) {

        ClientConfiguration cc = new ClientConfiguration(url);
        ClientConfiguration ccWithTenantId = new ClientConfiguration(url);

//		Client client = new Client(cc);


        RestClient rc = new RestClientOktHttpImpl(ccWithTenantId);


/*		ccWithTenantId.setTenantId(tenantID);
		Client clientWithTenantId = new Client(ccWithTenantId);


	*/

        APICall callHealth = APICall.healthBuilder().build();

        APICall callReport = APICall.reportBuilder()
                .tenantId(tenantID)
                .username(username)
                .userIP(clientIp)
                .userAgent(useragent)
                .timingData(bdata)
                .sessionId(uuid)
//                .operatorFlags(Constants.FINALIZE_DIRECTLY)
                .build();

//		APICall callReport = APICall.reportBuilder().username(username).userIP(ip).userAgent(useragent)
//				.timingData(bdata).sessionId(uuid).build();

        try {
            Response h = rc.makeCall(callHealth);
            if (h.hasReport()) {
                System.out.println(h.getReport().toString());
            }
            Response r = rc.makeCall(callReport);
//			System.out.println(r);
            if (r.hasReport()) {
                Report rep = r.getReport();
                if (rep != null) {
                    log.fine(rep.toString());
                    log.fine("Boolean evaluator: " + (new BooleanEvaluator()).evaluate(rep));
                    log.fine("Score evaluator: " + (new ScoreEvaluator()).evaluate(rep));
                    return rep;
                }
            } else {
                log.fine("Error:" + r.getMessage());
            }
        } catch (BehavioSecException e) {
            e.printStackTrace();
        }
        return null;
    }

}
