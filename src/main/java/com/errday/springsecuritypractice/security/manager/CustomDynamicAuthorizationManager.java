package com.errday.springsecuritypractice.security.manager;

import com.errday.springsecuritypractice.admin.repository.ResourcesRepository;
import com.errday.springsecuritypractice.security.mapper.PersistentUrlRoleMapper;
import com.errday.springsecuritypractice.security.service.DynamicAuthorizationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.expression.DefaultHttpSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomDynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings;
    private static final AuthorizationDecision DENY = new AuthorizationDecision(false);
    private static final AuthorizationDecision ACCESS = new AuthorizationDecision(true);
    private final HandlerMappingIntrospector handlerMappingIntrospector;
    private final ResourcesRepository resourcesRepository;
    private final RoleHierarchyImpl roleHierarchy;
    private DynamicAuthorizationService dynamicAuthorizationService;

    @PostConstruct
    public void mapping() {
        //DynamicAuthorizationService dynamicAuthorizationService = new DynamicAuthorizationService(new MapBasedUrlRoleMapper());
        dynamicAuthorizationService = new DynamicAuthorizationService(new PersistentUrlRoleMapper(resourcesRepository));
        setMapping();
    }

    public synchronized void reload() {
        mappings.clear();
        setMapping();
    }

    private void setMapping() {
        mappings = dynamicAuthorizationService.getUrlRoleMappings()
                .entrySet().stream()
                .map(entry -> new RequestMatcherEntry<>(
                        new MvcRequestMatcher(handlerMappingIntrospector, entry.getKey()),
                        customAuthorizationManager(entry.getValue())))
                .collect(Collectors.toList());
    }

    @Override
    public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        AuthorizationManager.super.verify(authentication, object);
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext request) {

        for (RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>> mapping : this.mappings) {
            RequestMatcher matcher = mapping.getRequestMatcher();
            RequestMatcher.MatchResult matchResult = matcher.matcher(request.getRequest());
            if (matchResult.isMatch()) {
                AuthorizationManager<RequestAuthorizationContext> manager = mapping.getEntry();
                return manager.check(authentication, new RequestAuthorizationContext(request.getRequest(), matchResult.getVariables()));
            }
        }

        return ACCESS;
    }

    private AuthorizationManager<RequestAuthorizationContext> customAuthorizationManager(String role) {
        if (role.startsWith("ROLE")) {
            AuthorityAuthorizationManager<RequestAuthorizationContext> authorizationManager = AuthorityAuthorizationManager.hasAuthority(role);
            authorizationManager.setRoleHierarchy(roleHierarchy);
            return authorizationManager;
        } else {
            DefaultHttpSecurityExpressionHandler handler = new DefaultHttpSecurityExpressionHandler();
            handler.setRoleHierarchy(roleHierarchy);
            WebExpressionAuthorizationManager authorizationManager = new WebExpressionAuthorizationManager(role);
            authorizationManager.setExpressionHandler(handler);
            return authorizationManager;
        }
    }
}
