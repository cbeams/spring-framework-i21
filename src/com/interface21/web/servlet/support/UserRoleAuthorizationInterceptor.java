package com.interface21.web.servlet.support;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.HandlerInterceptor;

/**
 * Interceptor that checks the authorization of the current user via the
 * user's roles, as evaluated by HttpServletRequest's isUserInRole method.
 * @author Juergen Hoeller
 * @since 20.06.2003
 * @see javax.servlet.http.HttpServletRequest#isUserInRole
 */
public class UserRoleAuthorizationInterceptor implements HandlerInterceptor {

	private String[] authorizedRoles;

	/**
	 * Set the roles that this interceptor should treat as authorized.
	 * @param authorizedRoles array of role names
	 */
	public final void setAuthorizedRoles(String[] authorizedRoles) {
		this.authorizedRoles = authorizedRoles;
	}

	public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws ServletException, IOException {
		if (this.authorizedRoles != null) {
			for (int i = 0; i < this.authorizedRoles.length; i++) {
				if (request.isUserInRole(this.authorizedRoles[i])) {
					return true;
				}
			}
		}
		handleNotAuthorized(request, response, handler);
		return false;
	}

	/**
	 * Handle a request that is not authorized according to this interceptor.
	 * Default implementation sends HTTP status code 403 ("forbidden").
	 * <p>This method can be overridden to write a custom message, forward or
	 * redirect to some error page or login page, or throw a ServletException.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler chosen handler to execute, for type and/or instance evaluation
	 * @throws ServletException if there is an internal error
	 * @throws IOException in case of an I/O error when writing the response
	 */
	protected void handleNotAuthorized(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

}
