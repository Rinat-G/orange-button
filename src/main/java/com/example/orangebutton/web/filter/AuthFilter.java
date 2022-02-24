package com.example.orangebutton.web.filter;


import com.example.orangebutton.domain.service.AuthService;
import com.example.orangebutton.exception.AuthenticationException;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

//@Component
//@Order(1)
public class AuthFilter implements Filter {

    public static final String TOKEN_PARAM_NAME = "token";
    public static final String EMAIL_ATTR_NAME = "email";
    private final AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        var token = request.getParameter(TOKEN_PARAM_NAME);
        if (token == null) {
            throw new RuntimeException("Token param is not specified");
        }
        try {
            var email = authService.authenticate(token);
            request.setAttribute(EMAIL_ATTR_NAME, email);
        } catch (AuthenticationException e) {
            HttpServletResponse res = (HttpServletResponse) response;
            res.setStatus(SC_UNAUTHORIZED);
            res.getOutputStream().print(e.getMessage());
            return;
        }
        chain.doFilter(request, response);


    }

}