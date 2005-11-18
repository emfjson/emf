/**
 * <copyright>
 *
 * Copyright (c) 2002-2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * $Id: JETException.java,v 1.4 2005/11/18 12:03:32 emerks Exp $
 */
package org.eclipse.emf.codegen.jet;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.codegen.CodeGenPlugin;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticException;



/**
 * Base class for all exceptions generated by the JET engine. 
 * Makes it convienient to catch just this at the top-level.
 */
public class JETException extends DiagnosticException 
{
  public JETException(String reason) 
  {
    super(new BasicDiagnostic(Diagnostic.ERROR, CodeGenPlugin.INSTANCE.getSymbolicName(), 0, reason, null));
  }

  /**
   * Creates a JETException with the embedded exception and the reason for throwing a JETException.
   */
  public JETException(String reason, Throwable exception) 
  {
    super(new BasicDiagnostic(Diagnostic.ERROR, CodeGenPlugin.INSTANCE.getSymbolicName(), 0, reason, new Object [] { exception }));
  }

  /**
   * Creates a JETException with the embedded exception.
   */
  public JETException(Throwable exception) 
  {
    super
      (new BasicDiagnostic
        (Diagnostic.ERROR, CodeGenPlugin.INSTANCE.getSymbolicName(), 0, getMessage(exception), new Object [] { exception }));
  }

  public IStatus getStatus()
  {
    return BasicDiagnostic.toIStatus(getDiagnostic());
  }

  protected static String getMessage(Throwable exception)
  {
    String result = exception.getLocalizedMessage();
    if (result == null)
    {
      result = "";
    }

    return result;
  }
}
