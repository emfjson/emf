/**
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.test.core.ecore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.test.models.ppo.Item;
import org.eclipse.emf.test.models.ppo.PPOFactory;
import org.eclipse.emf.test.models.ppo.PurchaseOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @since 2.6
 */
public class BinaryResourceTest
{
  private URI resourceURI;
  private List<EObject> rootObjects;


  @Before
  public void setUp() throws Exception
  {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    if (!tempDir.exists())
    {
      assertTrue(tempDir.getAbsolutePath(), tempDir.mkdirs());
    }
    else
    {
      assertTrue(tempDir.getAbsolutePath(), tempDir.isDirectory());
      assertTrue(tempDir.getAbsolutePath(), tempDir.canWrite());
    }

    resourceURI = URI.createFileURI(tempDir.getAbsolutePath() + '/' + System.nanoTime() + ".dat");
    assertFalse(resourceURI.toString(), URIConverter.INSTANCE.exists(resourceURI, null));

    int numberORootObjects = 100;
    rootObjects = new ArrayList<EObject>(numberORootObjects);
    for (int i = 0; i < numberORootObjects; i++)
    {
      PurchaseOrder purchaseOrder = PPOFactory.eINSTANCE.createPurchaseOrder();
      rootObjects.add(purchaseOrder);
      purchaseOrder.setComment("purchase order " + i);
      purchaseOrder.setOrderDate(new Date());

      for (int j = 0, max = numberORootObjects / 2; j < max; j++)
      {
        Item item = PPOFactory.eINSTANCE.createItem();
        purchaseOrder.getItems().add(item);
        item.setProductName("item " + i + "-" + j);
        item.setQuantity(i * j);
      }
    }
  }

  @After
  public void tearDown() throws Exception
  {
    URIConverter.INSTANCE.delete(resourceURI, null);
    assertFalse(resourceURI.toString(), URIConverter.INSTANCE.exists(resourceURI, null));
  }

  @Test
  public void testSaveAndLoad1() throws Exception
  {
    testSaveAndLoad1Helper(null);
    Map<Object, Object> options = new HashMap<Object, Object>();
    options.put(BinaryResourceImpl.OPTION_STYLE_DATA_CONVERTER, Boolean.TRUE);
    options.put(BinaryResourceImpl.OPTION_VERSION, BinaryResourceImpl.BinaryIO.Version.VERSION_1_1);
    testSaveAndLoad1Helper(options);
  }

  public void testSaveAndLoad1Helper(Map<?, ?> options) throws Exception
  {
    Resource resource = new BinaryResourceImpl(resourceURI);
    resource.getContents().addAll(rootObjects);
    resource.save(options);
    assertTrue(resourceURI.toString(), URIConverter.INSTANCE.exists(resourceURI, null));

    resource = new BinaryResourceImpl(resourceURI);
    resource.load(options);
    assertTrue(EcoreUtil.equals(rootObjects, resource.getContents()));
  }

  @Test
  public void testSaveAndLoad2() throws Exception
  {
    testSaveAndLoad2Helper(null);
    Map<Object, Object> options = new HashMap<Object, Object>();
    options.put(BinaryResourceImpl.OPTION_STYLE_DATA_CONVERTER, Boolean.TRUE);
    options.put(BinaryResourceImpl.OPTION_VERSION, BinaryResourceImpl.BinaryIO.Version.VERSION_1_1);
    testSaveAndLoad2Helper(options);
  }

  public void testSaveAndLoad2Helper(Map<?, ?> options) throws Exception
  {
    Resource resource = new BinaryResourceImpl();
    resource.getContents().addAll(rootObjects);
    OutputStream outputStream = URIConverter.INSTANCE.createOutputStream(resourceURI);
    try
    {
      resource.save(outputStream, options);
    }
    finally
    {
      outputStream.close();
    }
    assertTrue(resourceURI.toString(), URIConverter.INSTANCE.exists(resourceURI, null));

    resource = new BinaryResourceImpl();
    InputStream inputStream = URIConverter.INSTANCE.createInputStream(resourceURI);
    try
    {
      resource.load(inputStream, options);
    }
    finally
    {
      inputStream.close();
    }
    assertTrue(EcoreUtil.equals(rootObjects, resource.getContents()));
  }

  @Test
  public void testSaveAndLoadWithXMIResource() throws Exception
  {
    Resource savedResource = new BinaryResourceImpl(resourceURI);
    savedResource.getContents().addAll(rootObjects);
    savedResource.save(null);
    assertTrue(resourceURI.toString(), URIConverter.INSTANCE.exists(resourceURI, null));

    XMLResource loadedResource = new XMIResourceImpl(resourceURI);
    loadedResource.getDefaultLoadOptions().put(XMLResource.OPTION_BINARY, Boolean.TRUE);
    loadedResource.load(null);
    assertTrue(EcoreUtil.equals(rootObjects, loadedResource.getContents()));
    for (int i = 0, size = rootObjects.size(); i < size; ++i)
    {
      String loadedURIFragment = loadedResource.getURIFragment(loadedResource.getContents().get(i));
      assertNotNull(loadedURIFragment);
      assertEquals(savedResource.getURIFragment(rootObjects.get(i)), loadedURIFragment);
    }
  }

  @Test
  public void testSaveWithBinaryResourceAndLoadWithXMIResource() throws Exception
  {
    XMLResource savedResource =
      new XMIResourceImpl(resourceURI)
      {
        @Override
        protected boolean useUUIDs()
        {
          return true;
        }
      };
    savedResource.getDefaultSaveOptions().put(XMLResource.OPTION_BINARY, Boolean.TRUE);
    savedResource.getContents().addAll(rootObjects);
    savedResource.save(null);
    assertTrue(resourceURI.toString(), URIConverter.INSTANCE.exists(resourceURI, null));

    XMLResource loadedResource = new XMIResourceImpl(resourceURI);
    loadedResource.getDefaultLoadOptions().put(XMLResource.OPTION_BINARY, Boolean.TRUE);
    loadedResource.load(null);
    assertTrue(EcoreUtil.equals(rootObjects, loadedResource.getContents()));
    for (int i = 0, size = rootObjects.size(); i < size; ++i)
    {
      String loadedID = loadedResource.getID(loadedResource.getContents().get(i));
      assertNotNull(loadedID);
      assertEquals(savedResource.getID(rootObjects.get(i)), loadedID);
    }
  }

  @Test
  public void testSaveWithBinaryResourceAndLoadWithXMIResourceAndInternalBuffer() throws Exception
  {
    XMLResource savedResource =
      new XMIResourceImpl(resourceURI)
      {
        @Override
        protected boolean useUUIDs()
        {
          return true;
        }
      };
    savedResource.getDefaultSaveOptions().put(XMLResource.OPTION_BINARY, Boolean.TRUE);
    savedResource.getDefaultSaveOptions().put(BinaryResourceImpl.OPTION_INTERNAL_BUFFER_CAPACITY, 256);
    savedResource.getContents().addAll(rootObjects);
    savedResource.save(null);
    assertTrue(resourceURI.toString(), URIConverter.INSTANCE.exists(resourceURI, null));

    XMLResource loadedResource = new XMIResourceImpl(resourceURI);
    loadedResource.getDefaultLoadOptions().put(XMLResource.OPTION_BINARY, Boolean.TRUE);
    loadedResource.getDefaultLoadOptions().put(BinaryResourceImpl.OPTION_INTERNAL_BUFFER_CAPACITY, 256);
    loadedResource.load(null);
    assertTrue(EcoreUtil.equals(rootObjects, loadedResource.getContents()));
    for (int i = 0, size = rootObjects.size(); i < size; ++i)
    {
      String loadedID = loadedResource.getID(loadedResource.getContents().get(i));
      assertNotNull(loadedID);
      assertEquals(savedResource.getID(rootObjects.get(i)), loadedID);
    }
  }

  @Test
  public void testSaveWithBinaryResourceAndLoadWithXMIResourceAndInternalBuffer2() throws Exception
  {
    OutputStream outputStream = URIConverter.INSTANCE.createOutputStream(resourceURI);
    XMLResource savedResource =
      new XMIResourceImpl(resourceURI)
      {
        @Override
        protected boolean useUUIDs()
        {
          return true;
        }
      };
    savedResource.getDefaultSaveOptions().put(XMLResource.OPTION_BINARY, Boolean.TRUE);
    savedResource.getDefaultSaveOptions().put(BinaryResourceImpl.OPTION_INTERNAL_BUFFER_CAPACITY, 256);
    savedResource.getContents().addAll(rootObjects);
    savedResource.save(outputStream, null);
    savedResource.save(outputStream, null);
    outputStream.close();
    assertTrue(resourceURI.toString(), URIConverter.INSTANCE.exists(resourceURI, null));

    InputStream inputStream = new BufferedInputStream(URIConverter.INSTANCE.createInputStream(resourceURI));
    for (int count = 0; count < 2; ++count)
    {
      XMLResource loadedResource = new XMIResourceImpl(resourceURI);
      loadedResource.getDefaultLoadOptions().put(XMLResource.OPTION_BINARY, Boolean.TRUE);
      loadedResource.getDefaultLoadOptions().put(BinaryResourceImpl.OPTION_INTERNAL_BUFFER_CAPACITY, 256);
      loadedResource.load(inputStream, null);
      assertTrue(EcoreUtil.equals(rootObjects, loadedResource.getContents()));
      for (int i = 0, size = rootObjects.size(); i < size; ++i)
      {
        String loadedID = loadedResource.getID(loadedResource.getContents().get(i));
        assertNotNull(loadedID);
        assertEquals(savedResource.getID(rootObjects.get(i)), loadedID);
      }
    }
    inputStream.close();
  }

  @Test
  public void testSaveAndLoadNoCache1() throws Exception
  {
    Map<String, Object> options = new HashMap<String, Object>();
    options.put(BinaryResourceImpl.OPTION_BUFFER_CAPACITY, 0);

    Resource resource = new BinaryResourceImpl(resourceURI);
    resource.getContents().addAll(rootObjects);
    resource.save(options);
    assertTrue(resourceURI.toString(), URIConverter.INSTANCE.exists(resourceURI, null));

    resource = new BinaryResourceImpl(resourceURI);
    resource.load(options);
    assertTrue(EcoreUtil.equals(rootObjects, resource.getContents()));
  }

  @Test
  public void testSaveAndLoadNoCache2() throws Exception
  {
    Resource resource = new BinaryResourceImpl();
    resource.getContents().addAll(rootObjects);
    OutputStream outputStream = new BufferedOutputStream(URIConverter.INSTANCE.createOutputStream(resourceURI));
    try
    {
      resource.save(outputStream, null);
    }
    finally
    {
      outputStream.close();
    }
    assertTrue(resourceURI.toString(), URIConverter.INSTANCE.exists(resourceURI, null));

    resource = new BinaryResourceImpl();
    InputStream inputStream = new BufferedInputStream(URIConverter.INSTANCE.createInputStream(resourceURI));
    try
    {
      resource.load(inputStream, null);
    }
    finally
    {
      inputStream.close();
    }
    assertTrue(EcoreUtil.equals(rootObjects, resource.getContents()));
  }
}
