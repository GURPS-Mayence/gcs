/*
 * Copyright (c) 1998-2014 by Richard A. Wilkes. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * version 2.0. If a copy of the MPL was not distributed with this file, You
 * can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as defined
 * by the Mozilla Public License, version 2.0.
 */

package com.trollworks.gcs.skill;

import com.trollworks.gcs.character.GURPSCharacter;

/** The possible skill attributes. */
public enum SkillAttribute {
	/** The strength attribute. */
	ST {
		@Override
		public int getBaseSkillLevel(GURPSCharacter character) {
			return character != null ? character.getStrength() : Integer.MIN_VALUE;
		}
	},
	/** The dexterity attribute. */
	DX {
		@Override
		public int getBaseSkillLevel(GURPSCharacter character) {
			return character != null ? character.getDexterity() : Integer.MIN_VALUE;
		}
	},
	/** The health attribute. */
	HT {
		@Override
		public int getBaseSkillLevel(GURPSCharacter character) {
			return character != null ? character.getHealth() : Integer.MIN_VALUE;
		}
	},
	/** The intelligence attribute. */
	IQ {
		@Override
		public int getBaseSkillLevel(GURPSCharacter character) {
			return character != null ? character.getIntelligence() : Integer.MIN_VALUE;
		}
	},
	/** The will attribute. */
	Will {
		@Override
		public int getBaseSkillLevel(GURPSCharacter character) {
			return character != null ? character.getWill() : Integer.MIN_VALUE;
		}
	},
	/** The perception attribute. */
	Per {
		@Override
		public int getBaseSkillLevel(GURPSCharacter character) {
			return character != null ? character.getPerception() : Integer.MIN_VALUE;
		}
	};

	/**
	 * @param character The character to work with.
	 * @return The base skill level for this attribute.
	 */
	public abstract int getBaseSkillLevel(GURPSCharacter character);
}
