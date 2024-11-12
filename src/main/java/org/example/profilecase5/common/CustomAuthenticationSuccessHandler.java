package org.example.profilecase5.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Collection<? extends GrantedAuthority > authorities = authentication.getAuthorities();
        String targetUrl;

        //Kiem tra role, sau do dieu huong ve trang tuong ung :
        if(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            targetUrl = "/admin";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            targetUrl = "/hosting";
            
        }else {
            targetUrl = (String) request.getSession().getAttribute("REDIRECT_URL");
            if (targetUrl == null || targetUrl.isEmpty()) {
                targetUrl = "/";
            }
            request.getSession().removeAttribute("REDIRECT_URL");
        }
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}