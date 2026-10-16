/*
 * Copyright © 2021-2026 Bull SAS
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of work done in the K-HEALTHinAIR EU funded project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.atos.ari.health.khealthinair.model.enums;

/**
 * Class for maintaining Pilot data.
 *
 */
public enum Pilot {
	//@formatter:off
	AT("Austria"),
	DE("Germany"),
	ES("Spain"),
	NL("Netherlands"),
	NO("Norway"),
	PL("Poland");
	//@formatter:on

	private final String name;

	Pilot(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}