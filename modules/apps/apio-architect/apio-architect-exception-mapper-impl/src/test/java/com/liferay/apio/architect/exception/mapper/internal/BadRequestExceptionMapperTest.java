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

package com.liferay.apio.architect.exception.mapper.internal;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.liferay.apio.architect.error.APIError;
import com.liferay.apio.architect.exception.mapper.ExceptionMapper;

import javax.ws.rs.BadRequestException;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class BadRequestExceptionMapperTest {

	@Test
	public void testConvertReturnsCorrectAPIErrorWithExceptionMessage() {
		ExceptionMapper<BadRequestException> exceptionMapper =
			new BadRequestExceptionMapper();

		BadRequestException badRequestException = new BadRequestException(
			"Message");

		APIError apiError = exceptionMapper.map(badRequestException);

		assertThat(apiError.getException(), is(badRequestException));
		assertThat(
			apiError.getDescription(),
			is(optionalWithValue(equalTo("Message"))));
		assertThat(apiError.getMessage(), is("Message"));
		assertThat(apiError.getStatusCode(), is(400));
		assertThat(apiError.getTitle(), is("Bad Request"));
		assertThat(apiError.getType(), is("bad-request"));
	}

	@Test
	public void testConvertReturnsCorrectAPIErrorWithoutExceptionMessage() {
		ExceptionMapper<BadRequestException> exceptionMapper =
			new BadRequestExceptionMapper();

		BadRequestException badRequestException = new BadRequestException();

		APIError apiError = exceptionMapper.map(badRequestException);

		assertThat(apiError.getDescription(), is(emptyOptional()));
		assertThat(apiError.getException(), is(badRequestException));
		assertThat(apiError.getMessage(), is("HTTP 400 Bad Request"));
		assertThat(apiError.getStatusCode(), is(400));
		assertThat(apiError.getTitle(), is("Bad Request"));
		assertThat(apiError.getType(), is("bad-request"));
	}

}