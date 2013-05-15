/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.bb.extensions.plugin.unittests.parsers;

import java.util.List;

import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import com.bb.extensions.plugin.unittests.internal.navigator.ParsedClassInformation;
import com.bb.extensions.plugin.unittests.internal.visitors.ClassVisitor;
import com.google.common.collect.Lists;

/**
 * @author tallen
 * 
 */
public class HeaderClassParser {
	/**
	 * Parse an IFile to obtain a list of C++ classes
	 * 
	 * @param file
	 *            The file to parse
	 * @return A list of classes found in the file
	 */
	public List<ParsedClassInformation> parse(IFile file) {
		List<ParsedClassInformation> result = Lists.newArrayList();

		ITranslationUnit tu = CoreModelUtil.findTranslationUnit(file);

		if (tu != null) {
			try {
				ClassVisitor visitor = new ClassVisitor(file);

				tu.accept(visitor);

				result.addAll(visitor.getClasses());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
}
