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

package com.liferay.source.formatter.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Hugo Huijser
 */
public class JavaSignature {

	public void addParameter(
		String parameterName, String parameterType,
		Set<String> parameterAnnotations, boolean parameterFinal) {

		_parameters.add(
			new JavaParameter(
				parameterName, parameterType, parameterAnnotations,
				parameterFinal));
	}

	public List<JavaParameter> getParameters() {
		return _parameters;
	}

	public String getReturnType() {
		return _returnType;
	}

	public void setReturnType(String returnType) {
		_returnType = returnType;
	}

	private final List<JavaParameter> _parameters = new ArrayList<>();
	private String _returnType;

}