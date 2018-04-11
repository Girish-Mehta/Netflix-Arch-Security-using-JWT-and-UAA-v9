package com.demo;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
@Component
public class ZullRedirectFilter extends ZuulFilter {
   
	@Value("${Authorities}")
    String Authorities;
	 @Override
    public boolean shouldFilter() {
        return true;
    }
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = request.getHeader("application-token");

        // if request is to public service
        String[] urlParts = request.getServletPath().split("/");
        System.out.println("**************************URLParts:" + request.getServletPath());
        String serviceUrl = urlParts[1];
        System.out.println("************************Service Url:" + serviceUrl);

        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(Authorities);
            System.out.println(Authorities);
        } catch (ParseException e3) {
            return HttpServletResponse.SC_FORBIDDEN;
            // e3.printStackTrace();
        }
        String services = (String) json.get("User");
        String[] servicess = services.split(",");
        System.out.println("*****************Services:" + services);
        for (String service : servicess) {
            if (serviceUrl.equals(service)) {
                // then let go
                return HttpServletResponse.SC_OK;
            }
        }
        //remove from original code...only for test
        if(request.getServletPath().equals("/hello-service/v2/api-docs"))
       	 return HttpServletResponse.SC_OK;
        
        // if private have token let it go else redirect to login page
        if (token.equals("none") || token.equals("")) {
        	// redirect to login page if service is private
        	HttpServletResponse response = ctx.getResponse();
            response.setHeader("Location", "localhost:8080/index.html");
        }
		return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}