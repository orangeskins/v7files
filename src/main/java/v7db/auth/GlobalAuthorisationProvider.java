/**
 * Copyright (c) 2011, Thilo Planz. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package v7db.auth;

import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import v7db.files.V7File;

class GlobalAuthorisationProvider implements AuthorisationProvider {

	private final Properties props;

	private final String endpoint;

	GlobalAuthorisationProvider(Properties props, String endpoint) {
		this.props = props;
		this.endpoint = endpoint;
	}

	private String getEndpointProperty(String endpoint, String key) {
		String p = props.getProperty(endpoint + "." + key);
		if (p == null)
			return props.getProperty(key);
		return p;
	}

	String getProperty(String name) {
		return getEndpointProperty(endpoint, name);
	}

	private String getAnonymousUser() {
		return getProperty("auth.anonymous");
	}

	public boolean authorise(V7File resource, AuthenticationToken user,
			String permission) {
		Object[] roles;
		if (user == null || user == AuthenticationToken.ANONYMOUS) {
			String a = getAnonymousUser();
			if (StringUtils.isBlank(a))
				return false;
			roles = new String[] { a };
		} else {
			roles = user.getRoles();
		}
		String[] acl = StringUtils.stripAll(StringUtils.split(
				getProperty(permission), ','));
		for (Object role : roles) {
			if (ArrayUtils.contains(acl, role))
				return true;
		}
		return false;
	}

}