/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.security.auth.verifier;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.auth.http.HttpAuthManagerUtil;
import com.liferay.portal.kernel.security.auth.http.HttpAuthorizationHeader;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.security.auth.AccessControlContext;
import com.liferay.portal.security.auth.AuthException;
import com.liferay.portal.util.PortalUtil;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"auth.verifier.DigestAuthenticationAuthVerifier.digest_auth=false",
		"auth.verifier.DigestAuthenticationAuthVerifier.hosts.allowed=",
		"auth.verifier.DigestAuthenticationAuthVerifier.urls.excludes=*",
		"auth.verifier.DigestAuthenticationAuthVerifier.urls.includes="
	}
)
public class DigestAuthenticationAuthVerifier implements AuthVerifier {

	@Override
	public String getAuthType() {
		return HttpServletRequest.DIGEST_AUTH;
	}

	@Override
	public AuthVerifierResult verify(
			AccessControlContext accessControlContext, Properties configuration)
		throws AuthException {

		try {
			AuthVerifierResult authVerifierResult = new AuthVerifierResult();

			HttpServletRequest request = accessControlContext.getRequest();

			long userId = PortalUtil.getDigestAuthUserId(request);

			if (userId == 0) {

				// Deprecated

				boolean forcedDigestAuth = MapUtil.getBoolean(
					accessControlContext.getSettings(), "digest_auth");

				if (forcedDigestAuth) {
					HttpServletResponse response =
						accessControlContext.getResponse();

					HttpAuthorizationHeader httpAuthorizationHeader =
						new HttpAuthorizationHeader(
							HttpAuthorizationHeader.SCHEME_DIGEST);

					HttpAuthManagerUtil.generateChallenge(
						request, response, httpAuthorizationHeader);

					authVerifierResult.setState(
						AuthVerifierResult.State.INVALID_CREDENTIALS);
				}

				return authVerifierResult;
			}

			authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
			authVerifierResult.setUserId(userId);

			return authVerifierResult;
		}
		catch (PortalException pe) {
			throw new AuthException(pe);
		}
		catch (SystemException se) {
			throw new AuthException(se);
		}
	}

}