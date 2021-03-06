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

package com.trollworks.gcs.spell;

import com.trollworks.gcs.character.GURPSCharacter;
import com.trollworks.gcs.prereq.PrereqsPanel;
import com.trollworks.gcs.skill.SkillDifficulty;
import com.trollworks.gcs.skill.SkillLevel;
import com.trollworks.gcs.weapon.MeleeWeaponEditor;
import com.trollworks.gcs.weapon.RangedWeaponEditor;
import com.trollworks.gcs.weapon.WeaponStats;
import com.trollworks.gcs.widgets.outline.RowEditor;
import com.trollworks.toolkit.annotation.Localize;
import com.trollworks.toolkit.ui.UIUtilities;
import com.trollworks.toolkit.ui.layout.ColumnLayout;
import com.trollworks.toolkit.ui.widget.LinkedLabel;
import com.trollworks.toolkit.ui.widget.outline.OutlineModel;
import com.trollworks.toolkit.utility.Localization;
import com.trollworks.toolkit.utility.text.NumberFilter;
import com.trollworks.toolkit.utility.text.Numbers;
import com.trollworks.toolkit.utility.text.TextUtility;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/** The detailed editor for {@link Spell}s. */
public class SpellEditor extends RowEditor<Spell> implements ActionListener, DocumentListener {
	@Localize("Name")
	private static String		NAME;
	@Localize("The name of the spell, without any notes")
	private static String		NAME_TOOLTIP;
	@Localize("The name field may not be empty")
	private static String		NAME_CANNOT_BE_EMPTY;
	@Localize("Tech Level")
	private static String		TECH_LEVEL;
	@Localize("Whether this spell requires tech level specialization,\nand, if so, at what tech level it was learned")
	private static String		TECH_LEVEL_TOOLTIP;
	@Localize("Tech Level Required")
	private static String		TECH_LEVEL_REQUIRED;
	@Localize("Whether this spell requires tech level specialization")
	private static String		TECH_LEVEL_REQUIRED_TOOLTIP;
	@Localize("College")
	private static String		COLLEGE;
	@Localize("The college the spell belongs to")
	private static String		COLLEGE_TOOLTIP;
	@Localize("Power Source")
	private static String		POWER_SOURCE;
	@Localize("The source of power for the spell")
	private static String		POWER_SOURCE_TOOLTIP;
	@Localize("Class")
	private static String		CLASS;
	@Localize("The class of spell (Area, Missile, etc.)")
	private static String		CLASS_ONLY_TOOLTIP;
	@Localize("The class field may not be empty")
	private static String		CLASS_CANNOT_BE_EMPTY;
	@Localize("Casting Cost")
	private static String		CASTING_COST;
	@Localize("The casting cost of the spell")
	private static String		CASTING_COST_TOOLTIP;
	@Localize("The casting cost field may not be empty")
	private static String		CASTING_COST_CANNOT_BE_EMPTY;
	@Localize("Maintenance Cost")
	private static String		MAINTENANCE_COST;
	@Localize("The cost to maintain a spell after its initial duration")
	private static String		MAINTENANCE_COST_TOOLTIP;
	@Localize("Casting Time")
	private static String		CASTING_TIME;
	@Localize("The casting time of the spell")
	private static String		CASTING_TIME_TOOLTIP;
	@Localize("The casting time field may not be empty")
	private static String		CASTING_TIME_CANNOT_BE_EMPTY;
	@Localize("Duration")
	private static String		DURATION;
	@Localize("The duration of the spell once its cast")
	private static String		DURATION_TOOLTIP;
	@Localize("The duration field may not be empty")
	private static String		DURATION_CANNOT_BE_EMPTY;
	@Localize("Categories")
	private static String		CATEGORIES;
	@Localize("The category or categories the spell belongs to (separate multiple categories with a comma)")
	private static String		CATEGORIES_TOOLTIP;
	@Localize("Notes")
	private static String		NOTES;
	@Localize("Any notes that you would like to show up in the list along with this spell")
	private static String		NOTES_TOOLTIP;
	@Localize("Points")
	private static String		EDITOR_POINTS;
	@Localize("The number of points spent on this spell")
	private static String		EDITOR_POINTS_TOOLTIP;
	@Localize("Level")
	private static String		EDITOR_LEVEL;
	@Localize("The spell level and relative spell level to roll against")
	private static String		EDITOR_LEVEL_TOOLTIP;
	@Localize("Difficulty")
	private static String		DIFFICULTY;
	@Localize("The difficulty of the spell")
	private static String		DIFFICULTY_TOOLTIP;
	@Localize("Page Reference")
	private static String		EDITOR_REFERENCE;
	@Localize("A reference to the book and page this spell appears\non (e.g. B22 would refer to \"Basic Set\", page 22)")
	private static String		REFERENCE_TOOLTIP;

	static {
		Localization.initialize();
	}

	private JTextField			mNameField;
	private JTextField			mCollegeField;
	private JTextField			mPowerSourceField;
	private JTextField			mClassField;
	private JTextField			mCastingCostField;
	private JTextField			mMaintenanceField;
	private JTextField			mCastingTimeField;
	private JTextField			mDurationField;
	private JComboBox<Object>	mDifficultyCombo;
	private JTextField			mNotesField;
	private JTextField			mCategoriesField;
	private JTextField			mPointsField;
	private JTextField			mLevelField;
	private JTextField			mReferenceField;
	private JTabbedPane			mTabPanel;
	private PrereqsPanel		mPrereqs;
	private JCheckBox			mHasTechLevel;
	private JTextField			mTechLevel;
	private String				mSavedTechLevel;
	private MeleeWeaponEditor	mMeleeWeapons;
	private RangedWeaponEditor	mRangedWeapons;

	/**
	 * Creates a new {@link Spell} editor.
	 *
	 * @param spell The {@link Spell} to edit.
	 */
	public SpellEditor(Spell spell) {
		super(spell);

		boolean notContainer = !spell.canHaveChildren();
		Container content = new JPanel(new ColumnLayout(2));
		Container fields = new JPanel(new ColumnLayout());
		Container wrapper1 = new JPanel(new ColumnLayout(notContainer ? 3 : 2));
		Container wrapper2 = new JPanel(new ColumnLayout(4));
		Container wrapper3 = new JPanel(new ColumnLayout(2));
		Container noGapWrapper = new JPanel(new ColumnLayout(2, 0, 0));
		Container ptsPanel = null;
		JLabel icon = new JLabel(spell.getIcon(true));
		Dimension size = new Dimension();
		Container refParent = wrapper3;

		mNameField = createCorrectableField(wrapper1, wrapper1, NAME, spell.getName(), NAME_TOOLTIP);
		fields.add(wrapper1);
		if (notContainer) {
			createTechLevelFields(wrapper1);
			mCollegeField = createField(wrapper2, wrapper2, COLLEGE, spell.getCollege(), COLLEGE_TOOLTIP, 0);
			mPowerSourceField = createField(wrapper2, wrapper2, POWER_SOURCE, spell.getPowerSource(), POWER_SOURCE_TOOLTIP, 0);
			mClassField = createCorrectableField(wrapper2, wrapper2, CLASS, spell.getSpellClass(), CLASS_ONLY_TOOLTIP);
			mCastingCostField = createCorrectableField(wrapper2, wrapper2, CASTING_COST, spell.getCastingCost(), CASTING_COST_TOOLTIP);
			mMaintenanceField = createField(wrapper2, wrapper2, MAINTENANCE_COST, spell.getMaintenance(), MAINTENANCE_COST_TOOLTIP, 0);
			mCastingTimeField = createCorrectableField(wrapper2, wrapper2, CASTING_TIME, spell.getCastingTime(), CASTING_TIME_TOOLTIP);
			mDurationField = createCorrectableField(wrapper2, wrapper2, DURATION, spell.getDuration(), DURATION_TOOLTIP);
			fields.add(wrapper2);

			ptsPanel = createPointsFields();
			fields.add(ptsPanel);
			refParent = ptsPanel;
		}
		mNotesField = createField(wrapper3, wrapper3, NOTES, spell.getNotes(), NOTES_TOOLTIP, 0);
		mCategoriesField = createField(wrapper3, wrapper3, CATEGORIES, spell.getCategoriesAsString(), CATEGORIES_TOOLTIP, 0);
		mReferenceField = createField(refParent, noGapWrapper, EDITOR_REFERENCE, mRow.getReference(), REFERENCE_TOOLTIP, 6);
		noGapWrapper.add(new JPanel());
		refParent.add(noGapWrapper);
		fields.add(wrapper3);

		determineLargest(wrapper1, 2, size);
		determineLargest(wrapper2, 4, size);
		if (ptsPanel != null) {
			determineLargest(ptsPanel, 100, size);
		}
		determineLargest(wrapper3, 2, size);
		applySize(wrapper1, 2, size);
		applySize(wrapper2, 4, size);
		if (ptsPanel != null) {
			applySize(ptsPanel, 100, size);
		}
		applySize(wrapper3, 2, size);

		icon.setVerticalAlignment(SwingConstants.TOP);
		icon.setAlignmentY(-1f);
		content.add(icon);
		content.add(fields);
		add(content);

		if (notContainer) {
			mTabPanel = new JTabbedPane();
			mPrereqs = new PrereqsPanel(mRow, mRow.getPrereqs());
			mMeleeWeapons = MeleeWeaponEditor.createEditor(mRow);
			mRangedWeapons = RangedWeaponEditor.createEditor(mRow);
			Component panel = embedEditor(mPrereqs);
			mTabPanel.addTab(panel.getName(), panel);
			mTabPanel.addTab(mMeleeWeapons.getName(), mMeleeWeapons);
			mTabPanel.addTab(mRangedWeapons.getName(), mRangedWeapons);
			if (!mIsEditable) {
				UIUtilities.disableControls(mMeleeWeapons);
				UIUtilities.disableControls(mRangedWeapons);
			}
			UIUtilities.selectTab(mTabPanel, getLastTabName());
			add(mTabPanel);
		}
	}

	private static void determineLargest(Container panel, int every, Dimension size) {
		int count = panel.getComponentCount();

		for (int i = 0; i < count; i += every) {
			Dimension oneSize = panel.getComponent(i).getPreferredSize();

			if (oneSize.width > size.width) {
				size.width = oneSize.width;
			}
			if (oneSize.height > size.height) {
				size.height = oneSize.height;
			}
		}
	}

	private static void applySize(Container panel, int every, Dimension size) {
		int count = panel.getComponentCount();

		for (int i = 0; i < count; i += every) {
			UIUtilities.setOnlySize(panel.getComponent(i), size);
		}
	}

	private JScrollPane embedEditor(Component editor) {
		JScrollPane scrollPanel = new JScrollPane(editor);

		scrollPanel.setMinimumSize(new Dimension(500, 120));
		scrollPanel.setName(editor.toString());
		if (!mIsEditable) {
			UIUtilities.disableControls(editor);
		}
		return scrollPanel;
	}

	private void createTechLevelFields(Container parent) {
		OutlineModel owner = mRow.getOwner();
		GURPSCharacter character = mRow.getCharacter();
		boolean enabled = !owner.isLocked();
		boolean hasTL;

		mSavedTechLevel = mRow.getTechLevel();
		hasTL = mSavedTechLevel != null;
		if (!hasTL) {
			mSavedTechLevel = ""; //$NON-NLS-1$
		}

		if (character != null) {
			JPanel wrapper = new JPanel(new ColumnLayout(2));

			mHasTechLevel = new JCheckBox(TECH_LEVEL, hasTL);
			mHasTechLevel.setToolTipText(TECH_LEVEL_TOOLTIP);
			mHasTechLevel.setEnabled(enabled);
			mHasTechLevel.addActionListener(this);
			wrapper.add(mHasTechLevel);

			mTechLevel = new JTextField("9999"); //$NON-NLS-1$
			UIUtilities.setOnlySize(mTechLevel, mTechLevel.getPreferredSize());
			mTechLevel.setText(mSavedTechLevel);
			mTechLevel.setToolTipText(TECH_LEVEL_TOOLTIP);
			mTechLevel.setEnabled(enabled && hasTL);
			wrapper.add(mTechLevel);
			parent.add(wrapper);

			if (!hasTL) {
				mSavedTechLevel = character.getDescription().getTechLevel();
			}
		} else {
			mTechLevel = new JTextField(mSavedTechLevel);
			mHasTechLevel = new JCheckBox(TECH_LEVEL_REQUIRED, hasTL);
			mHasTechLevel.setToolTipText(TECH_LEVEL_REQUIRED_TOOLTIP);
			mHasTechLevel.setEnabled(enabled);
			mHasTechLevel.addActionListener(this);
			parent.add(mHasTechLevel);
		}
	}

	@SuppressWarnings("unused")
	private Container createPointsFields() {
		boolean forCharacter = mRow.getCharacter() != null;
		boolean forTemplate = mRow.getTemplate() != null;
		JPanel panel = new JPanel(new ColumnLayout(forCharacter ? 8 : forTemplate ? 6 : 4));

		mDifficultyCombo = new JComboBox<>(new Object[] { SkillDifficulty.H.name(), SkillDifficulty.VH.name() });
		mDifficultyCombo.setSelectedIndex(mRow.isVeryHard() ? 1 : 0);
		mDifficultyCombo.setToolTipText(DIFFICULTY_TOOLTIP);
		UIUtilities.setOnlySize(mDifficultyCombo, mDifficultyCombo.getPreferredSize());
		mDifficultyCombo.addActionListener(this);
		mDifficultyCombo.setEnabled(mIsEditable);
		panel.add(new LinkedLabel(DIFFICULTY, mDifficultyCombo));
		panel.add(mDifficultyCombo);

		if (forCharacter || mRow.getTemplate() != null) {
			mPointsField = createField(panel, panel, EDITOR_POINTS, Integer.toString(mRow.getPoints()), EDITOR_POINTS_TOOLTIP, 4);
			new NumberFilter(mPointsField, false, false, false, 4);
			mPointsField.addActionListener(this);

			if (forCharacter) {
				mLevelField = createField(panel, panel, EDITOR_LEVEL, getDisplayLevel(mRow.getLevel(), mRow.getRelativeLevel()), EDITOR_LEVEL_TOOLTIP, 5);
				mLevelField.setEnabled(false);
			}
		}
		return panel;
	}

	private static String getDisplayLevel(int level, int relativeLevel) {
		if (level < 0) {
			return "-"; //$NON-NLS-1$
		}
		return Numbers.format(level) + "/IQ" + Numbers.formatWithForcedSign(relativeLevel); //$NON-NLS-1$
	}

	private JTextField createCorrectableField(Container labelParent, Container fieldParent, String title, String text, String tooltip) {
		JTextField field = new JTextField(text);
		field.setToolTipText(tooltip);
		field.setEnabled(mIsEditable);
		field.getDocument().addDocumentListener(this);

		LinkedLabel label = new LinkedLabel(title);
		label.setLink(field);

		labelParent.add(label);
		fieldParent.add(field);
		return field;
	}

	private JTextField createField(Container labelParent, Container fieldParent, String title, String text, String tooltip, int maxChars) {
		JTextField field = new JTextField(maxChars > 0 ? TextUtility.makeFiller(maxChars, 'M') : text);

		if (maxChars > 0) {
			UIUtilities.setOnlySize(field, field.getPreferredSize());
			field.setText(text);
		}
		field.setToolTipText(tooltip);
		field.setEnabled(mIsEditable);
		labelParent.add(new LinkedLabel(title, field));
		fieldParent.add(field);
		return field;
	}

	@Override
	public boolean applyChangesSelf() {
		boolean modified = mRow.setName(mNameField.getText());
		boolean notContainer = !mRow.canHaveChildren();

		modified |= mRow.setReference(mReferenceField.getText());
		if (notContainer) {
			if (mHasTechLevel != null) {
				modified |= mRow.setTechLevel(mHasTechLevel.isSelected() ? mTechLevel.getText() : null);
			}
			modified |= mRow.setCollege(mCollegeField.getText());
			modified |= mRow.setPowerSource(mPowerSourceField.getText());
			modified |= mRow.setSpellClass(mClassField.getText());
			modified |= mRow.setCastingCost(mCastingCostField.getText());
			modified |= mRow.setMaintenance(mMaintenanceField.getText());
			modified |= mRow.setCastingTime(mCastingTimeField.getText());
			modified |= mRow.setDuration(mDurationField.getText());
			modified |= mRow.setIsVeryHard(isVeryHard());
			if (mRow.getCharacter() != null || mRow.getTemplate() != null) {
				modified |= mRow.setPoints(getSpellPoints());
			}
		}
		modified |= mRow.setNotes(mNotesField.getText());
		modified |= mRow.setCategories(mCategoriesField.getText());
		if (mPrereqs != null) {
			modified |= mRow.setPrereqs(mPrereqs.getPrereqList());
		}
		if (mMeleeWeapons != null) {
			ArrayList<WeaponStats> list = new ArrayList<>(mMeleeWeapons.getWeapons());

			list.addAll(mRangedWeapons.getWeapons());
			modified |= mRow.setWeapons(list);
		}
		return modified;
	}

	@Override
	public void finished() {
		if (mTabPanel != null) {
			updateLastTabName(mTabPanel.getTitleAt(mTabPanel.getSelectedIndex()));
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object src = event.getSource();
		if (src == mHasTechLevel) {
			boolean enabled = mHasTechLevel.isSelected();

			mTechLevel.setEnabled(enabled);
			if (enabled) {
				mTechLevel.setText(mSavedTechLevel);
				mTechLevel.requestFocus();
			} else {
				mSavedTechLevel = mTechLevel.getText();
				mTechLevel.setText(""); //$NON-NLS-1$
			}
		} else if (src == mPointsField || src == mDifficultyCombo) {
			recalculateLevel();
		}
	}

	private void recalculateLevel() {
		if (mLevelField != null) {
			SkillLevel level = Spell.calculateLevel(mRow.getCharacter(), getSpellPoints(), isVeryHard(), mCollegeField.getText(), mPowerSourceField.getText(), mNameField.getText());
			mLevelField.setText(getDisplayLevel(level.mLevel, level.mRelativeLevel));
		}
	}

	private int getSpellPoints() {
		return Numbers.getLocalizedInteger(mPointsField.getText(), 0);
	}

	private boolean isVeryHard() {
		return mDifficultyCombo.getSelectedIndex() == 1;
	}

	@Override
	public void changedUpdate(DocumentEvent event) {
		Document doc = event.getDocument();
		if (doc == mNameField.getDocument()) {
			LinkedLabel.setErrorMessage(mNameField, mNameField.getText().trim().length() != 0 ? null : NAME_CANNOT_BE_EMPTY);
		} else if (doc == mClassField.getDocument()) {
			LinkedLabel.setErrorMessage(mClassField, mClassField.getText().trim().length() != 0 ? null : CLASS_CANNOT_BE_EMPTY);
		} else if (doc == mClassField.getDocument()) {
			LinkedLabel.setErrorMessage(mCastingCostField, mCastingCostField.getText().trim().length() != 0 ? null : CASTING_COST_CANNOT_BE_EMPTY);
		} else if (doc == mClassField.getDocument()) {
			LinkedLabel.setErrorMessage(mCastingTimeField, mCastingTimeField.getText().trim().length() != 0 ? null : CASTING_TIME_CANNOT_BE_EMPTY);
		} else if (doc == mClassField.getDocument()) {
			LinkedLabel.setErrorMessage(mDurationField, mDurationField.getText().trim().length() != 0 ? null : DURATION_CANNOT_BE_EMPTY);
		}
	}

	@Override
	public void insertUpdate(DocumentEvent event) {
		changedUpdate(event);
	}

	@Override
	public void removeUpdate(DocumentEvent event) {
		changedUpdate(event);
	}
}
