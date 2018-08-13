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

package com.liferay.blogs.uad.anonymizer;

import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.service.BlogsEntryLocalService;
import com.liferay.blogs.uad.constants.BlogsUADConstants;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;

import com.liferay.user.associated.data.anonymizer.DynamicQueryUADAnonymizer;

import org.osgi.service.component.annotations.Reference;

/**
 * Provides the base implementation for the blogs entry UAD anonymizer.
 *
 * <p>
 * This implementation exists only as a container for the default methods
 * generated by ServiceBuilder. All custom service methods should be put in
 * {@link BlogsEntryUADAnonymizer}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public abstract class BaseBlogsEntryUADAnonymizer
	extends DynamicQueryUADAnonymizer<BlogsEntry> {
	@Override
	public void autoAnonymize(BlogsEntry blogsEntry, long userId,
		User anonymousUser) throws PortalException {
		if (blogsEntry.getUserId() == userId) {
			blogsEntry.setUserId(anonymousUser.getUserId());
			blogsEntry.setUserName(anonymousUser.getFullName());
		}

		if (blogsEntry.getStatusByUserId() == userId) {
			blogsEntry.setStatusByUserId(anonymousUser.getUserId());
			blogsEntry.setStatusByUserName(anonymousUser.getFullName());
		}

		blogsEntryLocalService.updateBlogsEntry(blogsEntry);
	}

	@Override
	public void delete(BlogsEntry blogsEntry) throws PortalException {
		blogsEntryLocalService.deleteBlogsEntry(blogsEntry);
	}

	@Override
	public Class<BlogsEntry> getTypeClass() {
		return BlogsEntry.class;
	}

	@Override
	protected ActionableDynamicQuery doGetActionableDynamicQuery() {
		return blogsEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	protected String[] doGetUserIdFieldNames() {
		return BlogsUADConstants.USER_ID_FIELD_NAMES_BLOGS_ENTRY;
	}

	@Reference
	protected BlogsEntryLocalService blogsEntryLocalService;
}