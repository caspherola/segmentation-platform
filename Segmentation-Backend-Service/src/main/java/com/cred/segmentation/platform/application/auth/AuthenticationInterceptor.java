package com.cred.segmentation.platform.application.auth;

import com.cred.segmentation.platform.application.model.Permission;
import com.cred.segmentation.platform.application.model.SegmentationCreateUser;
import com.cred.segmentation.platform.application.service.impl.AuthenticationService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;  // Changed from javax to jakarta
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final AuthenticationService authenticationService;

    public AuthenticationInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String actualToken = token.substring(7);
        SegmentationCreateUser user = authenticationService.authenticate(actualToken);

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // Check permission based on HTTP method
        Permission requiredPermission = getRequiredPermission(request.getMethod());
        if (!authenticationService.authorize(user, requiredPermission)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        // Store user in request for controller access
        request.setAttribute("user", user);
        return true;
    }

    private Permission getRequiredPermission(String httpMethod) {
        switch (httpMethod.toUpperCase()) {
            case "GET":
                return Permission.VIEW;
            case "POST":
                return Permission.CREATE;
            case "PUT":
            case "PATCH":
                return Permission.UPDATE;
            case "DELETE":
                return Permission.DELETE;
            default:
                return Permission.VIEW;
        }
    }
}
