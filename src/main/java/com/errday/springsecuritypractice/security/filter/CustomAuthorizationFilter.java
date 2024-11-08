package com.errday.springsecuritypractice.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.function.Supplier;

@Setter
@Getter
public class CustomAuthorizationFilter extends GenericFilterBean implements InitializingBean {
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final AuthorizationManager<HttpServletRequest> authorizationManager;
    private AuthorizationEventPublisher eventPublisher = CustomAuthorizationFilter::noPublish;
    private boolean observeOncePerRequest = false;
    private boolean filterErrorDispatch = true;
    private boolean filterAsyncDispatch = true;

    public CustomAuthorizationFilter(AuthorizationManager<HttpServletRequest> authorizationManager) {
        Assert.notNull(authorizationManager, "authorizationManager cannot be null");
        this.authorizationManager = authorizationManager;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        if (this.observeOncePerRequest && this.isApplied(request)) {
            chain.doFilter(request, response);
        } else if (this.skipDispatch(request)) {
            chain.doFilter(request, response);
        } else {
            String alreadyFilteredAttributeName = this.getAlreadyFilteredAttributeName();
            request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);

            try {
                AuthorizationDecision decision = this.authorizationManager.check(this::getAuthentication, request);
                this.eventPublisher.publishAuthorizationEvent(this::getAuthentication, request, decision);
                if (decision != null && !decision.isGranted()) {
                    throw new AccessDeniedException("Access Denied");
                }

                chain.doFilter(request, response);
            } finally {
                request.removeAttribute(alreadyFilteredAttributeName);
            }

        }
    }

    private boolean skipDispatch(HttpServletRequest request) {
        if (DispatcherType.ERROR.equals(request.getDispatcherType()) && !this.filterErrorDispatch) {
            return true;
        } else {
            return DispatcherType.ASYNC.equals(request.getDispatcherType()) && !this.filterAsyncDispatch;
        }
    }

    private boolean isApplied(HttpServletRequest request) {
        return request.getAttribute(this.getAlreadyFilteredAttributeName()) != null;
    }

    private String getAlreadyFilteredAttributeName() {
        String name = this.getFilterName();
        if (name == null) {
            name = this.getClass().getName();
        }

        return name + ".APPLIED";
    }

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }

    private Authentication getAuthentication() {
        Authentication authentication = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("An Authentication object was not found in the SecurityContext");
        } else {
            return authentication;
        }
    }

    public void setAuthorizationEventPublisher(AuthorizationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "eventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    public AuthorizationManager<HttpServletRequest> getAuthorizationManager() {
        return this.authorizationManager;
    }

    /** @deprecated */
    @Deprecated(
            since = "6.1",
            forRemoval = true
    )
    public void setShouldFilterAllDispatcherTypes(boolean shouldFilterAllDispatcherTypes) {
        this.observeOncePerRequest = !shouldFilterAllDispatcherTypes;
        this.filterErrorDispatch = shouldFilterAllDispatcherTypes;
        this.filterAsyncDispatch = shouldFilterAllDispatcherTypes;
    }

    private static <T> void noPublish(Supplier<Authentication> authentication, T object, AuthorizationDecision decision) {
    }

    public boolean isObserveOncePerRequest() {
        return this.observeOncePerRequest;
    }

    public void setObserveOncePerRequest(boolean observeOncePerRequest) {
        this.observeOncePerRequest = observeOncePerRequest;
    }

    public void setFilterErrorDispatch(boolean filterErrorDispatch) {
        this.filterErrorDispatch = filterErrorDispatch;
    }

    public void setFilterAsyncDispatch(boolean filterAsyncDispatch) {
        this.filterAsyncDispatch = filterAsyncDispatch;
    }
}
