import 'clay-button';
import 'clay-sticker';
import 'clay-select';
import dom from 'metal-dom';
import Soy from 'metal-soy';
import {Config} from 'metal-state';
import templates from './ManageCollaborators.soy';
import PortletBase from 'frontend-js-web/liferay/PortletBase.es';

/**
 * Handles actions to delete or change permissions of the
 * collaborators for a file entry.
 */
class ManageCollaborators extends PortletBase {

	/**
	 * @inheritDoc
	 */
	attached() {
		this._deleteSharingEntryIds = [];
		this._sharingEntryIdsAndPermissions = new Map();
		this._sharingEntryIdsAndExpirationDate = new Map();

		let tomorrow = new Date();
		tomorrow = tomorrow.setDate(tomorrow.getDate() + 1);

		this._tomorrowDate = new Date(tomorrow).toISOString().split('T')[0];
	}

	/**
	 * Closes the dialog.
	 * @protected
	 */
	_closeDialog() {
		const collaboratorsDialog = Liferay.Util.getWindow(this._dialogId);

		if (collaboratorsDialog && collaboratorsDialog.hide) {
			collaboratorsDialog.hide();
		}
	}

	/**
	 * Finds a collaborator by his id
	 *
	 * @param  {String} collaboratorId The id of a collaborator
	 * @return {Object} Collaborator
	 */
	_getCollaborator(collaboratorId) {
		let collaborator = this.collaborators.find(
			collaborator => collaborator.id === collaboratorId
		);

		return collaborator;
	}

	/**
	 * Closes the dialog.
	 * @protected
	 */
	_handleCancelButtonClick() {
		this._closeDialog();
	}

	/**
	 * Gets the new permission key for the selected
	 * collaborator.
	 *
	 * @param {Event} event
	 * @protected
	 */
	_handleChangePermission(event) {
		let sharingEntryId = event.target.getAttribute('name');
		let sharingEntryPermissionKey = event.target.value;

		this._sharingEntryIdsAndPermissions.set(sharingEntryId, sharingEntryPermissionKey);
	}

	/**
	 * Gets the selected expiration date.
	 *
	 * @param {Event} event
	 * @protected
	 */
	_handleChangeExpirationDate(event) {
		let sharingEntryExpirationDate = event.target.value;
		let sharingEntryId = event.target.getAttribute('name');

		this._sharingEntryIdsAndExpirationDate.set(sharingEntryId, sharingEntryExpirationDate);
	}

	/**
	 * Deletes the collaborator.
	 *
	 * @param {Event} event
	 * @protected
	 */
	_handleDeleteCollaborator(event) {
		let collaboratorId = event.delegateTarget.dataset.collaboratorId;
		let sharingEntryId = event.delegateTarget.dataset.sharingentryId;

		this.collaborators = this.collaborators.filter(
			collaborator => collaborator.id != collaboratorId
		);

		this._deleteSharingEntryIds.push(sharingEntryId);
	}

	/**
	 * Remove the expiration date
	 * @param  {Event} event
	 * @protected
	 */
	_handleDeleteExpirationDate(event) {
		let collaboratorId = event.currentTarget.dataset.collaboratorid;

		let collaborator = this._getCollaborator(collaboratorId);

		if (collaborator) {
			let sharingEntryExpirationDate = null;

			collaborator.expanded = false;
			collaborator.sharingEntryExpirationDate = sharingEntryExpirationDate;
			collaborator.sharingEntryExpirationDateTooltip = null;

			this._sharingEntryIdsAndExpirationDate.set(collaborator.sharingEntryId, sharingEntryExpirationDate);

			this.collaborators = this.collaborators;
		}
	}

	/**
	 * Toggles the class 'active'
	 *
	 * @param {Event} event
	 * @protectec
	 */
	_handleHoverCollaborator(event) {
		dom.toggleClasses(event.delegateTarget, 'active');
	}

	/**
	 * Sends a request to the server to update permissions
	 * or delete collaborators.
	 *
	 * @protected
	 */
	_handleSaveButtonClick() {
		let expirationDates = Array.from(this._sharingEntryIdsAndExpirationDate, (id, date) => id + ',' + date);
		let permissions = Array.from(this._sharingEntryIdsAndPermissions, (id, key) => id + ',' + key);

		this.fetch(
			this.actionUrl,
			{
				deleteSharingEntryIds: this._deleteSharingEntryIds,
				sharingEntryIdActionIdPairs: permissions,
				sharingEntryIdExpirationDatePairs: expirationDates
			}
		)
			.then(
				response => {
					this.submitting = false;

					const jsonResponse = response.json();

					return response.ok ?
						jsonResponse :
						jsonResponse.then(
							json => {
								const error = new Error(json.errorMessage || response.statusText);
								throw Object.assign(error, {response});
							}
						)
					;
				}
			)
			.then(
				json => {
					this._loadingResponse = false;
					this._showNotification(json.successMessage);
				}
			)
			.catch(
				error => {
					this._loadingResponse = false;
					this._showNotification(error.message, true);
				}
			);

		this._loadingResponse = true;
	}

	/**
	 * Hides or show the block where the expiration
	 * date can be edited.
	 *
	 * @param  {Event} event
	 * @protected
	 */
	_hideShowExpirationDateBlock(event) {
		let collaboratorId = event.currentTarget.dataset.collaboratorid;

		let collaborator = this._getCollaborator(collaboratorId);

		collaborator.expanded = !collaborator.expanded;

		if (collaborator.expanded && !collaborator.sharingEntryExpirationDate) {
			collaborator.sharingEntryExpirationDate = this._tomorrowDate;
			collaborator.sharingEntryExpirationDateTooltip = this._getTooltipDate(this._tomorrowDate);

			this._sharingEntryIdsAndExpirationDate.set(
				collaborator.sharingEntryId,
				collaborator.sharingEntryExpirationDate
			);
		}

		this.collaborators = this.collaborators;
	}

	/**
	 * Get the formatted date that has to be shown
	 * in the tooltip.
	 *
	 * @param  {Date} expirationDate [description]
	 * @return {String}                [description]
	 */
	_getTooltipDate(expirationDate) {
		return new Date(expirationDate).toLocaleDateString(Liferay.ThemeDisplay.getBCP47LanguageId());
	}

	/**
	 * Show notification in the opener and closes dialog
	 * after is rendered
	 * @param {string} message message for notification
	 * @param {boolean} error Flag indicating if is an error or not
	 * @private
	 * @review
	 */
	_showNotification(message, error) {
		const parentOpenToast = Liferay.Util.getOpener().Liferay.Util.openToast;

		const openToastParams = {
			events: {
				'attached': this._closeDialog.bind(this)
			},
			message
		};

		if (error) {
			openToastParams.title = Liferay.Language.get('error');
			openToastParams.type = 'danger';
		}

		parentOpenToast(openToastParams);
	}
}

/**
 * State definition.
 * @ignore
 * @static
 * @type {!Object}
 */
ManageCollaborators.STATE = {

	/**
	 * Uri to send the manage collaborators fetch request.
	 * @instance
	 * @memberof ManageCollaborators
	 * @type {String}
	 */
	actionUrl: Config.string().required(),

	/**
	 * List of collaborators
	 * @type {Array.<Object>}
	 */
	collaborators: Config.array().required(),

	/**
	 * Id of the dialog
	 * @type {String}
	 */
	dialogId: Config.string().required,

	/**
	 * Path to images.
	 * @instance
	 * @memberof ManageCollaborators
	 * @type {String}
	 */
	spritemap: Config.string().required()
};

// Register component

Soy.register(ManageCollaborators, templates);

export default ManageCollaborators;