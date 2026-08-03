package com.app.my_project.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.app.my_project.WebConfig;
import com.app.my_project.annotation.Public;
import com.app.my_project.annotation.RequireRole;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // Spring forwards failed requests here to build the error response;
        // blocking it would turn every error into a confusing 401
        if ("/error".equals(request.getRequestURI())) {
            return true;
        }

        // Deny by default: every endpoint requires a valid token
        // unless it is explicitly marked @Public (e.g. signin)
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (handlerMethod.hasMethodAnnotation(Public.class)) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing Authorization header - sign in first");
            return false;
        }

        try {
            String tokenWithoutBearer = token.replace("Bearer ", "");
            DecodedJWT decoded = JWT.require(Algorithm.HMAC256(WebConfig.getSecret()))
                    .build()
                    .verify(tokenWithoutBearer);

            // Authentication passed; now authorisation. A missing @RequireRole means
            // "any signed-in user", which is what the shared modules (inventory,
            // production, sales) need.
            RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
            if (requireRole == null) {
                requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
            }

            if (requireRole != null) {
                String role = decoded.getClaim("role").asString();
                boolean allowed = false;
                for (String allowedRole : requireRole.value()) {
                    if (allowedRole.equals(role)) {
                        allowed = true;
                        break;
                    }
                }
                if (!allowed) {
                    // 403, not 401: the token is valid, the account simply lacks the role
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Forbidden: this endpoint requires role "
                            + String.join(" or ", requireRole.value()));
                    return false;
                }
            }

            return true;
        } catch (TokenExpiredException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired, please login again");
            return false;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return false;
        }
    }
}
