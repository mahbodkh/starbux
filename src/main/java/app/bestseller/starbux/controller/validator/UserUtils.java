package app.bestseller.starbux.controller.validator;

//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;

public class UserUtils {

    private UserUtils() {
    }

    public static String getAuthenticatedUsername() {
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        Authentication authentication = securityContext.getAuthentication();
//        if (authentication == null) {
//            return null;
//        }
//        if (authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
//            return springSecurityUser.getUsername();
//        } else if (authentication.getPrincipal() instanceof String) {
//            return (String) authentication.getPrincipal();
//        }
        return null;
    }

}
