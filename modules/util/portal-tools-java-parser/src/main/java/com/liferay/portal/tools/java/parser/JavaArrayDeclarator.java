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

package com.liferay.portal.tools.java.parser;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class JavaArrayDeclarator extends JavaExpression {

	public JavaArrayDeclarator(String className) {
		_className = new JavaSimpleValue(className);
	}

	public void setDimensionValueJavaExpressions(
		List<JavaExpression> dimensionValueJavaExpressions) {

		_dimensionValueJavaExpressions = dimensionValueJavaExpressions;
	}

	public void setGenericJavaTypes(List<JavaType> genericJavaTypes) {
		_genericJavaTypes = genericJavaTypes;
	}

	private final JavaSimpleValue _className;
	private List<JavaExpression> _dimensionValueJavaExpressions;
	private List<JavaType> _genericJavaTypes;

}