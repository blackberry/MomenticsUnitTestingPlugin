/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.actions;

import java.util.List;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ISourceRange;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.core.model.IWorkingCopy;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author tallen
 * 
 */
public class Util {
	/**
	 * Add an include line to a file if any if and only if include is not
	 * already present.
	 * 
	 * The placement is determined by the first match below: <br>
	 * 1) directly after the (currently) last include line <br>
	 * 2) directly before the first element (not including comments) <br>
	 * 3) at the end of the file
	 * 
	 * @param wc
	 *            The IWorkingCopy instance to modify
	 * @param include
	 *            The include (the part to go inside the <>)
	 * @throws CModelException
	 */
	public static void addInclude(IWorkingCopy wc, String include)
			throws CModelException {
		List<ICElement> includeElements = wc
				.getChildrenOfType(ICElement.C_INCLUDE);
		for (ICElement cElement : includeElements) {
			if (include.equals(cElement.getElementName())) {
				return;
			}
		}
		ICElement[] allElements = wc.getChildren();

		ICElement beforeElement = null;
		for (ICElement cElement : allElements) {
			if (cElement.getElementType() != ICElement.C_INCLUDE) {
				beforeElement = cElement;
				break;
			}
		}
		int insertNewlinePos = -1;
		if ((beforeElement == null) && (allElements.length > 0)) {
			ISourceRange range = ((ISourceReference) allElements[allElements.length - 1])
					.getSourceRange();
			insertNewlinePos = range.getStartPos() + range.getLength();
		}
		wc.createInclude(include, true, beforeElement,
				new NullProgressMonitor());
		if (insertNewlinePos >= 0) {
			wc.getBuffer().replace(insertNewlinePos, 0,
					System.getProperty("line.separator"));
		}
	}

}
